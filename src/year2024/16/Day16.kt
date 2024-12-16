package year2024.`16`

import readInput
import java.util.PriorityQueue

private const val CURRENT_DAY = "16"

private enum class Direction {
    LEFT, RIGHT, DOWN, TOP;
}

private fun Direction.rotateClockWise(): Direction {
    return when (this) {
        Direction.LEFT -> Direction.TOP
        Direction.RIGHT -> Direction.DOWN
        Direction.DOWN -> Direction.LEFT
        Direction.TOP -> Direction.RIGHT
    }
}

private fun Direction.rotateCounterClockWise(): Direction {
    return when (this) {
        Direction.LEFT -> Direction.DOWN
        Direction.RIGHT -> Direction.TOP
        Direction.DOWN -> Direction.RIGHT
        Direction.TOP -> Direction.LEFT
    }
}


private data class Point(
    val x: Int,
    val y: Int,
) {
    fun moveUp(): Point = copy(y = y - 1)
    fun moveDown(): Point = copy(y = y + 1)
    fun moveLeft(): Point = copy(x = x - 1)
    fun moveRight(): Point = copy(x = x + 1)

    fun moveWithDirection(direction: Direction): Point {
        return when (direction) {
            Direction.LEFT -> moveLeft()
            Direction.RIGHT -> moveRight()
            Direction.DOWN -> moveDown()
            Direction.TOP -> moveUp()
        }
    }

    fun moveWithOppositeDirection(direction: Direction): Point {
        return when (direction) {
            Direction.LEFT -> moveRight()
            Direction.RIGHT -> moveLeft()
            Direction.DOWN -> moveUp()
            Direction.TOP -> moveDown()
        }
    }

    override fun toString(): String {
        return "[$x,$y]"
    }
}


private sealed class BlockType {
    abstract val position: Point

    data class RainDeer(
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
                    "S" -> BlockType.RainDeer(point)
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
    val score: Long,
    val direction: Direction,
    val prevPath: Set<Point> = setOf(position),
) {
    fun toCacheWithValue(): CacheWithValue {
        return CacheWithValue(
            position,
            direction,
        )
    }
}

private data class CacheWithValue(
    val position: Point,
    val direction: Direction,
)

private fun StepWithScore.calculateNextSteps(
    initialMap: Map<Point, BlockType>,
): List<StepWithScore> {
    return listOfNotNull(
        StepWithScore(
            this.position.moveWithDirection(direction),
            score = score + 1,
            direction = direction,
            prevPath = prevPath + position.moveWithDirection(direction),
        ).takeIf { initialMap[it.position] !is BlockType.Wall },
        StepWithScore(
            position = position,
            score = score + 1000,
            direction = direction.rotateClockWise(),
            prevPath = prevPath
        ),
        StepWithScore(
            position = position,
            score = score + 1000,
            direction = direction.rotateCounterClockWise(),
            prevPath = prevPath
        ),
    )
}

private fun dfsSearch(
    initialMap: Map<Point, BlockType>,
    currentRaindeerPosition: Point,
    topPosition: Point,
): Pair<Long, Long> {

    val queue = PriorityQueue<StepWithScore>(compareBy<StepWithScore> { it.score })
    queue.add(
        StepWithScore(
            position = currentRaindeerPosition,
            score = 0,
            direction = Direction.RIGHT,
        )
    )

    val cache = mutableMapOf<CacheWithValue, Long>()
    var min = Long.MAX_VALUE
    val best = mutableSetOf<Point>()
    val endingSteps = mutableSetOf<StepWithScore>()
    while (queue.isNotEmpty()) {
        val currentStep = queue.poll()
        val cacheWithValue = currentStep.toCacheWithValue()

        if (initialMap[currentStep.position] is BlockType.End) {
            if (currentStep.score <= min) {
                min = currentStep.score
                best.addAll(currentStep.prevPath)
            }
            endingSteps.add(currentStep)
            continue
        }

        if (cacheWithValue in cache) {
            if (cache[cacheWithValue]!! < currentStep.score) continue
        }
        cache[cacheWithValue] = currentStep.score
        queue.addAll(
            currentStep.calculateNextSteps(initialMap)
        )
    }
    return endingSteps.minBy { it.score }.score to best.size.toLong()
}

fun main() {

    fun part1(input: List<String>): Long {
        val map = parseMap(input)
        val position = map.keys.find { map[it] is BlockType.RainDeer }!!
        val topPos = map.keys.find { map[it] is BlockType.End }!!
        return dfsSearch(map, position, topPos).first
    }

    fun part2(input: List<String>): Long {
        val map = parseMap(input)
        val position = map.keys.find { map[it] is BlockType.RainDeer }!!
        val topPos = map.keys.find { map[it] is BlockType.End }!!

        return dfsSearch(map, position, topPos).second
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 7036L)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 45L)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 99448L)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 498L)
}
