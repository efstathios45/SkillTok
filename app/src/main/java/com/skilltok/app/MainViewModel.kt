package com.skilltok.app

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

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

    private val _courses = MutableStateFlow<List<Course>>(MockData.courses)
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    private val _enrollments = MutableStateFlow<List<Enrollment>>(emptyList())
    val enrollments: StateFlow<List<Enrollment>> = _enrollments.asStateFlow()

    private val _enrollmentState = MutableStateFlow<EnrollmentState>(EnrollmentState.Idle)
    val enrollmentState: StateFlow<EnrollmentState> = _enrollmentState.asStateFlow()

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    private val _comments = MutableStateFlow<Map<String, List<ReelComment>>>(emptyMap())
    val comments: StateFlow<Map<String, List<ReelComment>>> = _comments.asStateFlow()

    private val _likes = MutableStateFlow<Set<String>>(emptySet())
    val likes: StateFlow<Set<String>> = _likes.asStateFlow()

    private val _savedVideos = MutableStateFlow<Set<String>>(emptySet())
    val savedVideos: StateFlow<Set<String>> = _savedVideos.asStateFlow()

    private val _reviews = MutableStateFlow<Map<String, List<CourseReview>>>(emptyMap())
    val reviews: StateFlow<Map<String, List<CourseReview>>> = _reviews.asStateFlow()

    private var userDataJob: Job? = null
    private val activeCommentJobs = mutableMapOf<String, Job>()
    private var isSeeding = false

    init {
        loadLocalData()
        loadCourses()
        observeAuthState()
    }

    private fun loadLocalData() {
        viewModelScope.launch {
            db.getAllCourses().collect { locals ->
                if (locals.isNotEmpty()) {
                    _courses.value = locals.map { 
                        Course(
                            id = it.firebaseId ?: "",
                            title = it.title,
                            description = it.description,
                            thumbnailUrl = it.thumbnailUrl,
                            subject = it.subject,
                            level = it.level
                        ) 
                    }
                }
            }
        }
    }

    private fun loadCourses() {
        viewModelScope.launch {
            repository.getRemoteCourses().collect { remoteCourses ->
                if (remoteCourses.isNotEmpty()) {
                    _courses.value = remoteCourses
                    remoteCourses.forEach { 
                        db.insertCourse(
                            LocalCourseEntity(
                                firebaseId = it.id,
                                title = it.title,
                                description = it.description,
                                thumbnailUrl = it.thumbnailUrl,
                                subject = it.subject,
                                level = it.level
                            )
                        ) 
                    }
                } else if (auth.currentUser != null && !isSeeding) {
                    seedDatabase()
                }
            }
        }
    }

    private suspend fun seedDatabase() {
        if (isSeeding) return
        val user = auth.currentUser ?: return
        isSeeding = true
        Log.d("MainViewModel", "Syncing Cloud SQL with MockData...")
        
        try {
            MockData.courses.forEach { mockCourse ->
                val newCourseId = repository.addCourse(mockCourse)
                if (newCourseId != null) {
                    MockData.modules.filter { it.courseId == mockCourse.id }.forEach { mockModule ->
                        val newModuleId = repository.addModule(mockModule.copy(courseId = newCourseId))
                        if (newModuleId != null) {
                            MockData.lessons.filter { it.moduleId == mockModule.id }.forEach { mockLesson ->
                                val newLessonId = repository.addLesson(mockLesson.copy(moduleId = newModuleId, courseId = newCourseId))
                                if (newLessonId != null) {
                                    repository.addReel(mockLesson.copy(courseId = newCourseId), newLessonId)
                                }
                            }
                        }
                    }
                }
            }
            Log.d("MainViewModel", "Cloud Seeding Finished Successfully")
            repository.getRemoteCourses().collect { if (it.isNotEmpty()) _courses.value = it }
        } catch (e: Exception) {
            Log.e("MainViewModel", "Cloud Seed Failed: ${e.message}")
        } finally {
            isSeeding = false
        }
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            auth.authStateFlow().collect { firebaseUser ->
                if (firebaseUser != null) {
                    syncUserToDatabase(firebaseUser)
                    loadUserData(firebaseUser.uid)
                    loadCourses()
                } else {
                    clearUserData()
                }
            }
        }
    }

    private fun clearUserData() {
        userDataJob?.cancel()
        activeCommentJobs.values.forEach { it.cancel() }
        activeCommentJobs.clear()
        _userProfile.value = null
        _enrollments.value = emptyList()
        _likes.value = emptySet()
        _savedVideos.value = emptySet()
        _comments.value = emptyMap()
        _reviews.value = emptyMap()
    }

    private fun loadUserData(uid: String) {
        userDataJob?.cancel()
        userDataJob = viewModelScope.launch {
            launch {
                db.getUserProfile(uid).collect { local ->
                    if (local != null && _userProfile.value == null) {
                        _userProfile.value = User(
                            id = local.id,
                            name = local.name,
                            email = local.email,
                            xp = local.xp,
                            streak = local.streak,
                            level = local.level,
                            onboardingCompleted = local.onboardingCompleted,
                            interests = local.interests.split(",").filter { it.isNotBlank() },
                            goals = local.goals.split(",").filter { it.isNotBlank() }
                        )
                    }
                }
            }
            launch {
                repository.getUserProfile(uid).collect { remoteUser ->
                    if (remoteUser != null) {
                        // Merge remote profile but preserve locally-stored onboarding state
                        val localProfile = _userProfile.value
                        val merged = remoteUser.copy(
                            onboardingCompleted = localProfile?.onboardingCompleted ?: remoteUser.onboardingCompleted,
                            interests = localProfile?.interests?.ifEmpty { null } ?: remoteUser.interests,
                            goals = localProfile?.goals?.ifEmpty { null } ?: remoteUser.goals
                        )
                        _userProfile.value = merged
                        db.insertUserProfile(
                            LocalUserEntity(
                                id = merged.id,
                                name = merged.name,
                                email = merged.email,
                                xp = merged.xp,
                                streak = merged.streak,
                                level = merged.level,
                                onboardingCompleted = merged.onboardingCompleted,
                                interests = merged.interests.joinToString(","),
                                goals = merged.goals.joinToString(",")
                            )
                        )
                    }
                }
            }
            launch { repository.getEnrollments().collect { _enrollments.value = it } }
            launch { repository.getUserSaved().collect { _savedVideos.value = it.toSet() } }
            launch { repository.getUserLikes().collect { _likes.value = it.toSet() } }
        }
    }

    suspend fun syncUserToDatabase(firebaseUser: FirebaseUser, customName: String? = null) {
        val uid = firebaseUser.uid
        // Check if user already exists locally to preserve onboarding state
        val existingLocal = db.getUserProfileOnce(uid)
        val newUser = User(
            id = uid,
            name = customName ?: firebaseUser.displayName ?: "Learner",
            email = firebaseUser.email ?: "",
            photoUrl = firebaseUser.photoUrl?.toString(),
            onboardingCompleted = existingLocal?.onboardingCompleted ?: false,
            interests = existingLocal?.interests?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
            goals = existingLocal?.goals?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
        )
        repository.updateUserProfile(newUser)
        _userProfile.value = newUser
        db.insertUserProfile(
            LocalUserEntity(
                id = newUser.id,
                name = newUser.name,
                email = newUser.email,
                xp = existingLocal?.xp ?: 0,
                streak = existingLocal?.streak ?: 0,
                level = existingLocal?.level ?: 1,
                onboardingCompleted = newUser.onboardingCompleted,
                interests = newUser.interests.joinToString(","),
                goals = newUser.goals.joinToString(",")
            )
        )
    }

    fun toggleLike(lessonId: String) {
        val uid = auth.currentUser?.uid ?: return
        val currentlyLiked = _likes.value.contains(lessonId)
        viewModelScope.launch {
            if (currentlyLiked) _likes.value -= lessonId else _likes.value += lessonId
            repository.toggleLike(lessonId, !currentlyLiked)
            db.insertInteraction(LocalInteractionEntity("${uid}_$lessonId", uid, lessonId, !currentlyLiked))
        }
    }

    fun toggleSave(lessonId: String) {
        val uid = auth.currentUser?.uid ?: return
        val currentlySaved = _savedVideos.value.contains(lessonId)
        viewModelScope.launch {
            if (currentlySaved) {
                _savedVideos.value -= lessonId
                repository.toggleSave(lessonId, false)
                db.deleteSaved(uid, lessonId)
            } else {
                _savedVideos.value += lessonId
                repository.toggleSave(lessonId, true)
                db.insertSaved(LocalSavedEntity("${uid}_$lessonId", uid, lessonId))
            }
        }
    }

    fun addComment(lessonId: String, text: String) {
        val user = _userProfile.value ?: return
        val commentId = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()
        val comment = ReelComment(commentId, lessonId, user.id, user.name, text, timestamp)
        viewModelScope.launch {
            db.insertComment(LocalCommentEntity(commentId, lessonId, user.id, user.name, text, timestamp))
            repository.addComment(comment)
            loadComments(lessonId)
        }
    }

    fun loadComments(lessonId: String) {
        if (activeCommentJobs.containsKey(lessonId)) return
        activeCommentJobs[lessonId] = viewModelScope.launch {
            db.getComments(lessonId).collect { locals ->
                _comments.value += (lessonId to locals.map { ReelComment(it.id, it.lessonId, it.userId, it.userName, it.text, it.createdAt) })
            }
        }
        viewModelScope.launch {
            repository.getComments(lessonId).collect { remotes ->
                remotes.forEach { db.insertComment(LocalCommentEntity(it.id, it.lessonId, it.userId, it.userName, it.text, it.createdAt)) }
            }
        }
    }

    fun completeOnboarding(interests: List<String>, goals: List<String>) {
        val user = _userProfile.value ?: return
        val updated = user.copy(interests = interests, goals = goals, onboardingCompleted = true)
        _userProfile.value = updated
        viewModelScope.launch {
            repository.updateUserProfile(updated)
            db.insertUserProfile(
                LocalUserEntity(
                    id = updated.id,
                    name = updated.name,
                    email = updated.email,
                    xp = updated.xp,
                    streak = updated.streak,
                    level = updated.level,
                    onboardingCompleted = true,
                    interests = interests.joinToString(","),
                    goals = goals.joinToString(",")
                )
            )
        }
    }

    fun getCourseModules(courseId: String): Flow<List<Module>> = repository.getModules(courseId).map { 
        if (it.isEmpty()) MockData.modules.filter { m -> m.courseId == courseId } else it
    }
    fun getModuleLessons(moduleId: String): Flow<List<Lesson>> = repository.getLessons(moduleId).map {
        if (it.isEmpty()) MockData.lessons.filter { l -> l.moduleId == moduleId } else it
    }

    fun getModuleLessonsForReels(courseId: String): Flow<List<Lesson>> = flow {
        val initial = MockData.lessons.filter { it.courseId == courseId }
        emit(initial)
        
        repository.getModules(courseId).collect { mods ->
            if (mods.isNotEmpty()) {
                val allLessons = mutableListOf<Lesson>()
                for (mod in mods) {
                    repository.getLessons(mod.id).collect { lessons ->
                        allLessons.addAll(lessons)
                        if (allLessons.isNotEmpty()) emit(allLessons.toList())
                    }
                }
            }
        }
    }

    fun resetEnrollmentState() { _enrollmentState.value = EnrollmentState.Idle }

    fun enrollInCourse(courseId: String, startLessonId: String? = null) {
        val uid = auth.currentUser?.uid ?: return
        // Prevent re-enrollment
        if (_enrollments.value.any { it.courseId == courseId }) return
        viewModelScope.launch {
            _enrollmentState.value = EnrollmentState.Loading
            try {
                repository.enrollInCourse(courseId)
                db.insertEnrollment(LocalEnrollmentEntity("${uid}_$courseId", uid, courseId, "enrolled", 0, startLessonId ?: ""))
                _enrollments.value += Enrollment(courseId = courseId)
                _enrollmentState.value = EnrollmentState.Success(courseId)
                SoundManager.playEnroll()
            } catch (e: Exception) {
                _enrollmentState.value = EnrollmentState.Error(e.message ?: "Enrollment failed")
            }
        }
    }

    fun isEnrolled(courseId: String): Boolean = _enrollments.value.any { it.courseId == courseId }

    fun completeLesson(lessonId: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            repository.completeLesson(lessonId)
            db.insertCompletion(LocalCompletionEntity(userId = uid, lessonId = lessonId, completedAt = System.currentTimeMillis().toString()))
        }
    }

    fun createCourse(title: String, description: String, subject: String, level: String, modules: List<ModuleData>) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            val courseId = repository.addCourse(Course(title = title, description = description, subject = subject, level = level, createdByUserId = uid))
            if (courseId != null) {
                modules.forEachIndexed { mIdx, mData ->
                    val moduleId = repository.addModule(Module(courseId = courseId, title = mData.title, orderIndex = mIdx))
                    if (moduleId != null) {
                        mData.lessons.forEachIndexed { lIdx, lData ->
                            val lesson = Lesson(moduleId = moduleId, courseId = courseId, title = lData.title, videoUrl = lData.videoUrl, orderIndex = lIdx, lessonType = "video", type = "reel")
                            val newLessonId = repository.addLesson(lesson)
                            if (newLessonId != null) {
                                repository.addReel(lesson, newLessonId)
                            }
                        }
                    }
                }
                loadCourses()
            }
        }
    }

    fun loadReviews(courseId: String) {}
    fun addReview(review: CourseReview) {}

    fun logout() {
        auth.signOut()
    }
}

fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}
