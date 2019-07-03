package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity

import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.utils.geometry.IntRect
import de.limbusdev.utils.geometry.IntVec2


/**
 * Simple [Component] which holds the [Entity]'s x and y coordinates, it's width and height and the
 * next potential position to move to. The time when the entity last moved is stored as well.
 *
 * @author Georg Eckert 2015-11-22
 */
class PositionComponent
(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        var layer: Int
)
    : IntRect(x, y, width, height), Component
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    var nextX           : Int = 0
    var nextY           : Int = 0
    var lastPixelStep   : Long = 0 // ms
    var onGrid          : IntVec2 = IntVec2(x / Constant.TILE_SIZE, y / Constant.TILE_SIZE)


    // --------------------------------------------------------------------------------------------- METHODS
    val center get() = IntVec2(

                onGrid.x * Constant.TILE_SIZE + Constant.TILE_SIZE / 2,
                onGrid.y * Constant.TILE_SIZE + Constant.TILE_SIZE / 2
    )

    fun updateGridPosition()
    {
        onGrid.x = x / Constant.TILE_SIZE
        onGrid.y = y / Constant.TILE_SIZE
    }
}
