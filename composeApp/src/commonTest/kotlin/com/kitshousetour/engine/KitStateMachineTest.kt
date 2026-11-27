package com.kitshousetour.engine

import com.kitshousetour.engine.KitMood.Curious
import com.kitshousetour.engine.KitMood.Happy
import com.kitshousetour.engine.KitMood.Proud
import com.kitshousetour.engine.KitMood.Tired
import kotlin.test.Test
import kotlin.test.assertEquals

class KitStateMachineTest {
    @Test
    fun tappingKitAlwaysMakesKitHappy() {
        val moods = listOf(Happy, Curious, Tired, Proud)

        moods.forEach { mood ->
            assertEquals(MoodState(Happy), onTapKit(MoodState(mood)))
        }
    }

    @Test
    fun idleFor59SecondsPreservesMood() {
        val state = MoodState(Curious, expiresAtMillis = 9_000)

        assertEquals(state, onIdleFor(59, state))
    }

    @Test
    fun idleFor60SecondsMakesKitTired() {
        assertEquals(MoodState(Tired), onIdleFor(60, MoodState(Happy)))
    }

    @Test
    fun idleForMoreThan60SecondsMakesKitTired() {
        assertEquals(MoodState(Tired), onIdleFor(120, MoodState(Curious)))
    }

    @Test
    fun idleDoesNotResetAnAlreadyTiredMood() {
        val state = MoodState(Tired, expiresAtMillis = 10_000)

        assertEquals(state, onIdleFor(60, state))
    }

    @Test
    fun finishingLessonMakesKitProudForTenSeconds() {
        val state = onLessonFinished(MoodState(Happy), nowMillis = 1_000)

        assertEquals(MoodState(Proud, expiresAtMillis = 11_000), state)
    }

    @Test
    fun timedMoodRemainsBeforeExpiry() {
        val state = MoodState(Curious, expiresAtMillis = 5_000)

        assertEquals(state, onTimedMoodExpired(state, nowMillis = 4_999))
    }

    @Test
    fun timedMoodReturnsToHappyAtExpiry() {
        assertEquals(
            MoodState(Happy),
            onTimedMoodExpired(MoodState(Proud, expiresAtMillis = 10_000), nowMillis = 10_000),
        )
    }

    @Test
    fun timedMoodReturnsToHappyAfterExpiry() {
        assertEquals(
            MoodState(Happy),
            onTimedMoodExpired(MoodState(Curious, expiresAtMillis = 5_000), nowMillis = 5_001),
        )
    }

    @Test
    fun untimedMoodDoesNotExpire() {
        val state = MoodState(Happy)

        assertEquals(state, onTimedMoodExpired(state, nowMillis = Long.MAX_VALUE))
    }

    @Test
    fun firstRoomVisitMakesKitCuriousForFiveSeconds() {
        val state = onRoomEntered(isFirstVisit = true, MoodState(Happy), nowMillis = 2_000)

        assertEquals(MoodState(Curious, expiresAtMillis = 7_000), state)
    }

    @Test
    fun repeatRoomVisitPreservesMoodAndExpiry() {
        val state = MoodState(Proud, expiresAtMillis = 12_000)

        assertEquals(state, onRoomEntered(isFirstVisit = false, state, nowMillis = 2_000))
    }

    @Test
    fun tappingLockedRoomMakesKitCuriousForFiveSeconds() {
        assertEquals(
            MoodState(Curious, expiresAtMillis = 7_000),
            onLockedRoomTapped(nowMillis = 2_000),
        )
    }
}
