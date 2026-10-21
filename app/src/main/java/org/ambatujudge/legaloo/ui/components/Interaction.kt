package org.ambatujudge.legaloo.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

class PressState(val interactionSource: MutableInteractionSource, private val depthState: State<Dp>) {
    val depth: Dp get() = depthState.value
}

@Composable
fun rememberPressState(maxDepth: Dp = 4.dp): PressState {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val depthState = animateDpAsState(targetValue = if (pressed) maxDepth else 0.dp, animationSpec = tween(durationMillis = 70), label = "pressDepth")
    return remember(source, depthState) { PressState(source, depthState) }
}

fun Modifier.pressClickable(press: PressState, onClick: () -> Unit): Modifier = this.clickable(interactionSource = press.interactionSource, indication = null, onClick = onClick)

fun Modifier.pressOffset(press: PressState): Modifier = this.offset { IntOffset(0, press.depth.roundToPx()) }

@Composable
fun rememberAnimatedProgress(target: Float, delayMillis: Int = 0): Float {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }
    val value by animateFloatAsState(targetValue = if (started) target.coerceIn(0f, 1f) else 0f, animationSpec = tween(durationMillis = 800, delayMillis = delayMillis, easing = FastOutSlowInEasing), label = "progress")
    return value
}
