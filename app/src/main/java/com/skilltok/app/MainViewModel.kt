package com.skilltok.app

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class EnrollmentState {
    object Idle : EnrollmentState()
    object Loading : EnrollmentState()
    data class Success(val courseId: String) : EnrollmentState()
    data class Error(val message: String) : EnrollmentState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FirebaseRepository()
    private val auth = FirebaseAuth.getInstance()
    private val db = SkillTokDatabase.getDatabase(application).dao()
    private val prefs = application.getSharedPreferences("skilltok_prefs", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _courses = MutableStateFlow<List<Course>>(MockData.courses)
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    private val _enrollments = MutableStateFlow<List<Enrollment>>(emptyList())
    val enrollments: StateFlow<List<Enrollment>> = _enrollments.asStateFlow()

    private val _enrollmentState = MutableStateFlow<EnrollmentState>(EnrollmentState.Idle)
    val enrollmentState: StateFlow<EnrollmentState> = _enrollmentState.asStateFlow()

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("dark_mode", true))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

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

    fun toggleTheme(darkMode: Boolean) {
        _isDarkMode.value = darkMode
        prefs.edit().putBoolean("dark_mode", darkMode).apply()
    }

    private fun observeUser(uid: String) {
        viewModelScope.launch {
            db.getUserProfile(uid).collect { localUser ->
                if (localUser != null) {
                    val current = _currentUser.value
                    _currentUser.value = User(
                        id = localUser.id,
                        name = localUser.name,
                        email = localUser.email,
                        xp = localUser.xp,
                        streak = localUser.streak,
                        level = localUser.level,
                        interests = current?.interests ?: emptyList(),
                        goals = current?.goals ?: emptyList(),
                        onboardingCompleted = current?.onboardingCompleted ?: false
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.getUserProfile(uid).collect { remoteUser ->
                if (remoteUser != null) {
                    _currentUser.value = remoteUser
                    db.insertUserProfile(LocalUserEntity(
                        remoteUser.id, remoteUser.name, remoteUser.email, 
                        remoteUser.xp, remoteUser.streak, remoteUser.level
                    ))
                } else {
                    val fallback = User(id = uid, email = auth.currentUser?.email ?: "", name = auth.currentUser?.displayName ?: "New Learner")
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

    fun completeOnboarding(interests: List<String>, goals: List<String>) {
        val user = _currentUser.value ?: return
        val updatedUser = user.copy(
            interests = interests,
            goals = goals,
            onboardingCompleted = true
        )
        _currentUser.value = updatedUser
        
        viewModelScope.launch {
            repository.updateUserProfile(updatedUser)
            db.insertUserProfile(LocalUserEntity(
                updatedUser.id, updatedUser.name, updatedUser.email,
                updatedUser.xp, updatedUser.streak, updatedUser.level
            ))
        }
    }

    private fun syncLocalData(uid: String) {
        viewModelScope.launch {
            repository.getEnrollments(uid).collect { remoteEnrollments ->
                _enrollments.value = remoteEnrollments
                remoteEnrollments.forEach { e ->
                    db.insertEnrollment(LocalEnrollmentEntity(
                        "${e.userId}_${e.courseId}", e.userId, e.courseId, e.status, e.progressPercent, e.currentLessonId
                    ))
                }
            }
            
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

    fun resetEnrollmentState() {
        _enrollmentState.value = EnrollmentState.Idle
    }

    fun enrollInCourse(courseId: String, startLessonId: String? = null) {
        val uid = auth.currentUser?.uid ?: run {
            _enrollmentState.value = EnrollmentState.Error("Please log in to enroll")
            return
        }
        
        viewModelScope.launch {
            _enrollmentState.value = EnrollmentState.Loading
            try {
                val finalLessonId = if (startLessonId != null) {
                    startLessonId
                } else {
                    // dynamically find the first lesson
                    val modules = getCourseModules(courseId).first()
                    val firstModuleId = modules.firstOrNull()?.id
                    if (firstModuleId != null) {
                        getModuleLessons(firstModuleId).first().firstOrNull()?.id
                    } else null
                }

                if (finalLessonId == null) {
                    _enrollmentState.value = EnrollmentState.Error("This course doesn't have any lessons yet")
                    return@launch
                }

                repository.enrollInCourse(uid, courseId, finalLessonId)
                
                // Optimistic local update
                db.insertEnrollment(LocalEnrollmentEntity(
                    "${uid}_$courseId", uid, courseId, "in_progress", 0, finalLessonId
                ))
                
                // Refresh enrollment list
                val updatedEnrollments = repository.getEnrollments(uid).first()
                _enrollments.value = updatedEnrollments
                
                _enrollmentState.value = EnrollmentState.Success(courseId)
            } catch (e: Exception) {
                _enrollmentState.value = EnrollmentState.Error("Enrollment failed: ${e.message}")
            }
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
            db.insertCompletion(LocalCompletionEntity(
                userId = uid,
                lessonId = lessonId,
                completedAt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US).format(java.util.Date()),
                synced = false
            ))
            
            try {
                repository.completeLesson(uid, lessonId, score)
                val latest = db.getUnsyncedCompletions().lastOrNull { it.lessonId == lessonId }
                if (latest != null) db.updateCompletion(latest.copy(synced = true))
            } catch (e: Exception) {
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
}

fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}
