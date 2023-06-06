package year2022.`08`

import readInput
import transpose

fun main() {

    fun createGrid(input: List<String>): List<List<Int>> {
        return input.map {
            it.split("")
                .drop(1)
                .dropLast(1)
                .map { item -> item.toInt() }
        }
    }

    fun <T> genericCombine(
        first: List<List<T>>,
        second: List<List<T>>,
        block: (left: T, right: T) -> T
    ): List<List<T>> {
        return first.mapIndexed { i, line ->
            List(line.size) { j -> block(first[i][j], second[i][j]) }
        }
    }

    fun <T> Iterable<T>.takeUntil(predicate: (T) -> Boolean): List<T> {
        val list = ArrayList<T>()
        for (item in this) {
            if (!predicate(item)) {
                list.add(item)
                return list
            }

            list.add(item)
        }
        return list
    }

    fun calculateHorizontalVisibilityGrid(
        grid: List<List<Int>>,
    ): List<List<Boolean>> {
        return grid.mapIndexed { i, line ->
            List(line.size) { j ->
                val currentTreeSize = grid[i][j]
                val leftSide = grid[i].take(j)
                val rightSide = grid[i].drop(j + 1)
                val isVisibleFromLeft = leftSide.all { it < currentTreeSize }
                val isVisibleFromRight = rightSide.all { it < currentTreeSize }
                isVisibleFromLeft || isVisibleFromRight
            }
        }
    }

    fun calculateHorizontalScoreGrid(
        grid: List<List<Int>>,
    ): List<List<Int>> {
        return grid.mapIndexed { i, line ->
            List(line.size) { j ->
                val currentTreeSize = grid[i][j]

                val fromLeft = grid[i].take(j).reversed()
                val fromLeftTaken = fromLeft.takeUntil { it < currentTreeSize }

                val fromRight = grid[i].drop(j + 1)
                val fromRightTaken = fromRight.takeUntil { it < currentTreeSize }

                val leftCount = fromLeftTaken.count().takeIf { it != 0 } ?: 1
                val rightCount = fromRightTaken.count().takeIf { it != 0 } ?: 1

                if (fromLeft.isEmpty() || fromRight.isEmpty()) {
                    0
                } else {
                    leftCount * rightCount
                }
            }
        }
    }

    fun part1(input: List<String>): Int {
        val grid = createGrid(input)
        val vertical = transpose(calculateHorizontalVisibilityGrid(transpose(grid)))
        val horizontal = calculateHorizontalVisibilityGrid(grid)
        return genericCombine(vertical, horizontal) { left, right -> left || right }
            .flatten()
            .count { it }
    }

    fun part2(input: List<String>): Int {
        val grid = createGrid(input)
        val horizontal = calculateHorizontalScoreGrid(grid)
        val vertical = transpose(calculateHorizontalScoreGrid(transpose(grid)))
        return genericCombine(vertical, horizontal) { left, right -> left * right }
            .flatten()
            .max()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    val part1Test = part2(testInput)

    println(part1Test)
    check(part1Test == 8)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
