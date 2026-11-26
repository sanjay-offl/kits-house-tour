package com.kitshousetour.engine

import com.kitshousetour.engine.KitMood.Curious
import com.kitshousetour.engine.KitMood.Happy
import com.kitshousetour.engine.KitMood.Proud
import com.kitshousetour.engine.KitMood.Tired

/** Returns a state with Kit happy after the player taps Kit. */
fun onTapKit(state: MoodState): MoodState = MoodState(Happy)

/**
 * Makes Kit tired after at least 60 seconds without interaction.
 * Shorter idle periods and an already-tired state are preserved.
 */
fun onIdleFor(seconds: Long, state: MoodState): MoodState =
    if (seconds >= 60 && state.mood != Tired) MoodState(Tired) else state

/** Makes Kit proud until 10 seconds after [nowMillis]. */
fun onLessonFinished(state: MoodState, nowMillis: Long): MoodState =
    MoodState(Proud, expiresAtMillis = nowMillis + 10_000)

/**
 * Makes Kit curious for five seconds on a first visit.
 * Repeat visits preserve the existing mood and timer.
 */
fun onRoomEntered(isFirstVisit: Boolean, state: MoodState, nowMillis: Long): MoodState =
    if (isFirstVisit) {
        MoodState(Curious, expiresAtMillis = nowMillis + 5_000)
    } else {
        state
    }

/** Returns Kit to happy when a curious or proud mood has reached its expiry time. */
fun onTimedMoodExpired(state: MoodState, nowMillis: Long): MoodState =
    if (
        state.mood in setOf(Curious, Proud) &&
        state.expiresAtMillis?.let { nowMillis >= it } == true
    ) {
        MoodState(Happy)
    } else {
        state
    }
