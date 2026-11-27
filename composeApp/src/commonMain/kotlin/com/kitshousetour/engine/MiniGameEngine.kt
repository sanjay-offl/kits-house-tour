package com.kitshousetour.engine

import com.kitshousetour.domain.MiniGame

/** The result of checking a single mini-game answer. */
data class MiniGameOutcome(
    val isCorrect: Boolean,
    val points: Int,
)

/** Returns validation errors for malformed mini-game content. */
fun validateMiniGame(game: MiniGame): List<String> {
    val errors = mutableListOf<String>()
    if (game.id.isBlank()) errors += "Game id must not be blank."
    if (game.title.isBlank()) errors += "Game title must not be blank."
    if (game.prompt.isBlank()) errors += "Game prompt must not be blank."
    if (game.codeLines.any(String::isBlank)) errors += "Code lines must not be blank."

    when (game) {
        is MiniGame.TokenOrder -> {
            if (game.tokens.size !in 2..8) errors += "Token games must contain 2 to 8 tokens."
            if (game.tokens.any(String::isBlank)) errors += "Tokens must not be blank."
            if (game.tokens.size != game.tokens.distinct().size) errors += "Tokens must be unique."
            if (
                game.correctOrder.size != game.tokens.size ||
                game.correctOrder.toSet() != game.tokens.toSet()
            ) {
                errors += "The correct order must contain the supplied tokens exactly once."
            }
        }

        is MiniGame.MultipleChoice -> {
            if (game.options.size !in 2..4) errors += "Choice games must contain 2 to 4 options."
            if (game.options.any(String::isBlank)) errors += "Options must not be blank."
            if (game.correctIndex !in game.options.indices) {
                errors += "Correct answer index must refer to an option."
            }
        }
    }

    return errors
}

/** Scores a selected option as one point for a correct answer and zero for a wrong answer. */
fun evaluateChoice(game: MiniGame.MultipleChoice, selectedIndex: Int): MiniGameOutcome {
    requireValidGame(game)
    require(selectedIndex in game.options.indices) { "Selected option index is out of range." }
    val isCorrect = selectedIndex == game.correctIndex
    return MiniGameOutcome(isCorrect, if (isCorrect) 1 else 0)
}

/** Scores an ordering after requiring the answer to use every supplied token exactly once. */
fun evaluateTokenOrder(
    game: MiniGame.TokenOrder,
    answer: List<String>,
): MiniGameOutcome {
    requireValidGame(game)
    require(answer.size == game.tokens.size && answer.toSet() == game.tokens.toSet()) {
        "Answer must use each supplied token exactly once."
    }
    val isCorrect = answer == game.correctOrder
    return MiniGameOutcome(isCorrect, if (isCorrect) 1 else 0)
}

private fun requireValidGame(game: MiniGame) {
    val errors = validateMiniGame(game)
    require(errors.isEmpty()) { errors.joinToString(" ") }
}
