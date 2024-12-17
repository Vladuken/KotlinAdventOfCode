package year2024.`17`

import readInput
import kotlin.math.pow

private const val CURRENT_DAY = "17"

data class Registers(
    val a: Long,
    val b: Long,
    val c: Long,
)

private fun parseRegisterInto(
    input: List<String>,
): Registers {

    return Registers(
        input[0].split(" ").mapNotNull { it.toLongOrNull() }.last(),
        input[1].split(" ").mapNotNull { it.toLongOrNull() }.last(),
        input[2].split(" ").mapNotNull { it.toLongOrNull() }.last(),
    )
}

private fun parseOperatorsInto(
    line: String,
): List<Long> {
    return line
        .split(" ").last()
        .split(",")
        .mapNotNull { it.toLongOrNull() }
}


/**
 * The adv instruction (opcode 0) performs division. The numerator is the value in the A register. The denominator is found by raising 2 to the power of the instruction's combo operand. (So, an operand of 2 would divide A by 4 (2^2); an operand of 5 would divide A by 2^B.) The result of the division operation is truncated to an integer and then written to the A register.
 *
 * The bxl instruction (opcode 1) calculates the bitwise XOR of register B and the instruction's literal operand, then stores the result in register B.
 *
 * The bst instruction (opcode 2) calculates the value of its combo operand modulo 8 (thereby keeping only its lowest 3 bits), then writes that value to the B register.
 *
 * The jnz instruction (opcode 3) does nothing if the A register is 0. However, if the A register is not zero, it jumps by setting the instruction pointer to the value of its literal operand; if this instruction jumps, the instruction pointer is not increased by 2 after this instruction.
 *
 * The bxc instruction (opcode 4) calculates the bitwise XOR of register B and register C, then stores the result in register B. (For legacy reasons, this instruction reads an operand but ignores it.)
 *
 * The out instruction (opcode 5) calculates the value of its combo operand modulo 8, then outputs that value. (If a program outputs multiple values, they are separated by commas.)
 *
 * The bdv instruction (opcode 6) works exactly like the adv instruction except that the result is stored in the B register. (The numerator is still read from the A register.)
 *
 * The cdv instruction (opcode 7) works exactly like the adv instruction except that the result is stored in the C register. (The numerator is still read from the A register.)
 */
private fun processInstruction(
    registers: Registers,
    operator: Long,
    currentInstructionPointer: Int,
    operand: Long,
    answerSink: MutableList<Int>,
    debugger: MutableList<Long>,
): Pair<Registers, Int> {
    fun adv(): Long {
        val numerator = registers.a
        val denominator = (2.0).pow(comboOperand(registers, operand).toDouble())
        val result = (numerator / denominator)
        return result.toLong()
    }

    val newInstructionPointer = (currentInstructionPointer + 2)
    debugger.add(operator)
    return when (operator) {
        0L -> {
            registers.copy(
                a = adv()
            ) to newInstructionPointer
        }

        1L -> {
            val first = registers.b
            val second = operand
            registers.copy(
                b = first xor second,
            ) to newInstructionPointer
        }

        2L -> {
            val first = comboOperand(registers, operand)

            registers.copy(
                b = first.mod(8).toLong()
            ) to newInstructionPointer
        }

        3L -> {
            if (registers.a == 0L) {
                // does nothing
                registers to newInstructionPointer
            } else {
                //todo 1
                registers to operand.toInt()
            }
        }

        4L -> {
            val first = registers.b
            val second = registers.c
            registers.copy(
                b = first xor second,
            ) to newInstructionPointer
        }

        5L -> {
            val first = comboOperand(registers, operand)
            answerSink.add(first.mod(8))
            registers to newInstructionPointer
        }

        6L -> {
            registers.copy(
                b = adv()
            ) to newInstructionPointer
        }

        7L -> {
            registers.copy(
                c = adv()
            ) to newInstructionPointer
        }

        else -> error("Illegal operator $operator")
    }
}

private fun comboOperand(
    registers: Registers,
    operand: Long,
): Long {
    return when (operand) {
        0L -> operand
        1L -> operand
        2L -> operand
        3L -> operand
        4L -> registers.a
        5L -> registers.b
        6L -> registers.c
        else -> error("INVALID COMBO OPERAND $operand")
    }
}

private fun performComputations(
    registers: Registers,
    operators: List<Long>,
): Pair<Registers, List<Int>> {
    var instructionPointer = 0
    var currentRegisters = registers

    val answer = mutableListOf<Int>()
    val debugger = mutableListOf<Long>()
    while (true) {

        val currentOperator = operators.getOrNull(instructionPointer)
        if (currentOperator == null) {
            return currentRegisters to answer
        }
        val currentOperand = operators.getOrNull(instructionPointer + 1)
        if (currentOperand == null) error("")

        val (newReg, newPointer) = processInstruction(
            registers = currentRegisters,
            operator = currentOperator,
            currentInstructionPointer = instructionPointer,
            operand = currentOperand,
            answer,
            debugger
        )
        instructionPointer = newPointer
        currentRegisters = newReg
    }
}

fun main() {

    // region Part 1
    println("If register C contains 9, the program 2,6 would set register B to 1. ")
    performComputations(
        Registers(0, 0, 9),
        listOf(2, 6)
    ).also { (resReg, resAnsw) ->
        check(resReg.b == 1L) { "If register C contains 9, the program 2,6 would set register B to 1. " }
    }
    println()

    println("If register A contains 10, the program 5,0,5,1,5,4 would output 0,1,2.")
    performComputations(
        Registers(10, 0, 0),
        listOf(5, 0, 5, 1, 5, 4)
    ).also { (resReg, resAnsw) ->
        check(resAnsw == listOf(0, 1, 2)) { "If register A contains 10, the program 5,0,5,1,5,4 would output 0,1,2." }
    }
    println()

    println("If register A contains 2024, the program 0,1,5,4,3,0 would output 4,2,5,6,7,7,7,7,3,1,0 and leave 0 in register A.")
    performComputations(
        Registers(2024, 0, 0),
        listOf(0, 1, 5, 4, 3, 0)
    ).also { (resReg, resAnsw) ->
        check(resReg.a == 0L) { "If register C contains 9, the program 2,6 would set register B to 1. " }
        check(
            resAnsw == listOf(
                4,
                2,
                5,
                6,
                7,
                7,
                7,
                7,
                3,
                1,
                0
            )
        ) { "If register C contains 9, the program 2,6 would set register B to 1. " }
    }

    println("If register B contains 29, the program 1,7 would set register B to 26.")
    performComputations(
        Registers(0, 29, 0),
        listOf(1, 7)
    ).also { (resReg, resAnsw) ->
        check(resReg.b == 26L) { "If register B contains 29, the program 1,7 would set register B to 26." }
    }
    println()

    println("If register B contains 2024 and register C contains 43690, the program 4,0 would set register B to 44354.")
    performComputations(
        Registers(0, 2024, 43690),
        listOf(4, 0)
    ).also { (resReg, resAnsw) ->
        check(resReg.b == 44354L) { "If register B contains 2024 and register C contains 43690, the program 4,0 would set register B to 44354." }
    }
    println()

    // endregion

    // region Part 2

    performComputations(
        Registers(117440, 0, 0),
        listOf(0, 3, 5, 4, 3, 0)
    ).also { (resReg, resAnsw) ->
        check(
            resAnsw == listOf(
                0,
                3,
                5,
                4,
                3,
                0
            )
        ) { "If register B contains 2024 and register C contains 43690, the program 4,0 would set register B to 44354." }
    }
    println()
    // endregion


    fun part1(input: List<String>): Pair<Registers, List<Int>> {
        val registersInput = input.takeWhile { it.isNotBlank() }
        val registers = parseRegisterInto(registersInput)
        val programInput = input.last()
        val operators = parseOperatorsInto(programInput)
        return performComputations(
            registers,
            operators
        )
    }

    fun part2(input: List<String>): Long {

        val registersInput = input.takeWhile { it.isNotBlank() }
        val registers = parseRegisterInto(registersInput)
        val programInput = input.last()
        val operators = parseOperatorsInto(programInput)

        // Manual brutforce
        var currentA: Long = 190384113204239


        val diff = 1
        var output = performComputations(
            registers.copy(a = currentA),
            operators
        ).second
        var count = 0
        val outputSizes = mutableSetOf<Int>()
        while (true) {

            output = performComputations(
                registers.copy(a = currentA),
                operators
            ).second

            outputSizes.add(output.size)
            val testOutput = output.joinToString(",")
            count++
            currentA += diff
            val endsWith = "2,4,1,2,7,5,0,3,4,7,1,7,5,5,3,0".drop(2)
            if (count % 10 == 0 && testOutput.endsWith(endsWith)) {
                println(endsWith)
                println("Current A $currentA Current Output:${output.joinToString(",")} OutputLength:${outputSizes} InputLength:${operators.size}")
                error("AAAA")
            }
        }
        return currentA
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput).also { (resReg, resAnsw) ->
        check(
            resAnsw == listOf(
                4,
                6,
                3,
                5,
                6,
                3,
                5,
                2,
                1,
                0
            )
        ) { "After the above program halts, its final output will be 4,6,3,5,6,3,5,2,1,0." }
    }
    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input).also { (_, resAnsw) ->
        check(
            resAnsw == listOf(7, 1, 3, 7, 5, 1, 0, 3, 4)
        ) { "Part1" }
    }
    println(part1)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 1L)
}
