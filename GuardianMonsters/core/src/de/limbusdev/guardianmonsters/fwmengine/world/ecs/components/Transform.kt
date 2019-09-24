package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils

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
class Transform(var gameObject: LimbusGameObject) : IntRect(0,0,16,16)
{
    var layer: Int = 0

    val asRectangle get() = IntRect(x, y, width, height)

    // --------------------------------------------------------------------------------------------- PROPERTIES

    var onGrid : IntVec2
        get() = IntVec2(x / Constant.TILE_SIZE, y / Constant.TILE_SIZE)
        set(value) { x = value.x ; y = value.y }



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
        //if(gameObject.name == "Hero") { println("$x|$y") }
    }

    /** Runs every 1/60 s */
    fun update60fps()
    {

    }
}
