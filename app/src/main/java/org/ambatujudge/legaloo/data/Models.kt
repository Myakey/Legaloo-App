package org.ambatujudge.legaloo.data

import kotlinx.serialization.Serializable

@Serializable enum class LevelState { COMPLETED, ACTIVE, LOCKED }
@Serializable enum class LevelType { LESSON, PRACTICE }

@Serializable
data class LevelNode(val id: Int, val type: LevelType, val state: LevelState, val title: String = "")

@Serializable
data class Course(val id: Int, val number: Int, val title: String, val progress: Float, val unitLabel: String, val isCurrent: Boolean, val levels: List<LevelNode>)

@Serializable
data class StudyDay(val label: String, val completed: Boolean)

@Serializable
data class UserProgress(val userName: String, val streakCount: Int, val minutesStudied: Int, val dailyGoalMinutes: Int, val week: List<StudyDay>, val currentCourseId: Int, val currentUnitNumber: Int)

// ---- Auth ----

@Serializable data class RegisterRequest(val name: String, val email: String, val password: String)
@Serializable data class LoginRequest(val email: String, val password: String)
@Serializable data class User(val id: String, val name: String, val email: String)
@Serializable data class AuthResponse(val token: String, val user: User)

// ---- Level completion ----

@Serializable data class CompleteRequest(val correct: Boolean, val minutesSpent: Int)
@Serializable data class CompleteResponse(val course: Course, val progress: UserProgress)

// ---- Lesson ----

/** A lesson with its steps already parsed by `kind`. Steps of an unknown kind are dropped. */
data class Lesson(val courseId: Int, val levelId: Int, val type: LevelType, val title: String, val steps: List<LessonStep>)

data class LessonStep(val modeLabel: String, val title: String?, val mascotMessage: String, val content: StepContent)

sealed interface StepContent {
    @Serializable data class Read(val paragraphs: List<String> = emptyList()) : StepContent
    @Serializable data class Examples(val examples: List<Example> = emptyList()) : StepContent
    @Serializable data class Quiz(
        val question: String,
        val options: List<QuizOption>,
        val feedbackCorrect: String = "",
        val feedbackWrong: String = "",
    ) : StepContent
}

@Serializable data class Example(val label: String, val tone: String, val caption: String, val illustration: String? = null)
@Serializable data class QuizOption(val text: String, val correct: Boolean)
