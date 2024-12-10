package year2024.`10`

import readInput

private const val CURRENT_DAY = "10"

private data class Point(
    val x: Int,
    val y: Int,
) {
    fun moveTop(): Point = copy(y = y - 1)
    fun moveRight(): Point = copy(x = x + 1)
    fun moveBottom(): Point = copy(y = y + 1)
    fun moveLeft(): Point = copy(x = x - 1)
}

private fun parseMap(input: List<String>): Map<Point, Int> {
    val mutableMap = mutableMapOf<Point, Int>()
    input.forEachIndexed { y, line ->
        line.split("")
            .filter { it.isNotBlank() }
            .forEachIndexed { x, value ->
                when (value.toIntOrNull()) {
                    in 0..9 -> mutableMap[Point(x, y)] = value.toInt()
                    else -> error("Illegal value x:$x y:$y value:$value")
                }
            }
    }
    return mutableMap
}

private fun walkUntilAllOfStepsAreNotFound(
    map: Map<Point, Int>,
    initialPoint: Point,
): Set<Point> {
    val visitedPoints = mutableSetOf<Point>()

    val queue: ArrayDeque<Point> = ArrayDeque()
    queue.add(initialPoint)

    while (queue.isNotEmpty()) {
        val currentItem = queue.removeFirst()
        val currentValue = map[currentItem] ?: continue
        if (currentItem in visitedPoints) continue

        visitedPoints.add(currentItem)

        fun addIfNeeded(p: Point) {
            map[p]?.let { newVal ->
                if (newVal - currentValue == 1) {
                    queue.add(p)
                }
            }
        }

        val leftPoint = currentItem.moveLeft()
        val rightPoint = currentItem.moveRight()
        val topPoint = currentItem.moveTop()
        val bottomPoint = currentItem.moveBottom()

        addIfNeeded(leftPoint)
        addIfNeeded(rightPoint)
        addIfNeeded(topPoint)
        addIfNeeded(bottomPoint)
    }

    return visitedPoints
}


private fun walkUntilAllOfStepsAreNotFoundPart2(
    map: Map<Point, Int>,
    initialPoint: Point,
): Set<List<Point>> {
    val visitedPaths = mutableSetOf<List<Point>>()

    val queue: ArrayDeque<List<Point>> = ArrayDeque()
    queue.add(listOf(initialPoint))

    while (queue.isNotEmpty()) {
        val currentItemPath = queue.removeFirst()
        val currentItem = currentItemPath.last()
        val currentValue = map[currentItem] ?: continue
        if (currentValue == 9) {
            visitedPaths.add(currentItemPath)
            continue
        }

        fun addIfNeeded(path: List<Point>, p: Point) {
            map[p]?.let { newVal ->
                if (newVal - currentValue == 1) {
                    queue.add(path + p)
                }
            }
        }

        val leftPoint = currentItem.moveLeft()
        val rightPoint = currentItem.moveRight()
        val topPoint = currentItem.moveTop()
        val bottomPoint = currentItem.moveBottom()

        addIfNeeded(currentItemPath, leftPoint)
        addIfNeeded(currentItemPath, rightPoint)
        addIfNeeded(currentItemPath, topPoint)
        addIfNeeded(currentItemPath, bottomPoint)
    }

    return visitedPaths
}


private fun Map<Point, Int>.startPoints(): Set<Point> {
    val expectedSet = mutableSetOf<Point>()
    this.forEach { (key, value) ->
        if (value == 0) {
            expectedSet.add(key)
        }
    }
    return expectedSet
}


fun main() {

    fun part1(input: List<String>): Int {
        val map = parseMap(input)
        val startPoints = map.startPoints()

        val allPaths = startPoints.map {
            walkUntilAllOfStepsAreNotFound(map, it)
        }
        val totalSum = allPaths.map { path ->
            path.count { map[it] == 9 }
        }
        return totalSum.sum()
    }

    fun part2(input: List<String>): Int {
        val map = parseMap(input)
        val startPoints = map.startPoints()

        val allPaths = startPoints.map {
            walkUntilAllOfStepsAreNotFoundPart2(map, it)
        }
        val totalSum = allPaths.map { path ->
            path.count()
        }
        return totalSum.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 36)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 81)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 737)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 1)
}
