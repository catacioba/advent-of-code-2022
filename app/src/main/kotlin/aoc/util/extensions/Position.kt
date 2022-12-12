package aoc.util.extensions

import aoc.util.Position

fun Position.neighbors(): Sequence<Position> {
    return sequenceOf(
        Position(1, 0),
        Position(0, 1),
        Position(-1, 0),
        Position(0, -1)
    ).map { this + it }
}

fun Position.isInBounds(xRange: IntRange, yRange: IntRange): Boolean =
    x in xRange && y in yRange