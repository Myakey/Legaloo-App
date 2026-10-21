package org.ambatujudge.legaloo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ambatujudge.legaloo.ui.components.LegalooButton
import org.ambatujudge.legaloo.ui.theme.LegalooTheme

@Composable
fun WelcomeScreen(onStartClick: () -> Unit, onLoginClick: () -> Unit, modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    Column(modifier.fillMaxSize().background(colorScheme.background).padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.weight(1f))
        LegalooLogo()
        Spacer(Modifier.height(14.dp))
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(color = colorScheme.primary)) { append("Lega") }
                withStyle(SpanStyle(color = colorScheme.secondary)) { append("loo") }
            },
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 46.sp, lineHeight = 52.sp)
        )
        Spacer(Modifier.height(6.dp))
        Text("BELAJAR HUKUM, SERU!", style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp, letterSpacing = 3.sp), color = colorScheme.primary)
        Spacer(Modifier.weight(1f))
        LegalooButton(text = "MULAI SEKARANG", onClick = onStartClick, fontSize = 17.sp)
        Spacer(Modifier.height(12.dp))
        LegalooButton(text = "SAYA SUDAH PUNYA AKUN", onClick = onLoginClick, containerColor = colorScheme.surface, edgeColor = colorScheme.outline, contentColor = colorScheme.primary, borderColor = colorScheme.outline, fontSize = 17.sp)
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun LegalooLogo(modifier: Modifier = Modifier) {
    Box(modifier.size(104.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(22.dp)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.Star, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(30.dp))
            Icon(Icons.Filled.Balance, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(60.dp).offset(y = (-6).dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 395, heightDp = 850)
@Composable
private fun WelcomeScreenPreview() { LegalooTheme { WelcomeScreen(onStartClick = {}, onLoginClick = {}) } }
