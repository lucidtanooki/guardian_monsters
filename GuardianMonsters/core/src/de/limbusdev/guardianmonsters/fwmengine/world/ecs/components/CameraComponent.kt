package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils
import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get
import de.limbusdev.utils.geometry.IntRect


/**
 * The camera system updates the cameras position according to the hero's position by keeping it
 * inside the map's bounds, unless a map is smaller than the Camera's field of view.
 *
 * @author Georg Eckert 2015-11-25
 */

/**
 * Simple [LimbusBehaviour] to store the camera position.
 *
 * @author Georg Eckert 2015-11-22
 */
class CameraComponent(private val camera: OrthographicCamera, tiledMap: TiledMap) : LimbusBehaviour()
{
    companion object
    {
        const val className = "CameraComponent"
    }
    // --------------------------------------------------------------------------------------------- PROPERTIES
    private val mapOutline: IntRect    // Bounds of map to be rendered

    init
    {
        // Get the maps bounds
        val width = tiledMap.properties["width",  10] * Constant.TILE_SIZE
        val height = tiledMap.properties["height", 10] * Constant.TILE_SIZE
        mapOutline = IntRect(0, 0, width, height)
    }

    override fun update(deltaTime: Float)
    {
        if (mapOutline.width >= camera.viewportWidth && mapOutline.height >= camera.viewportHeight)
        {
            // If map is bigger than camera field
            camera.position.x = MathUtils.clamp(
                    transform.xf,
                    0 + camera.viewportWidth / 2,
                    mapOutline.width - camera.viewportWidth / 2)

            camera.position.y = MathUtils.clamp(
                    transform.yf,
                    0 + camera.viewportHeight / 2,
                    mapOutline.height - camera.viewportHeight / 2)

            // If camera field is bigger than map dimension
            /*camera.position.set(
                    MathUtils.lerp(camera.position.x, transform.xf, deltaTime*5),
                    MathUtils.lerp(camera.position.y, transform.yf, deltaTime*5),
                    0f
            )*/
        }
        else
        {
            // Soft Camera Movement with Interpolation
            // If camera field is bigger than map dimension
//            camera.position.set(
//                    MathUtils.lerp(camera.position.x, transform.nextX.f(), deltaTime),
//                    MathUtils.lerp(camera.position.y, transform.nextY.f(), deltaTime),
//                    0f
//            )
            camera.position.set(transform.xf, transform.yf, 0f)
        }
    }
}
