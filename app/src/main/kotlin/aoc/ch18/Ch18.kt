package aoc.ch18

import aoc.Challenge

class Ch18 : Challenge {
    data class Point3(val x: Int, val y: Int, val z: Int) {
        fun neighbors(): Sequence<Point3> {
            return sequenceOf(
                Point3(-1, 0, 0),
                Point3(1, 0, 0),
                Point3(0, -1, 0),
                Point3(0, 1, 0),
                Point3(0, 0, -1),
                Point3(0, 0, 1),
            ).map { this + it }
        }

        operator fun plus(ot: Point3): Point3 =
            Point3(x + ot.x, y + ot.y, z + ot.z)

        operator fun times(ot: Int): Point3 =
            Point3(x * ot, y * ot, z * ot)
    }

    private fun parsePoint3(s: String): Point3 {
        val parts = s.split(',')
        return Point3(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
    }

    override fun partOne(input: String, debug: Boolean, isTestRun: Boolean) {
        val points = input.lineSequence().map(this::parsePoint3).toSet()

        println(
            points.asSequence()
                .map { it.neighbors().count { n -> !points.contains(n) } }
                .sum()
        )
    }

    private fun bfs(points: Set<Point3>): Set<Point3> {
        val minX = points.minOf { it.x }
        val maxX = points.maxOf { it.x }
        val minY = points.minOf { it.y }
        val maxY = points.maxOf { it.y }
        val minZ = points.minOf { it.z }
        val maxZ = points.maxOf { it.z }

        val start = Point3(minX - 1, minY - 1, minZ - 1)

        val visited = mutableSetOf<Point3>()
        val queue = ArrayDeque<Point3>();
        queue.add(start)
        visited.add(start)

        while (queue.isNotEmpty()) {
            val p = queue.removeFirst()

            for (n in p.neighbors()) {
                if (n.x < minX - 1 || n.y < minY - 1 || n.z < minZ - 1 || n.x > maxX + 1 || n.y > maxY + 1 || n.z > maxZ + 1) {
                    continue
                }
                if (points.contains(n) || visited.contains(n)) {
                    continue
                }
                queue.add(n)
                visited.add(n)
            }
        }

        return visited
    }

    override fun partTwo(input: String, debug: Boolean, isTestRun: Boolean) {
        val points = input.lineSequence().map(this::parsePoint3).toSet()

        val airCubes = bfs(points)

        println(points.sumOf {
            it.neighbors()
                .count { n -> airCubes.contains(n) }
        })
    }
}