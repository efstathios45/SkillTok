package com.skilltok.app

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.skilltok.app.dataconnect.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class FirebaseRepository {
    private val connector = SkilltokConnectorConnector.instance
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    companion object {
        val idMap = ConcurrentHashMap<String, String>()
        fun getMockId(remoteId: String): String = idMap[remoteId.lowercase()] ?: remoteId
        fun registerMapping(remoteId: String, mockId: String) { 
            idMap[remoteId.lowercase()] = mockId 
        }
    }

    private data class UserPreferencesSnapshot(
        val name: String? = null,
        val email: String? = null,
        val photoUrl: String? = null,
        val interests: List<String> = emptyList(),
        val goals: List<String> = emptyList(),
        val onboardingCompleted: Boolean = false
    )

    private fun String.toUUID(): UUID? {
        return try {
            val clean = replace("-", "")
            if (clean.length == 32) {
                UUID.fromString(StringBuilder(clean).insert(20, "-").insert(16, "-").insert(12, "-").insert(8, "-").toString())
            } else { UUID.fromString(this) }
        } catch (e: Exception) { null }
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

    private suspend fun getUserPreferences(uid: String): UserPreferencesSnapshot {
        return try {
            val snapshot = usersCollection.document(uid).get().await()
            if (!snapshot.exists()) {
                UserPreferencesSnapshot()
            } else {
                UserPreferencesSnapshot(
                    name = snapshot.getString("displayName"),
                    email = snapshot.getString("email"),
                    photoUrl = snapshot.getString("photoUrl"),
                    interests = (snapshot.get("interests") as? List<*>)?.filterIsInstance<String>().orEmpty(),
                    goals = (snapshot.get("goals") as? List<*>)?.filterIsInstance<String>().orEmpty(),
                    onboardingCompleted = snapshot.getBoolean("onboardingCompleted") ?: false
                )
            }
        } catch (e: Exception) {
            Log.w("FirebaseRepository", "Get user preferences failed", e)
            UserPreferencesSnapshot()
        }
    }

    // --- Social Interactions with Foreign Key Safety ---
    private suspend fun ensureReelExists(lessonId: UUID, lessonContext: Lesson? = null) {
        try {
            val mockId = getMockId(lessonId.toString())
            val lesson = lessonContext ?: MockData.lessons.find { it.id == mockId } ?: return
            val videoId = extractYoutubeId(lesson.videoUrl)
            if (videoId.isEmpty()) return
            
            val remoteCourseId = idMap.entries.find { it.value == lesson.courseId }?.key
            
            connector.createReel.execute(
                id = lessonId,
                title = lesson.title,
                description = lesson.description,
                videoUrl = videoId
            ) {
                courseId = remoteCourseId?.toUUID()
            }
        } catch (e: Exception) {
            if (e.message?.contains("already exists", ignoreCase = true) == false) {
                Log.e("FirebaseRepository", "Reel registration failed: ${e.message}")
            }
        }
    }

    suspend fun toggleLike(lessonId: String, isLiked: Boolean) {
        try {
            val remoteId = idMap.entries.find { it.value == lessonId }?.key ?: lessonId
            val uuid = remoteId.toUUID() ?: return
            ensureReelExists(uuid)
            if (isLiked) connector.toggleLike.execute(reelId = uuid)
            else connector.deleteLike.execute(reelId = uuid)
        } catch (e: Exception) { Log.e("FirebaseRepository", "Like failed", e) }
    }

    suspend fun toggleSave(lessonId: String, isSaved: Boolean) {
        try {
            val remoteId = idMap.entries.find { it.value == lessonId }?.key ?: lessonId
            val uuid = remoteId.toUUID() ?: return
            ensureReelExists(uuid)
            if (isSaved) connector.toggleSave.execute(reelId = uuid)
            else connector.deleteSave.execute(reelId = uuid)
        } catch (e: Exception) { Log.e("FirebaseRepository", "Save failed", e) }
    }

    suspend fun addComment(comment: ReelComment) {
        try {
            val remoteId = idMap.entries.find { it.value == comment.lessonId }?.key ?: comment.lessonId
            val uuid = remoteId.toUUID() ?: return
            ensureReelExists(uuid)
            connector.addComment.execute(text = comment.text) {
                reelId = uuid
                courseId = null
            }
        } catch (e: Exception) { Log.e("FirebaseRepository", "Comment failed", e) }
    }

    // --- Data Fetching ---
    fun getRemoteCourses(): Flow<List<Course>> = flow {
        try {
            val result = connector.listCourses.execute()
            val remote = result.data.courses.map { remoteCourse ->
                val mockMatch = MockData.courses.find { it.title == remoteCourse.title }
                if (mockMatch != null) registerMapping(remoteCourse.id.toString(), mockMatch.id)
                Course(
                    id = remoteCourse.id.toString(),
                    title = remoteCourse.title,
                    description = remoteCourse.description.ifBlank { mockMatch?.description ?: "" },
                    thumbnailUrl = remoteCourse.thumbnailUrl.ifBlank { mockMatch?.thumbnailUrl ?: "" },
                    subject = remoteCourse.subject,
                    level = remoteCourse.level,
                    createdAt = remoteCourse.createdAt.toString()
                )
            }
            if (remote.isNotEmpty()) emit(remote)
        } catch (e: Exception) { Log.e("FirebaseRepository", "Cloud Fetch Failed", e) }
    }

    fun getModules(courseId: String): Flow<List<Module>> = flow {
        emit(MockData.modules.filter { it.courseId == getMockId(courseId) })
    }

    fun getLessons(moduleId: String): Flow<List<Lesson>> = flow {
        emit(MockData.lessons.filter { it.moduleId == getMockId(moduleId) })
    }

    fun getComments(lessonId: String): Flow<List<ReelComment>> = flow {
        try {
            val remoteId = idMap.entries.find { it.value == lessonId }?.key ?: lessonId
            val uuid = remoteId.toUUID() ?: return@flow
            val result = connector.getComments.execute(reelId = uuid)
            emit(result.data.comments.map {
                ReelComment(it.id.toString(), lessonId, it.user.id, it.user.displayName, it.text, it.createdAt.seconds * 1000)
            })
        } catch (e: Exception) { 
            Log.w("FirebaseRepository", "Failed to get comments", e)
            emit(emptyList()) 
        }
    }

    // --- Content Seeding ---
    suspend fun addCourse(course: Course): String? {
        return try {
            val result = connector.createCourse.execute(
                title = course.title,
                description = course.description,
                thumbnailUrl = course.thumbnailUrl,
                subject = course.subject,
                level = course.level
            ) { id = null }
            val remoteId = result.data.course_insert.id.toString()
            registerMapping(remoteId, course.id)
            remoteId
        } catch (e: Exception) { 
            Log.e("FirebaseRepository", "Add course failed", e)
            null 
        }
    }

    suspend fun addModule(module: Module): String? {
        return try {
            val courseUuid = module.courseId.toUUID() ?: return null
            val result = connector.createModule.execute(
                courseId = courseUuid,
                title = module.title,
                orderIndex = module.orderIndex
            ) { id = null }
            val remoteId = result.data.module_insert.id.toString()
            registerMapping(remoteId, module.id)
            remoteId
        } catch (e: Exception) { 
            Log.e("FirebaseRepository", "Add module failed", e)
            null 
        }
    }

    suspend fun addLesson(lesson: Lesson): String? {
        return try {
            val moduleUuid = lesson.moduleId.toUUID() ?: return null
            val videoId = extractYoutubeId(lesson.videoUrl)
            val result = connector.createLesson.execute(
                moduleId = moduleUuid,
                title = lesson.title,
                description = lesson.description,
                lessonType = lesson.lessonType,
                orderIndex = lesson.orderIndex,
                contentUrl = if (videoId.isNotEmpty()) videoId else lesson.videoUrl
            ) { id = null }
            val remoteId = result.data.lesson_insert.id.toString()
            registerMapping(remoteId, lesson.id)
            remoteId
        } catch (e: Exception) { 
            Log.e("FirebaseRepository", "Add lesson failed", e)
            null 
        }
    }

    suspend fun addReel(lesson: Lesson, remoteLessonId: String) {
        val uuid = remoteLessonId.toUUID() ?: return
        ensureReelExists(uuid, lesson)
    }

    suspend fun completeLesson(lessonId: String) {
        try {
            val remoteId = idMap.entries.find { it.value == lessonId }?.key ?: lessonId
            val uuid = remoteId.toUUID() ?: return
            val mockLesson = MockData.lessons.find { it.id == getMockId(remoteId) }
            val remoteCourseId = idMap.entries.find { it.value == mockLesson?.courseId }?.key ?: return
            val courseUuid = remoteCourseId.toUUID() ?: return
            connector.updateProgress.execute(courseId = courseUuid, lessonId = uuid, isCompleted = true)
        } catch (e: Exception) { Log.e("FirebaseRepository", "Complete lesson failed", e) }
    }

    suspend fun enrollInCourse(courseId: String) {
        try {
            val remoteId = idMap.entries.find { it.value == courseId }?.key ?: courseId
            val courseUuid = remoteId.toUUID() ?: return
            connector.enrollInCourse.execute(courseId = courseUuid)
        } catch (e: Exception) { Log.e("FirebaseRepository", "Enroll failed", e) }
    }

    fun getEnrollments(): Flow<List<Enrollment>> = flow {
        try {
            val result = connector.getEnrollments.execute()
            emit(result.data.enrollments.map { Enrollment(courseId = getMockId(it.courseId.toString())) })
        } catch (e: Exception) { 
            Log.w("FirebaseRepository", "Get enrollments failed", e)
            emit(emptyList()) 
        }
    }

    fun getUserLikes(): Flow<List<String>> = flow {
        try {
            val result = connector.getUserLikes.execute()
            emit(result.data.likes.map { getMockId(it.reelId.toString()) })
        } catch (e: Exception) { 
            Log.w("FirebaseRepository", "Get likes failed", e)
            emit(emptyList()) 
        }
    }

    fun getUserSaved(): Flow<List<String>> = flow {
        try {
            val result = connector.getUserSaved.execute()
            emit(result.data.savedContents.map { getMockId(it.reelId.toString()) })
        } catch (e: Exception) { 
            Log.w("FirebaseRepository", "Get saved failed", e)
            emit(emptyList()) 
        }
    }

    suspend fun fetchUserProfile(uid: String): User? {
        val preferences = getUserPreferences(uid)
        val baseProfile = try {
            val result = connector.getUserProfile.execute(id = uid)
            result.data.user?.let { data ->
                User(
                    id = data.id,
                    name = data.displayName,
                    email = data.email,
                    photoUrl = data.photoUrl,
                    bio = data.bio,
                    role = data.role
                )
            }
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Get profile failed", e)
            null
        }

        return when {
            baseProfile != null -> {
                baseProfile.copy(
                    name = preferences.name ?: baseProfile.name,
                    email = preferences.email?.takeIf { it.isNotBlank() } ?: baseProfile.email,
                    photoUrl = preferences.photoUrl ?: baseProfile.photoUrl,
                    interests = preferences.interests,
                    goals = preferences.goals,
                    onboardingCompleted = preferences.onboardingCompleted
                )
            }
            preferences.name != null || preferences.email != null || preferences.photoUrl != null ||
                preferences.interests.isNotEmpty() || preferences.goals.isNotEmpty() || preferences.onboardingCompleted -> {
                User(
                    id = uid,
                    name = preferences.name ?: "Learner",
                    email = preferences.email.orEmpty(),
                    photoUrl = preferences.photoUrl,
                    interests = preferences.interests,
                    goals = preferences.goals,
                    onboardingCompleted = preferences.onboardingCompleted
                )
            }
            else -> null
        }
    }

    fun getUserProfile(uid: String): Flow<User?> = flow {
        try {
            emit(fetchUserProfile(uid))
        } catch (e: Exception) { 
            Log.e("FirebaseRepository", "Get profile failed", e)
            emit(null) 
        }
    }

    suspend fun updateUserProfile(user: User) {
        try {
            connector.upsertUser.execute(displayName = user.name, email = user.email, photoUrl = user.photoUrl ?: "")
        } catch (e: Exception) { Log.e("FirebaseRepository", "Update profile failed", e) }
        try {
            val payload = mutableMapOf<String, Any?>(
                "displayName" to user.name,
                "email" to user.email,
                "photoUrl" to user.photoUrl,
                "interests" to user.interests,
                "goals" to user.goals,
                "onboardingCompleted" to user.onboardingCompleted
            )
            usersCollection.document(user.id).set(payload, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Update user preferences failed", e)
        }
    }
}
