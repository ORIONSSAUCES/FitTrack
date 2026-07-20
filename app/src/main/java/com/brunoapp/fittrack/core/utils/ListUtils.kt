package com.brunoapp.fittrack.core.utils

/** Pure list manipulation helpers. Unit-tested. */
object ListUtils {

    /** Moves the element at [from] to [to]. Returns the same list when indices are invalid. */
    fun <T> move(list: List<T>, from: Int, to: Int): List<T> {
        if (from !in list.indices || to !in list.indices || from == to) return list
        val mutable = list.toMutableList()
        val item = mutable.removeAt(from)
        mutable.add(to, item)
        return mutable
    }
}
