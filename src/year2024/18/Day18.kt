package year2024.`18`

import readInput
import utils.findShortestPathByPredicate
import utils.printDebug
import utils.printlnDebug

private const val CURRENT_DAY = "18"


private data class Point(
    val x: Long,
    val y: Long,
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

private fun parseLineInto(
    line: String,
): Point {
    val items = line.split(",")
        .mapNotNull { it.toLongOrNull() }
    return Point(
        items.first(),
        items[1],
    )
}


private data class CurrentStep(
    val currentPoint: Point,
) {
    fun allNextSteps(
        grid: Point,
        setOfCorrupted: Set<Point>,
    ): Set<CurrentStep> {
        return currentPoint.neighbours()
            .filter {
                val currentPoint = it
                currentPoint.x >= 0L && currentPoint.x < grid.x &&
                        currentPoint.y >= 0L && currentPoint.y < grid.y &&
                        currentPoint !in setOfCorrupted
            }
            .map {
                CurrentStep(
                    it,
                )
            }

            .toSet()
    }
}

private fun printMap(
    grid: Point,
    initialSet: Set<Point>,
    corrupted: Set<Point>,
) {
    val maxX = grid.x
    val maxY = grid.y

    for (y in 0..maxY - 1) {
        for (x in 0..maxX - 1) {
            val currentPoint = Point(x, y)
            printDebug {
                "" + if (currentPoint in initialSet) {
                    "O"
                } else if (currentPoint in corrupted) {
                    "#"
                } else {
                    "."
                }
            }
        }
        printlnDebug { "" }
    }
}

fun main() {

    fun part1(
        input: List<String>,
        bytesCount: Int,
        grid: Point,
        maxPoint: Point,
    ): Int {

        val points = input.map { parseLineInto(it) }
            .take(bytesCount)
            .toSet()

        val path = findShortestPathByPredicate(
            start = CurrentStep(
                currentPoint = Point(0, 0),
            ),
            endFunction = { (p) -> p == maxPoint },
            neighbours = {
                it.allNextSteps(
                    grid = grid,
                    setOfCorrupted = points,
                )
            },
            cost = { _, _ -> 1 },
            heuristic = { (it.currentPoint.x + it.currentPoint.y).toInt() },
        )

        return minOf(path.getScore())
    }

    fun part2(input: List<String>): String {
        var count = 1024
        try {
            while (true) {
                part1(
                    input = input,
                    bytesCount = count,
                    grid = Point(71, 71),
                    maxPoint = Point(70, 70)
                )
                count++
            }
        } catch (e: Exception) {
            return input[count - 1]
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(
        input = testInput,
        bytesCount = 12,
        grid = Point(7, 7),
        maxPoint = Point(6, 6),
    )
    println(part1Test)
    check(part1Test == 22)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(
        input = input,
        bytesCount = 1024,
        grid = Point(71, 71),
        maxPoint = Point(70, 70),
    )
    println(part1)
    check(part1 == 438)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == "26,22")
}
