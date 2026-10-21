package org.umn.utstest.viewmodel

import androidx.lifecycle.ViewModel
import org.umn.utstest.data.Course
import org.umn.utstest.data.DummyData
import org.umn.utstest.data.UserProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _progress = MutableStateFlow(DummyData.userProgress)
    val progress: StateFlow<UserProgress> = _progress.asStateFlow()
    fun getCurrentCourse(): Course? = DummyData.courses.firstOrNull { it.id == _progress.value.currentCourseId }
}
