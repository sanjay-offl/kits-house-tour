package com.kitshousetour.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.kitshousetour.content.SettingsText
import com.kitshousetour.data.PlayerSettings

/** Lets the player update persisted speech and reduced-motion preferences. */
@Composable
fun SettingsScreen(
    settings: PlayerSettings,
    onSettingsChanged: (PlayerSettings) -> Unit,
    onBack: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = SettingsText.TITLE,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            SettingToggle(
                title = SettingsText.SPEECH_LABEL,
                description = SettingsText.SPEECH_DESCRIPTION,
                controlDescription = SettingsText.MUTE_DESCRIPTION,
                checked = !settings.speechMuted,
                onCheckedChange = { enabled ->
                    onSettingsChanged(settings.copy(speechMuted = !enabled))
                },
            )
            SettingToggle(
                title = SettingsText.REDUCED_MOTION_LABEL,
                description = SettingsText.REDUCED_MOTION_DESCRIPTION,
                controlDescription = SettingsText.REDUCED_MOTION_LABEL,
                checked = settings.reducedMotion,
                onCheckedChange = { enabled ->
                    onSettingsChanged(settings.copy(reducedMotion = enabled))
                },
            )
            Button(
                onClick = onBack,
                modifier = Modifier.semantics {
                    contentDescription = SettingsText.BACK_DESCRIPTION
                },
            ) {
                Text(SettingsText.BACK_BUTTON)
            }
        }
    }
}

@Composable
private fun SettingToggle(
    title: String,
    description: String,
    controlDescription: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.semantics {
                contentDescription = controlDescription
            },
        )
    }
}
