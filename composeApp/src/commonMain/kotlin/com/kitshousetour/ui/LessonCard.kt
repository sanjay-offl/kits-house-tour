package com.kitshousetour.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.kitshousetour.content.LessonText
import com.kitshousetour.domain.Lesson

/** Shows the lesson for one room and exposes completion and read-aloud actions. */
@Composable
fun LessonCard(
    lesson: Lesson,
    isCompleted: Boolean,
    onComplete: () -> Unit,
    onReadAloud: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = lesson.explanation,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
            ) {
                Text(
                    text = lesson.snippet,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodyMedium,
                    softWrap = false,
                )
            }
            Text(
                text = "${LessonText.KIT_SAYS_LABEL}: ${lesson.kitSays}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(
                onClick = { onReadAloud(lesson.kitSays) },
                modifier = Modifier.semantics {
                    contentDescription = LessonText.READ_ALOUD
                },
            ) {
                Text(LessonText.READ_ALOUD)
            }
            if (isCompleted) {
                Text(
                    text = LessonText.LESSON_COMPLETED,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            } else {
                Button(
                    onClick = onComplete,
                    modifier = Modifier.semantics {
                        contentDescription = LessonText.COMPLETE_LESSON
                    },
                ) {
                    Text(LessonText.COMPLETE_LESSON)
                }
            }
        }
    }
}
