package com.kitshousetour.content

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QuizTextTest {
    @Test
    fun quizContainsFiveValidQuestions() {
        assertEquals(5, QuizText.questions.size)
        assertTrue(QuizText.questions.all { it.correctIndex in it.options.indices })
    }

    @Test
    fun quizQuestionsCoverTheLessonsInOrder() {
        val prompts = QuizText.questions.map { it.prompt }

        assertTrue(prompts[0].contains("read-only"))
        assertTrue(prompts[1].contains("reassigned"))
        assertTrue(prompts[2].contains("copy()"))
        assertTrue(prompts[3].contains("sealed"))
        assertTrue(prompts[4].contains("blocking"))
    }

    @Test
    fun correctOptionsMatchTheKotlinConcepts() {
        assertEquals(
            listOf(
                "val",
                "val tries = 2",
                "A new instance, with selected properties changed.",
                "Every subtype, or an else branch.",
                "delay()",
            ),
            QuizText.questions.map { it.options[it.correctIndex] },
        )
    }
}
