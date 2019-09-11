package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity

import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.utils.extensions.f
import de.limbusdev.utils.geometry.IntRect
import de.limbusdev.utils.geometry.IntVec2


/**
 * Simple [Component] which holds the [Entity]'s x and y coordinates, it's width and height and the
 * next potential position to move to. The time when the entity last moved is stored as well.
 *
 * @author Georg Eckert 2015-11-22
 */
class TransformComponent() : LimbusBehaviour(), Component
{
    constructor(enabled: Boolean, x: Int, y: Int, width: Int, height: Int, layer: Int) : this()
    {
        this.enabled = enabled
        this.x = x
        this.y = y
        this.width = width
        this.height = height
        this.layer = layer
    }

    override val defaultJson = """
            {
                "enabled": true,
                "x": 0,
                "y": 0,
                "width": 16,
                "height": 16,
                "layer": 0
            }
        """.trimIndent()

    var x = 0
    var y = 0
    var width = 16
    var height = 16
    var layer: Int = 0

    var asRectangle = IntRect(x, y, width, height)

    // --------------------------------------------------------------------------------------------- PROPERTIES


    val xf get() = x.f()
    val yf get() = y.f()
    val widthf get() = width.f()
    val heightf get() = height.f()


    var nextX           : Int = 0
    var nextY           : Int = 0
    var lastPixelStep   : Long = 0 // ms
    var onGrid          : IntVec2 = IntVec2(x / Constant.TILE_SIZE, y / Constant.TILE_SIZE)


    // --------------------------------------------------------------------------------------------- METHODS
    fun moveBy(xDiff: Int, yDiff: Int)
    {
        x += xDiff
        y += yDiff
    }

    val center get() = IntVec2(

                onGrid.x * Constant.TILE_SIZE + Constant.TILE_SIZE / 2,
                onGrid.y * Constant.TILE_SIZE + Constant.TILE_SIZE / 2
    )

    fun updateGridPosition()
    {
        onGrid.x = x / Constant.TILE_SIZE
        onGrid.y = y / Constant.TILE_SIZE
    }

    override fun update(deltaTime: Float)
    {

    }
}
