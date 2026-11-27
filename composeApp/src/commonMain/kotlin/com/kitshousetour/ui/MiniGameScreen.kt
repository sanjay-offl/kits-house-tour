package com.kitshousetour.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.kitshousetour.content.MiniGameText
import com.kitshousetour.domain.MiniGame
import com.kitshousetour.domain.RoomId
import com.kitshousetour.engine.GameProgress
import com.kitshousetour.engine.MiniGameOutcome
import com.kitshousetour.engine.evaluateChoice
import com.kitshousetour.engine.evaluateTokenOrder
import com.kitshousetour.engine.validateMiniGame

/** Renders a room's coding challenge and reports earned points to the game state owner. */
@Composable
fun MiniGameScreen(
    game: MiniGame,
    progress: GameProgress,
    reducedMotion: Boolean,
    onScore: (RoomId, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val validationErrors = remember(game) { validateMiniGame(game) }
    val scrollState = rememberScrollState()
    var tokenOrder by remember(game.id) { mutableStateOf(emptyList<String>()) }
    var outcome by remember(game.id) { mutableStateOf<MiniGameOutcome?>(null) }
    val hasOutcome = outcome != null
    val feedbackAlpha by animateFloatAsState(
        targetValue = if (hasOutcome) 1f else 0f,
        animationSpec = tween(
            durationMillis = when {
                reducedMotion && hasOutcome -> 120
                reducedMotion -> 100
                hasOutcome -> 180
                else -> 140
            },
            easing = if (hasOutcome) LinearOutSlowInEasing else FastOutLinearInEasing,
        ),
        label = "mini-game-feedback-alpha",
    )
    val feedbackScale by animateFloatAsState(
        targetValue = if (reducedMotion || hasOutcome) 1f else 0.97f,
        animationSpec = tween(160, easing = LinearOutSlowInEasing),
        label = "mini-game-feedback-scale",
    )
    val completed = progress.miniGameScores[game.roomId] == 1
    val totalScore = progress.miniGameScores.values.sum()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (validationErrors.isNotEmpty()) {
            Text(
                text = MiniGameText.INVALID_GAME_LABEL,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
            )
            return@Column
        }

        Text(
            text = game.title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = game.prompt,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (game.codeLines.isNotEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium,
            ) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(scrollState)
                        .padding(16.dp),
                ) {
                    Text(
                        text = game.codeLines.joinToString("\n"),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodyMedium,
                        softWrap = false,
                    )
                }
            }
        }

        Text(
            text = MiniGameText.scoreLine(totalScore, RoomId.entries.size),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (completed) {
            Text(
                text = MiniGameText.COMPLETED_LABEL,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        when (game) {
            is MiniGame.TokenOrder -> {
                Text(
                    text = if (tokenOrder.isEmpty()) {
                        MiniGameText.EMPTY_SELECTION_LABEL
                    } else {
                        tokenOrder.joinToString("\n")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                game.tokens.filterNot(tokenOrder::contains).forEach { token ->
                    Button(
                        onClick = {
                            tokenOrder = tokenOrder + token
                            outcome = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription =
                                    "${MiniGameText.TOKEN_ACCESSIBILITY_PREFIX}: $token"
                            },
                    ) {
                        Text(token, softWrap = true)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            outcome = evaluateTokenOrder(game, tokenOrder)
                            outcome?.let { if (it.isCorrect) onScore(game.roomId, it.points) }
                        },
                        enabled = tokenOrder.size == game.tokens.size,
                        modifier = Modifier.semantics {
                            contentDescription = MiniGameText.CHECK_ANSWER
                        },
                    ) {
                        Text(MiniGameText.CHECK_ANSWER)
                    }
                    Button(
                        onClick = {
                            tokenOrder = emptyList()
                            outcome = null
                        },
                        enabled = tokenOrder.isNotEmpty(),
                        modifier = Modifier.semantics {
                            contentDescription = MiniGameText.RESET_ANSWER
                        },
                    ) {
                        Text(MiniGameText.RESET_ANSWER)
                    }
                }
            }

            is MiniGame.MultipleChoice -> {
                game.options.forEachIndexed { index, option ->
                    Button(
                        onClick = {
                            outcome = evaluateChoice(game, index)
                            outcome?.let { if (it.isCorrect) onScore(game.roomId, it.points) }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = option },
                    ) {
                        Text(option, softWrap = true)
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 32.dp)
                .graphicsLayer {
                    alpha = feedbackAlpha
                    scaleX = feedbackScale
                    scaleY = feedbackScale
                },
        ) {
            outcome?.let { answer ->
                Text(
                    text = if (answer.isCorrect) {
                        MiniGameText.CORRECT_FEEDBACK
                    } else {
                        MiniGameText.INCORRECT_FEEDBACK
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (answer.isCorrect) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }
    }
}
