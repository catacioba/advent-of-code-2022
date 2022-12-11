package aoc.ch11

import aoc.Challenge

class Ch11 : Challenge {

    sealed class Operation(open val value: Long?) {
        abstract fun apply(oldValue: Long): Long
    }

    data class Plus(override val value: Long?) : Operation(value) {
        override fun apply(oldValue: Long): Long =
            value?.plus(oldValue) ?: (oldValue + oldValue)
    }

    data class Multiply(override val value: Long?) : Operation(value) {
        override fun apply(oldValue: Long): Long =
            value?.times(oldValue) ?: (oldValue * oldValue)
    }

    class Monkey(
        val id: Int,
        val items: ArrayDeque<Long>,
        val operation: Operation,
        val testValue: Int,
        val trueTargetId: Int,
        val falseTargetId: Int
    ) {
        companion object {
            fun fromBlock(block: String): Monkey {
                val parts = block.lines()
                val id =
                    parts[0].removePrefix("Monkey ").removeSuffix(":").toInt()
                val items =
                    ArrayDeque(
                        parts[1].removePrefix("  Starting items: ")
                            .split(", ")
                            .map { it.toLong() })
                val p = parts[2].removePrefix("  Operation: new = old ")
                val value = when (val valueStr = p.drop(2)) {
                    "old" -> null
                    else -> valueStr.toLong()
                }
                val operation = if (p.startsWith('+')) {
                    Plus(value)
                } else {
                    Multiply(value)
                }
                val testValue =
                    parts[3].removePrefix("  Test: divisible by ").toInt()
                val trueTargetId =
                    parts[4].removePrefix("    If true: throw to monkey ")
                        .toInt()
                val falseTargetId =
                    parts[5].removePrefix("    If false: throw to monkey ")
                        .toInt()

                return Monkey(
                    id,
                    items,
                    operation,
                    testValue,
                    trueTargetId,
                    falseTargetId
                )
            }
        }

        var count: Long = 0
    }

    private fun playRound(
        monkeys: List<Monkey>,
        decreaseWorryLevel: Boolean = true,
        modulo: Int? = null,
    ) {
        for (monkey in monkeys) {
            for (item in monkey.items) {
                var newWorryLevel = monkey.operation.apply(item)
                if (modulo != null) {
                    newWorryLevel %= modulo
                }
                if (decreaseWorryLevel) {
                    newWorryLevel /= 3
                }
                if (newWorryLevel % monkey.testValue == 0L) {
                    monkeys[monkey.trueTargetId].items.addLast(newWorryLevel)
                } else {
                    monkeys[monkey.falseTargetId].items.addLast(newWorryLevel)
                }
            }
            monkey.count += monkey.items.size
            monkey.items.clear()
        }
    }

    private fun List<Monkey>.monkeyBusiness(): Long {
        return this.map { it.count }
            .sortedDescending()
            .take(2)
            .reduce { acc, i -> acc * i }
    }

    override fun partOne(input: String, debug: Boolean) {
        val monkeys = input.split("\n\n").map(Monkey::fromBlock)
        repeat(20) {
            playRound(monkeys)
        }
        println(monkeys.monkeyBusiness())
    }

    override fun partTwo(input: String, debug: Boolean) {
        val monkeys = input.split("\n\n").map(Monkey::fromBlock)
        val modulo =
            monkeys.asSequence().map { it.testValue }.reduce { a, i -> a * i }
        repeat(10000) {
            playRound(monkeys, decreaseWorryLevel = false, modulo)

            if (debug) {
                val round = it + 1
                if (round == 1 || round == 20 || round % 1000 == 0) {
                    for (monkey in monkeys) {
                        println("Monkey ${monkey.id} inspected items ${monkey.count} times.")
                    }
                    println()
                }
            }
        }
        println(monkeys.monkeyBusiness())
    }
}