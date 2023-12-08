package utils

fun gcf(a: Long, b: Long): Long {
    return if (b == 0L) a else gcf(b, a % b)
}

fun lcm(input1: Long, input2: Long): Long {
    return input1 * input2 / gcf(input1, input2)
}