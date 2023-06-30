package year2021.`07`

import readInput
import kotlin.math.abs

fun main() {

    fun calculateFullAmountOfFuelToPosition(
        position: Int,
        map: Map<Int, Int>,
        modifier: (Int) -> Int
    ): Int {
        return map.keys.sumOf {
            modifier(abs(it - position)) * map[it]!!
        }
    }

    fun solution(
        input: List<String>,
        modifier: (Int) -> Int
    ): Int {
        val mappedResult = input.first()
            .split(",")
            .map { it.toInt() }
            .groupBy { it }
            .mapValues { it.value.count() }

        return (mappedResult.keys.min()..mappedResult.keys.max())
            .minOf { calculateFullAmountOfFuelToPosition(it, mappedResult, modifier) }
    }

    fun part1(input: List<String>): Int = solution(input) { n -> n }

    fun part2(input: List<String>): Int = solution(input) { n -> n * (n + 1) / 2 }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    val part1Test = part1(testInput)

    println(part1Test)
    check(part1Test == 37)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
