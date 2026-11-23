package org.ambatujudge.legaloo.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import org.ambatujudge.legaloo.data.remote.ApiClient

/**
 * Courses and progress for the logged-in user, shared by every screen.
 * The server computes level states, so this is refreshed on screen entry rather than cached.
 * All suspend functions throw [org.ambatujudge.legaloo.data.remote.ApiException] on failure.
 */
object LegalooRepository {
    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    private val _progress = MutableStateFlow<UserProgress?>(null)
    val progress: StateFlow<UserProgress?> = _progress.asStateFlow()

    suspend fun refreshCourses() {
        _courses.value = ApiClient.get<List<Course>>("/courses")
    }

    suspend fun refreshProgress() {
        _progress.value = ApiClient.get<UserProgress>("/me/progress")
    }

    suspend fun getLesson(courseId: Int, levelId: Int): Lesson =
        ApiClient.get<LessonDto>("/courses/$courseId/levels/$levelId/lesson").toLesson()

    /** Reports a finished level and updates the course map and home screen from the response. */
    suspend fun completeLevel(courseId: Int, levelId: Int, correct: Boolean, minutesSpent: Int) {
        val result = ApiClient.post<CompleteRequest, CompleteResponse>(
            "/courses/$courseId/levels/$levelId/complete",
            CompleteRequest(correct, minutesSpent.coerceIn(0, 60)),
        )
        _courses.update { list ->
            if (list.none { it.id == result.course.id }) list + result.course
            else list.map { if (it.id == result.course.id) result.course else it }
        }
        _progress.value = result.progress
    }

    fun clear() {
        _courses.value = emptyList()
        _progress.value = null
    }
}

@Serializable
internal data class LessonStepDto(
    val position: Int,
    val kind: String,
    val modeLabel: String = "",
    val title: String? = null,
    val mascotMessage: String = "",
    val content: JsonObject = JsonObject(emptyMap()),
)

@Serializable
internal data class LessonDto(val courseId: Int, val levelId: Int, val type: LevelType, val title: String, val steps: List<LessonStepDto>) {
    fun toLesson() = Lesson(
        courseId = courseId,
        levelId = levelId,
        type = type,
        title = title,
        steps = steps.sortedBy { it.position }.mapNotNull { step ->
            val content: StepContent = when (step.kind) {
                "READ" -> ApiClient.json.decodeFromJsonElement(StepContent.Read.serializer(), step.content)
                "EXAMPLES" -> ApiClient.json.decodeFromJsonElement(StepContent.Examples.serializer(), step.content)
                "QUIZ" -> ApiClient.json.decodeFromJsonElement(StepContent.Quiz.serializer(), step.content)
                else -> return@mapNotNull null
            }
            LessonStep(step.modeLabel, step.title, step.mascotMessage, content)
        },
    )
}
