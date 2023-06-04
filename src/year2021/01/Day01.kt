package year2021.`01`

import readInput

fun main() {

    fun part1(input: List<String>): Int {
        var count = 0
        input
            .map { it.toInt() }
            .reduce { left, right ->
                if (right > left) count++
                right
            }
        return count
    }

    fun part2(input: List<String>): Int {
        var count = 0
        input
            .map { it.toInt() }
            .windowed(3, 1)
            .reduce { left, right ->
                if (right.sum() > left.sum()) count++
                right
            }
        return count
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part2(testInput) == 5)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
