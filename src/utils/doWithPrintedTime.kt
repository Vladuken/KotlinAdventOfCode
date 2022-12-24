package utils

import kotlin.system.measureTimeMillis

fun <T> doWithPrintedTime(
    tag: String = "",
    block: () -> T,
): T {
    var result: T
    measureTimeMillis {
        result = block()
        println("$tag answer:".trim().capitalize() + " " + result)
    }.let {
        println("$tag time spend: $it ms")
    }

    return result
}
