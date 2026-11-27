package com.kitshousetour.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.kitshousetour.content.QuizText
import com.kitshousetour.domain.Badge
import com.kitshousetour.domain.QuizQuestion
import com.kitshousetour.engine.calculateQuizScore
import com.kitshousetour.engine.scoreQuiz

/** Presents one quiz question at a time and reports the completed score. */
@Composable
fun QuizScreen(
    questions: List<QuizQuestion>,
    onFinished: (Int) -> Unit,
    onBack: () -> Unit,
) {
    var questionIndex by remember(questions) { mutableIntStateOf(0) }
    var answers by remember(questions) {
        mutableStateOf(List<Int?>(questions.size) { null })
    }
    var finalScore by remember(questions) { mutableStateOf<Int?>(null) }

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
                text = QuizText.TITLE,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            if (questions.isEmpty()) {
                Text(
                    text = QuizText.EMPTY_QUIZ,
                    style = MaterialTheme.typography.bodyLarge,
                )
                BackButton(onBack)
            } else if (finalScore != null) {
                val score = checkNotNull(finalScore)
                Text(
                    text = QuizText.RESULT_TITLE,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = QuizText.scoreLine(score, questions.size),
                    style = MaterialTheme.typography.bodyLarge,
                )
                val earnedBadge: Badge = scoreQuiz(score)
                Text(
                    text = QuizText.BADGE_RESULT_PREFIX + QuizText.badgeName(earnedBadge),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Button(
                    onClick = {
                        questionIndex = 0
                        answers = List<Int?>(questions.size) { null }
                        finalScore = null
                    },
                    modifier = Modifier.semantics {
                        contentDescription = QuizText.RETRY_DESCRIPTION
                    },
                ) {
                    Text(QuizText.RETRY_QUIZ)
                }
                BackButton(onBack)
            } else {
                val question = questions[questionIndex]
                Text(
                    text = QuizText.questionCounter(questionIndex + 1, questions.size),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = question.prompt,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                question.options.forEachIndexed { index, option ->
                    val isSelected = answers[questionIndex] == index
                    Button(
                        onClick = {
                            answers = answers.toMutableList().also { it[questionIndex] = index }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = QuizText.answerDescription(option)
                            },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                            contentColor = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        ),
                    ) {
                        Text(option, softWrap = true)
                    }
                }
                Button(
                    onClick = {
                        if (questionIndex == questions.lastIndex) {
                            val score = calculateQuizScore(questions, answers)
                            finalScore = score
                            onFinished(score)
                        } else {
                            questionIndex += 1
                        }
                    },
                    enabled = answers[questionIndex] != null,
                    modifier = Modifier.semantics {
                        contentDescription = if (questionIndex == questions.lastIndex) {
                            QuizText.FINISH_DESCRIPTION
                        } else {
                            QuizText.CONTINUE_DESCRIPTION
                        }
                    },
                ) {
                    Text(
                        if (questionIndex == questions.lastIndex) {
                            QuizText.FINISH_QUIZ
                        } else {
                            QuizText.NEXT_QUESTION
                        },
                    )
                }
                BackButton(onBack)
            }
        }
    }
}

@Composable
private fun BackButton(onBack: () -> Unit) {
    Button(
        onClick = onBack,
        modifier = Modifier.semantics {
            contentDescription = QuizText.BACK_DESCRIPTION
        },
    ) {
        Text(QuizText.BACK_TO_HOUSE)
    }
}
