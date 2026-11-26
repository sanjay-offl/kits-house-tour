package com.kitshousetour.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.kitshousetour.content.RoomText
import com.kitshousetour.domain.Room
import com.kitshousetour.engine.GameProgress
import com.kitshousetour.engine.KitMood

/** Shows the five tappable rooms and Kit in a compact or wide layout. */
@Composable
fun HouseScreen(
    progress: GameProgress,
    mood: KitMood,
    themeSettings: ThemeSettings,
    speechText: String?,
    speechTrigger: Int,
    tapTrigger: Int,
    onRoomSelected: (Room) -> Unit,
    onKitTapped: () -> Unit,
    onThemeSettingsChanged: (ThemeSettings) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWide = maxWidth >= 640.dp
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
            ThemeChooser(
                settings = themeSettings,
                onSettingsChanged = onThemeSettingsChanged,
            )
            Text(
                text = RoomText.progressLine(progress.visitedRooms.size, RoomText.rooms.size),
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
            if (isWide) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.weight(1.6f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(RoomText.rooms) { room ->
                            RoomCard(room = room, onClick = { onRoomSelected(room) })
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        speechText?.let { SpeechBubble(it, speechTrigger, Modifier.width(240.dp)) }
                        KitCharacter(
                            mood = mood,
                            themeSettings = themeSettings,
                            tapTrigger = tapTrigger,
                            onClick = onKitTapped,
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    RoomText.rooms.forEach { room ->
                        RoomCard(room = room, onClick = { onRoomSelected(room) })
                    }
                    speechText?.let { SpeechBubble(it, speechTrigger, Modifier.fillMaxWidth()) }
                    KitCharacter(
                        mood = mood,
                        themeSettings = themeSettings,
                        tapTrigger = tapTrigger,
                        onClick = onKitTapped,
                    )
                }
            }
        }
    }
}

@Composable
private fun RoomCard(room: Room, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = RoomText.accessibilityLabel(room) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = RoomText.iconFor(room.id),
                modifier = Modifier.size(36.dp),
                style = MaterialTheme.typography.headlineSmall,
            )
            Column {
                Text(
                    text = room.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = room.interactionPrompt,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
