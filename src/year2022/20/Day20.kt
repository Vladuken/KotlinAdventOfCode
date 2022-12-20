package year2022.`20`

import readInput

fun parseInput(input: List<String>): List<Long> {
    return input.map { it.toLong() }
}

fun mixList(
    repeat: Int,
    items: List<Pair<Int, Long>>,
): List<Pair<Int, Long>> {
    val mutableItems = items.toMutableList()

    repeat(repeat) {
        items.forEach {
            val indexOfItemToMove = mutableItems.indexOf(it)
            val itemToMove = mutableItems[indexOfItemToMove]
            val amountOfMovement = itemToMove.second

            // Remove item
            mutableItems.removeAt(indexOfItemToMove)

            // Calculate new item position
            val newIndex = (indexOfItemToMove + amountOfMovement % mutableItems.size).toInt()
            val finalIndex = when {
                newIndex < 0 -> mutableItems.size + newIndex
                newIndex != mutableItems.size && newIndex > mutableItems.size -> newIndex - mutableItems.size
                newIndex == mutableItems.size -> 0
                newIndex == 0 -> mutableItems.size
                else -> newIndex
            }

            // Insert item in new position
            mutableItems.add(finalIndex, itemToMove)
        }
    }

    return mutableItems
}

fun calculateFinalValue(mutableItems: List<Pair<Int, Long>>): Long {
    val zeroItem = mutableItems.find { it.second == 0L }
    val indexOfZero = mutableItems.indexOf(zeroItem)

    return listOf(1000, 2000, 3000)
        .map { indexOfZero + it }
        .map { it % mutableItems.size }
        .map { mutableItems[it] }
        .sumOf { it.second }
}


fun main() {

    fun part1(input: List<String>): Long {
        val items = parseInput(input).mapIndexed { index, i -> index to i }
        val mixedList = mixList(1, items)
        return calculateFinalValue(mixedList)
    }

    fun part2(input: List<String>): Long {
        val items = parseInput(input).mapIndexed { index, i -> index to i * 811589153L }
        val mixedList = mixList(10, items)
        return calculateFinalValue(mixedList)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day20_test")
    val part1Test = part1(testInput)

    println(part1Test)
    check(part1Test == 3L)

    val input = readInput("Day20")
    println(part1(input))
    println(part2(input))
}
