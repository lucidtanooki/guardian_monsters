package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity

import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject
import de.limbusdev.utils.extensions.f
import de.limbusdev.utils.geometry.IntRect
import de.limbusdev.utils.geometry.IntVec2


/**
 * Simple [Component] which holds the [Entity]'s x and y coordinates, it's width and height and the
 * next potential position to move to. The time when the entity last moved is stored as well.
 *
 * @author Georg Eckert 2015-11-22
 */
class Transform(var gameObject: LimbusGameObject) : Component
{
    var x = 0
    var y = 0
    var width = 16
    var height = 16
    var layer: Int = 0

    val asRectangle get() = IntRect(x, y, width, height)

    // --------------------------------------------------------------------------------------------- PROPERTIES


    val xf get() = x.f()
    val yf get() = y.f()
    val widthf get() = width.f()
    val heightf get() = height.f()


    var nextX           : Int = 0
    var nextY           : Int = 0
    var lastPixelStep   : Long = 0 // ms
    val onGrid : IntVec2 get() = IntVec2(x / Constant.TILE_SIZE, y / Constant.TILE_SIZE)


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

    fun update(deltaTime: Float)
    {

    }
}
