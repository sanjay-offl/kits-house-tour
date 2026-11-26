package com.kitshousetour.engine

import com.kitshousetour.domain.Badge
import com.kitshousetour.domain.RoomId

/** The player's progress through the house and final quiz. */
data class GameProgress(
    val visitedRooms: Set<RoomId>,
    val completedLessons: Set<RoomId>,
    val quizScore: Int?,
    val badge: Badge,
)

/** Returns a copy of [progress] with [roomId] recorded as visited. */
fun visitRoom(progress: GameProgress, roomId: RoomId): GameProgress =
    progress.copy(visitedRooms = progress.visitedRooms + roomId)

/**
 * Returns progress with the lesson completed if its room was visited.
 * Otherwise, the original progress is returned unchanged.
 */
fun completeLesson(progress: GameProgress, roomId: RoomId): GameProgress =
    if (roomId in progress.visitedRooms) {
        progress.copy(completedLessons = progress.completedLessons + roomId)
    } else {
        progress
    }

/** Returns whether every room has been visited and the quiz is unlocked. */
fun isQuizUnlocked(progress: GameProgress): Boolean =
    RoomId.entries.all { it in progress.visitedRooms }

/** Maps a score from zero to five correct answers to its earned badge. */
fun scoreQuiz(correctCount: Int): Badge {
    require(correctCount in 0..5) { "Quiz score must be between 0 and 5." }
    return when (correctCount) {
        in 0..2 -> Badge.NONE
        3 -> Badge.BRONZE
        4 -> Badge.SILVER
        5 -> Badge.GOLD
        else -> error("Validated quiz score has no badge mapping.")
    }
}
