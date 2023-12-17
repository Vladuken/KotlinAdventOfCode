package year2023.`17`

import readInput
import utils.findShortestPathByPredicate

private const val CURRENT_DAY = "17"

private typealias PointsToValues = Map<Point, Int>

private data class Point(
    val x: Int,
    val y: Int,
) {
    fun moveTop(): Point = copy(y = y - 1)
    fun moveRight(): Point = copy(x = x + 1)
    fun moveBottom(): Point = copy(y = y + 1)
    fun moveLeft(): Point = copy(x = x - 1)


    override fun toString(): String {
        return "[$x,$y]"
    }
}

private fun parseMap(input: List<String>): Map<Point, Int> {
    val mutableMap = mutableMapOf<Point, Int>()
    input.forEachIndexed { y, line ->
        line.split("")
            .filter { it.isNotBlank() }
            .forEachIndexed { x, value ->
                mutableMap[Point(x, y)] = value.toInt()
            }
    }
    return mutableMap
}

enum class Direction {
    BOTTOM,
    TOP,
    LEFT,
    RIGHT;

    fun rotateRight(): Direction {
        return when (this) {
            BOTTOM -> LEFT
            LEFT -> TOP
            TOP -> RIGHT
            RIGHT -> BOTTOM
        }
    }

    fun rotateLeft(): Direction {
        return when (this) {
            BOTTOM -> RIGHT
            RIGHT -> TOP
            TOP -> LEFT
            LEFT -> BOTTOM
        }
    }

    override fun toString(): String {
        return when (this) {
            BOTTOM -> "B"
            TOP -> "T"
            LEFT -> "L"
            RIGHT -> "R"
        }
    }
}

private data class CurrentStep(
    val currentPoint: Point,
    val currentDirection: Direction,
    val doneSteps: Int,
) {
    fun allNextSteps(): Set<CurrentStep> {
        val maxStepCount = 3
        val dir: Set<CurrentStep> = when (currentDirection) {
            Direction.BOTTOM -> setOfNotNull(
                copy(
                    currentPoint = currentPoint.moveBottom(),
                    doneSteps = doneSteps + 1,
                ).takeIf { it.doneSteps <= maxStepCount },
                copy(
                    currentPoint = currentPoint.moveLeft(),
                    currentDirection = Direction.LEFT,
                    doneSteps = 1,
                ),
                copy(
                    currentPoint = currentPoint.moveRight(),
                    currentDirection = Direction.RIGHT,
                    doneSteps = 1,
                ),
            )

            Direction.TOP -> setOfNotNull(
                copy(
                    currentPoint = currentPoint.moveTop(),
                    doneSteps = doneSteps + 1,
                ).takeIf { it.doneSteps <= maxStepCount },
                copy(
                    currentPoint = currentPoint.moveLeft(),
                    currentDirection = Direction.LEFT,
                    doneSteps = 1,
                ),
                copy(
                    currentPoint = currentPoint.moveRight(),
                    currentDirection = Direction.RIGHT,
                    doneSteps = 1,
                ),
            )

            Direction.LEFT -> setOfNotNull(
                copy(
                    currentPoint = currentPoint.moveLeft(),
                    doneSteps = doneSteps + 1,
                ).takeIf { it.doneSteps <= maxStepCount },
                copy(
                    currentPoint = currentPoint.moveTop(),
                    currentDirection = Direction.TOP,
                    doneSteps = 1,
                ),
                copy(
                    currentPoint = currentPoint.moveBottom(),
                    currentDirection = Direction.BOTTOM,
                    doneSteps = 1,
                ),
            )

            Direction.RIGHT -> setOfNotNull(
                copy(
                    currentPoint = currentPoint.moveRight(),
                    doneSteps = doneSteps + 1,
                ).takeIf { it.doneSteps <= maxStepCount },
                copy(
                    currentPoint = currentPoint.moveTop(),
                    currentDirection = Direction.TOP,
                    doneSteps = 1,
                ),
                copy(
                    currentPoint = currentPoint.moveBottom(),
                    currentDirection = Direction.BOTTOM,
                    doneSteps = 1,
                ),
            )
        }

        return dir
    }

    fun allNextUltraSteps(): Set<CurrentStep> {
        val minStepCount = 4
        val maxStepCount = 10

        fun CurrentStep.takeIfNeeded(): CurrentStep? {
            return takeIf { this@CurrentStep.doneSteps >= minStepCount }
        }

        val dir: Set<CurrentStep> = when (currentDirection) {
            Direction.BOTTOM -> setOfNotNull(
                copy(
                    currentPoint = currentPoint.moveBottom(),
                    doneSteps = doneSteps + 1,
                ).takeIf { it.doneSteps <= maxStepCount },
                copy(
                    currentPoint = currentPoint.moveLeft(),
                    currentDirection = Direction.LEFT,
                    doneSteps = 1,
                ).takeIfNeeded(),
                copy(
                    currentPoint = currentPoint.moveRight(),
                    currentDirection = Direction.RIGHT,
                    doneSteps = 1,
                ).takeIfNeeded(),
            )

            Direction.TOP -> setOfNotNull(
                copy(
                    currentPoint = currentPoint.moveTop(),
                    doneSteps = doneSteps + 1,
                ).takeIf { it.doneSteps <= maxStepCount },
                copy(
                    currentPoint = currentPoint.moveLeft(),
                    currentDirection = Direction.LEFT,
                    doneSteps = 1,
                ).takeIfNeeded(),
                copy(
                    currentPoint = currentPoint.moveRight(),
                    currentDirection = Direction.RIGHT,
                    doneSteps = 1,
                ).takeIfNeeded(),
            )

            Direction.LEFT -> setOfNotNull(
                copy(
                    currentPoint = currentPoint.moveLeft(),
                    doneSteps = doneSteps + 1,
                ).takeIf { it.doneSteps <= maxStepCount },
                copy(
                    currentPoint = currentPoint.moveTop(),
                    currentDirection = Direction.TOP,
                    doneSteps = 1,
                ).takeIfNeeded(),
                copy(
                    currentPoint = currentPoint.moveBottom(),
                    currentDirection = Direction.BOTTOM,
                    doneSteps = 1,
                ).takeIfNeeded(),
            )

            Direction.RIGHT -> setOfNotNull(
                copy(
                    currentPoint = currentPoint.moveRight(),
                    doneSteps = doneSteps + 1,
                ).takeIf { it.doneSteps <= maxStepCount },
                copy(
                    currentPoint = currentPoint.moveTop(),
                    currentDirection = Direction.TOP,
                    doneSteps = 1,
                ).takeIfNeeded(),
                copy(
                    currentPoint = currentPoint.moveBottom(),
                    currentDirection = Direction.BOTTOM,
                    doneSteps = 1,
                ).takeIfNeeded(),
            )
        }

        return dir
    }
}

private fun infinitePaths(
    initialPoint: Point,
    initialValues: PointsToValues
): PointsToValues {
    return initialValues.mapValues {
        if (it.key == initialPoint) {
            0
        } else {
            Int.MAX_VALUE
        }
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        val map = parseMap(input)
        val maxX = map.maxOf { it.key.x }
        val maxY = map.maxOf { it.key.y }
        val res = findShortestPathByPredicate(
            start = CurrentStep(
                Point(0, 0),
                Direction.RIGHT,
                0,
            ),
            endFunction = { (p, _) -> p == Point(maxX, maxY) },
            neighbours = { it.allNextSteps().filter { (n) -> n in map } },
            cost = { _, (point) -> map[point]!! }
        )
        return minOf(res.getScore())
    }

    fun part2(input: List<String>): Int {
        val map = parseMap(input)

        val maxX = map.maxOf { it.key.x }
        val maxY = map.maxOf { it.key.y }

        val rightDirRes = findShortestPathByPredicate(
            start = CurrentStep(
                currentPoint = Point(0, 0),
                currentDirection = Direction.RIGHT,
                doneSteps = 0,
            ),
            endFunction = { (p, _, steps) -> p == Point(maxX, maxY) && steps >= 4 },
            neighbours = { it.allNextUltraSteps().filter { (n) -> n in map } },
            cost = { _, (point) -> map[point]!! }
        ).getScore()

        val bottomDirRes = runCatching {
            val score = findShortestPathByPredicate(
                start = CurrentStep(
                    currentPoint = Point(0, 0),
                    currentDirection = Direction.BOTTOM,
                    doneSteps = 0,
                ),
                endFunction = { (p, _, steps) -> p == Point(maxX, maxY) && steps >= 4 },
                neighbours = { it.allNextUltraSteps().filter { (n) -> n in map } },
                cost = { _, (point) -> map[point]!! }
            ).getScore()
            score
        }.getOrNull() ?: Int.MAX_VALUE

        return minOf(rightDirRes, bottomDirRes)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 102)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 94)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 1246)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 1389)
}
