package year2023.`23`

import readInput

private const val CURRENT_DAY = "23"


private data class Point(
    val x: Int,
    val y: Int,
) {
    override fun toString(): String {
        return "[$x,$y]"
    }
}

private fun Point.neighbours(
    currentValue: String,
): Set<Point> {
    return when (currentValue) {
        ">" -> setOf(
            Point(x = x + 1, y = y),
        )

        "v" -> setOf(
            Point(x = x, y = y + 1),
        )

        "." -> setOf(
            Point(x = x, y = y + 1),
            Point(x = x, y = y - 1),
            Point(x = x + 1, y = y),
            Point(x = x - 1, y = y),
        )

        else -> error("Illegal neighbours value: $currentValue")
    }
}

private fun solve(input: List<String>, isPart1: Boolean): Int {
    val start = Point(1, 0)
    val end = Point(input.first().lastIndex - 1, input.lastIndex)
    val visited = Array(input.size) { BooleanArray(input.first().length) }
    return findMax(
        isPart1 = isPart1,
        input = input,
        end = end,
        point = start,
        visited = visited,
        current = 0,
    )
}

private fun findMax(
    isPart1: Boolean,
    input: List<String>,
    end: Point,
    point: Point,
    visited: Array<BooleanArray>,
    current: Int,
): Int {
    // Skip if outside of the box
    if (point.y !in input.indices || point.x !in input.first().indices) {
        return -1
    }

    // Skip if Visited
    if (visited[point.y][point.x]) {
        return -1
    }

    // Skip If Wall
    if (input[point.y][point.x] == '#') {
        return -1
    }

    // Return answer
    if (point == end) {
        return current
    }

    // Set this point to visited to avoid going back
    visited[point.y][point.x] = true
    val currentSymbol = input[point.y][point.x].toString().takeIf { isPart1 } ?: "."
    val max = point.neighbours(
        currentValue = currentSymbol,
    ).maxOf { neighbour ->
        findMax(
            isPart1 = isPart1,
            input = input,
            end = end,
            point = neighbour,
            visited = visited,
            current = current + 1,
        )
    }
    // Clear this point, to give ability to other recurstions to visit it.
    visited[point.y][point.x] = false
    return max
}

fun main() {

    fun part1(input: List<String>): Int {
        return solve(input, true)
    }

    fun part2(input: List<String>): Int {
        return solve(input, false)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 94)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 154)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 2170)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 6502)
}