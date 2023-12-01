package year2023.`01`

import readInput
import utils.printDebug

private const val CURRENT_DAY = "01"

private val mapOfValues = mapOf(
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9,
    "1" to 1,
    "2" to 2,
    "3" to 3,
    "4" to 4,
    "5" to 5,
    "6" to 6,
    "7" to 7,
    "8" to 8,
    "9" to 9,
)

private fun findSumForPart1(input: List<String>): Long {
    return input.sumOf {
        val listOfNumbers = it
            .split("")
            .mapNotNull { it.toIntOrNull() }

        val result = listOfNumbers.first().toString() + listOfNumbers.last().toString()
        printDebug { result }
        result.toLong()
    }
}


private fun findItem(line: String): String {
    val indexes = mapOfValues.keys.flatMap {
        val indexOfFirstValue = line.indexOf(it) to mapOfValues[it]!!
        val indexOfLastValue = line.lastIndexOf(it) to mapOfValues[it]!!

        listOf(indexOfFirstValue, indexOfLastValue)
    }
        .filter { it.first != -1 }
        .sortedBy { it.first }

    val firstNumber = indexes.first().second
    val lastNumber = indexes.last().second

    val result = "$firstNumber$lastNumber"
    printDebug { "$line --- $result" }
    return result
}

fun main() {

    fun part1(input: List<String>): Long {
        return findSumForPart1(input)
    }

    fun part2(input: List<String>): Long {
        return input
            .map { findItem(it) }
            .sumOf { it.toLong() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")
    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 54634L)

    // Part 2
    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 281L)

    val part2 = part2(input)
    println(part2)
    check(part2 == 1L)
}
