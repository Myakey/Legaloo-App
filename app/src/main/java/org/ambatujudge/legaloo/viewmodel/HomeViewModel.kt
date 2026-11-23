package org.ambatujudge.legaloo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.ambatujudge.legaloo.data.Course
import org.ambatujudge.legaloo.data.LegalooRepository
import org.ambatujudge.legaloo.data.UserProgress
import org.ambatujudge.legaloo.data.remote.ApiException

class HomeViewModel : ViewModel() {
    val progress: StateFlow<UserProgress?> = LegalooRepository.progress
    val courses: StateFlow<List<Course>> = LegalooRepository.courses

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun refresh() {
        if (_isLoading.value) return
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                coroutineScope {
                    val p = async { LegalooRepository.refreshProgress() }
                    val c = async { LegalooRepository.refreshCourses() }
                    p.await(); c.await()
                }
            } catch (e: ApiException) {
                _error.value = e.detail
            } finally {
                _isLoading.value = false
            }
        }
    }
}
