package year2022.`09`

import kotlin.math.abs
import readInput

data class Position(
    val x: Int,
    val y: Int
)

data class Command(
    val direction: Direction,
    val amount: Int
) {
    enum class Direction {
        UP, DOWN, LEFT, RIGHT;

        companion object {
            fun from(string: String): Direction {
                return when (string) {
                    "R" -> RIGHT
                    "L" -> LEFT
                    "D" -> DOWN
                    "U" -> UP
                    else -> error("!!!")
                }
            }
        }
    }
}

fun Position.move(direction: Command.Direction): Position {
    return when (direction) {
        Command.Direction.UP -> copy(y = y + 1)
        Command.Direction.DOWN -> copy(y = y - 1)
        Command.Direction.LEFT -> copy(x = x - 1)
        Command.Direction.RIGHT -> copy(x = x + 1)
    }
}

fun Position.follow(head: Position): Position {
    val tailX = x
    val tailY = y

    val dX = head.x - tailX
    val dY = head.y - tailY

    val position = when {
        abs(dX) <= 1 && abs(dY) <= 1 -> Position(tailX, tailY)
        abs(dX) == 2 && abs(dY) == 2 -> Position(tailX + dX / 2, tailY + dY / 2)
        abs(dX) == 2 && abs(dY) == 0 -> Position(tailX + dX / 2, tailY)
        abs(dX) == 2 && abs(dY) == 1 -> Position(tailX + dX / 2, tailY + dY)
        abs(dX) == 0 && abs(dY) == 2 -> Position(tailX, tailY + dY / 2)
        abs(dX) == 1 && abs(dY) == 2 -> Position(tailX + dX, tailY + dY / 2)
        else -> error("!! $dX $dY")
    }

    return position
}


fun main() {

    fun parseInput(input: List<String>): List<Command> {
        return input.map {
            val (direction, amount) = it.split(" ")
            Command(Command.Direction.from(direction), amount.toInt())
        }
    }

    fun part1(input: List<String>): Int {
        val visited: MutableSet<Position> = mutableSetOf()
        var head = Position(0, 0)
        var tail = Position(0, 0)

        parseInput(input)
            .forEach { command ->
                repeat(command.amount) {
                    head = head.move(command.direction)
                    tail = tail.follow(head)
                    visited.add(tail)
                }
            }
        return visited.size
    }

    fun part2(input: List<String>): Int {
        val visited: MutableSet<Position> = mutableSetOf()
        val knotPositions = MutableList(10) { Position(0, 0) }

        parseInput(input)
            .forEach { command ->
                repeat(command.amount) {
                    knotPositions.forEachIndexed { index, _ ->
                        knotPositions[index] = if (index == 0) {
                            knotPositions[0].move(command.direction)
                        } else {
                            knotPositions[index].follow(knotPositions[index - 1])
                        }
                    }
                    visited.add(knotPositions.last())
                }
            }
        return visited.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    val part1Test = part2(testInput)

    println(part1Test)
    check(part1Test == 36)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
