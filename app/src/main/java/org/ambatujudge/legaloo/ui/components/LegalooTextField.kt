package org.ambatujudge.legaloo.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.ambatujudge.legaloo.ui.theme.LocalExtraColors

@Composable
fun LegalooTextField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String, modifier: Modifier = Modifier, error: String? = null, isPassword: Boolean = false, enabled: Boolean = true, keyboardOptions: KeyboardOptions = KeyboardOptions.Default, keyboardActions: KeyboardActions = KeyboardActions.Default) {
    val colorScheme = MaterialTheme.colorScheme
    val fill = LocalExtraColors.current.fieldFill
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    Column(modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = colorScheme.primary)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(), enabled = enabled, singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyLarge) },
            isError = error != null,
            supportingText = error?.let { { Text(it, style = MaterialTheme.typography.labelSmall) } },
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, contentDescription = if (passwordVisible) "Sembunyikan kata sandi" else "Tampilkan kata sandi")
                    }
                }
            } else null,
            keyboardOptions = keyboardOptions, keyboardActions = keyboardActions,
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = fill, unfocusedContainerColor = fill, errorContainerColor = fill, disabledContainerColor = fill,
                focusedBorderColor = colorScheme.primary, unfocusedBorderColor = colorScheme.outline,
                focusedTextColor = colorScheme.onSurface, unfocusedTextColor = colorScheme.onSurface,
                focusedPlaceholderColor = colorScheme.onSurfaceVariant, unfocusedPlaceholderColor = colorScheme.onSurfaceVariant,
                focusedTrailingIconColor = colorScheme.primary, unfocusedTrailingIconColor = colorScheme.onSurfaceVariant
            )
        )
    }
}
