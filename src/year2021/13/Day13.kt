package year2021.`13`

import readInput


private data class Point(
    val x: Int,
    val y: Int
)

private fun extractPoints(input: List<String>): Set<Point> {
    return input.takeWhile { it.isNotBlank() }
        .map {
            val (x, y) = it.split(",")
                .map { it.toInt() }
            Point(x, y)
        }
        .toSet()
}

private sealed class Command {
    data class FoldUp(val y: Int) : Command()
    data class FoldLeft(val x: Int) : Command()
}

private fun extractCommands(input: List<String>): List<Command> {
    return input.reversed()
        .takeWhile { it.isNotBlank() }
        .reversed()
        .map {
            val (coordindate, number) = it.split(" ").last()
                .split("=")
            when (coordindate) {
                "x" -> Command.FoldLeft(x = number.toInt())
                "y" -> Command.FoldUp(y = number.toInt())
                else -> error("Illegal parsed data coordinate:$coordindate number:$number")
            }
        }

}

private fun foldUp(points: Set<Point>, command: Command.FoldUp): Set<Point> {
    val y = command.y

    val (below, above) = points
        .partition { it.y > y }
        .let { (below, above) -> below.toSet() to above.toSet() }

    val resultSet = above.toMutableSet()

    below.forEach {
        val verticalDivide = it.y - y
        resultSet.add(it.copy(y = y - verticalDivide))
    }

    return resultSet
}

private fun foldLeft(points: Set<Point>, command: Command.FoldLeft): Set<Point> {
    val x = command.x

    val (below, above) = points
        .partition { it.x > x }
        .let { (below, above) -> below.toSet() to above.toSet() }

    val resultSet = above.toMutableSet()

    below.forEach {
        val horizontalDivide = it.x - x
        resultSet.add(it.copy(x = x - horizontalDivide))
    }

    return resultSet
}

private fun fold(points: Set<Point>, command: Command): Set<Point> {
    return when (command) {
        is Command.FoldUp -> foldUp(points, command)
        is Command.FoldLeft -> foldLeft(points, command)
    }
}

private fun Collection<Point>.height(): Long = maxOf { it.y }.toLong()
private fun Collection<Point>.width(): Long = maxOf { it.x }.toLong()

private fun printSet(set: Set<Point>) {
    val height = set.height()
    println()
    for (i in 0..height) {
        print("|")
        repeat(set.width().toInt() + 1) {
            val symbol = if (Point(it, i.toInt()) in set) "█" else "."
            print(symbol)
        }
        print("|")
        println()
    }
    println()
}


fun main() {


    fun part1(input: List<String>): Int {
        val points = extractPoints(input)
        val commands = extractCommands(input)
        var buff = points
        buff = fold(buff, commands.first())
        return buff.size
    }

    fun part2(input: List<String>): Int {
        val points = extractPoints(input)
        val commands = extractCommands(input)
        var buff = points
        commands.forEach {
            buff = fold(buff, it)
        }
        printSet(buff)
        return buff.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    val part1Test = part1(testInput)

    println(part1Test.also { assert(it == 17) })

    val input = readInput("Day13")
    println(part1(input).also { assert(it == 710) })
    println(part2(input).also { assert(it == 97) })
}
