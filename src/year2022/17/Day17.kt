package year2022.`17`

import java.util.Collections
import readInput


data class Point(
    val x: Int,
    val y: Int
) {
    fun moveUp(): Point = copy(y = y + 1)
    fun moveDown(): Point = copy(y = y - 1)
    fun moveLeft(): Point = copy(x = x - 1)
    fun moveRight(): Point = copy(x = x + 1)
}

data class Figure(
    val points: Set<Point>
) {
    private val initialRange = 0..6
    fun isOverlapping(pointSet: Set<Point>): Boolean = points.any { point ->
        pointSet.contains(point)
    }

    fun moveLeft(
        fallenPoints: Set<Point>,
        range: IntRange = initialRange
    ): Figure {
        val movedObj = copy(points = points.map { it.moveLeft() }.toSet())
        return if (movedObj.points.any { it.x !in range } || movedObj.isOverlapping(fallenPoints)) {
            this
        } else {
            movedObj
        }
    }

    fun moveRight(
        fallenPoints: Set<Point>,
        range: IntRange = initialRange
    ): Figure {
        val movedObj = copy(points = points.map { it.moveRight() }.toSet())
        return if (movedObj.points.any { it.x !in range } || movedObj.isOverlapping(fallenPoints)) {
            this
        } else {
            movedObj
        }
    }

    fun moveDown(): Figure = copy(points = points.map { it.moveDown() }.toSet())
    fun moveUp(): Figure = copy(points = points.map { it.moveUp() }.toSet())

    companion object {

        fun createFigure(
            newFigureToCreate: FigureToCreate,
            heightPoint: Point
        ) = when (newFigureToCreate) {
            FigureToCreate.HORIZONTAL_LINE -> createLine(
                heightPoint.copy(x = 2),
                heightPoint.copy(y = heightPoint.y + 4)
            )
            FigureToCreate.CROSS -> createCross(
                heightPoint.copy(x = 2),
                heightPoint.copy(y = heightPoint.y + 4)
            )
            FigureToCreate.TRIANGLE -> createTriangle(
                heightPoint.copy(x = 2),
                heightPoint.copy(y = heightPoint.y + 4)
            )
            FigureToCreate.VERTICAL_LINE -> createVerticalLine(
                heightPoint.copy(x = 2),
                heightPoint.copy(y = heightPoint.y + 4)
            )
            FigureToCreate.SQUARE -> createSquare(
                heightPoint.copy(x = 2),
                heightPoint.copy(y = heightPoint.y + 4)
            )
        }

        fun createFloor(): Figure {
            return Figure(
                setOf(
                    Point(0, 0),
                    Point(1, 0),
                    Point(2, 0),
                    Point(3, 0),
                    Point(4, 0),
                    Point(5, 0),
                    Point(6, 0),
                )
            )
        }

        private fun createLine(
            initialLeftPoint: Point, // maybe remove
            initialBottomPoint: Point
        ): Figure {
            val startX = initialLeftPoint.x
            val startY = initialBottomPoint.y

            return Figure(
                setOf(
                    Point(startX, startY),
                    Point(startX + 1, startY),
                    Point(startX + 2, startY),
                    Point(startX + 3, startY),
                )
            )
        }

        private fun createCross(
            initialLeftPoint: Point, // maybe remove
            initialBottomPoint: Point
        ): Figure {
            val startX = initialLeftPoint.x
            val startY = initialBottomPoint.y

            return Figure(
                setOf(
                    Point(startX + 1, startY),
                    Point(startX + 1, startY + 1),
                    Point(startX + 1, startY + 2),
                    Point(startX, startY + 1),
                    Point(startX + 2, startY + 1),
                )
            )
        }

        private fun createTriangle(
            initialLeftPoint: Point, // maybe remove
            initialBottomPoint: Point
        ): Figure {
            val startX = initialLeftPoint.x
            val startY = initialBottomPoint.y

            return Figure(
                setOf(
                    Point(startX, startY),
                    Point(startX + 1, startY),
                    Point(startX + 2, startY),
                    Point(startX + 2, startY + 1),
                    Point(startX + 2, startY + 2),
                )
            )
        }

        private fun createVerticalLine(
            initialLeftPoint: Point, // maybe remove
            initialBottomPoint: Point
        ): Figure {
            val startX = initialLeftPoint.x
            val startY = initialBottomPoint.y

            return Figure(
                setOf(
                    Point(startX, startY),
                    Point(startX, startY + 1),
                    Point(startX, startY + 2),
                    Point(startX, startY + 3),
                )
            )
        }

        private fun createSquare(
            initialLeftPoint: Point, // maybe remove
            initialBottomPoint: Point
        ): Figure {
            val startX = initialLeftPoint.x
            val startY = initialBottomPoint.y

            return Figure(
                setOf(
                    Point(startX, startY),
                    Point(startX, startY + 1),
                    Point(startX + 1, startY),
                    Point(startX + 1, startY + 1),
                )
            )
        }

    }
}

enum class FigureToCreate {
    HORIZONTAL_LINE,
    CROSS,
    TRIANGLE,
    VERTICAL_LINE,
    SQUARE
}

enum class Movement {
    LEFT, RIGHT
}

data class CycleInfoModel(
    val figureWithNoOffset: Figure,
    val currentFallingFigure: FigureToCreate,
    val currentMovement: Movement,
    val currentMovementIndex: Int
) {
    companion object {
        fun findCycleHelper(
            points: Set<Point>,
            currentFallingFigure: FigureToCreate,
            prevMovement: Movement,
            index: Int
        ): CycleInfoModel {
            val topPointsSlice = points.sliceTopPoints()
            val lowPoint = topPointsSlice.minBy { it.y }
            val res = topPointsSlice
                .map { it.copy(x = it.x, y = it.y - lowPoint.y) }
                .toSet()

            return CycleInfoModel(
                figureWithNoOffset = Figure(res),
                currentFallingFigure = currentFallingFigure,
                currentMovement = prevMovement,
                currentMovementIndex = index
            )
        }
    }
}

fun parseCommands(input: List<String>): List<Movement> {
    return input.map { it.split("") }
        .flatten()
        .filter { it.isNotBlank() }
        .map {
            when (it) {
                ">" -> Movement.RIGHT
                "<" -> Movement.LEFT
                else -> error("!!! $it")
            }
        }
}

/**
 * TODO Write Custom Implementation instead of function
 */
fun <T> infiniteIteratorHelper(
    initialCollection: Collection<T>,
    initialIterator: Iterator<T>,
    onNextItem: (T) -> Unit = {},
    onIteratorRecreation: (Iterator<T>) -> Unit
): T {
    return if (initialIterator.hasNext()) {
        initialIterator.next().also(onNextItem)
    } else {
        val newIterator = initialCollection.iterator()
        onIteratorRecreation(newIterator)
        newIterator.next().also(onNextItem)
    }
}

@Deprecated("Print this points")
private fun printSet(set: Set<Point>) {
    val height = set.height()
    println()
    for (i in height downTo 0) {
        print("|")
        repeat(7) {
            val symbol = if (Point(it, i.toInt()) in set) "%" else "."
            print(symbol)
        }
        print("|")
        println()
    }
    println()
}

private fun Collection<Point>.height(): Long = maxOf { it.y }.toLong()

private val figuresPattern = listOf(
    FigureToCreate.HORIZONTAL_LINE,
    FigureToCreate.CROSS,
    FigureToCreate.TRIANGLE,
    FigureToCreate.VERTICAL_LINE,
    FigureToCreate.SQUARE,
)

private fun runOneFigureToBottom(
    fallenPoints: MutableSet<Point>,
    getNextFigure: () -> FigureToCreate,
    getNextMovement: () -> Movement,
): Pair<FigureToCreate, Movement> {
    val heightPoint = fallenPoints.maxBy { it.y }

    val newFigureToCreate = getNextFigure()

    var newFigure = Figure.createFigure(newFigureToCreate, heightPoint)
    var nextCommand: Movement
    do {
        nextCommand = getNextMovement()
        newFigure = when (nextCommand) {
            Movement.LEFT -> newFigure.moveLeft(fallenPoints)
            Movement.RIGHT -> newFigure.moveRight(fallenPoints)
        }
        newFigure = newFigure.moveDown()
    } while (!newFigure.isOverlapping(fallenPoints))

    newFigure = newFigure.moveUp()
    fallenPoints.addAll(newFigure.points)

    return newFigureToCreate to nextCommand
}

private fun Set<Point>.sliceTopPoints() = List(7) { x ->
    Point(
        x = x,
        y = filter { it.x == x }
            .height()
            .toInt()
    )
}

private fun calculateTowerHeight(
    initialFigure: Figure,
    commands: List<Movement>,
    figuresToCreate: List<FigureToCreate>,
    count: Long,
    calculateFinalAnswer: (
        fallenPoints: Set<Point>,
        newFigureToCreate: FigureToCreate,
        nextCommand: Movement,
        movementIndex: Int,
        commands: List<Movement>,
        counter: Long
    ) -> Long? = { _, _, _, _, _, _ -> null }
): Long {

    val fallenPoints = mutableSetOf<Point>().also {
        it.addAll(initialFigure.points)
    }

    var commandsIterator = commands.iterator()
    var figuresIterator = figuresToCreate.iterator()
    var counter = count
    var movementIndex = 0

    while (counter > 0) {
        val (newFigureToCreate, nextCommand) = runOneFigureToBottom(
            fallenPoints = fallenPoints,
            getNextFigure = {
                infiniteIteratorHelper(
                    figuresToCreate,
                    figuresIterator
                ) {
                    figuresIterator = it
                }
            },
            getNextMovement = {
                infiniteIteratorHelper(
                    initialCollection = commands,
                    initialIterator = commandsIterator,
                    onNextItem = {
                        movementIndex++
                    },
                    onIteratorRecreation = {
                        movementIndex = 0
                        commandsIterator = it
                    }
                )
            }
        )
        counter--

        calculateFinalAnswer(
            fallenPoints,
            newFigureToCreate,
            nextCommand,
            movementIndex,
            commands,
            counter
        )?.let {
            return it
        }
    }
    return fallenPoints.height()
}


private fun findHeightWithOffset(
    fallenPoints: Set<Point>,
    cycleFigure: Figure
): Long {
    val topLevelPoints = fallenPoints.sliceTopPoints()
    val result = topLevelPoints.map { point ->
        val verticalPoint = cycleFigure.points.find { it.x == point.x }!!
        point.copy(y = point.y - verticalPoint.y)
    }
    return result.height()
}

private fun calculateFinalAnswer(
    mapCycleHelper: Map<CycleInfoModel, Pair<Long, Long>>,
    currentCycleHelperModel: CycleInfoModel,
    startAmountOfSteps: Long,
    currentStep: Long,
    commands: List<Movement>,
    fallenPoints: Set<Point>
): Long {
    val (prevCycleItem, beforeCycleHeight) = mapCycleHelper[currentCycleHelperModel]!!
    val currentCycleItem = startAmountOfSteps - currentStep
    val cycleSize = currentCycleItem - prevCycleItem
    val amountOfCycles = currentStep / cycleSize + 1
    val remainingStepsAfterAllCycles = currentStep % cycleSize

    val rotatedCommands = commands.toMutableList().also {
        Collections.rotate(it, -currentCycleHelperModel.currentMovementIndex)
    }
    val rotatedFiguresToCreate = figuresPattern.toMutableList().also {
        val index = it.indexOf(currentCycleHelperModel.currentFallingFigure)
        Collections.rotate(it, -index - 1)
    }

    val remainingTowerHeight = calculateTowerHeight(
        initialFigure = currentCycleHelperModel.figureWithNoOffset,
        commands = rotatedCommands,
        figuresToCreate = rotatedFiguresToCreate,
        count = remainingStepsAfterAllCycles
    )

    val heightWithOffset = findHeightWithOffset(
        fallenPoints = fallenPoints,
        cycleFigure = currentCycleHelperModel.figureWithNoOffset
    )
    val cycleHeight = heightWithOffset - beforeCycleHeight

    return remainingTowerHeight +
        beforeCycleHeight +
        cycleHeight * amountOfCycles
}


fun main() {

    fun part1(input: List<String>, count: Long = 2022): Long {
        return calculateTowerHeight(
            initialFigure = Figure.createFloor(),
            commands = parseCommands(input),
            figuresToCreate = figuresPattern,
            count = count
        )
    }

    fun part2(input: List<String>, count: Long = 1_000_000_000_000): Long {
        val cycleHelper = mutableSetOf<CycleInfoModel>()
        val mapCycleHelper = mutableMapOf<CycleInfoModel, Pair<Long, Long>>()
        return calculateTowerHeight(
            initialFigure = Figure.createFloor(),
            commands = parseCommands(input),
            figuresToCreate = figuresPattern,
            count = count
        ) { fallenPoints, newFigureToCreate, nextCommand, movementIndex, commands, counter ->
            val cycleItem = CycleInfoModel.findCycleHelper(
                points = fallenPoints,
                currentFallingFigure = newFigureToCreate,
                prevMovement = nextCommand,
                index = movementIndex
            )

            if (cycleHelper.contains(cycleItem)) {
                calculateFinalAnswer(
                    mapCycleHelper = mapCycleHelper,
                    currentCycleHelperModel = cycleItem,
                    startAmountOfSteps = count,
                    currentStep = counter,
                    commands = commands,
                    fallenPoints = fallenPoints
                )
            } else {
                cycleHelper.add(cycleItem)
                mapCycleHelper[cycleItem] = (count - counter) to findHeightWithOffset(
                    fallenPoints = fallenPoints,
                    cycleFigure = cycleItem.figureWithNoOffset
                )
                null
            }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    val part1Test = part1(testInput)
    val part2Test = part2(testInput)

    check(part1Test == 3068L)
    check(part2Test == 1514285714288L)

    val input = readInput("Day17")
    println(part1(input))
    println(part2(input))
}
