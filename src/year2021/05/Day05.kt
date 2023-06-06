package year2021.`05`

import readInput

private data class AdventPoint(
    val x: Int,
    val y: Int
) {
    override fun toString(): String = "($x,$y)"

    operator fun plus(other: AdventPoint): AdventPoint {
        return this.copy(
            x = x + other.x,
            y = y + other.y
        )
    }
}

private data class CustomLine(
    val from: AdventPoint,
    val to: AdventPoint
) {
    override fun toString(): String = "$from->$to"
}


fun main() {

    fun parse(input: List<String>): List<CustomLine> {
        return input.map {

            fun String.toPoint(): AdventPoint {
                val (x, y) = split(",").map { it.trimStart().trimEnd().toInt() }
                return AdventPoint(x, y)
            }

            val (first, second) = it.split("->")
            val fromPoint = first.toPoint()
            val toPoint = second.toPoint()

            CustomLine(fromPoint, toPoint)
        }
    }

    fun MutableMap<AdventPoint, Int>.incrementCounter(point: AdventPoint) {
        if (this.containsKey(point)) {
            this[point] = this[point]?.inc() ?: error("Impossible state")
        } else {
            this[point] = 1
        }
    }

    fun between(line: CustomLine): Set<AdventPoint> {
        val from = line.from
        val to = line.to
        val deltaPoint = AdventPoint(
            x = when {
                from.x > to.x -> -1
                from.x < to.x -> 1
                else -> error("ILLEGAL")
            },
            y = when {
                from.y > to.y -> -1
                from.y < to.y -> 1
                else -> error("ILLEGAL")
            }
        )

        val mutableSet = mutableSetOf<AdventPoint>()
        var currentPoint = from
        do {
            mutableSet.add(currentPoint)
            currentPoint += deltaPoint
        } while ((currentPoint != to))

        mutableSet.add(currentPoint)

        return mutableSet
    }

    fun processItem(
        line: CustomLine,
        mutableSet: MutableMap<AdventPoint, Int>,
        hasDiagonal: Boolean = false
    ) {

        val fromX = line.from.x
        val toX = line.to.x
        val fromY = line.from.y
        val toY = line.to.y

        when {
            fromX == toX -> (minOf(fromY, toY)..maxOf(fromY, toY)).forEach {
                mutableSet.incrementCounter(AdventPoint(fromX, it))
            }
            fromY == toY -> (minOf(fromX, toX)..maxOf(fromX, toX)).forEach {
                mutableSet.incrementCounter(AdventPoint(it, fromY))
            }
            else -> if (hasDiagonal) {
                between(line).forEach { mutableSet.incrementCounter(it) }
            } else {
                Unit
            }
        }
    }


    fun part1(input: List<String>): Int {
        val lines = parse(input)
        val mapOf = mutableMapOf<AdventPoint, Int>()
        lines.forEach { processItem(it, mapOf) }
        return mapOf.count { it.value >= 2 }
    }

    fun part2(input: List<String>): Int {
        val lines = parse(input)
        val mapOf = mutableMapOf<AdventPoint, Int>()
        lines.forEach { processItem(it, mapOf, true) }
        return mapOf.count { it.value >= 2 }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    val part1Test = part1(testInput)
    val part2Test = part2(testInput)

    println(part1Test)
    println(part2Test)


    check(part1Test == 5)
    check(part2Test == 12)

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
