package com.kitshousetour.data

import com.kitshousetour.domain.Badge
import com.kitshousetour.domain.RoomId
import com.kitshousetour.engine.GameProgress
import com.kitshousetour.engine.KitMood
import com.kitshousetour.engine.MoodState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class ProgressRepositoryTest {
    @Test
    fun savesAndLoadsVisitedRoomsAndTimedMood() {
        val file = InMemoryProgressFile()
        val repository = JsonProgressRepository(file)
        val progress = PlayerProgress(
            game = GameProgress(
                visitedRooms = setOf(RoomId.LIVING_ROOM, RoomId.KITCHEN),
                completedLessons = setOf(RoomId.LIVING_ROOM),
                quizScore = 4,
                badge = Badge.SILVER,
                miniGameScores = mapOf(RoomId.LIVING_ROOM to 1, RoomId.KITCHEN to 1),
            ),
            mood = MoodState(KitMood.Curious, expiresAtMillis = 123_456L),
            settings = PlayerSettings(speechMuted = true, reducedMotion = true),
        )

        repository.save(progress)

        assertEquals(progress, JsonProgressRepository(file).loadOrCreate())
    }

    @Test
    fun missingFileCreatesAndSavesFreshProgress() {
        val file = InMemoryProgressFile()
        var reportedMessage: String? = null
        val repository = JsonProgressRepository(
            fileAccess = file,
            reportProgressFallback = { reportedMessage = it },
        )

        val loaded = repository.loadOrCreate()

        assertEquals(freshPlayerProgress(), loaded)
        assertNotNull(reportedMessage)
        assertNotNull(file.contents)
        assertEquals(loaded, JsonProgressRepository(file).loadOrCreate())
    }

    @Test
    fun olderProgressWithoutMiniGameScoresLoadsWithAnEmptyScoreMap() {
        val file = InMemoryProgressFile(
            contents = """
                {
                  "visitedRooms": ["LIVING_ROOM"],
                  "completedLessons": [],
                  "quizScore": null,
                  "badge": "NONE",
                  "mood": "HAPPY",
                  "moodExpiresAtMillis": null
                }
            """.trimIndent(),
        )

        assertEquals(emptyMap(), JsonProgressRepository(file).loadOrCreate().game.miniGameScores)
    }

    @Test
    fun olderProgressWithoutSettingsUsesDefaultPreferences() {
        val file = InMemoryProgressFile(
            contents = """
                {
                  "visitedRooms": [],
                  "completedLessons": [],
                  "quizScore": null,
                  "badge": "NONE",
                  "mood": "HAPPY",
                  "moodExpiresAtMillis": null
                }
            """.trimIndent(),
        )

        assertEquals(PlayerSettings(), JsonProgressRepository(file).loadOrCreate().settings)
    }

    @Test
    fun invalidSavedMiniGameScoreIsHandledAsCorruptData() {
        val file = InMemoryProgressFile(
            contents = """
                {
                  "visitedRooms": [],
                  "completedLessons": [],
                  "quizScore": null,
                  "badge": "NONE",
                  "mood": "HAPPY",
                  "moodExpiresAtMillis": null,
                  "miniGameScores": {"LIVING_ROOM": 2}
                }
            """.trimIndent(),
        )

        assertEquals(freshPlayerProgress(), JsonProgressRepository(file).loadOrCreate())
    }

    @Test
    fun corruptFileReportsFallbackAndReplacesItWithFreshProgress() {
        val file = InMemoryProgressFile(contents = "{not valid json")
        var reportedMessage: String? = null
        val repository = JsonProgressRepository(
            fileAccess = file,
            reportProgressFallback = { reportedMessage = it },
        )

        val loaded = repository.loadOrCreate()

        assertEquals(freshPlayerProgress(), loaded)
        assertNotNull(reportedMessage)
        assertFalse(reportedMessage!!.isBlank())
        assertNotNull(file.contents)
        assertEquals(loaded, JsonProgressRepository(file).loadOrCreate())
    }

    @Test
    fun invalidSavedScoreIsHandledAsCorruptData() {
        val file = InMemoryProgressFile(
            contents = """
                {
                  "visitedRooms": [],
                  "completedLessons": [],
                  "quizScore": 6,
                  "badge": "NONE",
                  "mood": "HAPPY",
                  "moodExpiresAtMillis": null
                }
            """.trimIndent(),
        )

        assertEquals(freshPlayerProgress(), JsonProgressRepository(file).loadOrCreate())
    }
}

private class InMemoryProgressFile(
    var contents: String? = null,
) : com.kitshousetour.platform.ProgressFileAccess {
    override val path: String = "/test/progress.json"

    override fun readText(): String? = contents

    override fun writeText(contents: String) {
        this.contents = contents
    }
}
