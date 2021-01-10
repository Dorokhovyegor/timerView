package com.dorokhov.ydtimerview.converter

/**
 * Converts from milliseconds to seconds, minutes and hours
 * */
fun Long.convertFromMsToS(): Long {
    return this / 1_000L
}

fun Long.convertFromMsToM(): Long {
    return this / 60_000L
}

fun Long.convertFromMsToH(): Long {
    return this / 3_600_000L
}

/**
 * Converts from seconds to milliseconds, minutes and hours
 * */
fun Long.convertFromStoMs(): Long {
    return this * 1_000L
}

fun Long.convertFromStoM(): Long {
    return this / 60L
}

fun Long.convertFromStoH(): Long {
    return this / 3_600L
}


/**
 * Convert from minutes to milliseconds, seconds and hours
 * */
fun Long.convertFromMtoMs(): Long {
    return this * 60_000L
}

fun Long.convertFromMtoS(): Long {
    return this * 60L
}

fun Long.convertFromMtoH(): Long {
    return this / 60L
}

/**
 * Convert from hours to milliseconds, seconds and minutes
 * */
fun Long.convertFromHtoMs(): Long {
    return this * 3_600_000L
}

fun Long.convertFromHtoS(): Long {
    return this * 3500L
}

fun Long.convertFromHtoMinutes(): Long {
    return this * 60L
}
