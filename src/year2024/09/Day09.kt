package year2024.`09`

import readInput

private const val CURRENT_DAY = "09"

const val DEBUG = false

private fun parseLineInto(
    line: String,
): List<IndexedFile> {
    val resFiles = mutableListOf<IndexedFile>()
    line.forEachIndexed { index, char ->
        resFiles += if (index % 2 == 0) {
            IndexedFile.File(
                char.toString().toInt(),
                index / 2,
            )
        } else {
            IndexedFile.Empty(
                char.toString().toInt(),
            )
        }
    }
    return resFiles
}


sealed class IndexedFile {
    abstract val size: Int

    data class File(
        override val size: Int,
        val startIndex: Int,
    ) : IndexedFile() {
        override fun toString(): String {
            if (DEBUG) {
                return "|${startIndex.toString().repeat(size)}|"
            } else {
                return "${startIndex.toString().repeat(size)}"
            }
        }
    }

    data class Empty(
        override val size: Int,
    ) : IndexedFile() {
        override fun toString(): String {
            if (DEBUG) {
                return "{${".".toString().repeat(size)}}"
            } else {
                return "${".".toString().repeat(size)}"
            }
        }
    }
}

data class FileBlock(
    val code: String,
) {
    override fun toString(): String {
        return code
    }
}

fun List<FileBlock>.performRearrange(): List<FileBlock> {
    val initialMutable = this.toMutableList()

    var leftP = 0
    var rightP = this.lastIndex

    while (leftP <= rightP) {
        while (initialMutable[leftP].code != ".") {
            leftP++
        }
        while (initialMutable[rightP].code == ".") {
            rightP--
        }

        initialMutable[leftP] = initialMutable[rightP]
        initialMutable[rightP] = FileBlock(".")
    }

    if (initialMutable[rightP].code == "." && initialMutable[leftP].code != ".") {
        initialMutable[rightP] = initialMutable[leftP]
        initialMutable[leftP] = FileBlock(".")
    }

    return initialMutable
}

fun List<FileBlock>.checkSum(): Long {
    return this.mapIndexedNotNull { index, item ->
        val res = item.code.toLongOrNull()
        if (res == null) {
            null
        } else {
            res * index
        }
    }.sum()
}

fun List<IndexedFile>.mapToBlocksArray(): List<FileBlock> {
    val resultList = mutableListOf<FileBlock>()
    forEach { file ->
        when (file) {
            is IndexedFile.Empty -> {
                repeat(file.size) {
                    resultList.add(FileBlock("."))
                }
            }

            is IndexedFile.File -> {
                repeat(file.size) {
                    resultList.add(FileBlock(file.startIndex.toString()))
                }
            }
        }
    }
    return resultList
}


fun List<IndexedFile>.performRearrange2(): List<IndexedFile> {
    val initialMutable = this.toMutableList()

    var leftP = 0
    var rightP = this.lastIndex

    while (leftP <= rightP) {
        while (initialMutable[leftP] !is IndexedFile.Empty) {
            leftP++
        }
        while (initialMutable[rightP] !is IndexedFile.File) {
            rightP--
        }


        val currentEmptyFile = initialMutable[leftP]
        val currentRealFile = initialMutable[rightP]

        initialMutable.printToBeautifulLine("BEFORE: ")
        if (currentEmptyFile.size >= currentRealFile.size) {
            val sizeDelta = currentEmptyFile.size - (currentEmptyFile.size - currentRealFile.size)
            initialMutable[leftP] = currentRealFile
            initialMutable[rightP] = IndexedFile.Empty(sizeDelta)
            leftP++
            initialMutable.add(leftP, IndexedFile.Empty(currentEmptyFile.size - sizeDelta))
        } else {
            // region new
            var buffLeftIndex = leftP
            while (buffLeftIndex <= rightP && buffLeftIndex >= 0) {
                while (initialMutable[buffLeftIndex] !is IndexedFile.Empty) {
                    buffLeftIndex++
                }
                val buffEmptyFile = initialMutable[buffLeftIndex]

                if (buffLeftIndex <= rightP) {
                } else continue
                if (buffEmptyFile.size >= currentRealFile.size) {
                    val sizeDelta = buffEmptyFile.size - (buffEmptyFile.size - currentRealFile.size)
                    initialMutable[buffLeftIndex] = currentRealFile
                    val preLeft = initialMutable[rightP - 1]
                    if (preLeft is IndexedFile.Empty) {
                        initialMutable[rightP - 1] = IndexedFile.Empty(preLeft.size + sizeDelta)
                        initialMutable.removeAt(rightP)
                        rightP++
                    } else {
                        initialMutable[rightP] = IndexedFile.Empty(sizeDelta)
                    }

                    // insert here union
                    initialMutable.add(buffLeftIndex + 1, IndexedFile.Empty(buffEmptyFile.size - sizeDelta))
                    buffLeftIndex = Int.MAX_VALUE//TODO?
                }

                buffLeftIndex++
            }
            rightP--
            // endregion
        }
        initialMutable.printToBeautifulLine("AFTER : ")
    }
    return initialMutable
}

private fun List<IndexedFile>.printToBeautifulLine(prefix: String = ""): String {
    val res = if (DEBUG) {
        joinToString(separator = "", prefix = prefix).also {
            println(it)
        }
    } else {
        ""
    }

    return res
}


fun main() {

    fun part1(input: List<String>): Long {
        val lines = parseLineInto(input.first())
            .mapToBlocksArray()
        val rearranged = lines.performRearrange()
        return rearranged.checkSum()
    }

    fun part2(input: List<String>): Long {
        val lines = parseLineInto(input.first())
            .performRearrange2()
        lines.printToBeautifulLine()
        return lines.mapToBlocksArray().checkSum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")

    val part1Test = part1(testInput)
    println(part1Test)
    check(part1Test == 1928L)

    val part2Test = part2(testInput)
    println(part2Test)
    check(part2Test == 2858L)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 6395800119709L)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 6418529470362L)
}
