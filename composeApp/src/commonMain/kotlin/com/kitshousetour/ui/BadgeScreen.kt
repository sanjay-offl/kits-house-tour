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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.kitshousetour.content.QuizText
import com.kitshousetour.domain.Badge
import com.kitshousetour.engine.hasEarnedBadge

/** Shows earned and locked badge tiers with their unlock requirements. */
@Composable
fun BadgeScreen(earnedBadge: Badge, onBack: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = QuizText.BADGE_SCREEN_TITLE,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Badge.entries.filterNot { it == Badge.NONE }.forEach { badge ->
                val isEarned = hasEarnedBadge(earnedBadge, badge)
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = QuizText.badgeName(badge),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                text = QuizText.badgeRequirement(badge),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Text(
                            text = QuizText.badgeStatus(isEarned),
                            modifier = Modifier.semantics {
                                contentDescription = QuizText.badgeStatus(isEarned)
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isEarned) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                }
            }
            Button(
                onClick = onBack,
                modifier = Modifier.semantics {
                    contentDescription = QuizText.BACK_TO_HOUSE
                },
            ) {
                Text(QuizText.BACK_TO_HOUSE)
            }
        }
    }
}
