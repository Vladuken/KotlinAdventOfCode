package year2022.`23`

import java.util.Collections
import readInput


data class Point(
    val x: Int,
    val y: Int
)

enum class Direction {
    N, S, W, E,
    NE, NW, SE, SW
}

fun parseInput(input: List<String>): Set<Point> {
    return input.mapIndexed { y, line ->
        line.split("")
            .filter { it.isNotEmpty() }
            .mapIndexedNotNull { x, cell ->
                if (cell == "#") {
                    Point(x, y)
                } else {
                    null
                }
            }
    }
        .flatten()
        .toSet()

}

fun Point.calculateNewPosition(
    direction: Direction
): Point {
    return when (direction) {
        Direction.N -> Point(y = y - 1, x = x)
        Direction.S -> Point(y = y + 1, x = x)
        Direction.W -> Point(y = y, x = x - 1)
        Direction.E -> Point(y = y, x = x + 1)
        Direction.NE -> Point(y = y - 1, x = x + 1)
        Direction.NW -> Point(y = y - 1, x = x - 1)
        Direction.SE -> Point(y = y + 1, x = x + 1)
        Direction.SW -> Point(y = y + 1, x = x - 1)
    }
}

fun Point.proposeMovement(direction: Direction): Point {
    return when (direction) {
        Direction.N, Direction.S, Direction.W, Direction.E -> calculateNewPosition(direction)
        else -> error("!! Illegal direction $direction")
    }
}

fun processElf(
    grid: Set<Point>,
    elf: Point,
    listOfMovements: List<Pair<List<Direction>, Direction>>
): Point? {
    /**
     * If all points around current are empty - just don't move
     */
    if (Direction.values().all { elf.calculateNewPosition(it) !in grid }) return null

    /**
     * Find new direction to move
     */
    val newDir = listOfMovements.firstOrNull { (checkDirections, _) ->
        checkDirections.all { elf.calculateNewPosition(it) !in grid }
    }?.second

    return if (newDir == null) {
        null
    } else {
        elf.proposeMovement(newDir)
    }
}

fun processRount(
    points: Set<Point>,
    listOfMovements: List<Pair<List<Direction>, Direction>>
): Set<Point> {
    /**
     * Part 1 of round
     * Create set of not moving points and map of proposed movements
     */
    val finalPointsPosition = mutableSetOf<Point>()
    val proposedMovingPointMap = mutableMapOf<Point, MutableList<Point>>()

    points.forEach { currentElf ->
        val proposedPoint = processElf(points, currentElf, listOfMovements)
        if (proposedPoint == null) {
            finalPointsPosition.add(currentElf)
        } else {
            if (proposedMovingPointMap.containsKey(proposedPoint)) {
                proposedMovingPointMap[proposedPoint]!!.add(currentElf)
            } else {
                proposedMovingPointMap[proposedPoint] = mutableListOf(currentElf)
            }
        }
    }

    /**
     * Part 2 of round
     * If points proposed positions collapses - just don't move them
     */
    proposedMovingPointMap.forEach { (proposedPoint, prevPoints) ->
        when (prevPoints.size) {
            0 -> error("Illegal state $proposedPoint $prevPoints")
            1 -> finalPointsPosition.add(proposedPoint)
            else -> finalPointsPosition.addAll(prevPoints)
        }
    }

    return finalPointsPosition
}


/**
 * Helper method to print grid of all points in current set
 */
fun Set<Point>.printGrid() {
    val minX = minOf { it.x }
    val minY = minOf { it.y }
    val maxX = maxOf { it.x }
    val maxY = maxOf { it.y }

    (minY..maxY).forEach { y ->
        (minX..maxX).forEach { x ->
            val pointToPrint = if (Point(x, y) in this) {
                "#"
            } else {
                "."
            }
            print(pointToPrint)
        }
        println()
    }
    println()
}


fun Set<Point>.countAnswer(): Int {
    val minX = minOf { it.x }
    val minY = minOf { it.y }
    val maxX = maxOf { it.x }
    val maxY = maxOf { it.y }

    val xWidth = maxX - minX + 1
    val yHeight = maxY - minY + 1

    return xWidth * yHeight - count()
}

private fun createInitialListOfDirections(): MutableList<Pair<List<Direction>, Direction>> {
    return mutableListOf(
        listOf(Direction.N, Direction.NE, Direction.NW) to Direction.N,
        listOf(Direction.S, Direction.SE, Direction.SW) to Direction.S,
        listOf(Direction.W, Direction.NW, Direction.SW) to Direction.W,
        listOf(Direction.E, Direction.NE, Direction.SE) to Direction.E
    )

}

fun main() {

    fun part1(input: List<String>): Int {
        val listOfMovements = createInitialListOfDirections()
        var currentGrid = parseInput(input)

        repeat(10) {
            currentGrid = processRount(currentGrid, listOfMovements)
            Collections.rotate(listOfMovements, -1)
        }

        return currentGrid.countAnswer()
    }

    fun part2(input: List<String>): Int {
        var currentGrid = parseInput(input)
        var prevGrid: Set<Point> = emptySet()
        var count = 0
        val listOfMovements = createInitialListOfDirections()

        while (currentGrid != prevGrid) {
            prevGrid = currentGrid
            currentGrid = processRount(currentGrid, listOfMovements)
            Collections.rotate(listOfMovements, -1)
            count++
        }

        return count
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day23_test")
    val part1Test = part1(testInput)
    val part2Test = part2(testInput)

    println(part1Test)
    check(part1Test == 110)
    println(part2Test)
    check(part2Test == 20)

    val input = readInput("Day23")
    println(part1(input))
    println(part2(input))
}
