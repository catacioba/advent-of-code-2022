package aoc.ch15

import aoc.Challenge
import aoc.util.Interval
import aoc.util.Position
import kotlin.math.abs

class Ch15 : Challenge {

    data class SensorBeacon(val sensor: Position, val beacon: Position) {
        companion object {
            fun fromLine(l: String): SensorBeacon {
                fun parsePosition(s: String): Position {
                    val parts = s.split(", ")
                    return Position(
                        parts[0].removePrefix("x=").toInt(),
                        parts[1].removePrefix("y=").toInt()
                    )
                }

                val parts = l.split(": ")
                return SensorBeacon(
                    parsePosition(parts[0].removePrefix("Sensor at ")),
                    parsePosition(parts[1].removePrefix("closest beacon is at "))
                )
            }
        }

        private val distance = sensor.distance(beacon)

        private val highestPoint = sensor.y + distance

        private val lowestPoint = sensor.y - distance

        fun intersectsWithLine(y: Int): Boolean = y in lowestPoint..highestPoint

        fun getIntersectionXInterval(y: Int): Interval {
            val dy = abs(sensor.y - y)
            return Interval(sensor.x - distance + dy, distance - dy + sensor.x)
        }
    }

    private fun mergeIntervals(intervals: List<Interval>): List<Interval> {
        val sorted =
            ArrayDeque(
                intervals.sortedWith(
                    compareBy(
                        { it.left },
                        { it.right })
                )
            )
        val merged = mutableListOf<Interval>()

        var interval = sorted.removeFirst()

        while (sorted.isNotEmpty()) {
            val current = sorted.removeFirst()

            interval = if (interval.overlaps(current)) {
                interval.merge(current)
            } else {
                merged.add(interval)
                current
            }
        }

        merged.add(interval)

        return merged
    }

    override fun partOne(input: String, debug: Boolean, isTestRun: Boolean) {
        val sensorBeacons =
            input.lineSequence().map(SensorBeacon::fromLine).toList()

        val y = if (isTestRun) 20 else 2000000

        val intervals = sensorBeacons
            .asSequence()
            .filter { it.intersectsWithLine(y) }
            .map { it.getIntersectionXInterval(y) }
            .toList()
        if (debug) {
            println(intervals)
        }

        val mergedIntervals = mergeIntervals(intervals)
        if (debug) {
            println(mergedIntervals)
        }

        val beaconsOnLine = sensorBeacons
            .asSequence()
            .map(SensorBeacon::beacon)
            .filter { it.y == y }
            .filter { b -> mergedIntervals.any { i -> i.left <= b.x && i.right >= b.x } }
            .toSet()
            .size
        if (debug) {
            println(beaconsOnLine)
        }

        println(mergedIntervals.sumOf { it.right - it.left + 1 } - beaconsOnLine)
    }

    override fun partTwo(input: String, debug: Boolean, isTestRun: Boolean) {
        val sensorBeacons =
            input.lineSequence().map(SensorBeacon::fromLine).toList()

        val maxY = if (isTestRun) 20 else 4000000

        for (y in 0..maxY) {
            val intervals = sensorBeacons
                .asSequence()
                .filter { it.intersectsWithLine(y) }
                .map { it.getIntersectionXInterval(y) }
                .toList()

            val mergedIntervals = mergeIntervals(intervals)

            if (mergedIntervals.size > 1) {
                println(y)
                println(mergedIntervals)
            }
        }
    }
}