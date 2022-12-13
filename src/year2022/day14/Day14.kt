package year2022.day14

import readInput

fun main() {

    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    val part1Test = part1(testInput)

    println(part1Test)
    check(part1Test == 1)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}
