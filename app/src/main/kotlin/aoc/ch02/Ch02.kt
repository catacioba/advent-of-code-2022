package aoc.ch02

import aoc.Challenge

class Ch02 : Challenge {

    enum class Hand {
        Rock, Paper, Scissors;
    }

    private fun Char.toHand(): Hand {
        return when (this) {
            'A' -> Hand.Rock
            'B' -> Hand.Paper
            'C' -> Hand.Scissors
            'X' -> Hand.Rock
            'Y' -> Hand.Paper
            'Z' -> Hand.Scissors
            else -> {
                throw IllegalArgumentException("Invalid option $this")
            }
        }
    }

    private val losingHands = mapOf(
        Hand.Rock to Hand.Paper,
        Hand.Paper to Hand.Scissors,
        Hand.Scissors to Hand.Rock
    )

    private val winningHands: Map<Hand, Hand> = mapOf(
        Hand.Rock to Hand.Scissors,
        Hand.Scissors to Hand.Paper,
        Hand.Paper to Hand.Rock
    )

    private fun Char.toStrategyHand(outcome: Char): Hand {
        return when (outcome) {
            'X' -> winningHands[this.toHand()]!!
            'Y' -> this.toHand()
            'Z' -> losingHands[this.toHand()]!!
            else -> {
                throw IllegalArgumentException("Invalid option $outcome")
            }
        }
    }

    data class Game(val otherHand: Hand, val myHand: Hand) {
        val score = handScore + winnerScore

        private val handScore: Int
            get() {
                return when (myHand) {
                    Hand.Rock -> 1
                    Hand.Paper -> 2
                    Hand.Scissors -> 3
                }
            }

        private val winnerScore: Int
            get() {
                if (myHand == otherHand) {
                    return 3
                }
                return when (myHand) {
                    Hand.Rock -> {
                        if (otherHand == Hand.Paper) 0 else 6
                    }

                    Hand.Paper -> {
                        if (otherHand == Hand.Scissors) 0 else 6
                    }

                    Hand.Scissors -> {
                        if (otherHand == Hand.Rock) 0 else 6
                    }
                }
            }
    }

    override fun partOne(input: String, debug: Boolean, isTestRun: Boolean) {
        println(input.split("\n")
            .map { it.split(" ") }
            .map { Game(it[0].first().toHand(), it[1].first().toHand()) }
            .sumOf { it.score })
    }

    override fun partTwo(input: String, debug: Boolean, isTestRun: Boolean) {
        println(input.split("\n").map { it.split(" ") }.map {
                Game(
                    it[0].first().toHand(),
                    it[0].first().toStrategyHand(it[1].first())
                )
            }.sumOf { it.score })
    }

}