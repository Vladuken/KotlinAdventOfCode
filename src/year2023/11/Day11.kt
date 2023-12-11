package year2023.`11`

import readInput
import utils.printlnDebug
import kotlin.math.abs

private const val CURRENT_DAY = "11"


private data class Point(
    val x: Long,
    val y: Long,
)

private fun parseMap(input: List<String>): Set<Point> {
    val mutableMap = mutableSetOf<Point>()
    input.forEachIndexed { y, line ->
        line.split("")
            .filter { it.isNotBlank() }
            .forEachIndexed { x, value ->
                if (value == "#") {
                    mutableMap.add(Point(x.toLong(), y.toLong()))
                }
            }
    }
    return mutableMap
}

private fun expandTheUniverse(
    input: Set<Point>,
    multiplier: Long = 1,
): Set<Point> {
    val maxX = input.maxOf { it.x }
    val maxY = input.maxOf { it.y }

    var newOutput = input
    var countAccumulator = 0
    for (x in 0 until maxX) {
        if (input.all { it.x != x }) {
            countAccumulator++
            printlnDebug { "EXPAND X=$x countAccumulator=$countAccumulator" }
            newOutput = newOutput.map {
                if (it.x >= x + (countAccumulator - 1) * multiplier) {
                    printlnDebug { "Current Point For x $it" }
                    it.copy(x = it.x + multiplier)
                } else {
                    it
                }
            }.toSet()
        }
    }

    var yCountAccumulator = 0
    for (y in 0 until maxY) {
        if (input.all { it.y != y }) {
            yCountAccumulator++
            printlnDebug { "EXPAND Y=$y countAccumulator=$yCountAccumulator" }
            newOutput = newOutput.map {
                if (it.y >= y + (yCountAccumulator - 1) * multiplier) {
                    it.copy(y = it.y + multiplier)
                } else {
                    it
                }
            }.toSet()
        }
    }

    return newOutput
}

private fun Point.calculateDistanceTo(point: Point): Long {
    val x = x - point.x
    val y = y - point.y
    return abs(x) + abs(y)
}

private fun countAllUniquePairs(input: Set<Point>): Set<Pair<Point, Point>> {
    val resSet = mutableSetOf<Pair<Point, Point>>()
    input.forEach { currentInitPoint ->
        input.forEach { destInitPoint ->
            val pair = currentInitPoint to destInitPoint
            val pairInverse = destInitPoint to currentInitPoint
            val hasInside = resSet.contains(pair) || resSet.contains(pairInverse)
            if (currentInitPoint != destInitPoint && (hasInside.not())) {
                resSet.add(currentInitPoint to destInitPoint)
            }
        }
    }
    return resSet
}

fun main() {

    fun solution(input: List<String>, multiplier: Long): Long {
        printlnDebug { "PART 2" }
        val setOfGalaxies = parseMap(input)
        val output = expandTheUniverse(setOfGalaxies, multiplier)
        val pairsSet = countAllUniquePairs(output)
        val distances = pairsSet.sumOf { it.first.calculateDistanceTo(it.second) }
        printlnDebug { "Pairs: ${pairsSet.size}" }
        return distances
    }


    fun part1(input: List<String>): Long {
        return solution(input, 1)
    }


    fun part2(input: List<String>, multiplier: Long): Long {
        return solution(input, multiplier)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")
    val part1Test = part1(testInput)

    println(part1Test)
    check(part1Test == 374L)

    val part2Test = part2(testInput, 9)
    println(part2Test)
    check(part2Test == 1030L)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 9608724L)

    // Part 2
    val part2 = part2(input, 999_999)
    println(part2)
    check(part2 == 904633799472L)
}
