package org.ambatujudge.legaloo.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.ambatujudge.legaloo.ui.components.AuthLayout
import org.ambatujudge.legaloo.ui.components.AuthSwitchLink
import org.ambatujudge.legaloo.ui.components.LegalooButton
import org.ambatujudge.legaloo.ui.components.LegalooTextField
import org.ambatujudge.legaloo.ui.components.TextLink
import org.ambatujudge.legaloo.ui.theme.LegalooTheme
import org.ambatujudge.legaloo.viewmodel.LoginViewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel, onLoggedIn: (String) -> Unit, onRegisterClick: () -> Unit, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val submit = { focusManager.clearFocus(); viewModel.submit() }
    LaunchedEffect(state.loggedInName) { state.loggedInName?.let(onLoggedIn) }
    Box(modifier.fillMaxSize()) {
        AuthLayout(bottom = {
            LegalooButton(text = "MASUK", onClick = submit, fontSize = 17.sp, loading = state.isLoading)
            AuthSwitchLink(prompt = "Belum punya akun?", action = "Daftar", onClick = onRegisterClick)
        }) {
            Text("Selamat datang kembali!", style = MaterialTheme.typography.headlineMedium.copy(fontSize = 28.sp), color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(10.dp))
            Text("Masuk untuk lanjut belajar hukum.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(20.dp))
            LegalooTextField(label = "Email", value = state.email, onValueChange = viewModel::onEmailChange, placeholder = "nama@email.com", error = state.emailError, enabled = !state.isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next))
            Spacer(Modifier.height(12.dp))
            LegalooTextField(label = "Kata sandi", value = state.password, onValueChange = viewModel::onPasswordChange, placeholder = "password", error = state.passwordError, enabled = !state.isLoading, isPassword = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { submit() }))
            state.formError?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(16.dp))
            TextLink("Lupa kata sandi?", onClick = {
                scope.launch { snackbarHostState.currentSnackbarData?.dismiss(); snackbarHostState.showSnackbar("Fitur atur ulang kata sandi segera hadir") }
            })
        }
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.TopCenter).padding(16.dp)) { data ->
            Snackbar(shape = MaterialTheme.shapes.large, containerColor = MaterialTheme.colorScheme.onSurface, contentColor = MaterialTheme.colorScheme.surface) {
                Text(text = data.visuals.message, style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 395, heightDp = 850)
@Composable
private fun LoginScreenPreview() { LegalooTheme { LoginScreen(viewModel = LoginViewModel(), onLoggedIn = {}, onRegisterClick = {}) } }
