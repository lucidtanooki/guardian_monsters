package de.limbusdev.utils.geometry

open class IntVec2(var x: Int, var y: Int)
{
    val xf : Float get() = x.toFloat()
    val yf : Float get() = y.toFloat()

    override fun toString(): String
    {
        return "IntVec2(x=$x, y=$y)"
    }

    operator fun plusAssign(other: IntVec2)
    {
        x += other.x
        y += other.y
    }
}