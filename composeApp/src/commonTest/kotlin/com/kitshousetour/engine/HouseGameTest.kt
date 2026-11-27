package com.kitshousetour.engine

import com.kitshousetour.domain.Badge
import com.kitshousetour.domain.QuizQuestion
import com.kitshousetour.domain.RoomId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class HouseGameTest {
    @Test
    fun quizIsLockedWithNoRoomsVisited() {
        assertFalse(isQuizUnlocked(GameProgress(emptySet(), emptySet(), null, Badge.NONE)))
    }

    @Test
    fun quizIsLockedWithFourRoomsVisited() {
        val visited = RoomId.entries.filterNot { it == RoomId.BEDROOM }.toSet()
        val progress = GameProgress(visited, emptySet(), null, Badge.NONE)

        assertFalse(isQuizUnlocked(progress))
    }

    @Test
    fun quizUnlocksOnlyWhenAllFiveRoomsAreVisited() {
        val progress = GameProgress(RoomId.entries.toSet(), emptySet(), null, Badge.NONE)

        assertTrue(isQuizUnlocked(progress))
    }

    @Test
    fun visitingRoomAddsItWithoutChangingOtherProgress() {
        val initial = GameProgress(emptySet(), emptySet(), null, Badge.NONE)

        assertEquals(initial.copy(visitedRooms = setOf(RoomId.STUDY)), visitRoom(initial, RoomId.STUDY))
    }

    @Test
    fun visitingSameRoomTwiceDoesNotDuplicateIt() {
        val once = visitRoom(GameProgress(emptySet(), emptySet(), null, Badge.NONE), RoomId.KITCHEN)

        assertEquals(once, visitRoom(once, RoomId.KITCHEN))
    }

    @Test
    fun unvisitedRoomIsIdentifiedAsFirstVisit() {
        val progress = GameProgress(emptySet(), emptySet(), null, Badge.NONE)

        assertTrue(isFirstVisit(progress, RoomId.STUDY))
    }

    @Test
    fun previouslyVisitedRoomIsNotIdentifiedAsFirstVisit() {
        val progress = GameProgress(setOf(RoomId.STUDY), emptySet(), null, Badge.NONE)

        assertFalse(isFirstVisit(progress, RoomId.STUDY))
    }

    @Test
    fun livingRoomStartsUnlocked() {
        val progress = GameProgress(emptySet(), emptySet(), null, Badge.NONE)

        assertTrue(isRoomUnlocked(progress, RoomId.LIVING_ROOM))
    }

    @Test
    fun eachRoomRequiresAllPreviousRoomsToBeVisited() {
        val progress = GameProgress(setOf(RoomId.LIVING_ROOM, RoomId.DRESSING), emptySet(), null, Badge.NONE)

        assertTrue(isRoomUnlocked(progress, RoomId.STUDY))
        assertFalse(isRoomUnlocked(progress, RoomId.DRESSING))
        assertFalse(isRoomUnlocked(progress, RoomId.KITCHEN))
        assertFalse(isRoomUnlocked(progress, RoomId.BEDROOM))
    }

    @Test
    fun visitingRoomUnlocksTheNextRoomInTourOrder() {
        val progress = visitRoom(GameProgress(emptySet(), emptySet(), null, Badge.NONE), RoomId.LIVING_ROOM)

        assertTrue(isRoomUnlocked(progress, RoomId.STUDY))
        assertFalse(isRoomUnlocked(progress, RoomId.DRESSING))
    }

    @Test
    fun allRoomsBecomeUnlockedAfterTheTourIsVisited() {
        val progress = GameProgress(RoomId.entries.toSet(), emptySet(), null, Badge.NONE)

        RoomId.entries.forEach { roomId ->
            assertTrue(isRoomUnlocked(progress, roomId))
        }
    }

    @Test
    fun lessonCannotBeCompletedBeforeRoomIsVisited() {
        val progress = GameProgress(emptySet(), emptySet(), null, Badge.NONE)

        assertEquals(progress, completeLesson(progress, RoomId.BEDROOM))
    }

    @Test
    fun lessonCanBeCompletedAfterRoomIsVisited() {
        val progress = visitRoom(GameProgress(emptySet(), emptySet(), null, Badge.NONE), RoomId.BEDROOM)

        assertEquals(progress.copy(completedLessons = setOf(RoomId.BEDROOM)), completeLesson(progress, RoomId.BEDROOM))
    }

    @Test
    fun completingLessonTwiceIsIdempotent() {
        val initial = GameProgress(setOf(RoomId.STUDY), emptySet(), null, Badge.NONE)
        val once = completeLesson(initial, RoomId.STUDY)

        assertEquals(once, completeLesson(once, RoomId.STUDY))
    }

    @Test
    fun zeroCorrectAnswersEarnNoBadge() {
        assertEquals(Badge.NONE, scoreQuiz(0))
    }

    @Test
    fun oneCorrectAnswerEarnsNoBadge() {
        assertEquals(Badge.NONE, scoreQuiz(1))
    }

    @Test
    fun twoCorrectAnswersEarnNoBadge() {
        assertEquals(Badge.NONE, scoreQuiz(2))
    }

    @Test
    fun threeCorrectAnswersEarnBronze() {
        assertEquals(Badge.BRONZE, scoreQuiz(3))
    }

    @Test
    fun fourCorrectAnswersEarnSilver() {
        assertEquals(Badge.SILVER, scoreQuiz(4))
    }

    @Test
    fun fiveCorrectAnswersEarnGold() {
        assertEquals(Badge.GOLD, scoreQuiz(5))
    }

    @Test
    fun negativeCorrectAnswerCountIsRejected() {
        assertFailsWith<IllegalArgumentException> { scoreQuiz(-1) }
    }

    @Test
    fun correctAnswerCountAboveFiveIsRejected() {
        assertFailsWith<IllegalArgumentException> { scoreQuiz(6) }
    }

    @Test
    fun miniGameBadgeThresholdsAwardBronzeSilverAndGold() {
        assertEquals(Badge.NONE, badgeForMiniGameScore(0))
        assertEquals(Badge.BRONZE, badgeForMiniGameScore(1))
        assertEquals(Badge.BRONZE, badgeForMiniGameScore(2))
        assertEquals(Badge.SILVER, badgeForMiniGameScore(3))
        assertEquals(Badge.SILVER, badgeForMiniGameScore(4))
        assertEquals(Badge.GOLD, badgeForMiniGameScore(5))
    }

    @Test
    fun miniGameBadgeScoreOutsideFiveRoomsIsRejected() {
        assertFailsWith<IllegalArgumentException> { badgeForMiniGameScore(-1) }
        assertFailsWith<IllegalArgumentException> { badgeForMiniGameScore(6) }
    }

    @Test
    fun miniGameResultCannotBeRecordedBeforeVisitingTheRoom() {
        val progress = GameProgress(emptySet(), emptySet(), null, Badge.NONE)

        assertEquals(progress, recordMiniGameScore(progress, RoomId.LIVING_ROOM, 1))
    }

    @Test
    fun recordingMiniGameWinAddsOnePointAndBronzeBadge() {
        val progress = GameProgress(setOf(RoomId.LIVING_ROOM), emptySet(), null, Badge.NONE)

        val updated = recordMiniGameScore(progress, RoomId.LIVING_ROOM, 1)

        assertEquals(mapOf(RoomId.LIVING_ROOM to 1), updated.miniGameScores)
        assertEquals(Badge.BRONZE, updated.badge)
    }

    @Test
    fun repeatedWinForOneRoomDoesNotIncreaseTheTotalScore() {
        val progress = GameProgress(
            visitedRooms = setOf(RoomId.LIVING_ROOM),
            completedLessons = emptySet(),
            quizScore = null,
            badge = Badge.BRONZE,
            miniGameScores = mapOf(RoomId.LIVING_ROOM to 1),
        )

        assertEquals(progress, recordMiniGameScore(progress, RoomId.LIVING_ROOM, 1))
    }

    @Test
    fun miniGameProgressDoesNotLowerAnExistingBadge() {
        val progress = GameProgress(
            visitedRooms = setOf(RoomId.LIVING_ROOM),
            completedLessons = emptySet(),
            quizScore = null,
            badge = Badge.SILVER,
        )

        assertEquals(
            Badge.SILVER,
            recordMiniGameScore(progress, RoomId.LIVING_ROOM, 1).badge,
        )
    }

    @Test
    fun roomMiniGameScoreMustBeZeroOrOne() {
        val progress = GameProgress(setOf(RoomId.LIVING_ROOM), emptySet(), null, Badge.NONE)

        assertFailsWith<IllegalArgumentException> {
            recordMiniGameScore(progress, RoomId.LIVING_ROOM, 2)
        }
    }

    @Test
    fun quizAnswersScoreEveryValueFromZeroThroughFive() {
        val questions = quizQuestions()

        (0..5).forEach { expectedScore ->
            val answers = questions.mapIndexed { index, question ->
                if (index < expectedScore) {
                    question.correctIndex
                } else {
                    (question.correctIndex + 1) % question.options.size
                }
            }

            assertEquals(expectedScore, calculateQuizScore(questions, answers))
        }
    }

    @Test
    fun quizScoringRejectsMissingQuestionsAndAnswers() {
        val questions = quizQuestions()

        assertFailsWith<IllegalArgumentException> { calculateQuizScore(emptyList(), emptyList()) }
        assertFailsWith<IllegalArgumentException> { calculateQuizScore(questions, emptyList()) }
        assertFailsWith<IllegalArgumentException> {
            calculateQuizScore(questions, List(questions.size) { null })
        }
        assertFailsWith<IllegalArgumentException> {
            calculateQuizScore(questions, List(questions.size) { 99 })
        }
    }

    @Test
    fun quizCannotBeRecordedUntilEveryRoomIsVisited() {
        val progress = GameProgress(emptySet(), emptySet(), null, Badge.NONE)

        assertFailsWith<IllegalArgumentException> { recordQuizScore(progress, 5) }
    }

    @Test
    fun recordingQuizResultPersistsTheScoreAndBadge() {
        val progress = GameProgress(RoomId.entries.toSet(), emptySet(), null, Badge.NONE)

        val updated = recordQuizScore(progress, 4)

        assertEquals(4, updated.quizScore)
        assertEquals(Badge.SILVER, updated.badge)
    }

    @Test
    fun quizRetainsTheBestScoreAndPreviouslyEarnedBadge() {
        val progress = GameProgress(
            visitedRooms = RoomId.entries.toSet(),
            completedLessons = emptySet(),
            quizScore = 5,
            badge = Badge.GOLD,
        )

        val updated = recordQuizScore(progress, 2)

        assertEquals(5, updated.quizScore)
        assertEquals(Badge.GOLD, updated.badge)
    }

    @Test
    fun badgeScreenCanCheckEarnedAndLockedStates() {
        assertTrue(hasEarnedBadge(Badge.SILVER, Badge.BRONZE))
        assertTrue(hasEarnedBadge(Badge.SILVER, Badge.SILVER))
        assertFalse(hasEarnedBadge(Badge.BRONZE, Badge.GOLD))
    }

    private fun quizQuestions() = List(5) { index ->
        QuizQuestion("question-$index", "Choose", listOf("A", "B", "C"), 0)
    }
}
