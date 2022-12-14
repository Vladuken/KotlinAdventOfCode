package year2022.`14`

import readInput

data class Point(
    val x: Int,
    val y: Int
)


sealed class Cell {
    abstract val point: Point

    data class Sand(
        override val point: Point
    ) : Cell()

    data class SandGenerator(
        override val point: Point
    ) : Cell()

    data class Rock(
        override val point: Point
    ) : Cell()
}

fun parseLine(string: String): List<Point> {
    return string.split(" -> ")
        .map { it.split(",") }
        .map { Point(it.first().toInt(), it[1].toInt()) }
}


private fun createSet(paths: List<List<Point>>, generator: Cell.SandGenerator): Set<Cell> {
    val mutableSet = mutableSetOf<Cell>()
    paths.forEach { path ->
        var initial = path.first()
        path.drop(1)
            .forEach { newPoint ->
                when {
                    initial.x == newPoint.x -> {
                        val initMinY = minOf(initial.y, newPoint.y)
                        val initMaxY = maxOf(initial.y, newPoint.y)
                        (initMinY..initMaxY).forEach { newYPoint ->
                            mutableSet.add(Cell.Rock(initial.copy(y = newYPoint)))
                        }
                    }
                    initial.y == newPoint.y -> {
                        val initMinX = minOf(initial.x, newPoint.x)
                        val initMaxX = maxOf(initial.x, newPoint.x)
                        (initMinX..initMaxX).forEach { newXPoint ->
                            mutableSet.add(Cell.Rock(initial.copy(x = newXPoint)))
                        }
                    }
                    else -> error("!! $initial $newPoint")
                }
                initial = newPoint
            }
    }

    mutableSet.add(generator)
    return mutableSet
}

private fun Set<Cell>.rect(): Pair<Point, Point> {
    val minX = minOf { it.point.x }
    val maxX = maxOf { it.point.x }
    val minY = minOf { it.point.y }
    val maxY = maxOf { it.point.y }

    return Point(minX, minY) to Point(maxX, maxY)
}

private fun Point.moveDown() = copy(x = x, y = y + 1)
private fun Point.moveLeft() = copy(x = x - 1, y = y)
private fun Point.moveRight() = copy(x = x + 1, y = y)

/**
 * This could be improved by removing sealed class
 * (I'm sorry for this, but at least it's not O(N)))
 */
private fun Set<Cell>.findFor(point: Point): Cell? {
    val sand = Cell.Sand(point)
    val rock = Cell.Rock(point)
    val generator = Cell.SandGenerator(point)

    return when {
        contains(sand) -> sand
        contains(rock) -> rock
        contains(generator) -> generator
        else -> null
    }
}

private fun Set<Cell>.withDownLine(): Set<Cell> {
    val (leftTop, rightBottom) = rect()

    val yDelta = rightBottom.y - leftTop.y
    val newLeftX = leftTop.x - yDelta
    val newRightX = rightBottom.x + yDelta

    val points = (newLeftX..newRightX)
        .map { Point(it, rightBottom.y + 2) }
        .map { Cell.Rock(it) }

    return this + points
}

private fun Point.pointInRange(
    leftTop: Point,
    rightBottom: Point
): Boolean {
    return (x in leftTop.x..rightBottom.x) && (y in leftTop.y..rightBottom.y)
}

private fun moveSand(
    sandPoint: Point, cellSet: MutableSet<Cell>,
    leftTop: Point,
    rightBottom: Point
) {
    var isStill = true
    var currentPoint = sandPoint
    while (isStill) {
        isStill = currentPoint.pointInRange(leftTop, rightBottom)
        when (cellSet.findFor(currentPoint.moveDown())) {
            is Cell.Rock, is Cell.Sand -> {
                val leftPoint = currentPoint.moveLeft()
                when (cellSet.findFor(leftPoint.moveDown())) {
                    is Cell.Rock, is Cell.Sand -> {
                        val rightPoint = currentPoint.moveRight()
                        when (cellSet.findFor(rightPoint.moveDown())) {
                            is Cell.Rock, is Cell.Sand -> isStill = false
                            is Cell.SandGenerator -> error("!!")
                            null -> currentPoint = rightPoint
                        }
                    }
                    is Cell.SandGenerator -> error("!!")
                    null -> currentPoint = leftPoint.moveDown()
                }
            }
            is Cell.SandGenerator -> error("!!")
            null -> currentPoint = currentPoint.moveDown()
        }
    }

    if (currentPoint.pointInRange(leftTop, rightBottom)) {
        cellSet.add(Cell.Sand(currentPoint))
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        val sendGeneratorPoint = Point(500, 0)
        val sendGenerator = Cell.SandGenerator(sendGeneratorPoint)

        val set = createSet(
            paths = input.map { parseLine(it) },
            generator = sendGenerator
        )
            .toMutableSet()

        val (leftTop, rightBottom) = set.rect()

        var currentSetSize: Int
        do {
            currentSetSize = set.size
            moveSand(sendGeneratorPoint, set, leftTop, rightBottom)
        } while (currentSetSize != set.size)

        return set.count { it is Cell.Sand }
    }

    fun part2(input: List<String>): Int {
        val sendGeneratorPoint = Point(500, 0)
        val sendGenerator = Cell.SandGenerator(sendGeneratorPoint)

        val set = createSet(
            paths = input.map { parseLine(it) },
            generator = sendGenerator
        )
            .withDownLine()
            .toMutableSet()

        val (leftTop, rightBottom) = set.rect()

        do {
            moveSand(sendGeneratorPoint, set, leftTop, rightBottom)
        } while (!set.contains(Cell.Sand(sendGeneratorPoint)))

        return set.count { it is Cell.Sand }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    val part1Test = part1(testInput)
    val part2Test = part2(testInput)

    check(part1Test == 24)
    check(part2Test == 93)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}
