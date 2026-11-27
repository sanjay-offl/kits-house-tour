package com.kitshousetour.engine

import com.kitshousetour.domain.MiniGame
import com.kitshousetour.domain.RoomId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MiniGameEngineTest {
    private val choiceGame = MiniGame.MultipleChoice(
        id = "study-test",
        roomId = RoomId.STUDY,
        title = "Choose",
        prompt = "Pick one.",
        codeLines = emptyList(),
        options = listOf("val", "var"),
        correctIndex = 0,
    )

    private val tokenGame = MiniGame.TokenOrder(
        id = "living-test",
        roomId = RoomId.LIVING_ROOM,
        title = "Order",
        prompt = "Arrange the lines.",
        codeLines = emptyList(),
        tokens = listOf("println(\"Hi\")", "}", "fun main() {"),
        correctOrder = listOf("fun main() {", "println(\"Hi\")", "}"),
    )

    @Test
    fun correctChoiceEarnsOnePoint() {
        assertEquals(MiniGameOutcome(isCorrect = true, points = 1), evaluateChoice(choiceGame, 0))
    }

    @Test
    fun incorrectChoiceEarnsNoPoints() {
        assertEquals(MiniGameOutcome(isCorrect = false, points = 0), evaluateChoice(choiceGame, 1))
    }

    @Test
    fun choiceRejectsAnIndexOutsideTheOptionList() {
        assertFailsWith<IllegalArgumentException> { evaluateChoice(choiceGame, 2) }
        assertFailsWith<IllegalArgumentException> { evaluateChoice(choiceGame, -1) }
    }

    @Test
    fun correctTokenOrderEarnsOnePoint() {
        assertTrue(evaluateTokenOrder(tokenGame, tokenGame.correctOrder).isCorrect)
        assertEquals(1, evaluateTokenOrder(tokenGame, tokenGame.correctOrder).points)
    }

    @Test
    fun wrongTokenOrderIsAValidLoss() {
        val outcome = evaluateTokenOrder(tokenGame, tokenGame.tokens)

        assertFalse(outcome.isCorrect)
        assertEquals(0, outcome.points)
    }

    @Test
    fun tokenAnswerMustUseEveryAvailableTokenExactlyOnce() {
        assertFailsWith<IllegalArgumentException> {
            evaluateTokenOrder(tokenGame, listOf("fun main() {", "}", "}"))
        }
    }

    @Test
    fun emptyChoiceListIsRejectedByValidation() {
        val invalidGame = choiceGame.copy(options = emptyList(), correctIndex = 0)

        assertTrue(validateMiniGame(invalidGame).isNotEmpty())
        assertFailsWith<IllegalArgumentException> { evaluateChoice(invalidGame, 0) }
    }

    @Test
    fun malformedTokenInventoryIsRejectedByValidation() {
        val invalidGame = tokenGame.copy(tokens = listOf("line", "line"))

        assertTrue(validateMiniGame(invalidGame).isNotEmpty())
        assertFailsWith<IllegalArgumentException> {
            evaluateTokenOrder(invalidGame, invalidGame.correctOrder)
        }
    }
}
