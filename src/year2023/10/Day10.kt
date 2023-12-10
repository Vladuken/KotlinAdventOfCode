package year2023.`10`

import readInput
import utils.printDebug
import utils.printlnDebug
import kotlin.system.measureTimeMillis

private const val CURRENT_DAY = "10"


private data class Point(
    val x: Int,
    val y: Int,
) {
    val left get() = Point(x - 1, y)
    val right get() = Point(x + 1, y)
    val top get() = Point(x, y - 1)
    val bottom get() = Point(x, y + 1)
    override fun toString(): String = "[$x,$y]"
}

enum class Pipe(val value: String) {
    VerticalPipe("|"),
    HorizontalPipe("-"),
    PipeL("L"),
    PipeJ("J"),
    Pipe7("7"),
    PipeF("F"),
    Ground("."),
    Animal("S"),
    O("O");

    override fun toString(): String {
        return value;
    }
}


// region Adjustments
private fun Pipe.adjustmentsRight(): Set<Pipe> {
    return when (this) {
        Pipe.VerticalPipe -> setOf()
        Pipe.HorizontalPipe -> setOf(Pipe.HorizontalPipe, Pipe.PipeJ, Pipe.Pipe7)
        Pipe.PipeL -> setOf(Pipe.HorizontalPipe, Pipe.PipeJ, Pipe.Pipe7)
        Pipe.PipeJ -> setOf()
        Pipe.Pipe7 -> setOf()
        Pipe.PipeF -> setOf(Pipe.HorizontalPipe, Pipe.PipeJ, Pipe.Pipe7)
        Pipe.Ground -> emptySet()
        Pipe.Animal -> emptySet()
        else -> error("")
    }
}

private fun Pipe.adjustmentsTop(): Set<Pipe> {
    return when (this) {
        Pipe.VerticalPipe -> setOf(Pipe.VerticalPipe, Pipe.Pipe7, Pipe.PipeF)
        Pipe.HorizontalPipe -> setOf()
        Pipe.PipeL -> setOf(Pipe.VerticalPipe, Pipe.Pipe7, Pipe.PipeF)
        Pipe.PipeJ -> setOf(Pipe.VerticalPipe, Pipe.Pipe7, Pipe.PipeF)
        Pipe.Pipe7 -> setOf()
        Pipe.PipeF -> setOf()
        Pipe.Ground -> emptySet()
        Pipe.Animal -> emptySet()
        else -> error("")
    }
}

private fun Pipe.adjustmentsBottom(): Set<Pipe> {
    return when (this) {
        Pipe.VerticalPipe -> setOf(Pipe.VerticalPipe, Pipe.PipeL, Pipe.PipeJ)
        Pipe.HorizontalPipe -> setOf()
        Pipe.PipeL -> setOf()
        Pipe.PipeJ -> setOf()
        Pipe.Pipe7 -> setOf(Pipe.VerticalPipe, Pipe.PipeL, Pipe.PipeJ)
        Pipe.PipeF -> setOf(Pipe.VerticalPipe, Pipe.PipeL, Pipe.PipeJ)
        Pipe.Ground -> emptySet()
        Pipe.Animal -> emptySet()
        else -> error("")
    }
}


private fun Pipe.adjustmentsLeft(): Set<Pipe> {
    return when (this) {
        Pipe.VerticalPipe -> setOf()
        Pipe.HorizontalPipe -> setOf(Pipe.HorizontalPipe, Pipe.PipeL, Pipe.PipeF)
        Pipe.PipeL -> setOf()
        Pipe.PipeJ -> setOf(Pipe.HorizontalPipe, Pipe.PipeL, Pipe.PipeF)
        Pipe.Pipe7 -> setOf(Pipe.HorizontalPipe, Pipe.PipeL, Pipe.PipeF)
        Pipe.PipeF -> setOf()
        Pipe.Ground -> emptySet()
        Pipe.Animal -> emptySet()
        else -> error("")
    }
}
// endregion

private fun String.toPipe(): Pipe {
    return when (this) {
        "|" -> Pipe.VerticalPipe
        "-" -> Pipe.HorizontalPipe
        "L" -> Pipe.PipeL
        "J" -> Pipe.PipeJ
        "7" -> Pipe.Pipe7
        "F" -> Pipe.PipeF
        "." -> Pipe.Ground
        "S" -> Pipe.Animal
        else -> error("ILLEGAL PIPE $this")
    }
}

private fun parseMap(input: List<String>): Map<Point, Pipe> {
    val mutableMap = mutableMapOf<Point, Pipe>()
    input.forEachIndexed { y, line ->
        line.split("")
            .filter { it.isNotBlank() }
            .forEachIndexed { x, value ->
                mutableMap[Point(x, y)] = value.toPipe()
            }
    }
    return mutableMap
}

private fun printMap(initialMap: Map<Point, Pipe>) {
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

private fun Map<Point, Pipe>.withReplacedVisited(
    visitedPoints: Set<Point>,
    isReversed: Boolean = false,
): Map<Point, Pipe> {
    return mapValues { (currentPoint, _) ->
        val isValueVisited = currentPoint in visitedPoints
        if (isValueVisited xor isReversed) {
            Pipe.O
        } else {
            Pipe.Ground
        }
    }
}


private fun findAnimal(initialMap: Map<Point, Pipe>): Point {
    initialMap.forEach { (point, pipe) ->
        if (pipe == Pipe.Animal) return point
    }
    error("Impossible state")
}

enum class Direction {
    BOTTOM,
    TOP,
    LEFT,
    RIGHT;

    override fun toString(): String {
        return when (this) {
            BOTTOM -> "B"
            TOP -> "T"
            LEFT -> "L"
            RIGHT -> "R"
        }
    }
}

private fun Direction.calculateNextPoint(currentPoint: Point): Point {
    return when (this) {
        Direction.LEFT -> currentPoint.left
        Direction.RIGHT -> currentPoint.right
        Direction.TOP -> currentPoint.top
        Direction.BOTTOM -> currentPoint.bottom
    }
}

private fun checkPointIfOk(
    initialMap: Map<Point, Pipe>,
    visitedPoints: Set<Point>,
    currentPipe: Pipe,
    point: Point,
    adjustments: (Pipe) -> Set<Pipe>
): Boolean {
    val newPipe = initialMap[point]
    return newPipe in adjustments(currentPipe) && point !in visitedPoints
}

private fun processLoop(
    initialAnimalPoint: Point,
    initialNeighbourPoint: Point,
    initialMap: Map<Point, Pipe>,
    initialDirection: Direction,
): Pair<Set<Point>, Set<Point>> {
    val visitedCyclePoints: MutableSet<Point> = mutableSetOf(initialAnimalPoint)


    val queue: ArrayDeque<Point> = ArrayDeque()
    queue.add(initialNeighbourPoint)

    var count = 0
    while (queue.isNotEmpty()) {
        count++
        val point = queue.removeFirst()

        if (point in visitedCyclePoints) continue
        val currentPipe = initialMap[point] ?: continue

        visitedCyclePoints.add(point)

        val leftPoint = point.left
        val rightPoint = point.right
        val topPoint = point.top
        val bottomPoint = point.bottom

        val shouldGoLeft = checkPointIfOk(
            initialMap = initialMap,
            visitedPoints = visitedCyclePoints,
            currentPipe = currentPipe,
            point = leftPoint,
            adjustments = Pipe::adjustmentsLeft,
        )
        val shouldGoRight = checkPointIfOk(
            initialMap = initialMap,
            visitedPoints = visitedCyclePoints,
            currentPipe = currentPipe,
            point = rightPoint,
            adjustments = Pipe::adjustmentsRight,
        )
        val shouldGoTop = checkPointIfOk(
            initialMap = initialMap,
            visitedPoints = visitedCyclePoints,
            currentPipe = currentPipe,
            point = topPoint,
            adjustments = Pipe::adjustmentsTop,
        )
        val shouldGoBottom = checkPointIfOk(
            initialMap = initialMap,
            visitedPoints = visitedCyclePoints,
            currentPipe = currentPipe,
            point = bottomPoint,
            adjustments = Pipe::adjustmentsBottom,
        )

        if (shouldGoLeft) queue.add(leftPoint)
        if (shouldGoRight) queue.add(rightPoint)
        if (shouldGoTop) queue.add(topPoint)
        if (shouldGoBottom) queue.add(bottomPoint)
    }

    println("Search Cycle Count processed: $count")

    val outsidePositions = mutableSetOf<Point>()
    var currentDirection = initialDirection

    visitedCyclePoints.forEach { point ->
        val currentPipe = initialMap[point] ?: error("")

        val prePoint = currentDirection.calculateNextPoint(point)
        currentDirection = currentDirection.switchIt(currentPipe)
        val afterPoint = currentDirection.calculateNextPoint(point)

        if (prePoint !in visitedCyclePoints) outsidePositions.add(prePoint)
        if (afterPoint !in visitedCyclePoints) outsidePositions.add(afterPoint)
    }

    val almostFinalIds = dfsFindAllNeighboursForEachPoint(
        initialMap = initialMap,
        outsideInitialPoints = outsidePositions,
        cyclePoints = visitedCyclePoints.toSet(),
    )

    printMapWithSpacingAndTags(
        tag = "VISITS CYCLE POINTS",
        map = initialMap,
        pointsToReplace = visitedCyclePoints.toSet(),
    )

    printMapWithSpacingAndTags(
        tag = "OUTSIDE POSITIONS",
        map = initialMap,
        pointsToReplace = outsidePositions.toSet(),
    )

    printMapWithSpacingAndTags(
        tag = "FINAL",
        map = initialMap,
        pointsToReplace = almostFinalIds,
    )

    return visitedCyclePoints.toSet() to almostFinalIds
}

private fun printMapWithSpacingAndTags(
    tag: String,
    map: Map<Point, Pipe>,
    pointsToReplace: Set<Point>,
) {
    printlnDebug { tag }
    val finalMap = map.withReplacedVisited(pointsToReplace)
    printlnDebug { tag }
    printMap(finalMap)
    printlnDebug { tag }
}

private fun dfsFindAllNeighboursForEachPoint(
    initialMap: Map<Point, Pipe>,
    outsideInitialPoints: Set<Point>,
    cyclePoints: Set<Point>,
): Set<Point> {
    printlnDebug { "INITIAL MAP SIZE: ${initialMap.size}" }
    printlnDebug { "outsideInitialPoints POINTS SIZE: ${outsideInitialPoints.size}" }
    printlnDebug { "CYCLE POINTS SIZE: ${cyclePoints.size}" }

    val initialPointsSet = initialMap.map { it.key }.toSet()
    val visitedPoints = (outsideInitialPoints + cyclePoints).toMutableSet()

    val queue = ArrayDeque(outsideInitialPoints)

    var count = 0
    while (queue.isNotEmpty()) {
        count++
        val point = queue.removeFirst()
        if (point in queue) continue

        visitedPoints.add(point)

        val leftPoint = point.left
        val rightPoint = point.right
        val topPoint = point.top
        val bottomPoint = point.bottom

        val shouldGoLeft = leftPoint !in visitedPoints && leftPoint in initialPointsSet
        val shouldGoRight = rightPoint !in visitedPoints && rightPoint in initialPointsSet
        val shouldGoTop = topPoint !in visitedPoints && topPoint in initialPointsSet
        val shouldGoBottom = bottomPoint !in visitedPoints && bottomPoint in initialPointsSet

        if (shouldGoLeft) {
            queue.addLast(leftPoint)
        }
        if (shouldGoRight) {
            queue.addLast(rightPoint)
        }
        if (shouldGoTop) {
            queue.addLast(topPoint)
        }
        if (shouldGoBottom) {
            queue.addLast(bottomPoint)
        }
    }

    println("DFS: Count processed $count")

    return initialPointsSet - visitedPoints
}

private fun Direction.switchIt(pipe: Pipe): Direction {
    return when (pipe) {
        Pipe.VerticalPipe -> this
        Pipe.HorizontalPipe -> this
        Pipe.PipeL -> when (this) {
            Direction.LEFT -> Direction.BOTTOM
            Direction.TOP -> Direction.RIGHT
            Direction.BOTTOM -> Direction.LEFT
            Direction.RIGHT -> Direction.TOP
        } // OK
        Pipe.PipeJ -> when (this) {
            Direction.BOTTOM -> Direction.RIGHT
            Direction.TOP -> Direction.LEFT
            Direction.LEFT -> Direction.TOP
            Direction.RIGHT -> Direction.BOTTOM
        }

        Pipe.Pipe7 -> when (this) {
            Direction.RIGHT -> Direction.TOP
            Direction.TOP -> Direction.RIGHT
            Direction.BOTTOM -> Direction.LEFT
            Direction.LEFT -> Direction.BOTTOM
        } // ok

        Pipe.PipeF -> when (this) {
            Direction.RIGHT -> Direction.BOTTOM
            Direction.TOP -> Direction.LEFT
            Direction.BOTTOM -> Direction.RIGHT
            Direction.LEFT -> Direction.TOP
        } // ok

        else -> this
    }

}

fun main() {

    fun part1AndTwo(input: List<String>): Pair<Int, Int> {
        val mapOfPoints = parseMap(input)
        printMap(mapOfPoints)
        val animalPos = findAnimal(mapOfPoints)

        val (cyclePoints, insidePoints) = processLoop(
            initialAnimalPoint = animalPos,
            initialNeighbourPoint = animalPos.top,
            initialMap = mapOfPoints,
            initialDirection = Direction.LEFT,
        )
        val maxDistanceInCycle = cyclePoints.size / 2
        val countOfInsidePoints = insidePoints.size

        return maxDistanceInCycle to countOfInsidePoints
    }


    // test if implementation meets criteria from the description, like:
    val input = readInput("Day$CURRENT_DAY")

    val time = measureTimeMillis {
        val (part1, part2) = part1AndTwo(input)
        // Part 1
        println("Part 1: $part1")
        check(part1 == 6697)

        // Part 2
        println("Part 2: $part2")
        check(part2 == 423)
    }

    println("Time Spend: $time ms")
}
