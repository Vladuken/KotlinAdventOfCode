package year2022.`16`

import java.util.PriorityQueue
import readInput

/**
 * With help of solution from
 * https://github.com/LiquidFun/adventofcode
 *
 * (This task was really hard and I've couldn't manage to solve it by myself)
 */
data class State(
    var remainingTime: RemainingTime,
    var currentValve: Valve,
    var elephantRemainingTime: RemainingTime? = null,
    var elephantValve: Valve? = null,
    var openedValves: Set<Valve> = setOf(),
    var sumOfPressure: Pressure = Pressure(0),
) : Comparable<State> {
    override fun compareTo(other: State) = compareValuesBy(this, other) { -it.sumOfPressure.amount }
}

@JvmInline
value class Valve(val title: String)

@JvmInline
value class RemainingTime(val time: Int) {
    operator fun plus(second: RemainingTime): RemainingTime {
        return RemainingTime(time + second.time)
    }

    operator fun plus(second: Int): RemainingTime {
        return RemainingTime(time + second)
    }

    operator fun minus(second: RemainingTime): RemainingTime {
        return RemainingTime(time - second.time)
    }

    operator fun minus(second: Int): RemainingTime {
        return RemainingTime(time - second)
    }
}

@JvmInline
value class Pressure(val amount: Int) {
    operator fun plus(second: Pressure): Pressure {
        return Pressure(amount + second.amount)
    }

    operator fun times(second: Int): Pressure {
        return Pressure(amount * second)
    }
}

/**
 * @param inputNeighbours - initial map of Valve to its Neighbours
 * @param inputFlows - initial map of Valve to it's pressure
 * @param currentValve - [Valve] to calculate result to
 * @param currentRemainingTime - [RemainingTime] on this current step
 * @param visitedValves - set of already visited valves
 */
private fun createMapOfValvesToDistanceInTime(
    inputNeighbours: Map<Valve, List<Valve>>,
    inputFlows: Map<Valve, Pressure>,
    currentValve: Valve,
    currentRemainingTime: RemainingTime,
    visitedValves: Set<Valve> = setOf()
): Map<Valve, RemainingTime> {
    val resultValvesDistances = mutableMapOf<Valve, RemainingTime>()

    /**
     * Iterate only on not visited neighbours
     */
    inputNeighbours[currentValve]!!.filter { it !in visitedValves }.forEach { neighborValve ->
        /**
         * If pressure for new valve is not empty - open it and spend 1 minute
         */
        if (inputFlows[neighborValve]!! != Pressure(0)) {
            resultValvesDistances[neighborValve] = currentRemainingTime + 1
        }

        /**
         * Then find all neighbours for this valve
         */
        createMapOfValvesToDistanceInTime(
            inputNeighbours = inputNeighbours,
            inputFlows = inputFlows,
            currentValve = neighborValve,
            currentRemainingTime = currentRemainingTime + 1,
            visitedValves = visitedValves + setOf(currentValve)
        )
            /**
             * Update result map with new data - where time is minimised
             * (Less time means that to arrive we will select most optimal path)
             */
            .forEach { (valve, distance) ->
                resultValvesDistances[valve] = listOfNotNull(
                    distance, resultValvesDistances.getOrDefault(valve, null)
                ).minBy { it.time }
            }
    }

    return resultValvesDistances
}

/**
 * @param inputValveToDistancesMap is map of [Valve] for all distances to another valves
 * @param inputFlowsMap is initial data of valves [Pressure]
 */
private fun solve(
    inputValveToDistancesMap: Map<Valve, Map<Valve, RemainingTime>>,
    inputFlowsMap: Map<Valve, Pressure>,
    initialState: State
): Int {
    val queue = PriorityQueue<State>().also { it.add(initialState) }

    var resultBestPressureSummary = 0

    val visitedListsOfValves: MutableMap<List<Valve>, Pressure> = mutableMapOf()

    /**
     * Iterate over each possible [State]
     */
    while (queue.isNotEmpty()) {
        var (time, currentValve, elephantTime, elephantValve, openedSet, flowValue) = queue.remove()
        /**
         * Update best pressure data with largest pressure
         */
        resultBestPressureSummary = maxOf(resultBestPressureSummary, flowValue.amount)
        /**
         *  Get sorted list of opened valves, currentValve, elephantValve
         */
        val visitedList: List<Valve> =
            (openedSet.toList() + listOfNotNull(currentValve, elephantValve)).sortedBy { it.title }

        /**
         * Optimisation - if it happens that current visited list is already in this map
         * AND
         * its cached pressure summary is more then current - just skip (it will not give us better result)
         */
        if (visitedListsOfValves.getOrDefault(
                visitedList, Pressure(-1)
            ).amount >= flowValue.amount
        ) continue

        /**
         * Cache pressure for current visited list
         */
        visitedListsOfValves[visitedList] = flowValue


        /**
         * Check needed for part 2
         * If elephantTime and valve is non null (it means we solve part 2) AND current remaining time less then when elephant help
         * That means that we need to switch elephant and ourself (to try the same state but inversed)
         */
        if (elephantTime != null && elephantValve != null && time.time < elephantTime.time) {
            time = elephantTime.also { elephantTime = time }
            currentValve = elephantValve.also { elephantValve = currentValve }
        }

        /**
         * Iterate over all neighbours to distances
         */
        inputValveToDistancesMap[currentValve]!!.forEach { (neighbor, dist) ->
            /**
             * Calculate newTime that is current State TIME - current distance to valve - 1
             * 1 means that it takes 1 minute to open valve
             */
            val newTime = time - dist - 1

            /**
             * Calculate sum pressure for current step (state)
             * We calculate for whole state, not on each step
             */
            val newFlow = flowValue + inputFlowsMap[neighbor]!! * newTime.time

            /**
             * Optimisation
             * If new time is not negative: <0 means VOLCANO ERUPTION
             * And current neighbour is still not opened (if it is opened, we throw away this result as useless/visited)
             */
            if (newTime.time >= 0 && neighbor !in openedSet) {
                /**
                 * Simply create new State to iterate over in parent cycle
                 */
                queue.add(
                    State(
                        remainingTime = newTime,
                        currentValve = neighbor,
                        elephantRemainingTime = elephantTime,
                        elephantValve = elephantValve,
                        /**
                         * This is because we opened this valve
                         */
                        openedValves = openedSet + setOf(neighbor),
                        sumOfPressure = newFlow
                    )
                )
            }
        }
    }
    return resultBestPressureSummary
}

fun main() {
    /**
     * Parse Input
     */
    val input: List<List<String>> =
        readInput("Day16").map { Regex("([A-Z]{2}|\\d+)").findAll(it).toList().map { it.value } }

    /**
     * Get map all neighbours info
     */
    val initialNeighbours: Map<Valve, List<Valve>> =
        input.associate { Valve(it[0]) to it.slice(2 until it.size).map { Valve(it) } }

    /**
     * Get map of all Pressure info
     */
    val flows: Map<Valve, Pressure> = input.associate { Valve(it[0]) to Pressure(it[1].toInt()) }

    /**
     * Map each valve to all Distance info for other valves
     */
    val nonZeroNeighbors: Map<Valve, Map<Valve, RemainingTime>> = input.associate {
        val currentValve = Valve(it[0])
        currentValve to createMapOfValvesToDistanceInTime(
            inputNeighbours = initialNeighbours,
            inputFlows = flows,
            currentValve = currentValve,
            currentRemainingTime = RemainingTime(0)
        )
    }

    /**
     * Solve First Part of Problem
     */
    solve(
        inputValveToDistancesMap = nonZeroNeighbors, inputFlowsMap = flows, initialState = State(
            RemainingTime(30), Valve("AA")
        )
    ).run(::println)

    /**
     * Solve second part of problem
     */
    solve(
        inputValveToDistancesMap = nonZeroNeighbors, inputFlowsMap = flows, initialState = State(
            remainingTime = RemainingTime(26),
            currentValve = Valve("AA"),
            elephantRemainingTime = RemainingTime(26),
            elephantValve = Valve("AA")
        )
    ).run(::println)
}
