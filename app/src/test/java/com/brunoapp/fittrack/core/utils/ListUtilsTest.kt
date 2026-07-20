package com.brunoapp.fittrack.core.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class ListUtilsTest {

    private val list = listOf("a", "b", "c", "d")

    @Test
    fun `move down shifts element correctly`() {
        assertEquals(listOf("b", "a", "c", "d"), ListUtils.move(list, 0, 1))
    }

    @Test
    fun `move up shifts element correctly`() {
        assertEquals(listOf("a", "c", "b", "d"), ListUtils.move(list, 2, 1))
    }

    @Test
    fun `move to same index returns same list`() {
        assertEquals(list, ListUtils.move(list, 1, 1))
    }

    @Test
    fun `move with invalid from index returns same list`() {
        assertEquals(list, ListUtils.move(list, -1, 2))
        assertEquals(list, ListUtils.move(list, 10, 2))
    }

    @Test
    fun `move with invalid to index returns same list`() {
        assertEquals(list, ListUtils.move(list, 0, 4))
        assertEquals(list, ListUtils.move(list, 0, -1))
    }

    @Test
    fun `move to last position works`() {
        assertEquals(listOf("b", "c", "d", "a"), ListUtils.move(list, 0, 3))
    }
}
