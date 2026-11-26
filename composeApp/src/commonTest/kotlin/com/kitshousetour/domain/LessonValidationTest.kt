package com.kitshousetour.domain

import com.kitshousetour.domain.RoomId.LIVING_ROOM
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class LessonValidationTest {
    @Test
    fun validLessonHasNoValidationErrors() {
        val lesson = Lesson(
            id = "hello-kotlin",
            roomId = LIVING_ROOM,
            title = "Hello, Kotlin",
            explanation = "A read-only value keeps its value once assigned.",
            snippet = "fun main() {\n    val greeting = \"Hello\"\n}",
            kitSays = "A val stays put!",
        )

        assertTrue(lesson.validationErrors().isEmpty())
    }

    @Test
    fun explanationWith60WordsIsRejected() {
        val lesson = validLesson().copy(explanation = List(60) { "word" }.joinToString(" "))

        assertTrue(lesson.validationErrors().any { "explanation" in it })
    }

    @Test
    fun snippetWithEightLinesIsRejected() {
        val lesson = validLesson().copy(snippet = List(8) { "// line" }.joinToString("\n"))

        assertTrue(lesson.validationErrors().any { "snippet" in it })
    }

    @Test
    fun explanationUnder60WordsAndSnippetUnderEightLinesAreAccepted() {
        val lesson = validLesson().copy(
            explanation = List(59) { "word" }.joinToString(" "),
            snippet = List(7) { "// line" }.joinToString("\n"),
        )

        assertFalse(lesson.validationErrors().isNotEmpty())
    }

    private fun validLesson() = Lesson(
        id = "hello-kotlin",
        roomId = LIVING_ROOM,
        title = "Hello, Kotlin",
        explanation = "A read-only value keeps its value once assigned.",
        snippet = "fun main() {\n    val greeting = \"Hello\"\n}",
        kitSays = "A val stays put!",
    )
}
