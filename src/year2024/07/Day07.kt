package year2024.`07`

import readInput

private const val CURRENT_DAY = "07"


private fun parseLineInto(
    line: String,
): CalibrationLine {
    val (left, right) = line.split(":")
    return CalibrationLine(
        result = left.toLong(),
        numbers = right.trim()
            .split(" ")
            .filter { it.isNotBlank() }
            .map { it.toLong() }
    )
}

data class CalibrationLine(
    val result: Long,
    val numbers: List<Long>,
)

fun brutForceLine(
    expectedValue: Long,
    currentValue: Long,
    numbers: List<Long>,
    withConcat: Boolean,
): Boolean {
    if (numbers.isEmpty()) return expectedValue == currentValue

    return if (withConcat) {
        brutForceLine(
            expectedValue = expectedValue,
            currentValue = (currentValue.toString() + numbers.first().toString()).toLong(),
            numbers = numbers.drop(1),
            withConcat = true,
        )
    } else {
        false
    } || brutForceLine(
        expectedValue = expectedValue,
        currentValue = currentValue * numbers.first(),
        numbers = numbers.drop(1),
        withConcat = withConcat,
    ) || brutForceLine(
        expectedValue = expectedValue,
        currentValue = currentValue + numbers.first(),
        numbers = numbers.drop(1),
        withConcat = withConcat,
    )

}

fun main() {

    fun part1(input: List<String>): Long {
        val calibrations = input.map {
            parseLineInto(it)
        }
        val new = calibrations.filter {
            brutForceLine(
                expectedValue = it.result,
                currentValue = it.numbers.first(),
                numbers = it.numbers.drop(1),
                withConcat = false,
            )
        }
        return new.sumOf { it.result }
    }

    fun part2(input: List<String>): Long {
        val calibrations = input.map {
            parseLineInto(it)
        }
        val old = calibrations.filter {
            brutForceLine(
                it.result,
                it.numbers.first(),
                it.numbers.drop(1),
                false,
            )
        }.toSet()

        val new = calibrations.filter {
            brutForceLine(
                it.result,
                it.numbers.first(),
                it.numbers.drop(1),
                true
            )
        }.toSet()


        return (old + new).sumOf { it.result }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 3749L)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 11387L)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 850435817339L)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 104824810233437L)
}
