package year2023.`04`

import readInput
import utils.printlnDebug
import kotlin.math.pow
import kotlin.math.roundToInt

private const val CURRENT_DAY = "04"

private fun parseLineIntoCardValues(
    line: String,
): Set<String> {
    val winningNumbers = line.split("|")
        .first()
        .split(":")[1]
        .trim()
        .split(" ")
        .filter { it.isNotEmpty() }

    val myNumbers = line.split("|")[1]
        .trim()
        .split(" ")
        .filter { it.isNotEmpty() }

    return winningNumbers.intersect(myNumbers.toSet())
}

private fun Collection<String>.toAnswerValue(): Int {
    if (isEmpty()) return 0
    return 2.0.pow(this.size - 1).roundToInt()
}

private fun part2Impl(input: List<String>): Int {
    val intersectedCountForCard = mutableMapOf<Int, Int>()

    input.mapIndexed { index, line ->
        val something = parseLineIntoCardValues(line)
        intersectedCountForCard[index] = something.count()
    }

    printlnDebug { intersectedCountForCard }

    val mapOfCardsAtAll = intersectedCountForCard.keys.associateWith { 1 }
        .toMutableMap()

    intersectedCountForCard.keys.sorted()
        .forEach { index ->
            val listSize = intersectedCountForCard[index]!!

            val howMuchCardIHaveRightNow = mapOfCardsAtAll[index]!!
            printlnDebug { "howMuchCardIHaveRightNow: index$index amount:$howMuchCardIHaveRightNow" }

            repeat(listSize) { wonIndex ->
                val realIndex = index + wonIndex + 1
                mapOfCardsAtAll.compute(realIndex) { _, currentValue ->
                    val initialValue = currentValue ?: 0
                    initialValue + howMuchCardIHaveRightNow
                }
            }
        }

    printlnDebug { "mapOfCount: $mapOfCardsAtAll" }
    return mapOfCardsAtAll.values.sum()
}

fun main() {

    fun part1(input: List<String>): Int {
        return input.sumOf {
            parseLineIntoCardValues(it).toAnswerValue()
        }
    }

    fun part2(input: List<String>): Int {
        return part2Impl(input)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")
    val part1Test = part1(testInput)
    val part2Test = part2(testInput)

    println(part1Test)
    check(part1Test == 13)

    println(part2Test)
    check(part2Test == 30)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 28750)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 10212704)
}
