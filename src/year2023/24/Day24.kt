package year2023.`24`

import readInput
import utils.printlnDebug
import java.math.BigDecimal

private const val CURRENT_DAY = "24"


private data class Point3D(
    val x: BigDecimal,
    val y: BigDecimal,
    val z: BigDecimal,
) {
    override fun toString(): String {
        return "[$x,$y,$z]"
    }
}

private data class PosNSpeed(
    val pos: Point3D,
    val speed: Point3D,
)

private fun parseLineInto(
    line: String
): PosNSpeed {
    val (first, second) = line.split("@").filter { it.isNotBlank() }
    val (px, py, pz) = first.split(",").map { it.trim() }
    val (dx, dy, dz) = second.split(",").map { it.trim() }
    return PosNSpeed(
        Point3D(px.toBigDecimal(), py.toBigDecimal(), pz.toBigDecimal()),
        Point3D(dx.toBigDecimal(), dy.toBigDecimal(), dz.toBigDecimal()),
    )
}

data class Solution(
    val x: BigDecimal,
    val y: BigDecimal,
    val t: BigDecimal,
)

private fun solve(
    pointNSpeed1: PosNSpeed,
    pointNSpeed2: PosNSpeed,
): Solution? {
    val px2 = pointNSpeed2.pos.x
    val py2 = pointNSpeed2.pos.y
    val dx2 = pointNSpeed2.speed.x
    val dy2 = pointNSpeed2.speed.y

    val px1 = pointNSpeed1.pos.x
    val py1 = pointNSpeed1.pos.y
    val dx1 = pointNSpeed1.speed.x
    val dy1 = pointNSpeed1.speed.y

    val y =
        runCatching { (px2 * dy1 * dy2 - px1 * dy1 * dy2 + py1 * dx1 * dy2 - py2 * dx2 * dy1) / (dx1 * dy2 - dx2 * dy1) }
            .onFailure { return null }
            .getOrThrow()

    val x = (y - py1) * dx1 / dy1 + px1

    val t = (x - px1) / dx1
    val t2 = (x - px2) / dx2

    return Solution(x, y, minOf(t, t2))
}


private fun findForPairs(items: List<PosNSpeed>): Sequence<Pair<Pair<PosNSpeed, PosNSpeed>, Solution?>> {
    return sequence {
        items.indices.forEach { i ->
            (i + 1..items.indices.last).forEach { j ->
                val first = items[i]
                val second = items[j]
                yield(first to second to solve(items[i], items[j]))
            }
        }
    }

}

fun main() {

    fun part1(input: List<String>, range: ClosedRange<BigDecimal>): Int {
        val items = input.map {
            parseLineInto(it)
        }

        val seq = findForPairs(items)
        val count = seq
            .filter { (inputPair, solution) ->
                val first = inputPair.first
                val second = inputPair.second
                printlnDebug { "Hailstone A:$first" }
                printlnDebug { "Hailstone B:$second" }
                printlnDebug { "Solution : $solution" }
                if (solution == null) return@filter false
                printlnDebug { "Is Inside: ${solution.x in range && solution.y in range}" }
                printlnDebug {}

                solution.t > BigDecimal.ZERO
            }
            .count { (_, solution) ->
                if (solution == null) return@count false
                solution.x in range && solution.y in range
            }

        return count
    }

    /**
     * https://sagecell.sagemath.org/
     */
    fun part2(input: List<String>): String {
        val items = input.map {
            parseLineInto(it)
        }

        val eqn = mutableListOf<String>()
        val t = mutableListOf<String>()
        val fn = mutableListOf<String>()
        eqn.add("var(\'xg yg zg dxg dyg dzg\')")
        items.take(4).forEachIndexed { i, posNSpeed ->
            eqn.add("var(\'t$i\')")
            t.add("t$i")
            for (d in 0 until 3) {
                val c = "xyz"[d]
                val pos = when (c) {
                    'x' -> posNSpeed.pos.x
                    'y' -> posNSpeed.pos.y
                    'z' -> posNSpeed.pos.z
                    else -> error("")
                }

                val speed = when (c) {
                    'x' -> posNSpeed.speed.x
                    'y' -> posNSpeed.speed.y
                    'z' -> posNSpeed.speed.z
                    else -> error("")
                }

                eqn.add("f$i$d = ${c}g+d${c}g*t${i} == ${pos} + ${speed}*t${i}") // todo return here
                fn.add("f$i$d")
            }


        }
        val fns = (fn.joinToString(",") { it })
        val ts = t.joinToString(",") { it }
        eqn.add("solve([${fns}], [xg,yg,zg,dxg,dyg,dzg,${ts}])")
        println(eqn.joinToString("\n") { it })

        return eqn.joinToString("\n") { it }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput, 7f.toBigDecimal()..27f.toBigDecimal())
    println(part1Test)
    check(part1Test == 2)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input, BigDecimal("200000000000000.0")..BigDecimal("400000000000000.0"))
    println(part1)
    check(part1 == 15593)

    // Part 2
    val part2 = part2(input)
    println(part2)
}
