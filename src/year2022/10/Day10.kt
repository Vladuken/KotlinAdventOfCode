package year2022.`10`

import readInput

sealed class Command {
    object NOP : Command()
    data class Addx(val amount: Int) : Command()
    companion object {
        fun from(string: String): Command {
            val items = string.split(" ")
            return when (items[0]) {
                "addx" -> Addx(items[1].toInt())
                "noop" -> NOP
                else -> error("!!")
            }
        }
    }
}

fun main() {

    fun cacheSignalIfConditionMet(
        currentCycle: Int,
        currentSignal: Int,
        results: MutableList<Int>,
    ) {
        if (currentCycle == 20 || ((currentCycle - 20) % 40 == 0)) {
            results.add(currentSignal * currentCycle)
        }
    }

    fun drawPixel(
        currentCycle: Int,
        currentSignal: Int,
        stringRes: StringBuilder
    ) {
        val width = 40
        val pixelToDraw = ((currentCycle - 1) % width)
        val spriteRange = (pixelToDraw - 1..pixelToDraw + 1)
        val symbol = if (currentSignal in spriteRange) {
            "%"
        } else {
            "."
        }
        stringRes.append(symbol)
        if (pixelToDraw + 1 == width) {
            stringRes.appendLine()
        }
    }

    fun iterateOver(
        commands: List<Command>,
        doEachCycle: (cycle: Int, signal: Int) -> Unit
    ) {
        var currentCycle = 1
        var currentSignal = 1

        val iterator = commands.iterator()
        while (iterator.hasNext()) {
            when (val command = iterator.next()) {
                is Command.Addx -> {
                    repeat(2) {
                        doEachCycle(currentCycle, currentSignal)
                        if (it == 1) currentSignal += command.amount
                        currentCycle++
                    }
                }
                Command.NOP -> {
                    doEachCycle(currentCycle, currentSignal)
                    currentCycle++
                }
            }
        }
    }

    fun part1(input: List<String>): Int {
        val commands = input.map { Command.from(it) }
        val results = mutableListOf<Int>()
        iterateOver(commands) { currentCycle, currentValue ->
            cacheSignalIfConditionMet(currentCycle, currentValue, results)
        }
        return results.sum()
    }


    fun part2(input: List<String>): String {
        val commands = input.map { Command.from(it) }
        val stringRes = StringBuilder()
        iterateOver(commands) { currentCycle, currentValue ->
            drawPixel(currentCycle, currentValue, stringRes)
        }
        return stringRes.toString()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    val part1Test = part1(testInput)

    println(part1Test)
    check(part1Test == 13140)

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}
