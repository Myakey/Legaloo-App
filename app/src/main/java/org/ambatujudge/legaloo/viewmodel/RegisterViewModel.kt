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

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val formError: String? = null,
    val isLoading: Boolean = false,
    val registeredName: String? = null
) {
    val hasErrors: Boolean get() = nameError != null || emailError != null || passwordError != null
}

class RegisterViewModel : ViewModel() {
    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    fun onNameChange(value: String) = _state.update { it.copy(name = value, nameError = AuthValidator.name(value), formError = null) }
    fun onEmailChange(value: String) = _state.update { it.copy(email = value, emailError = AuthValidator.email(value), formError = null) }
    fun onPasswordChange(value: String) = _state.update { it.copy(password = value, passwordError = AuthValidator.newPassword(value), formError = null) }

    fun submit() {
        val current = _state.value
        if (current.isLoading) return
        val checked = current.copy(nameError = AuthValidator.name(current.name), emailError = AuthValidator.email(current.email), passwordError = AuthValidator.newPassword(current.password))
        if (checked.hasErrors) { _state.value = checked; return }
        _state.value = checked.copy(isLoading = true, formError = null)
        viewModelScope.launch {
            try {
                val user = AuthRepository.register(current.name.trim(), current.email.trim(), current.password)
                _state.update { it.copy(isLoading = false, registeredName = user.name) }
            } catch (e: ApiException) {
                // Server validation and "Email sudah terdaftar" (409) come back with the matching field.
                _state.update {
                    when (e.field) {
                        "name" -> it.copy(isLoading = false, nameError = e.detail)
                        "email" -> it.copy(isLoading = false, emailError = e.detail)
                        "password" -> it.copy(isLoading = false, passwordError = e.detail)
                        else -> it.copy(isLoading = false, formError = e.detail)
                    }
                }
            }
        }
    }
}
