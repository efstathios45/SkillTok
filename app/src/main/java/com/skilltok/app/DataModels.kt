package com.skilltok.app

import com.google.firebase.firestore.PropertyName

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "learner", // "learner", "instructor", "admin"
    val avatarUrl: String = "",
    val interests: List<String> = emptyList(),
    val goal: String = "",
    val createdAt: String = "",
    val streak: Int = 0,
    val xp: Int = 0,
    val level: Int = 1,
    val totalMinutes: Int = 0
)

data class Course(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val subject: String = "",
    val level: String = "Beginner", // "Beginner", "Intermediate", "Advanced"
    val thumbnailUrl: String = "",
    val totalDurationMinutes: Int = 0,
    val createdByUserId: String = "",
    val hasCertificate: Boolean = false,
    val createdAt: String = "",
    val rating: Double = 0.0,
    val learnersCount: Int = 0
)

data class Module(
    val id: String = "",
    val courseId: String = "",
    val title: String = "",
    val orderIndex: Int = 0
)

data class Lesson(
    val id: String = "",
    val moduleId: String = "",
    val courseId: String = "",
    val title: String = "",
    val description: String = "",
    val videoUrl: String = "",
    val durationSeconds: Int = 0,
    val orderIndex: Int = 0,
    val hasQuiz: Boolean = false
)

data class QuizQuestion(
    val id: String = "",
    val lessonId: String = "",
    val questionText: String = "",
    val type: String = "single", // "single", "multi", "truefalse"
    val options: List<String> = emptyList(),
    val correctAnswerIndexes: List<Int> = emptyList(),
    val explanation: String = ""
)

data class Enrollment(
    val id: String = "",
    val userId: String = "",
    val courseId: String = "",
    val status: String = "not_started", // "not_started", "in_progress", "completed"
    val progressPercent: Int = 0,
    val currentLessonId: String = "",
    val startedAt: String = "",
    val completedAt: String? = null
)

data class LessonCompletion(
    val id: String = "",
    val userId: String = "",
    val lessonId: String = "",
    val completedAt: String = "",
    val quizScore: Int? = null
)

data class ReelComment(
    val id: String = "",
    val lessonId: String = "",
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val createdAt: String = "",
    val likes: Int = 0,
    val likedBy: List<String> = emptyList()
)

data class ReelCommentReply(
    val id: String = "",
    val commentId: String = "",
    val lessonId: String = "",
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val createdAt: String = ""
)

data class CourseReview(
    val id: String = "",
    val courseId: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Int = 5,
    val text: String = "",
    val createdAt: String = "",
    val moduleId: String? = null,
    val moduleTitle: String? = null
)
