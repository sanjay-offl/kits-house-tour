package com.kitshousetour.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SpeechBubble(
    text: String,
    trigger: Int,
    reducedMotion: Boolean,
    modifier: Modifier = Modifier,
) {
    var visible by remember(trigger) { mutableStateOf(true) }
    val bubbleColor = MaterialTheme.colorScheme.secondaryContainer

    LaunchedEffect(trigger) {
        delay(3_000)
        visible = false
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = if (reducedMotion) {
            fadeIn(tween(120))
        } else {
            fadeIn(tween(180, easing = LinearOutSlowInEasing)) +
                scaleIn(
                    initialScale = 0.97f,
                    animationSpec = tween(180, easing = LinearOutSlowInEasing),
                )
        },
        exit = if (reducedMotion) {
            fadeOut(tween(100))
        } else {
            fadeOut(tween(140, easing = FastOutLinearInEasing)) +
                scaleOut(
                    targetScale = 0.97f,
                    animationSpec = tween(140, easing = FastOutLinearInEasing),
                )
        },
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                color = bubbleColor,
                shape = RoundedCornerShape(18.dp),
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
            Canvas(
                modifier = Modifier
                    .padding(end = 32.dp)
                    .size(width = 20.dp, height = 10.dp),
            ) {
                val tail = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width * 0.3f, size.height)
                    close()
                }
                drawPath(tail, bubbleColor)
            }
        }
    }
}
