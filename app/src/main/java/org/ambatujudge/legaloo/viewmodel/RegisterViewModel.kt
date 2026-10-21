package org.ambatujudge.legaloo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ambatujudge.legaloo.data.AuthRepository
import org.ambatujudge.legaloo.data.UserAccount

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val registeredName: String? = null
) {
    val hasErrors: Boolean get() = nameError != null || emailError != null || passwordError != null
}

class RegisterViewModel : ViewModel() {
    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    fun onNameChange(value: String) = _state.update { it.copy(name = value, nameError = AuthValidator.name(value)) }
    fun onEmailChange(value: String) = _state.update { it.copy(email = value, emailError = AuthValidator.email(value)) }
    fun onPasswordChange(value: String) = _state.update { it.copy(password = value, passwordError = AuthValidator.newPassword(value)) }

    fun submit() {
        val current = _state.value
        if (current.isLoading) return
        val emailError = AuthValidator.email(current.email) ?: if (AuthRepository.findByEmail(current.email) != null) "Email sudah terdaftar" else null
        val checked = current.copy(nameError = AuthValidator.name(current.name), emailError = emailError, passwordError = AuthValidator.newPassword(current.password))
        if (checked.hasErrors) { _state.value = checked; return }
        _state.value = checked.copy(isLoading = true)
        viewModelScope.launch {
            delay(800)
            val name = current.name.trim()
            AuthRepository.register(UserAccount(name, current.email.trim(), current.password))
            _state.update { it.copy(isLoading = false, registeredName = name) }
        }
    }
}
