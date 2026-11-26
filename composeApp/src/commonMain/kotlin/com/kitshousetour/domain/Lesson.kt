package com.kitshousetour.domain

/** A short Kotlin lesson associated with a room. */
data class Lesson(
    val id: String,
    val roomId: RoomId,
    val title: String,
    val explanation: String,
    val snippet: String,
    val kitSays: String,
)

/**
 * Returns validation errors when lesson text exceeds the app's display limits.
 * Explanations must be under 60 words and snippets under 8 lines.
 */
fun Lesson.validationErrors(): List<String> = buildList {
    val wordCount = explanation.trim().split(Regex("\\s+")).count { it.isNotEmpty() }
    if (wordCount >= 60) {
        add("Lesson explanation must contain fewer than 60 words.")
    }

    if (snippet.lineSequence().count() >= 8) {
        add("Lesson snippet must contain fewer than 8 lines.")
    }
}
