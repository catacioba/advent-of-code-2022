package aoc.ch12

import aoc.Challenge
import aoc.util.Position
import aoc.util.extensions.isInBounds
import aoc.util.extensions.neighbors

class Ch12 : Challenge {

    private fun Char.getHeight(): Int {
        return when (this) {
            in 'a'..'z' -> 1 + this.code - 'a'.code
            'S' -> 1
            'E' -> 26
            else -> throw IllegalArgumentException("Invalid character $this")
        }
    }

    private fun List<String>.find(c: Char): Position {
        for (y in this.indices) {
            for (x in this[y].indices) {
                if (this[y][x] == c) {
                    return Position(x, y)
                }
            }
        }
        throw IllegalArgumentException("Could not find $c")
    }

    private fun List<String>.bfs(start: Position, end: Position): Int? {
        val dist = mutableMapOf<Position, Int>()
        val queue = ArrayDeque<Position>()

        queue.addLast(start)
        dist[start] = 0

        val h = this.size
        val w = this.first().length

        while (queue.isNotEmpty()) {
            val p = queue.removeFirst()
            val d = dist[p]!!

            for (n in p.neighbors()
                .filter { it.isInBounds(0 until w, 0 until h) }
                .filterNot { dist.contains(it) }
                .filter { this[it.y][it.x].getHeight() <= this[p.y][p.x].getHeight() + 1 }) {
                dist[n] = d + 1
                queue.addLast(n)
            }
        }

        return dist[end]
    }

    override fun partOne(input: String, debug: Boolean) {
        val map = input.lines()
        val start = map.find('S')
        val end = map.find('E')

        if (debug) {
            println("start: $start")
            println("end: $end")
        }

        println(map.bfs(start, end))
    }

    override fun partTwo(input: String, debug: Boolean) {
        val map = input.lines()
        val start = map.find('S')
        val end = map.find('E')

        val possibleStartPositions = map.flatMapIndexed { y, l ->
            l.asSequence().mapIndexed { x, c ->
                object {
                    val p = Position(x, y);
                    val c = c
                }
            }.filter { it.c.getHeight() == 1 }
        }.map { it.p }.toList()

        println(possibleStartPositions.map { map.bfs(it, end) }
            .filterNot { it == null }.minOf { it!! })
    }
}