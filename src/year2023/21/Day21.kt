package year2023.`21`

import readInput
import kotlin.math.pow
import kotlin.math.roundToLong

private const val CURRENT_DAY = "21"

private data class Point(
    val x: Int,
    val y: Int,
) {
    override fun toString(): String {
        return "[$x,$y]"
    }
}

private fun Point.neighbours(): Set<Point> {
    return setOf(
        Point(x = x, y = y + 1),
        Point(x = x, y = y - 1),
        Point(x = x + 1, y = y),
        Point(x = x - 1, y = y),
    )
}

private fun parseMap(input: List<String>, initialPoint: Point): Map<Point, String> {
    val mutableMap = mutableMapOf<Point, String>()
    input.forEachIndexed { y, line ->
        line.split("")
            .filter { it.isNotBlank() }
            .forEachIndexed { x, value ->
                val currentPoint = Point(x, y)
                mutableMap[currentPoint] = if (currentPoint == initialPoint) {
                    "S"
                } else {
                    value.takeIf { it != "S" } ?: "."
                }
            }
    }
    return mutableMap
}

private fun processMap(
    initialMap: Map<Point, String>,
    setOfCurrentPoints: Set<Point>,
): Map<Point, String> {
    val newPoints = setOfCurrentPoints.flatMap { point ->
        point.neighbours()
    }
        .filter { it in initialMap.keys }
        .filter { initialMap[it] != "#" }

    val mutableMap = mutableMapOf<Point, String>()
    initialMap.forEach { (point, value) ->
        when (value) {
            "#" -> mutableMap[point] = "#"
            "S", "." -> mutableMap[point] = "O".takeIf { point in newPoints } ?: "."
            "O" -> mutableMap[point] = "O".takeIf { mutableMap[point] == "O" } ?: "."
            else -> error("Illegal Point: $point Value:$value")
        }
    }
    return mutableMap
}

fun main() {

    fun part1(
        input: List<String>,
        repeatCount: Int = 64,
        sPoint: Point = Point(input.size / 2, input.size / 2)
    ): Int {
        val map = parseMap(input, sPoint)

        var currentMap = map
        repeat(repeatCount) {
            currentMap = processMap(
                initialMap = currentMap,
                setOfCurrentPoints = currentMap.keys.filter { currentMap[it] in listOf("O", "S") }
                    .toSet(),
            )
        }
        return currentMap.count { it.value == "O" }
    }

    fun part2(input: List<String>): Long {
        val useCachedValue = true

        assert(input.size == input[0].length)

        val size = input.size

        val startX = size / 2
        val startY = size / 2

        val steps = 26501365

        println("size:$size")
        assert(steps % size == size / 2)

        val gridWidth = steps.floorDiv(size) - 1
        println("gridWidth:$gridWidth")

        val odd = (gridWidth.floorDiv(2) * 2 + 1).toDouble().pow(2).roundToLong()
        val even = (gridWidth.inc().floorDiv(2) * 2).toDouble().pow(2).roundToLong()
        println("odd : $odd")
        println("even : $even")

        val oddPoints = if (useCachedValue) 7496 else part1(input, size * 2 + 1)
        val evenPoints = if (useCachedValue) 7570 else part1(input, size * 2)

        println()
        println("oddPoints : $oddPoints")
        println("evenPoints : $evenPoints")


        val edgeTop = if (useCachedValue) 5670 else part1(
            input = input,
            repeatCount = size - 1,
            sPoint = Point(startX, size - 1),
        )
        val edgeRight = if (useCachedValue) 5637 else part1(
            input = input,
            repeatCount = size - 1,
            sPoint = Point(0, startY),
        )
        val edgeBottom = if (useCachedValue) 5623 else part1(
            input = input,
            repeatCount = size - 1,
            sPoint = Point(startX, 0),
        )
        val edgeLeft = if (useCachedValue) 5656 else part1(
            input = input,
            repeatCount = size - 1,
            sPoint = Point(size - 1, startY),
        )
        println()
        println("edgeTop: $edgeTop")
        println("edgeRight: $edgeRight")
        println("edgeBottom: $edgeBottom")
        println("edgeLeft: $edgeLeft")

        val smallTopRight = if (useCachedValue) 980 else part1(
            input = input,
            repeatCount = size.floorDiv(2) - 1,
            sPoint = Point(0, size - 1),
        )
        val smallTopLeft = if (useCachedValue) 965 else part1(
            input = input,
            repeatCount = size.floorDiv(2) - 1,
            sPoint = Point(size - 1, size - 1),
        )
        val smallBottomRight = if (useCachedValue) 963 else part1(
            input = input,
            repeatCount = size.floorDiv(2) - 1,
            sPoint = Point(0, 0),
        )
        val smallBottomLeft = if (useCachedValue) 945 else part1(
            input = input,
            repeatCount = size.floorDiv(2) - 1,
            sPoint = Point(size - 1, 0),
        )

        val totalSmallCount: Long = (gridWidth + 1L) *
                (smallBottomRight + smallBottomLeft + smallTopRight + smallTopLeft)

        println()
        println("smallTopRight: $smallTopRight")
        println("smallTopLeft: $smallTopLeft")
        println("smallBottomRight: $smallBottomRight")
        println("smallBottomLeft: $smallBottomLeft")
        println()
        println("totalSmallCount: $totalSmallCount")


        val largeTopRight = if (useCachedValue) 6584 else part1(
            input = input,
            repeatCount = size.times(3).floorDiv(2) - 1,
            sPoint = Point(0, size - 1),
        )
        val largeTopLeft = if (useCachedValue) 6582 else part1(
            input = input,
            repeatCount = size.times(3).floorDiv(2) - 1,
            sPoint = Point(size - 1, size - 1),
        )
        val largeBottomRight = if (useCachedValue) 6549 else part1(
            input = input,
            repeatCount = size.times(3).floorDiv(2) - 1,
            sPoint = Point(0, 0),
        )
        val largeBottomLeft = if (useCachedValue) 6570 else part1(
            input = input,
            repeatCount = size.times(3).floorDiv(2) - 1,
            sPoint = Point(size - 1, 0),
        )
        val totalLargeCount = (gridWidth.toLong()) *
                (largeBottomRight + largeBottomLeft + largeTopRight + largeTopLeft)


        println()
        println("largeTopRight: $largeTopRight")
        println("largeTopLeft: $largeTopLeft")
        println("largeBottomRight: $largeBottomRight")
        println("largeBottomLeft: $largeBottomLeft")
        println()

        val countTotal = odd * oddPoints +
                even * evenPoints +
                edgeTop + edgeBottom + edgeLeft + edgeRight +
                totalSmallCount +
                totalLargeCount

        println("Total Count: $countTotal")
        return countTotal
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 42)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 3716)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 1L)
}
