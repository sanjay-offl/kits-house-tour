package com.kitshousetour.data

import com.kitshousetour.domain.Badge
import com.kitshousetour.engine.GameProgress
import com.kitshousetour.engine.KitMood
import com.kitshousetour.engine.MoodState

/** The durable game and mood state restored when the app launches. */
data class PlayerProgress(
    val game: GameProgress,
    val mood: MoodState,
)

/** Supplies a fresh player state for a new or unreadable progress file. */
fun freshPlayerProgress(): PlayerProgress = PlayerProgress(
    game = GameProgress(
        visitedRooms = emptySet(),
        completedLessons = emptySet(),
        quizScore = null,
        badge = Badge.NONE,
    ),
    mood = MoodState(KitMood.Happy),
)

/** Reads and writes the player's persisted progress. */
interface ProgressRepository {
    /** Loads saved progress or creates and saves a fresh state if none is present. */
    fun loadOrCreate(): PlayerProgress

    /** Saves the complete player state. */
    fun save(progress: PlayerProgress)
}
