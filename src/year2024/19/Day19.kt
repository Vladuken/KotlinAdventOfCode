package year2024.`19`

import readInput

private const val CURRENT_DAY = "19"


private fun parseLineIntoPatterns(
    line: String,
): List<String> {
    return line.split(",").map { it.trim() }
}

private fun countPossibleVariants(
    patterns: List<String>,
    remaining: String,
    cache: MutableMap<String, Long>,
): Long {
    if (remaining.isEmpty()) return 1
    val result = cache[remaining]
    if (result != null) return result

    var count = 0L
    patterns.forEach {
        if (remaining.startsWith(it)) {
            count += countPossibleVariants(patterns, remaining.removePrefix(it), cache)
        }
    }

    cache[remaining] = count
    return count
}

fun main() {

    fun part1(input: List<String>): Int {
        val patterns = parseLineIntoPatterns(input.first())
        val items = input.reversed().takeWhile { it.isNotBlank() }.reversed()

        val cache = mutableMapOf<String, Long>()
        val totalCount = items.count {
            countPossibleVariants(patterns, it, cache) != 0L
        }
        return totalCount
    }

    fun part2(input: List<String>): Long {
        val patterns = parseLineIntoPatterns(input.first())
        val items = input.reversed().takeWhile { it.isNotBlank() }.reversed()
        val totalCount = items.sumOf {
            val cache = mutableMapOf<String, Long>()
            countPossibleVariants(patterns, it, cache)
        }
        return totalCount
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 6)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 16L)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 296)

    // Part 2
    // 617532994 no
    val part2 = part2(input)
    println(part2)
    check(part2 == 619970556776002L)
}
