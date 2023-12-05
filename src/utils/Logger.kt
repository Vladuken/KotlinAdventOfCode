package utils

private const val DEBUG_PRINT_ENABLED = true

fun printDebug(block: () -> Any) {
    if (DEBUG_PRINT_ENABLED) {
        print(block())
    }
}

fun printlnDebug(block: () -> Any) {
    if (DEBUG_PRINT_ENABLED) {
        println(block())
    }
}
