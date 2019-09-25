package de.limbusdev.utils.geometry

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle

/**
 * Rectangle with integers for exact positioning
 *
 * Created by Georg Eckert on 25.11.15.
 */
open class IntRect : IntVec2
{
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ ATTRIBUTES
    // .................................................... public
    var width: Int = 0
    var height: Int = 0
    var ID: Int = 0

    val xMax get() = x + width - 1
    val xMin get() = x
    val yMax get() = y + height - 1
    val yMin get() = y

    val widthf  get() = width.toFloat()
    val heightf get() = height.toFloat()

    // .................................................... static
    companion object
    {
        var IDcount = 0
    }

    //.............................................................................................. CONSTRUCTOR
    constructor(x: Int, y: Int, width: Int, height: Int) : super(x, y)
    {
        this.ID = IDcount
        IDcount++
        this.width = width
        this.height = height
    }

    /**
     * Rounds the given values
     * @param x
     * @param y
     * @param width
     * @param height
     */
    constructor(x: Float, y: Float, width: Float, height: Float) : this(
            MathUtils.round(x),
            MathUtils.round(y),
            MathUtils.round(width),
            MathUtils.round(height)) {}

    constructor(r: Rectangle) : super(MathUtils.round(r.x), MathUtils.round(r.y))
    {
        this.ID = IDcount
        IntRect.IDcount++
        this.width = MathUtils.round(r.width)
        this.height = MathUtils.round(r.height)
    }

    // ............................................................................................. METHODS
    operator fun contains(point: IntVec2): Boolean
    {
        return (point.x in xMin..xMax && point.y in yMin..yMax)
    }

    fun contains(x: Int, y: Int) : Boolean = contains(IntVec2(x,y))

    fun overlaps(other: IntRect) : Boolean
    {
        return  (other.xMin in xMin..xMax && other.yMin in yMin..yMax) ||
                (other.xMax in xMin..xMax && other.yMin in yMin..yMax) ||
                (other.xMax in xMin..xMax && other.yMax in yMin..yMax) ||
                (other.xMin in xMin..xMax && other.yMax in yMin..yMax)
    }

    fun equals(r: IntRect): Boolean
    {
        return (r.ID == this.ID)
    }

    override fun toString(): String
    {
        return "IntRect(x = $x y = $y width = $width height = $height)"
    }
}
