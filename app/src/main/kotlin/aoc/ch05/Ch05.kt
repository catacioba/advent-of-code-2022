package aoc.ch05

import aoc.Challenge

class Ch05 : Challenge {
    private fun printStacks(stacks: List<ArrayDeque<Char>>) {
        stacks.forEach { it ->
            println(
                it.joinToString(
                    separator = " ",
                    prefix = "[",
                    postfix = "]"
                )
            )
        }
    }

    private fun parseStacks(
        stacksPart: List<String>,
        stackCount: Int
    ): List<ArrayDeque<Char>> {
        val stacks = List(stackCount) { ArrayDeque<Char>() }
        stacksPart.asReversed()
            .forEach { l ->
                (0 until stackCount).map { p ->
                    val s = l.getOrElse(1 + 4 * p) { ' ' }
                    if (!s.isWhitespace()) {
                        stacks[p].add(s)
                    }
                }
            }
        return stacks
    }

    override fun partOne(input: String) {
        val lines = input.lines()
        val split = lines.indexOfFirst { it.isBlank() }

        val stackCount = lines[split - 1].split(' ').last().toInt()

        val stacksPart = lines.subList(0, split - 1)
        val stacks = parseStacks(stacksPart, stackCount)

        lines.subList(split + 1, lines.count())
            .forEach {
                val parts = it.split(' ')
                val count = parts[1].toInt()
                val from = parts[3].toInt()
                val to = parts[5].toInt()

                repeat(count) {
                    stacks[to - 1].add(stacks[from - 1].last())
                    stacks[from - 1].removeLast()
                }
            }

        println(stacks.map { it.last() }.joinToString(separator = ""))
    }

    override fun partTwo(input: String) {
        val lines = input.lines()
        val split = lines.indexOfFirst { it.isBlank() }

        val stackCount = lines[split - 1].split(' ').last().toInt()

        val stacksPart = lines.subList(0, split - 1)
        val stacks = parseStacks(stacksPart, stackCount)

        lines.subList(split + 1, lines.count())
            .forEach {
                val parts = it.split(' ')
                val count = parts[1].toInt()
                val from = parts[3].toInt()
                val to = parts[5].toInt()

                stacks[to - 1].addAll(stacks[from - 1].takeLast(count))
                repeat(count) {
                    stacks[from - 1].removeLast()
                }
            }

        println(stacks.map { it.last() }.joinToString(separator = ""))
    }
}