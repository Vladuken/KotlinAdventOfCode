package year2024.`02`

import readInput

private const val CURRENT_DAY = "02"


private fun parseLineInto(
    line: String,
): List<Long> {
    return line.split(" ")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .map { it.toLong() }
}


fun List<Long>.isFullySafe(): Boolean {
    if (isSafe()) return true
    if (allCombinations().any { it.isSafe() }) return true
    return false
}

fun List<Long>.isSafe(): Boolean {
    val sortedList = if (first() < last()) {
        sorted()
    } else {
        sortedDescending()
    }

    if (sortedList != this) return false

    val realSorted = sortedList.sorted()

    var first = realSorted.first()
    realSorted.drop(1).forEach { curr ->
        if (curr - first !in 1..3) return false
        first = curr
    }

    return true
}

fun List<Long>.allCombinations(): List<List<Long>> {
    val mutableList = mutableListOf<List<Long>>()

    for (i in indices) {
        val newList = toMutableList()
        newList.removeAt(i)
        mutableList.add(newList)
    }
    return mutableList
}

fun main() {

    fun part1(input: List<String>): Int {
        val items = input.map { parseLineInto(it) }
        return items.count { it.isSafe() }
    }

    fun part2(input: List<String>): Int {
        val items = input.map {
            parseLineInto(it)
        }
        return items.count { it.isFullySafe() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 2)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 4)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 606)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 644)
}
