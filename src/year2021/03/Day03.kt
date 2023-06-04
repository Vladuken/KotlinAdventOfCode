package year2021.`03`

import readInput

fun main() {

    fun parse(input: List<String>): List<List<Boolean>> {
        return input
            .map { line ->
                line.split("")
                    .filter { it.isNotBlank() }
                    .map { bitline ->
                        when (bitline) {
                            "0" -> false
                            "1" -> true
                            else -> error("Illegal: $bitline")
                        }
                    }
            }
    }

    fun parseToPairsOfCounts(items: List<List<Boolean>>): List<Pair<Int, Int>> {
        val result = List(items.first().size) { index ->
            val ones = items.count {
                it[index]
            }
            val zeroes = items.count {
                it[index].not()
            }

            zeroes to ones
        }
        return result
    }

    fun part1Extraction(
        result: List<Pair<Int, Int>>,
        zeroChar: Char,
        oneChar: Char,
    ): Int {
        val epsilon = result.map { (zero, one) ->
            when {
                zero > one -> oneChar
                zero < one -> zeroChar
                else -> error("Illegal")
            }
        }
            .joinToString("") { it.toString() }
            .toInt(2)
        return epsilon
    }

    fun part1(input: List<String>): Int {
        val items = parse(input)
        val result = parseToPairsOfCounts(items)

        val epsilon = part1Extraction(result, '0', '1')
        val gamma = part1Extraction(result, '1', '0')

        return gamma * epsilon
    }

    fun part2subpart1(
        input: List<String>,
        zeroChar: Char,
        oneChar: Char,
    ): List<String> {
        var buffItems = input
        var index = 0

        while (buffItems.size > 1) {
            val boolsPairs = parse(buffItems)
            val result = parseToPairsOfCounts(boolsPairs)
            val (zero, one) = result[index]
            buffItems = when {
                zero > one -> buffItems.filter { it[index] == zeroChar }
                zero < one -> buffItems.filter { it[index] == oneChar }
                else -> buffItems.filter { it[index] == oneChar }
            }
            index++
        }
        return buffItems
    }


    fun part2(input: List<String>): Int {
        val num1 = part2subpart1(input, '0', '1').first().toInt(2)
        val num2 = part2subpart1(input, '1', '0').first().toInt(2)
        return num1 * num2
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part2(testInput) == 230)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
