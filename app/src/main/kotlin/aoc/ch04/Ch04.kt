package aoc.ch04

import aoc.Challenge

class Ch04 : Challenge {
    data class Interval(val l: Int, val r: Int) {
        fun contains(ot: Interval): Boolean = this.l <= ot.l && this.r >= ot.r

        fun overlaps(ot: Interval): Boolean =
            this.r >= ot.l && ot.r >= this.l
    }

    fun String.toInterval(): Interval {
        val p = this.split('-')
        return Interval(p[0].toInt(), p[1].toInt())
    }

    override fun partOne(input: String) {
        println(input.lineSequence()
            .map { it.split(',') }
            .map { Pair(it[0].toInterval(), it[1].toInterval()) }
            .filter { it.first.contains(it.second) || it.second.contains(it.first) }
            .count())
    }

    override fun partTwo(input: String) {
        println(input.lineSequence()
            .map { it.split(',') }
            .map { Pair(it[0].toInterval(), it[1].toInterval()) }
            .filter { it.first.overlaps(it.second) }
            .count())
    }
}