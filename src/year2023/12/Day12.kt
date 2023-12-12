package year2023.`12`

import kotlinx.coroutines.runBlocking
import readInput

private const val CURRENT_DAY = "12"

private fun parseLineInto(
    line: String
): Pair<String, List<Int>> {
    val splitted = line.split(" ")
    val resLine = splitted.first()
    val ranges = splitted[1].split(",").mapNotNull { it.toIntOrNull() }
    return resLine to ranges
}


private fun unfold(
    input: Pair<String, List<Int>>
): Pair<String, List<Int>> {
    val first = input.first
    val ranges = input.second

    val newFirst = buildString {
        repeat(5) {
            append(first)
            if (it != 4) {
                append("?")
            }
        }
    }
    val newRanges = buildList {
        repeat(5) {
            addAll(ranges)
        }
    }
    return newFirst to newRanges
}

fun main() {

    fun part1(input: List<String>): Long {
        cache.clear()
        val sum = input.map { parseLineInto(it) }
            .asSequence()
            .map { (line, indexes) ->
                solve(
                    line = line.toList(),
                    ranges = indexes.map { it.toLong() }
                )
            }
            .sum()
        return sum
    }

    fun part2(input: List<String>): Long {
        cache.clear()
        val result = runBlocking {
            val sum = input
                .map { unfold(parseLineInto(it)) }
                .mapIndexed { _, pair ->
                    solve(
                        line = pair.first.toList(),
                        ranges = pair.second.map { it.toLong() }
                    )
                }
                .sumOf { it }
            sum
        }
        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 21L)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 525152L)


    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 7221L)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 7139671893722L)
}


fun solve(
    line: List<Char>,
    ranges: List<Long>,
): Long {
    if (line.isEmpty()) {
        return if (ranges.isEmpty()) 1 else 0
    }

    return when (line.first()) {
        '.' -> solve(line.drop(1), ranges) // ok
        '#' -> findForSprinkle(ranges, line)
        '?' -> solve(
            line = line.drop(1),
            ranges = ranges,
        ) + findForSprinkle(
            ranges = ranges,
            line = line,
        )

        else -> error("Illegal State $line")
    }
}

val cache = mutableMapOf<Pair<List<Long>, List<Char>>, Long>()

private fun findForSprinkle(
    ranges: List<Long>,
    line: List<Char>
): Long {
    cache[ranges to line]?.let { return it }

    // No Ranges
    if (ranges.isEmpty()) return 0

    val currentRange = ranges[0]
    if (line.size < currentRange) return 0

    val isThereAnyDotsInRange = line.take(currentRange.toInt()).any { it == '.' }
    if (isThereAnyDotsInRange) return 0

    // Length of line is same as LAST range
    if (line.size == currentRange.toInt()) {
        if (ranges.size == 1) return 1
        return 0
    }

    val rangeEndsWithSprinkle = line[currentRange.toInt()] == '#'
    if (rangeEndsWithSprinkle) return 0

    return solve(
        line = line.drop(currentRange.toInt() + 1),
        ranges = ranges.drop(1),
    ).also { cache[ranges to line] = it }
}