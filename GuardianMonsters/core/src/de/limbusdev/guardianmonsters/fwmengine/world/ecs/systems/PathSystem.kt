package de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.utils.TimeUtils

import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.ColliderComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.InputComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PathComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent
import de.limbusdev.guardianmonsters.utils.getComponent
import de.limbusdev.utils.geometry.IntVec2
import de.limbusdev.guardianmonsters.enums.SkyDirection


/**
 * Moves around entities with a [PathComponent] like persons, animals and so on
 * Created by georg on 30.11.15.
 */
class PathSystem(private val gameArea: GameArea) : EntitySystem()
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    companion object { const val TAG = "PathSystem" }

    private lateinit var entities: ImmutableArray<Entity>


    // --------------------------------------------------------------------------------------------- METHODS
    override fun addedToEngine(engine: Engine)
    {
        entities = engine.getEntitiesFor(Family.all(
                PositionComponent::class.java,
                ColliderComponent::class.java,
                PathComponent::class.java).exclude(
                InputComponent::class.java
        ).get())
    }

    override fun update(deltaTime: Float)
    {
        for (entity in entities)
        {
            val position = entity.getComponent<PositionComponent>()
            val collider = entity.getComponent<ColliderComponent>()
            val path     = entity.getComponent<PathComponent>()
            makeOneStep(position!!, path!!, collider!!)
            position.updateGridPosition()
        }
    }

    fun makeOneStep(position: PositionComponent, path: PathComponent, collider: ColliderComponent)
    {
        if (path.startMoving && !path.staticEntity)
        {
            // Define direction of movement
            when (path.path.get(path.currentDir))
            {
                SkyDirection.N ->
                {
                    position.nextX = position.x
                    position.nextY = position.y + Constant.TILE_SIZE
                }
                SkyDirection.W ->
                {
                    position.nextX = position.x - Constant.TILE_SIZE
                    position.nextY = position.y
                }
                SkyDirection.E ->
                {
                    position.nextX = position.x + Constant.TILE_SIZE
                    position.nextY = position.y
                }
                SkyDirection.S ->
                {
                    position.nextX = position.x
                    position.nextY = position.y - Constant.TILE_SIZE
                }
                else ->
                {
                    position.nextY = position.x
                    position.nextY = position.y
                }
            }

            // Check whether movement is possible or blocked by a collider
            val nextPos = IntVec2(0, 0)
            for (r in gameArea.dynamicColliders.get(position.layer))
            {
                nextPos.addOffset(Constant.TILE_SIZE/2)
                if (!collider.collider.equals(r) && r.contains(nextPos)) { return }
            }

            collider.collider += position
            position.lastPixelStep = TimeUtils.millis()
            path.moving = true
            path.startMoving = false
        }

        // If moving, check whether next pixel step should take place
        if (
                !path.staticEntity
                && path.moving
                && TimeUtils.timeSinceMillis(position.lastPixelStep) > Constant.ONE_STEP_DURATION_PERSON
                && !path.talking
        ) {
            position += when (path.path.get(path.currentDir))
            {
                SkyDirection.N -> IntVec2(0,1)
                SkyDirection.W -> IntVec2(-1,0)
                SkyDirection.E -> IntVec2(1,0)
                SkyDirection.S -> IntVec2(0,-1)
                else           -> { path.stop(); IntVec2() }
            }

            // if stopping, count up stopping time
            position.lastPixelStep = TimeUtils.millis()

            when (path.path.get(path.currentDir))
            {
                SkyDirection.N -> if (position.y == position.nextY) { path.moving = false }
                SkyDirection.E -> if (position.x == position.nextX) { path.moving = false }
                SkyDirection.W -> if (position.x == position.nextX) { path.moving = false }
                SkyDirection.S -> if (position.y == position.nextY) { path.moving = false }
                else           -> if (path.stopCounter == 0)        { path.moving = false }
            }

            // Go on if finger is still on screen
            if (!path.moving)
            {
                path.next()
                path.startMoving = true
            }
        }
    }
}
