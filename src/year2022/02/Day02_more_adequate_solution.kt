package year2022.`02`

import readInput

fun main() {

    fun part1(input: List<String>): Int {
        return input
            .map { it.split(" ") }
            .map {
                val opponentFigure = Figure.from(it.first())
                val yourFigure = Figure.from(it[1])

                calculateOutcomeForYou(
                    opponent = opponentFigure,
                    you = yourFigure
                ) to yourFigure
            }
            .sumOf { (outcome, yourFigure) ->
                outcome.amount + yourFigure.amount
            }
    }

    fun part2(input: List<String>): Int {
        return input
            .map { it.split(" ") }
            .map {
                val opponentFigure = Figure.from(it.first())
                val outcome = Outcome.from(it[1])

                calculateFigureForOutcome(
                    opponent = opponentFigure,
                    outcome = outcome
                ) to outcome
            }
            .sumOf { (outcome, yourFigure) ->
                outcome.amount + yourFigure.amount
            }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    val part1Test = part1(testInput)

    //println(part1Test)
    check(part1Test == 15)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}

/**
 * Representation of `year2022.02`.Figure with its value
 */
enum class Figure(val amount: Int) {
    ROCK(1),
    PAPER(2),
    SCISSORS(3);

    companion object {
        fun from(code: String): Figure {
            return when (code) {
                "A", "X" -> ROCK
                "B", "Y" -> PAPER
                "C", "Z" -> SCISSORS
                else -> error("Illegal state")
            }
        }
    }
}


/**
 * `year2022.02`.Outcome Representation with it value
 */
enum class Outcome(val amount: Int) {
    LOSE(0),
    DRAW(3),
    WIN(6);

    companion object {
        fun from(code: String): Outcome {
            return when (code) {
                "X" -> LOSE
                "Y" -> DRAW
                "Z" -> WIN
                else -> error("Illegal state")
            }
        }
    }
}

fun calculateOutcomeForYou(opponent: Figure, you: Figure): Outcome {
    return when (opponent) {
        Figure.ROCK -> when (you) {
            Figure.ROCK -> Outcome.DRAW
            Figure.PAPER -> Outcome.WIN
            Figure.SCISSORS -> Outcome.LOSE
        }
        Figure.PAPER -> when (you) {
            Figure.ROCK -> Outcome.LOSE
            Figure.PAPER -> Outcome.DRAW
            Figure.SCISSORS -> Outcome.WIN
        }
        Figure.SCISSORS -> when (you) {
            Figure.ROCK -> Outcome.WIN
            Figure.PAPER -> Outcome.LOSE
            Figure.SCISSORS -> Outcome.DRAW
        }
    }
}

fun calculateFigureForOutcome(opponent: Figure, outcome: Outcome): Figure {
    return when (opponent) {
        Figure.ROCK -> when (outcome) {
            Outcome.LOSE -> Figure.SCISSORS
            Outcome.DRAW -> Figure.ROCK
            Outcome.WIN -> Figure.PAPER
        }
        Figure.PAPER -> when (outcome) {
            Outcome.LOSE -> Figure.ROCK
            Outcome.DRAW -> Figure.PAPER
            Outcome.WIN -> Figure.SCISSORS
        }
        Figure.SCISSORS -> when (outcome) {
            Outcome.LOSE -> Figure.PAPER
            Outcome.DRAW -> Figure.SCISSORS
            Outcome.WIN -> Figure.ROCK
        }
    }
}
