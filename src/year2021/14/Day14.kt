package year2021.`14`

import readInput


// region Parsing
private fun parseInput(input: List<String>): String = input.first()

private fun parseMapper(input: List<String>): Transformations {
    val transformations = input.drop(2)

    return transformations.associate { transformationString ->
        val (left, right) = transformationString.split("->")
            .map { it.trim() }
        left to right
    }
}
// endregion

typealias MapPairCount = MutableMap<String, Long>
typealias MapCharCount = Map<Char, Long>
typealias Transformations = Map<String, String>

private fun cycle(input: MapPairCount, transformations: Transformations): MapPairCount {
    val resultMap: MapPairCount = mutableMapOf()
    input.keys
        .forEach { initialPair ->
            if (initialPair.length != 2) error("Illegal pair : $initialPair")
            val chatToSet = transformations[initialPair]

            val left = initialPair.first()
            val right = initialPair.last()

            val leftPair = "$left$chatToSet"
            val rightPair = "$chatToSet$right"

            resultMap[leftPair] = resultMap.getOrDefault(leftPair, 0) + input[initialPair]!!
            resultMap[rightPair] = resultMap.getOrDefault(rightPair, 0) + input[initialPair]!!
        }

    return resultMap

}

private fun MapPairCount.countCharCount(firstChar: Char, lastChar: Char): MapCharCount {
    val map = mutableMapOf<Char, Long>()

    forEach { (key, value) ->
        val left = key.first()
        val right = key.last()

        map[left] = map.getOrDefault(left, 0) + value
        map[right] = map.getOrDefault(right, 0) + value
    }

    map[firstChar] = map[firstChar]!! + 1
    map[lastChar] = map[lastChar]!! + 1

    return map
}

private fun stringToMapCount(string: String): MapPairCount {
    return string.windowed(2)
        .groupBy { it }
        .mapValues { it.value.size.toLong() } as MapPairCount
}

private fun fastAnswer(input: List<String>, n: Int): Long {
    val initLine = parseInput(input)
    val transformations = parseMapper(input)


    var bufRes = stringToMapCount(initLine)
    repeat(n) {
        bufRes = cycle(bufRes, transformations)
    }

    val res = bufRes.countCharCount(initLine.first(), initLine.last())
    val answer = res.values.max() - res.values.min()
    return answer / 2
}


fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    val part1Test = fastAnswer(testInput, 10)
    val part2Test = fastAnswer(testInput, 40)

    println(part1Test.also { check(it == 1588L) })
    println(part2Test.also { assert(it == 2188189693529) })

    val input = readInput("Day14")
    println(fastAnswer(input, 10).also { assert(it == 2188189693529) })
    println(fastAnswer(input, 40).also { assert(it == 4110568157153) })
}
