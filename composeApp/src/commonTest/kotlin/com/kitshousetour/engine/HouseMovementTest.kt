package com.kitshousetour.engine

import com.kitshousetour.domain.RoomId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HouseMovementTest {
    @Test
    fun compactTargetsFollowTheListedRoomOrder() {
        val targets = RoomId.entries.map { roomTarget(it, HouseLayout.COMPACT) }

        assertTrue(targets.all { it.xFraction == targets.first().xFraction })
        assertTrue(targets.zipWithNext().all { (first, second) -> first.yFraction < second.yFraction })
        assertTrue(targets.all { it.xFraction in 0f..1f && it.yFraction in 0f..1f })
    }

    @Test
    fun wideTargetsMatchTheTwoColumnFloorPlan() {
        assertEquals(HousePoint(0.43f, 1f / 6f), roomTarget(RoomId.LIVING_ROOM, HouseLayout.WIDE))
        assertEquals(HousePoint(0.93f, 0.5f), roomTarget(RoomId.KITCHEN, HouseLayout.WIDE))
        assertEquals(HousePoint(0.93f, 5f / 6f), roomTarget(RoomId.BEDROOM, HouseLayout.WIDE))
    }

    @Test
    fun eachWideRoomHasAUniqueTarget() {
        val targets = RoomId.entries.map { roomTarget(it, HouseLayout.WIDE) }

        assertEquals(targets.size, targets.toSet().size)
    }
}
