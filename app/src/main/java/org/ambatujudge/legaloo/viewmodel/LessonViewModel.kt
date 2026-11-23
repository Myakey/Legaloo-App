package org.ambatujudge.legaloo.viewmodel

import android.os.SystemClock
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ambatujudge.legaloo.data.LegalooRepository
import org.ambatujudge.legaloo.data.Lesson
import org.ambatujudge.legaloo.data.StepContent
import org.ambatujudge.legaloo.data.remote.ApiException
import org.ambatujudge.legaloo.navigation.Routes
import kotlin.math.roundToInt

data class LessonUiState(
    val lesson: Lesson? = null,
    val loadError: String? = null,
    val step: Int = 0,
    /** Selected option per quiz step index. */
    val answers: Map<Int, Int> = emptyMap(),
    /** Quiz step indexes whose answer has been checked. */
    val checked: Set<Int> = emptySet(),
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val finished: Boolean = false,
) {
    val totalSteps: Int get() = lesson?.steps?.size ?: 0
    val quiz: StepContent.Quiz? get() = lesson?.steps?.getOrNull(step)?.content as? StepContent.Quiz
    val selected: Int get() = answers[step] ?: -1
    val isChecked: Boolean get() = step in checked
    val isLastStep: Boolean get() = step == totalSteps - 1
    val selectedIsCorrect: Boolean get() = quiz?.options?.getOrNull(selected)?.correct == true

    /** True when every quiz in the level was answered correctly. */
    val allCorrect: Boolean get() = lesson?.steps.orEmpty().withIndex().all { (i, s) ->
        val q = s.content as? StepContent.Quiz ?: return@all true
        q.options.getOrNull(answers[i] ?: -1)?.correct == true
    }
}

class LessonViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    val courseId: Int = checkNotNull(savedStateHandle[Routes.COURSE_ID_ARG])
    val levelId: Int = checkNotNull(savedStateHandle[Routes.LEVEL_ID_ARG])

    private val _state = MutableStateFlow(LessonUiState())
    val state: StateFlow<LessonUiState> = _state.asStateFlow()

    private var startedAt = 0L

    init { load() }

    fun load() {
        _state.update { it.copy(loadError = null) }
        viewModelScope.launch {
            try {
                val lesson = LegalooRepository.getLesson(courseId, levelId)
                startedAt = SystemClock.elapsedRealtime()
                _state.update { LessonUiState(lesson = lesson) }
            } catch (e: ApiException) {
                _state.update { it.copy(loadError = e.detail) }
            }
        }
    }

    fun select(option: Int) = _state.update { if (it.isChecked) it else it.copy(answers = it.answers + (it.step to option)) }

    /** Returns false when already on the first step (the caller should close the lesson). */
    fun back(): Boolean {
        val s = _state.value
        if (s.step == 0) return false
        _state.update { it.copy(step = it.step - 1, checked = it.checked - it.step, submitError = null) }
        return true
    }

    /** The primary button: LANJUT, PERIKSA or SELESAI. */
    fun primary() {
        val s = _state.value
        when {
            s.quiz != null && !s.isChecked -> if (s.selected >= 0) _state.update { it.copy(checked = it.checked + it.step) }
            !s.isLastStep -> _state.update { it.copy(step = it.step + 1) }
            else -> finish()
        }
    }

    private fun finish() {
        val s = _state.value
        if (s.isSubmitting) return
        val minutes = ((SystemClock.elapsedRealtime() - startedAt) / 60_000.0).roundToInt().coerceIn(1, 60)
        _state.update { it.copy(isSubmitting = true, submitError = null) }
        viewModelScope.launch {
            try {
                LegalooRepository.completeLevel(courseId, levelId, s.allCorrect, minutes)
                _state.update { it.copy(isSubmitting = false, finished = true) }
            } catch (e: ApiException) {
                _state.update { it.copy(isSubmitting = false, submitError = e.detail) }
            }
        }
    }
}
