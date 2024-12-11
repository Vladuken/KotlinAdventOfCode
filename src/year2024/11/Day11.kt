package year2024.`11`

import readInput

private const val CURRENT_DAY = "11"


private fun parseLineInto(
    line: String,
): List<Stone> {
    return line.split(" ")
        .map { it.toLong() }
        .map { Stone(it) }
}

data class Stone(
    val currentValue: Long,
) {
    override fun toString(): String {
        return "$currentValue"
    }

    fun transformStone(): List<Stone> {
        // RULE 1
        if (currentValue == 0L) {
            return listOf(Stone(1L))
        }

        // RULE 2
        val curValueString = currentValue.toString()
        val len = curValueString.length
        if (len % 2 == 0) {
            val left = curValueString.substring(0, len / 2)
            val right = curValueString.substring(len / 2, len)
            return listOf(Stone(left.toLong()), Stone(right.toLong()))
        }

        // RULE 3
        return listOf(Stone(currentValue * 2024L))
    }
}

fun blink(initialList: List<Stone>): List<Stone> {
    val newList = mutableListOf<Stone>()
    initialList.forEach {
        newList.addAll(it.transformStone())
    }
    return newList
}

fun blink25TimesWithCache(
    initialItem: Stone,
    cache: MutableMap<Stone, List<Stone>> = mutableMapOf(),
): List<Stone> {
    cache[initialItem]?.let {
        return it
    }
    var currentStones = listOf(initialItem)
    repeat(25) {
        currentStones = blink(currentStones)
    }
    cache[initialItem] = currentStones
    return currentStones
}

fun main() {

    fun part1(input: List<String>): Int {
        val stones = parseLineInto(
            input.first()
        )
        val totalCount = stones.sumOf {
            blink25TimesWithCache(it).size
        }
        return totalCount
    }

    fun part2(input: List<String>): Long {
        val stones = parseLineInto(
            input.first()
        )

        val cache = mutableMapOf<Stone, List<Stone>>()
        var totalCount: Long = 0L
        stones.forEach { stone1Iteration ->
            blink25TimesWithCache(stone1Iteration, cache).forEach { stone2Iteration ->
                blink25TimesWithCache(stone2Iteration, cache).forEach { stone3Iteration ->
                    totalCount += blink25TimesWithCache(stone3Iteration, cache).size
                }
            }
        }
        return totalCount
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 55312)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 65601038650482L)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 197157)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 234430066982597L)
}
