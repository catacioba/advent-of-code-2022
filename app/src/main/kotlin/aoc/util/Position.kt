package aoc.util

import kotlin.math.absoluteValue
import kotlin.math.sqrt

data class Position(val x: Int, val y: Int) {
    constructor() : this(0, 0)

    operator fun plus(ot: Position): Position =
        Position(x + ot.x, y + ot.y)

    operator fun minus(ot: Position): Position =
        Position(x - ot.x, y - ot.y)

    operator fun div(ot: Double): Position =
        Position((x / ot).toInt(), (y / ot).toInt())

    fun distance(ot: Position): Int =
        (x - ot.x).absoluteValue + (y - ot.y).absoluteValue

    fun scaled(): Position = this / length

    val length: Double = sqrt((x * x + y * y).toDouble())
}

