package com.kitshousetour.content

import com.kitshousetour.domain.MiniGame
import com.kitshousetour.domain.RoomId

/** Original room mini-games and the short labels used by their shared play screen. */
object MiniGameText {
    val games: List<MiniGame> = listOf(
        MiniGame.TokenOrder(
            id = "living-room-launch",
            roomId = RoomId.LIVING_ROOM,
            title = "Build the greeting",
            prompt = "Tap each line once to put the Kotlin program in order.",
            codeLines = emptyList(),
            tokens = listOf("println(\"Hello, Kit!\")", "}", "fun main() {"),
            correctOrder = listOf("fun main() {", "println(\"Hello, Kit!\")", "}"),
        ),
        MiniGame.MultipleChoice(
            id = "study-steady-value",
            roomId = RoomId.STUDY,
            title = "Find the steady value",
            prompt = "Which declaration cannot be reassigned?",
            codeLines = listOf("val roomName = \"Study\""),
            options = listOf("var roomName", "val roomName", "fun roomName"),
            correctIndex = 1,
        ),
        MiniGame.MultipleChoice(
            id = "dressing-copy-look",
            roomId = RoomId.DRESSING,
            title = "Copy the new look",
            prompt = "What does this copy call give you?",
            codeLines = listOf(
                "data class Outfit(val color: String)",
                "val newOutfit = outfit.copy(color = \"purple\")",
            ),
            options = listOf(
                "The original outfit is changed in place.",
                "A new outfit with the changed color.",
                "A plain string containing the color.",
            ),
            correctIndex = 1,
        ),
        MiniGame.MultipleChoice(
            id = "kitchen-sealed-menu",
            roomId = RoomId.KITCHEN,
            title = "Complete the sealed menu",
            prompt = "What must a when expression cover for a sealed type?",
            codeLines = listOf(
                "sealed interface Meal",
                "fun label(meal: Meal) = when (meal) {",
                "    Soup -> \"warm\"",
                "    Salad -> \"crunchy\"",
                "}",
            ),
            options = listOf(
                "Only the most common subtype.",
                "Every subtype, or an else branch.",
                "A default value before the when.",
            ),
            correctIndex = 1,
        ),
        MiniGame.MultipleChoice(
            id = "bedroom-wake-up",
            roomId = RoomId.BEDROOM,
            title = "Predict the wake-up",
            prompt = "How does delay wait inside this function?",
            codeLines = listOf(
                "suspend fun wake() {",
                "    delay(1_000)",
                "    println(\"Good morning, Kit! Time to explore the house again.\")",
                "}",
            ),
            options = listOf(
                "It pauses the coroutine without blocking its thread.",
                "It blocks the whole app for one second.",
                "It starts a new thread automatically.",
            ),
            correctIndex = 0,
        ),
    )

    private val gamesByRoom = games.associateBy { it.roomId }

    const val CHECK_ANSWER = "Check order"
    const val RESET_ANSWER = "Clear order"
    const val SCORE_LABEL = "Mini-game points"
    const val SCORE_SEPARATOR = " / "
    const val CORRECT_FEEDBACK = "That's right! You earned a point."
    const val INCORRECT_FEEDBACK = "Not quite. Try again."
    const val COMPLETED_LABEL = "Game completed"
    const val INVALID_GAME_LABEL = "This mini-game is unavailable."
    const val EMPTY_SELECTION_LABEL = "Choose or arrange an answer to play."
    const val TOKEN_ACCESSIBILITY_PREFIX = "Add code token"

    /** Returns the authored mini-game for [roomId], or fails if room content is missing. */
    fun gameFor(roomId: RoomId): MiniGame = checkNotNull(gamesByRoom[roomId]) {
        "No mini-game is defined for $roomId."
    }

    /** Formats the cumulative room-game score with its maximum. */
    fun scoreLine(score: Int, maximum: Int): String = "$SCORE_LABEL: $score$SCORE_SEPARATOR$maximum"
}
