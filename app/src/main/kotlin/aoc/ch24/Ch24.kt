package aoc.ch24

import aoc.Challenge
import aoc.util.Position
import aoc.util.extensions.neighbors
import java.util.*

class Ch24 : Challenge {

    data class Blizzard(val position: Position, val direction: Char)

    private fun findBlizzards(map: List<String>): List<Blizzard> {
        return map.flatMapIndexed { x, l ->
            l.mapIndexedNotNull { y, c ->
                if (c != '#' && c != '.') Blizzard(Position(x, y), c) else null
            }
        }.toList()
    }

    data class BlizzardInformation(
        val blizzards: List<Blizzard>,
        val blizzardPositions: Set<Position>
    )

    class BlizzardByEpochGenerator(
        initialBlizzards: List<Blizzard>,
        private val height: Int,
        private val width: Int
    ) {
        private val blizzardsByEpoch = mutableMapOf<Int, BlizzardInformation>()

        init {
            blizzardsByEpoch[0] = BlizzardInformation(
                initialBlizzards,
                initialBlizzards.asSequence().map { it.position }.toSet()
            )
        }

        fun getBlizzardByEpoch(epoch: Int): BlizzardInformation {
            if (blizzardsByEpoch.contains(epoch)) {
                return blizzardsByEpoch[epoch]!!
            }
            if (epoch < 0) {
                throw IllegalArgumentException("negative epoch")
            }
            val previousBlizzards = getBlizzardByEpoch(epoch - 1).blizzards
            val newBlizzards = previousBlizzards.map {
                var newPosition = when (it.direction) {
                    '>' -> it.position + Position(0, 1)
                    '<' -> it.position + Position(0, -1)
                    '^' -> it.position + Position(-1, 0)
                    'v' -> it.position + Position(1, 0)
                    else -> throw IllegalArgumentException("Invalid blizzard ${it.direction}")
                }
                if (newPosition.x == 0) {
                    newPosition = Position(height - 2, newPosition.y)
                }
                if (newPosition.x == height - 1) {
                    newPosition = Position(1, newPosition.y)
                }
                if (newPosition.y == 0) {
                    newPosition = Position(newPosition.x, width - 2)
                }
                if (newPosition.y == width - 1) {
                    newPosition = Position(newPosition.x, 1)
                }
                Blizzard(newPosition, it.direction)
            }
            val blizzardInformation = BlizzardInformation(
                newBlizzards,
                newBlizzards.asSequence().map { it.position }.toSet()
            )
            blizzardsByEpoch[epoch] = blizzardInformation
            return blizzardInformation
        }

    }

    data class BfsState(
        val position: Position,
        val epoch: Int,
    )

    private fun bfs(
        startState: BfsState,
        end: Position,
        map: List<String>,
        blizzardByEpochGenerator: BlizzardByEpochGenerator,
    ): BfsState {
        val height = map.size
        val width = map.first().length

        val queue = ArrayDeque<BfsState>()
        val visited = mutableSetOf(startState)
        queue.add(startState)

        while (queue.isNotEmpty()) {
            val state = queue.remove()

            if (state.position == end) {
                return state
            }

            val nextBlizzards =
                blizzardByEpochGenerator.getBlizzardByEpoch(state.epoch + 1)

            for (next in (state.position.neighbors() + sequenceOf(state.position))) {
                if (next == end || next == startState.position ||
                    (next.x >= 1 && next.x < height - 1 && next.y >= 1 && next.y < width - 1 &&
                     !nextBlizzards.blizzardPositions.contains(next))
                ) {
                    val nextState =
                        BfsState(next, state.epoch + 1)
                    if (!visited.contains(nextState)) {
                        visited.add(nextState)
                        queue.add(nextState)
                    }
                }
            }
        }

        throw IllegalArgumentException("No solution found")
    }

    override fun partOne(input: String, debug: Boolean, isTestRun: Boolean) {
        val map = input.lines()

        val blizzards = findBlizzards(map)
        val blizzardByEpochGenerator =
            BlizzardByEpochGenerator(blizzards, map.size, map.first().length)

        val start = Position(0, 1)
        val end = Position(map.lastIndex, map.last().lastIndex - 1)

        println(
            bfs(
                BfsState(start, 0),
                end,
                map,
                blizzardByEpochGenerator
            ).epoch
        )
    }

    override fun partTwo(input: String, debug: Boolean, isTestRun: Boolean) {
        val map = input.lines()

        val blizzards = findBlizzards(map)
        val blizzardByEpochGenerator =
            BlizzardByEpochGenerator(blizzards, map.size, map.first().length)

        val start = Position(0, 1)
        val end = Position(map.lastIndex, map.last().lastIndex - 1)

        val s1 = bfs(BfsState(start, 0), end, map, blizzardByEpochGenerator)
        val s2 = bfs(s1, start, map, blizzardByEpochGenerator)
        val s3 = bfs(s2, end, map, blizzardByEpochGenerator)
        println(s3.epoch)
    }
}