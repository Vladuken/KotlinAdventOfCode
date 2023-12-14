package year2023.`14`

import readInput
import utils.printDebug
import utils.printlnDebug

private const val CURRENT_DAY = "14"


private data class Point(
    val x: Int,
    val y: Int,
) {
    fun moveNorth(): Point = copy(y = y - 1)
    fun moveEast(): Point = copy(x = x + 1)
    fun moveSouth(): Point = copy(y = y + 1)
    fun moveWest(): Point = copy(x = x - 1)
}

private fun parseMap(input: List<String>): Map<Point, String> {
    val mutableMap = mutableMapOf<Point, String>()
    input.forEachIndexed { y, line ->
        line.split("")
            .filter { it.isNotBlank() }
            .forEachIndexed { x, value ->
                when (value) {
                    "O" -> mutableMap[Point(x, y)] = value
                    "#" -> mutableMap[Point(x, y)] = value
                    "." -> mutableMap[Point(x, y)] = value
                    else -> error("Illegal value x:$x y:$y value:$value")
                }
            }
    }
    return mutableMap
}

// region Tilts
private val invalidList = listOf("O", "#", null)

private val tiltNorthCache = mutableMapOf<Map<Point, String>, Map<Point, String>>()
private fun tiltNorth(
    maxX: Int,
    maxY: Int,
    map: Map<Point, String>
): Map<Point, String> {
    val cachedValue = tiltNorthCache[map]
    if (cachedValue != null) return cachedValue

    val resMap = map.toMutableMap()

    for (x in 0..maxX) {
        for (y in 0..maxY) {
            val currentPoint = Point(x, y)
            val currentValue = resMap[Point(x, y)]
            if (currentValue == "O") {
                var buffPoint = currentPoint
                while (buffPoint.y >= 0 && resMap[buffPoint.moveNorth()] !in invalidList) {
                    val prevPoint = buffPoint
                    val newPoint = prevPoint.moveNorth()

                    buffPoint = newPoint
                    resMap[prevPoint] = "."
                    resMap[newPoint] = "O"
                }
            }
        }
    }
    tiltNorthCache[map] = resMap.toMap()
    return resMap
}


private val tiltEastCache = mutableMapOf<Map<Point, String>, Map<Point, String>>()
private fun tiltEast(
    maxX: Int,
    maxY: Int,
    map: Map<Point, String>,
): Map<Point, String> {
    val cachedValue = tiltEastCache[map]
    if (cachedValue != null) return cachedValue

    val resMap = map.toMutableMap()

    for (x in maxX downTo 0) {
        for (y in maxY downTo 0) {
            val currentPoint = Point(x, y)
            val currentValue = resMap[Point(x, y)]
            if (currentValue == "O") {
                var buffPoint = currentPoint
                while (buffPoint.x <= maxX && resMap[buffPoint.moveEast()] !in invalidList) {
                    val prevPoint = buffPoint
                    val newPoint = prevPoint.moveEast()

                    buffPoint = newPoint
                    resMap[prevPoint] = "."
                    resMap[newPoint] = "O"
                }
            }
        }
    }

    tiltEastCache[map] = resMap.toMap()
    return resMap
}


private val tiltSouthCache = mutableMapOf<Map<Point, String>, Map<Point, String>>()
private fun tiltSouth(
    maxX: Int,
    maxY: Int,
    map: Map<Point, String>
): Map<Point, String> {
    val cachedValue = tiltSouthCache[map]
    if (cachedValue != null) return cachedValue
    val resMap = map.toMutableMap()

    for (x in maxX downTo 0) {
        for (y in maxY downTo 0) {
            val currentPoint = Point(x, y)
            val currentValue = resMap[Point(x, y)]
            if (currentValue == "O") {
                var buffPoint = currentPoint
                while (buffPoint.y <= maxY && resMap[buffPoint.moveSouth()] !in invalidList) {
                    val prevPoint = buffPoint
                    val newPoint = prevPoint.moveSouth()

                    buffPoint = newPoint
                    resMap[prevPoint] = "."
                    resMap[newPoint] = "O"
                }
            }
        }
    }
    tiltSouthCache[map] = resMap.toMap()
    return resMap
}

private val tiltWestCache = mutableMapOf<Map<Point, String>, Map<Point, String>>()
private fun tiltWest(
    maxX: Int,
    maxY: Int,
    map: Map<Point, String>
): Map<Point, String> {
    val cachedValue = tiltWestCache[map]
    if (cachedValue != null) return cachedValue

    val resMap = map.toMutableMap()

    for (x in 0..maxX) {
        for (y in maxY downTo 0) {
            val currentPoint = Point(x, y)
            val currentValue = resMap[Point(x, y)]
            if (currentValue == "O") {
                var buffPoint = currentPoint
                while (buffPoint.x >= 0 && resMap[buffPoint.moveWest()] !in invalidList) {
                    val prevPoint = buffPoint
                    val newPoint = prevPoint.moveWest()

                    buffPoint = newPoint
                    resMap[prevPoint] = "."
                    resMap[newPoint] = "O"
                }
            }
        }
    }
    tiltWestCache[map] = resMap.toMap()
    return resMap
}
// endregion

private fun calculateAnswer(map: Map<Point, String>): Int {
    val maxY = map.maxOf { it.key.y } + 1

    var sum = 0
    map.keys.forEach {
        val currentValue = map[it] ?: error("AAAAAA")
        if (currentValue == "O") {
            sum += maxY - it.y
        }
    }

    return sum
}

private fun printMap(
    initialMap: Map<Point, String>
) {
    val maxX = initialMap.maxOf { it.key.x }
    val maxY = initialMap.maxOf { it.key.y }

    for (y in 0..maxY) {
        for (x in 0..maxX) {
            val currentPoint = Point(x, y)
            printDebug { "" + initialMap[currentPoint] }
        }
        printlnDebug { "" }
    }
}

private fun performCycle(initialMap: Map<Point, String>): Map<Point, String> {
    val maxX = initialMap.maxOf { it.key.x }
    val maxY = initialMap.maxOf { it.key.y }

    val northMap = tiltNorth(maxX, maxY, initialMap)
    val westMap = tiltWest(maxX, maxY, northMap)
    val southMap = tiltSouth(maxX, maxY, westMap)
    return tiltEast(maxX, maxY, southMap)
}

fun main() {

    fun part1(input: List<String>): Int {
        val inputMap = parseMap(input)
        val maxX = inputMap.maxOf { it.key.x }
        val maxY = inputMap.maxOf { it.key.y }
        val resMap = tiltNorth(maxX, maxY, inputMap)
        return calculateAnswer(resMap)
    }

    fun part2(input: List<String>): Int {
        val inputMap = parseMap(input)
        printlnDebug { "Initial Map" }
        printMap(inputMap)

        var currentMap = inputMap
        val listOf = mutableListOf<Map<Point, String>>()

        var wasCycleFound = false
        var currentCount = 1000000000
        while (currentCount > 0) {
            currentMap = performCycle(currentMap)
            if (wasCycleFound.not()) {
                if (listOf.contains(currentMap)) {
                    val listSize = listOf.size
                    val cycleSize = listSize - listOf.indexOf(currentMap)
                    printlnDebug { "CYCLE FOUND $cycleSize" }
                    currentCount %= cycleSize
                    wasCycleFound = true
                } else {
                    listOf.add(currentMap)
                }
            }
            currentCount--
        }

        return calculateAnswer(currentMap)
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 136)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 64)

    val input = readInput("Day$CURRENT_DAY")

// Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 113525)

// Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 101292)
}
