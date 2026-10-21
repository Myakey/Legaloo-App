package org.ambatujudge.legaloo.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ambatujudge.legaloo.ui.components.AuthLayout
import org.ambatujudge.legaloo.ui.components.AuthSwitchLink
import org.ambatujudge.legaloo.ui.components.LegalooButton
import org.ambatujudge.legaloo.ui.components.LegalooTextField
import org.ambatujudge.legaloo.ui.theme.LegalooTheme
import org.ambatujudge.legaloo.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(viewModel: RegisterViewModel, onRegistered: (String) -> Unit, onLoginClick: () -> Unit, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current
    val submit = { focusManager.clearFocus(); viewModel.submit() }
    LaunchedEffect(state.registeredName) { state.registeredName?.let(onRegistered) }
    AuthLayout(modifier = modifier, bottom = {
        LegalooButton(text = "DAFTAR", onClick = submit, fontSize = 17.sp, loading = state.isLoading)
        AuthSwitchLink(prompt = "Sudah punya akun?", action = "Masuk", onClick = onLoginClick)
    }) {
        Text("Buat akunmu", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(20.dp))
        LegalooTextField(label = "Nama", value = state.name, onValueChange = viewModel::onNameChange, placeholder = "Nama lengkap", error = state.nameError, enabled = !state.isLoading,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text, imeAction = ImeAction.Next))
        Spacer(Modifier.height(12.dp))
        LegalooTextField(label = "Email", value = state.email, onValueChange = viewModel::onEmailChange, placeholder = "nama@email.com", error = state.emailError, enabled = !state.isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next))
        Spacer(Modifier.height(12.dp))
        LegalooTextField(label = "Kata sandi", value = state.password, onValueChange = viewModel::onPasswordChange, placeholder = "password", error = state.passwordError, enabled = !state.isLoading, isPassword = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { submit() }))
    }
}

@Preview(showBackground = true, widthDp = 395, heightDp = 850)
@Composable
private fun RegisterScreenPreview() { LegalooTheme { RegisterScreen(viewModel = RegisterViewModel(), onRegistered = {}, onLoginClick = {}) } }
