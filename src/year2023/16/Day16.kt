package year2023.`16`

import readInput
import utils.printDebug
import utils.printlnDebug

private const val CURRENT_DAY = "16"


private data class Point(
    val x: Int,
    val y: Int,
) {
    val left get() = Point(x - 1, y)
    val right get() = Point(x + 1, y)
    val top get() = Point(x, y - 1)
    val bottom get() = Point(x, y + 1)
    override fun toString(): String = "[$x,$y]"
}


enum class PointConfig(val value: String) {
    Vertical("|"),
    Horizontal("-"),
    Angle1("/"),
    Angle2("\\"),
    EmptySpace(".");

    override fun toString(): String = value;
}

private fun String.toPointConfig(): PointConfig {
    return when (this) {
        "|" -> PointConfig.Vertical
        "-" -> PointConfig.Horizontal
        "/" -> PointConfig.Angle1
        "\\" -> PointConfig.Angle2
        "." -> PointConfig.EmptySpace
        else -> error("ILLEGAL PIPE $this")
    }
}

private fun parseMap(input: List<String>): Map<Point, PointConfig> {
    val mutableMap = mutableMapOf<Point, PointConfig>()
    input.forEachIndexed { y, line ->
        line.split("")
            .filter { it.isNotBlank() }
            .forEachIndexed { x, value ->
                mutableMap[Point(x, y)] = value.toPointConfig()
            }
    }
    return mutableMap
}

enum class Direction {
    BOTTOM,
    TOP,
    LEFT,
    RIGHT;

    override fun toString(): String {
        return when (this) {
            BOTTOM -> "B"
            TOP -> "T"
            LEFT -> "L"
            RIGHT -> "R"
        }
    }
}

private data class PointWithDirection(
    val point: Point,
    val direction: Direction
)

private fun Direction.calculateNextPoint(currentPoint: Point): Point {
    return when (this) {
        Direction.LEFT -> currentPoint.left
        Direction.RIGHT -> currentPoint.right
        Direction.TOP -> currentPoint.top
        Direction.BOTTOM -> currentPoint.bottom
    }
}

private fun printMap(
    initialMap: Map<Point, PointConfig>,
    visitedMaps: Set<PointWithDirection>,
) {
    val number = initialMap.maxOf { it.key.x + 1 }
    val sortedPoints = initialMap.keys.toList()
        .sortedWith(compareBy({ it.y }, { it.x }))

    sortedPoints.forEachIndexed { index, point ->
        if (index % number == 0) {
            printlnDebug { "" }
        }
        printDebug {
            if (visitedMaps.any { it.point == point }) {
                "#"
            } else {
                "."
            }
        }

    }
    printlnDebug { "" }
}


private fun newPointWithDirection(p: Point, newDir: Direction): PointWithDirection {
    val newP = newDir.calculateNextPoint(p)
    return PointWithDirection(newP, newDir)
}

private fun processStartPoint(
    startPoint: Point,
    startDirection: Direction,
    configuration: Map<Point, PointConfig>,
): Int {
    val visitedPointsAndDirections = mutableSetOf<PointWithDirection>()
    val queue = ArrayDeque(listOf(PointWithDirection(startPoint, startDirection)))
    while (queue.isNotEmpty()) {
        val currentPoint = queue.removeFirst()

        val p = currentPoint.point
        val dir = currentPoint.direction

        val currentConfig = configuration[p] ?: continue
        if (visitedPointsAndDirections.contains(currentPoint)) continue
        visitedPointsAndDirections.add(currentPoint)

        when (currentConfig) {
            PointConfig.Vertical -> {
                when (dir) {
                    Direction.LEFT, Direction.RIGHT -> {
                        queue.add(newPointWithDirection(p, Direction.BOTTOM))
                        queue.add(newPointWithDirection(p, Direction.TOP))
                    }

                    Direction.TOP, Direction.BOTTOM -> queue.add(newPointWithDirection(p, dir))
                }
            }

            PointConfig.Horizontal -> when (dir) {
                Direction.LEFT, Direction.RIGHT -> queue.add(newPointWithDirection(p, dir))
                Direction.TOP, Direction.BOTTOM -> {
                    queue.add(newPointWithDirection(p, Direction.LEFT))
                    queue.add(newPointWithDirection(p, Direction.RIGHT))
                }
            }

            PointConfig.Angle1 -> when (dir) {
                Direction.LEFT -> queue.add(newPointWithDirection(p, Direction.BOTTOM))
                Direction.RIGHT -> queue.add(newPointWithDirection(p, Direction.TOP))
                Direction.TOP -> queue.add(newPointWithDirection(p, Direction.RIGHT))
                Direction.BOTTOM -> queue.add(newPointWithDirection(p, Direction.LEFT))
            }

            PointConfig.Angle2 -> when (dir) {
                Direction.LEFT -> queue.add(newPointWithDirection(p, Direction.TOP))
                Direction.RIGHT -> queue.add(newPointWithDirection(p, Direction.BOTTOM))
                Direction.TOP -> queue.add(newPointWithDirection(p, Direction.LEFT))
                Direction.BOTTOM -> queue.add(newPointWithDirection(p, Direction.RIGHT))
            }

            PointConfig.EmptySpace -> queue.add(newPointWithDirection(p, dir))
        }
    }

//    printMap(
//        initialMap = configuration,
//        visitedMaps = visitedPointsAndDirections,
//    )

    return visitedPointsAndDirections.distinctBy { it.point }.size
}

fun main() {

    fun part1(input: List<String>): Int {
        val map = parseMap(input)
        return processStartPoint(
            startPoint = Point(0, 0),
            startDirection = Direction.RIGHT,
            configuration = map,
        )
    }

    fun part2(input: List<String>): Int {
        val map = parseMap(input)

        val allVariants = map.keys.flatMap {
            listOf(
                PointWithDirection(it, Direction.RIGHT),
                PointWithDirection(it, Direction.LEFT),
                PointWithDirection(it, Direction.TOP),
                PointWithDirection(it, Direction.BOTTOM),
            )
        }
        var counter = 0
        return allVariants
            .maxOf {
                counter++
                if (counter % 500 == 0) {
                    printlnDebug { "Processed ${counter.toFloat() / allVariants.size * 100}%" }
                }
                processStartPoint(
                    startPoint = it.point,
                    startDirection = it.direction,
                    configuration = map,
                )
            }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 46)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 51)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 7496)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 7932)
}
