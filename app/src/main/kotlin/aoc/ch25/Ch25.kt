package aoc.ch25

import aoc.Challenge

class Ch25 : Challenge {

    private fun snafuToDecimal(s: String): Long {
        var p = 1L
        var accum = 0L
        for (c in s.toCharArray().reversed()) {
            accum += when (c) {
                         '2' -> 2L
                         '1' -> 1L
                         '0' -> 0L
                         '-' -> -1L
                         '=' -> -2L
                         else -> throw IllegalArgumentException("invalid character $c")
                     } * p
            p *= 5L
        }
        return accum
    }

    private fun decimalToSnafu(n: Long): String {
        val s = n.toString(5)
        var accum = 0L
        var p = 1L
        for (c in s) {
            accum += 2 * p
            p *= 5
        }
        val ss = (n + accum).toString(5)

        // y = a * 5^3 + b * 5^2 + c * 5^1 + d * 5^0
        // z = 2 * 5^3 + 2 * 5^2 + 2 * 5^1 + 2 * 5^0
        // y - z  = (a-2) * 5^3 + (b-2) * 5^2 + (c-2) * 5^1 + (d-2) * 5^0
        return ss.map {
            when (it) {
                '4' -> '2'
                '3' -> '1'
                '2' -> '0'
                '1' -> '-'
                '0' -> '='
                else -> throw IllegalArgumentException("invalid state")
            }
        }.joinToString(separator = "")
    }

    override fun partOne(input: String, debug: Boolean, isTestRun: Boolean) {
        val s = input.lines()
            .sumOf(this::snafuToDecimal)
        println(s)
        println(decimalToSnafu(s))
    }

    override fun partTwo(input: String, debug: Boolean, isTestRun: Boolean) {
        TODO("There is no part 2 for the final day")
    }
}