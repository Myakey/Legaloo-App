package org.ambatujudge.legaloo.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ambatujudge.legaloo.data.LevelNode
import org.ambatujudge.legaloo.data.LevelState
import org.ambatujudge.legaloo.data.LevelType
import org.ambatujudge.legaloo.ui.components.pressClickable
import org.ambatujudge.legaloo.ui.components.pressOffset
import org.ambatujudge.legaloo.ui.components.rememberPressState
import org.ambatujudge.legaloo.ui.theme.LegalooTheme
import org.ambatujudge.legaloo.ui.theme.LocalExtraColors
import org.ambatujudge.legaloo.viewmodel.CourseViewModel
import kotlinx.coroutines.launch

private val nodeOffsets = listOf(0, -56, -14, 42, 0, -56, -14, 42)

@Composable
fun CourseContentScreen(
    courseId: Int,
    viewModel: CourseViewModel,
    onStartLesson: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val courses by viewModel.courses.collectAsState()
    val course = courses.firstOrNull { it.id == courseId }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun showSnackbar(message: String) {
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (course == null) {
            Text(
                text = "Kursus tidak ditemukan",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Center),
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                CourseHeader(
                    unitLabel = course.unitLabel,
                    title = course.title,
                    modifier = Modifier.padding(start = 19.dp, end = 19.dp, top = 10.dp),
                )
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(top = 56.dp, bottom = 32.dp),
                ) {
                    itemsIndexed(course.levels, key = { _, level -> level.id }) { index, level ->
                        LevelRow(
                            level = level,
                            xOffset = nodeOffsets[index % nodeOffsets.size].dp,
                            onLockedClick = { showSnackbar("Selesaikan level sebelumnya dulu ya!") },
                            onStartClick = {
                                // Hanya "Hak dan Kewajiban" (id = 1) yang punya materi
                                if (courseId == 1) onStartLesson()
                                else showSnackbar("Materi ini segera hadir!")
                            },
                        )
                    }
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        ) { data ->
            Snackbar(
                shape = MaterialTheme.shapes.large,
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface,
            ) {
                Text(text = data.visuals.message, style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp))
            }
        }
    }
}

@Composable
private fun CourseHeader(unitLabel: String, title: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().background(LocalExtraColors.current.primaryEdge, MaterialTheme.shapes.large)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.large)
                .padding(horizontal = 18.dp, vertical = 14.dp),
        ) {
            Text(text = unitLabel, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
            Text(text = title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
private fun LevelRow(level: LevelNode, xOffset: Dp, onLockedClick: () -> Unit, onStartClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(94.dp), contentAlignment = Alignment.TopCenter) {
        Box(modifier = Modifier.offset(x = xOffset)) {
            LevelNodeView(level = level, onLockedClick = onLockedClick, onStartClick = onStartClick)
        }
    }
}

@Composable
private fun LevelNodeView(level: LevelNode, onLockedClick: () -> Unit, onStartClick: () -> Unit) {
    val extra = LocalExtraColors.current
    when (level.state) {
        LevelState.COMPLETED -> StandardNode(
            face = MaterialTheme.colorScheme.secondary, edge = extra.secondaryEdge,
            type = level.type, shakeOnClick = false, onClick = {},
        )
        LevelState.LOCKED -> StandardNode(
            face = extra.lockedFill, edge = extra.lockedEdge,
            type = level.type, shakeOnClick = true, onClick = onLockedClick,
        )
        LevelState.ACTIVE -> ActiveNode(type = level.type, onClick = onStartClick)
    }
}

@Composable
private fun NodeIcon(type: LevelType) {
    Icon(
        imageVector = if (type == LevelType.LESSON) Icons.Filled.Star else Icons.Filled.FitnessCenter,
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(28.dp),
    )
}

@Composable
private fun StandardNode(face: Color, edge: Color, type: LevelType, shakeOnClick: Boolean, onClick: () -> Unit) {
    val press = rememberPressState(maxDepth = 6.dp)
    val shake = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier.size(width = 72.dp, height = 78.dp)
            .graphicsLayer { translationX = shake.value.dp.toPx() }
            .pressClickable(press) {
                if (shakeOnClick) {
                    scope.launch {
                        repeat(3) { shake.animateTo(10f, tween(50)); shake.animateTo(-10f, tween(50)) }
                        shake.animateTo(0f, tween(50))
                    }
                }
                onClick()
            },
    ) {
        Box(modifier = Modifier.size(72.dp).offset(y = 6.dp).background(edge, CircleShape))
        Box(modifier = Modifier.pressOffset(press).size(72.dp).background(face, CircleShape), contentAlignment = Alignment.Center) {
            NodeIcon(type)
        }
    }
}

@Composable
private fun ActiveNode(type: LevelType, onClick: () -> Unit) {
    val extra = LocalExtraColors.current
    val press = rememberPressState(maxDepth = 6.dp)
    val transition = rememberInfiniteTransition(label = "activeNode")
    val bob by transition.animateFloat(
        initialValue = 0f, targetValue = -6f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 700, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse),
        label = "bob",
    )
    val t by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1400, easing = LinearOutSlowInEasing), repeatMode = RepeatMode.Restart),
        label = "halo",
    )
    Box(modifier = Modifier.offset(y = (-6).dp).size(width = 84.dp, height = 90.dp).pressClickable(press) { onClick() }) {
        Box(
            modifier = Modifier.size(84.dp)
                .graphicsLayer { val s = 1f + 0.3f * t; scaleX = s; scaleY = s; alpha = 0.8f * (1f - t) }
                .background(extra.activeRing, CircleShape),
        )
        Box(modifier = Modifier.size(84.dp).offset(y = 6.dp).background(extra.primaryEdge, CircleShape))
        Box(modifier = Modifier.pressOffset(press).size(84.dp)) {
            Box(modifier = Modifier.fillMaxSize().background(extra.activeRing, CircleShape))
            Box(
                modifier = Modifier.size(72.dp).align(Alignment.Center).background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center,
            ) { NodeIcon(type) }
        }
        Box(
            modifier = Modifier.align(Alignment.TopCenter)
                .offset(y = (-34f + bob).dp)
                .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
                .border(2.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                .padding(horizontal = 12.dp, vertical = 5.dp),
        ) {
            Text(text = "MULAI", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Preview(showBackground = true, widthDp = 395, heightDp = 850)
@Composable
private fun CourseContentScreenPreview() {
    LegalooTheme { CourseContentScreen(courseId = 1, viewModel = CourseViewModel()) }
}