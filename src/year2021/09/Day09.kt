package year2021.`09`

import readInput


private data class Point(
    val x: Int,
    val y: Int,
) {
    override fun toString(): String {
        return "[$x,$y]"
    }
}

private fun Point.neighbours(): Set<Point> {
    return setOf(
        Point(x = x, y = y + 1),
        Point(x = x, y = y - 1),
        Point(x = x + 1, y = y),
        Point(x = x - 1, y = y),
    )
}

fun main() {

    fun parseMap(input: List<String>): Map<Point, Int> {
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

    fun findAllLowesPoints(input: Map<Point, Int>): Set<Point> {
        return input.keys.filter { currentPoint ->
            val currentValue = input[currentPoint]!!
            currentPoint
                .neighbours()
                .mapNotNull { input[it] }
                .all { neighbourValue ->
                    currentValue < neighbourValue
                }
        }
            .toSet()
    }

    fun part1(input: List<String>): Int {
        val parsedMap = parseMap(input)
        return findAllLowesPoints(parsedMap)
            .sumOf { currentPoint ->
                val currentValue = parsedMap[currentPoint]!!
                currentValue + 1
            }
    }

    fun recourciveFindAllBasins(
        point: Point,
        mapOfValues: Map<Point, Int>,
        processed: MutableSet<Point>
    ) {
        if (mapOfValues[point] == 9) return
        if (point in processed) return
        processed.add(point)
        point.neighbours()
            .filter { mapOfValues[it] != null }
            .forEach { currentPoint ->
                recourciveFindAllBasins(currentPoint, mapOfValues, processed)
            }
    }

    fun part2(input: List<String>): Int {
        val parsedMap = parseMap(input)
        val points = findAllLowesPoints(parsedMap)

        return points.map {
            val firstSet = mutableSetOf<Point>()
            recourciveFindAllBasins(it, parsedMap, firstSet)
            firstSet.size
        }
            .sorted()
            .takeLast(3)
            .reduce { acc, i -> acc * i }
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    val part1Test = part1(testInput)

    println(part1Test)
    check(part1Test == 15)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
