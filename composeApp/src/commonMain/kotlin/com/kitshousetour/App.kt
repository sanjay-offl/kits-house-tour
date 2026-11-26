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
import com.kitshousetour.domain.Badge
import com.kitshousetour.domain.Room
import com.kitshousetour.engine.GameProgress
import com.kitshousetour.engine.KitMood
import com.kitshousetour.engine.MoodState
import com.kitshousetour.engine.isFirstVisit
import com.kitshousetour.engine.onRoomEntered
import com.kitshousetour.engine.onTapKit
import com.kitshousetour.engine.onTimedMoodExpired
import com.kitshousetour.engine.visitRoom
import com.kitshousetour.ui.HouseScreen
import com.kitshousetour.ui.KitThemeTheme
import com.kitshousetour.ui.RoomScreen
import com.kitshousetour.ui.ThemeSettings
import kotlinx.coroutines.delay
import kotlin.time.TimeSource

@Composable
fun App() {
    var progress by remember {
        mutableStateOf(GameProgress(emptySet(), emptySet(), null, Badge.NONE))
    }
    var moodState by remember { mutableStateOf(MoodState(KitMood.Happy)) }
    var themeSettings by remember { mutableStateOf(ThemeSettings()) }
    var selectedRoom by remember { mutableStateOf<Room?>(null) }
    var speechText by remember { mutableStateOf<String?>(null) }
    var speechTrigger by remember { mutableIntStateOf(0) }
    var tapTrigger by remember { mutableIntStateOf(0) }
    val sessionClock = remember { TimeSource.Monotonic.markNow() }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            moodState = onTimedMoodExpired(
                moodState,
                sessionClock.elapsedNow().inWholeMilliseconds,
            )
        }
    }

    KitThemeTheme(settings = themeSettings) {
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
                        progress = progress,
                        mood = moodState.mood,
                        themeSettings = themeSettings,
                        speechText = speechText,
                        speechTrigger = speechTrigger,
                        tapTrigger = tapTrigger,
                        onRoomSelected = { selected ->
                            val firstVisit = isFirstVisit(progress, selected.id)
                            progress = visitRoom(progress, selected.id)
                            moodState = onRoomEntered(
                                firstVisit,
                                moodState,
                                sessionClock.elapsedNow().inWholeMilliseconds,
                            )
                            speechText = KitText.lineForMood(moodState.mood)
                            speechTrigger += 1
                            selectedRoom = selected
                        },
                        onKitTapped = {
                            moodState = onTapKit(moodState)
                            tapTrigger += 1
                            speechText = KitText.lineForMood(moodState.mood)
                            speechTrigger += 1
                        },
                        onThemeSettingsChanged = { themeSettings = it },
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
