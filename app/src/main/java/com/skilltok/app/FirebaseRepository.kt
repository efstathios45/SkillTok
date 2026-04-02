package com.skilltok.app

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.skilltok.app.dataconnect.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val connector = SkilltokConnectorConnector.instance

    companion object {
        val idMap = ConcurrentHashMap<String, String>()
        fun getMockId(remoteId: String): String = idMap[remoteId] ?: remoteId
        fun registerMapping(remoteId: String, mockId: String) { idMap[remoteId] = mockId }
    }

    private fun String.toUUID(): UUID? {
        return try {
            val clean = replace("-", "")
            if (clean.length == 32) {
                UUID.fromString(StringBuilder(clean).insert(20, "-").insert(16, "-").insert(12, "-").insert(8, "-").toString())
            } else { UUID.fromString(this) }
        } catch (e: Exception) { null }
    }

    // --- Social Interactions with Foreign Key Safety ---
    private suspend fun ensureReelExists(lessonId: UUID) {
        try {
            // We attempt to create the reel. If it already exists, Data Connect/Postgres 
            // will either ignore or we handle the conflict. For simplicity in this logic,
            // we check if we can find it or just try-catch the insert.
            val lesson = MockData.lessons.find { it.id == getMockId(lessonId.toString()) }
            connector.createReel.execute(
                id = lessonId,
                title = lesson?.title ?: "Video Lesson",
                description = lesson?.description ?: "",
                videoUrl = lesson?.videoUrl ?: ""
            ) {
                courseId = lesson?.courseId?.toUUID()
            }
        } catch (e: Exception) {
            // Ignore errors if reel already exists (Primary Key violation)
            if (e.message?.contains("already exists") == false) {
                Log.d("FirebaseRepository", "Reel check/create: ${e.message}")
            }
        }
    }

    suspend fun toggleLike(userId: String, lessonId: String, isLiked: Boolean) {
        try {
            val remoteId = idMap.entries.find { it.value == lessonId }?.key ?: lessonId
            val uuid = remoteId.toUUID() ?: return
            ensureReelExists(uuid)
            if (isLiked) connector.toggleLike.execute(reelId = uuid)
            else connector.deleteLike.execute(reelId = uuid)
        } catch (e: Exception) { Log.e("FirebaseRepository", "Like failed: ${e.message}") }
    }

    suspend fun toggleSave(userId: String, lessonId: String, isSaved: Boolean) {
        try {
            val remoteId = idMap.entries.find { it.value == lessonId }?.key ?: lessonId
            val uuid = remoteId.toUUID() ?: return
            ensureReelExists(uuid)
            if (isSaved) connector.toggleSave.execute(reelId = uuid)
            else connector.deleteSave.execute(reelId = uuid)
        } catch (e: Exception) { Log.e("FirebaseRepository", "Save failed: ${e.message}") }
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
        } catch (e: Exception) { Log.e("FirebaseRepository", "Comment failed: ${e.message}") }
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
        } catch (e: Exception) { Log.e("FirebaseRepository", "Cloud Fetch Failed") }
    }

    fun getModules(courseId: String): Flow<List<Module>> = flow {
        try {
            val remoteCourseId = idMap.entries.find { it.value == courseId }?.key ?: courseId
            val uuid = remoteCourseId.toUUID() ?: throw Exception("Invalid ID")
            val result = connector.getModules.execute(courseId = uuid)
            val remote = result.data.modules.map { rm ->
                val mockMatch = MockData.modules.find { m -> m.title == rm.title && m.courseId == courseId }
                if (mockMatch != null) registerMapping(rm.id.toString(), mockMatch.id)
                Module(id = rm.id.toString(), courseId = courseId, title = rm.title, description = rm.description ?: "", orderIndex = rm.orderIndex)
            }
            if (remote.isNotEmpty()) emit(remote)
        } catch (e: Exception) {
            emit(MockData.modules.filter { it.courseId == getMockId(courseId) })
        }
    }

    fun getLessons(moduleId: String): Flow<List<Lesson>> = flow {
        try {
            val remoteModuleId = idMap.entries.find { it.value == moduleId }?.key ?: moduleId
            val uuid = remoteModuleId.toUUID() ?: throw Exception("Invalid ID")
            val result = connector.getLessons.execute(moduleId = uuid)
            val remote = result.data.lessons.map { rl ->
                val mockMatch = MockData.lessons.find { l -> l.title == rl.title && l.moduleId == moduleId }
                if (mockMatch != null) registerMapping(rl.id.toString(), mockMatch.id)
                Lesson(id = rl.id.toString(), moduleId = moduleId, title = rl.title, description = rl.description, videoUrl = rl.contentUrl, lessonType = rl.lessonType, orderIndex = rl.orderIndex)
            }
            if (remote.isNotEmpty()) emit(remote)
        } catch (e: Exception) {
            emit(MockData.lessons.filter { it.moduleId == getMockId(moduleId) })
        }
    }

    fun getComments(lessonId: String): Flow<List<ReelComment>> = flow {
        try {
            val remoteId = idMap.entries.find { it.value == lessonId }?.key ?: lessonId
            val uuid = remoteId.toUUID() ?: return@flow
            val result = connector.getComments.execute(reelId = uuid)
            emit(result.data.comments.map {
                ReelComment(it.id.toString(), lessonId, it.user.id, it.user.displayName, it.text, it.createdAt.seconds * 1000)
            })
        } catch (e: Exception) { emit(emptyList()) }
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
        } catch (e: Exception) { null }
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
        } catch (e: Exception) { null }
    }

    suspend fun addLesson(lesson: Lesson): String? {
        return try {
            val moduleUuid = lesson.moduleId.toUUID() ?: return null
            val result = connector.createLesson.execute(
                moduleId = moduleUuid,
                title = lesson.title,
                description = lesson.description,
                lessonType = lesson.lessonType,
                orderIndex = lesson.orderIndex,
                contentUrl = lesson.videoUrl
            ) { id = null }
            val remoteId = result.data.lesson_insert.id.toString()
            registerMapping(remoteId, lesson.id)
            remoteId
        } catch (e: Exception) { null }
    }

    suspend fun addReel(lesson: Lesson, remoteLessonId: String) {
        val uuid = remoteLessonId.toUUID() ?: return
        ensureReelExists(uuid)
    }

    suspend fun completeLesson(userId: String, lessonId: String) {
        try {
            val remoteId = idMap.entries.find { it.value == lessonId }?.key ?: lessonId
            val uuid = remoteId.toUUID() ?: return
            val mockLesson = MockData.lessons.find { it.id == getMockId(remoteId) }
            val remoteCourseId = idMap.entries.find { it.value == mockLesson?.courseId }?.key ?: return
            val courseUuid = remoteCourseId.toUUID() ?: return
            connector.updateProgress.execute(courseId = courseUuid, lessonId = uuid, isCompleted = true)
        } catch (e: Exception) { }
    }

    suspend fun enrollInCourse(userId: String, courseId: String) {
        try {
            val remoteId = idMap.entries.find { it.value == courseId }?.key ?: courseId
            val courseUuid = remoteId.toUUID() ?: return
            connector.enrollInCourse.execute(courseId = courseUuid)
        } catch (e: Exception) { }
    }

    fun getEnrollments(userId: String): Flow<List<Enrollment>> = flow {
        try {
            val result = connector.getEnrollments.execute()
            emit(result.data.enrollments.map { Enrollment(courseId = getMockId(it.courseId.toString())) })
        } catch (e: Exception) { emit(emptyList()) }
    }

    fun getUserLikes(userId: String): Flow<List<String>> = flow {
        try {
            val result = connector.getUserLikes.execute()
            emit(result.data.likes.map { getMockId(it.reelId.toString()) })
        } catch (e: Exception) { emit(emptyList()) }
    }

    fun getUserSaved(userId: String): Flow<List<String>> = flow {
        try {
            val result = connector.getUserSaved.execute()
            emit(result.data.savedContents.map { getMockId(it.reelId.toString()) })
        } catch (e: Exception) { emit(emptyList()) }
    }

    fun getUserProfile(uid: String): Flow<User?> = flow {
        try {
            val result = connector.getUserProfile.execute(id = uid)
            val data = result.data.user
            if (data != null) emit(User(id = data.id, name = data.displayName, email = data.email, photoUrl = data.photoUrl, bio = data.bio, role = data.role))
        } catch (e: Exception) { emit(null) }
    }

    suspend fun updateUserProfile(user: User) {
        try {
            connector.upsertUser.execute(displayName = user.name, email = user.email, photoUrl = user.photoUrl ?: "")
        } catch (e: Exception) { }
    }
}
