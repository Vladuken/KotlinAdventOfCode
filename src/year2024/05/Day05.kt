package year2024.`05`

import readInput
import kotlin.collections.component1
import kotlin.collections.component2

private const val CURRENT_DAY = "05"


data class PageRule(
    val left: Int,
    val right: Int,
) {
    override fun toString(): String = "[$left|$right]"
}


data class PageNumber(
    val number: Int,
) {
    override fun toString(): String = "[$number]"
}

private fun parseRules(
    line: String,
): PageRule {
    val (left, right) = line.split("|").map { it.toInt() }
    return PageRule(left, right)
}

private fun parsePages(
    line: String,
): List<PageNumber> {
    return line.split(",").map { PageNumber(it.toInt()) }
}

fun List<PageNumber>.isValidWithRule(rule: PageRule): Boolean {
    val (left, right) = rule

    val leftIndex = this.indexOfFirst { it.number == left }
    val rightIndex = this.indexOfFirst { it.number == right }

    if (leftIndex == -1 || rightIndex == -1) return true
    return leftIndex < rightIndex
}

data class ComparablePages(
    val pageNumber: PageNumber,
    val rules: List<PageRule>,
) : Comparable<ComparablePages> {
    override fun compareTo(other: ComparablePages): Int {
        rules.forEach {
            if (pageNumber.number == it.left && other.pageNumber.number == it.right) {
                return -1
            }
        }
        return 0
    }
}


fun List<PageNumber>.fixSort(
    rules: List<PageRule>,
): List<PageNumber> {
    return this.map { ComparablePages(it, rules) }.sorted().map { it.pageNumber }
}

fun List<PageNumber>.findCenter(): Int {
    return this[size / 2].number
}

fun main() {
    fun parseRulesAndPages(input: List<String>): Pair<List<PageRule>, List<List<PageNumber>>> {
        val rules = input.takeWhile { it.isNotBlank() }.map { parseRules(it) }
        val pagesList = input.reversed().takeWhile { it.isNotBlank() }.reversed().map { parsePages(it) }

        return rules to pagesList
    }

    fun part1(input: List<String>): Int {
        val (rules, pagesList) = parseRulesAndPages(input)
        val validPages = pagesList.filter { pages ->
            rules.all { curRul -> pages.isValidWithRule(curRul) }
        }
        return validPages.map { it.findCenter() }.sum()
    }

    fun part2(input: List<String>): Int {
        val (rules, pagesList) = parseRulesAndPages(input)
        val inValidPages = pagesList.filterNot { pages ->
            rules.all { curRul -> pages.isValidWithRule(curRul) }
        }
        val fixedPages = inValidPages.map { invalid ->
            invalid.fixSort(rules)
        }

        return fixedPages.map { it.findCenter() }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 143)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 123)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 5248)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 4507)
}
