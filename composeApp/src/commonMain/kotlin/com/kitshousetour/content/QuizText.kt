package com.kitshousetour.content

import com.kitshousetour.domain.Badge
import com.kitshousetour.domain.QuizQuestion

/** Quiz questions and text for quiz and badge screens. */
object QuizText {
    val questions = listOf(
        QuizQuestion(
            id = "read-only-keyword",
            prompt = "Which keyword declares a read-only value?",
            options = listOf("val", "var", "fun"),
            correctIndex = 0,
        ),
        QuizQuestion(
            id = "cannot-reassign",
            prompt = "Which declaration cannot be reassigned?",
            options = listOf("var tries = 2", "val tries = 2", "fun tries = 2"),
            correctIndex = 1,
        ),
        QuizQuestion(
            id = "copy-result",
            prompt = "What does copy() return for a data class?",
            options = listOf(
                "The same instance with every property changed.",
                "A new instance, with selected properties changed.",
                "A string containing the old property values.",
            ),
            correctIndex = 1,
        ),
        QuizQuestion(
            id = "sealed-when",
            prompt = "What must a when expression on a sealed type cover?",
            options = listOf(
                "Every subtype, or an else branch.",
                "Only the subtype used most often.",
                "A return statement in every branch.",
            ),
            correctIndex = 0,
        ),
        QuizQuestion(
            id = "nonblocking-delay",
            prompt = "Which function pauses without blocking a thread?",
            options = listOf("Thread.sleep()", "delay()", "println()"),
            correctIndex = 1,
        ),
    )

    const val TITLE = "Kotlin house quiz"
    const val EMPTY_QUIZ = "The quiz is not available yet."
    const val NEXT_QUESTION = "Next question"
    const val FINISH_QUIZ = "Finish quiz"
    const val RETRY_QUIZ = "Try the quiz again"
    const val BACK_TO_HOUSE = "Back to the house"
    const val RESULT_TITLE = "Your result"
    const val BADGE_RESULT_PREFIX = "Badge earned: "
    const val NO_BADGE = "No badge earned yet"
    const val BADGE_SCREEN_TITLE = "Your badges"
    const val EARNED_STATUS = "Earned"
    const val LOCKED_STATUS = "Locked"
    const val BACK_DESCRIPTION = "Return to the house from the quiz"
    const val CONTINUE_DESCRIPTION = "Continue to the next quiz question"
    const val FINISH_DESCRIPTION = "Submit the completed quiz"
    const val RETRY_DESCRIPTION = "Restart the quiz"

    /** Formats the visible position of the current quiz question. */
    fun questionCounter(current: Int, total: Int): String = "Question $current of $total"

    /** Formats the final quiz score. */
    fun scoreLine(correct: Int, total: Int): String = "Correct answers: $correct of $total"

    /** Returns the accessibility description for choosing [option]. */
    fun answerDescription(option: String): String = "Select answer: $option"

    /** Returns the display name for a badge. */
    fun badgeName(badge: Badge): String = when (badge) {
        Badge.NONE -> NO_BADGE
        Badge.BRONZE -> "Bronze"
        Badge.SILVER -> "Silver"
        Badge.GOLD -> "Gold"
    }

    /** Returns the visible status for an earned or locked badge. */
    fun badgeStatus(isEarned: Boolean): String =
        if (isEarned) EARNED_STATUS else LOCKED_STATUS

    /** Describes the mini-game and quiz thresholds for [badge]. */
    fun badgeRequirement(badge: Badge): String = when (badge) {
        Badge.NONE -> NO_BADGE
        Badge.BRONZE -> "Earn 1 mini-game point or answer 3 quiz questions correctly."
        Badge.SILVER -> "Earn 3 mini-game points or answer 4 quiz questions correctly."
        Badge.GOLD -> "Earn all 5 mini-game points or answer all 5 quiz questions correctly."
    }
}
