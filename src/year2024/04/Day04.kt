package year2024.`04`

import readInput

private const val CURRENT_DAY = "04"


private data class Point(
    val x: Int,
    val y: Int,
) {
    override fun toString(): String = "[$x,$y]"
}

private fun parseMap(input: List<String>): Map<Point, String> {
    val mutableMap = mutableMapOf<Point, String>()
    input.forEachIndexed { y, line ->
        line.split("")
            .filter { it.isNotBlank() }
            .forEachIndexed { x, value ->
                mutableMap[Point(x, y)] = value
            }
    }
    return mutableMap
}


// region Part 1
private fun findAllPointsWithX(map: Map<Point, String>): Set<Point> {
    val items = map.filter { it.value == "X" }.keys
    return items
}

private fun xmasNeighboursRB(p: Point, map: Map<Point, String>): Boolean {
    val x = p
    val m = p.copy(x = p.x + 1, y = p.y + 1)
    val a = p.copy(x = p.x + 2, y = p.y + 2)
    val s = p.copy(x = p.x + 3, y = p.y + 3)

    return map[m] == "M" &&
            map[a] == "A" &&
            map[s] == "S"
}

private fun xmasNeighboursR(p: Point, map: Map<Point, String>): Boolean {
    val x = p
    val m = p.copy(x = p.x + 1, y = p.y)
    val a = p.copy(x = p.x + 2, y = p.y)
    val s = p.copy(x = p.x + 3, y = p.y)

    return map[m] == "M" &&
            map[a] == "A" &&
            map[s] == "S"
}

private fun xmasNeighboursB(p: Point, map: Map<Point, String>): Boolean {
    val x = p
    val m = p.copy(x = p.x, y = p.y + 1)
    val a = p.copy(x = p.x, y = p.y + 2)
    val s = p.copy(x = p.x, y = p.y + 3)

    return map[m] == "M" &&
            map[a] == "A" &&
            map[s] == "S"
}

private fun xmasNeighboursLB(p: Point, map: Map<Point, String>): Boolean {
    val x = p
    val m = p.copy(x = p.x - 1, y = p.y + 1)
    val a = p.copy(x = p.x - 2, y = p.y + 2)
    val s = p.copy(x = p.x - 3, y = p.y + 3)

    return map[m] == "M" &&
            map[a] == "A" &&
            map[s] == "S"
}

private fun xmasNeighboursL(p: Point, map: Map<Point, String>): Boolean {
    val x = p
    val m = p.copy(x = p.x - 1, y = p.y)
    val a = p.copy(x = p.x - 2, y = p.y)
    val s = p.copy(x = p.x - 3, y = p.y)

    return map[m] == "M" &&
            map[a] == "A" &&
            map[s] == "S"
}

private fun xmasNeighboursLU(p: Point, map: Map<Point, String>): Boolean {
    val x = p
    val m = p.copy(x = p.x - 1, y = p.y - 1)
    val a = p.copy(x = p.x - 2, y = p.y - 2)
    val s = p.copy(x = p.x - 3, y = p.y - 3)

    return map[m] == "M" &&
            map[a] == "A" &&
            map[s] == "S"
}

private fun xmasNeighboursU(p: Point, map: Map<Point, String>): Boolean {
    val x = p
    val m = p.copy(x = p.x, y = p.y - 1)
    val a = p.copy(x = p.x, y = p.y - 2)
    val s = p.copy(x = p.x, y = p.y - 3)

    return map[m] == "M" &&
            map[a] == "A" &&
            map[s] == "S"
}

private fun xmasNeighboursUR(p: Point, map: Map<Point, String>): Boolean {
    val x = p
    val m = p.copy(x = p.x + 1, y = p.y - 1)
    val a = p.copy(x = p.x + 2, y = p.y - 2)
    val s = p.copy(x = p.x + 3, y = p.y - 3)

    return map[m] == "M" &&
            map[a] == "A" &&
            map[s] == "S"
}
// endregion

// region Part 2
private fun findAllPointsWithA(map: Map<Point, String>): Set<Point> {
    val items = map.filter { it.value == "A" }.keys
    return items
}

// up left start
private fun masUL(p: Point, map: Map<Point, String>): Boolean {
    val m = p.copy(x = p.x - 1, y = p.y - 1)
    val a = p
    val s = p.copy(x = p.x + 1, y = p.y + 1)

    return map[m] == "M" &&
            map[a] == "A" &&
            map[s] == "S"
}

// bottom right start
private fun masBR(p: Point, map: Map<Point, String>): Boolean {
    val m = p.copy(x = p.x + 1, y = p.y + 1)
    val a = p
    val s = p.copy(x = p.x - 1, y = p.y - 1)

    return map[m] == "M" &&
            map[a] == "A" &&
            map[s] == "S"
}

// up right start
private fun masUR(p: Point, map: Map<Point, String>): Boolean {
    val m = p.copy(x = p.x + 1, y = p.y - 1)
    val a = p
    val s = p.copy(x = p.x - 1, y = p.y + 1)

    return map[m] == "M" &&
            map[a] == "A" &&
            map[s] == "S"
}

// bottom left start
private fun masBL(p: Point, map: Map<Point, String>): Boolean {
    val m = p.copy(x = p.x - 1, y = p.y + 1)
    val a = p
    val s = p.copy(x = p.x + 1, y = p.y - 1)

    return map[m] == "M" &&
            map[a] == "A" &&
            map[s] == "S"
}
// endregion

fun main() {

    fun part1(input: List<String>): Int {
        val map = parseMap(input)
        var count = 0
        findAllPointsWithX(map)
            .forEach { curP ->
                if (xmasNeighboursU(curP, map)) count++
                if (xmasNeighboursR(curP, map)) count++
                if (xmasNeighboursB(curP, map)) count++
                if (xmasNeighboursL(curP, map)) count++
                if (xmasNeighboursRB(curP, map)) count++
                if (xmasNeighboursLB(curP, map)) count++
                if (xmasNeighboursLU(curP, map)) count++
                if (xmasNeighboursUR(curP, map)) count++
            }
        return count
    }

    fun part2(input: List<String>): Int {
        val map = parseMap(input)
        var count = 0
        findAllPointsWithA(map)
            .forEach { curP ->
                if (masUL(curP, map) && masUR(curP, map)) count++
                if (masBR(curP, map) && masUR(curP, map)) count++
                if (masUL(curP, map) && masBL(curP, map)) count++
                if (masBR(curP, map) && masBL(curP, map)) count++

            }
        return count
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 18)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 9)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 2504)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 1923)
}
