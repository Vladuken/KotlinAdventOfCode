package year2021.`08`

import readInput

fun main() {

    fun deductMappingOfNumbers(left: List<String>): Map<Set<Char>, Int> {
        val leftSet = left.toSortedSet()

        val one = leftSet.find { it.length == 2 }!!.toSortedSet()
        val four = leftSet.find { it.length == 4 }!!.toSortedSet()
        val seven = leftSet.find { it.length == 3 }!!.toSortedSet()
        val eight = leftSet.find { it.length == 7 }!!.toSortedSet()

        val segment0 = (seven - one).single()
        val six = leftSet
            .filter { it.length == 6 }
            .map { it.toSortedSet() }
            .single { sixCandidate ->
                sixCandidate.containsAll(eight - seven)
            }
        val segment2 = (eight - six).single()
        val five = leftSet
            .filter { it.length == 5 }
            .map { it.toSortedSet() }
            .single { fiveCandidate ->
                six.containsAll(fiveCandidate)
            }
        val segment4 = (six - five).single()
        val segment6 = (five - four - segment0).single()
        val two = leftSet
            .filter { it.length == 5 }
            .map { it.toSortedSet() }
            .single { twoCandidate ->
                twoCandidate.containsAll(setOf(segment0, segment2, segment4, segment6))
            }
        val three = leftSet
            .filter { it.length == 5 }
            .map { it.toSortedSet() }
            .single { threeCandidate ->
                threeCandidate !in listOf(two, five)
            }
        val nine = leftSet
            .filter { it.length == 6 }
            .map { it.toSortedSet() }
            .single { nineCandidate ->
                (eight - nineCandidate) == setOf(segment4)
            }
        val zero = leftSet
            .filter { it.length == 6 }
            .map { it.toSortedSet() }
            .single { zeroCandidate ->
                zeroCandidate !in listOf(six, nine)
            }

//        println("Segment 0: $segment0")
//        println("Segment 2: $segment2")
//        println("Segment 4: $segment4")
//        println("Segment 6: $segment6")
//
//        println("Zero: $zero")
//        println("One: $one")
//        println("Two: $two")
//        println("Three: $three")
//        println("Four: $four")
//        println("Five: $five")
//        println("Six: $six")
//        println("Seven: $seven")
//        println("Eight: $eight")
//        println("Nine: $nine")

        return mapOf(
            zero to 0,
            one to 1,
            two to 2,
            three to 3,
            four to 4,
            five to 5,
            six to 6,
            seven to 7,
            eight to 8,
            nine to 9,
        )
    }

    fun part1(input: List<String>): Int {
        val result = input.map { line ->
            line.split("|")[1].split(" ").filter { it.isNotBlank() }
        }

        return result.flatten()
            .count { it.length in listOf(2, 4, 3, 7) }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { line ->
            val (left, right) = line.split("|")
                .map { part -> part.split(" ").filter { it.isNotBlank() } }

            val mapOfMapping = deductMappingOfNumbers(left)
            val number = right
                .joinToString("") { mapOfMapping[it.toSortedSet()].toString() }
                .toInt()

            number
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    val part1Test = part1(testInput)

    println(part1Test)
    check(part1Test == 26)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
