package com.kitshousetour.data

import com.kitshousetour.domain.Badge
import com.kitshousetour.domain.RoomId
import com.kitshousetour.engine.GameProgress
import com.kitshousetour.engine.KitMood
import com.kitshousetour.engine.MoodState
import com.kitshousetour.platform.ProgressFileAccess
import com.kitshousetour.platform.ProgressFileLocation
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** Stores player progress as JSON using the supplied platform file access. */
class JsonProgressRepository(
    private val fileAccess: ProgressFileAccess = ProgressFileLocation,
    private val freshState: PlayerProgress = freshPlayerProgress(),
    private val reportProgressFallback: (String) -> Unit = ::println,
) : ProgressRepository {
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    override fun loadOrCreate(): PlayerProgress {
        val savedText = fileAccess.readText()
        if (savedText == null) {
            reportProgressFallback(
                "Progress file at ${fileAccess.path} was not found; starting with fresh progress.",
            )
            save(freshState)
            return freshState
        }

        return try {
            json.decodeFromString<StoredPlayerProgress>(savedText).toPlayerProgress()
        } catch (exception: SerializationException) {
            replaceCorruptFile(exception)
        } catch (exception: IllegalArgumentException) {
            replaceCorruptFile(exception)
        }
    }

    override fun save(progress: PlayerProgress) {
        fileAccess.writeText(json.encodeToString(progress.toStoredPlayerProgress()))
    }

    private fun replaceCorruptFile(exception: Exception): PlayerProgress {
        reportProgressFallback(
            "Progress file at ${fileAccess.path} is invalid; starting with fresh progress. " +
                exception.message.orEmpty(),
        )
        save(freshState)
        return freshState
    }
}

private enum class StoredKitMood {
    HAPPY,
    CURIOUS,
    TIRED,
    PROUD,
}

@Serializable
private data class StoredPlayerProgress(
    val visitedRooms: Set<RoomId>,
    val completedLessons: Set<RoomId>,
    val quizScore: Int?,
    val badge: Badge,
    val mood: StoredKitMood,
    val moodExpiresAtMillis: Long?,
)

private fun PlayerProgress.toStoredPlayerProgress(): StoredPlayerProgress =
    StoredPlayerProgress(
        visitedRooms = game.visitedRooms,
        completedLessons = game.completedLessons,
        quizScore = game.quizScore,
        badge = game.badge,
        mood = mood.mood.toStoredKitMood(),
        moodExpiresAtMillis = mood.expiresAtMillis,
    )

private fun StoredPlayerProgress.toPlayerProgress(): PlayerProgress {
    require(quizScore == null || quizScore in 0..5) {
        "Saved quiz score must be between 0 and 5."
    }
    return PlayerProgress(
        game = GameProgress(
            visitedRooms = visitedRooms,
            completedLessons = completedLessons,
            quizScore = quizScore,
            badge = badge,
        ),
        mood = MoodState(
            mood = mood.toKitMood(),
            expiresAtMillis = moodExpiresAtMillis,
        ),
    )
}

private fun KitMood.toStoredKitMood(): StoredKitMood = when (this) {
    KitMood.Happy -> StoredKitMood.HAPPY
    KitMood.Curious -> StoredKitMood.CURIOUS
    KitMood.Tired -> StoredKitMood.TIRED
    KitMood.Proud -> StoredKitMood.PROUD
}

private fun StoredKitMood.toKitMood(): KitMood = when (this) {
    StoredKitMood.HAPPY -> KitMood.Happy
    StoredKitMood.CURIOUS -> KitMood.Curious
    StoredKitMood.TIRED -> KitMood.Tired
    StoredKitMood.PROUD -> KitMood.Proud
}
