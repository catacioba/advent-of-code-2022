package aoc.ch01

import aoc.Challenge

class Ch01 : Challenge {
    override fun partOne(input: String, debug: Boolean) {
        val parts = input.split("\n\n")
        println(parts.maxOfOrNull { x -> x.split("\n").sumOf { it.toInt() } })
    }

    override fun partTwo(input: String, debug: Boolean) {
        println(input.split("\n\n")
            .map { x -> x.split("\n").sumOf { it.toInt() } }
            .sortedDescending()
            .take(3)
            .sum())
    }
}
