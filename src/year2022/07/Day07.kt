package year2022.`07`

import readInput

sealed class Record {

    abstract val title: String
    abstract var size: Long

    data class File(
        override val title: String,
        override var size: Long,
        val parent: Dir,
    ) : Record()

    data class Dir(
        override val title: String,
        override var size: Long,
        val parent: Dir?,
        val files: MutableList<Record>,
    ) : Record()
}

sealed class Line {
    object LS : Line()
    data class CD(val directory: String) : Line()
    data class Dir(val title: String) : Line()
    data class File(val size: Long, val title: String) : Line()

    companion object {
        fun parseLine(string: String): Line {
            val nodes = string.split(" ")
            return if (nodes.first() == "$") {
                when (nodes[1]) {
                    "ls" -> LS
                    "cd" -> CD(nodes[2])
                    else -> error("!!")
                }
            } else {
                if (nodes[0] == "dir") {
                    Dir(nodes[1])
                } else {
                    File(nodes[0].toLong(), nodes[1])
                }
            }
        }
    }
}

fun calculateSizes(currentRecord: Record): Long {
    return when (currentRecord) {
        is Record.Dir -> {
            val size = currentRecord.files.sumOf { calculateSizes(it) }
            currentRecord.size = size
            size
        }
        is Record.File -> currentRecord.size
    }
}

fun fillListWithDirsThatAre(
    currentDir: Record,
    list: MutableList<Record>,
    predicate: (Long) -> Boolean
) {
    if (currentDir is Record.Dir) {
        if (predicate(currentDir.size)) {
            list.add(currentDir)
        }
        currentDir.files.forEach { fillListWithDirsThatAre(it, list, predicate) }
    }
}

fun builtTree(input: List<String>): Record.Dir {
    val rootDir: Record.Dir = Record.Dir("/", 0, null, mutableListOf())
    var currentDir: Record.Dir = rootDir

    input
        .drop(1)
        .map { Line.parseLine(it) }
        .forEach { command ->
            when (command) {
                is Line.CD -> {
                    currentDir = if (command.directory == "..") {
                        currentDir.parent!!
                    } else {
                        currentDir
                            .files
                            .filterIsInstance<Record.Dir>()
                            .find { file -> file.title == command.directory }!!
                    }
                }
                is Line.File -> {
                    val record = Record.File(
                        title = command.title,
                        size = command.size,
                        parent = currentDir,
                    )
                    currentDir.files.add(record)
                }
                is Line.Dir -> {
                    val record = Record.Dir(command.title, 0, currentDir, mutableListOf())
                    currentDir.files.add(record)
                }
                is Line.LS -> Unit
            }
        }
    return rootDir
}

fun main() {

    fun part1(input: List<String>): Long {
        val totalSize = 100_000
        val rootDir: Record.Dir = builtTree(input)
            .also { calculateSizes(it) }
        return mutableListOf<Record>()
            .also { fillListWithDirsThatAre(rootDir, it) { size -> size <= totalSize } }
            .sumOf { it.size }
    }

    fun part2(input: List<String>): Long {
        val rootDir: Record.Dir = builtTree(input)
        val filledSize = calculateSizes(rootDir)

        val updateSize = 30_000_000
        val totalSize = 70_000_000
        val unusedSpace = updateSize - (totalSize - filledSize)

        return mutableListOf<Record>()
            .also { fillListWithDirsThatAre(rootDir, it) { size -> size >= unusedSpace } }
            .minOf { it.size }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    val part1Test = part2(testInput)

    println(part1Test)
    check(part1Test == 24933642L)

    val input = readInput("Day07")
    println("Part1: " + part1(input))
    println("Part2: " + part2(input))
}
