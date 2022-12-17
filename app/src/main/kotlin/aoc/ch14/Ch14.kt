package aoc.ch14

import aoc.Challenge
import aoc.util.Position
import kotlin.math.max
import kotlin.math.min

class Ch14 : Challenge {

    data class Line(val pairs: List<Position>) {
        fun points(): Sequence<Position> = sequence {
            pairs.zipWithNext().forEach {
                val l = it.first
                val r = it.second

                val fromX = min(r.x, l.x)
                val untilX = max(r.x, l.x)
                val fromY = min(r.y, l.y)
                val untilY = max(r.y, l.y)

                for (x in fromX..untilX) {
                    for (y in fromY..untilY) {
                        yield(Position(x, y))
                    }
                }
            }
        }
    }

    enum class Tile {
        Air, Sand, Rock, Source
    }

    class Grid(val map: MutableMap<Position, Tile>) {
        companion object {
            fun fromInput(input: String): Grid {
                val lines = input.lineSequence()
                    .map { l ->
                        l.splitToSequence(" -> ")
                            .map { t -> t.split(',') }
                            .map { Position(it[0].toInt(), it[1].toInt()) }
                            .toList()
                    }
                    .map { Line(it) }
                    .toList()

                val points =
                    lines.flatMap { l -> l.points().map { it to Tile.Rock } }
                        .toMap()
                        .toMutableMap()

                points[Position(500, 0)] = Tile.Source

                return Grid(points)
            }
        }

        fun simulate() {
            val yBoundsByX: Map<Int, Int> = map.keys
                .groupBy { it.x }
                .mapValues { l -> l.value.maxOf { it.y } }

            val downDelta = Position(0, 1)
            val downLeftDelta = Position(-1, 1)
            val downRightDelta = Position(1, 1)

            var modified: Position?
            fun simulateAux(s: Position) {
                if (!yBoundsByX.contains(s.x) || yBoundsByX[s.x]!! < s.y) {
                    return
                }
                val down = s + downDelta

                if (map.contains(down)) {
                    val downLeft = s + downLeftDelta

                    if (map.contains(downLeft)) {
                        val downRight = s + downRightDelta

                        if (map.contains(downRight)) {
                            modified = s
                            map[s] = Tile.Sand
                        } else {
                            simulateAux(downRight)
                        }
                    } else {
                        simulateAux(downLeft)
                    }
                } else {
                    simulateAux(down)
                }
            }

            val nextFromSource = Position(500, 0)
            do {
                modified = null
                simulateAux(nextFromSource)
            } while (modified != null && modified != nextFromSource)
        }

        private fun minY() = map.keys.minOf { it.y }
        fun maxY() = map.keys.maxOf { it.y }
        fun minX() = map.keys.minOf { it.x }
        fun maxX() = map.keys.maxOf { it.x }

        fun draw() {
            for (y in min(minY(), 0)..max(maxY(), 0)) {
                for (x in min(minX(), 500)..max(500, maxX())) {
                    val t = map.getOrDefault(Position(x, y), Tile.Air)
                    print(
                        when (t) {
                            Tile.Air -> '.'
                            Tile.Sand -> 'o'
                            Tile.Rock -> '#'
                            Tile.Source -> '+'
                        }
                    )
                }
                println()
            }
        }
    }

    override fun partOne(input: String, debug: Boolean, isTestRun: Boolean) {
        val grid = Grid.fromInput(input)

        grid.simulate()

        if (debug) {
            grid.draw()
        }

        println(grid.map.values.count { it == Tile.Sand })
    }

    override fun partTwo(input: String, debug: Boolean, isTestRun: Boolean) {
        val grid = Grid.fromInput(input)

        val y = 2 + grid.maxY()
        grid.map.putAll(((grid.minX() - y)..(grid.maxX() + y)).associate { x ->
            Position(x, y) to Tile.Rock
        })

        grid.simulate()

        if (debug) {
            grid.draw()
        }

        println(grid.map.values.count { it == Tile.Sand })
    }
}