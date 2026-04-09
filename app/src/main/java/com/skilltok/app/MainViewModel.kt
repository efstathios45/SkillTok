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
    private val soundManager = SoundManager(application)

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
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

    private val _completedLessons = MutableStateFlow<Set<String>>(emptySet())
    val completedLessons: StateFlow<Set<String>> = _completedLessons.asStateFlow()

    // Professor & Forum Specific Flows
    private val _participants = MutableStateFlow<Map<String, List<Enrollment>>>(emptyMap())
    val participants: StateFlow<Map<String, List<Enrollment>>> = _participants.asStateFlow()

    private val _quizResults = MutableStateFlow<Map<String, List<QuizResult>>>(emptyMap())
    val quizResults: StateFlow<Map<String, List<QuizResult>>> = _quizResults.asStateFlow()

    private val _forumTopics = MutableStateFlow<Map<String, List<ForumTopic>>>(emptyMap())
    val forumTopics: StateFlow<Map<String, List<ForumTopic>>> = _forumTopics.asStateFlow()

    private val _forumReplies = MutableStateFlow<Map<String, List<ForumReply>>>(emptyMap())
    val forumReplies: StateFlow<Map<String, List<ForumReply>>> = _forumReplies.asStateFlow()

    private val _levelUpEvent = MutableSharedFlow<Int>()
    val levelUpEvent = _levelUpEvent.asSharedFlow()

    private var userDataJob: Job? = null
    private val activeCommentJobs = mutableMapOf<String, Job>()
    private val activeReviewJobs = mutableMapOf<String, Job>()
    private var isSeeding = false

    init {
        loadCourses()
        observeAuthState()
    }

    private fun loadCourses() {
        viewModelScope.launch {
            repository.getRemoteCourses().collect { remoteCourses ->
                _courses.value = (remoteCourses + MockData.courses).distinctBy { it.id }
                remoteCourses.forEach { 
                    db.insertCourse(LocalCourseEntity(it.id, it.title, it.description, it.thumbnailUrl, it.subject, it.level)) 
                }
                if (remoteCourses.isEmpty() && auth.currentUser != null && !isSeeding) {
                    seedDatabase()
                }
            }
        }
    }

    private suspend fun seedDatabase() {
        if (isSeeding) return
        isSeeding = true
        try {
            MockData.courses.forEach { mockCourse ->
                repository.addCourse(mockCourse)
                MockData.modules.filter { it.courseId == mockCourse.id }.forEach { mockModule ->
                    repository.addModule(mockModule)
                    MockData.lessons.filter { it.moduleId == mockModule.id }.forEach { mockLesson ->
                        repository.addLesson(mockLesson)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MainViewModel", "Seed Failed: ${e.message}")
        } finally {
            isSeeding = false
        }
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            auth.authStateFlow().collect { firebaseUser ->
                if (firebaseUser != null) {
                    loadUserData(firebaseUser.uid)
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
        activeReviewJobs.values.forEach { it.cancel() }
        activeReviewJobs.clear()
        _userProfile.value = null
        _enrollments.value = emptyList()
        _likes.value = emptySet()
        _savedVideos.value = emptySet()
        _comments.value = emptyMap()
        _reviews.value = emptyMap()
        _completedLessons.value = emptySet()
    }

    private fun loadUserData(uid: String) {
        userDataJob?.cancel()
        userDataJob = viewModelScope.launch {
            launch {
                db.getUserProfile(uid).collect { local ->
                    if (local != null && _userProfile.value == null) {
                        _userProfile.value = User(id = local.id, name = local.name, email = local.email, xp = local.xp, streak = local.streak, level = local.level)
                    }
                }
            }
            launch {
                repository.getUserProfile(uid).collect { remoteUser ->
                    if (remoteUser != null) {
                        _userProfile.value = remoteUser
                        db.insertUserProfile(LocalUserEntity(id = remoteUser.id, name = remoteUser.name, email = remoteUser.email, xp = remoteUser.xp, streak = remoteUser.streak, level = remoteUser.level))
                    } else {
                        auth.currentUser?.let { syncUserToDatabase(it) }
                    }
                }
            }
            launch { repository.getEnrollments(uid).collect { _enrollments.value = it } }
            launch { repository.getUserSaved(uid).collect { _savedVideos.value = it.toSet() } }
            launch { repository.getUserLikes(uid).collect { _likes.value = it.toSet() } }
            launch {
                db.getCompletions(uid).collect { completions ->
                    _completedLessons.value = completions.map { it.lessonId }.toSet()
                }
            }
        }
    }

    suspend fun syncUserToDatabase(firebaseUser: FirebaseUser, customName: String? = null, role: String? = null) {
        val newUser = User(
            id = firebaseUser.uid, 
            name = customName ?: firebaseUser.displayName ?: "Learner", 
            email = firebaseUser.email ?: "", 
            photoUrl = firebaseUser.photoUrl?.toString(),
            role = role ?: _userProfile.value?.role ?: "learner",
            onboardingCompleted = _userProfile.value?.onboardingCompleted ?: false,
            interests = _userProfile.value?.interests ?: emptyList(),
            goals = _userProfile.value?.goals ?: emptyList()
        )
        repository.updateUserProfile(newUser)
        _userProfile.value = newUser
        db.insertUserProfile(LocalUserEntity(id = newUser.id, name = newUser.name, email = newUser.email, xp = 0, streak = 0, level = 1))
    }

    private fun calculateLevel(xp: Int): Int {
        return (xp / 100) + 1
    }

    fun toggleLike(lessonId: String) {
        val uid = auth.currentUser?.uid ?: return
        val currentlyLiked = _likes.value.contains(lessonId)
        viewModelScope.launch {
            if (currentlyLiked) _likes.value -= lessonId else {
                _likes.value += lessonId
                soundManager.playLikeSound()
            }
            repository.toggleLike(uid, lessonId, !currentlyLiked)
            db.insertInteraction(LocalInteractionEntity("${uid}_$lessonId", uid, lessonId, !currentlyLiked))
        }
    }

    fun toggleSave(lessonId: String) {
        val uid = auth.currentUser?.uid ?: return
        val currentlySaved = _savedVideos.value.contains(lessonId)
        viewModelScope.launch {
            if (currentlySaved) {
                _savedVideos.value -= lessonId
                repository.toggleSave(uid, lessonId, false)
                db.deleteSaved(uid, lessonId)
            } else {
                _savedVideos.value += lessonId
                soundManager.playSaveSound()
                repository.toggleSave(uid, lessonId, true)
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
            soundManager.playCommentSound()
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

    fun addReview(courseId: String, rating: Int, comment: String) {
        val user = _userProfile.value ?: return
        val reviewId = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()
        val review = CourseReview(reviewId, courseId, user.id, user.name, rating, comment, timestamp)
        viewModelScope.launch {
            repository.addReview(review)
            loadReviews(courseId)
        }
    }

    fun loadReviews(courseId: String) {
        if (activeReviewJobs.containsKey(courseId)) return
        activeReviewJobs[courseId] = viewModelScope.launch {
            repository.getReviews(courseId).collect { reviews ->
                _reviews.value += (courseId to reviews)
            }
        }
    }

    // --- Professor & Forum Data Loading ---
    fun loadCourseManagementData(courseId: String) {
        viewModelScope.launch {
            repository.getCourseEnrollments(courseId).collect { enrollments ->
                _participants.value = _participants.value + (courseId to enrollments)
            }
        }
        viewModelScope.launch {
            repository.getForumTopics(courseId).collect { topics ->
                _forumTopics.value = _forumTopics.value + (courseId to topics)
            }
        }
    }

    fun loadForumReplies(topicId: String) {
        viewModelScope.launch {
            repository.getForumReplies(topicId).collect { replies ->
                _forumReplies.value = _forumReplies.value + (topicId to replies)
            }
        }
    }

    fun createForumTopic(courseId: String, title: String, content: String) {
        val user = _userProfile.value ?: return
        val topicId = UUID.randomUUID().toString()
        val topic = ForumTopic(topicId, courseId, user.id, user.name, title, content)
        viewModelScope.launch {
            repository.addForumTopic(topic)
            loadCourseManagementData(courseId)
        }
    }

    fun addForumReply(topicId: String, text: String) {
        val user = _userProfile.value ?: return
        val replyId = UUID.randomUUID().toString()
        val reply = ForumReply(replyId, topicId, user.id, user.name, text)
        viewModelScope.launch {
            repository.addForumReply(reply)
            loadForumReplies(topicId)
        }
    }

    fun sendClassNotification(courseId: String, title: String, content: String) {
        val user = _userProfile.value ?: return
        if (user.role != "professor") return
        
        val notification = CourseNotification(UUID.randomUUID().toString(), courseId, title, content)
        viewModelScope.launch {
            repository.addNotification(notification)
        }
    }

    fun completeOnboarding(interests: List<String>, goals: List<String>) {
        val user = _userProfile.value ?: return
        val updated = user.copy(interests = interests, goals = goals, onboardingCompleted = true)
        _userProfile.value = updated
        viewModelScope.launch {
            repository.updateUserProfile(updated)
            db.insertUserProfile(LocalUserEntity(id = updated.id, name = updated.name, email = updated.email, xp = updated.xp, streak = updated.streak, level = updated.level))
        }
    }

    fun getCourseModules(courseId: String): Flow<List<Module>> = repository.getModules(courseId)
    
    fun getModuleLessons(moduleId: String): Flow<List<Lesson>> = repository.getLessons(moduleId)

    fun getLesson(lessonId: String): Flow<Lesson?> = repository.getLesson(lessonId)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getModuleLessonsForReels(courseId: String): Flow<List<Lesson>> {
        return repository.getModules(courseId).flatMapLatest { modules ->
            if (modules.isEmpty()) flowOf(emptyList())
            else {
                val lessonFlows = modules.map { repository.getLessons(it.id) }
                combine(lessonFlows) { arrays ->
                    arrays.flatMap { it }.distinctBy { it.id }.sortedBy { it.orderIndex }
                }
            }
        }
    }

    fun resetEnrollmentState() { _enrollmentState.value = EnrollmentState.Idle }

    fun enrollInCourse(courseId: String, startLessonId: String? = null) {
        val uid = auth.currentUser?.uid ?: return
        val user = _userProfile.value ?: return
        if (isEnrolled(courseId)) {
            _enrollmentState.value = EnrollmentState.Success(courseId)
            return
        }
        
        viewModelScope.launch {
            _enrollmentState.value = EnrollmentState.Loading
            try {
                repository.enrollInCourse(uid, courseId, user.name)
                db.insertEnrollment(LocalEnrollmentEntity("${uid}_$courseId", uid, courseId, "enrolled", 0, startLessonId ?: ""))
                _enrollments.value += Enrollment(userId = uid, courseId = courseId, userName = user.name)
                soundManager.playEnrollSound()
                _enrollmentState.value = EnrollmentState.Success(courseId)
            } catch (e: Exception) {
                _enrollmentState.value = EnrollmentState.Error(e.message ?: "Enrollment failed")
            }
        }
    }

    fun unenrollFromCourse(courseId: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                repository.unenrollFromCourse(uid, courseId)
                db.deleteEnrollment(uid, courseId)
                _enrollments.value = _enrollments.value.filter { it.courseId != courseId }
                _enrollmentState.value = EnrollmentState.Idle
            } catch (e: Exception) {
                Log.e("MainViewModel", "Unenroll failed: ${e.message}")
            }
        }
    }

    fun isEnrolled(courseId: String): Boolean = _enrollments.value.any { it.courseId == courseId }

    fun completeLesson(lessonId: String) {
        val uid = auth.currentUser?.uid ?: return
        val profile = _userProfile.value ?: return
        if (_completedLessons.value.contains(lessonId)) return 

        viewModelScope.launch {
            // Determine courseId and total lessons for progress tracking
            var targetCourseId = ""
            var totalLessonsInCourse = 0
            
            // Check in MockData
            val mockLesson = MockData.lessons.find { it.id == lessonId }
            if (mockLesson != null) {
                targetCourseId = mockLesson.courseId
                totalLessonsInCourse = MockData.lessons.count { it.courseId == targetCourseId }
            } else {
                // Check all loaded courses
                for (course in _courses.value) {
                    val modules = repository.getModules(course.id).first()
                    for (module in modules) {
                        val lessons = repository.getLessons(module.id).first()
                        if (lessons.any { it.id == lessonId }) {
                            targetCourseId = course.id
                            totalLessonsInCourse = 0
                            for (m in modules) {
                                totalLessonsInCourse += repository.getLessons(m.id).first().size
                            }
                            break
                        }
                    }
                    if (targetCourseId.isNotEmpty()) break
                }
            }

            if (targetCourseId.isNotEmpty()) {
                repository.completeLesson(uid, targetCourseId, lessonId, totalLessonsInCourse)
                db.insertCompletion(LocalCompletionEntity(userId = uid, lessonId = lessonId, completedAt = System.currentTimeMillis().toString()))
                
                val newXp = profile.xp + 25
                val newLevel = calculateLevel(newXp)
                val updatedUser = profile.copy(xp = newXp, level = newLevel)
                
                if (newLevel > profile.level) {
                    _levelUpEvent.emit(newLevel)
                }

                _userProfile.value = updatedUser
                repository.updateUserProfile(updatedUser)
                db.insertUserProfile(LocalUserEntity(id = profile.id, name = profile.name, email = profile.email, xp = updatedUser.xp, streak = profile.streak, level = updatedUser.level))
                
                _completedLessons.value += lessonId
            }
        }
    }

    fun createCourse(title: String, description: String, subject: String, level: String, thumbnailUrl: String, modules: List<ModuleData>) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            val courseId = UUID.randomUUID().toString()
            val newCourse = Course(id = courseId, title = title, description = description, subject = subject, level = level, createdByUserId = uid, thumbnailUrl = thumbnailUrl)
            repository.addCourse(newCourse)
            
            modules.forEachIndexed { mIdx, mData ->
                val moduleId = UUID.randomUUID().toString()
                repository.addModule(Module(id = moduleId, courseId = courseId, title = mData.title, orderIndex = mIdx))
                mData.lessons.forEachIndexed { lIdx, lData ->
                    val lessonId = UUID.randomUUID().toString()
                    repository.addLesson(Lesson(id = lessonId, moduleId = moduleId, courseId = courseId, title = lData.title, videoUrl = lData.videoUrl, orderIndex = lIdx, lessonType = "video", type = "reel", hasQuiz = lData.quiz.isNotEmpty()))
                    
                    // Add Quizzes
                    lData.quiz.forEach { q ->
                        val type = if (q.isMultipleChoice) "multiple" else "single"
                        repository.addQuizQuestion(QuizQuestion(UUID.randomUUID().toString(), lessonId, lessonId, q.question, type, q.options, q.correctIndexes))
                    }
                }
            }
            loadCourses()
        }
    }

    fun updateCourse(course: Course) {
        viewModelScope.launch {
            repository.addCourse(course)
            loadCourses()
        }
    }

    fun addModule(courseId: String, title: String, orderIndex: Int) {
        viewModelScope.launch {
            repository.addModule(Module(id = UUID.randomUUID().toString(), courseId = courseId, title = title, orderIndex = orderIndex))
        }
    }

    fun updateModule(module: Module) {
        viewModelScope.launch {
            repository.addModule(module)
        }
    }

    fun deleteModule(moduleId: String) {
        viewModelScope.launch {
            // Note: Repository needs deleteModule implementation
            repository.deleteModule(moduleId)
        }
    }

    fun addLesson(moduleId: String, courseId: String, title: String, videoUrl: String, orderIndex: Int) {
        viewModelScope.launch {
            repository.addLesson(Lesson(id = UUID.randomUUID().toString(), moduleId = moduleId, courseId = courseId, title = title, videoUrl = videoUrl, orderIndex = orderIndex))
        }
    }

    fun updateLesson(lesson: Lesson) {
        viewModelScope.launch {
            repository.addLesson(lesson)
        }
    }

    fun deleteLesson(lessonId: String) {
        viewModelScope.launch {
            repository.deleteLesson(lessonId)
        }
    }

    fun logout() {
        auth.signOut()
    }
}

fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}
