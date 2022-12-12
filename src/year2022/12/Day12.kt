package year2022.`12`

import kotlin.math.abs
import readInput

data class Square(
    val i: Int,
    val j: Int,
    val height: Int,
)

data class SquareRecord(
    val currentNode: Square,
    var shortestDistance: Int,
    var previousNode: Square?
)

fun calculateDistances(start: Square, grid: List<List<Square>>): List<SquareRecord> {

    fun nodeAtOrNull(i: Int, j: Int): Square? {
        val iMax = grid.lastIndex
        val jMax = grid[0].lastIndex

        return if (i < 0 || i > iMax || j < 0 || j > jMax) null else grid[i][j]
    }

    fun Square.neighbors() = listOfNotNull(
        nodeAtOrNull(i - 1, j), nodeAtOrNull(i + 1, j),
        nodeAtOrNull(i, j - 1), nodeAtOrNull(i, j + 1)
    )
        .filter { it.height + 1 >= height }

    val result: List<SquareRecord> = grid
        .flatten()
        .map {
            SquareRecord(
                currentNode = it,
                shortestDistance = if (it == start) 0 else Int.MAX_VALUE,
                previousNode = null
            )
        }

    val unvisitedSquares = grid.flatten().toMutableSet()
    val visitedSquares = mutableSetOf<Square>()

    while (unvisitedSquares.isNotEmpty()) {
        // Find next record to visit
        val record = result
            .filter { it.currentNode in unvisitedSquares }
            .minBy { it.shortestDistance }

        //Find neighbours that have one step distance
        val neighbours = record.currentNode.neighbors()
        neighbours
            .filter { it in unvisitedSquares }
            .forEach { neighbour ->
                // Find current record for this neighbour
                val neighbourRecord = result
                    .find { it.currentNode == neighbour } ?: error("Illegal State")
                // Calculate new distance (1 is a step)
                val distance = record.shortestDistance + 1
                // If distance is less - then update record with new distance
                if (distance < neighbourRecord.shortestDistance) {
                    neighbourRecord.shortestDistance = distance
                    neighbourRecord.previousNode = record.currentNode
                }
            }

        // Mark node as visited
        unvisitedSquares.remove(record.currentNode)
        visitedSquares.add(record.currentNode)
    }

    return result
}

fun main() {

    fun parseInput(input: List<String>): Triple<Square, Square, List<List<Square>>> {
        var start: Square? = null
        var end: Square? = null
        val grid = input.mapIndexed { i, line ->
            line
                .split("")
                .dropLast(1)
                .drop(1)
                .mapIndexed { j, symbol ->
                    val isS = symbol == "S"
                    val isE = symbol == "E"
                    val newChar = when {
                        isS -> 'a'
                        isE -> 'z'
                        else -> symbol[0]
                    }
                    Square(
                        i = i,
                        j = j,
                        height = abs('a' - newChar),
                    ).also {
                        if (isS) start = it
                        if (isE) end = it
                    }
                }
        }
        return Triple(start!!, end!!, grid)
    }

    fun part1(input: List<String>): Int {
        val (start, end, grid) = parseInput(input)
        val results = calculateDistances(end, grid)
        val sum = results.find { it.currentNode == start }
        return sum!!.shortestDistance
    }

    fun part2(input: List<String>): Int {
        val (_, end, grid) = parseInput(input)
        val results = calculateDistances(end, grid)
        val sum = results
            .filter { it.currentNode.height == 0 }
            .filter { it.shortestDistance >= 0 }
            .minBy { it.shortestDistance }
        return sum.shortestDistance
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    val part1Test = part1(testInput)
    val part2Test = part2(testInput)

    println(part1Test)
    check(part1Test == 31)
    check(part2Test == 29)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}
