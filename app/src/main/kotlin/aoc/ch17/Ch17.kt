package aoc.ch17

import aoc.Challenge
import aoc.util.Position
import java.lang.Integer.max

class Ch17 : Challenge {

    data class State(val topThree: String, val nextJet: Int, val nextRock: Int)
    data class Statistics(val epoch: Long, val height: Int)

    class Game(private val jets: String) {
        private val tiles = mutableSetOf<Position>()

        private var height: Int = 0
        private val seenStates = mutableMapOf<State, Statistics>()

        fun simulate(epochs: Long): Long? {
            var it = 0

            for (epoch in 1..epochs) {
                var rock = getRock(epoch)

                while (true) {
                    val jet = jets[it % jets.length]
                    it++

                    rock = when (jet) {
                        '>' -> moveRight(rock)
                        '<' -> moveLeft(rock)
                        else -> throw IllegalArgumentException("Unreachable state")
                    }

                    val downRock = moveDown(rock)
                    if (downRock == null) {
                        tiles.addAll(rock.tiles)
                        height = max(height, rock.tiles.maxOf { it.y } + 1)
                        break
                    } else {
                        rock = downRock
                    }
                }

                val state =
                    State(
                        topThree(),
                        it % jets.length,
                        (epochs % 5).toInt()
                    )
                if (seenStates.contains(state)) {
                    val seenState = seenStates[state]!!
                    val loopEpochSize = epoch - seenState.epoch
                    val loopHeightSize = height - seenState.height

                    val prevState = seenStates.asSequence().find {
                        it.value.epoch == seenState.epoch - 1
                    }!!
                    val remainingHeight = seenStates.asSequence().find {
                        it.value.epoch == seenState.epoch + (epochs - seenState.epoch) % loopEpochSize
                    }!!.value.height

                    return (epochs - seenState.epoch) / loopEpochSize * loopHeightSize + remainingHeight
                } else {
                    seenStates[state] = Statistics(epoch, height)
                }
            }
            return null
        }

        private fun moveLeft(r: Rock) = move(r, Position(-1, 0)) ?: r
        private fun moveRight(r: Rock) = move(r, Position(1, 0)) ?: r
        private fun moveDown(r: Rock) = move(r, Position(0, -1))

        private fun move(r: Rock, d: Position): Rock? {
            val newTiles =
                r.tiles.asSequence()
                    .map { it + d }
                    .filter { it.x in 0..6 && it.y >= 0 && !tiles.contains(it) }
                    .toSet()
            if (newTiles.size != r.tiles.size) {
                return null
            }
            return when (r) {
                is Corner -> Corner(newTiles)
                is Line -> Line(newTiles)
                is Minus -> Minus(newTiles)
                is Plus -> Plus(newTiles)
                is Square -> Square(newTiles)
            }
        }

        private fun getRock(r: Long): Rock {
            return when ((r - 1) % 5) {
                0L -> Minus(
                    setOf(
                        Position(2, height + 3),
                        Position(3, height + 3),
                        Position(4, height + 3),
                        Position(5, height + 3)
                    )
                )

                1L -> Plus(
                    setOf(
                        Position(2, height + 4),
                        Position(3, height + 4),
                        Position(4, height + 4),
                        Position(3, height + 5),
                        Position(3, height + 3)
                    )
                )

                2L -> Corner(
                    setOf(
                        Position(2, height + 3),
                        Position(3, height + 3),
                        Position(4, height + 3),
                        Position(4, height + 4),
                        Position(4, height + 5)
                    )
                )

                3L -> Line(
                    setOf(
                        Position(2, height + 3),
                        Position(2, height + 4),
                        Position(2, height + 5),
                        Position(2, height + 6)
                    )
                )

                4L -> Square(
                    setOf(
                        Position(2, height + 3),
                        Position(3, height + 3),
                        Position(2, height + 4),
                        Position(3, height + 4)
                    )
                )

                else -> throw IllegalArgumentException("Unreachable state")
            }
        }

        fun draw(rock: Rock? = null) {
            for (y in (height + 5) downTo 0) {
                print('|')
                for (x in 0..6) {
                    val p = Position(x, y)
                    if (tiles.contains(p)) {
                        print('#')
                    } else if (rock?.tiles?.contains(p) == true) {
                        print('@')
                    } else {
                        print('.')
                    }
                }
                println('|')
            }

            print('+')
            for (x in 0..6) {
                print('-')
            }
            println('+')
            println()
        }

        private fun topThree(): String {
            return (1..3).joinToString(separator = "\n") { dy ->
                (0..6).map {
                    if (tiles.contains(Position(it, height - dy))) '#' else '.'
                }.joinToString(separator = "")
            }
        }
    }

    sealed class Rock(open val tiles: Set<Position>)

    data class Minus(override val tiles: Set<Position>) : Rock(tiles)
    data class Plus(override val tiles: Set<Position>) : Rock(tiles)
    data class Corner(override val tiles: Set<Position>) : Rock(tiles)
    data class Line(override val tiles: Set<Position>) : Rock(tiles)
    data class Square(override val tiles: Set<Position>) : Rock(tiles)

    override fun partOne(input: String, debug: Boolean, isTestRun: Boolean) {
        val game = Game(input)
        println(game.simulate(2022))
    }

    override fun partTwo(input: String, debug: Boolean, isTestRun: Boolean) {
        val rocks: Long = 1000000000000
        val game = Game(input)
        println(game.simulate(rocks))
    }
}