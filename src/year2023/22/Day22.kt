package year2023.`22`

import readInput
import kotlin.math.max
import kotlin.math.min

private const val CURRENT_DAY = "22"

data class Point(
    val x: Int,
    val y: Int,
    val z: Int,
) {
    override fun toString(): String = "[$x,$y,$z]"
}

private val cache = mutableMapOf<Brick, Set<Point>>()

data class Brick(
    val start: Point,
    val end: Point,
) {
    override fun toString(): String = "[start=$start,end=$end]"
    val maxX = max(start.x, end.x)
    val maxY = max(start.y, end.y)
    private val maxZ = max(start.z, end.z)

    val minX = min(start.x, end.x)
    val minY = min(start.y, end.y)
    private val minZ = min(start.z, end.z)


    fun setOfPoints(): Set<Point> {
        val cachedValue = cache[this]
        if (cachedValue != null) return cachedValue

        val result = when {
            start.x != end.x -> {
                val range = minX..maxX
                assert(start.y == end.y)
                assert(start.z == end.z)
                range
                    .map { start.copy(x = it) }
                    .toSet()
            }

            start.y != end.y -> {
                val range = minY..maxY
                assert(start.x == end.x)
                assert(start.z == end.z)
                range
                    .map { start.copy(y = it) }
                    .toSet()
            }

            start.z != end.z -> {
                val range = minZ..maxZ
                assert(start.x == end.x)
                assert(start.y == end.y)
                range
                    .map { start.copy(z = it) }
                    .toSet()
            }

            start == end -> {
                setOf(start)
            }

            else -> error("ILLEGAL STATE $this")
        }
        cache[this] = result
        return result
    }

    fun dropN(n: Int): Brick {
        return copy(
            start = start.copy(z = start.z - n),
            end = end.copy(z = end.z - n),
        )
    }
}

private fun parseLineInto(
    line: String
): Brick {
    val (first, second) = line.split("~").filter { it.isNotBlank() }
    val (x1, y1, z1) = first.split(",").filter { it.isNotBlank() }.map { it.toInt() }
    val (x2, y2, z2) = second.split(",").filter { it.isNotBlank() }.map { it.toInt() }
    return Brick(
        start = Point(x1, y1, z1),
        end = Point(x2, y2, z2),
    )
}

fun dropAllUntilItFalls(
    bricks: Set<Brick>,
): Pair<Set<Brick>, Int> {
    var currentDroppedBricks = emptySet<Brick>()
    var droppedCount = 0
    val flatPanel = prepareFlatPanel(bricks)
    bricks.forEachIndexed { _, currentBrick ->
        val (newBricks, wasDropped) = dropTillTheEnd(
            flatGrid = flatPanel,
            droppedBricks = currentDroppedBricks,
            currentBrick = currentBrick,
        )
        currentDroppedBricks = newBricks
        if (wasDropped) droppedCount++
    }
    return currentDroppedBricks to droppedCount
}

fun dropTillTheEnd(
    flatGrid: Set<Point>,
    droppedBricks: Set<Brick>,
    currentBrick: Brick,
): Pair<Set<Brick>, Boolean> {
    val droppedBricksPoints = droppedBricks
        .flatMap { it.setOfPoints() }

    val allPointsHere: Set<Point> = flatGrid + droppedBricksPoints

    val res = currentBrick.setOfPoints()
        .minOf { point ->
            val maxZPoint = allPointsHere
                .filter { point.x == it.x && point.y == it.y }
                .maxBy { it.z }
            val something = point.z - maxZPoint.z - 1
            something
        }
    return (droppedBricks + currentBrick.dropN(res)) to (res != 0)
}

private fun prepareFlatPanel(bricks: Set<Brick>): Set<Point> {
    val maxX = bricks.maxOf { it.maxX }
    val minX = bricks.minOf { it.minX }
    val maxY = bricks.maxOf { it.maxY }
    val minY = bricks.minOf { it.minY }

    val res = mutableSetOf<Point>()
    (minX..maxX).forEach { x ->
        (minY..maxY).forEach { y ->
            res.add(Point(x, y, 0))
        }
    }
    return res
}

private fun returnBricksThatAreSaveToDelete(initialBricks: Set<Brick>): Map<Brick, Int> {
    val setToReturn = mutableMapOf<Brick, Int>()
    println("returnBricksThatAreSaveToDelete size${initialBricks.size}")
    initialBricks.forEachIndexed { index, brick ->
        println("returnBricksThatAreSaveToDelete index $index")
        val bricksWithoutIt = initialBricks - brick
        val (_, count) = dropAllUntilItFalls(bricksWithoutIt)
        setToReturn[brick] = count
    }

    return setToReturn
}

fun main() {

    fun part1(input: List<String>): Int {
        val bricks = input.map {
            parseLineInto(it)
        }
            .sortedBy { minOf(it.end.z, it.start.z) }
            .onEach { println(it) }
            .toSet()
        val resBricks = dropAllUntilItFalls(bricks).first
        val resMap = returnBricksThatAreSaveToDelete(resBricks)
        return resMap.filter { it.value == 0 }.size
    }

    fun part2(input: List<String>): Int {
        val bricks = input.map { parseLineInto(it) }
            .sortedBy { minOf(it.end.z, it.start.z) }
            .onEach { println(it) }
        val resBricks = dropAllUntilItFalls(bricks.toSet())
            .first
            .onEach { println(it) }

        val res = returnBricksThatAreSaveToDelete(resBricks)
        return res.values.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 5)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 7)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 507)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 51733)
}
