package com.kitshousetour.domain

/** A tap performed by the player. */
sealed interface Interaction {
    /** A tap on the room's featured object. */
    data class TapObject(val roomId: RoomId) : Interaction

    /** A tap directly on Kit. */
    data object TapKit : Interaction
}
