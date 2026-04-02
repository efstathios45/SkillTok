package com.skilltok.app

data class User(
    val id: String = "",
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val role: String = "learner", // "learner", "instructor"
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
    val isPublished: Boolean = false,
    val source: String = "user", // "user" or "ai"
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
    val type: String = "reel", // "reel" or "deep_dive"
    val hasQuiz: Boolean = false,
    val contentUrl: String = "",
    val resourceUrl: String? = null,
    val quizData: String? = null
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
    val courseId: String = "",
    val status: String = "in_progress",
    val progressPercent: Int = 0,
    val currentLessonId: String = "",
    val enrolledAt: String = "",
    val startedAt: String = ""
)
