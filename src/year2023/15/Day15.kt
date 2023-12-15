package year2023.`15`

import readInput
import utils.printlnDebug

private const val CURRENT_DAY = "15"


data class Label(
    val data: String,
    val operation: String,
    val number: Int?,
) {
    override fun toString(): String = "[$data $number]"
}

private fun parseLineInto(
    line: String
): List<String> = line.split(",").filter { it.isNotBlank() }

// HASH Algorithm:
// Determine the ASCII code for the current character of the string.
// Increase the current value by the ASCII code you just determined.
// Set the current value to itself multiplied by 17.
// Set the current value to the remainder of dividing itself by 256.
private fun findHash(line: String): Int {
    var hash = 0
    line.forEach {
        hash += it.code
        hash *= 17
        hash %= 256
    }
    return hash
}

private fun parseLineIntoLabels(
    line: String
): List<Label> {
    return line.split(",")
        .filter { it.isNotBlank() }
        .map {
            if (it.contains("=")) {
                val list = it.split("=")
                Label(
                    data = list.first(),
                    operation = "=",
                    number = list[1].toInt(),
                )
            } else if (it.contains("-")) {
                val list = it.split("-")
                Label(
                    data = list.first(),
                    operation = "-",
                    number = null,
                )
            } else error("Illegal Line $it")
        }
}


class MyHashMap {
    private val hashMap = List(256) { mutableListOf<Label>() }
    fun add(label: Label) {
        val hash = findHash(label.data)

        hashMap[hash].replaceAll { currentLabel ->
            label.takeIf { it.data == currentLabel.data } ?: currentLabel
        }

        if (hashMap[hash].none { it.data == label.data }) {
            hashMap[hash].add(label)
        }
    }

    fun remove(label: Label) {
        val hash = findHash(label.data)
        hashMap[hash].removeAll { it.data == label.data }
    }

    override fun toString(): String {
        var boxIndex = 0
        val string = hashMap.joinToString("") {
            boxIndex++
            if (it.isNotEmpty()) {
                "Box $boxIndex :$it\n"
            } else {
                ""
            }
        }
        return string
    }

    fun focusPower(): Int {
        var sum = 0
        hashMap.forEachIndexed { index, labels ->
            var partialSum = 0
            labels.forEachIndexed { indexInLine, label ->
                val resSum = label.number!! * (index + 1) * (indexInLine + 1)
                partialSum += resSum
            }
            sum += partialSum
        }
        return sum
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        val hashes = parseLineInto(input.first())
            .map { findHash(it) }
        printlnDebug { hashes }
        return hashes.sum()
    }

    fun part2(input: List<String>): Int {
        val mineHashMap = MyHashMap()
        parseLineIntoLabels(input.first())
            .forEach {
                when (it.operation) {
                    "=" -> mineHashMap.add(it)
                    "-" -> mineHashMap.remove(it)
                }
            }
        printlnDebug { mineHashMap }
        return mineHashMap.focusPower()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 1320)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 145)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 507769)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 269747)
}
