package aoc.util

import java.lang.Integer.max
import java.lang.Integer.min

data class Interval(val left: Int, val right: Int) {
    fun contains(ot: Interval): Boolean =
        this.left <= ot.left && this.right >= ot.right

    fun overlaps(ot: Interval): Boolean =
        this.right >= ot.left && ot.right >= this.left

    fun merge(ot: Interval): Interval =
        Interval(min(left, ot.left), max(right, ot.right))
}
