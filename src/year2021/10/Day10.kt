package year2021.`10`

import readInput


val entryBrackets = setOf<Char>(
    '{',
    '[',
    '(',
    '<',
)

val closingBracket = setOf<Char>(
    '}',
    ']',
    ')',
    '>',
)

fun charToValue(bracket: Char): Int {
    return when (bracket) {
        ')' -> 3
        ']' -> 57
        '}' -> 1197
        '>' -> 25137
        else -> error("Illegal bracket: $bracket")
    }
}

private sealed class LineAnswer {
    data class Success(val line: String) : LineAnswer()
    data class Incomplete(
        val line: String,
        val stack: ArrayDeque<Char>,
    ) : LineAnswer()

    data class Corrupted(
        val line: String,
        val corruptedBy: Char,
    ) : LineAnswer()
}

private fun processLine(line: String): LineAnswer {
    val stack = ArrayDeque<Char>()
    line.forEach { currentBracket ->
        if (currentBracket in entryBrackets) {
            stack.add(currentBracket)
        } else if (currentBracket in closingBracket) {
            when (stack.lastOrNull()) {
                '{' -> if (currentBracket == '}') {
                    stack.removeLast()
                } else {
                    return LineAnswer.Corrupted(line, currentBracket)
                }
                '[' -> if (currentBracket == ']') {
                    stack.removeLast()
                } else {
                    return LineAnswer.Corrupted(line, currentBracket)
                }
                '(' -> if (currentBracket == ')') {
                    stack.removeLast()
                } else {
                    return LineAnswer.Corrupted(line, currentBracket)
                }
                '<' -> if (currentBracket == '>') {
                    stack.removeLast()
                } else {
                    return LineAnswer.Corrupted(line, currentBracket)
                }
                else -> stack.add(currentBracket)
            }
        }
    }

    return if (stack.isEmpty()) {
        LineAnswer.Success(line)
    } else {
        LineAnswer.Incomplete(line, ArrayDeque(stack))
    }

}

fun createEndingLineForStack(stack: ArrayDeque<Char>): String {
    var resString = ""
    while (stack.isNotEmpty()) {
        val last = stack.removeLast()
        resString += when (last) {
            '{' -> '}'
            '[' -> ']'
            '(' -> ')'
            '<' -> '>'
            else -> error("Illegal state: $last")
        }
    }
    return resString
}


fun mapToValue2(char: Char): Int {
//    ): 1 point.
//    ]: 2 points.
//}: 3 points.
//>: 4 points.
    return when (char) {
        ')' -> 1
        ']' -> 2
        '}' -> 3
        '>' -> 4
        else -> error("Illegal bracket: $char")
    }
}

fun calculateAnswerFromLine(line: String): Long {

    var counter = 0L
    line.forEach {
        counter = counter * 5 + mapToValue2(it)
    }
    return counter
}

fun main() {


    fun part1(input: List<String>): Int {
        return input
            .map { processLine(it) }
            .filterIsInstance<LineAnswer.Corrupted>()
            .sumOf { charToValue(it.corruptedBy) }
    }

    fun part2(input: List<String>): Long {
        val result = input
            .asSequence()
            .map { processLine(it) }
            .filterIsInstance<LineAnswer.Incomplete>()
            .map { createEndingLineForStack(it.stack) }
            .map { answerLine -> calculateAnswerFromLine(answerLine) }
            .sorted()
            .toList()

        return result[(result.size / 2)]
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    val part1Test = part1(testInput)
    val part2Test = part2(testInput)

    println(part1Test)
    println(part2Test)
    check(part1Test == 26397)
    check(part2Test == 288957L)

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}
