package aoc.ch10

import aoc.Challenge

class Ch10 : Challenge {

    sealed interface Instruction {
        companion object {
            fun fromLine(l: String): Instruction {
                val parts = l.split(' ')
                return when (parts[0]) {
                    "addx" -> AddX(parts[1].toInt())
                    "noop" -> NoOp
                    else -> throw IllegalArgumentException("Invalid instruction: ${parts[0]}")
                }
            }
        }

    }

    object NoOp : Instruction
    data class AddX(val value: Int) : Instruction

    private fun processInstructions(instructions: List<Instruction>): MutableMap<Int, Int> {
        var cycle = 1
        var x = 1

        val xByCycles = mutableMapOf<Int, Int>()

        for (instr in instructions) {
            when (instr) {
                is AddX -> {
                    xByCycles[cycle] = x
                    xByCycles[cycle + 1] = x
                    cycle += 2
                    x += instr.value
                }

                NoOp -> {
                    xByCycles[cycle] = x
                    cycle += 1
                }
            }
        }

        return xByCycles
    }

    override fun partOne(input: String, debug: Boolean) {
        val instructions =
            input.lineSequence().map(Instruction::fromLine).toList()

        val xByCycles = processInstructions(instructions)

        println((20..220 step 40).sumOf {
            it * xByCycles[it]!!
        })
    }

    override fun partTwo(input: String, debug: Boolean) {
        val instructions =
            input.lineSequence().map(Instruction::fromLine).toList()

        val xByCycles = processInstructions(instructions)

        (1..240).map {
            if (it > 1 && (it - 1) % 40 == 0) {
                println()
            }
            val x = xByCycles[it]!!
            val crt = (it - 1) % 40
            if (crt in x - 1..x + 1) {
                print('#')
            } else {
                print('.')
            }
        }
        println()
    }
}