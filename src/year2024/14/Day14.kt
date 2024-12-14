package year2024.`14`

import readInput

private const val CURRENT_DAY = "14"

private data class Point(
    val x: Int,
    val y: Int,
) {
    fun moveTop(): Point = copy(y = y - 1)
    fun moveRight(): Point = copy(x = x + 1)
    fun moveBottom(): Point = copy(y = y + 1)
    fun moveLeft(): Point = copy(x = x - 1)

    override fun toString(): String {
        return "{$x,$y}"
    }
}

private data class RobotInfo(
    val position: Point,
    val velocity: Point,
) {
    override fun toString(): String = "[$position,$velocity]"

    fun moveIn(
        grid: Point,
    ): RobotInfo {
        val buffNewX = (position.x + velocity.x) % grid.x
        val newX = if (buffNewX < 0) buffNewX + grid.x else buffNewX
        val buffNewY = (position.y + velocity.y) % grid.y
        val newY = if (buffNewY < 0) buffNewY + grid.y else buffNewY

        return copy(
            position = Point(
                x = newX,
                y = newY,
            ),
        )
    }
}

// p=91,23 v=98,-65
private fun parseLineInto(
    line: String,
): RobotInfo {
    val items = line.split("p=", "v=")
        .map { it.trim() }
        .filter { it.contains(",") }
        .map {
            val numbers = it.split(",").map { it.toInt() }
            Point(
                numbers[0],
                numbers[1],
            )
        }
    return RobotInfo(
        items[0],
        items[1],
    )
}

private fun List<RobotInfo>.printRobots(grid: Point) {
    val roboMap = this.groupBy { it.position }
    for (y in 0..grid.y - 1) {
        for (x in 0..grid.x - 1) {
            val count = roboMap[Point(x, y)]?.count()
            if (count == null) {
                print(".")
            } else {
                print("1")
            }
        }
        println()
    }
}


private fun walkUntilAllCategoriesAreNotFound(
    map: Set<Point>,
    initialPoint: Point,
): Set<Point> {
    val visitedPoints = mutableSetOf<Point>()

    val queue: ArrayDeque<Point> = ArrayDeque()
    queue.add(initialPoint)

    while (queue.isNotEmpty()) {
        val currentItem = queue.removeFirst()
        if (currentItem in visitedPoints) continue

        visitedPoints.add(currentItem)

        fun addIfNeeded(p: Point) {
            if (p in map) {
                queue.add(p)
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

private fun List<RobotInfo>.performMovementNTimes(n: Int, grid: Point): List<RobotInfo> {
    var currentRobots = this

    repeat(n) {
        currentRobots = currentRobots.map { it.moveIn(grid) }


        val set = currentRobots.map { it.position }.toSet()
        val itemsWith10 = currentRobots.map { currRobot ->
            walkUntilAllCategoriesAreNotFound(
                set,
                currRobot.position,
            )
        }.filter { it.size > 50 }

        if (itemsWith10.isNotEmpty()) {
            println("After ${it + 1} second:")
            currentRobots.printRobots(grid)
            println()
        }
    }

    return currentRobots
}

private fun List<RobotInfo>.calculateValue(grid: Point): Int {
    var q1 = 0
    var q2 = 0
    var q3 = 0
    var q4 = 0


    val middleX = grid.x / 2
    val middleY = grid.y / 2
    map { it.position }.forEach {
        when {
            it.x < middleX && it.y < middleY -> q1++
            it.x > middleX && it.y < middleY -> q2++
            it.x < middleX && it.y > middleY -> q3++
            it.x > middleX && it.y > middleY -> q4++
        }
    }

    return q1 * q2 * q3 * q4
}

fun main() {

    fun part1(input: List<String>, grid: Point): Int {
        val robots = input.map {
            parseLineInto(it)
        }
        val result = robots.performMovementNTimes(100, grid)
        println(robots)
        return result.calculateValue(grid)
    }

    fun part2(input: List<String>, grid: Point): Int {
        val robots = input.map {
            parseLineInto(it)
        }
        val result = robots.performMovementNTimes(10000, grid)
        println(robots)
        return result.calculateValue(grid)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput, Point(11, 7))
    println(part1Test)
    check(part1Test == 12)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input, Point(101, 103))
    println(part1)
    check(part1 == 221655456)

    // Part 2
    val part2 = part2(input, Point(101, 103))
    println(part2)
}
