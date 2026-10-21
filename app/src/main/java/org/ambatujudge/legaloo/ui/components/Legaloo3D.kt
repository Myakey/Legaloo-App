package org.ambatujudge.legaloo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ambatujudge.legaloo.ui.theme.LocalExtraColors

@Composable
fun LegalooCard(modifier: Modifier = Modifier, containerColor: Color = MaterialTheme.colorScheme.surface, borderColor: Color = MaterialTheme.colorScheme.outline, edgeColor: Color = MaterialTheme.colorScheme.outline, content: @Composable ColumnScope.() -> Unit) {
    Box(modifier = modifier.fillMaxWidth().background(edgeColor, MaterialTheme.shapes.large)) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp).background(containerColor, MaterialTheme.shapes.large).border(2.dp, borderColor, MaterialTheme.shapes.large).padding(horizontal = 16.dp, vertical = 14.dp), content = content)
    }
}

@Composable
fun LegalooButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, containerColor: Color = MaterialTheme.colorScheme.secondary, edgeColor: Color = LocalExtraColors.current.secondaryEdge, contentColor: Color = MaterialTheme.colorScheme.onSecondary) {
    val press = rememberPressState(maxDepth = 4.dp)
    Box(modifier = modifier.fillMaxWidth().clip(MaterialTheme.shapes.large).background(edgeColor).pressClickable(press, onClick)) {
        Box(modifier = Modifier.pressOffset(press).fillMaxWidth().padding(bottom = 4.dp).height(56.dp).background(containerColor, MaterialTheme.shapes.large), contentAlignment = Alignment.Center) {
            Text(text = text, style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp, letterSpacing = 1.5.sp), color = contentColor)
        }
    }
}
