package year2024.`03`

import readInput

private const val CURRENT_DAY = "03"


private fun parseLineIntoValidConditions(
    line: String,
): Int {
    val conditions = line.split("mul(", ")")
    val validConditions = conditions.mapNotNull {
        val splitList = runCatching { it.split(",").map { it.toInt() } }
            .getOrNull()
        splitList
    }

    return validConditions.findSumOfValid()
}

private fun List<List<Int>>.findSumOfValid(): Int {
    return this.sumOf {
        val left = it[0]
        val right = it[1]
        left * right
    }
}


private fun parseLineIntoValidConditionsAndDoDont(line: String): Int {
    val resLine = line.split("do()", "don't()")
    var currentLine = line
    var counter = 0
    resLine.forEach {
        val subsrtingbefore = currentLine.substringBefore(it, "")
        when (subsrtingbefore) {
            "" -> counter += parseLineIntoValidConditions(it)
            "don't()" -> {}
            "do()" -> counter += parseLineIntoValidConditions(it)
        }
        currentLine = currentLine.substringAfter(it)
    }
    return counter
}

fun main() {

    fun part1(input: List<String>): Int {
        val resString = input.joinToString("", "", "")
        return parseLineIntoValidConditions(resString)
    }

    fun part2(input: List<String>): Int {
        val resString = input.joinToString("", "", "")
        return parseLineIntoValidConditionsAndDoDont(resString)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 161)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 48)
    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 187825547)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 85508223)
}
