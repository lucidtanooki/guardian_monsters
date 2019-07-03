package de.limbusdev.guardianmonsters.fwmengine.world.model

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle

import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.utils.geometry.IntRect


/**
 * WarpPoint
 * Rectangle containing information about place change
 *
 * @author Georg Eckert 2015-11-23
 */
class WarpPoint
(
        var targetWarpPointID: Int,
        warpField: Rectangle,
        var targetID: Int
)
    : IntRect(warpField)
{
    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        x = MathUtils.round(warpField.x + Constant.TILE_SIZE / 2)
        y = MathUtils.round(warpField.y + Constant.TILE_SIZE / 2)
    }
}
