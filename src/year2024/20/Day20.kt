package year2024.`20`

import readInput
import utils.findShortestPathByPredicate
import kotlin.math.abs

private const val CURRENT_DAY = "20"

private data class Point(
    val x: Int,
    val y: Int,
) {
    fun moveUp(): Point = copy(y = y - 1)
    fun moveDown(): Point = copy(y = y + 1)
    fun moveLeft(): Point = copy(x = x - 1)
    fun moveRight(): Point = copy(x = x + 1)

    override fun toString(): String {
        return "[$x,$y]"
    }
}


private sealed class BlockType {
    abstract val position: Point

    data class Start(
        override val position: Point,
    ) : BlockType() {
        override fun toString(): String = "S"
    }

    data class End(
        override val position: Point,
    ) : BlockType() {
        override fun toString(): String = "E"
    }

    data class Wall(
        override val position: Point,
    ) : BlockType() {
        override fun toString(): String = "#"
    }

    data class Empty(
        override val position: Point,
    ) : BlockType() {
        override fun toString(): String = "."
    }

}

private fun parseMap(input: List<String>): Map<Point, BlockType> {
    val mutableMap = mutableMapOf<Point, BlockType>()
    input.forEachIndexed { y, line ->
        line.split("")
            .filter { it.isNotBlank() }
            .forEachIndexed { x, value ->
                val point = Point(x, y)
                val result = when (value) {
                    "#" -> BlockType.Wall(point)
                    "." -> BlockType.Empty(point)
                    "S" -> BlockType.Start(point)
                    "E" -> BlockType.End(point)
                    else -> error("AAAA $value")
                }
                mutableMap[point] = result
            }
    }
    return mutableMap
}

private data class StepWithScore(
    val position: Point,
)

private fun StepWithScore.calculateNextSteps(
    initialMap: Map<Point, BlockType>,
    visited: Set<Point>,
    minX: Int,
    minY: Int,
    maxX: Int,
    maxY: Int,
): List<StepWithScore> {


    return listOfNotNull(
        StepWithScore(
            position.moveUp(),
        ).takeIf { initialMap[it.position] !is BlockType.Wall },
        StepWithScore(
            position.moveDown(),
        ).takeIf { initialMap[it.position] !is BlockType.Wall },
        StepWithScore(
            position.moveLeft(),
        ).takeIf { initialMap[it.position] !is BlockType.Wall },
        StepWithScore(
            position.moveRight(),
        ).takeIf { initialMap[it.position] !is BlockType.Wall },
    ).filter {
        it.position.x in minX..maxX &&
                it.position.y >= minY && it.position.y <= maxY
    }
        .filter {
            it.position !in visited
        }
}


private fun pathSearch(
    initialMap: Map<Point, BlockType>,
    currentRaindeerPosition: Point,
    topPosition: Point,
): Pair<Long, List<StepWithScore>> {
    val minX = initialMap.keys.minOf { it.x }
    val minY = initialMap.keys.minOf { it.y }
    val maxX = initialMap.keys.maxOf { it.x }
    val maxY = initialMap.keys.maxOf { it.y }

    val path = findShortestPathByPredicate(
        start = StepWithScore(
            position = currentRaindeerPosition,
        ),
        endFunction = { (p) -> p == topPosition },
        neighbours = {
            it.calculateNextSteps(
                initialMap = initialMap,
                visited = emptySet(),
                minX = minX,
                minY = minY,
                maxX = maxX,
                maxY = maxY,
            )
        },
        cost = { _, _ -> 1 },
        heuristic = { (it.position.x + it.position.y) },
    )
    return path.getScore().toLong() to path.getPath()
}


private fun findSavedDistance(steps: List<Point>, cheatSize: Int): Long {

    var count = 0L
    for (i1 in steps.indices) {
        for (i2 in i1..steps.lastIndex) {
            val p1 = steps[i1]
            val p2 = steps[i2]
            val dist = p1.manhattanDistance(p2)
            if (dist > cheatSize) continue

            val totalSaveTime = i2 - i1 - dist
            if (totalSaveTime >= 100) {
                count++
            }
        }
    }

    return count
}

private fun Point.manhattanDistance(point: Point): Int {
    return abs(point.x - x) + abs(point.y - y)
}

fun main() {

    fun part1(input: List<String>): Long {
        val map = parseMap(input)
        val position = map.keys.find { map[it] is BlockType.Start }!!
        val topPos = map.keys.find { map[it] is BlockType.End }!!
        val (_, path) = pathSearch(map, position, topPos)
        return findSavedDistance(path.map { it.position }, 2)
    }

    fun part2(input: List<String>): Long {
        val map = parseMap(input)
        val position = map.keys.find { map[it] is BlockType.Start }!!
        val topPos = map.keys.find { map[it] is BlockType.End }!!
        val (_, path) = pathSearch(map, position, topPos)
        return findSavedDistance(path.map { it.position }, 20)
    }

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 1321L)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 971737L)
}
