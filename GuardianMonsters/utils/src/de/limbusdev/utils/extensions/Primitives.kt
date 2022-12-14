package de.limbusdev.utils.extensions

import com.badlogic.gdx.math.MathUtils

/**
 * Primitives
 * @author Georg Eckert 2019
 */
fun Int.f(): Float = toFloat()

fun Float.i(): Int = toInt()

fun Boolean.toggle() = not()

/** returns enum as lower case string */
fun Enum<*>.toLCString() = toString().toLowerCase()

/** returns enum as upper case string */
fun Enum<*>.toUCString() = toString().toUpperCase()

/** Returns this floats cosine with it interpreted as degrees. */
val Float.cos : Float get() = MathUtils.cosDeg(this)

/** Returns this floats cosine with it interpreted as radians. */
val Float.cosr : Float get() = MathUtils.cos(this)

/** Returns this floats sine with it interpreted as degrees. */
val Float.sin : Float get() = MathUtils.sinDeg(this)

/** Returns this floats sine with it interpreted as radians. */
val Float.sinr : Float get() = MathUtils.sin(this)

/** Removes the character with the given index, looking from the string's end (0 is last character) */
fun String.removeLast(indexFromEnd: Int): String
{
    if(indexFromEnd >= this.length || indexFromEnd < 0) { return "" }
    return this.substring(0,this.length-(indexFromEnd+1))
}

fun String.subStringFromEnd(indexFromEnd: Int): String
{
    if(indexFromEnd >= this.length || indexFromEnd < 0) { return "" }
    return this.substring((this.length-1)-indexFromEnd)
}