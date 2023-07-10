package year2021.`12`

import readInput


private sealed class Place {

    data object Start : Place() {
        override fun toString(): String = "start"
    }


    data class LargePlace(val key: String) : Place() {
        override fun toString(): String = key
    }

    data class SmallPlace(val key: String) : Place() {
        override fun toString(): String = key
    }

    data object End : Place() {
        override fun toString(): String = "end"
    }

    fun isStart(): Boolean = this is Start
}

private data class Path(
    val from: Place,
    val to: Place,
) {
    override fun toString(): String {
        return "$from-$to"
    }
}

private fun Place.neighbours(paths: Set<Path>): Set<Place> {
    return paths
        .filter { it.from == this }
        .map { it.to }
        .toSet()
}

fun main() {

    fun from(string: String): Place {
        return when {
            string == "start" -> Place.Start
            string == "end" -> Place.End
            string.all { it.isUpperCase() } -> Place.LargePlace(string)
            string.all { it.isLowerCase() } -> Place.SmallPlace(string)
            else -> error("Illegal string: $string")
        }
    }

    fun parse(input: List<String>): Set<Path> {
        return input.map {
            val (from, to) = it.split("-")
                .map(::from)

            listOf(Path(from, to), Path(to, from))
        }
            .flatten()
            .toSet()
    }


    fun recursion(
        cache: MutableSet<List<Place>>,
        current: Place,
        paths: Set<Path>,
        currentPath: List<Place>,
        placeThatCanBeVisitedTwice: Place?,
        visitedPlaces: Map<Place, Int>,
    ) {
        if (current is Place.End) {
            cache.add(currentPath + current)
            return
        }


        val amountOfVisited = visitedPlaces.getOrDefault(current, 0).inc()
        val newVisitedPlace = visitedPlaces + (current to amountOfVisited)

        current.neighbours(paths)
            // Do not return to start
            .filter { neighbour -> !neighbour.isStart() }
            // Do not return to small cave if you were here previously.
            .filter { neighbour ->
                val threshold = if (neighbour == placeThatCanBeVisitedTwice) {
                    1
                } else {
                    0
                }
                val smallPlaceVisitedTwice = (visitedPlaces.getOrDefault(neighbour, 0) > threshold)
                val shouldStop = (neighbour is Place.SmallPlace) && smallPlaceVisitedTwice
                !shouldStop
            }
            .forEach {
                recursion(
                    cache = cache,
                    current = it,
                    paths = paths,
                    currentPath = currentPath + current,
                    placeThatCanBeVisitedTwice = placeThatCanBeVisitedTwice,
                    visitedPlaces = newVisitedPlace
                )
            }
    }

    fun part1(input: List<String>): Int {
        val pathSet = parse(input)
        val cache = mutableSetOf<List<Place>>()

        recursion(
            cache = cache,
            current = Place.Start,
            paths = pathSet,
            currentPath = emptyList(),
            placeThatCanBeVisitedTwice = null,
            visitedPlaces = emptyMap()
        )
        return cache.size
    }

    fun part2(input: List<String>): Int {
        val pathSet = parse(input)
        val cache = mutableSetOf<List<Place>>()

        pathSet.map { listOf(it.to, it.from) }
            .flatten()
            .filterIsInstance<Place.SmallPlace>()
            .toSet()
            .forEach {
                recursion(
                    cache = cache,
                    current = Place.Start,
                    paths = pathSet,
                    currentPath = emptyList(),
                    placeThatCanBeVisitedTwice = it,
                    visitedPlaces = emptyMap()
                )
            }

        return cache.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    val part1Test = part1(testInput)

    println(part1Test)
    check(part1Test == 10)

    val input = readInput("Day12")
    println(part1(input).also { check(it == 5254) })
    println(part2(input).also { check(it == 149385) })
}
