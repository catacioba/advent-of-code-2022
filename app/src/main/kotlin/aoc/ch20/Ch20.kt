package aoc.ch20

import aoc.Challenge

class Ch20 : Challenge {

    data class Number(val id: Int, val value: Long)

    private fun mix(
        numbers: List<Number>,
        iterationOrder: List<Int>
    ): List<Number> {
        val mixedNumbers = numbers.toMutableList()

        for (id in iterationOrder) {
            val oldPosition = mixedNumbers.indexOfFirst { it.id == id }
            val number = mixedNumbers[oldPosition]

            val newPosition =
                ((numbers.size - 1 + (oldPosition + number.value) % (numbers.size - 1)) % (numbers.size - 1)).toInt()
            if (oldPosition <= newPosition) {
                for (idx in oldPosition until newPosition) {
                    mixedNumbers[idx] = mixedNumbers[idx + 1]
                }
            } else {
                for (idx in oldPosition downTo (newPosition + 1)) {
                    mixedNumbers[idx] = mixedNumbers[idx - 1]
                }
            }
            mixedNumbers[newPosition] = number
        }

        return mixedNumbers
    }

    private fun List<Number>.circularIndex(idx: Int): Long {
        val zeroIndex = indexOfFirst { it.value == 0L }
        return this[(zeroIndex + idx) % size].value
    }

    private fun List<Number>.getGroveCoordinates(): Long {
        return circularIndex(1000) + circularIndex(2000) + circularIndex(3000)
    }

    private var nextId = 1

    override fun partOne(input: String, debug: Boolean, isTestRun: Boolean) {
        val numbers =
            input.lineSequence().map { Number(nextId++, it.toLong()) }.toList()

        val iterationOrder = numbers.map { it.id }
        val mixedNumbers = mix(numbers, iterationOrder)

        if (debug) {
            println(mixedNumbers)
            println(mixedNumbers.circularIndex(1000))
            println(mixedNumbers.circularIndex(2000))
            println(mixedNumbers.circularIndex(3000))
        }

        println(mixedNumbers.getGroveCoordinates())
    }

    override fun partTwo(input: String, debug: Boolean, isTestRun: Boolean) {
        val numbers =
            input.lineSequence().map { Number(nextId++, it.toLong()) }.toList()

        val decryptionKey = 811589153

        val iterationOrder = numbers.map { it.id }
        var mixedNumbers =
            numbers.map { Number(it.id, it.value * decryptionKey) }

        repeat(10) {
            mixedNumbers = mix(mixedNumbers, iterationOrder)
        }

        println(mixedNumbers.getGroveCoordinates())
    }
}