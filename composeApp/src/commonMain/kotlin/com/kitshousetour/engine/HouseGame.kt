package com.kitshousetour.engine

import com.kitshousetour.domain.Badge
import com.kitshousetour.domain.QuizQuestion
import com.kitshousetour.domain.RoomId

/** The player's progress through the house and final quiz. */
data class GameProgress(
    val visitedRooms: Set<RoomId>,
    val completedLessons: Set<RoomId>,
    val quizScore: Int?,
    val badge: Badge,
    val miniGameScores: Map<RoomId, Int> = emptyMap(),
)

/** Returns a copy of [progress] with [roomId] recorded as visited. */
fun visitRoom(progress: GameProgress, roomId: RoomId): GameProgress =
    progress.copy(visitedRooms = progress.visitedRooms + roomId)

/** Returns whether [roomId] has not yet been visited in [progress]. */
fun isFirstVisit(progress: GameProgress, roomId: RoomId): Boolean =
    roomId !in progress.visitedRooms

/** Returns whether all rooms before [roomId] in the tour order have been visited. */
fun isRoomUnlocked(progress: GameProgress, roomId: RoomId): Boolean {
    val roomIndex = RoomId.entries.indexOf(roomId)
    return RoomId.entries.take(roomIndex).all { it in progress.visitedRooms }
}

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

/** Counts correct answers after requiring one valid selection for every quiz question. */
fun calculateQuizScore(
    questions: List<QuizQuestion>,
    answers: List<Int?>,
): Int {
    require(questions.isNotEmpty()) { "A quiz must contain at least one question." }
    require(answers.size == questions.size) { "Every quiz question must have one answer." }
    questions.indices.forEach { index ->
        val answer = requireNotNull(answers[index]) { "Every quiz question must be answered." }
        require(answer in questions[index].options.indices) { "Quiz answer index is out of range." }
    }
    return questions.indices.count { answers[it] == questions[it].correctIndex }
}

/** Records a quiz result, retaining the best score and highest badge earned. */
fun recordQuizScore(progress: GameProgress, correctCount: Int): GameProgress {
    require(isQuizUnlocked(progress)) { "The quiz is locked until all rooms have been visited." }
    val bestScore = maxOf(progress.quizScore ?: 0, correctCount)
    val quizBadge = scoreQuiz(bestScore)
    return progress.copy(
        quizScore = bestScore,
        badge = higherBadge(progress.badge, quizBadge),
    )
}

/** Returns the badge earned from zero to five mini-game points. */
fun badgeForMiniGameScore(score: Int): Badge {
    require(score in 0..5) { "Mini-game score must be between 0 and 5." }
    return when {
        score >= 5 -> Badge.GOLD
        score >= 3 -> Badge.SILVER
        score >= 1 -> Badge.BRONZE
        else -> Badge.NONE
    }
}

/** Returns whether [earnedBadge] meets or exceeds [targetBadge]. */
fun hasEarnedBadge(earnedBadge: Badge, targetBadge: Badge): Boolean =
    earnedBadge.rank() >= targetBadge.rank()

/** Records a room's best mini-game score and updates the player's highest badge. */
fun recordMiniGameScore(
    progress: GameProgress,
    roomId: RoomId,
    score: Int,
): GameProgress {
    require(score in 0..1) { "A room mini-game score must be zero or one." }
    if (roomId !in progress.visitedRooms) return progress

    val scores = progress.miniGameScores + (roomId to maxOf(progress.miniGameScores[roomId] ?: 0, score))
    val miniGameBadge = badgeForMiniGameScore(scores.values.sum())
    return progress.copy(
        miniGameScores = scores,
        badge = higherBadge(progress.badge, miniGameBadge),
    )
}

private fun higherBadge(first: Badge, second: Badge): Badge =
    if (first.rank() >= second.rank()) first else second

private fun Badge.rank(): Int = when (this) {
    Badge.NONE -> 0
    Badge.BRONZE -> 1
    Badge.SILVER -> 2
    Badge.GOLD -> 3
}
