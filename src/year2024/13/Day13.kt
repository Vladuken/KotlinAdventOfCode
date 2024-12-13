package year2024.`13`

import readInput

private const val CURRENT_DAY = "13"


private data class Point(
    val x: Long,
    val y: Long,
) {

    override fun toString(): String {
        return "{$x,$y}"
    }
}

private data class Configuration(
    val buttonA: Point,
    val buttonB: Point,
    val prize: Point,
) {
    override fun toString(): String {
        return "{A:$buttonA, B:$buttonB, $prize}"
    }
}

private fun String.toButton(): Point {
    val items = this.split("+", ",").mapNotNull {
        it.toLongOrNull()
    }
    return Point(
        items[0],
        items[1],
    )
}

private fun String.toPrize(part2: Boolean = false): Point {
    val items = this.split("=", ",").mapNotNull {
        it.toIntOrNull()
    }
    return Point(
        items[0] + if (part2) 10000000000000L else 0L,
        items[1] + if (part2) 10000000000000L else 0L,
    )
}

private fun parseIntoButton(input: List<String>, part2: Boolean = false): List<Configuration> {
    return input.windowed(4, 4, true).map { conf ->
        Configuration(
            buttonA = conf[0].toButton(),
            buttonB = conf[1].toButton(),
            prize = conf[2].toPrize(part2),
        )
    }
}

private fun Configuration.minAmountToWin2(): Long? {
    val bClick = (prize.y * buttonA.x - prize.x * buttonA.y) / (buttonA.x * buttonB.y - buttonB.x * buttonA.y)
    val aClick = (buttonB.y * prize.x - buttonB.x * prize.y) / (buttonB.y * buttonA.x - buttonB.x * buttonA.y)
    println("[$bClick,$aClick]")

    val resPoint = Point(
        buttonA.x * aClick + buttonB.x * bClick,
        buttonA.y * aClick + buttonB.y * bClick,
    )
    println(resPoint)
    return if (resPoint == prize) {
        aClick * 3L + bClick * 1L
    } else {
        null
    }
}

fun main() {

    fun part1(input: List<String>): Long {
        val data = parseIntoButton(input)
            .mapNotNull { it.minAmountToWin2() }
        return data.sum()
    }

    fun part2(input: List<String>): Long {
        val data = parseIntoButton(input, true)
            .mapNotNull { it.minAmountToWin2() }
        return data.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 480L)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 37680L)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 87550094242995L)
}
