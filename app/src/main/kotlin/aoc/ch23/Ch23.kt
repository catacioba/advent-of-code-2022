package aoc.ch23

import aoc.Challenge
import aoc.util.Position

class Ch23 : Challenge {
    private val north = Position(-1, 0)
    private val northEast = Position(-1, 1)
    private val east = Position(0, 1)
    private val southEast = Position(1, 1)
    private val south = Position(1, 0)
    private val southWest = Position(1, -1)
    private val west = Position(0, -1)
    private val northWest = Position(-1, -1)

    private val allDirections =
        listOf(
            north,
            northEast,
            east,
            southEast,
            south,
            southWest,
            west,
            northWest
        )

    private val directionsToCheck = listOf(
        listOf(north, northEast, northWest),
        listOf(south, southEast, southWest),
        listOf(west, northWest, southWest),
        listOf(east, northEast, southEast),
    )

    private val directionsToMove = listOf(north, south, west, east)

    private var hasMoved = false

    private fun iterate(
        elfPositions: Set<Position>,
        iteration: Int
    ): Set<Position> {
        val newElfPositions = mutableSetOf<Position>()
        val duplicatePositions = mutableMapOf<Position, MutableList<Position>>()

        hasMoved = false

        fun getPosition(elfPosition: Position): Position {
            if (allDirections.all { !elfPositions.contains(it + elfPosition) }) {
                return elfPosition
            }

            for (idx in 0 until 4) {
                val i = (iteration + idx) % 4
                val toCheck = directionsToCheck[i]
                val toMove = directionsToMove[i]

                if (toCheck.all {
                        !elfPositions.contains(elfPosition + it)
                    }) {
                    hasMoved = true
                    return elfPosition + toMove
                }
            }

            return elfPosition
        }

        for (elfPosition in elfPositions) {
            val nextPosition = getPosition(elfPosition)

            newElfPositions.add(nextPosition)
            val positions =
                duplicatePositions.getOrDefault(nextPosition, mutableListOf())
            positions.add(elfPosition)
            duplicatePositions[nextPosition] = positions
        }

        for (duplicatePosition in duplicatePositions.keys) {
            val positions = duplicatePositions[duplicatePosition]!!
            if (positions.size > 1) {
                newElfPositions.remove(duplicatePosition)
                newElfPositions.addAll(positions)
            }
        }

        return newElfPositions
    }

    private fun count(elfPositions: Set<Position>): Int {
        val minX = elfPositions.minOf { it.x }
        val maxX = elfPositions.maxOf { it.x }
        val minY = elfPositions.minOf { it.y }
        val maxY = elfPositions.maxOf { it.y }

        var cnt = 0

        for (x in minX..maxX) {
            for (y in minY..maxY) {
                if (!elfPositions.contains(Position(x, y))) {
                    cnt += 1
                }
            }
        }
        return cnt
    }

    private fun parseInput(input: String): Set<Position> {
        return input.lineSequence().flatMapIndexed { x, l ->
            l.mapIndexedNotNull { y, c ->
                if (c == '#') Position(
                    x,
                    y
                ) else null
            }
        }.toSet()
    }

    override fun partOne(input: String, debug: Boolean, isTestRun: Boolean) {
        var elfPositions = parseInput(input)

        repeat(10) {
            elfPositions = iterate(elfPositions, it)
        }
        println(count(elfPositions))
    }

    override fun partTwo(
        input: String,
        debug: Boolean,
        isTestRun: Boolean
    ) {
        var elfPositions = parseInput(input)

        var it = 0
        do {
            elfPositions = iterate(elfPositions, it++)
        } while (hasMoved)
        println(it)
    }
}