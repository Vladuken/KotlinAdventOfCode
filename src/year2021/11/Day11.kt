package year2021.`11`

import readInput


//private const val NUMBER = 5
private const val NUMBER = 10

private const val ENABLE_LOGS = false

private data class Point(
    val x: Int,
    val y: Int,
) {
    override fun toString(): String = "[$x,$y]"
}

private fun Point.neighbours(): Set<Point> {
    return setOf(
        Point(x + 1, y),
        Point(x - 1, y),
        Point(x, y + 1),
        Point(x, y - 1),
        Point(x + 1, y + 1),
        Point(x - 1, y - 1),
        Point(x + 1, y - 1),
        Point(x - 1, y + 1),
    )
}

private fun printMap(initialMap: Map<Point, Int>, number: Int, enableLog: Boolean = ENABLE_LOGS) {
    if (enableLog.not()) return
    val sortedPoints = initialMap.keys.toList()
        .sortedWith(compareBy({ it.y }, { it.x }))

    sortedPoints.forEachIndexed { index, point ->
        if (index % number == 0) {
            println()
        }
        print(" " + initialMap[point])

    }
    println()
}

private fun parseMap(input: List<String>): Map<Point, Int> {
    val mutableMap = mutableMapOf<Point, Int>()
    input.forEachIndexed { y, line ->
        line.split("")
            .filter { it.isNotBlank() }
            .forEachIndexed { x, value ->
                mutableMap[Point(x, y)] = value.toInt()
            }
    }
    return mutableMap
}


private fun mergeTwoMatrix(one: Map<Point, Int>, two: Map<Point, Int>): Map<Point, Int> {
    return one.mapValues { (key, value) ->
        value + two.getOrElse(key) { error("Illegal key $key") }
    }
}

private fun step1(initialMap: Map<Point, Int>): Map<Point, Int> {
    val newMap = mutableMapOf<Point, Int>()
    initialMap.keys.forEach { currentPoint ->
        newMap[currentPoint] = initialMap
            .getOrElse(currentPoint) { error("Illegal key $currentPoint") }
            .inc()
    }
    return newMap
}

private fun step2(initialMap: Map<Point, Int>, count: (Int) -> Unit = {}): Map<Point, Int> {
    var currentMap = initialMap.toMutableMap()
    var deltaMap = currentMap

    val setOfFlashedPoints = mutableSetOf<Point>()

    while (currentMap.any { it.value > 9 }) {
        deltaMap = deltaMap
            .mapValues { 0 }
            .toMutableMap()

        currentMap.forEach { (currentPoint, value) ->
            if (value > 9 && currentPoint !in setOfFlashedPoints) {
                // flash and increase neighbours
                currentPoint.neighbours()
                    .filter { it in deltaMap }
                    .forEach { neighbour ->
                        deltaMap.computeIfPresent(neighbour) { _, value -> value.inc() }
                    }
                setOfFlashedPoints.add(currentPoint)
            }
        }

        if (ENABLE_LOGS) {
            println("-----------".repeat(5))

            println("currentMap")
            printMap(currentMap, NUMBER)

            println("Delta")
            printMap(deltaMap, NUMBER)
        }

        currentMap = mergeTwoMatrix(currentMap, deltaMap)
            .mapValues { (point, value) ->
                if (value > 9 && point in setOfFlashedPoints) {
                    0
                } else {
                    value
                }
            }
            .toMutableMap()

        if (ENABLE_LOGS) {
            println("Merge Result")
            printMap(currentMap, NUMBER)
        }

    }

    return currentMap
        .mapValues { (point, value) ->
            if (point in setOfFlashedPoints) {
                0
            } else {
                value
            }
        }
        .toMutableMap()
        .also {
            count(setOfFlashedPoints.size)
            if (ENABLE_LOGS) {
                println("Final Result")
                printMap(it, NUMBER)
                println("-----------".repeat(5))
            }
        }
}

fun main() {

    fun part1(input: List<String>): Int {
        var parsedMap = parseMap(input)

        var counter = 0
        repeat(100) {
            printMap(parsedMap, NUMBER)
            parsedMap = step2(step1(parsedMap)) {
                counter += it
            }
        }

        return counter
    }

    fun part2(input: List<String>): Int {
        var parsedMap = parseMap(input)

        var counter = 0
        while (parsedMap.all { it.value == 0 }.not()) {
            parsedMap = step2(step1(parsedMap))
            counter++
        }

        return counter
    }
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    val part1Test = part1(testInput)

    println(part1Test)
    check(part1Test == 1656)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}
