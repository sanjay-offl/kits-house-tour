package com.kitshousetour.domain

/** Describes one room's coding mini-game and the input shape it accepts. */
sealed interface MiniGame {
    val id: String
    val roomId: RoomId
    val title: String
    val prompt: String
    val codeLines: List<String>

    /** A game where each supplied token must be used once in the correct order. */
    data class TokenOrder(
        override val id: String,
        override val roomId: RoomId,
        override val title: String,
        override val prompt: String,
        override val codeLines: List<String>,
        val tokens: List<String>,
        val correctOrder: List<String>,
    ) : MiniGame

    /** A game where the player selects one answer from a short list of choices. */
    data class MultipleChoice(
        override val id: String,
        override val roomId: RoomId,
        override val title: String,
        override val prompt: String,
        override val codeLines: List<String>,
        val options: List<String>,
        val correctIndex: Int,
    ) : MiniGame
}
