package year2024.`08`

import readInput
import kotlin.collections.mutableMapOf

private const val CURRENT_DAY = "08"


data class Point(
    val x: Int,
    val y: Int,
)

private fun parseMap(input: List<String>): Map<String, Set<Point>> {
    val mutableMap = mutableMapOf<String, Set<Point>>()
    input.forEachIndexed { y, line ->
        line.split("")
            .filter { it.isNotBlank() }
            .forEachIndexed { x, value ->
                mutableMap.compute(value) { key, mapValue ->
                    if (mapValue == null) {
                        setOf(Point(x, y))
                    } else {
                        mapValue + Point(x, y)
                    }
                }
            }
    }
    mutableMap.remove(".")
    return mutableMap
}

fun Set<Point>.generateAllAntiNodes(
    width: Int,
    height: Int,
    isSecondPart: Boolean,
): Set<Point> {
    val mutableSet = mutableSetOf<Point>()

    var currentPoint = this.first()
    var currentList = this.drop(1).toList()

    while (currentList.isNotEmpty()) {
        currentList.forEach { it ->
            val points = if (isSecondPart) {
                currentPoint.generateAntiNodes2(
                    other = it,
                    maxX = width,
                    maxY = height,
                )
            } else {
                currentPoint.generateAntiNodes(
                    other = it,
                ).toList()
            }
            mutableSet.addAll(points)
        }

        currentPoint = currentList.first()
        currentList = currentList.drop(1)
    }
    return mutableSet
}

fun Point.generateAntiNodes(other: Point): Pair<Point, Point> {
    val inBetweenPointX = x - other.x
    val inBetweenPointY = y - other.y

    return Point(
        x + inBetweenPointX,
        y + inBetweenPointY
    ) to Point(
        other.x - inBetweenPointX,
        other.y - inBetweenPointY
    )
}


fun Point.generateAntiNodes2(
    other: Point,
    maxX: Int,
    maxY: Int,
): Set<Point> {
    val inBetweenPointX = x - other.x
    val inBetweenPointY = y - other.y

    val resSet = mutableSetOf<Point>(
        this,
        other
    )

    var currentX = x
    var currentY = y
    while ((currentX < maxX && currentY < maxY) && (currentX >= 0 && currentY >= 0)) {
        val newX = currentX + inBetweenPointX
        val newY = currentY + inBetweenPointY


        val newPoint = Point(currentX, currentY)
        resSet.add(newPoint)

        currentX = newX
        currentY = newY
    }

    currentX = x
    currentY = y

    while ((currentX < maxX && currentY < maxY) && (currentX >= 0 && currentY >= 0)) {
        val newX = currentX - inBetweenPointX
        val newY = currentY - inBetweenPointY

        resSet.add(Point(currentX, currentY))

        currentX = newX
        currentY = newY
    }

    return resSet
}

fun main() {

    fun solution(input: List<String>, secondPart: Boolean): Int {
        val readyMap = parseMap(input)

        val height = input.size
        val width = input.first().length

        val uniqueItems = mutableSetOf<Point>()
        readyMap.forEach { (_, value) ->
            val results = if (secondPart) {
                value.generateAllAntiNodes(width, height, secondPart)
            } else {
                value.generateAllAntiNodes(width, height, false)
            }
            val resres = results.filter {
                (it.x < width && it.y < height) && (it.x >= 0 && it.y >= 0)
            }

            uniqueItems.addAll(resres)
        }

        return uniqueItems.size
    }

    fun part1(input: List<String>): Int {
        return solution(input, false)
    }

    fun part2(input: List<String>): Int {
        return solution(input, true)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 14)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 34)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 261)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 898)
}
