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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
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
fun LegalooButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, containerColor: Color = MaterialTheme.colorScheme.secondary, edgeColor: Color = LocalExtraColors.current.secondaryEdge, contentColor: Color = MaterialTheme.colorScheme.onSecondary, borderColor: Color? = null, fontSize: TextUnit = 20.sp, loading: Boolean = false) {
    val press = rememberPressState(maxDepth = 4.dp)
    val shape = MaterialTheme.shapes.large
    Box(modifier = modifier.fillMaxWidth().clip(shape).background(edgeColor).pressClickable(press) { if (!loading) onClick() }) {
        Box(modifier = Modifier.pressOffset(press).fillMaxWidth().padding(bottom = 4.dp).height(56.dp).background(containerColor, shape).then(if (borderColor != null) Modifier.border(2.dp, borderColor, shape) else Modifier), contentAlignment = Alignment.Center) {
            if (loading) CircularProgressIndicator(modifier = Modifier.size(26.dp), color = contentColor, strokeWidth = 3.dp)
            else Text(text = text, style = MaterialTheme.typography.titleMedium.copy(fontSize = fontSize, letterSpacing = 1.5.sp), color = contentColor)
        }
    }
}
