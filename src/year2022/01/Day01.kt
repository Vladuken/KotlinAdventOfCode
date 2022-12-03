package year2022.`01`

import readInput

fun main() {

    fun prepareListOfElfsWithCalories(input: List<String>): List<List<Int>> {
        return input
            // Null is for empty strings that are separators.
            .map { it.toIntOrNull() }
            .fold(mutableListOf(mutableListOf<Int>())) { listOfElfs, product ->
                if (product == null) {
                    listOfElfs.add(mutableListOf())
                } else {
                    listOfElfs.last().add(product)
                }
                listOfElfs
            }
    }

    fun part1(input: List<String>): Int {
        return prepareListOfElfsWithCalories(input)
            .maxOfOrNull { it.sum() }
            ?: error("Illegal state")
    }

    fun part2(input: List<String>): Int {
        return prepareListOfElfsWithCalories(input)
            .map { it.sum() }
            .sortedDescending()
            .take(3)
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part2(testInput) == 45000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
