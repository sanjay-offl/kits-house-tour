package com.kitshousetour.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.kitshousetour.content.LessonText
import com.kitshousetour.content.MiniGameText
import com.kitshousetour.content.RoomText
import com.kitshousetour.domain.Room
import com.kitshousetour.domain.RoomId
import com.kitshousetour.engine.GameProgress

/** Shows the room lesson, its mini-game, and a route back to the house. */
@Composable
fun RoomScreen(
    room: Room,
    progress: GameProgress,
    isLessonCompleted: Boolean,
    reducedMotion: Boolean,
    onLessonComplete: () -> Unit,
    onReadAloud: (String) -> Unit,
    onMiniGameScore: (RoomId, Int) -> Unit,
    onBack: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "${RoomText.iconFor(room.id)} ${room.title}",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            LessonCard(
                lesson = LessonText.lessonFor(room.id),
                isCompleted = isLessonCompleted,
                onComplete = onLessonComplete,
                onReadAloud = onReadAloud,
                modifier = Modifier.fillMaxWidth(),
            )
            MiniGameScreen(
                game = MiniGameText.gameFor(room.id),
                progress = progress,
                reducedMotion = reducedMotion,
                onScore = onMiniGameScore,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp),
            )
            Button(
                onClick = onBack,
                modifier = Modifier.semantics {
                    contentDescription = RoomText.BACK_BUTTON_DESCRIPTION
                },
            ) {
                Text(RoomText.BACK_BUTTON)
            }
        }
    }
}
