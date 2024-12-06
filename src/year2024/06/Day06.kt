package year2024.`06`

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import readInput


private const val CURRENT_DAY = "06"

enum class Direction {
    BOTTOM,
    TOP,
    LEFT,
    RIGHT;

    fun rotate90(): Direction {
        return when (this) {
            Direction.BOTTOM -> Direction.LEFT
            Direction.TOP -> Direction.RIGHT
            Direction.LEFT -> Direction.TOP
            Direction.RIGHT -> Direction.BOTTOM
        }
    }
}

data class Point(
    val x: Int,
    val y: Int,
) {
    fun moveUp(): Point = copy(y = y - 1)
    fun moveDown(): Point = copy(y = y + 1)
    fun moveLeft(): Point = copy(x = x - 1)
    fun moveRight(): Point = copy(x = x + 1)
}

private fun parseMap(input: List<String>): Pair<Point, Set<Point>> {
    val mutableMap = mutableSetOf<Point>()
    var point: Point? = null
    input.forEachIndexed { y, line ->
        line.split("")
            .filter { it.isNotBlank() }
            .forEachIndexed { x, value ->
                if (value == "#") {
                    mutableMap.add(Point(x, y))
                }
                if (value == "^") {
                    point = Point(x, y)
                }
            }
    }
    return point!! to mutableMap
}

fun Point.isInBounds(points: Set<Point>): Boolean {
    val maxX = points.maxOf { it.x }
    val maxY = points.maxOf { it.y }

    return (x > maxX || y > maxY || x < 0 || y < 0).not()
}

data class DirPoint(
    val point: Point,
    val direction: Direction,
)

fun Set<Point>.moveAcrossMap(initialPoint: Point): Set<Point> {
    var currentPoint = initialPoint
    var currentDirection = Direction.TOP

    val visitedPoints = mutableSetOf(currentPoint)
    val visitedPointsDir = mutableSetOf(DirPoint(currentPoint, Direction.TOP))

    while (currentPoint.isInBounds(this)) {
        val visitedPointDirsBefore = visitedPointsDir.toSet()

        val newCurrentPoint = when (currentDirection) {
            Direction.BOTTOM -> {
                val nextPoint = currentPoint.moveDown()
                if (nextPoint in this) {
                    currentDirection = currentDirection.rotate90()
                    currentPoint
                } else {
                    nextPoint
                }
            }

            Direction.TOP -> {
                val nextPoint = currentPoint.moveUp()
                if (nextPoint in this) {
                    currentDirection = currentDirection.rotate90()
                    currentPoint
                } else {
                    nextPoint
                }
            }

            Direction.LEFT -> {
                val nextPoint = currentPoint.moveLeft()
                if (nextPoint in this) {
                    currentDirection = currentDirection.rotate90()
                    currentPoint
                } else {
                    nextPoint
                }
            }

            Direction.RIGHT -> {
                val nextPoint = currentPoint.moveRight()
                if (nextPoint in this) {
                    currentDirection = currentDirection.rotate90()
                    currentPoint
                } else {
                    nextPoint
                }
            }
        }

        if (newCurrentPoint.isInBounds(this)) {
            visitedPoints.add(newCurrentPoint)
            visitedPointsDir.add(DirPoint(newCurrentPoint, currentDirection))
            if (visitedPointsDir == visitedPointDirsBefore) {
                return emptySet()
            }
        }
        currentPoint = newCurrentPoint
    }
    return visitedPoints
}


// part 2
fun Set<Point>.generateAllNewPossibleStepsOptimal(
    allInitialPoints: Set<Point>,
    initialPoint: Point,
): List<Set<Point>> {
    val list = mutableListOf<Set<Point>>()
    this.forEach { currentPoint ->
        if (currentPoint == initialPoint) return@forEach
        if (currentPoint in allInitialPoints) return@forEach
        list.add(allInitialPoints + currentPoint)
    }
    return list
}

fun main() {

    fun part1(input: List<String>): Int {
        val (initpoint, map) = parseMap(input)
        val res = map.moveAcrossMap(initpoint)
        return res.size
    }

    fun part2(input: List<String>): Int {
        val (initPoint, map) = parseMap(input)
        val visitedMap = map.moveAcrossMap(initPoint)
        val allNewSteps = visitedMap.generateAllNewPossibleStepsOptimal(
            allInitialPoints = map,
            initialPoint = initPoint,
        )
        println("PART 2 VISITED POINTS SIZE: " + allNewSteps.size)
        var resresres = 0
        val job = GlobalScope.launch {
            coroutineScope {
                var totalCount = 0
                val parallelised =
                    allNewSteps.windowed(allNewSteps.size / 24, allNewSteps.size / 24, partialWindows = true)
                check(parallelised.flatten() == allNewSteps)
                val reses = parallelised.map { currentList ->
                    async {
                        currentList.count { curr ->
                            val size = curr.moveAcrossMap(initPoint)
                            totalCount++
                            if (totalCount % 500 == 0) println("TOTAL PROCESSED: $totalCount")
                            size.isEmpty()
                        }
                    }
                }

                val resDeffereds = reses.awaitAll()
                val sumOfDeffered = resDeffereds.sum()

                println(resDeffereds)
                resresres = sumOfDeffered
                println("Final Sum Answer = $sumOfDeffered")
            }
        }
        runBlocking { job.join() }
        return resresres
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 41)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 6)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 4433)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 1516)
}
