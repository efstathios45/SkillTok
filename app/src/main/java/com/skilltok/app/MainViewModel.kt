package com.skilltok.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FirebaseRepository()
    private val auth = FirebaseAuth.getInstance()
    private val db = SkillTokDatabase.getDatabase(application).dao()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _courses = MutableStateFlow<List<Course>>(MockData.courses)
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    private val _enrollments = MutableStateFlow<List<Enrollment>>(emptyList())
    val enrollments: StateFlow<List<Enrollment>> = _enrollments.asStateFlow()

    init {
        viewModelScope.launch {
            auth.authStateFlow().collect { firebaseUser ->
                if (firebaseUser != null) {
                    observeUser(firebaseUser.uid)
                    syncLocalData(firebaseUser.uid)
                } else {
                    _currentUser.value = null
                    _enrollments.value = emptyList()
                }
            }
        }
        observeCourses()
    }

    private fun observeUser(uid: String) {
        // First, try local DB
        viewModelScope.launch {
            db.getUserProfile(uid).collect { localUser ->
                if (localUser != null) {
                    _currentUser.value = User(
                        id = localUser.id,
                        name = localUser.name,
                        email = localUser.email,
                        xp = localUser.xp,
                        streak = localUser.streak,
                        level = localUser.level
                    )
                }
            }
        }

        // Then, observe Firebase and update local
        viewModelScope.launch {
            repository.getUserProfile(uid).collect { remoteUser ->
                if (remoteUser != null) {
                    _currentUser.value = remoteUser
                    db.insertUserProfile(LocalUserEntity(
                        remoteUser.id, remoteUser.name, remoteUser.email, 
                        remoteUser.xp, remoteUser.streak, remoteUser.level
                    ))
                } else {
                    // Fallback to Mock if new user
                    val fallback = MockData.currentUser.copy(id = uid, email = auth.currentUser?.email ?: "")
                    _currentUser.value = fallback
                }
            }
        }
    }

    private fun observeCourses() {
        viewModelScope.launch {
            repository.getCourses().collect { coursesList ->
                if (coursesList.isNotEmpty()) {
                    _courses.value = coursesList
                }
            }
        }
    }

    private fun syncLocalData(uid: String) {
        viewModelScope.launch {
            // Sync Enrollments
            repository.getEnrollments(uid).collect { remoteEnrollments ->
                _enrollments.value = remoteEnrollments
                remoteEnrollments.forEach { e ->
                    db.insertEnrollment(LocalEnrollmentEntity(
                        "${e.userId}_${e.courseId}", e.userId, e.courseId, e.status, e.progressPercent, e.currentLessonId
                    ))
                }
            }
            
            // Sync offline completions to Firebase
            val unsynced = db.getUnsyncedCompletions()
            unsynced.forEach { c ->
                repository.completeLesson(c.userId, c.lessonId)
                db.updateCompletion(c.copy(synced = true))
            }
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun enrollInCourse(courseId: String, firstLessonId: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            repository.enrollInCourse(uid, courseId, firstLessonId)
            db.insertEnrollment(LocalEnrollmentEntity(
                "${uid}_$courseId", uid, courseId, "in_progress", 0, firstLessonId
            ))
        }
    }

    fun isEnrolled(courseId: String): Boolean {
        return _enrollments.value.any { it.courseId == courseId }
    }

    fun getCourseModules(courseId: String): Flow<List<Module>> {
        return repository.getModules(courseId).map { 
            if (it.isEmpty()) MockData.modules.filter { m -> m.courseId == courseId } else it
        }
    }

    fun getModuleLessons(moduleId: String): Flow<List<Lesson>> {
        return repository.getLessons(moduleId).map {
            if (it.isEmpty()) MockData.lessons.filter { l -> l.moduleId == moduleId } else it
        }
    }

    fun getLessonQuiz(lessonId: String): Flow<List<QuizQuestion>> {
        return repository.getQuizQuestions(lessonId)
    }

    fun completeLesson(lessonId: String, score: Int? = null) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            // Update local first for instant feedback
            db.insertCompletion(LocalCompletionEntity(
                userId = uid,
                lessonId = lessonId,
                completedAt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US).format(java.util.Date()),
                synced = false
            ))
            
            // Try updating Firebase
            try {
                repository.completeLesson(uid, lessonId, score)
                // If successful, mark synced locally
                val latest = db.getUnsyncedCompletions().lastOrNull { it.lessonId == lessonId }
                if (latest != null) db.updateCompletion(latest.copy(synced = true))
            } catch (e: Exception) {
                // Stay unsynced
            }
        }
    }

    fun createCourse(title: String, description: String, subject: String, level: String, modulesData: List<ModuleData>) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            val course = Course(
                title = title,
                description = description,
                subject = subject,
                level = level,
                createdByUserId = uid
            )
            val courseId = repository.addCourse(course)
            if (courseId != null) {
                modulesData.forEachIndexed { mIndex, mData ->
                    val moduleId = repository.addModule(Module(courseId = courseId, title = mData.title, orderIndex = mIndex))
                    if (moduleId != null) {
                        mData.lessons.forEachIndexed { lIndex, lData ->
                            repository.addLesson(Lesson(
                                moduleId = moduleId,
                                courseId = courseId,
                                title = lData.title,
                                description = lData.description,
                                videoUrl = lData.videoUrl,
                                orderIndex = lIndex
                            ))
                        }
                    }
                }
            }
        }
    }

    fun sendEmailVerification() {
        auth.currentUser?.sendEmailVerification()
    }

    fun changePassword(newPassword: String) {
        auth.currentUser?.updatePassword(newPassword)
    }
}

fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}
