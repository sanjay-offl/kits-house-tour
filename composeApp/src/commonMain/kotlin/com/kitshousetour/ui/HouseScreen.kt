package com.kitshousetour.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kitshousetour.content.NavigationText
import com.kitshousetour.content.RoomText
import com.kitshousetour.domain.Room
import com.kitshousetour.domain.RoomId
import com.kitshousetour.engine.GameProgress
import com.kitshousetour.engine.HouseLayout
import com.kitshousetour.engine.HousePoint
import com.kitshousetour.engine.KitMood
import com.kitshousetour.engine.isRoomUnlocked
import com.kitshousetour.engine.roomTarget
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.math.sqrt

/** Shows the layered floor plan, sequential room locks, and Kit's moving destination marker. */
@Composable
fun HouseScreen(
    progress: GameProgress,
    mood: KitMood,
    speechText: String?,
    speechTrigger: Int,
    tapTrigger: Int,
    currentRoomId: RoomId,
    quizUnlocked: Boolean,
    reducedMotion: Boolean,
    onOpenQuiz: () -> Unit,
    onOpenBadges: () -> Unit,
    onOpenSettings: () -> Unit,
    onRoomSelected: (Room) -> Unit,
    onLockedRoomTapped: () -> Unit,
    onKitTapped: () -> Unit,
) {
    val latestOnRoomSelected by rememberUpdatedState(onRoomSelected)
    val dragScope = rememberCoroutineScope()
    var pendingRoom by remember { mutableStateOf<Room?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(
            text = RoomText.APP_TITLE,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = RoomText.progressLine(progress.visitedRooms.size, RoomText.rooms.size),
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.bodyLarge,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            TextButton(
                onClick = onOpenQuiz,
                enabled = quizUnlocked,
                modifier = Modifier.semantics {
                    contentDescription = if (quizUnlocked) {
                        NavigationText.QUIZ_OPEN_DESCRIPTION
                    } else {
                        NavigationText.QUIZ_LOCKED_DESCRIPTION
                    }
                },
            ) {
                Text(NavigationText.QUIZ_BUTTON)
            }
            TextButton(
                onClick = onOpenBadges,
                modifier = Modifier.semantics {
                    contentDescription = NavigationText.BADGES_DESCRIPTION
                },
            ) {
                Text(NavigationText.BADGES_BUTTON)
            }
            TextButton(
                onClick = onOpenSettings,
                modifier = Modifier.semantics {
                    contentDescription = NavigationText.SETTINGS_DESCRIPTION
                },
            ) {
                Text(NavigationText.SETTINGS_BUTTON)
            }
        }
        if (!quizUnlocked) {
            Text(
                text = NavigationText.QUIZ_LOCKED_HINT,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        speechText?.let {
            SpeechBubble(
                text = it,
                trigger = speechTrigger,
                reducedMotion = reducedMotion,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp, bottom = 4.dp),
            )
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            val layout = if (maxWidth >= 640.dp) HouseLayout.WIDE else HouseLayout.COMPACT
            val initialTarget = remember(layout, currentRoomId) {
                roomTarget(currentRoomId, layout)
            }
            val kitX = remember(layout, currentRoomId) { Animatable(initialTarget.xFraction) }
            val kitY = remember(layout, currentRoomId) { Animatable(initialTarget.yFraction) }
            val dragX = remember { Animatable(0f) }
            val dragLimitPx = with(LocalDensity.current) { 40.dp.toPx() }
            val kitPosition = HousePoint(kitX.value, kitY.value)
            val innerWidth = (maxWidth - 24.dp).coerceAtLeast(0.dp)
            val innerHeight = (
                maxHeight - 24.dp -
                    10.dp * if (layout == HouseLayout.WIDE) 2 else 4
                ).coerceAtLeast(0.dp)
            val kitSize = 48.dp
            val kitOffsetX = 12.dp + innerWidth * kitPosition.xFraction - kitSize / 2
            val kitOffsetY = 12.dp + innerHeight * kitPosition.yFraction - kitSize / 2
            val kitTranslationX = with(LocalDensity.current) { kitOffsetX.toPx() }
            val kitTranslationY = with(LocalDensity.current) { kitOffsetY.toPx() }

            LaunchedEffect(pendingRoom, layout, reducedMotion) {
                val destination = pendingRoom ?: return@LaunchedEffect
                val target = roomTarget(destination.id, layout)
                if (reducedMotion) {
                    kitX.snapTo(target.xFraction)
                    kitY.snapTo(target.yFraction)
                } else {
                    coroutineScope {
                        launch {
                            kitX.animateTo(
                                targetValue = target.xFraction,
                                animationSpec = tween(240, easing = FastOutSlowInEasing),
                            )
                        }
                        launch {
                            kitY.animateTo(
                                targetValue = target.yFraction,
                                animationSpec = tween(240, easing = FastOutSlowInEasing),
                            )
                        }
                    }
                }
                latestOnRoomSelected(destination)
                pendingRoom = null
            }

            HouseFloorPlan(
                layout = layout,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = dragX.value * 0.24f
                    },
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(dragLimitPx) {
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                val target = rubberBand(dragX.value, dragAmount, dragLimitPx)
                                dragScope.launch { dragX.snapTo(target) }
                            },
                            onDragEnd = {
                                dragScope.launch {
                                    if (reducedMotion) {
                                        dragX.snapTo(0f)
                                    } else {
                                        dragX.animateTo(0f, animationSpec = spring())
                                    }
                                }
                            },
                            onDragCancel = {
                                dragScope.launch {
                                    if (reducedMotion) {
                                        dragX.snapTo(0f)
                                    } else {
                                        dragX.animateTo(0f, animationSpec = spring())
                                    }
                                }
                            },
                        )
                    }
                    .graphicsLayer {
                        translationX = dragX.value
                    },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (layout == HouseLayout.WIDE) {
                        RoomText.rooms.chunked(2).forEach { rowRooms ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                if (rowRooms.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                                rowRooms.forEach { room ->
                                    RoomCard(
                                        room = room,
                                        isLocked = !isRoomUnlocked(progress, room.id),
                                        reducedMotion = reducedMotion,
                                        highlight = roomHighlight(
                                            kitPosition,
                                            roomTarget(room.id, layout),
                                        ),
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            if (isRoomUnlocked(progress, room.id)) {
                                                pendingRoom = room
                                            } else {
                                                pendingRoom = null
                                                onLockedRoomTapped()
                                            }
                                        },
                                    )
                                }
                            }
                        }
                    } else {
                        RoomText.rooms.forEach { room ->
                            RoomCard(
                                room = room,
                                isLocked = !isRoomUnlocked(progress, room.id),
                                reducedMotion = reducedMotion,
                                highlight = roomHighlight(
                                    kitPosition,
                                    roomTarget(room.id, layout),
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                onClick = {
                                    if (isRoomUnlocked(progress, room.id)) {
                                        pendingRoom = room
                                    } else {
                                        pendingRoom = null
                                        onLockedRoomTapped()
                                    }
                                },
                            )
                        }
                    }
                }

                KitCharacter(
                    mood = mood,
                    tapTrigger = tapTrigger,
                    reducedMotion = reducedMotion,
                    onClick = onKitTapped,
                    modifier = Modifier
                        .size(kitSize)
                        .graphicsLayer {
                            translationX = kitTranslationX
                            translationY = kitTranslationY
                        }
                        .zIndex(1f),
                )
            }
        }
    }
}

@Composable
private fun RoomCard(
    room: Room,
    isLocked: Boolean,
    highlight: Float,
    reducedMotion: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed && !reducedMotion) 0.97f else 1f,
        animationSpec = tween(120, easing = FastOutSlowInEasing),
        label = "room-card-press",
    )
    val borderColor = blendColors(
        from = KitPalette.Border,
        to = KitPalette.AccentHighlight.copy(alpha = 0.65f),
        fraction = highlight,
    )
    val containerColor = blendColors(
        from = KitPalette.Surface,
        to = KitPalette.Accent,
        fraction = highlight * 0.12f,
    )
    Card(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .semantics {
                contentDescription = RoomText.accessibilityLabel(room, isLocked)
            },
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp + 4.dp * highlight),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = 10.dp, bottom = 10.dp, end = 62.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (isLocked) RoomText.LOCKED_ICON else RoomText.iconFor(room.id),
                modifier = Modifier.size(36.dp),
                style = MaterialTheme.typography.headlineSmall,
            )
            Column {
                Text(
                    text = room.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isLocked) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                )
                Text(
                    text = if (isLocked) RoomText.LOCKED_HINT else room.interactionPrompt,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (isLocked) {
                    Text(
                        text = RoomText.LOCKED_LABEL,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun HouseFloorPlan(layout: HouseLayout, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(KitPalette.Surface, KitPalette.Background),
                startY = 0f,
                endY = size.height,
            ),
            cornerRadius = CornerRadius(24.dp.toPx()),
        )
        val perspective = Path().apply {
            moveTo(size.width * 0.06f, size.height * 0.08f)
            lineTo(size.width * 0.94f, size.height * 0.08f)
            lineTo(size.width * 0.88f, size.height * 0.22f)
            lineTo(size.width * 0.12f, size.height * 0.22f)
            close()
        }
        drawPath(
            path = perspective,
            color = KitPalette.AccentHighlight.copy(alpha = 0.05f),
        )

        val points = RoomId.entries.map { roomTarget(it, layout) }
        points.zipWithNext().forEach { (from, to) ->
            drawLine(
                color = KitPalette.AccentHighlight.copy(alpha = 0.14f),
                start = Offset(from.xFraction * size.width, from.yFraction * size.height),
                end = Offset(to.xFraction * size.width, to.yFraction * size.height),
                strokeWidth = 5.dp.toPx(),
            )
        }

        if (layout == HouseLayout.WIDE) {
            drawLine(
                color = KitPalette.Border,
                start = Offset(size.width / 2f, size.height * 0.08f),
                end = Offset(size.width / 2f, size.height * 0.92f),
                strokeWidth = 1.dp.toPx(),
            )
            listOf(1f / 3f, 2f / 3f).forEach { fraction ->
                drawLine(
                    color = KitPalette.Border,
                    start = Offset(size.width * 0.06f, size.height * fraction),
                    end = Offset(size.width * 0.94f, size.height * fraction),
                    strokeWidth = 1.dp.toPx(),
                )
            }
        } else {
            (1..4).forEach { row ->
                drawLine(
                    color = KitPalette.Border,
                    start = Offset(size.width * 0.06f, size.height * row / 5f),
                    end = Offset(size.width * 0.94f, size.height * row / 5f),
                    strokeWidth = 1.dp.toPx(),
                )
            }
        }
    }
}

private fun roomHighlight(position: HousePoint, target: HousePoint): Float {
    val xDistance = position.xFraction - target.xFraction
    val yDistance = position.yFraction - target.yFraction
    val distance = sqrt(xDistance * xDistance + yDistance * yDistance)
    return (1f - distance / 0.18f).coerceIn(0f, 1f)
}

private fun rubberBand(value: Float, delta: Float, limit: Float): Float {
    val proposed = value + delta
    if (abs(proposed) <= limit) return proposed
    val overshoot = (abs(proposed) - limit) * 0.2f
    return sign(proposed) * (limit + overshoot).coerceAtMost(limit * 1.35f)
}

private fun blendColors(from: Color, to: Color, fraction: Float): Color {
    val progress = fraction.coerceIn(0f, 1f)
    return Color(
        red = from.red + (to.red - from.red) * progress,
        green = from.green + (to.green - from.green) * progress,
        blue = from.blue + (to.blue - from.blue) * progress,
        alpha = from.alpha + (to.alpha - from.alpha) * progress,
    )
}
