package com.kitshousetour.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.kitshousetour.content.KitText
import com.kitshousetour.engine.KitMood
import kotlinx.coroutines.delay

/** Draws an animated original character and exposes it as an accessible tap target. */
@Composable
fun KitCharacter(
    mood: KitMood,
    tapTrigger: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bodyColor by animateColorAsState(
        targetValue = kitColorForMood(mood),
        animationSpec = tween(350),
        label = "kit-mood-color",
    )
    val transition = rememberInfiniteTransition(label = "kit-idle-animation")
    val bobDistance = when (mood) {
        KitMood.Tired -> 3f
        KitMood.Proud -> 12f
        else -> 5f
    }
    val bobDurationMillis = when (mood) {
        KitMood.Tired -> 1_800
        KitMood.Proud -> 420
        else -> 1_050
    }
    val bobOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = bobDistance,
        animationSpec = infiniteRepeatable(
            animation = tween(bobDurationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "kit-bob",
    )

    var eyesClosed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(3_000)
            eyesClosed = true
            delay(120)
            eyesClosed = false
        }
    }

    var tapScaleTarget by remember { mutableFloatStateOf(1f) }
    LaunchedEffect(tapTrigger) {
        if (tapTrigger > 0) {
            tapScaleTarget = 0.88f
            delay(90)
            tapScaleTarget = 1.08f
            delay(100)
            tapScaleTarget = 1f
        }
    }
    val tapScale by animateFloatAsState(
        targetValue = tapScaleTarget,
        animationSpec = tween(100, easing = FastOutSlowInEasing),
        label = "kit-tap-reaction",
    )

    Box(
        modifier = modifier
            .size(176.dp)
            .semantics {
                contentDescription = KitText.ACCESSIBILITY_DESCRIPTION
                role = Role.Button
            }
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier
                .size(148.dp)
                .graphicsLayer {
                    translationY = bobOffset
                    rotationZ = if (mood == KitMood.Curious) -6f else 0f
                    scaleX = 1f + (1f - tapScale) * 0.45f
                    scaleY = tapScale
                },
        ) {
            val centerX = size.width / 2f
            val bodyRadius = size.minDimension * 0.34f
            val bodyCenterY = size.height * 0.59f
            val eyeColor = KitPalette.KitFace
            val earSize = Size(bodyRadius * 0.55f, bodyRadius * 0.9f)

            val tail = Path().apply {
                moveTo(centerX + bodyRadius * 0.78f, bodyCenterY + bodyRadius * 0.55f)
                cubicTo(
                    centerX + bodyRadius * 1.15f,
                    bodyCenterY + bodyRadius * 0.9f,
                    centerX + bodyRadius * 1.3f,
                    bodyCenterY + bodyRadius * 0.25f,
                    centerX + bodyRadius * 1.02f,
                    bodyCenterY + bodyRadius * 0.16f,
                )
            }
            drawPath(
                path = tail,
                color = bodyColor,
                style = Stroke(width = bodyRadius * 0.14f),
            )
            drawOval(
                color = bodyColor,
                topLeft = Offset(centerX - bodyRadius * 1.15f, bodyCenterY - bodyRadius * 1.35f),
                size = earSize,
            )
            drawOval(
                color = bodyColor,
                topLeft = Offset(centerX + bodyRadius * 0.6f, bodyCenterY - bodyRadius * 1.35f),
                size = earSize,
            )
            drawCircle(color = bodyColor, radius = bodyRadius, center = Offset(centerX, bodyCenterY))

            val eyeY = bodyCenterY - bodyRadius * 0.12f
            val eyeOffset = bodyRadius * 0.4f
            val eyeWidth = bodyRadius * 0.22f
            val eyeHeight = bodyRadius * 0.28f
            if (eyesClosed) {
                drawLine(
                    color = eyeColor,
                    start = Offset(centerX - eyeOffset - eyeWidth / 2f, eyeY),
                    end = Offset(centerX - eyeOffset + eyeWidth / 2f, eyeY),
                    strokeWidth = bodyRadius * 0.07f,
                )
                drawLine(
                    color = eyeColor,
                    start = Offset(centerX + eyeOffset - eyeWidth / 2f, eyeY),
                    end = Offset(centerX + eyeOffset + eyeWidth / 2f, eyeY),
                    strokeWidth = bodyRadius * 0.07f,
                )
            } else {
                val visibleEyeHeight = if (mood == KitMood.Tired) eyeHeight * 0.55f else eyeHeight
                drawOval(
                    color = eyeColor,
                    topLeft = Offset(centerX - eyeOffset - eyeWidth / 2f, eyeY - visibleEyeHeight / 2f),
                    size = Size(eyeWidth, visibleEyeHeight),
                )
                drawOval(
                    color = eyeColor,
                    topLeft = Offset(centerX + eyeOffset - eyeWidth / 2f, eyeY - visibleEyeHeight / 2f),
                    size = Size(eyeWidth, visibleEyeHeight),
                )
            }

            val cheekY = bodyCenterY + bodyRadius * 0.12f
            drawCircle(KitPalette.KitCheek, bodyRadius * 0.12f, Offset(centerX - bodyRadius * 0.63f, cheekY))
            drawCircle(KitPalette.KitCheek, bodyRadius * 0.12f, Offset(centerX + bodyRadius * 0.63f, cheekY))
            drawArc(
                color = eyeColor,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(centerX - bodyRadius * 0.23f, bodyCenterY + bodyRadius * 0.1f),
                size = Size(bodyRadius * 0.46f, bodyRadius * 0.3f),
                style = Stroke(width = bodyRadius * 0.05f),
            )
        }
    }
}
