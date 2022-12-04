package year2022.`04`

import readInput

fun main() {

    fun List<Int>.asRange(): IntRange {
        return first()..get(1)
    }

    fun IntRange.contains(range: IntRange): Boolean {
        return first >= range.first && last <= range.last
    }

    fun IntRange.overlaps(range: IntRange): Boolean {
        return toSet().intersect(range.toSet()).isNotEmpty()
    }

    fun String.toPairOfRanges(): Pair<IntRange, IntRange> {
        val list = split(",")
        return list.first().split("-").map { it.toInt() }.asRange() to
            list[1].split("-").map { it.toInt() }.asRange()
    }

    fun part1(input: List<String>): Int {
        return input
            .map { it.toPairOfRanges() }
            .filter { (leftRange, rightRange) ->
                leftRange.contains(rightRange) || rightRange.contains(leftRange)
            }
            .size
    }

    fun part2(input: List<String>): Int {
        return input
            .map { it.toPairOfRanges() }
            .filter { (leftRange, rightRange) -> leftRange.overlaps(rightRange) }
            .size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    val part1Test = part2(testInput)

    println(part1Test)
    check(part1Test == 4)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
