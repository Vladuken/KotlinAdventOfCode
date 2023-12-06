package year2023.`06`

import readInput
import utils.printlnDebug

private const val CURRENT_DAY = "06"


data class Race(
    val time: Long,
    val distance: Long,
)


data class Combination(
    val initTime: Long,
    val leftTime: Long,
    val travelDistance: Long,
)

private fun parseInputIntoRaces(input: List<String>): List<Race> {
    val times = input.first().split(" ").mapNotNull { it.toLongOrNull() }
    val distances = input[1].split(" ").mapNotNull { it.toLongOrNull() }

    return times.mapIndexed { index, time ->
        Race(time, distances[index])
    }
}

private fun findAllCombinations(race: Race): List<Combination> {
    val result = mutableListOf<Combination>()
    var currentTime = 0L
    while (currentTime != race.time) {
        val initSpeed = calculateSpeedForNumber(currentTime)
        val remainingTime = race.time - currentTime
        val travelDistance = calculateDistanceFor(initSpeed, remainingTime)

        result += Combination(
            initSpeed,
            remainingTime,
            travelDistance
        )
        currentTime++
    }

    return result
}

private fun calculateSpeedForNumber(num: Long): Long = num

private fun calculateDistanceFor(
    initTime: Long,
    leftTime: Long,
): Long = initTime * leftTime

fun main() {

    fun part1(input: List<String>): Int {
        val races = parseInputIntoRaces(input)
            .map { race -> findAllCombinations(race).count { it.travelDistance > race.distance } }

        return races.fold(1) { acc, raceCount -> acc * raceCount }
    }

    fun part2(input: List<String>): Int {
        val racesTime = parseInputIntoRaces(input).joinToString("") { it.time.toString() }
        val racesDistance = parseInputIntoRaces(input).joinToString("") { it.distance.toString() }
        printlnDebug { "TIME: $racesTime" }
        printlnDebug { "DISTANCE: $racesDistance" }
        val race = Race(
            time = racesTime.toLong(),
            distance = racesDistance.toLong(),
        )
        return findAllCombinations(race).count { it.travelDistance > race.distance }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")
    val part1Test = part1(testInput)

    println(part1Test)
    check(part1Test == 288)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 3316275)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 27102791)
}
