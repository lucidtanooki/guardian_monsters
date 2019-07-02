package de.limbusdev.utils.geometry

open class IntVec2(var x: Int, var y: Int)
{
    val xf : Float get() = x.toFloat()
    val yf : Float get() = y.toFloat()

    constructor(other: IntVec2) : this(other.x, other.y)

    override fun toString(): String
    {
        return "IntVec2(x=$x, y=$y)"
    }

    operator fun plusAssign(other: IntVec2)
    {
        x += other.x
        y += other.y
    }

    override fun equals(other: Any?): Boolean
    {
        if (this === other) { return true }
        if (javaClass != other?.javaClass) { return false }

        other as IntVec2

        return (x == other.x && y == other.y)
    }

    override fun hashCode(): Int = 31 * x + y
}