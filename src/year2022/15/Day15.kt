package year2022.`15`

import kotlin.math.abs
import readInput

data class Point(
    val x: Int, val y: Int
)

data class SensorToBeacon(
    val sensor: Point, val beacon: Point
)

fun parseInput(input: List<String>): List<SensorToBeacon> {
    return input.map {
        val items = it.split("=", ",", ":").mapNotNull { it.toIntOrNull() }
        val sensor = Point(items[0], items[1])
        val beacon = Point(items[2], items[3])
        SensorToBeacon(sensor, beacon)
    }
}

private fun Point.distressSignal(): Long = x * 4000000L + y

private fun Point.calculateDistanceTo(point: Point): Int {
    val x = x - point.x
    val y = y - point.y
    return abs(x) + abs(y)
}

private fun List<Pair<Point, Point>>.iteratePointsRange(): Sequence<Point> = sequence {
    forEach { (left, right) ->
        (left.x..right.x).forEach { yield(Point(it, left.y)) }
    }
}.distinct()

private fun createSetOfIndexes(
    result: List<SensorToBeacon>, row: Int
): List<Pair<Point, Point>> {
    val mutableResult = mutableListOf<Pair<Point, Point>>()
    val minX = result.minOf { minOf(it.beacon.x, it.sensor.x) }
    val maxX = result.maxOf { maxOf(it.beacon.x, it.sensor.x) }

    (minX..maxX).forEach {
        val point = Point(it, row)
        result.forEach { (sensor, beacon) ->
            if (point.calculateDistanceTo(sensor) == sensor.calculateDistanceTo(beacon)) {
                val pair = findPairForCurrentPointY(sensor, beacon, point)
                mutableResult.add(pair)
            }
        }
    }

    return mutableResult
}

private fun iteratePointsAroundEachSensor(
    result: List<SensorToBeacon>, size: Int
): Sequence<Point> = sequence {
    val range = 0..size
    result.map { it.sensor to it.sensor.calculateDistanceTo(it.beacon) + 1 }
        .forEach { (sensor, distance) ->
            for (xDelta in -distance..distance) {
                val yDelta = distance - abs(xDelta)
                val res = listOf(
                    Point(sensor.x + xDelta, y = sensor.y + yDelta),
                    Point(sensor.x + xDelta, y = sensor.y - yDelta)
                ).filter { it.x in range && it.y in range }
                yieldAll(res)
            }
        }
}.distinct()

private fun findPairForCurrentPointY(
    sensor: Point, beacon: Point, point: Point
): Pair<Point, Point> {
    val distance = sensor.calculateDistanceTo(beacon)

    val rightPart = distance - abs(sensor.y - point.y)

    val firstX = sensor.x + rightPart
    val secondX = sensor.x - rightPart

    return Point(minOf(firstX, secondX), point.y) to Point(maxOf(firstX, secondX), point.y)
}

private fun isPointOutsideOfAllSensors(
    result: List<SensorToBeacon>, point: Point
) = result.all { (sensor, beacon) ->
    sensor.calculateDistanceTo(point) > sensor.calculateDistanceTo(beacon)
}

fun main() {

    fun part1(input: List<String>, row: Int): Int {
        val result = parseInput(input)
        val setOfBeacons = result.map { it.beacon }.toSet()
        val mutableResult = createSetOfIndexes(result, row)
        return mutableResult.iteratePointsRange().count { it !in setOfBeacons }
    }

    fun part2(input: List<String>, size: Int): Point {
        val result = parseInput(input)
        return iteratePointsAroundEachSensor(
            result = result,
            size = size
        ).first { point -> isPointOutsideOfAllSensors(result, point) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    val part1Test = part1(testInput, 10)
    val part2Test = part2(testInput, 20)

    check(part1Test == 26)
    check(part2Test.distressSignal() == 56000011L)

    val input = readInput("Day15")
    println(part1(input, 2000000))
    println(part2(input, 4_000_000).distressSignal())
}
