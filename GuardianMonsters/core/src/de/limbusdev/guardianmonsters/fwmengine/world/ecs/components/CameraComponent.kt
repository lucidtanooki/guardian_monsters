package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils
import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.GdxBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get
import de.limbusdev.utils.geometry.IntRect


import de.limbusdev.utils.geometry.IntVec2

/**
 * The camera system updates the cameras position according to the hero's position by keeping it
 * inside the map's bounds, unless a map is smaller than the Camera's field of view.
 *
 * @author Georg Eckert 2015-11-25
 */

/**
 * Simple [GdxBehaviour] to store the camera position.
 *
 * @author Georg Eckert 2015-11-22
 */
class CameraComponent(private val camera: OrthographicCamera, tiledMap: TiledMap) : GdxBehaviour()
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    private val mapOutline: IntRect    // Bounds of map to be rendered

    var position = IntVec2(0,0)

    init
    {
        // Get the maps bounds
        val width = tiledMap.properties["width",  10] * Constant.TILE_SIZE
        val height = tiledMap.properties["height", 10] * Constant.TILE_SIZE
        mapOutline = IntRect(0, 0, width, height)
    }

    override fun update(deltaTime: Float)
    {
        val position = gameObject?.get<PositionComponent>()
        if(position != null)
        {
            if (mapOutline.width >= camera.viewportWidth && mapOutline.height >= camera.viewportHeight) {
                // If map is bigger than camera field
                camera.position.x = MathUtils.clamp(
                        position.xf,
                        0 + camera.viewportWidth / 2,
                        mapOutline.width - camera.viewportWidth / 2)

                camera.position.y = MathUtils.clamp(
                        position.yf,
                        0 + camera.viewportHeight / 2,
                        mapOutline.height - camera.viewportHeight / 2)
            } else {
                // If camera field is bigger than map dimension
                camera.position.set(position.xf, position.yf, 0f)
            }
        }
    }
}
