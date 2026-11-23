package org.ambatujudge.legaloo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ambatujudge.legaloo.data.AuthRepository
import org.ambatujudge.legaloo.data.remote.ApiException

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val formError: String? = null,
    val isLoading: Boolean = false,
    val loggedInName: String? = null
)

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEmailChange(value: String) = _state.update { it.copy(email = value, emailError = AuthValidator.email(value), formError = null) }
    fun onPasswordChange(value: String) = _state.update { it.copy(password = value, passwordError = AuthValidator.password(value), formError = null) }

    fun submit() {
        val current = _state.value
        if (current.isLoading) return
        val checked = current.copy(emailError = AuthValidator.email(current.email), passwordError = AuthValidator.password(current.password))
        if (checked.emailError != null || checked.passwordError != null) { _state.value = checked; return }
        _state.value = checked.copy(isLoading = true, formError = null)
        viewModelScope.launch {
            try {
                val user = AuthRepository.login(current.email.trim(), current.password)
                _state.update { it.copy(isLoading = false, loggedInName = user.name) }
            } catch (e: ApiException) {
                _state.update {
                    when (e.field) {
                        "email" -> it.copy(isLoading = false, emailError = e.detail)
                        "password" -> it.copy(isLoading = false, passwordError = e.detail)
                        else -> it.copy(isLoading = false, formError = e.detail)
                    }
                }
            }
        }
    }
}
