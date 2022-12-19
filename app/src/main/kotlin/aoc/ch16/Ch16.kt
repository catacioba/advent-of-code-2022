package aoc.ch16

import aoc.Challenge
import com.google.common.math.IntMath.pow
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.collections.set
import kotlin.math.max

class Ch16 : Challenge {

    class Valve(val label: String, val flowRate: Int, val next: List<String>) {
        companion object {
            fun fromLine(l: String): Valve {
                val parts = l.split("; ")
                val label = parts[0].slice(6..7)
                val flowRate = parts[0].split("=")[1].toInt()
                val next = if (parts[1].startsWith("tunnels lead to valves"))
                    parts[1].removePrefix("tunnels lead to valves ")
                        .split(", ") else
                    listOf(parts[1].removePrefix("tunnel leads to valve "))

                return Valve(label, flowRate, next)
            }
        }

        override fun toString(): String {
            val nextString =
                next.joinToString(separator = ", ", prefix = "[", postfix = "]")
            return "[$label $flowRate => $nextString"

        }
    }

    class Graph(private val valves: Map<String, Valve>) {
        private val dist =
            valves.keys.asSequence().map { it to bfs(it) }.toMap()

        val valvesWithFlow = valves.filter { it.value.flowRate > 0 }

        private fun bfs(start: String): Map<String, Int> {
            val queue = ArrayDeque<String>()
            val dist = mutableMapOf(start to 0)
            queue.add(start)

            while (queue.isNotEmpty()) {
                val el = queue.removeFirst()

                val d = dist[el]!!
                val valve = valves[el]!!

                for (next in valve.next) {
                    if (!dist.contains(next)) {
                        dist[next] = d + 1
                        queue.add(next)
                    }
                }
            }
            return dist
        }

        data class State(
            val prev: String,
            val current: String,
            val remainingEpochs: Int,
            val maxFlow: Int,
            val closedValves: List<String>
        )

        // Max flow if going from `from` and opening `to` until the end.
        private fun flow(to: String, remainingEpochs: Int): Int {
            return remainingEpochs * valves[to]!!.flowRate
        }

        fun dijkstra(epochs: Int, valvesToVisit: List<String>): Int {
            val pq = PriorityQueue(compareByDescending(State::maxFlow))

            pq.addAll(valvesToVisit.map {
                val remainingEpochs = epochs - dist["AA"]!![it]!! - 1
                State(
                    "AA",
                    it,
                    remainingEpochs,
                    flow(it, remainingEpochs),
                    valvesToVisit.filter { s -> s != it }
                )
            })

            var best = Int.MIN_VALUE

            while (pq.isNotEmpty()) {
                val s = pq.remove()

                best = max(best, s.maxFlow)

                for (n in s.closedValves) {
                    val remainingEpochs =
                        s.remainingEpochs - dist[s.current]!![n]!! - 1

                    if (remainingEpochs > 0) {
                        val newFlow = s.maxFlow + flow(n, remainingEpochs)

                        pq.add(
                            State(
                                s.current,
                                n,
                                remainingEpochs,
                                newFlow,
                                s.closedValves.asSequence()
                                    .filter { it != n }
                                    .toList()
                            )
                        )
                    }
                }
            }

            return best
        }

        data class GraphState(
            val prev: String?,
            val current: String,
            val remainingEpochs: Int
        )

        data class State2(
            val me: GraphState,
            val elephant: GraphState,
            val maxFlow: Int,
            val closedValves: List<String>,
        ) {
            fun symmetricState(): State2 {
                return State2(elephant, me, maxFlow, closedValves)
            }
        }

        fun elephantDijkstra(epochs: Int): Int {
            val pq = PriorityQueue(compareByDescending(State2::maxFlow))

            pq.addAll(valvesWithFlow.values.flatMap {
                val remainingEpochs = epochs - dist["AA"]!![it.label]!! - 1
                sequenceOf(
                    State2(
                        GraphState("AA", it.label, remainingEpochs),
                        GraphState(null, "AA", epochs),
                        flow(it.label, remainingEpochs),
                        valvesWithFlow.keys.asSequence()
                            .filter { s -> s != it.label }
                            .toList(),
                    ),
                    State2(
                        GraphState(null, "AA", epochs),
                        GraphState("AA", it.label, remainingEpochs),
                        flow(it.label, remainingEpochs),
                        valvesWithFlow.keys.asSequence()
                            .filter { s -> s != it.label }
                            .toList()
                    )
                )
            })

            var best = Int.MIN_VALUE
            val seenStates = mutableSetOf<State2>()

            var cnt = 0
            while (pq.isNotEmpty()) {
                cnt += 1
                val s = pq.remove()

                best = max(best, s.maxFlow)

                if (cnt % 3000000 == 0) {
                    return best
                }

                for (next in s.closedValves) {
                    val newClosedValves =
                        s.closedValves.filterNot { it == next }

                    val remainingEpochs =
                        s.me.remainingEpochs - dist[s.me.current]!![next]!! - 1
                    val s1 = State2(
                        GraphState(s.me.current, next, remainingEpochs),
                        s.elephant,
                        s.maxFlow + flow(next, remainingEpochs),
                        newClosedValves
                    )
                    if (remainingEpochs > 0 && !seenStates.contains(s1)) {
                        pq.add(s1)
                        seenStates.add(s1)
                        seenStates.add(s1.symmetricState())
                    }

                    val remainingElephant =
                        s.elephant.remainingEpochs - dist[s.elephant.current]!![next]!! - 1
                    val s2 = State2(
                        s.me,
                        GraphState(
                            s.elephant.current,
                            next,
                            remainingElephant
                        ),
                        s.maxFlow + flow(next, remainingElephant),
                        newClosedValves
                    )
                    if (remainingElephant > 0 && !seenStates.contains(s2)) {
                        pq.add(s2)
                        seenStates.add(s2)
                        seenStates.add(s2.symmetricState())
                    }
                }
            }

            return best
        }
    }

    override fun partOne(input: String, debug: Boolean, isTestRun: Boolean) {
        val valves =
            input.lineSequence().map(Valve::fromLine).associateBy { it.label }

        val graph = Graph(valves)

        val valvesWithFlow = graph.valvesWithFlow.keys.toList()

        println(graph.dijkstra(30, valvesWithFlow))
    }

    private fun <T> subsets(collection: List<T>): Sequence<List<T>> = sequence {
        val end = pow(2, collection.size)

        for (idx in 1 until end) {
            val s = mutableListOf<T>()

            for (k in collection.indices) {
                if (idx.shl(k).and(idx) != 0) {
                    s.add(collection[k])
                }
            }
            yield(s)
        }
    }

    override fun partTwo(input: String, debug: Boolean, isTestRun: Boolean) {
        val valves =
            input.lineSequence().map(Valve::fromLine).associateBy { it.label }

        val graph = Graph(valves)

//        val valvesWithFlow = graph.valvesWithFlow
//
//        var best = Int.MIN_VALUE
//        for (subset in subsets(valvesWithFlow.keys.toList())) {
//            val rest =
//                valvesWithFlow.keys.asSequence()
//                    .filterNot { subset.contains(it) }
//                    .toList()
//
//            best =
//                max(best, graph.dijkstra(26, subset) + graph.dijkstra(26, rest))
//        }
//
//        println(best)
        println(graph.elephantDijkstra(26))
    }
}