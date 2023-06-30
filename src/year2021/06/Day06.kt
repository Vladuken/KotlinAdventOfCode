package year2021.`06`

import readInput

fun main() {

    fun cycle(input: Map<Long, Long>): Map<Long, Long> {
        val resultMap = mutableMapOf<Long, Long>()

        input.keys
            .sortedDescending()
            .forEach {
                when (it) {
                    0L -> {
                        resultMap[6] = (resultMap[6] ?: 0) + (input[0] ?: 0)
                        resultMap[8] = (resultMap[8] ?: 0) + (input[0] ?: 0)
                    }
                    else -> {
                        resultMap[it - 1] = (input[it] ?: 0)
                    }
                }
            }

        return resultMap
    }

    fun functionWithMap(input: List<String>, cycleAmount: Int): Long {
        val items = input.first().split(",").map { it.toLong() }

        val initialMap = mutableMapOf<Long, Long>()
        items.forEach {
            if (initialMap.containsKey(it)) {
                initialMap[it] = initialMap[it]!! + 1
            } else {
                initialMap[it] = 1
            }
        }

        var resultMap: Map<Long, Long> = initialMap
        repeat(cycleAmount) {
            resultMap = cycle(resultMap)
        }

        return resultMap.values.sum()
    }

    // endregion

    fun part1(input: List<String>): Long {
        return functionWithMap(input, 80)
    }

    fun part2(input: List<String>): Long {
        return functionWithMap(input, 256)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    val part1Test = part1(testInput)

    println(part1Test)
    check(part1Test == 5934L)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
