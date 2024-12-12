package year2024.`12`

import readInput

private const val CURRENT_DAY = "12"


private data class Point(
    val x: Int,
    val y: Int,
) {
    fun moveTop(): Point = copy(y = y - 1)
    fun moveRight(): Point = copy(x = x + 1)
    fun moveBottom(): Point = copy(y = y + 1)
    fun moveLeft(): Point = copy(x = x - 1)

    override fun toString(): String {
        return "{$x,$y}"
    }
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


private fun walkUntilAllCategoriesAreNotFound(
    map: Map<Point, String>,
    initialPoint: Point,
): Set<Point> {
    val visitedPoints = mutableSetOf<Point>()

    val initialValue = map[initialPoint]!!

    val queue: ArrayDeque<Point> = ArrayDeque()
    queue.add(initialPoint)

    while (queue.isNotEmpty()) {
        val currentItem = queue.removeFirst()
        if (currentItem in visitedPoints) continue

        visitedPoints.add(currentItem)

        fun addIfNeeded(p: Point) {
            map[p]?.let { newVal ->
                if (newVal == initialValue) {
                    queue.add(p)
                }
            }
        }

        val leftPoint = currentItem.moveLeft()
        val rightPoint = currentItem.moveRight()
        val topPoint = currentItem.moveTop()
        val bottomPoint = currentItem.moveBottom()

        addIfNeeded(leftPoint)
        addIfNeeded(rightPoint)
        addIfNeeded(topPoint)
        addIfNeeded(bottomPoint)
    }

    return visitedPoints
}

private data class Category(
    val value: String,
    val points: Set<Point>,
) {
    override fun toString(): String {
        return "[$value:${points.joinToString()}]"
    }
}

private fun calculateSidesOfCategory(category: Category): Pair<Int, Int> {
    val pointsOfCategory = category.points
    val minX = pointsOfCategory.minOf { it.x }
    val maxX = pointsOfCategory.maxOf { it.x }
    val minY = pointsOfCategory.minOf { it.y }
    val maxY = pointsOfCategory.maxOf { it.y }

    // top->bottom left->right
    var totalCount = 0
    var sideCount = 0
    for (newY in minY - 1..maxY + 1) {
        var prevPoint: Point? = null
        var prevSide: Boolean? = null
        var listOfSomething = mutableListOf<Boolean>()
        var currentSideCount = 0
        for (newX in minX - 1..maxX + 1) {
            val currPoint = Point(newX, newY)
            val topPoint = currPoint.moveTop()

            val isFromTop = (currPoint in pointsOfCategory && topPoint !in pointsOfCategory)
            val isFromBottom = (currPoint !in pointsOfCategory && topPoint in pointsOfCategory)

            when {
                isFromTop || isFromBottom -> {
                    totalCount++
                    val pp = prevPoint
                    if (pp == null) {
                        listOfSomething.add(false)
                    } else {
                        val currentSide = when {
                            isFromTop -> true
                            isFromBottom -> false
                            else -> error("")
                        }
                        if (prevSide != null && currentSide != prevSide) listOfSomething.add(false)
                        listOfSomething.add(true)

                    }
                }

                else -> {
                    listOfSomething.add(false)
                    //break of line
                }
            }
            if (isFromTop) prevSide = true
            if (isFromBottom) prevSide = false

            prevPoint = currPoint
        }
        listOfSomething.reduce<Boolean, Boolean> { a, b ->
            if (a != b && b == true) currentSideCount++
            b
        }
        sideCount += currentSideCount
    }


    // left->right top->bottom
    for (newX in minX - 1..maxX + 1) {
        var prevPoint: Point? = null
        var prevSide: Boolean? = null
        var listOfSomething = mutableListOf<Boolean>()
        var currentSideCount = 0
        for (newY in minY - 1..maxY + 1) {
            val currPoint = Point(newX, newY)
            val leftPoint = currPoint.moveLeft()

            val isFromLeft = (currPoint in pointsOfCategory && leftPoint !in pointsOfCategory)
            val isFromRight = (currPoint !in pointsOfCategory && leftPoint in pointsOfCategory)

            when {
                isFromLeft || isFromRight -> {
                    totalCount++
                    val pp = prevPoint
                    if (pp == null) {
                        listOfSomething.add(false)
                    } else {

                        val currentSide = when {
                            isFromLeft -> true
                            isFromRight -> false
                            else -> error("")
                        }
                        if (prevSide != null && currentSide != prevSide) listOfSomething.add(false)
                        listOfSomething.add(true)
                    }
                }

                else -> {
                    listOfSomething.add(false)
                    //break of line
                }
            }

            if (isFromLeft) prevSide = true
            if (isFromRight) prevSide = false
            prevPoint = currPoint
        }
        listOfSomething.reduce<Boolean, Boolean> { a, b ->
            if (a != b && b == true) currentSideCount++
            b
        }
        sideCount += currentSideCount
    }

    return totalCount to sideCount
}


private fun mapToCategories(
    map: Map<Point, String>,
): Set<Category> {
    val resCategories = mutableSetOf<Category>()
    map.keys.forEach { currentPoint ->
        val boundedPoints = mutableSetOf<Point>()
        if (currentPoint !in boundedPoints) {
            val categoryPoints = walkUntilAllCategoriesAreNotFound(map, currentPoint)
            boundedPoints.addAll(categoryPoints)
            resCategories.add(
                Category(
                    map[currentPoint]!!,
                    boundedPoints,
                )
            )
        }
    }
    return resCategories
}

private fun calculateAnswer(categoryWithPerimeters: List<Pair<Category, Int>>): Int {
    val res = categoryWithPerimeters.map {
        it.first to it.first.points.size * it.second
    }
    return res.sumOf { it.second }
}

fun main() {

    fun part1(input: List<String>): Int {
        val map = parseMap(input)
        val resCategories = mapToCategories(map)
        val categoryWithPerims = resCategories.map { it to calculateSidesOfCategory(it).first }
        return calculateAnswer(categoryWithPerims)
    }

    fun part2(input: List<String>): Int {
        val map = parseMap(input)
        val resCategories = mapToCategories(map)
        val categoryWithPerims = resCategories.map { it to calculateSidesOfCategory(it).second }
        return calculateAnswer(categoryWithPerims)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 140)
    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 80)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 1450422)
    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 906606)
}
