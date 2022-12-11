package year2022.`11`

import java.math.BigInteger
import readInput

fun main() {

    fun runRound(monkeys: List<Monkey>, is3: Boolean) {
        val divider = monkeys.fold(1L) { item, monkey -> item * monkey.divisibleBy }

        monkeys.forEach { monkey ->
            monkey.startingItems.forEach { item ->
                val newWorryLevel = (monkey.operation(item) / if (is3) 3L else 1L) % divider
                monkey.inspectedCount++
                val newMonkey = if (newWorryLevel % monkey.divisibleBy == 0L) {
                    monkey.trueDest
                } else {
                    monkey.falseDest
                }
                monkeys[newMonkey].startingItems.add(newWorryLevel)
            }
            monkey.startingItems.clear()
        }
    }


    fun part1(input: List<String>): BigInteger {
        val data = parseInput(input)
        repeat(20) { runRound(data, true) }
        val (firstMonkey, secondMonkey) = data.sortedByDescending { it.inspectedCount }
        return firstMonkey.inspectedCount * secondMonkey.inspectedCount
    }

    fun part2(input: List<String>): BigInteger {
        val data = parseInput(input)
        repeat(10000) { runRound(data, false) }
        val (firstMonkey, secondMonkey) = data.sortedByDescending { it.inspectedCount }
        return firstMonkey.inspectedCount * secondMonkey.inspectedCount
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    val part1Test = part1(testInput)

    println(part1Test)
    check(part1Test == 10605.toBigInteger())

    val input = readInput("Day11")

    println(part1(input))
    println(part2(input))
}


data class Monkey(
    val index: Int,
    val startingItems: MutableList<Long>,
    val operation: (Long) -> Long,
    val divisibleBy: Long,
    val trueDest: Int,
    val falseDest: Int,
    var inspectedCount: BigInteger = 0.toBigInteger()
)

fun parseInput(input: List<String>): List<Monkey> {
    return input.chunked(7)
        .map {
            val monkeyIndex = it[0]
                .replace("Monkey", "")
                .trim()
                .dropLast(1)
                .toInt()
            val startingItems = it[1]
                .replace("Starting items:", "")
                .trim()
                .split(",")
                .map { it.trim().toLong() }
            val operation = it[2]
                .replace("Operation: new =", "")
                .trim()
                .split(" ")
            val lambda: (Long) -> Long = { firstNumber ->
                val (_, op, third) = operation
                val secondNumber = third.takeIf { it != "old" }?.toLong() ?: firstNumber
                when (op) {
                    "*" -> firstNumber * secondNumber
                    "+" -> firstNumber + secondNumber
                    else -> error("!!")
                }
            }
            val divisibleBy = it[3].replace("Test: divisible by", "").trim().toLong()
            val ifTrue = it[4].replace("If true: throw to monkey", "").trim().toInt()
            val ifFalse = it[5].replace("If false: throw to monkey", "").trim().toInt()

            Monkey(
                index = monkeyIndex,
                startingItems = startingItems.toMutableList(),
                operation = lambda,
                divisibleBy = divisibleBy,
                trueDest = ifTrue,
                falseDest = ifFalse
            )
        }
}
