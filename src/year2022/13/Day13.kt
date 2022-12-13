package year2022.`13`

import java.util.Stack
import readInput


sealed class Packet : Comparable<Packet> {
    override fun compareTo(other: Packet): Int {
        return when (compare(this, other)) {
            true -> -1
            false -> 1
            null -> 0
        }
    }

    data class Number(val value: Int) : Packet() {
        override fun toString(): String {
            return "$value"
        }
    }

    data class PacketList(val list: List<Packet>) : Packet() {
        override fun toString(): String {
            return list.joinToString(",", "[", "]")
        }
    }

    private fun compare(left: Packet, right: Packet): Boolean? {
        return when (left) {
            is PacketList -> {
                when (right) {
                    is PacketList -> compareLists(left, right)
                    is Number -> compareLists(left, PacketList(listOf(right)))
                }
            }
            is Number -> {
                when (right) {
                    is PacketList -> compareLists(PacketList(listOf(left)), right)
                    is Number -> compareInts(left, right)
                }
            }
        }
    }

    private fun compareLists(left: PacketList, right: PacketList): Boolean? {
        val leftIterator = left.list.iterator()
        val rightIterator = right.list.iterator()

        while (leftIterator.hasNext() && rightIterator.hasNext()) {
            val leftItem = leftIterator.next()
            val rightItem = rightIterator.next()
            val compareResult = compare(leftItem, rightItem)
            compareResult?.let { return it }
        }

        if (leftIterator.hasNext()) return false
        if (rightIterator.hasNext()) return true
        return null
    }

    private fun compareInts(left: Number, right: Number): Boolean? {
        return when {
            left.value < right.value -> true
            left.value > right.value -> false
            left.value == right.value -> null
            else -> error("!!")
        }
    }
}

fun parseInput(string: String): Packet.PacketList {
    val resList = mutableListOf<Packet>()
    val parentStacks = Stack<MutableList<Packet>>()

    var currentList = resList
    var inputNumberString = ""

    string.forEachIndexed { _, character ->
        when {
            character == '[' -> {
                val list = mutableListOf<Packet>()
                currentList.add(Packet.PacketList(list))
                parentStacks.push(currentList)
                currentList = list
            }
            character == ']' -> {
                if (inputNumberString.isEmpty()) {
                    if (currentList.isNotEmpty()) {
                        currentList = parentStacks.pop()
                    }
                } else {
                    currentList.add(Packet.Number(inputNumberString.toInt()))
                }
                inputNumberString = ""
            }
            character == ',' -> {
                if (inputNumberString.isEmpty()) {
                    currentList = parentStacks.pop()
                } else {
                    currentList.add(Packet.Number(inputNumberString.toInt()))
                }
                inputNumberString = ""
            }
            character.isDigit() -> {
                inputNumberString += character
            }
        }
    }

    return Packet.PacketList((resList.first() as Packet.PacketList).list)
}

fun main() {

    fun part1(input: List<String>): Int {
        val result = input
            .asSequence()
            .windowed(2, 3)
            .map { (leftString, rightString) -> parseInput(leftString) to parseInput(rightString) }
            .mapIndexed { index, (left, right) -> (index + 1) to (left < right) }
            .filter { (_, isCorrect) -> isCorrect ?: false }
            .sumOf { it.first }

        return result
    }

    fun part2(input: List<String>): Int {
        val first = parseInput("[[2]]")
        val second = parseInput("[[6]]")

        val result = input.windowed(2, 3)
            .flatten()
            .map { parseInput(it) }
            .let { it + listOf(first, second) }
            .sorted()

        val i = result.indexOf(first) + 1
        val j = result.indexOf(second) + 1

        return i * j
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    val part1Test = part1(testInput)

    println(part1Test)
    check(part1Test == 13)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}
