package year2022.`05`

import java.util.Stack
import readInput

fun main() {

    fun part1(input: List<String>): String {
        val (mutableStacks, listOfCommands) = prepareStacksToCommands(input)

        applyCommandsToStacks(
            mutableStacks = mutableStacks,
            commands = listOfCommands
        )

        return peekListOfStacks(mutableStacks).joinToString("")
    }


    fun part2(input: List<String>): String {
        val (mutableStacks, listOfCommands) = prepareStacksToCommands(input)

        applyCommandsToStacksPart2(
            mutableStacks = mutableStacks,
            commands = listOfCommands
        )

        return peekListOfStacks(mutableStacks).joinToString("")
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    val part1Test = part1(testInput)

    //println(part1Test)
    check(part1Test == "CMZ")

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}

/**
 * Class for working with moving commands
 */
data class Command(
    val amount: Int,
    val from: Int,
    val to: Int
)

/**
 * Build list of stacks from input
 */
fun buildListOfStacks(
    amountOfStacks: Int,
    lines: List<List<String?>>
): List<Stack<String>> {
    // Build initial empty stacks
    val stacks = mutableListOf<Stack<String>>()
    repeat(amountOfStacks) {
        stacks.add(Stack<String>())
    }

    // Fill stacks with values
    for (item in lines.reversed()) {
        repeat(amountOfStacks) {
            // Workaround for cases, when input line is not full
            try {
                val crate = item[it]
                if (crate != null) {
                    stacks[it].add(crate)
                }
            } catch (e: Exception) {
                return@repeat
            }
        }
    }

    return stacks
}

/**
 * Parse string "[W] [W]     [N] [L] [V] [W] [C]   "
 * to list of [W,W,null,N,L,V,W,C,null]
 */
fun cratesInputToLists(input: List<String>): List<List<String?>> {
    return input.map {
        it.windowed(3, 4)
            .map { crateString ->
                if (crateString.isBlank()) {
                    null
                } else {
                    crateString[1].toString()
                }
            }
    }
}

/**
 * Parse list of stack amount and fetch max amount
 */
fun crateAmountToInt(amountLine: String): Int {
    return amountLine.split(" ")
        .last { it.isNotEmpty() }
        .toInt()
}

/**
 * Move "Crates" one by one
 */
fun applyCommandsToStacks(
    mutableStacks: List<Stack<String>>,
    commands: List<Command>
) {
    commands.forEach { (amount, from, to) ->
        val fromStack = mutableStacks[from - 1]
        val toStack = mutableStacks[to - 1]
        repeat(amount) {
            val itemToMove = fromStack.pop()
            toStack.push(itemToMove)
        }
    }
}

/**
 * Move "Crates" all together
 */
fun applyCommandsToStacksPart2(
    mutableStacks: List<Stack<String>>,
    commands: List<Command>
) {
    commands.forEach { (amount, from, to) ->
        val fromStack = mutableStacks[from - 1]
        val toStack = mutableStacks[to - 1]

        val listToMove = mutableListOf<String>()
        repeat(amount) {
            listToMove.add(fromStack.pop())
        }

        toStack.addAll(listToMove.reversed())
    }
}

/**
 * Parse strings to list of [Command]
 */
fun buildListOfCommands(
    lines: List<String>
): List<Command> {
    return lines.map {
        val keys = it.split(" ")
        Command(keys[1].toInt(), keys[3].toInt(), keys[5].toInt())
    }
}

/**
 * Peek list of stacks and return top items
 */
fun peekListOfStacks(
    stacks: List<Stack<String>>
): List<String> {
    return stacks.map { it.peek() }
}

/**
 * Parse all input to data
 */
fun prepareStacksToCommands(
    input: List<String>
): Pair<List<Stack<String>>, List<Command>> {
    // Part 1
    val firstPart = input.takeWhile { it.isNotBlank() }

    val crates = firstPart.dropLast(1)
    val cratesAmountInput = firstPart.last()

    val mutableStacks = buildListOfStacks(
        amountOfStacks = crateAmountToInt(cratesAmountInput),
        lines = cratesInputToLists(crates)
    )

    // Part 2
    val listOfCommands = buildListOfCommands(
        lines = input.reversed().takeWhile { it.isNotBlank() }.reversed()
    )

    return mutableStacks to listOfCommands
}
