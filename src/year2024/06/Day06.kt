package year2024.`06`

import readInput

private const val CURRENT_DAY = "06"


private fun parseLineInto(
    line: String,
): Any {
    return 0
}

fun main() {

    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 1)

//    val part2Test = part2(testInput)
//    println(part2Test)
//    check(part2Test == 1)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 1)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 1)
}
