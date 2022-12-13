package aoc.ch13

import aoc.Challenge

class Ch13 : Challenge {

    sealed class Packet : Comparable<Packet> {
        companion object {
            fun fromLine(line: String): Packet {
                fun fromLineAux(
                    it: Iterator<Char>,
                    accum: Int?,
                    listAccum: MutableList<Packet>
                ): Packet {
                    return when (val c = it.next()) {
                        '[' -> {
                            val inner = fromLineAux(it, null, mutableListOf())
                            listAccum.add(inner)
                            if (it.hasNext()) {
                                fromLineAux(it, null, listAccum)
                            } else {
                                inner
                            }
                        }

                        in '0'..'9' -> fromLineAux(
                            it,
                            (accum?.times(10) ?: 0) + c.digitToInt(),
                            listAccum
                        )

                        ',' -> fromLineAux(
                            it,
                            null,
                            listAccum.apply {
                                if (accum != null) {
                                    add(IntPacket(accum))
                                }
                            })

                        ']' -> {
                            ListPacket(listAccum.apply {
                                if (accum != null) {
                                    add(IntPacket(accum))
                                }
                            })
                        }

                        else -> {
                            throw IllegalArgumentException("invalid option $c")
                        }
                    }
                }

                return fromLineAux(line.iterator(), 0, mutableListOf())
            }

            private fun fromBlock(block: String): Pair<Packet, Packet> {
                val packets = block.lineSequence().map(this::fromLine).toList()
                return Pair(packets[0], packets[1])
            }

            fun fromBlocks(blocks: String): List<Pair<Packet, Packet>> {
                return blocks.splitToSequence("\n\n")
                    .map(this::fromBlock)
                    .toList()
            }
        }

        override fun compareTo(other: Packet): Int {
            return when (this) {
                is IntPacket -> {
                    when (other) {
                        is IntPacket -> this.value.compareTo(other.value)

                        is ListPacket -> this.toListPacket().compareTo(other)
                    }
                }

                is ListPacket -> {
                    when (other) {
                        is IntPacket -> this.compareTo(other.toListPacket())
                        is ListPacket -> {
                            val leftIter = this.values.iterator()
                            val rightIter = other.values.iterator()

                            while (leftIter.hasNext() && rightIter.hasNext()) {
                                val c =
                                    leftIter.next().compareTo(rightIter.next())
                                if (c != 0) {
                                    return c
                                }
                            }

                            if (this.values.size != other.values.size) {
                                return if (this.values.size < other.values.size) -1 else 1
                            }

                            return 0
                        }
                    }
                }
            }
        }
    }

    data class IntPacket(val value: Int) : Packet() {
        fun toListPacket(): ListPacket = ListPacket(listOf(this))

        override fun toString(): String = value.toString()
    }

    data class ListPacket(val values: List<Packet>) : Packet() {
        override fun toString(): String =
            values.joinToString(separator = ",", postfix = "]", prefix = "[")
    }

    override fun partOne(input: String, debug: Boolean) {
        val pairs = Packet.fromBlocks(input)

        println(pairs.mapIndexed { idx, p ->
            if (p.first.compareTo(p.second) <= 0) idx + 1 else 0
        }.sum())
    }

    override fun partTwo(input: String, debug: Boolean) {
        val pairs =
            input.lineSequence()
                .filterNot { it.isBlank() }
                .map(Packet::fromLine)
                .sorted()
                .toList()

        if (debug) {
            pairs.forEach { println(it) }
        }

        val a = Packet.fromLine("[[2]]")
        val b = Packet.fromLine("[[6]]")

        val aPosition = -pairs.binarySearch(a)
        val bPosition = -pairs.binarySearch(b) + 1

        println(bPosition * aPosition)
    }
}