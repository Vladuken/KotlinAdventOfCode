package year2022.`25`

import kotlin.math.pow
import kotlin.math.roundToLong
import readInput

fun String.toDecimal(): Long {
    return split("").filter { it.isNotEmpty() }.map {
        when (it) {
            "2" -> 2
            "1" -> 1
            "0" -> 0
            "-" -> -1
            "=" -> -2
            else -> error("!!")
        }
    }.reversed().mapIndexed { index, i ->
        val pow = 5.0.pow(index).roundToLong()
        pow * i
    }.sum()
}

fun Long.toSNUFU(): String {
    val answer = StringBuilder()
    var number = this

    while (number != 0L) {
        val whatToAppend = when (number % 5) {
            2L -> "2"
            1L -> "1"
            0L -> "0"
            3L -> {
                number += 5
                "="
            }
            4L -> {
                number += 5
                "-"
            }
            else -> error("!!")
        }

        answer.append(whatToAppend)
        number /= 5L
    }
    return answer.reversed().toString()
}

fun main() {

    fun part1(input: List<String>): String {
        return input.sumOf { it.toDecimal() }.toSNUFU()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day25_test")
    val part1Test = part1(testInput)

    println(part1Test)
    check(part1Test == "2=-1=0")

    val input = readInput("Day25")
    println(part1(input))
}
