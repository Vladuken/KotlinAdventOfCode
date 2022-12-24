package year2022.`24`

import java.util.PriorityQueue
import readInput
import utils.doWithPrintedTime

data class Point(
    val x: Int,
    val y: Int
)

enum class Direction { LEFT, RIGHT, BOTTOM, TOP }

/**
 * Sealed class representing either Blizzard or Wall
 */
sealed class Structure {
    abstract val position: Point

    data class Blizzard(
        override val position: Point,
        val direction: Direction
    ) : Structure()

    data class Wall(
        override val position: Point,
    ) : Structure()
}


data class State(
    val minute: Int,
    val ourPosition: Point,
    val currentGrid: Set<Structure>,
) : Comparable<State> {
    override fun compareTo(other: State): Int {
        return compareValuesBy(this, other) { it.minute }
    }
}


private fun parseInput(input: List<String>): Set<Structure> {
    return input.mapIndexed { y, line ->
        line.split("")
            .filter { it.isNotEmpty() }
            .mapIndexedNotNull { x, cell ->
                if (cell == "#") return@mapIndexedNotNull Structure.Wall(Point(x, y))
                val direction = when (cell) {
                    ">" -> Direction.RIGHT
                    "<" -> Direction.LEFT
                    "^" -> Direction.TOP
                    "v" -> Direction.BOTTOM
                    "." -> null
                    else -> error("Illegal state")
                }
                if (direction == null) {
                    null
                } else {
                    Structure.Blizzard(
                        position = Point(x, y),
                        direction = direction
                    )
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
        Direction.LEFT -> Point(y = y, x = x - 1)
        Direction.RIGHT -> Point(y = y, x = x + 1)
        Direction.BOTTOM -> Point(y = y + 1, x = x)
        Direction.TOP -> Point(y = y - 1, x = x)
    }
}

/**
 * Helper method to print grid of all points in current set
 */
fun Set<Structure>.printGrid() {
    val minX = minOf { it.position.x }
    val minY = minOf { it.position.y }
    val maxX = maxOf { it.position.x }
    val maxY = maxOf { it.position.y }

    (minY..maxY).forEach { y ->
        (minX..maxX).forEach { x ->
            val cells = filter { Point(x, y) == it.position }
            val whatToPrint = when {
                cells.isEmpty() -> "."
                cells.size == 1 -> when (val cell = cells.first()) {
                    is Structure.Blizzard -> when (cell.direction) {
                        Direction.LEFT -> "<"
                        Direction.RIGHT -> ">"
                        Direction.BOTTOM -> "v"
                        Direction.TOP -> "^"
                    }
                    is Structure.Wall -> "#"
                }
                else -> cells.size
            }

            print(whatToPrint)
        }
        println()
    }
    println()
}


fun Set<Point>.calculateEdgePosition(
    currentPoint: Point,
    direction: Direction
): Point {
    val minX = minOf { it.x }
    val minY = minOf { it.y }
    val maxX = maxOf { it.x }
    val maxY = maxOf { it.y }

    return when (direction) {
        Direction.LEFT -> currentPoint.copy(x = maxX - 1)
        Direction.RIGHT -> currentPoint.copy(x = minX + 1)
        Direction.BOTTOM -> currentPoint.copy(y = minY + 1)
        Direction.TOP -> currentPoint.copy(y = maxY - 1)
    }
}

fun processBlizzard(
    wallPoints: Set<Point>,
    point: Point,
    direction: Direction
): Point {
    val proposedPosition = point.calculateNewPosition(direction)
    return if (wallPoints.contains(proposedPosition)) {
        wallPoints.calculateEdgePosition(point, direction)
    } else {
        proposedPosition
    }
}

fun processOurPosition(
    wallPoints: Set<Point>,
    point: Point,
    direction: Direction
): Point? {
    val proposedPosition = point.calculateNewPosition(direction)
    return if (wallPoints.contains(proposedPosition)) {
        null
    } else {
        proposedPosition
    }
}

fun round(
    wallPoints: Set<Point>,
    grid: Set<Structure>,
): Set<Structure> {

    val resultSet = mutableSetOf<Structure>()
    grid.forEach {
        when (it) {
            is Structure.Blizzard -> {
                val newPosition = processBlizzard(wallPoints, it.position, it.direction)
                resultSet.add(Structure.Blizzard(newPosition, it.direction))
            }
            is Structure.Wall -> {
                resultSet.add(it)
            }
        }
    }

    return resultSet
}

private val directions = Direction.values()
fun Point.neighbours(
    maxY: Int,
    wallPoints: Set<Point>
): Set<Point> {
    return directions
        .mapNotNull { processOurPosition(wallPoints, this, it) }
        .filter { it.y in 0..maxY }
        .toSet()
        .let { it + this }
}

typealias StateCache = MutableMap<Int, Set<Structure>>

fun StateCache.cacheAndGet(
    wallPoints: Set<Point>,
    currentState: State
): Set<Structure> {
    val newGrid = if (this.contains(currentState.minute)) {
        this[currentState.minute]!!
    } else {
        val calculatedRound = round(wallPoints, currentState.currentGrid)
        this[currentState.minute] = calculatedRound
        calculatedRound
    }
    return newGrid
}


typealias BlizzardCache = MutableMap<Int, Set<Point>>

private fun BlizzardCache.cacheAndGetBlizzards(
    newGrid: Set<Structure>,
    currentState: State
): Set<Point> {
    return if (contains(currentState.minute)) {
        this[currentState.minute]!!
    } else {
        val calculatedRound = newGrid.blizzardPoints()
        this[currentState.minute] = calculatedRound
        calculatedRound
    }
}


@SuppressWarnings("all")
fun solve(
    cachedStates: StateCache,
    blizzardCache: BlizzardCache,
    initialTime: Int,
    initialGrid: Set<Structure>,
    initialPoint: Point,
    destination: Point
): Int {
    val wallPoints: Set<Point> = initialGrid.wallPoints()
    val maxY = wallPoints.maxOf { it.y }

    val queue = PriorityQueue<State>().also {
        it.add(
            State(
                minute = initialTime,
                ourPosition = initialPoint,
                currentGrid = initialGrid
            )
        )
    }

    val visitedStates = mutableSetOf<State>()

    var resultTime = Int.MAX_VALUE
    while (queue.isNotEmpty()) {
        val currentState = queue.remove()

        if (currentState in visitedStates) continue
        visitedStates.add(currentState)

        if (currentState.ourPosition == destination) {
            resultTime = minOf(currentState.minute, resultTime)
            continue
        }

        if (currentState.minute > resultTime) continue

        val newGrid = cachedStates.cacheAndGet(wallPoints, currentState)
        val blizzardPoints = blizzardCache.cacheAndGetBlizzards(newGrid, currentState)

        val nextStates = currentState.ourPosition
            .neighbours(maxY, wallPoints)
            .filter { it !in blizzardPoints }
            .map {
                State(
                    minute = currentState.minute + 1,
                    ourPosition = it,
                    currentGrid = newGrid
                )
            }
            .toList()

        queue.addAll(nextStates)
    }

    return resultTime
}


fun Set<Structure>.wallPoints(): Set<Point> {
    return asSequence()
        .filterIsInstance<Structure.Wall>()
        .map { it.position }
        .toSet()
}

fun Set<Structure>.blizzardPoints(): Set<Point> {
    return asSequence()
        .filterIsInstance<Structure.Blizzard>()
        .map { it.position }
        .toSet()
}

fun main() {

    fun part1(input: List<String>): Int {
        val parsedInput = parseInput(input)
        val cachedStates: StateCache = mutableMapOf()
        val cachedBlizzards: BlizzardCache = mutableMapOf()

        val walls = parsedInput.wallPoints()
        val maxX = walls.maxOf { it.x }
        val maxY = walls.maxOf { it.y }

        val initialPosition = Point(1, 0)
        val destinationPosition = Point(maxX - 1, maxY)

        return solve(
            cachedStates = cachedStates,
            blizzardCache = cachedBlizzards,
            initialTime = 0,
            initialGrid = parsedInput,
            initialPoint = initialPosition,
            destination = destinationPosition
        )
    }

    fun part2(input: List<String>): Int {
        val parsedInput = parseInput(input)

        val cachedStates: StateCache = mutableMapOf()
        val cachedBlizzards: BlizzardCache = mutableMapOf()

        val walls = parsedInput.wallPoints()
        val maxX = walls.maxOf { it.x }
        val maxY = walls.maxOf { it.y }

        val initialPosition = Point(1, 0)
        val destinationPosition = Point(maxX - 1, maxY)

        val time1 = solve(
            cachedStates = cachedStates,
            blizzardCache = cachedBlizzards,
            initialTime = 0,
            initialGrid = parsedInput,
            initialPoint = initialPosition,
            destination = destinationPosition
        )
        val time2 = solve(
            cachedStates = cachedStates,
            blizzardCache = cachedBlizzards,
            initialTime = time1,
            initialGrid = cachedStates[time1]!!,
            initialPoint = destinationPosition,
            destination = initialPosition,
        )
        val time3 = solve(
            cachedStates = cachedStates,
            blizzardCache = cachedBlizzards,
            initialTime = time2,
            initialGrid = cachedStates[time2]!!,
            initialPoint = initialPosition,
            destination = destinationPosition
        )
        return time3
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day24_test")
    val part1Test = doWithPrintedTime("Test 1") { part1(testInput) }
    val part2Test = doWithPrintedTime("Test 2") { part2(testInput) }

    check(part1Test == 18)
    check(part2Test == 54)

    val input = readInput("Day24")
    doWithPrintedTime("Part 1") { part1(input) }
    doWithPrintedTime("Part 2") { part2(input) }
}
