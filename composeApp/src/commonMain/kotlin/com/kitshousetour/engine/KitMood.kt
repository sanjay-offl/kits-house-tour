package com.kitshousetour.engine

/** Kit's current expression and energy level. */
sealed interface KitMood {
    /** Kit is cheerful. */
    data object Happy : KitMood

    /** Kit is exploring something new. */
    data object Curious : KitMood

    /** Kit is ready for a rest. */
    data object Tired : KitMood

    /** Kit is proud of the player's progress. */
    data object Proud : KitMood
}

/** A mood and the optional time at which it should expire. */
data class MoodState(
    val mood: KitMood,
    val expiresAtMillis: Long? = null,
)
