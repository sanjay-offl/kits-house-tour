package com.kitshousetour

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import com.kitshousetour.data.JsonProgressRepository
import com.kitshousetour.data.PlayerProgress
import com.kitshousetour.content.KitText
import com.kitshousetour.content.LessonText
import com.kitshousetour.content.NavigationText
import com.kitshousetour.content.QuizText
import com.kitshousetour.domain.RoomId
import com.kitshousetour.domain.Room
import com.kitshousetour.engine.completeLesson
import com.kitshousetour.engine.isFirstVisit
import com.kitshousetour.engine.isQuizUnlocked
import com.kitshousetour.engine.isRoomUnlocked
import com.kitshousetour.engine.onLessonFinished
import com.kitshousetour.engine.onRoomEntered
import com.kitshousetour.engine.onLockedRoomTapped as moodOnLockedRoomTapped
import com.kitshousetour.engine.onTapKit
import com.kitshousetour.engine.onTimedMoodExpired
import com.kitshousetour.engine.recordMiniGameScore
import com.kitshousetour.engine.recordQuizScore
import com.kitshousetour.engine.visitRoom
import com.kitshousetour.platform.SpeechSynthesizer
import com.kitshousetour.ui.BadgeScreen
import com.kitshousetour.ui.HouseScreen
import com.kitshousetour.ui.KitThemeTheme
import com.kitshousetour.ui.QuizScreen
import com.kitshousetour.ui.RoomScreen
import com.kitshousetour.ui.SettingsScreen
import kotlinx.coroutines.delay
import kotlin.time.Clock

@Composable
@OptIn(kotlin.time.ExperimentalTime::class)
fun App() {
    val progressRepository = remember { JsonProgressRepository() }
    var playerProgress by remember { mutableStateOf(progressRepository.loadOrCreate()) }
    var destination by remember { mutableStateOf<AppDestination>(AppDestination.House) }
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

    fun speakIfEnabled(text: String) {
        if (!playerProgress.settings.speechMuted) {
            SpeechSynthesizer.speak(text)
        }
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
                targetState = destination,
                transitionSpec = {
                    if (playerProgress.settings.reducedMotion) {
                        EnterTransition.None togetherWith ExitTransition.None
                    } else {
                        (slideInHorizontally { it / 4 } + fadeIn()) togetherWith
                            (slideOutHorizontally { -it / 4 } + fadeOut())
                    }
                },
                label = NavigationText.TRANSITION_LABEL,
            ) { screen ->
                when (screen) {
                    AppDestination.House -> HouseScreen(
                        progress = playerProgress.game,
                        mood = playerProgress.mood.mood,
                        speechText = speechText,
                        speechTrigger = speechTrigger,
                        tapTrigger = tapTrigger,
                        currentRoomId = currentRoomId,
                        quizUnlocked = isQuizUnlocked(playerProgress.game),
                        onOpenQuiz = { destination = AppDestination.Quiz },
                        onOpenBadges = { destination = AppDestination.Badges },
                        onOpenSettings = { destination = AppDestination.Settings },
                        onRoomSelected = { selected ->
                            val updatedMood = onRoomEntered(
                                isFirstVisit(playerProgress.game, selected.id),
                                playerProgress.mood,
                                Clock.System.now().toEpochMilliseconds(),
                            )
                            updateProgress(
                                playerProgress.copy(
                                    game = visitRoom(playerProgress.game, selected.id),
                                    mood = updatedMood,
                                ),
                            )
                            val line = KitText.lineForMood(updatedMood.mood)
                            speechText = line
                            speechTrigger += 1
                            speakIfEnabled(line)
                            currentRoomId = selected.id
                            destination = AppDestination.RoomDestination(selected)
                        },
                        onLockedRoomTapped = {
                            val updatedMood = moodOnLockedRoomTapped(
                                Clock.System.now().toEpochMilliseconds(),
                            )
                            updateProgress(playerProgress.copy(mood = updatedMood))
                            val line = KitText.lineForMood(updatedMood.mood)
                            speechText = line
                            speechTrigger += 1
                            speakIfEnabled(line)
                        },
                        onKitTapped = {
                            val updatedMood = onTapKit(playerProgress.mood)
                            updateProgress(playerProgress.copy(mood = updatedMood))
                            tapTrigger += 1
                            val line = KitText.lineForMood(updatedMood.mood)
                            speechText = line
                            speechTrigger += 1
                            speakIfEnabled(line)
                        },
                    )

                    is AppDestination.RoomDestination -> RoomScreen(
                        room = screen.room,
                        progress = playerProgress.game,
                        isLessonCompleted = screen.room.id in playerProgress.game.completedLessons,
                        onLessonComplete = {
                            val lesson = LessonText.lessonFor(screen.room.id)
                            val updatedMood = onLessonFinished(
                                playerProgress.mood,
                                Clock.System.now().toEpochMilliseconds(),
                            )
                            updateProgress(
                                playerProgress.copy(
                                    game = completeLesson(playerProgress.game, screen.room.id),
                                    mood = updatedMood,
                                ),
                            )
                            speakIfEnabled(lesson.kitSays)
                        },
                        onReadAloud = ::speakIfEnabled,
                        onMiniGameScore = { roomId, score ->
                            updateProgress(
                                playerProgress.copy(
                                    game = recordMiniGameScore(
                                        playerProgress.game,
                                        roomId,
                                        score,
                                    ),
                                ),
                            )
                        },
                        onBack = { destination = AppDestination.House },
                    )

                    AppDestination.Quiz -> QuizScreen(
                        questions = QuizText.questions,
                        onFinished = { correctCount ->
                            updateProgress(
                                playerProgress.copy(
                                    game = recordQuizScore(playerProgress.game, correctCount),
                                ),
                            )
                        },
                        onBack = { destination = AppDestination.House },
                    )

                    AppDestination.Badges -> BadgeScreen(
                        earnedBadge = playerProgress.game.badge,
                        onBack = { destination = AppDestination.House },
                    )

                    AppDestination.Settings -> SettingsScreen(
                        settings = playerProgress.settings,
                        onSettingsChanged = { settings ->
                            updateProgress(playerProgress.copy(settings = settings))
                        },
                        onBack = { destination = AppDestination.House },
                    )
                }
            }
        }
    }
}

private sealed interface AppDestination {
    data object House : AppDestination
    data class RoomDestination(val room: Room) : AppDestination
    data object Quiz : AppDestination
    data object Badges : AppDestination
    data object Settings : AppDestination
}
