package year2022.`03`

import readInput

const val ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

fun main() {
    val ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

    fun part1(input: List<String>): Int {
        return input
            .map { s1 ->
                val mid: Int = s1.length / 2
                s1.substring(0, mid) to s1.substring(mid)
            }
            .sumOf { (s1, s2) ->
                val union = s1.toSet().intersect(s2.toSet())
                union.sumOf { ALPHABET.indexOf(it).inc() }
            }
    }

    fun part2(input: List<String>): Int {
        val windowSize = 3
        return input
            .windowed(windowSize, windowSize)
            .sumOf { rucksacks ->
                val unique = rucksacks
                    .map { it.toSet() }
                    .reduce { init, item -> init.intersect(item) }
                unique.sumOf { ALPHABET.indexOf(it).inc() }
            }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    val part2Test = part2(testInput)

    println(part2Test)
    check(part2Test == 70)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
