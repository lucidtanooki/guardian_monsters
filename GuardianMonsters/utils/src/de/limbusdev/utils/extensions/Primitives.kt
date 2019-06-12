package de.limbusdev.utils.extensions

/**
 * Primitives
 * @author Georg Eckert 2019
 */
fun Int.f(): Float = toFloat()

fun Float.i(): Int = toInt()

fun Boolean.toggle() = not()

/** returns enum as lower case string */
fun Enum<*>.toLCString() = toString().toLowerCase()