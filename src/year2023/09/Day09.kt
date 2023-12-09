package year2023.`09`

import readInput
import utils.printlnDebug

private const val CURRENT_DAY = "09"

private fun parseLineInto(line: String): List<Long> {
    return line.split(" ")
        .map { it.toLong() }
}

fun process(initialList: List<Long>): List<List<Long>> {
    val resList = mutableListOf(initialList)
    var currentValue: List<Long> = initialList

    while (currentValue.all { it == 0L }.not()) {
        val newList = mutableListOf<Long>()
        currentValue.windowed(2, 1, false) {
            val last = it.last()
            val first = it.first()

            newList.add(last - first)
        }
        currentValue = newList
        resList.add(currentValue)
    }
    return resList
}

fun List<List<Long>>.predictNewItem(): Long {
    return map { it.last() }
        .reversed()
        .reduce { acc, i -> acc + i }
}

fun List<List<Long>>.predictPrevItem(): Long {
    return map { it.first() }
        .reversed()
        .reduce { acc, i -> i - acc }
}

fun main() {

    fun part1(input: List<String>): Long {
        return input.map {
            process(parseLineInto(it))
        }
            .onEach { printlnDebug { it } }
            .sumOf { it.predictNewItem() }
    }

    fun part2(input: List<String>): Long {
        return input.map {
            process(parseLineInto(it))
        }
            .onEach { printlnDebug { it } }
            .sumOf { it.predictPrevItem() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")
    val part1Test = part1(testInput)

    println(part1Test)
    check(part1Test == 114L)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 1479011877L)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 973L)
}
