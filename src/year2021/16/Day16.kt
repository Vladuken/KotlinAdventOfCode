package year2021.`16`

import readInput
import utils.parseToLongRadix
import utils.printlnDebug

// region Models for task
data class Packet(
    val header: Header,
    val packetType: PacketType,
    val packetValue: Long?,
    val subPackets: List<Packet>,
) {

    /**
     * Finds sum of this packet and its subpackets
     */
    fun versionSum(): Long = header.version + subPackets.sumOf { it.versionSum() }


    /**
     * Calculate value for part 2
     */
    fun calculateValue(): Long {
        return when (packetType.id) {
            0L -> subPackets.sumOf { it.calculateValue() }
            1L -> subPackets.fold(1) { a, b -> a * b.calculateValue() }
            2L -> subPackets.minOf { it.calculateValue() }
            3L -> subPackets.maxOf { it.calculateValue() }
            4L -> packetValue ?: error("")
            5L -> 1L.takeIf { subPackets.first().calculateValue() > subPackets[1].calculateValue() }
                ?: 0

            6L -> 1L.takeIf { subPackets.first().calculateValue() < subPackets[1].calculateValue() }
                ?: 0

            7L -> 1L.takeIf {
                subPackets.first().calculateValue() == subPackets[1].calculateValue()
            } ?: 0

            else -> error("IllegalState")
        }
    }

    class Builder {
        private var header: Header? = null
        private var _packetType: PacketType? = null
        val packetType: PacketType get() = _packetType ?: error("Illegal Impossible State")

        private var packetValue: String = ""

        private var subPackets: List<Packet> = emptyList()

        fun header(header: String) {
            this.header = Header(header.parseToLongRadix(2))
        }

        fun packetType(packetType: String) {
            this._packetType = PacketType(packetType.parseToLongRadix(2))
        }

        fun packetValue(packetValue: String) {
            this.packetValue += packetValue.drop(1)
        }

        fun subPackets(subPackets: List<Packet>) {
            this.subPackets = subPackets
        }

        fun build(): Packet {
            return Packet(
                header = header ?: error("header is null"),
                packetType = _packetType ?: error("packetType is null"),
                packetValue = packetValue.ifBlank { null }?.parseToLongRadix(2),
                subPackets = subPackets,
            )
        }
    }
}


data class Header(
    val version: Long,
)

data class PacketType(
    val id: Long,
)

sealed class CurrentCalculatingThing {
    data object Header : CurrentCalculatingThing()
    data object PacketType : CurrentCalculatingThing()
    data object Datagram : CurrentCalculatingThing()
    data object LengthType : CurrentCalculatingThing()

    sealed class SubPacket : CurrentCalculatingThing() {
        data object Zero : SubPacket()
        data object One : SubPacket()
    }
}

// endregion


// region Helper Methods
private fun mapHexToBitArray(char: Char): List<Boolean> {
    val biteString = when (char) {
        '0' -> "0000"
        '1' -> "0001"
        '2' -> "0010"
        '3' -> "0011"
        '4' -> "0100"
        '5' -> "0101"
        '6' -> "0110"
        '7' -> "0111"
        '8' -> "1000"
        '9' -> "1001"
        'A' -> "1010"
        'B' -> "1011"
        'C' -> "1100"
        'D' -> "1101"
        'E' -> "1110"
        'F' -> "1111"
        else -> error("Illegal char: $char")
    }

    return biteString.map {
        when (it) {
            '1' -> true
            '0' -> false
            else -> error("Illegal state: $it")
        }
    }
}
// endregion


private fun iterateAndCollectPackets(
    iterator: CharIterator,
    limit: Long = -1,
): List<Packet> {
    val resultList = mutableListOf<Packet>()

    var packetBuilder = Packet.Builder()

    var currentEvent: CurrentCalculatingThing = CurrentCalculatingThing.Header

    val buffer = StringBuilder("")

    var counterOfPackets = 0L
    while (iterator.hasNext() && counterOfPackets != limit) {
        val currentChar = iterator.next()
        buffer.append(currentChar)

        when (currentEvent) {
            CurrentCalculatingThing.Header -> {
                if (buffer.length == 3) {
                    packetBuilder.header(buffer.toString())
                    buffer.clear()
                    currentEvent = CurrentCalculatingThing.PacketType
                }
            }

            CurrentCalculatingThing.PacketType -> {
                if (buffer.length == 3) {
                    packetBuilder.packetType(buffer.toString())
                    buffer.clear()
                    currentEvent = when (packetBuilder.packetType.id) {
                        4L -> CurrentCalculatingThing.Datagram
                        else -> CurrentCalculatingThing.LengthType
                    }
                }
            }

            CurrentCalculatingThing.Datagram -> {
                if (buffer.length == 5) {
                    when (buffer.first()) {
                        '0' -> {
                            packetBuilder.packetValue(buffer.toString())
                            currentEvent = CurrentCalculatingThing.Header
                            counterOfPackets++
                            resultList.add(packetBuilder.build())
                            packetBuilder = Packet.Builder()
                            buffer.clear()
                        }

                        '1' -> {
                            packetBuilder.packetValue(buffer.toString())
                            buffer.clear()
                        }

                        else -> error("Illegal State")
                    }
                }
            }

            CurrentCalculatingThing.LengthType -> {
                when (buffer.toString()) {
                    "0" -> {
                        currentEvent = CurrentCalculatingThing.SubPacket.Zero
                        buffer.clear()
                    }

                    "1" -> {
                        currentEvent = CurrentCalculatingThing.SubPacket.One
                        buffer.clear()
                    }

                    else -> error("CurrentCalculatingThing.LengthType Illegal State")
                }
            }

            is CurrentCalculatingThing.SubPacket.Zero -> {
                if (buffer.length == 15) {
                    val amountOfBitsForSubpackets = buffer.toString().parseToLongRadix(2)
                    printlnDebug { "buffer = $buffer amountOfSubPackets=$amountOfBitsForSubpackets" }
                    var collector = ""
                    repeat(amountOfBitsForSubpackets.toInt()) {
                        collector += iterator.next()
                    }
                    val packets = iterateAndCollectPackets(collector.iterator())
                    printlnDebug { "$packets" }
                    packetBuilder.subPackets(packets)

                    resultList.add(packetBuilder.build())
                    packetBuilder = Packet.Builder()
                    currentEvent = CurrentCalculatingThing.Header
                    counterOfPackets++
                    buffer.clear()
                }
            }

            is CurrentCalculatingThing.SubPacket.One -> {
                if (buffer.length == 11) {
                    val amountOfSubpackets = buffer.toString().parseToLongRadix(2)
                    printlnDebug { "buffer = $buffer  amountOfSubPackets=$amountOfSubpackets" }
                    val packets =
                        iterateAndCollectPackets(iterator, amountOfSubpackets)
                    printlnDebug { "$packets" }
                    packetBuilder.subPackets(packets)

                    resultList.add(packetBuilder.build())
                    packetBuilder = Packet.Builder()
                    buffer.clear()

                    currentEvent = CurrentCalculatingThing.Header
                    counterOfPackets++
                }
            }
        }
    }

    printlnDebug { "COUNTER OF PACKETS: $counterOfPackets" }

    return resultList
}


fun main() {


    fun part1(input: List<String>): Long {
        val output = input.first()
            .trimEnd('0')
            .map { mapHexToBitArray(it) }
            .flatten()

        val mappedBiteString = output.joinToString("") { if (it) "1" else "0" }
        printlnDebug { mappedBiteString }

        val mappedResult = iterateAndCollectPackets(mappedBiteString.iterator())
        printlnDebug { "mappedResult$mappedResult" }

        return mappedResult.sumOf { it.versionSum() }
    }

    fun part2(input: List<String>): Long {
        val output = input.first()
            .trimEnd('0')
            .map { mapHexToBitArray(it) }
            .flatten()

        val mappedBiteString = output.joinToString("") { if (it) "1" else "0" }
        printlnDebug { mappedBiteString }

        val mappedResult = iterateAndCollectPackets(mappedBiteString.iterator())
        printlnDebug { "mappedResult$mappedResult" }

        return mappedResult.sumOf { it.calculateValue() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    val part1Test = part2(testInput)

    println(part1Test)
    check(part1Test == 54L)

    val input = readInput("Day16")
    val part1Result = part1(input)
    val part2Result = part2(input)

    println(part1Result)
    println(part2Result)

    check(part1Result == 993L)
    check(part2Result == 144595909277L)
}