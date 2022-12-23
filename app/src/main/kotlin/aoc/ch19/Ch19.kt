package aoc.ch19

import aoc.Challenge
import kotlin.math.max

class Ch19 : Challenge {

    data class OreRobot(val oreCost: Int)
    data class ClayRobot(val oreCost: Int)
    data class ObsidianRobot(val oreCost: Int, val clayCost: Int)
    data class GeodeRobot(val oreCost: Int, val obsidianCost: Int)

    data class Blueprint(
        val id: Int,
        val oreRobot: OreRobot,
        val clayRobot: ClayRobot,
        val obsidianRobot: ObsidianRobot,
        val geodeRobot: GeodeRobot
    ) {
        companion object {
            fun fromLine(l: String): Blueprint {
                val groupValues =
                    Regex("Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.")
                        .find(l)!!
                        .groupValues
                val id = groupValues[1].toInt()
                val oreRobotCost = groupValues[2].toInt()
                val clayRobotCost = groupValues[3].toInt()
                val obsidianRobotOreCost = groupValues[4].toInt()
                val obsidianRobotClayCost = groupValues[5].toInt()
                val geodeRobotOreCost = groupValues[6].toInt()
                val geodeRobotObsidianCost = groupValues[7].toInt()
                return Blueprint(
                    id,
                    OreRobot(oreRobotCost),
                    ClayRobot(clayRobotCost),
                    ObsidianRobot(obsidianRobotOreCost, obsidianRobotClayCost),
                    GeodeRobot(geodeRobotOreCost, geodeRobotObsidianCost)
                )
            }
        }
    }

    data class State(
        val oreRobotCount: Int,
        val oreCount: Int,
        val clayRobotCount: Int,
        val clayCount: Int,
        val obsidianRobotCount: Int,
        val obsidianCount: Int,
        val geodeRobotCount: Int,
        val geodeCount: Int
    )

    private fun maxGeodes(blueprint: Blueprint, epochs: Int): Int {
        fun maxGeodesAux(epochs: Int, s: State): Int {
            if (epochs <= 0) {
                return s.geodeCount
            }
            val newOreCount = s.oreCount + s.oreRobotCount
            val newClayCount = s.clayCount + s.clayRobotCount
            val newObsidianCount = s.obsidianCount + s.obsidianRobotCount
            val newGeodeCount = s.geodeCount + s.geodeRobotCount
            val newEpochs = epochs - 1

            var maxGeodes = Int.MIN_VALUE
            if (s.oreCount >= blueprint.geodeRobot.oreCost && s.obsidianCount >= blueprint.geodeRobot.obsidianCost) {
                maxGeodes = max(
                    maxGeodes, maxGeodesAux(
                        newEpochs,
                        State(
                            s.oreRobotCount,
                            newOreCount - blueprint.geodeRobot.oreCost,
                            s.clayRobotCount,
                            newClayCount,
                            s.obsidianRobotCount,
                            newObsidianCount - blueprint.geodeRobot.obsidianCost,
                            s.geodeRobotCount + 1,
                            newGeodeCount
                        )
                    )
                )
            }
            if (s.oreCount >= blueprint.obsidianRobot.oreCost && s.clayCount >= blueprint.obsidianRobot.clayCost) {
                maxGeodes = max(
                    maxGeodes, maxGeodesAux(
                        newEpochs,
                        State(
                            s.oreRobotCount,
                            newOreCount - blueprint.obsidianRobot.oreCost,
                            s.clayRobotCount,
                            newClayCount - blueprint.obsidianRobot.clayCost,
                            s.obsidianRobotCount + 1,
                            newObsidianCount,
                            s.geodeRobotCount,
                            newGeodeCount
                        )
                    )
                )
            }
            if (s.oreCount >= blueprint.clayRobot.oreCost && s.clayRobotCount < 11) {
                maxGeodes = max(
                    maxGeodes, maxGeodesAux(
                        newEpochs,
                        State(
                            s.oreRobotCount,
                            newOreCount - blueprint.clayRobot.oreCost,
                            s.clayRobotCount + 1,
                            newClayCount,
                            s.obsidianRobotCount,
                            newObsidianCount,
                            s.geodeRobotCount,
                            newGeodeCount
                        )
                    )
                )
            }
            if (s.oreCount >= blueprint.oreRobot.oreCost && s.oreRobotCount < 3) {
                maxGeodes = max(
                    maxGeodes, maxGeodesAux(
                        newEpochs,
                        State(
                            s.oreRobotCount + 1,
                            newOreCount - blueprint.oreRobot.oreCost,
                            s.clayRobotCount,
                            newClayCount,
                            s.obsidianRobotCount,
                            newObsidianCount,
                            s.geodeRobotCount,
                            newGeodeCount
                        )
                    )
                )
            }
            if (maxGeodes == Int.MIN_VALUE || s.oreCount <= blueprint.geodeRobot.oreCost || s.oreCount <= blueprint.obsidianRobot.oreCost) {
                maxGeodes = max(
                    maxGeodes, maxGeodesAux(
                        newEpochs, State(
                            s.oreRobotCount,
                            newOreCount,
                            s.clayRobotCount,
                            newClayCount,
                            s.obsidianRobotCount,
                            newObsidianCount,
                            s.geodeRobotCount,
                            newGeodeCount
                        )
                    )
                )
            }
            return maxGeodes
        }

        return maxGeodesAux(epochs, State(1, 0, 0, 0, 0, 0, 0, 0))
    }

    override fun partOne(input: String, debug: Boolean, isTestRun: Boolean) {
        val blueprints = input.lineSequence().map(Blueprint::fromLine).toList()

        if (debug) {
            println(blueprints)
        }

        println(blueprints.sumOf {
            it.id * maxGeodes(it, 24)
        })
    }

    override fun partTwo(input: String, debug: Boolean, isTestRun: Boolean) {
        val blueprints =
            input.lineSequence().map(Blueprint::fromLine).take(3).toList()

        println(blueprints.map { maxGeodes(it, 32) }.reduce { a, b -> a * b })
    }
}