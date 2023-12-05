package year2023.`05`

import readInput
import utils.printlnDebug

private const val CURRENT_DAY = "05"


data class RangeMap(
    val input: LongRange,
    val delta: Long,
)

data class Mapper(
    val inputRanges: List<RangeMap>,
)

private fun parseIntoGroups(input: List<String>): List<List<String>> {
    val listOfLists: MutableList<List<String>> = mutableListOf()
    var currentList = mutableListOf<String>()
    input.forEach {
        if (it.isBlank()) {
            listOfLists.add(currentList)
            currentList = mutableListOf()
        } else {
            currentList.add(it)
        }
    }

    if (currentList.isNotEmpty()) {
        listOfLists.add(currentList)
    }

    return listOfLists
}

private fun parseGroupIntoMapper(group: List<String>): Mapper {
    val ranges = group.drop(1)
        .map { inputLine -> parseLineIntoSeeds(inputLine) }
        .map { (dest, source, delta) ->
            RangeMap(
                input = source..<source + delta,
                delta = dest - source,
            )
        }

    return Mapper(ranges)

}

private fun parseLineIntoSeeds(line: String): Triple<Long, Long, Long> {
    val res = line.split(" ")
        .mapNotNull { it.toLongOrNull() }

    val first = res.first()
    val second = res[1]
    val third = runCatching { res[2] }
        .onFailure { error("Failed on line $line - ${it.message}") }
        .getOrThrow()

    return Triple(first, second, third)
}

private fun processMappers(
    seeds: List<Long>,
    mappers: List<Mapper>,
): Long {
    val resListLocations = seeds
        .asSequence()
        .map {
            mappers.fold(it) { cur, mapper ->
                mapSeedFromInputToOutput(cur, mapper)
            }
        }
        .min()

    return resListLocations
}

private fun mapSeedFromInputToOutput(
    seed: Long,
    mappers: Mapper,
): Long {

    val realMapRange = mappers.inputRanges.find {
        it.input.contains(seed)
    }

    return if (realMapRange != null) {
        seed + realMapRange.delta
    } else {
        seed
    }
}

fun main() {

    fun part1(input: List<String>): Long {
        val seeds = input.first().split(" ")
            .mapNotNull { it.toLongOrNull() }
        val mappers = parseIntoGroups(input).map { parseGroupIntoMapper(it) }

        val result = processMappers(seeds, mappers)
        printlnDebug { "result : $result" }
        return result
    }


    fun part2(input: List<String>): Long {
        val mappers = parseIntoGroups(input).map { parseGroupIntoMapper(it) }

        val seedRanges = input.first().split(" ")
            .mapNotNull { it.toLongOrNull() }
            .windowed(2, 2, false)
            .map {
                check(it.size == 2)
                val first = it.first()
                val second = it[1]
                (first..<first + second)
            }
            .also { printlnDebug { "SIZE = ${it.size}" } }
            .minOf { sedRange ->
                printlnDebug { "Ranges seeds = $sedRange" }
                sedRange.minOf { seedItem ->
                    val mappedValueLocation = mappers.fold(seedItem) { cur, mapper ->
                        mapSeedFromInputToOutput(
                            cur,
                            mapper
                        )
                    }
                    mappedValueLocation
                }.also {
                    printlnDebug { "Counted Ranges seeds = $sedRange min = $it" }
                }
            }
        return seedRanges
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")
    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 35L)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 46L)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 910845529L)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 77435348L)
}
