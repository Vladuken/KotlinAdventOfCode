package year2023.`08`

import readInput
import utils.lcm
import utils.printlnDebug

private const val CURRENT_DAY = "08"


private data class Node(
    val data: String,
    val left: String,
    val right: String,
)

private fun parseLineIntoNode(line: String): Node {
    val a = line.substring(0, 3)
    val b = line.substring(7, 10)
    val c = line.substring(12, 15)

    return Node(a, b, c).also {
        printlnDebug { it }
    }
}

private fun countStepsUntilFound(
    destinations: Map<String, Node>,
    directions: String,
    initNode: Node,
    isFound: (String) -> Boolean,
): Int {
    var directionsIterator = directions.iterator()
    printlnDebug { "INIT NODE : $initNode" }

    var count = 0
    var currentNode = initNode

    while (directionsIterator.hasNext() && isFound(currentNode.data).not()) {
        count++
        currentNode = step(
            direction = directionsIterator.next(),
            destinations = destinations,
            currentNode = currentNode,
        )

        printlnDebug { "NEW NODE : $currentNode" }
        if (isFound(currentNode.data)) {
            printlnDebug { "FOUND NODE: $currentNode" }
            return count
        }

        if (directionsIterator.hasNext().not()) {
            directionsIterator = directions.iterator()
        }
    }
    error("it should never arrive there")
}

private fun step(
    direction: Char,
    destinations: Map<String, Node>,
    currentNode: Node,
): Node {
    val nextKey = when (direction) {
        'R' -> currentNode.right
        'L' -> currentNode.left
        else -> error("Illegal step $direction")
    }
    return destinations[nextKey] ?: error("step: $nextKey is not found")
}

private fun prepareDestinations(input: List<String>): Map<String, Node> {
    val destinations = mutableMapOf<String, Node>()
    input
        .drop(2)
        .map { parseLineIntoNode(it) }
        .onEach { destinations[it.data] = it }
    return destinations
}

fun main() {

    fun part1(input: List<String>): Int {
        val destinations = prepareDestinations(input)
        val firstLine = input.first()
        return countStepsUntilFound(
            destinations = destinations,
            directions = firstLine,
            initNode = destinations["AAA"]!!,
            isFound = { it == "ZZZ" }
        )
    }

    fun part2(input: List<String>): Long {
        val destinations = prepareDestinations(input)
        val directionsLine = input.first()
        val allFoundCounts = destinations.values
            .filter { it.data.endsWith("A") }
            .map { node ->
                countStepsUntilFound(
                    destinations = destinations,
                    directions = directionsLine,
                    initNode = node,
                    isFound = { cur -> cur.endsWith("Z") }
                )
                    .also { printlnDebug { "node $node count = $it" } }
            }
            .map { it.toLong() }

        return allFoundCounts.fold(allFoundCounts.first()) { acc, i -> lcm(acc, i) }
    }

    // test if implementation meets criteria from the description, like:
    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 19951)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 16342438708751)
}
