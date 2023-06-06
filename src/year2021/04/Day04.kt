package year2021.`04`

import readInput
import transpose


private data class Bingos<T>(
    val items: List<List<T>>
)

private data class Marked<T>(
    val item: T,
    var marked: Boolean
)

fun main() {

    fun extractListOfNumbers(input: List<String>): List<Int> {
        val filteredItems = input.filter { it.isNotBlank() }
        val numbers = filteredItems.first()
            .split(",")
        return numbers.map { it.toInt() }
    }

    fun extractBingoList(input: List<String>): List<Bingos<Marked<Int>>> {
        val filteredItems = input.filter { it.isNotBlank() }
        val bingos = filteredItems.drop(1)
            .windowed(5, 5)
            .map { bingo ->
                bingo.map { line ->
                    line.split(" ")
                        .filter { it.isNotBlank() }
                        .map {
                            Marked(
                                item = it.toInt(),
                                marked = false
                            )
                        }
                }
            }

        return bingos.map { Bingos(it) }
    }

    fun Bingos<Marked<Int>>.playRound(number: Int) {
        items.forEach { line ->
            line.forEach {
                if (it.item == number) it.marked = true
            }
        }
    }

    fun Bingos<Marked<Int>>.isWinner(): Boolean {
        val horizontal = items.any { line -> line.all { it.marked } }
        val vertical = transpose(items).any { line -> line.all { it.marked } }
        return horizontal || vertical
    }

    fun Bingos<Marked<Int>>.calculateResult(numberWon: Int): Int {
        val sum = this.items.sumOf { line ->
            line.sumOf { markedItem ->
                markedItem.item.takeIf { !markedItem.marked } ?: 0
            }
        }
        return sum * numberWon
    }

    fun part1(input: List<String>): Int {
        val numbers = extractListOfNumbers(input)
        val bingoList = extractBingoList(input)

        val iterator = numbers.iterator()
        var hasWinner = false
        var number = 0

        while (!hasWinner) {
            number = iterator.next()
            bingoList.forEach { it.playRound(number) }
            hasWinner = bingoList.any { it.isWinner() }
        }

        val winner = bingoList.find { it.isWinner() } ?: error("No items found")

        return winner.calculateResult(number)
    }


    fun part2(input: List<String>): Int {
        val numbers = extractListOfNumbers(input)
        var bingoList = extractBingoList(input)
        val iterator = numbers.iterator()

        var number = 0
        // Run rounds and filter-out winners
        while (bingoList.size != 1) {
            number = iterator.next()
            bingoList.forEach { it.playRound(number) }

            val winners = bingoList.filter { it.isWinner() }.toSet()
            bingoList = bingoList - winners
        }

        // Run rounds until last bingo is not won.
        val lastBingo = bingoList.first()
        while (!lastBingo.isWinner()) {
            number = iterator.next()
            lastBingo.playRound(number)
        }

        return lastBingo.calculateResult(number)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    val part1Test = part1(testInput)

    check(part1Test == 4512)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
