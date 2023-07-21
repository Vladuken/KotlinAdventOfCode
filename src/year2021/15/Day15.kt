package year2021.`15`

import readInput
import java.util.*

private data class Point(
    val x: Int,
    val y: Int,
)

private typealias PointsToValues = Map<Point, Int>
private typealias MutablePointsToValues = MutableMap<Point, Int>

private fun infinitePaths(initialPoint: Point, initialValues: PointsToValues): PointsToValues {
    return initialValues.mapValues {
        if (it.key == initialPoint) {
            0
        } else {
            Int.MAX_VALUE
        }
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

private fun multiplyWithRulesBy25(initialPoints: PointsToValues, width: Int): PointsToValues {
    val newPoints: MutablePointsToValues = mutableMapOf()

    initialPoints.keys.forEach {
        repeat(5) { i ->
            repeat(5) { j ->
                val sumIndex = i + j
                val newX = width * i + it.x
                val newY = width * j + it.y

                val res = (initialPoints.getValue(it) + sumIndex)
                val newRes = if (res > 9) {
                    res - 9
                } else {
                    res
                }
                newPoints[Point(newX, newY)] = newRes
            }
        }
    }

    return newPoints
}


fun main() {
    fun parse(input: List<String>): PointsToValues {
        val mutableMap = mutableMapOf<Point, Int>()
        input.forEachIndexed { y, line ->
            line.forEachIndexed { x, number ->
                mutableMap[Point(x, y)] = number.digitToInt()
            }
        }

        return mutableMap
    }

    fun deikstra(initialPoint: Point, weights: PointsToValues): PointsToValues {

        val currentMutableValuesHolder = infinitePaths(initialPoint, weights).toMutableMap()
        val priorityQueue = PriorityQueue(compareBy<Point> { currentMutableValuesHolder[it] })

        val visitedPoints = mutableSetOf<Point>()
        priorityQueue.add(initialPoint)
        while (priorityQueue.isNotEmpty()) {
            val currentElement = priorityQueue.poll()
            currentElement.neighbours()
                .filter { it in currentMutableValuesHolder.keys }
                .filter { it !in visitedPoints }
                .forEach { nextPoint ->
                    val distanceToNextPoint = currentMutableValuesHolder.getValue(nextPoint)
                    val distanceToCurrentPoint = currentMutableValuesHolder.getValue(currentElement)
                    val weightOfNextPoint = weights.getValue(nextPoint)

                    if (distanceToNextPoint > distanceToCurrentPoint + weightOfNextPoint) {
                        currentMutableValuesHolder[nextPoint] =
                            distanceToCurrentPoint + weightOfNextPoint
                        priorityQueue.add(nextPoint)
                    }
                }

            visitedPoints.add(currentElement)
        }

        return currentMutableValuesHolder
    }


    fun part1(input: List<String>): Int {
        val parsedValues = parse(input)
        val result = deikstra(Point(0, 0), parsedValues)
        val maxPoint = parsedValues.keys.maxBy { it.x + it.y }
        return result.getValue(maxPoint)
    }

    fun part2(input: List<String>): Int {
        val parsedValues = parse(input)
        val newRes = multiplyWithRulesBy25(parsedValues, parsedValues.keys.maxOf { it.x }.inc())
        val result = deikstra(Point(0, 0), newRes)
        val maxPoint = newRes.keys.maxBy { it.x + it.y }
        return result.getValue(maxPoint)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    val part1Test = part1(testInput)
    val part2Test = part2(testInput)

    println(part1Test)
    check(part1Test == 40)

    println(part2Test)
    check(part2Test == 315)

    val input = readInput("Day15")
    println(part1(input))
    println(part2(input))
}
