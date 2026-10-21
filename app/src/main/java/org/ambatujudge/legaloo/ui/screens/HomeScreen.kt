package org.ambatujudge.legaloo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ambatujudge.legaloo.data.StudyDay
import org.ambatujudge.legaloo.ui.components.LegalooButton
import org.ambatujudge.legaloo.ui.components.LegalooCard
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.graphicsLayer
import org.ambatujudge.legaloo.ui.components.rememberAnimatedProgress
import kotlinx.coroutines.delay
import org.ambatujudge.legaloo.ui.theme.LegalooTheme
import org.ambatujudge.legaloo.ui.theme.LocalExtraColors
import org.ambatujudge.legaloo.viewmodel.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel, onContinueClick: (Int) -> Unit, modifier: Modifier = Modifier) {
    val progress by viewModel.progress.collectAsState()
    val currentCourse = viewModel.getCurrentCourse()
    val extra = LocalExtraColors.current
    val flameTransition = rememberInfiniteTransition(label = "flame")
    val flameScale by flameTransition.animateFloat(initialValue = 1f, targetValue = 1.18f, animationSpec = infiniteRepeatable(animation = tween(durationMillis = 700, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse), label = "flameScale")
    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).padding(horizontal = 28.dp)) {
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Halo, ${progress.userName}!", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(28.dp).graphicsLayer { scaleX = flameScale; scaleY = flameScale })
                Spacer(Modifier.width(8.dp))
                Text("${progress.streakCount} hari", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
            }
        }
        Spacer(Modifier.height(16.dp))
        LegalooCard {
            Text("TARGET HARI INI", style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.5.sp), color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                DailyProgressBar(progress = if (progress.dailyGoalMinutes == 0) 0f else progress.minutesStudied.toFloat() / progress.dailyGoalMinutes.toFloat(), modifier = Modifier.weight(1f))
                Spacer(Modifier.width(12.dp))
                Text("${progress.minutesStudied}/${progress.dailyGoalMinutes} mnt", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            }
        }
        Spacer(Modifier.height(12.dp))
        LegalooCard {
            Text("STREAK MINGGU INI", style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.5.sp), color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                progress.week.forEachIndexed { index, day -> DayBubble(day, index) }
            }
        }
        Spacer(Modifier.height(12.dp))
        LegalooCard(containerColor = MaterialTheme.colorScheme.primary, borderColor = extra.primaryEdge, edgeColor = extra.primaryEdge) {
            Text("LANJUTKAN PELAJARAN", style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.5.sp), color = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.height(8.dp))
            Text("Unit ${progress.currentUnitNumber} · ${currentCourse?.title ?: "-"}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary)
            Spacer(Modifier.height(12.dp))
            LegalooButton(text = "LANJUTKAN", onClick = { onContinueClick(progress.currentCourseId) })
        }
        Spacer(Modifier.height(24.dp))
    }
}

  @Composable
  private fun DailyProgressBar(progress: Float, modifier: Modifier = Modifier) {
      val animated = rememberAnimatedProgress(progress)
      Box(modifier = modifier.height(18.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)) {
          Box(Modifier.fillMaxWidth(animated).fillMaxHeight().background(MaterialTheme.colorScheme.primary, CircleShape))
      }
  }

@Composable
private fun DayBubble(day: StudyDay, index: Int) {
    val face = if (day.completed) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (day.completed) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { kotlinx.coroutines.delay(200L + index * 60L); visible = true }
    val scale by animateFloatAsState(targetValue = if (visible) 1f else 0f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow), label = "bubbleScale")
    Box(Modifier.width(38.dp).height(36.dp).graphicsLayer { scaleX = scale; scaleY = scale }.background(face, CircleShape), contentAlignment = Alignment.Center) {
        Text(day.label, style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp), color = textColor)
    }
}

@Preview(showBackground = true, widthDp = 395, heightDp = 850)
@Composable
private fun HomeScreenPreview() {
    LegalooTheme { HomeScreen(viewModel = HomeViewModel(), onContinueClick = {}) }
}
