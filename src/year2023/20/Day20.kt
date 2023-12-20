package year2023.`20`

import readInput
import utils.lcm
import utils.printlnDebug

private const val CURRENT_DAY = "20"


private sealed class TaskModule {

    abstract val key: String
    abstract val nextKeys: List<String>

    data class Broadcaster(
        override val nextKeys: List<String>,
    ) : TaskModule() {
        override val key: String = "broadcaster"
    }

    data class FlipFlop(
        override val key: String,
        override val nextKeys: List<String>,
        private var cache: PulseType = PulseType.LOW,
    ) : TaskModule() {
        fun nextPulse(): PulseType {
            val currentlyCachedPulse = cache.inverse()
            cache = currentlyCachedPulse
            return currentlyCachedPulse
        }
    }

    data class Conjunction(
        override val key: String,
        override val nextKeys: List<String>,
        private var cache: Map<String, PulseType> = emptyMap(),
    ) : TaskModule() {
        fun nextPulse(sourceKey: String, newPulse: PulseType): PulseType {
            val newCache = cache.mapValues { (inputKey, currentPulse) ->
                if (inputKey == sourceKey) {
                    newPulse
                } else {
                    currentPulse
                }
            }
            val areAllItemsHigh = newCache
                .all { (_, currentPulse) -> currentPulse == PulseType.HIGH }
            cache = newCache
            return if (areAllItemsHigh) {
                PulseType.LOW
            } else {
                PulseType.HIGH
            }
        }
    }
}

private fun parseLineInto(
    line: String
): TaskModule {
    val (left, right) = line
        .split("->")
        .map { it.trim() }

    val keys = right.split(",")
        .map { it.trim() }

    return when {
        left == "broadcaster" -> TaskModule.Broadcaster(keys)
        left.startsWith("%") -> TaskModule.FlipFlop(left.drop(1), keys)
        left.startsWith("&") -> TaskModule.Conjunction(left.drop(1), keys)
        else -> error(left)
    }
}

enum class PulseType {
    LOW,
    HIGH;

    fun inverse(): PulseType {
        return when (this) {
            LOW -> HIGH
            HIGH -> LOW
        }
    }

    override fun toString(): String {
        return when (this) {
            LOW -> "-low"
            HIGH -> "-high"
        }
    }
}

private fun pushTheButton(
    modulesMap: Map<String, TaskModule>,
    broadcast: TaskModule.Broadcaster,
    onRxSend: (String) -> Unit = {},
    onIncLow: () -> Unit = {},
    onIncHigh: () -> Unit = {},
): Map<String, TaskModule> {

    val queue = ArrayDeque<Triple<String, TaskModule, PulseType>>(
        listOf(Triple("button", broadcast, PulseType.LOW))
    )
    while (queue.isNotEmpty()) {
        val item = queue.removeFirst()
        val (sourceKey, destinationModuleItem, newPulse) = item
        if (destinationModuleItem is TaskModule.FlipFlop && newPulse == PulseType.HIGH) continue

        callIfNeeded(
            sourceKey = sourceKey,
            onCall = onRxSend,
            arrivedPulse = newPulse,
        )

        val pulseToSend = when (destinationModuleItem) {
            is TaskModule.Broadcaster -> newPulse
            is TaskModule.FlipFlop -> destinationModuleItem.nextPulse()
            is TaskModule.Conjunction -> destinationModuleItem.nextPulse(sourceKey, newPulse)
        }

        destinationModuleItem.nextKeys.forEach {
            val cachedModule = modulesMap[it]
            if (cachedModule != null) {
                val triple = Triple(destinationModuleItem.key, cachedModule, pulseToSend)
                when (pulseToSend) {
                    PulseType.HIGH -> onIncHigh()
                    PulseType.LOW -> onIncLow()
                }
                queue.addLast(triple)
            } else {
                when (pulseToSend) {
                    PulseType.HIGH -> onIncHigh()
                    PulseType.LOW -> onIncLow()
                }
            }
        }
    }

    return modulesMap
}


private fun prepareCache(mapOfPaths: Map<String, TaskModule>): Map<String, TaskModule> {
    return mapOfPaths.mapValues { (key, value) ->
        when (value) {
            is TaskModule.Broadcaster -> value
            is TaskModule.FlipFlop -> value.copy(cache = PulseType.LOW)
            is TaskModule.Conjunction -> {
                val cache = mapOfPaths.values.filter { it.nextKeys.contains(key) }
                    .associate { it.key to PulseType.LOW }
                value.copy(cache = cache)
            }
        }
    }
}

private fun callIfNeeded(
    sourceKey: String,
    onCall: (String) -> Unit,
    arrivedPulse: PulseType,
) {
    val validKeys = setOf("dh", "mk", "vf", "rn")
    if (sourceKey in validKeys && arrivedPulse == PulseType.HIGH) {
        onCall(sourceKey)
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        val modules = input.map {
            parseLineInto(it)
        }
        printlnDebug { modules }

        var lowCount = 0
        var highCount = 0

        val mapOfPaths = modules.associateBy { it.key }

        var cachePulses = prepareCache(mapOfPaths)
        repeat(1000) {
            lowCount++
            cachePulses = pushTheButton(
                modulesMap = cachePulses,
                broadcast = cachePulses["broadcaster"] as TaskModule.Broadcaster,
                onIncHigh = { highCount++ },
                onIncLow = { lowCount++ }
            )
        }
        printlnDebug { "Low Count :$lowCount" }
        printlnDebug { "High Count :$highCount" }
        return lowCount * highCount
    }

    fun part2(input: List<String>): Long {
        val modules = input.map {
            parseLineInto(it)
        }
        val mapOfPaths = modules.associateBy { it.key }

        var cachePulses = prepareCache(mapOfPaths)
        var count = 0
        val map = mutableMapOf<String, Int>()
        while (true) {
            count++
            cachePulses = pushTheButton(
                modulesMap = cachePulses,
                broadcast = cachePulses["broadcaster"] as TaskModule.Broadcaster,
                onRxSend = {
                    if (map.containsKey(it).not()) {
                        map[it] = count
                    }
                }
            )

            if (map.size == 4) {
                printlnDebug { map }
                return map.values.lcm()
            }
        }
    }

    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 32000000)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 861743850)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 247023644760071)
}
