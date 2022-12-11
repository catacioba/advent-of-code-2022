package aoc.ch06

import aoc.Challenge

class Ch06 : Challenge {

    private fun findMarker(s: String, cnt: Int): Int? {
        for (idx in s.indices) {
            if (idx < cnt) {
                continue
            }
            if (s.slice((idx - cnt + 1)..idx).toSet().count() == cnt) {
                return idx + 1
            }
        }
        return null
    }

    override fun partOne(input: String, debug: Boolean) {
        println(findMarker(input, 4))
    }

    override fun partTwo(input: String, debug: Boolean) {
        println(findMarker(input, 14))
    }
}