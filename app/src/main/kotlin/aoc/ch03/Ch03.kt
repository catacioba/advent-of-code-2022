package aoc.ch03

import aoc.Challenge

class Ch03 : Challenge {

    private fun getPriority(c: Char): Int {
        return if (c.isUpperCase()) {
            27 + c.code - 'A'.code
        } else {
            1 + c.code - 'a'.code
        }
    }

    override fun partOne(input: String, debug: Boolean) {
        println(input.lines().map { l ->
            val first =
                l.slice(0 until l.length / 2).asSequence().toSet()
            val second =
                l.slice(l.length / 2 until l.length)
                    .asSequence()
                    .toSet()
            first.intersect(second).first()
        }.sumOf { getPriority(it) })
    }

    override fun partTwo(input: String, debug: Boolean) {
        val lines = input.lines()
        println((0 until lines.count() / 3).map { idx ->
            lines.slice(3 * idx until 3 * idx + 3)
                .map { it.asSequence().toSet() }
                .reduce { l, r -> l.intersect(r) }
                .first()
        }.sumOf { getPriority(it) })
    }
}