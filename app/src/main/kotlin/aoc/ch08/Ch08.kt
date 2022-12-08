package aoc.ch08

import aoc.Challenge

class Ch08 : Challenge {

    class Grid(private val grid: List<List<Int>>) {
        companion object {
            fun fromInput(input: String): Grid {
                return Grid(input.lineSequence().map { l ->
                    l.asSequence().map { it.digitToInt() }.toList()
                }.toList())
            }
        }

        private val height: Int = grid.count()
        private val width: Int = grid.firstOrNull()?.count() ?: 0

        fun countVisible(): Int = grid.indices.sumOf { x ->
            grid[x].indices.count { y ->
                isVisible(
                    x, y
                )
            }
        }

        private fun isVisible(x: Int, y: Int): Boolean {
            return (0 until x).all { xx -> grid[xx][y] < grid[x][y] } ||
                   (x + 1 until height).all { xx -> grid[xx][y] < grid[x][y] } ||
                   (0 until y).all { yy -> grid[x][yy] < grid[x][y] } ||
                   (y + 1 until width).all { yy -> grid[x][yy] < grid[x][y] }
        }

        fun maxScenicScore(): Int =
            grid.indices.maxOf { x ->
                grid[x].indices.maxOf { y ->
                    scenicScore(x, y)
                }
            }

        private fun scenicScore(x: Int, y: Int): Int {
            return countDirection(x, y, 0, 1) *
                   countDirection(x, y, 1, 0) *
                   countDirection(x, y, 0, -1) *
                   countDirection(x, y, -1, 0)
        }

        private fun countDirection(x: Int, y: Int, dx: Int, dy: Int): Int {
            var xx = x + dx
            var yy = y + dy

            var cnt = 0

            while (isInBounds(xx, yy)) {
                cnt++
                if (grid[xx][yy] >= grid[x][y]) {
                    break
                }
                xx += dx
                yy += dy
            }

            return cnt
        }

        private fun isInBounds(x: Int, y: Int) =
            x in 0 until height && y in 0 until width
    }

    override fun partOne(input: String) {
        val grid = Grid.fromInput(input)

        println(grid.countVisible())
    }

    override fun partTwo(input: String) {
        val grid = Grid.fromInput(input)

        println(grid.maxScenicScore())
    }
}