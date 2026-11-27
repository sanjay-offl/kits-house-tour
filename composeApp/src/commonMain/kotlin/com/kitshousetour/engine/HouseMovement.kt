package com.kitshousetour.engine

import com.kitshousetour.domain.RoomId

/** Describes a room target as fractions of the house plan's width and height. */
data class HousePoint(
    val xFraction: Float,
    val yFraction: Float,
)

/** The responsive arrangement used to select a room's position in the house plan. */
enum class HouseLayout {
    COMPACT,
    WIDE,
}

/** Returns Kit's normalized destination for [roomId] in the requested house layout. */
fun roomTarget(roomId: RoomId, layout: HouseLayout): HousePoint {
    val index = RoomId.entries.indexOf(roomId)
    return when (layout) {
        HouseLayout.COMPACT -> HousePoint(
            xFraction = 0.86f,
            yFraction = (index + 0.5f) / RoomId.entries.size,
        )
        HouseLayout.WIDE -> when (roomId) {
            RoomId.LIVING_ROOM -> HousePoint(0.43f, 1f / 6f)
            RoomId.STUDY -> HousePoint(0.93f, 1f / 6f)
            RoomId.DRESSING -> HousePoint(0.43f, 0.5f)
            RoomId.KITCHEN -> HousePoint(0.93f, 0.5f)
            RoomId.BEDROOM -> HousePoint(0.93f, 5f / 6f)
        }
    }
}
