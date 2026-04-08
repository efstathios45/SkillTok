package com.skilltok.app

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()

    companion object {
        val idMap = ConcurrentHashMap<String, String>()
        fun getMockId(remoteId: String): String = idMap[remoteId.lowercase()] ?: remoteId
        fun registerMapping(remoteId: String, mockId: String) { 
            idMap[remoteId.lowercase()] = mockId 
        }
    }

    private fun extractYoutubeId(url: String): String {
        if (url.isBlank()) return ""
        val trimmed = url.trim()
        val idRegex = "^[a-zA-Z0-9_-]{11}$".toRegex()
        if (idRegex.matches(trimmed)) return trimmed
        
        val patterns = listOf(
            "(?:v=|(?:be|embed|shorts)/|watch\\?v=)([a-zA-Z0-9_-]{11})".toRegex(),
            "(?:youtu\\.be/|youtube\\.com/(?:v/|u/\\w/|embed/|watch\\?v=))([a-zA-Z0-9_-]{11})".toRegex()
        )
        for (pattern in patterns) {
            val match = pattern.find(trimmed)
            val id = match?.groupValues?.get(1)
            if (id != null && id.length == 11) return id
        }
        return ""
    }

    // --- Social Interactions ---
    suspend fun toggleLike(userId: String, lessonId: String, isLiked: Boolean) {
        try {
            val docRef = db.collection("likes").document("${userId}_${lessonId}")
            val lessonRef = db.collection("lessons").document(lessonId)
            
            if (isLiked) {
                val data = mapOf("userId" to userId, "lessonId" to lessonId, "createdAt" to System.currentTimeMillis())
                docRef.set(data).await()
                lessonRef.update("likeCount", FieldValue.increment(1)).await()
            } else {
                docRef.delete().await()
                lessonRef.update("likeCount", FieldValue.increment(-1)).await()
            }
        } catch (e: Exception) { 
            Log.e("FirebaseRepository", "Like failed", e)
            // If update fails because field doesn't exist, create it
            if (isLiked) db.collection("lessons").document(lessonId).update("likeCount", 1)
        }
    }

    suspend fun toggleSave(userId: String, lessonId: String, isSaved: Boolean) {
        try {
            val docRef = db.collection("saved_content").document("${userId}_${lessonId}")
            if (isSaved) {
                val data = mapOf("userId" to userId, "lessonId" to lessonId, "createdAt" to System.currentTimeMillis())
                docRef.set(data).await()
            } else {
                docRef.delete().await()
            }
        } catch (e: Exception) { Log.e("FirebaseRepository", "Save failed", e) }
    }

    suspend fun addComment(comment: ReelComment) {
        try {
            db.collection("comments").document(comment.id).set(comment).await()
        } catch (e: Exception) { Log.e("FirebaseRepository", "Comment failed", e) }
    }

    fun getComments(lessonId: String): Flow<List<ReelComment>> = callbackFlow {
        val subscription = db.collection("comments")
            .whereEqualTo("lessonId", lessonId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val comments = snapshot?.documents?.mapNotNull { it.toObject<ReelComment>() } ?: emptyList()
                trySend(comments.sortedByDescending { it.createdAt })
            }
        awaitClose { subscription.remove() }
    }

    // --- Course Data ---
    fun getRemoteCourses(): Flow<List<Course>> = callbackFlow {
        val subscription = db.collection("courses")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val courses = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject<Course>()?.copy(id = doc.id)
                } ?: emptyList()
                trySend(courses)
            }
        awaitClose { subscription.remove() }
    }

    fun getModules(courseId: String): Flow<List<Module>> = flow {
        try {
            val snapshot = db.collection("modules").whereEqualTo("courseId", courseId).get().await()
            val modules = snapshot.documents.mapNotNull { it.toObject<Module>()?.copy(id = it.id) }
            emit(modules.sortedBy { it.orderIndex })
        } catch (e: Exception) {
            emit(emptyList<Module>())
        }
    }

    fun getLessons(moduleId: String): Flow<List<Lesson>> = flow {
        try {
            val snapshot = db.collection("lessons").whereEqualTo("moduleId", moduleId).get().await()
            val lessons = snapshot.documents.mapNotNull { it.toObject<Lesson>()?.copy(id = it.id) }
            emit(lessons.sortedBy { it.orderIndex })
        } catch (e: Exception) {
            emit(emptyList<Lesson>())
        }
    }

    // --- Enrollment ---
    suspend fun enrollInCourse(userId: String, courseId: String) {
        try {
            val now = System.currentTimeMillis().toString()
            val data = mapOf(
                "userId" to userId,
                "courseId" to courseId,
                "enrolledAt" to now,
                "startedAt" to now,
                "status" to "in_progress",
                "progressPercent" to 0
            )
            db.collection("enrollments").document("${userId}_${courseId}").set(data).await()
        } catch (e: Exception) { Log.e("FirebaseRepository", "Enroll failed", e) }
    }

    suspend fun unenrollFromCourse(userId: String, courseId: String) {
        try {
            db.collection("enrollments").document("${userId}_${courseId}").delete().await()
        } catch (e: Exception) { Log.e("FirebaseRepository", "Unenroll failed", e) }
    }

    fun getEnrollments(userId: String): Flow<List<Enrollment>> = callbackFlow {
        val subscription = db.collection("enrollments")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val enrollments = snapshot?.documents?.mapNotNull { it.toObject<Enrollment>() } ?: emptyList()
                trySend(enrollments)
            }
        awaitClose { subscription.remove() }
    }

    // --- User Profile ---
    fun getUserProfile(uid: String): Flow<User?> = callbackFlow {
        val subscription = db.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject<User>())
            }
        awaitClose { subscription.remove() }
    }

    suspend fun updateUserProfile(user: User) {
        try {
            db.collection("users").document(user.id).set(user).await()
        } catch (e: Exception) { Log.e("FirebaseRepository", "Update profile failed", e) }
    }

    // --- User Interaction Lists ---
    fun getUserLikes(userId: String): Flow<List<String>> = callbackFlow {
        val subscription = db.collection("likes")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                trySend(snapshot?.documents?.mapNotNull { it.getString("lessonId") } ?: emptyList())
            }
        awaitClose { subscription.remove() }
    }

    fun getUserSaved(userId: String): Flow<List<String>> = callbackFlow {
        val subscription = db.collection("saved_content")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                trySend(snapshot?.documents?.mapNotNull { it.getString("lessonId") } ?: emptyList())
            }
        awaitClose { subscription.remove() }
    }

    // --- Content Management ---
    suspend fun addCourse(course: Course): String? {
        return try {
            db.collection("courses").document(course.id).set(course).await()
            course.id
        } catch (e: Exception) { null }
    }

    suspend fun addModule(module: Module): String? {
        return try {
            db.collection("modules").document(module.id).set(module).await()
            module.id
        } catch (e: Exception) { null }
    }

    suspend fun addLesson(lesson: Lesson): String? {
        return try {
            db.collection("lessons").document(lesson.id).set(lesson).await()
            lesson.id
        } catch (e: Exception) { null }
    }

    suspend fun completeLesson(userId: String, courseId: String, lessonId: String) {
        try {
            val data = mapOf(
                "userId" to userId,
                "courseId" to courseId,
                "lessonId" to lessonId,
                "isCompleted" to true,
                "completedAt" to System.currentTimeMillis().toString()
            )
            db.collection("progress").document("${userId}_${lessonId}").set(data).await()
        } catch (e: Exception) { Log.e("FirebaseRepository", "Complete lesson failed", e) }
    }
}
