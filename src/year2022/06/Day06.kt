package year2022.`06`

import readInput

fun main() {

    fun solutionWithSets(data: String, messageSize: Int): Int {
        return data
            .windowed(messageSize)
            .map { it.toSet() }
            .indexOfFirst { it.size == messageSize }
            .plus(messageSize)
    }

    fun part1(input: List<String>): Int {
        return solutionWithSets(input.first(), 4)
    }

    fun part2(input: List<String>): Int {
        return solutionWithSets(input.first(), 14)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    val part1Test = part2(testInput)

    println(part1Test)
    check(part1Test == 19)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
