package com.skilltok.app

import com.google.firebase.firestore.PropertyName

data class User(
    val id: String = "",
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val role: String = "learner", // "learner", "professor"
    val xp: Int = 0,
    val streak: Int = 0,
    val level: Int = 1,
    val photoUrl: String? = null,
    val bio: String? = null,
    val interests: List<String> = emptyList(),
    val goals: List<String> = emptyList(),
    val onboardingCompleted: Boolean = false,
    val savedVideosCount: Int = 0
)

data class Course(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val subject: String = "",
    val level: String = "Beginner",
    val thumbnailUrl: String = "",
    val rating: Double = 4.5,
    val learnersCount: Int = 0,
    @get:PropertyName("isPublished") @set:PropertyName("isPublished")
    var isPublished: Boolean = false,
    val source: String = "user", // "user", "ai"
    val generationStatus: String? = null,
    val originalPrompt: String? = null,
    val createdAt: String = "",
    val createdByUserId: String = ""
)

data class Module(
    val id: String = "",
    val courseId: String = "",
    val title: String = "",
    val description: String = "",
    val orderIndex: Int = 0
)

data class Lesson(
    val id: String = "",
    val moduleId: String = "",
    val courseId: String = "",
    val title: String = "",
    val description: String = "",
    val videoUrl: String = "",
    val lessonType: String = "video", // "video", "quiz", "article"
    val durationSeconds: Int = 0,
    val orderIndex: Int = 0,
    val type: String = "reel", // "reel", "deep_dive"
    val hasQuiz: Boolean = false,
    val contentUrl: String = "",
    val resourceUrl: String? = null,
    val quizData: String? = null,
    val likeCount: Int = 0
)

data class QuizQuestion(
    val id: String = "",
    val referenceId: String = "", // lessonId, moduleId or courseId
    val lessonId: String = "",
    val questionText: String = "",
    val type: String = "single",
    val options: List<String> = emptyList(),
    val correctAnswerIndexes: List<Int> = emptyList(),
    val explanation: String = ""
)

data class QuizResult(
    val id: String = "",
    val lessonId: String = "",
    val userId: String = "",
    val userName: String = "",
    val score: Int = 0,
    val total: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

data class ForumTopic(
    val id: String = "",
    val courseId: String = "",
    val userId: String = "",
    val userName: String = "",
    val title: String = "",
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class ForumReply(
    val id: String = "",
    val topicId: String = "",
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class CourseNotification(
    val id: String = "",
    val courseId: String = "",
    val title: String = "",
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val forumTopicId: String? = null // Link to discussion
)

data class ChatMessage(
    val id: String = "",
    val courseId: String = "",
    val userId: String = "",
    val userName: String = "",
    val encryptedText: String = "", // Industry standard: encryption
    val createdAt: Long = System.currentTimeMillis(),
    val userRole: String = "learner"
)

data class ReelComment(
    val id: String = "",
    val lessonId: String = "",
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class ReelLike(
    val userId: String = "",
    val lessonId: String = ""
)

data class CourseReview(
    val id: String = "",
    val courseId: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Int = 5,
    val comment: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class Enrollment(
    val id: String = "",
    val userId: String = "",
    val userName: String = "", // Added for real tracking
    val courseId: String = "",
    val status: String = "in_progress",
    val progressPercent: Int = 0,
    val currentLessonId: String = "",
    val enrolledAt: String = "",
    val startedAt: String = ""
)
