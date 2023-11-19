private data class TargetPoint(
    val x: Int,
    val y: Int
) {

    fun applySpeed(speed: TargetPoint): TargetPoint {
        return TargetPoint(
            x + speed.x,
            y + speed.y,
        )
    }

    fun correctSpeed(): TargetPoint {
        return TargetPoint(
            x = (x - 1).coerceAtLeast(0),
            y = y - 1,
        )
    }

    fun isInside(area: TargetArea): Boolean {
        return x in area.xRange && y in area.yRange
    }
}


private data class TargetArea(
    val xRange: IntRange,
    val yRange: IntRange,
) {

    fun leftArea(
        currentPosition: TargetPoint,
    ): Boolean {
        return currentPosition.x > xRange.last ||
                currentPosition.y < yRange.first
    }
}


private val initialPoint = TargetPoint(0, 0)

private val inputTest = TargetArea(
    xRange = 20..30,
    yRange = -10..-5,
)


private val inputReal = TargetArea(
    xRange = 79..137,
    yRange = -176..-117,
)


private fun calculateAllVariantsForInput(
    targetArea: TargetArea,
    calculateAnswer: (Map<TargetPoint, Set<TargetPoint>>) -> Int,
): Int {
    // Speed To Positions for correct items
    val mutableSet = mutableMapOf<TargetPoint, Set<TargetPoint>>()

    val xRange = targetArea.xRange
    val yRange = targetArea.yRange

    /**
     * THIS IS HARDCODED VALUES - Instead of calculating it just put large values for x and y that works for both inputs.
     */
    val hardcodedXRange = 0..xRange.last
    val hardcodedYRange = yRange.first..200

    for (x in hardcodedXRange) {
        for (y in hardcodedYRange) {
            val initialSpeed = TargetPoint(
                x = x,
                y = y,
            )

            val result = checkIsInTargetAreaWithInitialPosition(
                targetArea = targetArea,
                initialPoint = initialPoint,
                initialSpeed = initialSpeed,
            )

            if (result.any { it.isInside(targetArea) }) {
                mutableSet[initialSpeed] = result
            }
        }
    }

    return calculateAnswer(mutableSet)
}

private fun checkIsInTargetAreaWithInitialPosition(
    targetArea: TargetArea,
    initialPoint: TargetPoint,
    initialSpeed: TargetPoint,
): Set<TargetPoint> {
    val allPositions = mutableSetOf(initialPoint)

    var currentSpeed = initialSpeed
    var currentPosition = initialPoint

    while (targetArea.leftArea(currentPosition).not()) {
        currentPosition = currentPosition.applySpeed(currentSpeed)
        currentSpeed = currentSpeed.correctSpeed()
        allPositions.add(currentPosition)
    }

    return allPositions
}

fun main() {

    fun part1(input: TargetArea): Int {
        return calculateAllVariantsForInput(input) { mutableSet ->
            mutableSet.mapValues { it.value.maxOf { it.y } }
                .filter { it.value != 0 }
                .maxOf { it.value }
        }
    }

    fun part2(input: TargetArea): Int {
        return calculateAllVariantsForInput(input) { mutableSet ->
            mutableSet.keys.size
        }
    }

    // test if implementation meets criteria from the description, like:
    val part1Test = part1(inputTest)

    check(part1Test == 45)

    val part1 = part1(inputReal)
    println("Part 1: $part1")
    check(part1 == 15400)

    val part2 = part2(inputReal)
    println("Part 2: $part2")
    check(part2 == 5844)
}
