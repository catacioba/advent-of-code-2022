package aoc.ch22

import aoc.Challenge
import aoc.util.Position
import kotlin.math.sqrt

class Ch22 : Challenge {

    data class PositionOrientation(
        val position: Position,
        val orientation: Int
    ) {
        fun getPositionScore(): Int {
            return (position.x + 1) * 1000 + (position.y + 1) * 4 + orientation
        }

        private val orientationDeltas =
            listOf(
                Position(0, 1),
                Position(1, 0),
                Position(0, -1),
                Position(-1, 0)
            )

        fun next(): PositionOrientation {
            val nextPosition =
                position + orientationDeltas[orientation]
            return PositionOrientation(nextPosition, orientation)
        }

        fun rotateRight(): PositionOrientation {
            val nextOrientation = (orientation + 1) % orientationDeltas.size
            return PositionOrientation(position, nextOrientation)
        }

        fun rotateLeft(): PositionOrientation {
            val nextOrientation =
                (orientationDeltas.size + orientation - 1) % orientationDeltas.size
            return PositionOrientation(position, nextOrientation)
        }

        fun isHorizontal(): Boolean {
            return orientation % 2 == 0
        }
    }

    private fun followInstructions(
        map: List<String>,
        instructions: String,
        wrappingRules: WrappingRules,
        debug: Boolean = false
    ): PositionOrientation {
        var current = PositionOrientation(Position(0, map[0].indexOf('.')), 0)

        var accum = 0

        val steps = mutableMapOf<Position, Char>()

        fun draw() {
            for (x in map.indices) {
                for (y in map[x].indices) {
                    val c = steps[Position(x, y)]
                    if (c != null) {
                        print(c)
                    } else {
                        print(map[x][y])
                    }
                }
                println()
            }
            println()
        }

        fun move() {
            if (accum > 0) {
                var hitWall = false

                while (accum > 0 && !hitWall) {
                    steps[current.position] = when (current.orientation) {
                        0 -> '>'
                        1 -> 'v'
                        2 -> '<'
                        3 -> '^'
                        else -> throw IllegalArgumentException("invalid state")
                    }
                    accum -= 1
                    val next =
                        wrappingRules.nextPositionWrapped(current)
                    if (map[next.position.x][next.position.y] != '#') {
                        current = next
                    } else {
                        hitWall = true
                    }
                }
                if (debug) {
                    draw()
                }
                accum = 0
            }
        }

        for (c in instructions) {
            if (c.isDigit()) {
                accum *= 10
                accum += c.digitToInt()
                continue
            }
            move()
            if (c == 'R') {
                current = current.rotateRight()
                continue
            }
            current = current.rotateLeft()
        }
        move()
        return current
    }

    interface WrappingRules {
        fun nextPositionWrapped(po: PositionOrientation): PositionOrientation
    }

    class SimpleWrappingRules(private val map: List<String>) : WrappingRules {

        private val horizontalEdges = computeHorizontalEdges()
        private val verticalEdges = computeVerticalEdges()

        override fun nextPositionWrapped(po: PositionOrientation): PositionOrientation {
            val next = po.next()
            return if (next.isHorizontal()) {
                horizontalEdges[next] ?: next
            } else {
                verticalEdges[next] ?: next
            }
        }

        private fun computeHorizontalEdges(): Map<PositionOrientation, PositionOrientation> {
            val edges = mutableMapOf<PositionOrientation, PositionOrientation>()
            for (x in map.indices) {
                val line = map[x]
                val left = line.indexOfFirst { !it.isWhitespace() }
                val right = line.lastIndex

                edges[PositionOrientation(Position(x, left - 1), 2)] =
                    PositionOrientation(Position(x, right), 2)
                edges[PositionOrientation(Position(x, right + 1), 0)] =
                    PositionOrientation(Position(x, left), 0)
            }
            return edges
        }

        private fun computeVerticalEdges(): Map<PositionOrientation, PositionOrientation> {
            val edges = mutableMapOf<PositionOrientation, PositionOrientation>()
            val maxY = map.maxOf { it.length }
            for (y in 0 until maxY) {
                val top =
                    map.indexOfFirst { it.length > y && !it[y].isWhitespace() }
                val bottom =
                    map.indexOfLast { it.length > y && !it[y].isWhitespace() }

                edges[PositionOrientation(Position(top - 1, y), 3)] =
                    PositionOrientation(Position(bottom, y), 3)
                edges[PositionOrientation(Position(bottom + 1, y), 1)] =
                    PositionOrientation(Position(top, y), 1)
            }
            return edges
        }
    }

    data class TileOrientation(val tileId: Int, val orientation: Int)

    data class Neighbor(
        val tileId: Int,
        val orientation: Int,
        val isReversed: Boolean
    )

    class CubeWrappingRules(
        private val map: List<String>,
        private val blockPositions: Map<TileOrientation, Neighbor>
    ) : WrappingRules {
        private val size =
            sqrt((map.sumOf { it.count { c -> !c.isWhitespace() } } / 6).toDouble()).toInt()

        // Top left corner of each tile
        private val tileIdsPosition = computeTileIds()

        override fun nextPositionWrapped(po: PositionOrientation): PositionOrientation {
            val next = po.next()

            if (next.position.x >= 0 &&
                next.position.y >= 0 &&
                map.size > next.position.x &&
                map[next.position.x].length > next.position.y &&
                !map[next.position.x][next.position.y].isWhitespace()
            ) {
                return next
            }

            val currentTileId = getTileForPosition(po.position)!!
            val nextTileId = getTileForPosition(next.position)

            if (nextTileId == null || currentTileId != nextTileId) {
                val neighbor =
                    blockPositions[TileOrientation(
                        currentTileId,
                        po.orientation
                    )]!!

                val tilePosition = tileIdsPosition[currentTileId]!!
//                val nextTilePosition = tileIdsPosition[neighbor.tileId]C!!
                val nextTilePosition = tileIdsPosition[neighbor.tileId]!!

//                val currentX = (po.position.x - tilePosition.x) % size
//                val currentY = (po.position.y - tilePosition.y) % size
//                val idx = max(currentX, currentY)
                val idx = when (po.orientation) {
                    0 -> po.position.x - tilePosition.x
                    1 -> po.position.y - tilePosition.y
                    2 -> po.position.x - tilePosition.x
                    3 -> po.position.y - tilePosition.y
                    else -> throw IllegalArgumentException("invalid state")
                }

                val positionDelta = when (neighbor.orientation) {
                    0 -> {
                        if (neighbor.isReversed) Position(
                            size - 1 - idx,
                            0
                        ) else Position(idx, 0)
                    }

                    1 -> {
                        if (neighbor.isReversed) Position(
                            0,
                            size - 1 - idx
                        ) else Position(0, idx)
                    }

                    2 -> {
                        if (neighbor.isReversed) Position(
                            size - 1 - idx,
                            size - 1
                        ) else Position(idx, size - 1)
                    }

                    3 -> {
                        if (neighbor.isReversed) Position(
                            size - 1,
                            size - 1 - idx
                        ) else Position(size - 1, idx)
                    }

                    else -> throw IllegalArgumentException("invalid state")
                }

                return PositionOrientation(
                    nextTilePosition + positionDelta,
                    neighbor.orientation
                )
            }

            return next
        }

        private fun getTileForPosition(p: Position): Int? {
            return tileIdsPosition.entries.firstOrNull {
                it.value.x <= p.x && it.value.x + size > p.x && it.value.y <= p.y && it.value.y + size > p.y
            }?.key
        }

        private fun computeTileIds(): Map<Int, Position> {
            var tileId = 1
            val tilePositions = mutableMapOf<Int, Position>()

            for (x in map.indices step size) {
                var y = map[x].indexOfFirst { !it.isWhitespace() }
                while (map[x].length > y) {
                    tilePositions[tileId++] = Position(x, y)
                    y += size
                }
            }

            return tilePositions
        }
    }

    override fun partOne(input: String, debug: Boolean, isTestRun: Boolean) {
        val parts = input.split("\n\n")
        val map = parts[0].lines()
        val instructions = parts[1]

        val wrappingRules = SimpleWrappingRules(map)
        val op = followInstructions(map, instructions, wrappingRules)
        println(op.getPositionScore())
    }

    override fun partTwo(input: String, debug: Boolean, isTestRun: Boolean) {
        val parts = input.split("\n\n")
        val map = parts[0].lines()
        val instructions = parts[1]

        val blockPositions = if (isTestRun) mapOf(
            TileOrientation(1, 0) to Neighbor(6, 2, true),
            TileOrientation(1, 2) to Neighbor(3, 1, false),
            TileOrientation(1, 3) to Neighbor(2, 1, true),
            TileOrientation(2, 1) to Neighbor(5, 3, true),
            TileOrientation(2, 2) to Neighbor(6, 3, true),
            TileOrientation(2, 3) to Neighbor(1, 2, true),
            TileOrientation(3, 1) to Neighbor(5, 0, true),
            TileOrientation(3, 3) to Neighbor(1, 0, false),
            TileOrientation(4, 0) to Neighbor(6, 1, true),
            TileOrientation(5, 1) to Neighbor(2, 3, true),
            TileOrientation(5, 2) to Neighbor(3, 3, true),
            TileOrientation(6, 0) to Neighbor(1, 2, true),
            TileOrientation(6, 1) to Neighbor(2, 0, true),
            TileOrientation(6, 3) to Neighbor(4, 2, true)
        ) else mapOf(
            TileOrientation(1, 2) to Neighbor(4, 0, true),
            TileOrientation(1, 3) to Neighbor(6, 0, false),
            TileOrientation(2, 0) to Neighbor(5, 2, true),
            TileOrientation(2, 1) to Neighbor(3, 2, false),
            TileOrientation(2, 3) to Neighbor(6, 3, false),
            TileOrientation(3, 0) to Neighbor(2, 3, false),
            TileOrientation(3, 2) to Neighbor(4, 1, false),
            TileOrientation(4, 2) to Neighbor(1, 0, true),
            TileOrientation(4, 3) to Neighbor(3, 0, false),
            TileOrientation(5, 0) to Neighbor(2, 2, true),
            TileOrientation(5, 1) to Neighbor(6, 2, false),
            TileOrientation(6, 0) to Neighbor(5, 3, false),
            TileOrientation(6, 1) to Neighbor(2, 1, false),
            TileOrientation(6, 2) to Neighbor(1, 1, false),
        )
        val wrappingRules = CubeWrappingRules(map, blockPositions)
        val op = followInstructions(map, instructions, wrappingRules, debug)
        println(op)
        println(op.getPositionScore())
    }
}