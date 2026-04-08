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
            if (isLiked) {
                try { db.collection("lessons").document(lessonId).update("likeCount", 1) } catch(e: Exception) {}
            }
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

    fun getModules(courseId: String): Flow<List<Module>> = callbackFlow {
        val subscription = db.collection("modules")
            .whereEqualTo("courseId", courseId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val modules = snapshot?.documents?.mapNotNull { it.toObject<Module>()?.copy(id = it.id) } ?: emptyList()
                trySend(modules.sortedBy { it.orderIndex })
            }
        awaitClose { subscription.remove() }
    }

    fun getLessons(moduleId: String): Flow<List<Lesson>> = callbackFlow {
        val subscription = db.collection("lessons")
            .whereEqualTo("moduleId", moduleId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val lessons = snapshot?.documents?.mapNotNull { it.toObject<Lesson>()?.copy(id = it.id) } ?: emptyList()
                trySend(lessons.sortedBy { it.orderIndex })
            }
        awaitClose { subscription.remove() }
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
            db.collection("courses").document(courseId).update("learnersCount", FieldValue.increment(1)).await()
        } catch (e: Exception) { Log.e("FirebaseRepository", "Enroll failed", e) }
    }

    suspend fun unenrollFromCourse(userId: String, courseId: String) {
        try {
            db.collection("enrollments").document("${userId}_${courseId}").delete().await()
            db.collection("courses").document(courseId).update("learnersCount", FieldValue.increment(-1)).await()
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

    // --- Professor Functions ---
    fun getCourseEnrollments(courseId: String): Flow<List<Enrollment>> = callbackFlow {
        val subscription = db.collection("enrollments")
            .whereEqualTo("courseId", courseId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                trySend(snapshot?.documents?.mapNotNull { it.toObject<Enrollment>() } ?: emptyList())
            }
        awaitClose { subscription.remove() }
    }

    fun getStudentProgress(courseId: String): Flow<List<Map<String, Any>>> = callbackFlow {
        val subscription = db.collection("progress")
            .whereEqualTo("courseId", courseId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                trySend(snapshot?.documents?.map { it.data ?: emptyMap() } ?: emptyList())
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addForumTopic(topic: ForumTopic) {
        try { db.collection("forum_topics").document(topic.id).set(topic).await() } catch(e: Exception) {}
    }

    fun getForumTopics(courseId: String): Flow<List<ForumTopic>> = callbackFlow {
        val subscription = db.collection("forum_topics")
            .whereEqualTo("courseId", courseId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                trySend(snapshot?.documents?.mapNotNull { it.toObject<ForumTopic>() } ?: emptyList())
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addForumReply(reply: ForumReply) {
        try { db.collection("forum_replies").document(reply.id).set(reply).await() } catch(e: Exception) {}
    }

    fun getForumReplies(topicId: String): Flow<List<ForumReply>> = callbackFlow {
        val subscription = db.collection("forum_replies")
            .whereEqualTo("topicId", topicId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                trySend(snapshot?.documents?.mapNotNull { it.toObject<ForumReply>() } ?: emptyList())
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addNotification(notification: CourseNotification) {
        try { db.collection("course_notifications").document(notification.id).set(notification).await() } catch(e: Exception) {}
    }

    fun getCourseNotifications(courseId: String): Flow<List<CourseNotification>> = callbackFlow {
        val subscription = db.collection("course_notifications")
            .whereEqualTo("courseId", courseId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                trySend(snapshot?.documents?.mapNotNull { it.toObject<CourseNotification>() } ?: emptyList())
            }
        awaitClose { subscription.remove() }
    }

    suspend fun saveQuizResult(result: QuizResult) {
        try { db.collection("quiz_results").document(result.id).set(result).await() } catch(e: Exception) {}
    }

    fun getCourseQuizResults(courseId: String): Flow<List<QuizResult>> = callbackFlow {
        val subscription = db.collection("quiz_results")
            .whereEqualTo("courseId", courseId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                trySend(snapshot?.documents?.mapNotNull { it.toObject<QuizResult>() } ?: emptyList())
            }
        awaitClose { subscription.remove() }
    }

    fun getLessonQuizzes(lessonId: String): Flow<List<QuizQuestion>> = callbackFlow {
        val subscription = db.collection("quizzes")
            .whereEqualTo("lessonId", lessonId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                trySend(snapshot?.documents?.mapNotNull { it.toObject<QuizQuestion>() } ?: emptyList())
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

    // --- Reviews ---
    suspend fun addReview(review: CourseReview) {
        try {
            db.collection("reviews").document(review.id).set(review).await()
        } catch (e: Exception) { Log.e("FirebaseRepository", "Add review failed", e) }
    }

    fun getReviews(courseId: String): Flow<List<CourseReview>> = callbackFlow {
        val subscription = db.collection("reviews")
            .whereEqualTo("courseId", courseId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val reviews = snapshot?.documents?.mapNotNull { it.toObject<CourseReview>() } ?: emptyList()
                trySend(reviews.sortedByDescending { it.createdAt })
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

    suspend fun addQuizQuestion(question: QuizQuestion) {
        try {
            db.collection("quizzes").document(question.id).set(question).await()
        } catch (e: Exception) { Log.e("FirebaseRepository", "Add quiz failed", e) }
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
