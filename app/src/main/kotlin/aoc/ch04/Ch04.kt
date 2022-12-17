package aoc.ch04

import aoc.Challenge
import aoc.util.Interval

class Ch04 : Challenge {

    private fun String.toInterval(): Interval {
        val p = this.split('-')
        return Interval(p[0].toInt(), p[1].toInt())
    }

    override fun partOne(input: String, debug: Boolean, isTestRun: Boolean) {
        println(input.lineSequence()
            .map { it.split(',') }
            .map { Pair(it[0].toInterval(), it[1].toInterval()) }
            .filter { it.first.contains(it.second) || it.second.contains(it.first) }
            .count())
    }

    override fun partTwo(input: String, debug: Boolean, isTestRun: Boolean) {
        println(input.lineSequence()
            .map { it.split(',') }
            .map { Pair(it[0].toInterval(), it[1].toInterval()) }
            .filter { it.first.overlaps(it.second) }
            .count())
    }
}