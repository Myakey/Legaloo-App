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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ambatujudge.legaloo.ui.components.pressClickable
import org.ambatujudge.legaloo.ui.components.pressOffset
import org.ambatujudge.legaloo.ui.components.rememberPressState
import org.ambatujudge.legaloo.ui.theme.LegalooTheme
import org.ambatujudge.legaloo.ui.theme.LocalExtraColors

private const val TOTAL_STEPS = 3
private val CorrectGreen = Color(0xFF2E9E5B)

private data class QuizOption(val text: String, val isCorrect: Boolean)

private const val quizQuestion = "Kamu ingin menyeberang jalan, tetapi lampu pejalan kaki berwarna merah. Apa yang harus kamu lakukan?"
private val quizOptions = listOf(
    QuizOption("Berlari cepat menyeberang", false),
    QuizOption("Menunggu sampai lampu pejalan kaki hijau", true),
    QuizOption("Menyeberang sambil bermain HP", false),
    QuizOption("Mengikuti teman yang sudah menyeberang", false),
)

@Composable
fun CourseMaterial1Screen(onClose: () -> Unit, onFinish: () -> Unit, modifier: Modifier = Modifier) {
    var step by rememberSaveable { mutableIntStateOf(0) }
    var selected by rememberSaveable { mutableIntStateOf(-1) }
    var checked by rememberSaveable { mutableStateOf(false) }

    BackHandler(enabled = step > 0) { step-- }

    val progress by animateFloatAsState(targetValue = (step + 1) / TOTAL_STEPS.toFloat(), label = "progress")
    val isQuiz = step == TOTAL_STEPS - 1

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding()) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Close, contentDescription = "Tutup",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp).clip(CircleShape).clickable { onClose() },
            )
            Box(modifier = Modifier.padding(horizontal = 14.dp).weight(1f).height(12.dp).clip(CircleShape).background(MaterialTheme.colorScheme.outline)) {
                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(progress).clip(CircleShape).background(MaterialTheme.colorScheme.secondary))
            }
            Text("${step + 1}/$TOTAL_STEPS", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Column(modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            when (step) {
                0 -> StepIntro()
                1 -> StepExamples()
                else -> StepQuiz(selected = selected, checked = checked, onSelect = { if (!checked) selected = it })
            }
            Spacer(Modifier.height(16.dp))
            Mascot(
                message = when {
                    step == 0 -> "Ayo kita lanjut!"
                    step == 1 -> "Perhatikan lampunya ya!"
                    !checked -> "Pilih jawabanmu!"
                    quizOptions[selected].isCorrect -> "Hebat, kamu benar!"
                    else -> "Tidak apa-apa, coba lagi nanti!"
                },
            )
            Spacer(Modifier.height(16.dp))
        }

        Row(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 20.dp, vertical = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MaterialButton(
                text = "KEMBALI",
                face = MaterialTheme.colorScheme.surface,
                edge = MaterialTheme.colorScheme.outline,
                contentColor = MaterialTheme.colorScheme.primary,
                borderColor = MaterialTheme.colorScheme.outline,
                modifier = Modifier.weight(0.4f),
                onClick = { if (step == 0) onClose() else { step--; checked = false } },
            )
            MaterialButton(
                text = when { !isQuiz -> "LANJUT"; !checked -> "PERIKSA"; else -> "SELESAI" },
                face = MaterialTheme.colorScheme.secondary,
                edge = LocalExtraColors.current.secondaryEdge,
                contentColor = MaterialTheme.colorScheme.onSurface,
                enabled = !isQuiz || selected >= 0,
                modifier = Modifier.weight(0.6f),
                onClick = {
                    when {
                        !isQuiz -> step++
                        !checked -> checked = true
                        else -> onFinish()
                    }
                },
            )
        }
    }
}

@Composable
private fun StepIntro() {
    ModeBadge("MODE BACA")
    Spacer(Modifier.height(12.dp))
    Text("Rambu dan Lampu Lalu Lintas", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
    Spacer(Modifier.height(16.dp))
    ContentCard {
        Text(
            "Jalan raya punya aturan supaya semua orang aman. Rambu dan lampu lalu lintas adalah tandanya.",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp, lineHeight = 28.sp),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Lampu merah artinya berhenti, kuning artinya hati-hati, dan hijau artinya boleh jalan. Pejalan kaki menyeberang di zebra cross saat lampu pejalan kaki berwarna hijau.",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp, lineHeight = 28.sp),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun StepExamples() {
    ModeBadge("MODE BACA")
    Spacer(Modifier.height(12.dp))
    Text("Menyeberang dengan Aman", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
    Spacer(Modifier.height(16.dp))
    ExampleCard(label = "CONTOH BAIK", labelColor = CorrectGreen, caption = "Lampu pejalan kaki hijau. Anak menyeberang di zebra cross.", pedestrianGreen = true, showCar = false)
    Spacer(Modifier.height(12.dp))
    ExampleCard(label = "CONTOH BURUK", labelColor = MaterialTheme.colorScheme.error, caption = "Lampu pejalan kaki merah, tetapi anak tetap menyeberang. Berbahaya!", pedestrianGreen = false, showCar = true)
}

@Composable
private fun ExampleCard(label: String, labelColor: Color, caption: String, pedestrianGreen: Boolean, showCar: Boolean) {
    ContentCard {
        Text(label, style = MaterialTheme.typography.labelLarge, color = labelColor)
        Spacer(Modifier.height(8.dp))
        Canvas(modifier = Modifier.fillMaxWidth().height(150.dp).clip(MaterialTheme.shapes.medium)) {
            drawCrossing(pedestrianGreen, showCar)
        }
        Spacer(Modifier.height(8.dp))
        Text(caption, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
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
private fun StepQuiz(selected: Int, checked: Boolean, onSelect: (Int) -> Unit) {
    ModeBadge("MODE KUIS")
    Spacer(Modifier.height(12.dp))
    Text(quizQuestion, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
    Spacer(Modifier.height(16.dp))
    quizOptions.forEachIndexed { index, option ->
        val isSelected = index == selected
        val borderColor = when {
            checked && option.isCorrect -> CorrectGreen
            checked && isSelected -> MaterialTheme.colorScheme.error
            isSelected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outline
        }
        Box(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                .clip(MaterialTheme.shapes.large)
                .background(if (isSelected || (checked && option.isCorrect)) borderColor.copy(alpha = 0.10f) else MaterialTheme.colorScheme.surface)
                .border(2.dp, borderColor, MaterialTheme.shapes.large)
                .clickable(enabled = !checked) { onSelect(index) }
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Text(option.text, style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp), color = MaterialTheme.colorScheme.onSurface)
        }
    }
    if (checked) {
        Spacer(Modifier.height(4.dp))
        val correct = quizOptions[selected].isCorrect
        Text(
            text = if (correct) "Benar! Tunggu lampu pejalan kaki hijau sebelum menyeberang." else "Kurang tepat. Jawaban yang benar: tunggu sampai lampu pejalan kaki hijau.",
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
    onClick: () -> Unit,
) {
    val press = rememberPressState(maxDepth = 4.dp)
    Box(modifier = modifier.height(56.dp).alpha(if (enabled) 1f else 0.5f).pressClickable(press) { if (enabled) onClick() }) {
        Box(modifier = Modifier.fillMaxWidth().height(52.dp).offset(y = 4.dp).background(edge, MaterialTheme.shapes.large))
        Box(
            modifier = Modifier.pressOffset(press).fillMaxWidth().height(52.dp)
                .background(face, MaterialTheme.shapes.large)
                .then(if (borderColor != null) Modifier.border(2.dp, borderColor, MaterialTheme.shapes.large) else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            Text(text, style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp), color = contentColor)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun CourseMaterial1Preview() { LegalooTheme { CourseMaterial1Screen(onClose = {}, onFinish = {}) } }