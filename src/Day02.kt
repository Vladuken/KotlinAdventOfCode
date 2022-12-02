fun main() {

    fun part1(input: List<String>): Int {
        return input
            .map { it.split(" ") }
            .map { Round(it.first(), it[1]) }
            .sumOf { it.outcome() }
    }

    fun part2(input: List<String>): Int {
        return input
            .map { it.split(" ") }
            .map { Round(it.first(), it[1]) }
            .sumOf { it.outcome2() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    val part1Test = part2(testInput)

    //println(part1Test)
    check(part1Test == 12)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}

data class Round(
    val opponent: String,
    val outcome: String
) {
    fun outcome(): Int {
        val myRes = when (outcome) {
            "X" -> 1 // rock
            "Y" -> 2 //paper
            "Z" -> 3 //scissors
            else -> error("aaa")
        }

        // rock (A) - 1
        // paper (B) - 2
        // scissors (C) - 3
        val youOutcome = when {
            opponent == "A" && outcome == "X" -> 3
            opponent == "A" && outcome == "Y" -> 6
            opponent == "A" && outcome == "Z" -> 0
            opponent == "B" && outcome == "X" -> 0
            opponent == "B" && outcome == "Y" -> 3
            opponent == "B" && outcome == "Z" -> 6
            opponent == "C" && outcome == "X" -> 6
            opponent == "C" && outcome == "Y" -> 0
            opponent == "C" && outcome == "Z" -> 3
            else -> error("illegal state")
        }

        return youOutcome + myRes
    }

    fun outcome2(): Int {
        val myRes = when (outcome) {
            "X" -> 0 //you lose
            "Y" -> 3 //draw
            "Z" -> 6 //you win
            else -> error("Illegal State")
        }

        // rock (A) - 1
        // paper (B) - 2
        // scissors (C) - 3
        val yourFigure = when {
            opponent == "A" && outcome == "X" -> 3
            opponent == "A" && outcome == "Y" -> 1
            opponent == "A" && outcome == "Z" -> 2
            opponent == "B" && outcome == "X" -> 1
            opponent == "B" && outcome == "Y" -> 2
            opponent == "B" && outcome == "Z" -> 3
            opponent == "C" && outcome == "X" -> 2
            opponent == "C" && outcome == "Y" -> 3
            opponent == "C" && outcome == "Z" -> 1
            else -> error("Illegal State")
        }

        return yourFigure + myRes
    }
}
