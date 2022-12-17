package aoc

interface Challenge {
    fun partOne(input: String, debug: Boolean, isTestRun: Boolean)
    fun partTwo(input: String, debug: Boolean, isTestRun: Boolean)
}