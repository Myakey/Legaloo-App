package org.ambatujudge.legaloo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ambatujudge.legaloo.data.SessionStore
import org.ambatujudge.legaloo.ui.components.LegalooButton
import org.ambatujudge.legaloo.ui.components.LegalooCard

@Composable
fun ProfileScreen(onLogout: () -> Unit, modifier: Modifier = Modifier) {
    val name = remember { SessionStore.userName }
    val email = remember { SessionStore.userEmail }
    var confirming by remember { mutableStateOf(false) }

    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 28.dp)) {
        Spacer(Modifier.height(16.dp))
        Text("Profil", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(24.dp))
        LegalooCard {
            Text("AKUN", style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.5.sp), color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(name ?: "-", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (email != null) {
                Text(email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Spacer(Modifier.height(24.dp))
        LegalooButton(
            text = "KELUAR",
            onClick = { confirming = true },
            containerColor = MaterialTheme.colorScheme.surface,
            edgeColor = MaterialTheme.colorScheme.outline,
            contentColor = MaterialTheme.colorScheme.error,
            borderColor = MaterialTheme.colorScheme.outline,
            fontSize = 17.sp,
        )
    }

    if (confirming) {
        AlertDialog(
            onDismissRequest = { confirming = false },
            title = { Text("Keluar dari akun?") },
            text = { Text("Kamu perlu masuk lagi untuk melanjutkan belajar.") },
            confirmButton = {
                TextButton(onClick = { confirming = false; SessionStore.clear(); onLogout() }) {
                    Text("Keluar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { confirming = false }) { Text("Batal") } },
        )
    }
}
