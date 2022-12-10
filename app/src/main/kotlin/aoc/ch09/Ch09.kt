package aoc.ch09

import aoc.Challenge
import kotlin.math.absoluteValue
import kotlin.math.sign
import kotlin.math.sqrt

class Ch09 : Challenge {

    sealed class Direction(open val steps: Int) {
        companion object {
            fun fromLine(l: String): Direction {
                val parts = l.split(' ')
                val steps = parts[1].toInt()
                return when (parts[0]) {
                    "R" -> Right(steps)
                    "L" -> Left(steps)
                    "U" -> Up(steps)
                    "D" -> Down(steps)
                    else -> throw IllegalArgumentException("Invalid option ${parts[0]}")
                }
            }
        }

        abstract val positionDelta: Position
    }

    data class Right(override val steps: Int) : Direction(steps) {
        override val positionDelta: Position
            get() = Position(1, 0)
    }

    data class Left(override val steps: Int) : Direction(steps) {
        override val positionDelta: Position
            get() = Position(-1, 0)
    }

    data class Up(override val steps: Int) : Direction(steps) {
        override val positionDelta: Position
            get() = Position(0, 1)
    }

    data class Down(override val steps: Int) : Direction(steps) {
        override val positionDelta: Position
            get() = Position(0, -1)
    }

    data class Position(val x: Int, val y: Int) {
        constructor() : this(0, 0)

        fun moveTowards(ot: Position): Position {
            if (this == ot) return this
            val d = distance(ot)
            if (x == ot.x || y == ot.y) {
                if (d == 1) {
                    return ot
                }
                return ot + (this - ot).scaled()
            }
            if (d == 2) {
                return ot
            }
            return ot + Position(
                sign((x - ot.x).toDouble()).toInt(),
                sign((y - ot.y).toDouble()).toInt()
            )
        }

        operator fun plus(ot: Position): Position =
            Position(x + ot.x, y + ot.y)

        operator fun minus(ot: Position): Position =
            Position(x - ot.x, y - ot.y)

        operator fun div(ot: Double): Position =
            Position((x / ot).toInt(), (y / ot).toInt())

        private fun distance(ot: Position): Int =
            (x - ot.x).absoluteValue + (y - ot.y).absoluteValue

        private fun scaled(): Position = this / length

        private val length: Double = sqrt((x * x + y * y).toDouble())
    }

    data class State2(val head: Position, val tail: Position) {
        fun move(d: Direction): List<State2> {
            return (1..d.steps).scan(this) { (h, t), _ ->
                val newHead = h + d.positionDelta
                val newTail = newHead.moveTowards(t)
                State2(newHead, newTail)
            }.drop(1)
        }
    }

    data class State(val positions: List<Position>) {
        constructor(n: Int) : this((1..n).map { Position() })

        fun move(d: Direction): List<State> {
            return (1..d.steps).scan(this) { s, _ ->
                var p = s.positions.first() + d.positionDelta
                val newPositions = mutableListOf(p)

                for (position in s.positions.drop(1)) {
                    p = p.moveTowards(position)
                    newPositions.add(p)
                }

                State(newPositions)
            }.drop(1)
        }

        val tail: Position = positions.last()
    }

    override fun partOne(input: String) {
        val directions = input.lineSequence().map(Direction::fromLine).toList()
        val state = State2(Position(), Position())
        val states = directions.fold(mutableListOf(state)) { states, d ->
            states.apply { addAll(states.last().move(d)) }
        }

        println(states.map { it.tail }.toSet().size)
    }

    override fun partTwo(input: String) {
        val directions = input.lineSequence().map(Direction::fromLine).toList()
        val state = State(10)
        val states = directions.fold(mutableListOf(state)) { states, d ->
            states.apply {
                addAll(states.last().move(d))
            }
        }

        println(states.map { it.tail }.toSet().size)
    }
}
