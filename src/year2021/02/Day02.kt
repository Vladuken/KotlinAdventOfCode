package year2021.`02`

import readInput

private enum class Command {
    Forward,
    Down,
    Up;
}

private data class CommandWrapper(
    val command: Command,
    val amount: Int,
)

fun main() {

    fun parse(input: List<String>): List<CommandWrapper> {
        return input
            .map {
                val (command, amount) = it.split(" ")
                val result = when (command) {
                    "forward" -> Command.Forward
                    "down" -> Command.Down
                    "up" -> Command.Up
                    else -> error("Illegal State $command")
                }

                CommandWrapper(result, amount.toInt())
            }
    }

    fun part1(input: List<String>): Int {
        var horizontal = 0
        var depth = 0

        parse(input)
            .forEach { (command, amount) ->
                when (command) {
                    Command.Forward -> {
                        horizontal += amount
                    }
                    Command.Up -> {
                        depth -= amount
                    }
                    Command.Down -> {
                        depth += amount
                    }
                }
            }

        return horizontal * depth
    }

    fun part2(input: List<String>): Int {
        var aim = 0
        var horizontal = 0
        var depth = 0

        parse(input)
            .forEach { (command, amount) ->
                when (command) {
                    Command.Forward -> {
                        horizontal += amount
                        depth += aim * amount
                    }
                    Command.Up -> {
                        aim -= amount
                    }
                    Command.Down -> {
                        aim += amount
                    }
                }
            }

        return horizontal * depth
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 150)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
