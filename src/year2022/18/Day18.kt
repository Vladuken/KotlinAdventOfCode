package year2022.`18`

import java.util.LinkedList
import readInput

/**
 * Data Class Representing Cube of 1x1x1 in 3D Grid
 */
data class Cube(
    val x: Int,
    val y: Int,
    val z: Int
) {
    fun inRange(range: IntRange): Boolean {
        return x in range && y in range && z in range
    }
}

/**
 * Parse input to list of cubes
 */
fun parseInput(input: List<String>): List<Cube> {
    return input.map { line ->
        line.split(",").mapNotNull { it.toIntOrNull() }
    }
        .map {
            Cube(it[0], it[1], it[2])
        }
}

/**
 * Get Neighbours of this cube
 */
fun Cube.neighbours(): List<Cube> {
    return listOf(
        copy(x = x + 1),
        copy(x = x - 1),
        copy(y = y + 1),
        copy(y = y - 1),
        copy(z = z + 1),
        copy(z = z - 1),
    )
}

fun iterateOverAllEmptyCubes(
    initialCubes: List<Cube>,
    startingCube: Cube,
    range: IntRange
): Int {
    val visited = mutableSetOf<Cube>()
    val queue = LinkedList<Cube>().also { it.add(startingCube) }

    var count = 0
    while (queue.isNotEmpty()) {
        val currentCube = queue.remove()
        if (currentCube in visited) continue
        visited.add(currentCube)

        currentCube.neighbours()
            .forEach {
                if (it in initialCubes) {
                    count++
                } else if (it.inRange(range)) {
                    queue.add(it)
                }
            }
    }

    return count
}

fun main() {
    fun part1(input: List<String>): Int {
        val cubes = parseInput(input)
        val allCubeSides = 6
        return cubes.sumOf { cube ->
            val neighbours = cube.neighbours()
            val filledCubeSides = cubes.count { it in neighbours }
            allCubeSides - filledCubeSides
        }
    }


    fun part2(input: List<String>): Int {
        val cubes = parseInput(input)
        val max = listOf(
            cubes.maxOf { it.x },
            cubes.maxOf { it.y },
            cubes.maxOf { it.z },
        ).max()
        return iterateOverAllEmptyCubes(
            initialCubes = cubes,
            startingCube = Cube(max, max, max),
            range = -1..max + 1
        )
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    val part1Test = part1(testInput)
    val part2Test = part2(testInput)

    check(part1Test == 64)
    check(part2Test == 58)

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}
