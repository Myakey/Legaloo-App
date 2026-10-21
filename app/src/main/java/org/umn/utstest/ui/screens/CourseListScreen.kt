package org.umn.utstest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.umn.utstest.data.Course
import org.umn.utstest.ui.theme.LocalExtraColors
import org.umn.utstest.ui.components.pressClickable
import org.umn.utstest.ui.components.pressOffset
import org.umn.utstest.ui.components.rememberAnimatedProgress
import org.umn.utstest.ui.components.rememberPressState
import org.umn.utstest.viewmodel.CourseViewModel

@Composable
fun CourseListScreen(viewModel: CourseViewModel, onCourseClick: (Int) -> Unit, modifier: Modifier = Modifier) {
    val courses by viewModel.courses.collectAsState()
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 28.dp)) {
        Spacer(Modifier.height(16.dp))
        Text("Kursus", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(24.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
            items(courses, key = { it.id }) { course ->
                CourseCard(course = course, onClick = { onCourseClick(course.id) })
            }
        }
    }
}

@Composable
private fun CourseCard(course: Course, onClick: () -> Unit) {
    val press = rememberPressState(maxDepth = 4.dp)
    val colorScheme = MaterialTheme.colorScheme
    val shapes = MaterialTheme.shapes
    Box(Modifier.fillMaxWidth().background(colorScheme.outline, shapes.large).clip(shapes.large).pressClickable(press, onClick)) {
        Row(Modifier.pressOffset(press).fillMaxWidth().padding(bottom = 4.dp).background(colorScheme.surface, shapes.large).border(2.dp, colorScheme.outline, shapes.large).padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            NumberBadge(course)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(course.title, style = MaterialTheme.typography.titleMedium, color = colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(8.dp))
                CourseProgressBar(progress = course.progress)
            }
        }
    }
}

@Composable
private fun NumberBadge(course: Course) {
    val colorScheme = MaterialTheme.colorScheme
    val shapes = MaterialTheme.shapes
    val extra = LocalExtraColors.current
    val (face, edge, numColor) = if (!course.isCurrent) Triple(colorScheme.primary, extra.primaryEdge, colorScheme.onPrimary) else Triple(colorScheme.secondary, extra.secondaryEdge, colorScheme.onSecondary)
    Box(Modifier.width(56.dp).height(60.dp)) {
        Box(Modifier.fillMaxSize().background(edge, shapes.medium))
        Box(Modifier.fillMaxWidth().height(56.dp).background(face, shapes.medium), contentAlignment = Alignment.Center) {
            Text(course.number.toString(), style = MaterialTheme.typography.headlineSmall, color = numColor)
        }
    }
}

@Composable
private fun CourseProgressBar(progress: Float, modifier: Modifier = Modifier) {
    val animated = rememberAnimatedProgress(progress)
    Box(modifier = modifier.fillMaxWidth().height(12.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)) {
        Box(Modifier.fillMaxWidth(animated).fillMaxHeight().background(MaterialTheme.colorScheme.primary, CircleShape))
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 395, heightDp = 850)
@Composable
private fun CourseListPreview() {
    org.umn.utstest.ui.theme.LegalooTheme { CourseListScreen(CourseViewModel(), {}) }
}
