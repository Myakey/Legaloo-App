package org.ambatujudge.legaloo.viewmodel

import androidx.lifecycle.ViewModel
import org.ambatujudge.legaloo.data.Course
import org.ambatujudge.legaloo.data.DummyData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CourseViewModel : ViewModel() {
    private val _courses = MutableStateFlow(DummyData.courses)
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()
    fun getCourseById(id: Int): Course? = _courses.value.firstOrNull { it.id == id }
}
