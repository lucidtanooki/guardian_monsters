package de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils

import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.CameraComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get
import de.limbusdev.guardianmonsters.utils.FamilyAll
import de.limbusdev.utils.geometry.IntRect

/**
 * The camera system updates the cameras position according to the hero's position by keeping it
 * inside the lmaps bounds, unless a map is smaller than the Camera.
 * field of view
 *
 * @author Georg Eckert 2015-11-25
 */

/**
 * Default constructor that will initialise an EntitySystem with priority 0.
 */
class CameraSystem(private val camera: OrthographicCamera, tiledMap: TiledMap) : EntitySystem()
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    private val mapOutline: IntRect    // Bounds of map to be rendered

    private var entities: ImmutableArray<Entity>? = null


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        // Get the maps bounds
        val width = tiledMap.properties["width",  10] * Constant.TILE_SIZE
        val height = tiledMap.properties["height", 10] * Constant.TILE_SIZE
        mapOutline = IntRect(0, 0, width, height)
    }


    // --------------------------------------------------------------------------------------------- METHODS
    override fun addedToEngine(engine: Engine)
    {
        entities = engine.getEntitiesFor(FamilyAll(PositionComponent::class, CameraComponent::class).get())
    }

    override fun update(deltaTime: Float)
    {
        // Move all cameras of entities with a {@link CameraComponent} (hero only)
        for (entity in entities!!)
        {
            val position = Components.getPositionComponent(entity)

            if (mapOutline.width >= camera.viewportWidth && mapOutline.height >= camera.viewportHeight)
            {
                // If map is bigger than camera field
                camera.position.x = MathUtils.clamp(
                        position.xf,
                        0 + camera.viewportWidth / 2,
                        mapOutline.width - camera.viewportWidth / 2)

                camera.position.y = MathUtils.clamp(
                        position.yf,
                        0 + camera.viewportHeight / 2,
                        mapOutline.height - camera.viewportHeight / 2)
            }
            else
            {
                // If camera field is bigger than map dimension
                camera.position.set(position.xf, position.yf, 0f)
            }
        }
    }
}
