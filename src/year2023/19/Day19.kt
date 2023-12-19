package year2023.`19`

import readInput
import utils.printlnDebug

private const val CURRENT_DAY = "19"


data class XmasRanged(
    val x: IntRange,
    val m: IntRange,
    val a: IntRange,
    val s: IntRange,
) {
    fun countAll(): Long {
        return x.count().toLong() * m.count() * a.count() * s.count()
    }
}

data class Xmas(
    val x: Int,
    val m: Int,
    val a: Int,
    val s: Int,
) {
    fun sum(): Int = x + m + a + s
}

sealed class Rule {
    data class Less(
        val key: String,
        val num: Int,
        val destination: String,
    ) : Rule()

    data class More(
        val key: String,
        val num: Int,
        val destination: String,
    ) : Rule()

    data class Terminal(
        val destination: String
    ) : Rule()
}

data class Workflow(
    val key: String,
    val rules: List<Rule>,
)

fun String.toRule(): Rule {
    return when {
        contains("<") -> {
            val split = split("<", ":")
            Rule.Less(
                key = split.first(),
                num = split[1].toInt(),
                destination = split[2],
            )
        }

        contains(">") -> {
            val split = split(">", ":")
            Rule.More(
                key = split.first(),
                num = split[1].toInt(),
                destination = split[2],
            )
        }

        else -> Rule.Terminal(
            destination = this,
        )

    }
}

fun String.toRules(): List<Rule> {
    return split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .map { it.toRule() }
}

fun lineToWorkflow(line: String): Workflow {
    val key = line.split("{").first()
    val rulesLine = line.substring(
        line.indexOfFirst { it == '{' }.inc(),
        line.indexOfLast { it == '}' },
    )
    return Workflow(
        key = key,
        rules = rulesLine.toRules()
    )

}

fun String.toKeyValue(): Pair<String, Int> {
    val split = split("=")
    return split.first() to split.last().toInt()
}

fun lineToXmas(line: String): Xmas {
    val xmasLine = line.substring(
        line.indexOfFirst { it == '{' },
        line.indexOfLast { it == '}' },
    )

    val pairs = xmasLine.split(",")
        .map { it.trim() }
        .map { it.toKeyValue() }

    return Xmas(
        pairs[0].second,
        pairs[1].second,
        pairs[2].second,
        pairs[3].second,
    )
}

private fun List<Workflow>.asMapOfWorkflows(): Map<String, List<Rule>> {
    return associate { it.key to it.rules }
}

private fun processAll(
    xmases: List<Xmas>,
    workflows: List<Workflow>,
    initialKey: String,
): Int {
    val workflowsMap: Map<String, List<Rule>> = workflows.asMapOfWorkflows()
    val res = xmases.map {
        var currentKey = initialKey
        while (currentKey !in listOf("R", "A")) {
            currentKey = processXmas(it, workflowsMap[currentKey]!!)
        }
        it to currentKey
    }

    printlnDebug { res }

    return res
        .filter { it.second == "A" }
        .sumOf { (xmas, _) -> xmas.sum() }
}

private fun processAllRanged(
    xmases: XmasRanged,
    workflows: List<Workflow>,
    initialKey: String
): Long {
    val workflowsMap: Map<String, List<Rule>> = workflows.asMapOfWorkflows()

    var currentKey: Map<XmasRanged, String> = mapOf(xmases to initialKey)
    while (currentKey.values.any { it !in listOf("R", "A") }) {
        val mutableMap: MutableMap<XmasRanged, String> = mutableMapOf()
        currentKey.forEach { (xmasRanged, key) ->
            if (key in listOf("R", "A")) {
                mutableMap[xmasRanged] = key
            } else {
                val foundRules = workflowsMap[key] ?: error("CouldNotFindWorkflows for key $key")
                val map = processXmasRanged(xmasRanged, foundRules)
                mutableMap.putAll(map)
            }
        }
        currentKey = mutableMap
    }

    val resMap = currentKey.filter { it.value == "A" }
    return resMap.keys.sumOf { it.countAll() }
}


private fun processXmas(
    xmas: Xmas,
    rules: List<Rule>,
): String {
    val xmasMap = mapOf(
        "x" to xmas.x,
        "m" to xmas.m,
        "a" to xmas.a,
        "s" to xmas.s,
    )
    rules.forEach {
        when (it) {
            is Rule.Less -> {
                val value = xmasMap[it.key] ?: error("Nothing found for ${it.key}")
                if (value < it.num) return it.destination
            }

            is Rule.More -> {
                val value = xmasMap[it.key]!!
                if (value > it.num) return it.destination
            }

            is Rule.Terminal -> {
                return it.destination
            }
        }
    }
    error("IMPOSSIBLE STATE XMAS:$xmas Rules:$rules")
}

// The batch satisfying 1≤x<1637, 1341≤m<2683, 3078≤a<3149, 1477≤s<4001 has a total size of 393444532448.
private fun processXmasRanged(
    xmas: XmasRanged,
    rules: List<Rule>,
): Map<XmasRanged, String> {


    val resultMap = mutableMapOf<XmasRanged, String>()
    var uXmas = xmas
    rules.forEach { rule ->
        val xmasRangeMap = mapOf(
            "x" to uXmas.x,
            "m" to uXmas.m,
            "a" to uXmas.a,
            "s" to uXmas.s,
        )
        when (rule) {

            is Rule.Less -> {
                val range = xmasRangeMap[rule.key] ?: error("Nothing found for ${rule.key}")
                if (rule.num in range) {
                    val acceptedXmas = when (rule.key) {
                        "x" -> uXmas.copy(x = range.first until rule.num)
                        "m" -> uXmas.copy(m = range.first until rule.num)
                        "a" -> uXmas.copy(a = range.first until rule.num)
                        "s" -> uXmas.copy(s = range.first until rule.num)
                        else -> error("Illegal key:${rule.key}")
                    }
                    val goNextXmas = when (rule.key) {
                        "x" -> uXmas.copy(x = rule.num..range.last)
                        "m" -> uXmas.copy(m = rule.num..range.last)
                        "a" -> uXmas.copy(a = rule.num..range.last)
                        "s" -> uXmas.copy(s = rule.num..range.last)
                        else -> error("Illegal key:${rule.key}")
                    }
                    resultMap[acceptedXmas] = rule.destination
                    uXmas = goNextXmas
                } else {
                    error("I DONT KNOW RETURN HERE??")
                }
            }

            is Rule.More -> {
                val range = xmasRangeMap[rule.key] ?: error("Nothing found for ${rule.key}")
                if (rule.num in range) {
                    val goNextXmas = when (rule.key) {
                        "x" -> uXmas.copy(x = range.first..rule.num)
                        "m" -> uXmas.copy(m = range.first..rule.num)
                        "a" -> uXmas.copy(a = range.first..rule.num)
                        "s" -> uXmas.copy(s = range.first..rule.num)
                        else -> error("Illegal key:${rule.key}")
                    }
                    val acceptedXmas = when (rule.key) {
                        "x" -> uXmas.copy(x = rule.num + 1..range.last)
                        "m" -> uXmas.copy(m = rule.num + 1..range.last)
                        "a" -> uXmas.copy(a = rule.num + 1..range.last)
                        "s" -> uXmas.copy(s = rule.num + 1..range.last)
                        else -> error("Illegal key:${rule.key}")
                    }
                    resultMap[acceptedXmas] = rule.destination
                    uXmas = goNextXmas
                } else {
                    error("I DONT KNOW RETURN HERE??")
                }
            }

            is Rule.Terminal -> {
                resultMap[uXmas] = rule.destination
            }
        }
    }
    return resultMap
}

fun main() {

    fun part1(input: List<String>): Int {
        val tookLines = input.takeWhile { it.isNotBlank() }
        val workflows = tookLines.map {
            lineToWorkflow(it)
        }

        val xmasLines = input.reversed()
            .takeWhile { it.isNotBlank() }
            .reversed()

        val xmases = xmasLines.map {
            lineToXmas(it)
        }
        printlnDebug { workflows }
        printlnDebug { xmases }

        return processAll(xmases, workflows, "in")
    }

    fun part2(input: List<String>): Long {
        val tookLines = input.takeWhile { it.isNotBlank() }
        val workflows = tookLines.map {
            lineToWorkflow(it)
        }

        val initialRange = XmasRanged(
            x = 1..4000,
            m = 1..4000,
            a = 1..4000,
            s = 1..4000,
        )
        printlnDebug { workflows }
        return processAllRanged(
            initialRange,
            workflows,
            "in"
        )
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 19114)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 167409079868000L)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 319062)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 118638369682135L)
}
