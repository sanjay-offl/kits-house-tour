package com.kitshousetour.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.kitshousetour.content.RoomText

/** Provides accessible controls for choosing a palette and its dark variant. */
@Composable
fun ThemeChooser(settings: ThemeSettings, onSettingsChanged: (ThemeSettings) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = RoomText.THEME_CHOOSER_DESCRIPTION },
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            ThemePalette.entries.forEach { palette ->
                val label = paletteLabel(palette)
                FilterChip(
                    selected = settings.palette == palette,
                    onClick = { onSettingsChanged(settings.copy(palette = palette)) },
                    modifier = Modifier
                        .weight(1f)
                        .semantics { contentDescription = paletteDescription(palette) },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                        )
                    },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(themeColors(ThemeSettings(palette)).primary),
                        )
                    },
                )
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = RoomText.DARK_MODE_LABEL,
                style = MaterialTheme.typography.labelLarge,
            )
            Switch(
                checked = settings.isDark,
                onCheckedChange = { onSettingsChanged(toggleDarkMode(settings)) },
                modifier = Modifier.semantics {
                    contentDescription = RoomText.DARK_MODE_DESCRIPTION
                },
            )
        }
    }
}

private fun paletteLabel(palette: ThemePalette): String = when (palette) {
    ThemePalette.PURPLE -> RoomText.PURPLE_PALETTE
    ThemePalette.WHITE -> RoomText.WHITE_PALETTE
    ThemePalette.BLACK -> RoomText.BLACK_PALETTE
    ThemePalette.GREY -> RoomText.GREY_PALETTE
}

private fun paletteDescription(palette: ThemePalette): String = when (palette) {
    ThemePalette.PURPLE -> RoomText.SELECT_PURPLE_PALETTE
    ThemePalette.WHITE -> RoomText.SELECT_WHITE_PALETTE
    ThemePalette.BLACK -> RoomText.SELECT_BLACK_PALETTE
    ThemePalette.GREY -> RoomText.SELECT_GREY_PALETTE
}
