package year2024.`15`

import readInput
import utils.printlnDebug
import utils.printDebug

private const val CURRENT_DAY = "15"


private data class Point(
    val x: Int,
    val y: Int,
) {
    fun moveUp(): Point = copy(y = y - 1)
    fun moveDown(): Point = copy(y = y + 1)
    fun moveLeft(): Point = copy(x = x - 1)
    fun moveRight(): Point = copy(x = x + 1)

    fun moveWithDirection(direction: Direction): Point {
        return when (direction) {
            Direction.LEFT -> moveLeft()
            Direction.RIGHT -> moveRight()
            Direction.DOWN -> moveDown()
            Direction.TOP -> moveUp()
        }
    }

    override fun toString(): String {
        return "[$x,$y]"
    }
}

private enum class Direction { LEFT, RIGHT, DOWN, TOP }

private sealed class BlockType {
    abstract val position: Point

    data class Robot(
        override val position: Point,
    ) : BlockType() {
        override fun toString(): String = "@"
    }

    data class Box(
        override val position: Point,
    ) : BlockType() {
        override fun toString(): String = "O"
    }

    sealed class Box2 : BlockType() {
        data class Left(
            override val position: Point,
        ) : Box2() {
            override fun toString(): String = "["
        }

        data class Right(
            override val position: Point,
        ) : Box2() {
            override fun toString(): String = "]"
        }

    }

    data class Wall(
        override val position: Point,
    ) : BlockType() {
        override fun toString(): String = "#"
    }

    data class Empty(
        override val position: Point,
    ) : BlockType() {
        override fun toString(): String = "."
    }

}


private fun prepareMapV2(input: List<String>): List<String> {
    return input.map { line ->
        line.toCharArray().joinToString("", "", "") { char ->
            when (char) {
                '#' -> "##"
                'O' -> "[]"
                '.' -> ".."
                '@' -> "@."
                else -> error("AAAA $char")
            }
        }
    }
}

private fun parseMap(input: List<String>): Map<Point, BlockType> {
    val mutableMap = mutableMapOf<Point, BlockType>()
    input.forEachIndexed { y, line ->
        line.split("")
            .filter { it.isNotBlank() }
            .forEachIndexed { x, value ->
                val point = Point(x, y)
                val result = when (value) {
                    "#" -> BlockType.Wall(point)
                    "O" -> BlockType.Box(point)
                    "." -> BlockType.Empty(point)
                    "@" -> BlockType.Robot(point)
                    "[" -> BlockType.Box2.Left(point)
                    "]" -> BlockType.Box2.Right(point)
                    else -> error("AAAA $value")
                }
                mutableMap[point] = result
            }
    }
    return mutableMap
}

private fun printMap(initialMap: Map<Point, BlockType>) {
    val number = initialMap.maxOf { it.key.x + 1 }
    val sortedPoints = initialMap.keys.toList()
        .sortedWith(compareBy({ it.y }, { it.x }))

    sortedPoints.forEachIndexed { index, point ->
        if (index % number == 0) {
            printlnDebug { }
        }
        printDebug { "" + initialMap[point] }
    }
    printlnDebug { }
}

private fun parseLineInto(
    line: String,
): List<Direction> {
    //<^^>>>vv<v>>v<<
    return line.map {
        when (it) {
            '<' -> Direction.LEFT
            '^' -> Direction.TOP
            '>' -> Direction.RIGHT
            'v' -> Direction.DOWN
            else -> error("")
        }
    }
}


private fun performMovement(
    input: Map<Point, BlockType>,
    movement: Direction,
    shouldPrintDebug: Boolean,
): Map<Point, BlockType> {
    val robotPosition =
        input.keys.find { input[it] is BlockType.Robot } ?: error("impossible state robot should be present")

    val (mapOfLine, canMove) = findAllLineOfItems(input, movement, robotPosition, shouldPrintDebug)

    val resultMap = input.toMutableMap()
    if (canMove) {
        mapOfLine.forEach {
            resultMap[it] = BlockType.Empty(it)
        }
        mapOfLine.forEach {
            val itemToMove = input[it]!!
            val newPosition = it.moveWithDirection(direction = movement)
            resultMap[newPosition] = itemToMove

            if (itemToMove is BlockType.Robot) {
                resultMap[it] = BlockType.Empty(it)
            }
        }
    }

    return resultMap
}

/**
 * true - can be shifted
 */
private fun findAllLineOfItems(
    input: Map<Point, BlockType>,
    movement: Direction,
    initPosition: Point,
    shouldPrintDebug: Boolean,
): Pair<List<Point>, Boolean> {
    val resultList = mutableListOf<Point>()
    var currentPosition = initPosition

    while (input[currentPosition] != null &&
        input[currentPosition] !is BlockType.Wall
        && input[currentPosition] !is BlockType.Empty
    ) {
        resultList.add(currentPosition)

        val nextPosition = when (movement) {
            Direction.LEFT -> currentPosition.moveLeft()
            Direction.RIGHT -> currentPosition.moveRight()
            Direction.DOWN -> {
                val downPos = currentPosition.moveDown()
                val item = input[downPos]
                when (item) {
                    is BlockType.Box2.Left -> {
                        val (checkBottomList, checkBottomCan) = findAllLineOfItems(
                            input,
                            movement,
                            downPos.moveRight(),
                            shouldPrintDebug
                        )

                        resultList.addAll(checkBottomList)
                    }

                    is BlockType.Box2.Right -> {
                        val (checkBottomList, checkBottomCan) = findAllLineOfItems(
                            input,
                            movement,
                            downPos.moveLeft(),
                            shouldPrintDebug
                        )

                        resultList.addAll(checkBottomList)
                    }

                    else -> {}
                }

                downPos
            }

            Direction.TOP -> {
                val topPos = currentPosition.moveUp()
                val item = input[topPos]
                when (item) {
                    is BlockType.Box2.Left -> {
                        val (checkTopList, checkTopCan) = findAllLineOfItems(
                            input,
                            movement,
                            topPos.moveRight(),
                            shouldPrintDebug,
                        )
                        resultList.addAll(checkTopList)
                    }

                    is BlockType.Box2.Right -> {
                        val (checkTopList, checkTopCan) = findAllLineOfItems(
                            input,
                            movement,
                            topPos.moveLeft(),
                            shouldPrintDebug,
                        )

                        resultList.addAll(checkTopList)
                    }

                    else -> {}
                }
                topPos
            }
        }
        currentPosition = nextPosition
    }

    val canMove = input[currentPosition] is BlockType.Empty
    val resultCanMove = resultList.none { input[it.moveWithDirection(movement)] is BlockType.Wall }
    if (shouldPrintDebug) {
        val currentPrintItems = resultList.map { input[it] }
        val nextPrintItems = resultList.map { input[it.moveWithDirection(movement)] }
        println("LIST: $resultList CAN MOVE :$resultCanMove \nItems: ${currentPrintItems} \nNextItems:$nextPrintItems")
        println()
    }
    return resultList to resultCanMove
}

private fun calculateGpsResult(input: Map<Point, BlockType>): Int {
    var result = 0
    input.keys.forEach {
        val currentItem = input[it]!!
        if (currentItem is BlockType.Box || currentItem is BlockType.Box2.Left) {
            result += 100 * it.y + it.x
        }
    }
    return result
}


fun main() {

    fun part1(input: List<String>): Int {
        val blocks = input.takeWhile { it.isNotBlank() }
        val blocksMap = parseMap(blocks)
        val movementsLine = input.reversed().takeWhile { it.isNotBlank() }.reversed().joinToString("", "", "")
        val movements = parseLineInto(movementsLine)

        var currentStateMap = blocksMap
        movements.forEach {
            currentStateMap = performMovement(currentStateMap, it, false)
        }


        return calculateGpsResult(currentStateMap)
    }

    fun part2(input: List<String>): Int {
        val blocks = input.takeWhile { it.isNotBlank() }
        val blocksMap = parseMap(prepareMapV2(blocks))
        printMap(blocksMap)
        val movementsLine = input.reversed().takeWhile { it.isNotBlank() }.reversed().joinToString("", "", "")
        val movements = parseLineInto(movementsLine)

        var currentStateMap = blocksMap
        movements.forEachIndexed { index, direction ->
            currentStateMap = performMovement(currentStateMap, direction, false)
        }

        println()
        println("LAST")
        printMap(currentStateMap)

        //1445099


        return calculateGpsResult(currentStateMap)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 10092)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 9021)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 1478649)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 1495455)
}
