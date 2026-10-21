package org.ambatujudge.legaloo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun AuthLayout(modifier: Modifier = Modifier, bottom: @Composable ColumnScope.() -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).imePadding().padding(horizontal = 24.dp)) {
        Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(top = 24.dp), content = content)
        Column(Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 16.dp), horizontalAlignment = Alignment.CenterHorizontally, content = bottom)
    }
}

@Composable
fun TextLink(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Text(text, modifier = modifier.clickable(onClick = onClick), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold, textDecoration = TextDecoration.Underline), color = MaterialTheme.colorScheme.primary)
}

@Composable
fun AuthSwitchLink(prompt: String, action: String, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 12.dp)) {
        Text("$prompt ", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
        TextLink(action, onClick)
    }
}
