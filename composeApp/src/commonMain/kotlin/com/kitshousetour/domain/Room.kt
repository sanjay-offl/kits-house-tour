package com.kitshousetour.domain

/** Identifies one of the five rooms in Kit's house. */
enum class RoomId {
    LIVING_ROOM,
    STUDY,
    DRESSING,
    KITCHEN,
    BEDROOM,
}

/** Describes a room and the object the player should interact with there. */
data class Room(
    val id: RoomId,
    val title: String,
    val interactionPrompt: String,
)
