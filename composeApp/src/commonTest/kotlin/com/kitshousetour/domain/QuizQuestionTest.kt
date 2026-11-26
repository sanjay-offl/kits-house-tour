package com.kitshousetour.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class QuizQuestionTest {
    @Test
    fun validQuestionAcceptsCorrectIndexAtStart() {
        val question = QuizQuestion("q1", "Pick one", listOf("A", "B"), 0)

        assertEquals("A", question.options[question.correctIndex])
    }

    @Test
    fun validQuestionAcceptsCorrectIndexAtEnd() {
        val question = QuizQuestion("q2", "Pick one", listOf("A", "B", "C", "D"), 3)

        assertEquals("D", question.options[question.correctIndex])
    }

    @Test
    fun fewerThanTwoOptionsAreRejected() {
        assertFailsWith<IllegalArgumentException> {
            QuizQuestion("q3", "Pick one", listOf("A"), 0)
        }
    }

    @Test
    fun moreThanFourOptionsAreRejected() {
        assertFailsWith<IllegalArgumentException> {
            QuizQuestion("q4", "Pick one", listOf("A", "B", "C", "D", "E"), 0)
        }
    }

    @Test
    fun negativeCorrectIndexIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            QuizQuestion("q5", "Pick one", listOf("A", "B"), -1)
        }
    }

    @Test
    fun correctIndexAtOptionCountIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            QuizQuestion("q6", "Pick one", listOf("A", "B"), 2)
        }
    }
}
