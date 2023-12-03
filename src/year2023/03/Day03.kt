package year2023.`03`

import readInput

private const val CURRENT_DAY = "03"

private data class Point(
    val x: Int,
    val y: Int,
)


private data class NumberDataStore(
    val digits: List<DataPoint>,
) {

    fun digitRepresentation(): Int {
        return digits.joinToString("") { it.data }.toInt()
    }

    fun isNearAnyOfSymbols(
        symbols: Collection<Point>,
    ): Boolean {
        return digits.any { dataPoint ->
            dataPoint.position.neighbours().any { neighbour ->
                symbols.contains(neighbour)
            }
        }
    }

    override fun toString(): String {
        return "NumberDataStore[" + digits.joinToString(separator = "") { it.data } + "]"
    }
}

private data class DataPoint(
    val data: String,
    val position: Point,
)

private fun Point.neighbours(): Set<Point> {
    return setOf(
        Point(x + 1, y),
        Point(x - 1, y),
        Point(x, y + 1),
        Point(x, y - 1),
        Point(x + 1, y + 1),
        Point(x - 1, y - 1),
        Point(x + 1, y - 1),
        Point(x - 1, y + 1),
    )
}

private data class SymbolDataStore(
    val symbolPoint: DataPoint,
) {
    override fun toString(): String {
        return "SymbolDataStore[" + symbolPoint.data + "]"
    }
}

private fun parseIntoLineOfDataPoints(line: String, y: Int): List<DataPoint> {
    val allDataPoints = mutableListOf<DataPoint>()
    line.forEachIndexed { x, c ->
        if (c.isDigit()) {
            allDataPoints.add(DataPoint(c.toString(), Point(x, y)))
        }
        if (c.isDigit().not() && c != '.') {
            allDataPoints.add(DataPoint(c.toString(), Point(x, y)))
        }
    }

    return allDataPoints
}


private const val UNINITIALISED = -1
private fun List<DataPoint>.toListOfNumbers(): List<NumberDataStore> {
    var prevX = UNINITIALISED

    val resList = mutableListOf<NumberDataStore>()

    val buffList = mutableListOf<DataPoint>()
    filter { it.data.toIntOrNull() != null }
        .forEach { dataPoint ->
            val currentPos = dataPoint.position.x

            prevX = if (prevX == UNINITIALISED) {
                buffList.add(dataPoint)
                currentPos
            } else if (prevX + 1 == currentPos) {
                buffList.add(dataPoint)
                currentPos
            } else {
                resList.add(NumberDataStore(buffList.toList()))
                buffList.clear()
                buffList.add(dataPoint)
                currentPos
            }
        }

    if (buffList.isNotEmpty()) {
        resList.add(NumberDataStore(buffList.toList()))
    }

    return resList
}

private fun List<DataPoint>.toListOfSymbols(): List<SymbolDataStore> {
    val resList = mutableListOf<SymbolDataStore>()
    filter { it.data.toIntOrNull() == null }
        .forEach { dataPoint ->
            resList.add(SymbolDataStore(dataPoint))
        }
    return resList
}

private fun prepareOutput(
    input: List<String>
): Pair<List<NumberDataStore>, Set<SymbolDataStore>> {
    val allNumbers = mutableListOf<NumberDataStore>()
    val allSymbols = mutableSetOf<SymbolDataStore>()
    input.mapIndexed { y, s ->
        parseIntoLineOfDataPoints(s, y)
    }
        .map { dataPoints ->
            dataPoints.toListOfNumbers() to dataPoints.toListOfSymbols()
        }
        .forEach { (numbers, symbols) ->
            allNumbers.addAll(numbers)
            allSymbols.addAll(symbols)
        }

    return allNumbers to allSymbols
}

fun main() {

    fun part1(input: List<String>): Int {
        val (allNumbers, allSymbols) = prepareOutput(input)

        val symbolPointsSet = allSymbols.map { it.symbolPoint.position }.toSet()
        return allNumbers
            .filter { numberDataStore -> numberDataStore.isNearAnyOfSymbols(symbolPointsSet) }
            .sumOf { numberDataStore -> numberDataStore.digitRepresentation() }
    }

    fun part2(input: List<String>): Int {
        val (allNumbers, allSymbols) = prepareOutput(input)

        val symbolPointsSet = allSymbols
            .filter { it.symbolPoint.data == "*" }
            .map { it.symbolPoint.position }
            .toSet()

        val resSum = symbolPointsSet.map { symbolPoint ->
            allNumbers.filter { numberDataStore ->
                numberDataStore.isNearAnyOfSymbols(setOf(symbolPoint))
            }
        }
            .filter { it.size == 2 }
            .sumOf { gearList ->
                gearList.fold<NumberDataStore, Int>(1) { prod, item -> prod * item.digitRepresentation() }
            }


        return resSum
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")
    val part1Test = part1(testInput)

    println(part1Test)
    check(part1Test == 4361)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 467835)


    val input = readInput("Day$CURRENT_DAY")

    // Part 1

    // 562922 - false
    val part1 = part1(input)
    println(part1)
    check(part1 == 550934)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 81997870)
}
