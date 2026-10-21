package org.ambatujudge.legaloo.viewmodel

import androidx.lifecycle.ViewModel
import org.ambatujudge.legaloo.data.Course
import org.ambatujudge.legaloo.data.DummyData
import org.ambatujudge.legaloo.data.UserProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _progress = MutableStateFlow(DummyData.userProgress)
    val progress: StateFlow<UserProgress> = _progress.asStateFlow()
    fun getCurrentCourse(): Course? = DummyData.courses.firstOrNull { it.id == _progress.value.currentCourseId }
}
