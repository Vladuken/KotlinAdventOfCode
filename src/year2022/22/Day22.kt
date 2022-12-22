package year2022.`22`

import kotlin.math.roundToInt
import kotlin.math.sqrt
import readInput

/**
 * Point with x,y and side on cube
 */
data class Point(
    val x: Int,
    val y: Int,
) {
    var side: Int = -1

    fun heuristic(): Int = x + y

    fun copyWithSide(
        x: Int = this.x,
        y: Int = this.y
    ): Point {
        return copy(
            x = x,
            y = y
        ).also { it.side = side }
    }


    operator fun minus(other: Point): Point {
        return copyWithSide(
            x = x - other.x,
            y = y - other.y
        )
    }

    operator fun plus(other: Point): Point {
        return copyWithSide(
            x = x + other.x,
            y = y + other.y
        )
    }

    override fun toString(): String {
        return "Point(x=$x, y=$y, side=$side)"
    }
}

sealed class Cell {
    abstract val point: Point

    data class Empty(override val point: Point) : Cell()
    data class Wall(override val point: Point) : Cell()

    companion object {
        fun from(
            x: Int,
            y: Int,
            string: String
        ): Cell? {
            return when (string) {
                " " -> null
                "." -> Empty(Point(x, y))
                "#" -> Wall(Point(x, y))
                else -> error("!! $string")
            }
        }

        fun from(
            cell: Cell,
            side: Int,
        ): Cell {
            return when (cell) {
                is Empty -> cell.copy(cell.point.copyWithSide().also { it.side = side })
                is Wall -> cell.copy(cell.point.copyWithSide().also { it.side = side })
            }
        }
    }
}

/**
 * Enum for direction for moving point on map
 */
enum class Direction {
    RIGHT, LEFT, TOP, DOWN;
}


/**
 * Type of rotation command
 */
enum class RotationType {
    R, L
}

/**
 * Command representing either Move or Rotation
 */
sealed class Command {

    data class Move(val amount: Int) : Command() {
        override fun toString(): String {
            return this.amount.toString()
        }
    }

    data class Rotation(val rotation: RotationType) : Command() {
        override fun toString(): String {
            return when (this.rotation) {
                RotationType.R -> "R"
                RotationType.L -> "L"
            }
        }
    }
}

/**
 * Calculate number of side on the cube
 */
fun Map<Point, Cell>.sideSize(): Int = sqrt(size / 6f).roundToInt()
fun List<Cell>.sideSize(): Int = sqrt(size / 6f).roundToInt()

/**
 * Return map of Point to Cell
 */
fun parseInput(input: List<String>): Map<Point, Cell> {

    val listOfListOfCells = input.mapIndexed { y, line ->
        line.split("")
            .filter { it.isNotEmpty() }
            .mapIndexedNotNull { x, s ->
                Cell.from(x, y, s)
            }
    }
        .flatten()

    val sideOfSquare = listOfListOfCells.sideSize()
    val cellsWithSides = listOfListOfCells
        .map { cell ->
            val xSide = cell.point.x / sideOfSquare
            val ySide = cell.point.y / sideOfSquare
            Cell.from(cell, xSide + ySide * 6)
        }

    val groupedIndexes = cellsWithSides
        .groupBy { it.point.side }
        .keys
        .sorted()

    val resResMap = cellsWithSides.map { cell ->
        val newSide = groupedIndexes.indexOf(cell.point.side) + 1
        Cell.from(cell = cell, side = newSide)
    }

    return resResMap.associateBy { it.point }
}

/**
 * Map line with commands to list of [Command]
 */
fun parseInputCommands(input: String): List<Command> {
    val mutableList = mutableListOf<Command>()

    var currentNumber = ""
    input.forEach { char ->
        when {
            char.isDigit() -> currentNumber += char
            char == 'R' -> {
                mutableList.add(Command.Move(currentNumber.toInt()))
                mutableList.add(Command.Rotation(RotationType.R))
                currentNumber = ""
            }
            char == 'L' -> {
                mutableList.add(Command.Move(currentNumber.toInt()))
                mutableList.add(Command.Rotation(RotationType.L))
                currentNumber = ""
            }
        }
    }
    if (currentNumber.isNotEmpty()) {
        mutableList.add(Command.Move(currentNumber.toInt()))
    }
    return mutableList
}

/**
 * Map [Direction] to new [RotationType] and provide new [Direction]
 */
fun Direction.applyRotation(rotation: RotationType): Direction {
    return when (rotation) {
        RotationType.R -> when (this) {
            Direction.RIGHT -> Direction.DOWN
            Direction.LEFT -> Direction.TOP
            Direction.TOP -> Direction.RIGHT
            Direction.DOWN -> Direction.LEFT
        }
        RotationType.L -> when (this) {
            Direction.RIGHT -> Direction.TOP
            Direction.LEFT -> Direction.DOWN
            Direction.TOP -> Direction.LEFT
            Direction.DOWN -> Direction.RIGHT
        }
    }
}

/**
 * Create new point, moved in new direction
 */
fun Point.moveInDirection(
    currentDirection: Direction,
): Point {
    return when (currentDirection) {
        Direction.RIGHT -> copyWithSide(x = x + 1)
        Direction.LEFT -> copyWithSide(x = x - 1)
        Direction.TOP -> copyWithSide(y = y - 1)
        Direction.DOWN -> copyWithSide(y = y + 1)
    }
}

private fun Point.applyCommand(
    isTest: Boolean,
    isCube: Boolean,
    initialDirection: Direction,
    command: Command.Move,
    cells: Map<Point, Cell>
): Pair<Point, Direction> {
    var resultPoint = this
    var currentDirection: Direction = initialDirection
    repeat(command.amount) {
        val nextPoint = resultPoint.moveInDirection(currentDirection)
        if (cells.containsKey(nextPoint)) {
            when (cells[nextPoint]!!) {
                is Cell.Empty -> resultPoint = cells[nextPoint]!!.point
                is Cell.Wall -> return@repeat
            }
        } else {
            val prevPoint = cells[resultPoint]!!.point
            val (newPoint: Point, newDir) = if (isCube) {
                if (isTest) {
                    findNextPointOnCubeTestData(
                        cells = cells,
                        initialPoint = prevPoint,
                        initialDirection = currentDirection
                    )
                } else {
                    findNextPointOnCubeRealData(
                        cells = cells,
                        initialPoint = prevPoint,
                        initialDirection = currentDirection
                    )
                }
            } else {
                findNextPointOnFlat(
                    currentDirection = currentDirection,
                    cells = cells,
                    resultPoint = resultPoint
                ) to currentDirection
            }
            when (cells[newPoint]!!) {
                is Cell.Empty -> {
                    resultPoint = newPoint
                    currentDirection = newDir
                }
                is Cell.Wall -> return@repeat
            }
        }
    }

    return resultPoint to currentDirection
}

private fun findNextPointOnFlat(
    currentDirection: Direction,
    cells: Map<Point, Cell>,
    resultPoint: Point
): Point {
    val nextPoint = when (currentDirection) {
        Direction.RIGHT -> cells.keys.filter { it.y == resultPoint.y }.minBy { it.x }
        Direction.LEFT -> cells.keys.filter { it.y == resultPoint.y }.maxBy { it.x }
        Direction.TOP -> cells.keys.filter { it.x == resultPoint.x }.maxBy { it.y }
        Direction.DOWN -> cells.keys.filter { it.x == resultPoint.x }.minBy { it.y }
    }
    return nextPoint
}


private fun findNextPointOnCubeRealData(
    cells: Map<Point, Cell>,
    initialPoint: Point,
    initialDirection: Direction
): Pair<Point, Direction> {

    val nextPoint = when (initialDirection) {
        Direction.RIGHT -> when (initialPoint.side) {
            2 -> calculateNewPointAndDirection(
                cells = cells,
                initialPoint = initialPoint,
                newSide = 5,
                newDir = Direction.LEFT
            ) { sizeSide, diffPoint ->
                indexMinusY(sizeSide, diffPoint)
            }
            3 -> calculateNewPointAndDirection(
                cells = cells,
                initialPoint = initialPoint,
                newSide = 2,
                newDir = Direction.TOP
            ) { _, diffPoint ->
                switchXY(diffPoint)
            }
            5 -> calculateNewPointAndDirection(
                cells = cells,
                initialPoint = initialPoint,
                newSide = 2,
                newDir = Direction.LEFT
            ) { sizeSide, diffPoint ->
                indexMinusY(sizeSide, diffPoint)
            }
            6 -> calculateNewPointAndDirection(
                cells = cells,
                initialPoint = initialPoint,
                newSide = 5,
                newDir = Direction.TOP
            ) { _, diffPoint ->
                switchXY(diffPoint)
            }
            else -> error("Failed Init Direction: $initialDirection Point: $initialPoint")
        }
        Direction.LEFT -> when (initialPoint.side) {
            1 -> calculateNewPointAndDirection(
                cells = cells,
                initialPoint = initialPoint,
                newSide = 4,
                newDir = Direction.RIGHT
            ) { sizeSide, diffPoint ->
                indexMinusY(sizeSide, diffPoint)
            }
            3 -> calculateNewPointAndDirection(
                cells = cells,
                initialPoint = initialPoint,
                newSide = 4,
                newDir = Direction.DOWN
            ) { _, diffPoint ->
                switchXY(diffPoint)
            }
            4 -> calculateNewPointAndDirection(
                cells = cells,
                initialPoint = initialPoint,
                newSide = 1,
                newDir = Direction.RIGHT
            ) { sizeSide, diffPoint ->
                indexMinusY(sizeSide, diffPoint)
            }
            6 -> calculateNewPointAndDirection(
                cells = cells,
                initialPoint = initialPoint,
                newSide = 1,
                newDir = Direction.DOWN
            ) { sizeSide, diffPoint ->
                switchXY(diffPoint)
            }
            else -> error("Failed Init Direction: $initialDirection Point: $initialPoint")
        }
        Direction.TOP -> when (initialPoint.side) {
            1 -> calculateNewPointAndDirection(
                cells = cells,
                initialPoint = initialPoint,
                newSide = 6,
                newDir = Direction.RIGHT
            ) { _, diffPoint ->
                switchXY(diffPoint)
            }
            2 -> calculateNewPointAndDirection(
                cells = cells,
                initialPoint = initialPoint,
                newSide = 6,
                newDir = Direction.TOP
            ) { sizeSide, diffPoint ->
                indexMinusY(sizeSide, diffPoint)
            }
            4 -> calculateNewPointAndDirection(
                cells = cells,
                initialPoint = initialPoint,
                newSide = 3,
                newDir = Direction.RIGHT
            ) { _, diffPoint ->
                switchXY(diffPoint)
            }
            else -> error("Failed Init Direction: $initialDirection Point: $initialPoint")
        }
        Direction.DOWN -> when (initialPoint.side) {
            6 -> calculateNewPointAndDirection(
                cells = cells,
                initialPoint = initialPoint,
                newSide = 2,
                newDir = Direction.DOWN
            ) { sizeSide, diffPoint ->
                indexMinusY(sizeSide, diffPoint)
            }
            2 -> calculateNewPointAndDirection(
                cells = cells,
                initialPoint = initialPoint,
                newSide = 3,
                newDir = Direction.LEFT
            ) { _, diffPoint ->
                switchXY(diffPoint)
            }
            5 -> calculateNewPointAndDirection(
                cells = cells,
                initialPoint = initialPoint,
                newSide = 6,
                newDir = Direction.LEFT
            ) { _, diffPoint ->
                switchXY(diffPoint)
            }
            else -> error("Failed Init Direction: $initialDirection Point: $initialPoint")
        }
    }
    return nextPoint
}

private fun findNextPointOnCubeTestData(
    cells: Map<Point, Cell>,
    initialPoint: Point,
    initialDirection: Direction
): Pair<Point, Direction> {

    val nextPoint = when (initialDirection) {
        Direction.RIGHT -> {
            when (initialPoint.side) {
                1 -> calculateNewPointAndDirection(
                    cells = cells,
                    initialPoint = initialPoint,
                    newSide = 6,
                    newDir = Direction.LEFT
                ) { sizeSide, diffPoint ->
                    diffPoint.copyWithSide(
                        x = diffPoint.x,
                        y = diffPoint.y - sizeSide,
                    )
                }
                4 -> calculateNewPointAndDirection(
                    cells = cells,
                    initialPoint = initialPoint,
                    newSide = 6,
                    newDir = Direction.DOWN
                ) { sizeSide, diffPoint ->
                    val index = sizeSide - 1
                    diffPoint.copyWithSide(
                        x = index - diffPoint.y,
                        y = index - diffPoint.x,
                    )
                }
                6 -> calculateNewPointAndDirection(
                    cells = cells,
                    initialPoint = initialPoint,
                    newSide = 1,
                    newDir = Direction.LEFT
                ) { sizeSide, diffPoint ->
                    indexMinusY(sizeSide, diffPoint)
                }
                else -> error("Failed Init Direction: $initialDirection Point: $initialPoint")
            }
        }
        Direction.LEFT -> error("! $initialDirection $initialPoint")
        Direction.TOP -> {
            when (initialPoint.side) {
                3 -> calculateNewPointAndDirection(
                    cells = cells,
                    initialPoint = initialPoint,
                    newSide = 1,
                    newDir = Direction.RIGHT
                ) { _, diffPoint ->
                    switchXY(diffPoint)
                }
                else -> error("Failed Init Direction: $initialDirection Point: $initialPoint")
            }
        }
        Direction.DOWN -> {
            when (initialPoint.side) {
                5 -> calculateNewPointAndDirection(
                    cells = cells,
                    initialPoint = initialPoint,
                    newSide = 2,
                    newDir = Direction.TOP
                ) { sizeSide, diffPoint ->
                    val index = sizeSide - 1
                    diffPoint.copyWithSide(
                        x = index - diffPoint.x,
                        y = diffPoint.y,
                    )
                }
                else -> error("Failed Init Direction: $initialDirection Point: $initialPoint")
            }
        }
    }
    return nextPoint
}

/**
 *
 */
private fun indexMinusY(
    sizeSide: Int,
    diffPoint: Point
): Point {
    val index = sizeSide - 1
    return diffPoint.copyWithSide(
        x = diffPoint.x,
        y = index - diffPoint.y,
    )
}

/**
 * Helper method to create new point with switched X and Y
 */
private fun switchXY(diffPoint: Point) = diffPoint.copyWithSide(
    x = diffPoint.y,
    y = diffPoint.x,
)

private fun calculateNewPointAndDirection(
    cells: Map<Point, Cell>,
    initialPoint: Point,
    newSide: Int,
    newDir: Direction,
    transpose: (sideSize: Int, diffPoint: Point) -> Point
): Pair<Point, Direction> {
    val sizeSide = cells.sideSize()
    val currentSideTopLeftPoint = cells.keys
        .filter { it.side == initialPoint.side }
        .minBy { it.heuristic() }
    val diffPoint = initialPoint - currentSideTopLeftPoint
    val newSideTopLeftPoint = cells.keys
        .filter { it.side == newSide }
        .minBy { it.heuristic() }

    val resDifPoint = transpose(sizeSide, diffPoint)
    val resultPoint = newSideTopLeftPoint + resDifPoint

    return resultPoint to newDir
}

fun walkOverPoints(
    isTest: Boolean,
    isCube: Boolean,
    initialPoint: Point,
    commands: List<Command>,
    cells: Map<Point, Cell>
): Pair<Point, Direction> {
    var currentPoint = initialPoint
    var currentDirection = Direction.RIGHT

    commands.forEach {
        when (it) {
            is Command.Move -> {
                val (nextPoint, nextDirection) = currentPoint.applyCommand(
                    isTest = isTest,
                    isCube = isCube,
                    initialDirection = currentDirection,
                    command = it,
                    cells = cells
                )
                currentPoint = nextPoint
                currentDirection = nextDirection
            }
            is Command.Rotation -> {
                currentDirection = currentDirection.applyRotation(it.rotation)
            }
        }
    }

    return currentPoint to currentDirection
}

fun main() {

    fun solve(
        input: List<String>,
        isCube: Boolean,
        isTest: Boolean
    ): Int {
        /**
         * Parse input
         */
        val map = parseInput(input.dropLast(2))
        val commands = parseInputCommands(input.last())

        /**
         * Find top left point
         */
        val minY = map.keys.minBy { it.y }
        val initialPoint = map.keys.filter { it.y == minY.y }.minBy { it.x }

        val (finalPoint, finalDirection) = walkOverPoints(
            isTest = isTest,
            isCube = isCube,
            initialPoint = initialPoint,
            commands = commands,
            cells = map
        )

        /**
         * Calculate final answer
         */
        val pointNumber = finalPoint.y.inc() * 1000 + finalPoint.x.inc() * 4
        val dirNumber = when (finalDirection) {
            Direction.RIGHT -> 0
            Direction.DOWN -> 1
            Direction.LEFT -> 2
            Direction.TOP -> 3
        }
        return pointNumber + dirNumber
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day22_test")
    val part1Test = solve(input = testInput, isCube = false, isTest = true)
    val part2Test = solve(input = testInput, isCube = true, isTest = true)

    check(part1Test == 6032)
    check(part2Test == 5031)

    val input = readInput("Day22")
    val part1 = solve(input = input, isCube = false, isTest = false)
    check(part1 == 43466)
    println(part1)
    val part2 = solve(input = input, isCube = true, isTest = false)
    check(part2 == 162155)
    println(part2)
}
