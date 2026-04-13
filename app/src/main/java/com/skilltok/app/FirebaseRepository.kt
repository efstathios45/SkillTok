package com.skilltok.app

import android.util.Log
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
                    Log.e("FirebaseRepository", "Comments listener error", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val comments = snapshot?.documents?.mapNotNull { it.toObject<ReelComment>()?.copy(id = it.id) } ?: emptyList()
                trySend(comments.sortedByDescending { it.createdAt })
            }
        awaitClose { subscription.remove() }
    }

    // --- Course Data ---
    fun getRemoteCourses(): Flow<List<Course>> = callbackFlow {
        val subscription = db.collection("courses")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirebaseRepository", "Courses listener error", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val courses = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject<Course>()?.copy(id = doc.id)
                } ?: emptyList()
                trySend(courses)
            }
        awaitClose { subscription.remove() }
    }

    // REMOTE QUERY: Filtering courses by Rating (Requirement Satisfaction)
    fun getHighRatedCourses(minRating: Double): Flow<List<Course>> = callbackFlow {
        val subscription = db.collection("courses")
            .whereGreaterThanOrEqualTo("rating", minRating)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { trySend(emptyList()); return@addSnapshotListener }
                val courses = snapshot?.documents?.mapNotNull { it.toObject<Course>()?.copy(id = it.id) } ?: emptyList()
                trySend(courses)
            }
        awaitClose { subscription.remove() }
    }

    fun getCourse(courseId: String): Flow<Course?> = callbackFlow {
        val subscription = db.collection("courses").document(courseId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { 
                    Log.e("FirebaseRepository", "Course listener error", error)
                    trySend(null)
                    return@addSnapshotListener 
                }
                trySend(snapshot?.toObject<Course>()?.copy(id = snapshot.id))
            }
        awaitClose { subscription.remove() }
    }

    fun getModules(courseId: String): Flow<List<Module>> = callbackFlow {
        val subscription = db.collection("modules")
            .whereEqualTo("courseId", courseId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { 
                    Log.e("FirebaseRepository", "Modules listener error", error)
                    trySend(emptyList())
                    return@addSnapshotListener 
                }
                val modules = snapshot?.documents?.mapNotNull { it.toObject<Module>()?.copy(id = it.id) } ?: emptyList()
                trySend(modules.sortedBy { it.orderIndex })
            }
        awaitClose { subscription.remove() }
    }

    fun getModule(moduleId: String): Flow<Module?> = callbackFlow {
        val subscription = db.collection("modules").document(moduleId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { 
                    Log.e("FirebaseRepository", "Module listener error", error)
                    trySend(null)
                    return@addSnapshotListener 
                }
                trySend(snapshot?.toObject<Module>()?.copy(id = snapshot.id))
            }
        awaitClose { subscription.remove() }
    }

    fun getLessons(moduleId: String): Flow<List<Lesson>> = callbackFlow {
        val subscription = db.collection("lessons")
            .whereEqualTo("moduleId", moduleId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { 
                    Log.e("FirebaseRepository", "Lessons listener error", error)
                    trySend(emptyList())
                    return@addSnapshotListener 
                }
                val lessons = snapshot?.documents?.mapNotNull { it.toObject<Lesson>()?.copy(id = it.id) } ?: emptyList()
                trySend(lessons.sortedBy { it.orderIndex })
            }
        awaitClose { subscription.remove() }
    }

    fun getLesson(lessonId: String): Flow<Lesson?> = callbackFlow {
        val subscription = db.collection("lessons").document(lessonId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { 
                    Log.e("FirebaseRepository", "Lesson listener error", error)
                    trySend(null)
                    return@addSnapshotListener 
                }
                trySend(snapshot?.toObject<Lesson>()?.copy(id = snapshot.id))
            }
        awaitClose { subscription.remove() }
    }

    suspend fun getLessonsByIds(ids: List<String>): List<Lesson> {
        if (ids.isEmpty()) return emptyList()
        return try {
            val snapshot = db.collection("lessons")
                .whereIn(FieldPath.documentId(), ids.take(10)) 
                .get().await()
            snapshot.documents.mapNotNull { it.toObject<Lesson>()?.copy(id = it.id) }
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Failed to fetch lessons by IDs", e)
            emptyList()
        }
    }

    // --- Enrollment ---
    suspend fun enrollInCourse(userId: String, courseId: String, userName: String) {
        try {
            val now = System.currentTimeMillis().toString()
            val data = mapOf(
                "id" to "${userId}_${courseId}",
                "userId" to userId,
                "userName" to userName,
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
                    Log.e("FirebaseRepository", "Enrollments listener error", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val enrollments = snapshot?.documents?.mapNotNull { it.toObject<Enrollment>()?.copy(id = it.id) } ?: emptyList()
                trySend(enrollments)
            }
        awaitClose { subscription.remove() }
    }

    // --- Professor Functions ---
    fun getCourseEnrollments(courseId: String): Flow<List<Enrollment>> = callbackFlow {
        val subscription = db.collection("enrollments")
            .whereEqualTo("courseId", courseId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { 
                    Log.e("FirebaseRepository", "Course enrollments listener error", error)
                    trySend(emptyList())
                    return@addSnapshotListener 
                }
                trySend(snapshot?.documents?.mapNotNull { it.toObject<Enrollment>()?.copy(id = it.id) } ?: emptyList())
            }
        awaitClose { subscription.remove() }
    }

    fun getStudentProgress(courseId: String): Flow<List<Map<String, Any>>> = callbackFlow {
        val subscription = db.collection("progress")
            .whereEqualTo("courseId", courseId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { 
                    Log.e("FirebaseRepository", "Student progress listener error", error)
                    trySend(emptyList())
                    return@addSnapshotListener 
                }
                trySend(snapshot?.documents?.mapNotNull { it.data } ?: emptyList())
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addCourse(course: Course) {
        try { db.collection("courses").document(course.id).set(course).await() } catch(e: Exception) {}
    }

    suspend fun addModule(module: Module) {
        try { db.collection("modules").document(module.id).set(module).await() } catch(e: Exception) {}
    }

    suspend fun addLesson(lesson: Lesson) {
        try { db.collection("lessons").document(lesson.id).set(lesson).await() } catch(e: Exception) {}
    }

    suspend fun addQuizQuestion(question: QuizQuestion) {
        try { db.collection("quizzes").document(question.id).set(question).await() } catch(e: Exception) {}
    }

    suspend fun deleteModule(moduleId: String) {
        try { db.collection("modules").document(moduleId).delete().await() } catch(e: Exception) {}
    }

    suspend fun deleteLesson(lessonId: String) {
        try { db.collection("lessons").document(lessonId).delete().await() } catch(e: Exception) {}
    }

    suspend fun completeLesson(userId: String, courseId: String, lessonId: String, totalLessons: Int) {
        try {
            val completionId = "${userId}_${lessonId}"
            val data = mapOf("userId" to userId, "courseId" to courseId, "lessonId" to lessonId, "completedAt" to System.currentTimeMillis())
            db.collection("completions").document(completionId).set(data).await()
            
            // Increment progress
            val enrollmentRef = db.collection("enrollments").document("${userId}_${courseId}")
            val enrollment = enrollmentRef.get().await().toObject<Enrollment>()
            if (enrollment != null) {
                val completionsCount = db.collection("completions")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("courseId", courseId)
                    .get().await().size()
                val progress = if (totalLessons > 0) (completionsCount.toFloat() / totalLessons * 100).toInt() else 0
                enrollmentRef.update("progressPercent", progress, "currentLessonId", lessonId).await()
            }
        } catch (e: Exception) { Log.e("FirebaseRepository", "Complete lesson failed", e) }
    }

    // --- Reviews ---
    suspend fun addReview(review: CourseReview) {
        try {
            db.collection("reviews").document(review.id).set(review).await()
            val reviews = db.collection("reviews").whereEqualTo("courseId", review.courseId).get().await()
            val avg = reviews.documents.mapNotNull { it.toObject<CourseReview>()?.rating }.average()
            db.collection("courses").document(review.courseId).update("rating", if (avg.isNaN()) 5.0 else avg).await()
        } catch (e: Exception) { Log.e("FirebaseRepository", "Review failed", e) }
    }

    fun getReviews(courseId: String): Flow<List<CourseReview>> = callbackFlow {
        val subscription = db.collection("reviews")
            .whereEqualTo("courseId", courseId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { 
                    Log.e("FirebaseRepository", "Reviews listener error", error)
                    trySend(emptyList())
                    return@addSnapshotListener 
                }
                trySend(snapshot?.documents?.mapNotNull { it.toObject<CourseReview>()?.copy(id = it.id) } ?: emptyList())
            }
        awaitClose { subscription.remove() }
    }

    // --- Forum Functions ---
    suspend fun addForumTopic(topic: ForumTopic) {
        try { db.collection("forum_topics").document(topic.id).set(topic).await() } catch(e: Exception) {}
    }

    fun getForumTopics(courseId: String): Flow<List<ForumTopic>> = callbackFlow {
        val subscription = db.collection("forum_topics")
            .whereEqualTo("courseId", courseId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { 
                    Log.e("FirebaseRepository", "Forum topics listener error", error)
                    trySend(emptyList())
                    return@addSnapshotListener 
                }
                val topics = snapshot?.documents?.mapNotNull { it.toObject<ForumTopic>()?.copy(id = it.id) } ?: emptyList()
                trySend(topics.sortedByDescending { it.createdAt })
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
                if (error != null) { 
                    Log.e("FirebaseRepository", "Forum replies listener error", error)
                    trySend(emptyList())
                    return@addSnapshotListener 
                }
                val replies = snapshot?.documents?.mapNotNull { it.toObject<ForumReply>()?.copy(id = it.id) } ?: emptyList()
                trySend(replies.sortedByDescending { it.createdAt })
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
                if (error != null) { 
                    Log.e("FirebaseRepository", "Notifications listener error", error)
                    trySend(emptyList())
                    return@addSnapshotListener 
                }
                trySend(snapshot?.documents?.mapNotNull { it.toObject<CourseNotification>()?.copy(id = it.id) } ?: emptyList())
            }
        awaitClose { subscription.remove() }
    }

    // --- Chat Functions ---
    suspend fun sendChatMessage(message: ChatMessage) {
        try { db.collection("course_chats").document(message.id).set(message).await() } catch(e: Exception) {}
    }

    fun getCourseChat(courseId: String): Flow<List<ChatMessage>> = callbackFlow {
        val subscription = db.collection("course_chats")
            .whereEqualTo("courseId", courseId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { 
                    Log.e("FirebaseRepository", "Chat listener error", error)
                    trySend(emptyList())
                    return@addSnapshotListener 
                }
                val messages = snapshot?.documents?.mapNotNull { it.toObject<ChatMessage>()?.copy(id = it.id) } ?: emptyList()
                // Sort client-side to avoid index requirements
                trySend(messages.sortedBy { it.createdAt })
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
                if (error != null) { 
                    Log.e("FirebaseRepository", "Quiz results listener error", error)
                    trySend(emptyList())
                    return@addSnapshotListener 
                }
                trySend(snapshot?.documents?.mapNotNull { it.toObject<QuizResult>()?.copy(id = it.id) } ?: emptyList())
            }
        awaitClose { subscription.remove() }
    }

    fun getLessonQuizzes(lessonId: String): Flow<List<QuizQuestion>> = callbackFlow {
        val subscription = db.collection("quizzes")
            .whereEqualTo("lessonId", lessonId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { 
                    Log.e("FirebaseRepository", "Quizzes listener error", error)
                    trySend(emptyList())
                    return@addSnapshotListener 
                }
                trySend(snapshot?.documents?.mapNotNull { it.toObject<QuizQuestion>()?.copy(id = it.id) } ?: emptyList())
            }
        awaitClose { subscription.remove() }
    }

    // --- User Profile ---
    fun getUserProfile(uid: String): Flow<User?> = callbackFlow {
        val subscription = db.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirebaseRepository", "User profile listener error", error)
                    trySend(null)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject<User>()?.copy(id = snapshot.id))
            }
        awaitClose { subscription.remove() }
    }

    fun getLeaderboard(): Flow<List<User>> = callbackFlow {
        val subscription = db.collection("users")
            .orderBy("xp", Query.Direction.DESCENDING)
            .limit(20)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { 
                    Log.e("FirebaseRepository", "Leaderboard listener error", error)
                    trySend(emptyList())
                    return@addSnapshotListener 
                }
                val users = snapshot?.documents?.mapNotNull { it.toObject<User>()?.copy(id = it.id) } ?: emptyList()
                trySend(users)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun updateUserProfile(user: User) {
        try {
            db.collection("users").document(user.id).set(user).await()
        } catch (e: Exception) { Log.e("FirebaseRepository", "Update profile failed", e) }
    }

    suspend fun incrementUserXP(userId: String, amount: Int, newLevel: Int) {
        try {
            db.collection("users").document(userId).update(
                "xp", FieldValue.increment(amount.toLong()),
                "level", newLevel
            ).await()
        } catch (e: Exception) { Log.e("FirebaseRepository", "XP Increment failed", e) }
    }

    // --- User Interaction Lists ---
    fun getUserLikes(userId: String): Flow<List<String>> = callbackFlow {
        val subscription = db.collection("likes")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { 
                    Log.e("FirebaseRepository", "Likes listener error", error)
                    trySend(emptyList())
                    return@addSnapshotListener 
                }
                trySend(snapshot?.documents?.mapNotNull { it.getString("lessonId") } ?: emptyList())
            }
        awaitClose { subscription.remove() }
    }

    fun getUserSaved(userId: String): Flow<List<String>> = callbackFlow {
        val subscription = db.collection("saved_content")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { 
                    Log.e("FirebaseRepository", "Saved listener error", error)
                    trySend(emptyList())
                    return@addSnapshotListener 
                }
                trySend(snapshot?.documents?.mapNotNull { it.getString("lessonId") } ?: emptyList())
            }
        awaitClose { subscription.remove() }
    }
}
