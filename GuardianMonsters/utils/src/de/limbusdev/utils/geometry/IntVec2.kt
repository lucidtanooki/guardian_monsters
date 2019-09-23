package de.limbusdev.utils.geometry

open class IntVec2()
{
    var x: Int = 0
    var y: Int = 0

    val xf : Float get() = x.toFloat()
    val yf : Float get() = y.toFloat()

    constructor(x: Int = 0, y: Int = 0) : this()
    {
        this.x = x
        this.y = y
    }

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

    /** Returns a new IntVec2 where x and y have set off about the given value. */
    fun offset(o: Int) = offset(o,o)

    /** Returns a new IntVec2 where x and y have set off about the given values. */
    fun offset(xo: Int, yo: Int) = IntVec2(x+xo, y+yo)

    /** Changes this IntVec2's values about the given value. */
    fun addOffset(o: Int) { addOffset(o,o) }

    /** Changes this IntVec2's values about the given values. */
    fun addOffset(xo: Int, yo: Int)
    {
        x += xo
        y += yo
    }


    override fun equals(other: Any?): Boolean
    {
        if (this === other) { return true }
        if (javaClass != other?.javaClass) { return false }

        other as IntVec2

        return (x == other.x && y == other.y)
    }

    override fun hashCode(): Int = 31 * x + y

    operator fun plus(other: IntVec2) = IntVec2(x+other.x, y+other.y)
    operator fun minus(other: IntVec2) = IntVec2(x-other.x, y-other.y)
}