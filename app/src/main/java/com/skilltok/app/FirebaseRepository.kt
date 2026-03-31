package com.skilltok.app

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private fun getCurrentIsoTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }

    // --- User Profile ---
    fun getUserProfile(uid: String): Flow<User?> = callbackFlow {
        val subscription = db.collection("user_profiles").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(User::class.java))
            }
        awaitClose { subscription.remove() }
    }.catch { emit(null) }

    suspend fun updateUserProfile(user: User) {
        db.collection("user_profiles").document(user.id).set(user, SetOptions.merge()).await()
    }

    // --- Courses ---
    fun getCourses(): Flow<List<Course>> = callbackFlow {
        val subscription = db.collection("courses")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList<Course>())
                    return@addSnapshotListener
                }
                val courses = snapshot?.documents?.mapNotNull { it.toObject(Course::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(courses)
            }
        awaitClose { subscription.remove() }
    }.catch { emit(emptyList()) }

    suspend fun addCourse(course: Course): String? {
        return try {
            val courseWithTimestamp = course.copy(createdAt = getCurrentIsoTimestamp())
            val doc = db.collection("courses").add(courseWithTimestamp).await()
            doc.id
        } catch (e: Exception) {
            null
        }
    }

    // --- Modules ---
    fun getModules(courseId: String): Flow<List<Module>> = callbackFlow {
        val subscription = db.collection("modules")
            .whereEqualTo("courseId", courseId)
            .orderBy("orderIndex")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList<Module>())
                    return@addSnapshotListener
                }
                val modules = snapshot?.documents?.mapNotNull { it.toObject(Module::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(modules)
            }
        awaitClose { subscription.remove() }
    }.catch { emit(emptyList()) }

    suspend fun addModule(module: Module): String? {
        return try {
            val doc = db.collection("modules").add(module).await()
            doc.id
        } catch (e: Exception) {
            null
        }
    }

    // --- Lessons ---
    fun getLessons(moduleId: String): Flow<List<Lesson>> = callbackFlow {
        val subscription = db.collection("lessons")
            .whereEqualTo("moduleId", moduleId)
            .orderBy("orderIndex")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList<Lesson>())
                    return@addSnapshotListener
                }
                val lessons = snapshot?.documents?.mapNotNull { it.toObject(Lesson::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(lessons)
            }
        awaitClose { subscription.remove() }
    }.catch { emit(emptyList()) }

    suspend fun addLesson(lesson: Lesson): String? {
        return try {
            val doc = db.collection("lessons").add(lesson).await()
            doc.id
        } catch (e: Exception) {
            null
        }
    }

    // --- Quiz Questions ---
    fun getQuizQuestions(lessonId: String): Flow<List<QuizQuestion>> = callbackFlow {
        val subscription = db.collection("quiz_questions")
            .whereEqualTo("lessonId", lessonId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList<QuizQuestion>())
                    return@addSnapshotListener
                }
                val questions = snapshot?.documents?.mapNotNull { it.toObject(QuizQuestion::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(questions)
            }
        awaitClose { subscription.remove() }
    }.catch { emit(emptyList()) }

    // --- Enrollments ---
    fun getEnrollments(userId: String): Flow<List<Enrollment>> = callbackFlow {
        val subscription = db.collection("user_enrollments")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList<Enrollment>())
                    return@addSnapshotListener
                }
                val enrollments = snapshot?.documents?.mapNotNull { it.toObject(Enrollment::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(enrollments)
            }
        awaitClose { subscription.remove() }
    }.catch { emit(emptyList()) }

    suspend fun enrollInCourse(userId: String, courseId: String, firstLessonId: String) {
        val enrollment = Enrollment(
            id = "${userId}_$courseId",
            userId = userId,
            courseId = courseId,
            status = "in_progress",
            currentLessonId = firstLessonId,
            startedAt = getCurrentIsoTimestamp()
        )
        db.collection("user_enrollments").document("${userId}_$courseId").set(enrollment).await()
    }

    suspend fun completeLesson(userId: String, lessonId: String, score: Int? = null) {
        try {
            val completion = LessonCompletion(
                userId = userId,
                lessonId = lessonId,
                completedAt = getCurrentIsoTimestamp(),
                quizScore = score
            )
            db.collection("lesson_completions").add(completion).await()
            db.collection("user_profiles").document(userId).update("xp", FieldValue.increment(25))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // --- Engagement ---
    suspend fun toggleLike(userId: String, lessonId: String, isLiked: Boolean) {
        try {
            val interactionRef = db.collection("user_lesson_interactions").document("${userId}_$lessonId")
            db.runTransaction { transaction ->
                transaction.set(interactionRef, mapOf("liked" to isLiked), SetOptions.merge())
            }.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun addComment(comment: ReelComment) {
        try {
            val commentWithTimestamp = comment.copy(createdAt = getCurrentIsoTimestamp())
            db.collection("reel_comments").add(commentWithTimestamp).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
