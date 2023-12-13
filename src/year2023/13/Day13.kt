package year2023.`13`

import readInput
import utils.printDebug
import utils.printlnDebug

private const val CURRENT_DAY = "13"


private data class Point(
    val x: Int,
    val y: Int,
)

private fun parseIntoListsOfLists(input: List<String>): List<List<String>> {
    val mutableRes = mutableListOf<List<String>>()
    var currentList = mutableListOf<String>()
    input.forEach {
        if (it.isNotBlank()) {
            currentList.add(it)
        } else {
            mutableRes.add(currentList)
            currentList = mutableListOf()
        }
    }

    mutableRes.add(currentList)
    return mutableRes
}

private fun parseMap(input: List<String>): Set<Point> {
    val mutableMap = mutableSetOf<Point>()
    input.forEachIndexed { y, line ->
        line.split("")
            .filter { it.isNotBlank() }
            .forEachIndexed { x, value ->
                if (value == "#") {
                    mutableMap.add(Point(x, y))
                }
            }
    }
    return mutableMap
}

private fun Set<Point>.tryToFindMirrorsDifferentItems(): List<Set<Point>> {
    val newSetList: MutableList<Set<Point>> = mutableListOf()
    val maxX = maxOf { it.x }
    val maxY = maxOf { it.y }
    for (x in 0..maxX) {
        for (y in 0..maxY) {
            val point = Point(x, y)
            val newSet = if (this.contains(point)) {
                this - point
            } else {
                this + point
            }
            newSetList.add(newSet)
        }
    }

    check(newSetList.all { it.size in setOf(size + 1, size - 1) })
    return newSetList
}

private fun Set<Point>.tryToFindMirrors(prevAnswers: Set<Int>): Int? {
    val maxX = maxOf { it.x }

    val res: MutableSet<Int> = mutableSetOf()
    for (i in 0..maxX) {
        val mapToCheck = isMirroredHorizontally(i)
        val isValid = verify(maxX, this, mapToCheck)
//        if (isValid) println("X = $i isValid=$isValid")
        if (isValid) res.add(i)

    }

    return (res - prevAnswers).firstOrNull()
}

private fun Set<Point>.tryToFindVerticalMirrors(prevAnswers: Set<Int>): Int? {
    val maxY = maxOf { it.y }
    val res: MutableSet<Int> = mutableSetOf()

    for (i in 0..maxY) {
        val mapToCheck = isMirroredVertically(i)
        val isValid = verifyVertical(maxY, this, mapToCheck)
//        if (isValid) println("Y = $i isValid=$isValid")
        if (isValid) res.add(i)
    }

    return (res - prevAnswers).firstOrNull()
}


private fun verify(
    maxX: Int,
    points: Set<Point>,
    validPoints: Map<Point, Boolean>,
): Boolean {
    if (validPoints.values.distinct().size == 1) return false

    val list = mutableListOf<Boolean>()
    for (i in 0..maxX) {
        val allSameForX = points.filter { it.x == i }
            .map { validPoints[it]!! }
            .distinctBy { it }
            .size == 1

        list.add(points.filter { it.x == i }
            .map { validPoints[it]!! }
            .all { it })

        if (allSameForX.not()) return false
    }

    var count = 0
    list.reduce { acc, b ->
        if (acc == b) {
            b
        } else {
            count++
            b
        }
    }
    return count == 1
}

private fun verifyVertical(
    maxY: Int,
    points: Set<Point>,
    validPoints: Map<Point, Boolean>,
): Boolean {
    if (validPoints.values.distinct().size == 1) return false

    val list = mutableListOf<Boolean>()
    for (i in 0..maxY) {
        val allSameForY = points.filter { it.y == i }
            .map { validPoints[it]!! }
            .distinctBy { it }
            .size == 1

        list.add(points.filter { it.y == i }
            .map { validPoints[it]!! }
            .all { it })

        if (allSameForY.not()) return false
    }

    var count = 0
    list.reduce { acc, b ->
        if (acc == b) {
            b
        } else {
            count++
            b
        }
    }

    return count == 1
}

private fun Map<Point, Boolean>.height(): Long = maxOf { it.key.y }.toLong()
private fun Map<Point, Boolean>.width(): Long = maxOf { it.key.x }.toLong()


private fun printSet(set: Map<Point, Boolean>) {
    val height = set.height()
    printlnDebug {  }
    for (i in 0..height) {
        printDebug { "|" }
        repeat(set.width().toInt() + 1) {
            val symbol = when (set[Point(it, i.toInt())]) {
                true -> "1"
                false -> "0"
                null -> "."
            }
            printDebug { symbol }
        }
        printDebug { "|" }
        printlnDebug {  }
    }
    printlnDebug {  }
}


private fun Set<Point>.isMirroredHorizontally(currentX: Int): Map<Point, Boolean> {
    val result = associate {
        if (it.x >= currentX) {
            val xDelta = it.x - currentX
            val pointToSearch = it.copy(x = currentX - xDelta - 1)
            it to (pointToSearch in this)
        } else {
            val xDelta = currentX - it.x
            val pointToSearch = it.copy(x = currentX + xDelta - 1)
            it to (pointToSearch in this)
        }
    }
    return result
}

private fun Set<Point>.isMirroredVertically(currentY: Int): Map<Point, Boolean> {
    val result = associate {
        if (it.y >= currentY) {
            val xDelta = it.y - currentY
            val pointToSearch = it.copy(y = currentY - xDelta - 1)
            it to (pointToSearch in this)
        } else {
            val yDelta = currentY - it.y
            val pointToSearch = it.copy(y = currentY + yDelta - 1)
            it to (pointToSearch in this)
        }
    }

    return result
}

fun main() {

    fun part1(input: List<String>): Int {
        var horizontalCounter = 0
        var verticalCounter = 0
        parseIntoListsOfLists(input)
            .map { parseMap(it) }
            .forEach {
                printlnDebug { "NEW SET ARRIVED" }
                printSet(it.associateWith { true })

                val horizontal = it.tryToFindMirrors(emptySet())
                val vertical = it.tryToFindVerticalMirrors(emptySet())

                if (horizontal != null) {
                    horizontalCounter += horizontal
                }
                if (vertical != null) {
                    verticalCounter += vertical
                }
            }
        return horizontalCounter + verticalCounter * 100
    }

    fun part2(input: List<String>): Int {
        var horizontalCounter = 0
        var verticalCounter = 0
        parseIntoListsOfLists(input)
            .map { parseMap(it) }
            .forEach { ourSetOfPoints ->
                printlnDebug { "NEW SET ARRIVED" }
                val horizontal = ourSetOfPoints.tryToFindMirrors(emptySet())
                val vertical = ourSetOfPoints.tryToFindVerticalMirrors(emptySet())
                val resList = ourSetOfPoints.tryToFindMirrorsDifferentItems()
                resList
                    .asSequence()
                    .map {
                        val newMirror = it.tryToFindMirrors(setOfNotNull(horizontal))
                        val newVMirror = it.tryToFindVerticalMirrors(setOfNotNull(vertical))
                        Triple(it, newMirror, newVMirror)
                    }
                    .filter { (_, hMirror, vMirror) ->
                        hMirror != null || vMirror != null
                    }
                    .filter { (_, hMirror, vMirror) ->
                        hMirror != horizontal || vMirror != vertical
                    }
                    .distinctBy { (_, hMirror, vMirror) -> hMirror to vMirror }
                    .onEach { (_, hMirror, vMirror) ->
                        printlnDebug { "H: $horizontal V:$vertical NEW_H:$hMirror NEW_V:$vMirror" }
                        if (hMirror != null && hMirror != horizontal) {
                            horizontalCounter += hMirror
                        }
                        if (vMirror != null && vMirror != vertical) {
                            verticalCounter += vMirror
                        }
                    }
                    .toList()
                    .ifEmpty {
                        printlnDebug { "NEW SET IS EMPTY - CHECK THIS" }
                        printSet(ourSetOfPoints.associateWith { true })
                    }

            }
        return horizontalCounter + verticalCounter * 100
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 405)// + 700)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 400)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 34993)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 29341)
}
