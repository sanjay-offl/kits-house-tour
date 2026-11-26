package com.kitshousetour.engine

import com.kitshousetour.domain.Badge
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
}
