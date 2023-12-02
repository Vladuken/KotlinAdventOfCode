package year2023.`02`

import readInput
import utils.printlnDebug

private const val CURRENT_DAY = "02"

private val initialGameCount = Round(
    red = 12,
    green = 13,
    blue = 14,
)

private data class Game(
    val index: Int,
    val listOfGames: List<Round>,
)

private data class Round(
    val blue: Int,
    val red: Int,
    val green: Int,
)

private fun parseLineIntoGame(input: String): Game {
    // Game N
    val gameIndex = input.split(":")
        .first()
        .split(" ")
        .last()
        .toInt()

    // X blue, Y red, Z green; X1 blue, Y1 red, Y1 green; .....
    val listOfGames: List<Round> = input.split(":")
        .last()
        .split(";")
        .map { roundString ->
            var initGame = Round(0, 0, 0)

            roundString.trim().split(",").forEach { numWithColor ->
                val numWithColorList = numWithColor.trim().split(" ")
                val num = numWithColorList.first().toInt()
                when (val color = numWithColorList.last()) {
                    "red" -> initGame = initGame.copy(red = num)
                    "green" -> initGame = initGame.copy(green = num)
                    "blue" -> initGame = initGame.copy(blue = num)
                }
            }

            initGame
        }

    printlnDebug { listOfGames.joinToString { it.toString() } }
    return Game(
        index = gameIndex,
        listOfGames = listOfGames,
    )
}

private fun Game.isThisGamePossible(
    game: Round,
): Boolean {
    return listOfGames.all {
        it.blue <= game.blue &&
                it.red <= game.red &&
                it.green <= game.green
    }
}

private fun Game.findPowerOfTheGame(): Int {
    val green = listOfGames.maxOf { it.green }
    val red = listOfGames.maxOf { it.red }
    val blue = listOfGames.maxOf { it.blue }

    return green * red * blue
}

fun main() {

    fun part1(input: List<String>): Int {
        return input.map { parseLineIntoGame(it) }
            .filter { it.isThisGamePossible(initialGameCount) }
            .sumOf { it.index }
    }

    fun part2(input: List<String>): Int {
        return input
            .map { parseLineIntoGame(it) }
            .map { it.findPowerOfTheGame() }
            .sumOf { it }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")
    val part1Test = part1(testInput)
    val part2Test = part2(testInput)
    println(part1Test)
    println(part2Test)

    check(part1Test == 8)
    check(part2Test == 2286)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 2512)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 67335)
}
