package utils

import java.math.BigInteger

fun String.parseToLongRadix(num: Int = 2): Long = BigInteger(this, num).toLong()
