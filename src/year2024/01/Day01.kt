package year2024.`01`

import readInput
import kotlin.math.abs

private fun prepareList(input: List<String>): Pair<MutableList<Long>, MutableList<Long>> {
    val leftList = mutableListOf<Long>()
    val rightList = mutableListOf<Long>()

    input
        .forEach {
            val parsedList = it.split(" ")
                .filter { it.isNotBlank() }
                .map { it.toLong() }
            leftList.add(parsedList[0])
            rightList.add(parsedList[1])
        }

    return leftList to rightList
}

fun main() {


    fun part1(input: List<String>): Long {
        val (leftList, rightList) = prepareList(input)

        leftList.sort()
        rightList.sort()

        val finalAnswer = leftList.mapIndexed { index, leftItem ->
            abs(leftItem - rightList[index])
        }

        return finalAnswer.sum()
    }

    fun part2(input: List<String>): Long {
        val (leftList, rightList) = prepareList(input)

        leftList.sort()
        rightList.sort()

        val finalAnswer = leftList.mapIndexed { i, leftItem ->
            leftItem * rightList.count { it == leftItem }
        }

        return finalAnswer.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    println("Test Part 1 = " + part1(testInput))
    println("Test Part 2 = " + part2(testInput))
    check(part1(testInput) == 11L)
    check(part2(testInput) == 31L)

    val input = readInput("Day01")
    println("Part 1 = " + part1(input))
    println("Part 2 = " + part2(input))
    check(part1(input) == 1938424L)
    check(part2(input) == 22014209L)
}
