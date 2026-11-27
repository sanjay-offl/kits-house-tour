package com.kitshousetour

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kitshousetour.content.KitText
import com.kitshousetour.domain.Room
import com.kitshousetour.engine.isFirstVisit
import com.kitshousetour.engine.isRoomUnlocked
import com.kitshousetour.engine.onRoomEntered
import com.kitshousetour.engine.onLockedRoomTapped as moodOnLockedRoomTapped
import com.kitshousetour.engine.onTapKit
import com.kitshousetour.engine.onTimedMoodExpired
import com.kitshousetour.engine.visitRoom
import com.kitshousetour.data.JsonProgressRepository
import com.kitshousetour.data.PlayerProgress
import com.kitshousetour.domain.RoomId
import com.kitshousetour.ui.HouseScreen
import com.kitshousetour.ui.KitThemeTheme
import com.kitshousetour.ui.RoomScreen
import com.kitshousetour.platform.SpeechSynthesizer
import kotlinx.coroutines.delay
import kotlin.time.Clock

@Composable
@OptIn(kotlin.time.ExperimentalTime::class)
fun App() {
    val progressRepository = remember { JsonProgressRepository() }
    var playerProgress by remember { mutableStateOf(progressRepository.loadOrCreate()) }
    var selectedRoom by remember { mutableStateOf<Room?>(null) }
    var currentRoomId by remember {
        mutableStateOf(
            RoomId.entries.lastOrNull { roomId ->
                roomId in playerProgress.game.visitedRooms &&
                    isRoomUnlocked(playerProgress.game, roomId)
            } ?: RoomId.LIVING_ROOM,
        )
    }
    var speechText by remember { mutableStateOf<String?>(null) }
    var speechTrigger by remember { mutableIntStateOf(0) }
    var tapTrigger by remember { mutableIntStateOf(0) }

    fun updateProgress(updated: PlayerProgress) {
        progressRepository.save(updated)
        playerProgress = updated
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            val updatedMood = onTimedMoodExpired(
                playerProgress.mood,
                Clock.System.now().toEpochMilliseconds(),
            )
            if (updatedMood != playerProgress.mood) {
                updateProgress(playerProgress.copy(mood = updatedMood))
            }
        }
    }

    KitThemeTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            AnimatedContent(
                targetState = selectedRoom,
                transitionSpec = {
                    (slideInHorizontally { it / 4 } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it / 4 } + fadeOut())
                },
                label = "house-room-transition",
            ) { room ->
                if (room == null) {
                    HouseScreen(
                        progress = playerProgress.game,
                        mood = playerProgress.mood.mood,
                        speechText = speechText,
                        speechTrigger = speechTrigger,
                        tapTrigger = tapTrigger,
                        currentRoomId = currentRoomId,
                        onRoomSelected = { selected ->
                            val firstVisit = isFirstVisit(playerProgress.game, selected.id)
                            val updatedMood = onRoomEntered(
                                firstVisit,
                                playerProgress.mood,
                                Clock.System.now().toEpochMilliseconds(),
                            )
                            updateProgress(
                                playerProgress.copy(
                                    game = visitRoom(playerProgress.game, selected.id),
                                    mood = updatedMood,
                                ),
                            )
                            speechText = KitText.lineForMood(updatedMood.mood)
                            speechTrigger += 1
                            currentRoomId = selected.id
                            selectedRoom = selected
                        },
                        onLockedRoomTapped = {
                            val updatedMood = moodOnLockedRoomTapped(
                                Clock.System.now().toEpochMilliseconds(),
                            )
                            updateProgress(playerProgress.copy(mood = updatedMood))
                            speechText = KitText.lineForMood(updatedMood.mood)
                            speechTrigger += 1
                            speechText?.let(SpeechSynthesizer::speak)
                        },
                        onKitTapped = {
                            val updatedMood = onTapKit(playerProgress.mood)
                            updateProgress(playerProgress.copy(mood = updatedMood))
                            tapTrigger += 1
                            speechText = KitText.lineForMood(updatedMood.mood)
                            speechTrigger += 1
                            speechText?.let(SpeechSynthesizer::speak)
                        },
                    )
                } else {
                    RoomScreen(
                        room = room,
                        onBack = {
                            selectedRoom = null
                            speechText = null
                        },
                    )
                }
            }
        }
    }
}
