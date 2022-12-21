package year2022.`21`

import readInput

@JvmInline
value class NodeTitle(val value: String)

enum class OperationType {
    PLUS, MINUS, MUL, DIV;

    companion object {
        fun from(string: String): OperationType {
            return when (string) {
                "*" -> MUL
                "/" -> DIV
                "+" -> PLUS
                "-" -> MINUS
                else -> error("!!!")
            }
        }
    }
}

sealed class Command {
    data class Value(val amount: Long) : Command()
    data class Operation(
        val leftNode: NodeTitle,
        val rightNode: NodeTitle,
        val type: OperationType
    ) : Command()
}

fun parseInput(input: List<String>): Map<NodeTitle, Command> {
    return input
        .map {
            val res = it.split(": ")
            val nodeTitle = NodeTitle(res[0])
            val split = res[1].split(" ")
            val rightSide = if (split.size == 1) {
                Command.Value(split.first().toLong())
            } else {
                val (left, operation, right) = split
                Command.Operation(
                    leftNode = NodeTitle(left),
                    rightNode = NodeTitle(right),
                    type = OperationType.from(operation)
                )
            }
            nodeTitle to rightSide
        }
        .associate { it }
}

fun evalCommand(
    command: Command,
    nodeMap: Map<NodeTitle, Command>
): Long {
    return when (command) {
        is Command.Operation -> {
            val left = nodeMap[command.leftNode]!!
            val right = nodeMap[command.rightNode]!!
            when (command.type) {
                OperationType.PLUS -> evalCommand(left, nodeMap) + evalCommand(right, nodeMap)
                OperationType.MINUS -> evalCommand(left, nodeMap) - evalCommand(right, nodeMap)
                OperationType.MUL -> evalCommand(left, nodeMap) * evalCommand(right, nodeMap)
                OperationType.DIV -> evalCommand(left, nodeMap) / evalCommand(right, nodeMap)
            }
        }
        is Command.Value -> command.amount
    }
}

fun buildExpression(
    command: Command,
    stringBuilder: StringBuilder,
    map: Map<NodeTitle, Command>
) {
    when (command) {
        is Command.Operation -> {
            val left = map[command.leftNode]!!
            val right = map[command.rightNode]!!
            stringBuilder.append("(")
            buildExpression(left, stringBuilder, map)
            when (command.type) {
                OperationType.PLUS -> stringBuilder.append("+")
                OperationType.MINUS -> stringBuilder.append("-")
                OperationType.MUL -> stringBuilder.append("*")
                OperationType.DIV -> stringBuilder.append("/")
            }
            buildExpression(right, stringBuilder, map)
            stringBuilder.append(")")
        }
        is Command.Value -> {
            if (command.amount == 0L) {
                stringBuilder.append("x")
            } else {
                stringBuilder.append(command.amount)
            }
        }
    }
}

fun main() {

    fun part1(input: List<String>): Long {
        val root = NodeTitle("root")
        val map = parseInput(input)
        val rootNode = map[root]!!
        return evalCommand(rootNode, map)
    }

    /**
     * Lazy solution - just build string and put it into online solver
     */
    fun part2(input: List<String>): String {
        val root = NodeTitle("root")
        val humn = NodeTitle("humn")

        val map = parseInput(input)
            .mapValues { (nodeTitle, command) ->
                if (nodeTitle == humn) {
                    Command.Value(0)
                } else {
                    command
                }
            }

        val expressionString = StringBuilder().also { stringBuilder ->
            val rootNode = map[root]!! as Command.Operation
            /**
             *  Build left side of expression
             */
            buildExpression(map[rootNode.leftNode]!!, stringBuilder, map)

            stringBuilder.append("=")

            /**
             *  Calculate and build right side of expression
             */
            val rightResult = evalCommand(map[rootNode.rightNode]!!, map)
            stringBuilder.append(rightResult.toString())
            stringBuilder.appendLine()
        }.toString()

        /**
         * Just Copy-paste result of expression and put in here:
         * https://www.mathpapa.com/equation-solver/
         */
        return expressionString
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    val part1Test = part1(testInput)

    println(part1Test)
    check(part1Test == 152L)

    val input = readInput("Day21")
    println(part1(input))
    println(part2(input))
}
