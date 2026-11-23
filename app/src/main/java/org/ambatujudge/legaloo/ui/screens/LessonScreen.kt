package org.ambatujudge.legaloo.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ambatujudge.legaloo.data.Example
import org.ambatujudge.legaloo.data.LessonStep
import org.ambatujudge.legaloo.data.StepContent
import org.ambatujudge.legaloo.ui.components.LoadStatus
import org.ambatujudge.legaloo.ui.components.pressClickable
import org.ambatujudge.legaloo.ui.components.pressOffset
import org.ambatujudge.legaloo.ui.components.rememberPressState
import org.ambatujudge.legaloo.ui.theme.LocalExtraColors
import org.ambatujudge.legaloo.viewmodel.LessonViewModel

private val CorrectGreen = Color(0xFF2E9E5B)

@Composable
fun LessonScreen(viewModel: LessonViewModel, onClose: () -> Unit, onFinish: () -> Unit, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state.finished) { if (state.finished) onFinish() }

    val lesson = state.lesson
    if (lesson == null || lesson.steps.isEmpty()) {
        Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding()) {
            CloseRow(onClose = onClose)
            // 403 (locked) and 404 messages come straight from the server.
            LoadStatus(error = state.loadError ?: if (lesson != null) "Materi belum tersedia" else null, onRetry = viewModel::load)
        }
        return
    }

    BackHandler(enabled = state.step > 0) { viewModel.back() }

    val step = lesson.steps[state.step]
    val quiz = state.quiz
    val progress by animateFloatAsState(targetValue = (state.step + 1) / state.totalSteps.toFloat(), label = "progress")

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding()) {
        CloseRow(onClose = onClose) {
            Box(modifier = Modifier.padding(horizontal = 14.dp).weight(1f).height(12.dp).clip(CircleShape).background(MaterialTheme.colorScheme.outline)) {
                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(progress).clip(CircleShape).background(MaterialTheme.colorScheme.secondary))
            }
            Text("${state.step + 1}/${state.totalSteps}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Column(modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            StepView(step = step, selected = state.selected, checked = state.isChecked, onSelect = viewModel::select)
            Spacer(Modifier.height(16.dp))
            Mascot(
                message = when {
                    quiz == null || !state.isChecked -> step.mascotMessage
                    state.selectedIsCorrect -> "Hebat, kamu benar!"
                    else -> "Tidak apa-apa, coba lagi nanti!"
                },
            )
            state.submitError?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(16.dp))
        }

        Row(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 20.dp, vertical = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MaterialButton(
                text = "KEMBALI",
                face = MaterialTheme.colorScheme.surface,
                edge = MaterialTheme.colorScheme.outline,
                contentColor = MaterialTheme.colorScheme.primary,
                borderColor = MaterialTheme.colorScheme.outline,
                enabled = !state.isSubmitting,
                modifier = Modifier.weight(0.4f),
                onClick = { if (!viewModel.back()) onClose() },
            )
            MaterialButton(
                text = when {
                    quiz != null && !state.isChecked -> "PERIKSA"
                    !state.isLastStep -> "LANJUT"
                    else -> "SELESAI"
                },
                face = MaterialTheme.colorScheme.secondary,
                edge = LocalExtraColors.current.secondaryEdge,
                contentColor = MaterialTheme.colorScheme.onSurface,
                enabled = (quiz == null || state.selected >= 0) && !state.isSubmitting,
                loading = state.isSubmitting,
                modifier = Modifier.weight(0.6f),
                onClick = viewModel::primary,
            )
        }
    }
}

@Composable
private fun CloseRow(onClose: () -> Unit, content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.Close, contentDescription = "Tutup",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(28.dp).clip(CircleShape).clickable { onClose() },
        )
        content()
    }
}

@Composable
private fun StepView(step: LessonStep, selected: Int, checked: Boolean, onSelect: (Int) -> Unit) {
    ModeBadge(step.modeLabel)
    Spacer(Modifier.height(12.dp))
    when (val content = step.content) {
        is StepContent.Read -> {
            step.title?.let { StepTitle(it) }
            ContentCard {
                content.paragraphs.forEachIndexed { i, paragraph ->
                    if (i > 0) Spacer(Modifier.height(16.dp))
                    Text(paragraph, style = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp, lineHeight = 28.sp), color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
        is StepContent.Examples -> {
            step.title?.let { StepTitle(it) }
            content.examples.forEachIndexed { i, example ->
                if (i > 0) Spacer(Modifier.height(12.dp))
                ExampleCard(example)
            }
        }
        is StepContent.Quiz -> StepQuiz(content, selected = selected, checked = checked, onSelect = onSelect)
    }
}

@Composable
private fun StepTitle(text: String) {
    Text(text, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun ExampleCard(example: Example) {
    ContentCard {
        Text(example.label, style = MaterialTheme.typography.labelLarge, color = if (example.tone == "GOOD") CorrectGreen else MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(8.dp))
        // Unknown or missing illustration keys show no picture.
        val crossing = when (example.illustration) {
            "CROSSING_GREEN" -> true to false
            "CROSSING_RED_CAR" -> false to true
            else -> null
        }
        if (crossing != null) {
            Canvas(modifier = Modifier.fillMaxWidth().height(150.dp).clip(MaterialTheme.shapes.medium)) {
                drawCrossing(pedestrianGreen = crossing.first, showCar = crossing.second)
            }
            Spacer(Modifier.height(8.dp))
        }
        Text(example.caption, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

private fun DrawScope.drawCrossing(pedestrianGreen: Boolean, showCar: Boolean) {
    val w = size.width
    val h = size.height
    drawRect(Color(0xFFCFE8C8), size = Size(w, h * 0.3f))
    drawRect(Color(0xFF59627A), topLeft = Offset(0f, h * 0.3f), size = Size(w, h * 0.7f))
    val stripeW = w * 0.05f
    for (i in 0 until 5) {
        drawRect(Color.White, topLeft = Offset(w * 0.38f + i * stripeW * 1.8f, h * 0.3f), size = Size(stripeW, h * 0.7f))
    }
    drawRoundRect(Color(0xFF2B2F42), topLeft = Offset(w * 0.07f, 0f), size = Size(w * 0.1f, h * 0.29f), cornerRadius = CornerRadius(10f))
    val lampR = w * 0.03f
    drawCircle(if (pedestrianGreen) Color(0xFF5A2323) else Color(0xFFFF4B4B), lampR, Offset(w * 0.12f, h * 0.08f))
    drawCircle(if (pedestrianGreen) Color(0xFF3DDC84) else Color(0xFF1F4A31), lampR, Offset(w * 0.12f, h * 0.21f))
    if (showCar) {
        drawRoundRect(Color(0xFFFAB826), topLeft = Offset(w * 0.68f, h * 0.5f), size = Size(w * 0.28f, h * 0.28f), cornerRadius = CornerRadius(18f))
        drawRoundRect(Color(0xFFBFE3FF), topLeft = Offset(w * 0.72f, h * 0.54f), size = Size(w * 0.12f, h * 0.1f), cornerRadius = CornerRadius(8f))
        drawCircle(Color(0xFF1B2A4E), h * 0.05f, Offset(w * 0.75f, h * 0.78f))
        drawCircle(Color(0xFF1B2A4E), h * 0.05f, Offset(w * 0.89f, h * 0.78f))
    }
    val cx = w * 0.52f
    drawRect(Color(0xFF1B2A4E), topLeft = Offset(cx - h * 0.06f, h * 0.78f), size = Size(h * 0.045f, h * 0.14f))
    drawRect(Color(0xFF1B2A4E), topLeft = Offset(cx + h * 0.015f, h * 0.78f), size = Size(h * 0.045f, h * 0.14f))
    drawRoundRect(Color(0xFF1F4FA3), topLeft = Offset(cx - h * 0.08f, h * 0.55f), size = Size(h * 0.16f, h * 0.25f), cornerRadius = CornerRadius(14f))
    drawCircle(Color(0xFFF2C29B), h * 0.08f, Offset(cx, h * 0.48f))
    drawArc(Color(0xFF3B2A20), 180f, 180f, true, Offset(cx - h * 0.08f, h * 0.4f), Size(h * 0.16f, h * 0.14f))
}

@Composable
private fun StepQuiz(quiz: StepContent.Quiz, selected: Int, checked: Boolean, onSelect: (Int) -> Unit) {
    Text(quiz.question, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
    Spacer(Modifier.height(16.dp))
    quiz.options.forEachIndexed { index, option ->
        val isSelected = index == selected
        val borderColor = when {
            checked && option.correct -> CorrectGreen
            checked && isSelected -> MaterialTheme.colorScheme.error
            isSelected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outline
        }
        Box(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                .clip(MaterialTheme.shapes.large)
                .background(if (isSelected || (checked && option.correct)) borderColor.copy(alpha = 0.10f) else MaterialTheme.colorScheme.surface)
                .border(2.dp, borderColor, MaterialTheme.shapes.large)
                .clickable(enabled = !checked) { onSelect(index) }
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Text(option.text, style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp), color = MaterialTheme.colorScheme.onSurface)
        }
    }
    if (checked) {
        Spacer(Modifier.height(4.dp))
        val correct = quiz.options.getOrNull(selected)?.correct == true
        Text(
            text = if (correct) quiz.feedbackCorrect else quiz.feedbackWrong,
            style = MaterialTheme.typography.titleMedium,
            color = if (correct) CorrectGreen else MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
private fun ModeBadge(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.clip(MaterialTheme.shapes.small).background(MaterialTheme.colorScheme.primary).padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

@Composable
private fun ContentCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(2.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.large)
            .padding(18.dp),
    ) { content() }
}

@Composable
private fun Mascot(message: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(88.dp).background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f), CircleShape), contentAlignment = Alignment.Center) {
            Text("🦁", fontSize = 48.sp)
        }
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.clip(MaterialTheme.shapes.medium).background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text("Pak Singa", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Start)
        }
    }
}

@Composable
private fun MaterialButton(
    text: String,
    face: Color,
    edge: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    borderColor: Color? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    onClick: () -> Unit,
) {
    val press = rememberPressState(maxDepth = 4.dp)
    Box(modifier = modifier.height(56.dp).alpha(if (enabled || loading) 1f else 0.5f).pressClickable(press) { if (enabled) onClick() }) {
        Box(modifier = Modifier.fillMaxWidth().height(52.dp).offset(y = 4.dp).background(edge, MaterialTheme.shapes.large))
        Box(
            modifier = Modifier.pressOffset(press).fillMaxWidth().height(52.dp)
                .background(face, MaterialTheme.shapes.large)
                .then(if (borderColor != null) Modifier.border(2.dp, borderColor, MaterialTheme.shapes.large) else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            if (loading) CircularProgressIndicator(color = contentColor, strokeWidth = 3.dp, modifier = Modifier.size(24.dp))
            else Text(text, style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp), color = contentColor)
        }
    }
}
