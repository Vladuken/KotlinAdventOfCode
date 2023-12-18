package year2023.`18`

import readInput
import utils.printDebug
import utils.printlnDebug
import kotlin.math.absoluteValue

private const val CURRENT_DAY = "18"

private data class Point(
    val x: Int,
    val y: Int,
) {
    val left get() = Point(x - 1, y)
    val right get() = Point(x + 1, y)
    val top get() = Point(x, y - 1)
    val bottom get() = Point(x, y + 1)
    override fun toString(): String = "[$x,$y]"
}

private fun Direction.calculateNextPoint(currentPoint: Point): Point {
    return when (this) {
        Direction.LEFT -> currentPoint.left
        Direction.RIGHT -> currentPoint.right
        Direction.TOP -> currentPoint.top
        Direction.BOTTOM -> currentPoint.bottom
    }
}


enum class Direction {
    BOTTOM,
    TOP,
    LEFT,
    RIGHT;

    override fun toString(): String {
        return when (this) {
            BOTTOM -> "B"
            TOP -> "T"
            LEFT -> "L"
            RIGHT -> "R"
        }
    }

    companion object {
        fun fromString(string: String): Direction {
            return when (string) {
                "R" -> RIGHT
                "L" -> LEFT
                "U" -> TOP
                "D" -> BOTTOM
                else -> error("")
            }
        }

        fun fromHex(string: String): Direction {
            return when (string) {
                "0" -> RIGHT
                "1" -> BOTTOM
                "2" -> LEFT
                "3" -> TOP
                else -> error("")
            }
        }
    }
}

private data class Line(
    val start: Point,
    val end: Point,
)

data class Command(
    val direction: Direction,
    val amount: Int,
)

private fun parseLineIntoCommands(
    line: String
): Command {
    val list = line.split(" ")
    return Command(
        Direction.fromString(list[0]),
        list[1].toInt(),
    )
}

private fun parseHexLineIntoCommand(
    line: String
): Command {
    val list = line.split(" ")
    val last = list[2].drop(1).dropLast(1)

    return Command(
        Direction.fromHex(last.last().toString()),
        last.drop(1).dropLast(1).toInt(16),
    )
}

private fun processLines(list: List<Command>): Set<Line> {
    val resSet = mutableSetOf<Line>()
    var currentPoint = Point(0, 0)
    list.forEach { command ->
        val listOf = mutableListOf<Point>()
        var cur = currentPoint
        repeat(command.amount) {
            val newPoint = when (command.direction) {
                Direction.BOTTOM -> cur.bottom
                Direction.TOP -> cur.top
                Direction.LEFT -> cur.left
                Direction.RIGHT -> cur.right
            }
            listOf.add(newPoint)
            cur = newPoint
        }

        resSet.add(Line(currentPoint, listOf.last()))
        currentPoint = listOf.last()
    }
    return resSet
}

private fun processCommandsIntoPoints(list: List<Command>): Set<Point> {
    val resSet = mutableSetOf<Point>()
    var currentPoint = Point(0, 0)
    list.forEach { command ->
        repeat(command.amount) {
            val newPoint = when (command.direction) {
                Direction.BOTTOM -> currentPoint.bottom
                Direction.TOP -> currentPoint.top
                Direction.LEFT -> currentPoint.left
                Direction.RIGHT -> currentPoint.right
            }
            resSet.add(currentPoint)
            currentPoint = newPoint
        }
    }
    return resSet
}

private fun printSet(set: Set<Point>) {
    val minX = set.minOf { it.x }
    val minY = set.minOf { it.y }
    val maxX = set.maxOf { it.x }
    val maxY = set.maxOf { it.y }
    println()
    for (i in minY..maxY) {
        for (x in minX..maxX) {
            val symbol = if (Point(x, i) in set) "#" else "."
            printDebug { symbol }
        }
        printlnDebug { }
    }
    printlnDebug { }
}

private fun Point.neighbours(): Set<Point> {
    return setOf(
        Point(x + 1, y),
        Point(x - 1, y),
        Point(x, y + 1),
        Point(x, y - 1),
    )
}

private fun searchForInternalPoints(set: Set<Point>): Set<Point> {
    val minX = set.minOf { it.x }
    val minY = set.minOf { it.y }
    val maxX = set.maxOf { it.x }
    val maxY = set.maxOf { it.y }

    val initialPoint = Point(1, 1)
    val queue = ArrayDeque(listOf(initialPoint))

    val visitedPoints = mutableSetOf<Point>()

    while (queue.isNotEmpty()) {
        val currentPoint = queue.removeFirst()
        if (currentPoint in visitedPoints) continue
        visitedPoints.add(currentPoint)
        currentPoint.neighbours()
            .filter { it !in set }
            .filter { it.x in minX..maxX && it.y in minY..maxY }
            .forEach { queue.add(it) }
    }
    visitedPoints.addAll(set)

    return visitedPoints
}

fun main() {

    fun part1(input: List<String>): Int {
        val commands = input.map { parseLineIntoCommands(it) }
        val points = processCommandsIntoPoints(commands)
        printSet(points)
        printlnDebug { }
        val allPoints = searchForInternalPoints(points)
        printSet(allPoints)
        return allPoints.size
    }

    fun part2(input: List<String>): Long {
        val map = input.map { parseHexLineIntoCommand(it) }
        val lines = processLines(map)
        val outerArea = lines.sumOf {
            val xDif = (it.start.x - it.end.x).absoluteValue
            val yDif = (it.start.y - it.end.y).absoluteValue
            xDif + yDif
        }
        val internalArea = lines.sumOf {
            val firstDif = it.start.x.toLong() * it.end.y.toLong()
            val secondDif = it.end.x.toLong() * it.start.y.toLong()
            firstDif - secondDif
        }.absoluteValue / 2L

        return internalArea + outerArea / 2 + 1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 62)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 35244)


    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 952408144115L)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 85070763635666L)
}
