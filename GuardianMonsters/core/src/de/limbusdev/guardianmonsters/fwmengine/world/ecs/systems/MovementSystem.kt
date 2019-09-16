package de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.TimeUtils

import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.EntityComponentSystem
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.World
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.*
import de.limbusdev.guardianmonsters.fwmengine.world.model.WarpPoint
import de.limbusdev.guardianmonsters.guardians.battle.BattleFactory
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.utils.createRectangle
import de.limbusdev.guardianmonsters.utils.getComponent
import de.limbusdev.utils.geometry.IntRect
import de.limbusdev.utils.geometry.IntVec2
import de.limbusdev.utils.logDebug


/**
 * @author Georg Eckert 2017
 */
class MovementSystem
(
        private val ecs: EntityComponentSystem,
        private val warpPoints: ArrayMap<Int, Array<WarpPoint>>,
        private val healFields: ArrayMap<Int, Array<Rectangle>>
)
    : EntitySystem()
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    companion object{ const val TAG = "MovementSystem" }

    val newFrameEveryXPixels = 8
    var stepsSinceLastFrameUpdate = 0
    var elapsedTime = 0f

    // --------------------------------------------------------------------------------------------- METHODS
    override fun addedToEngine(engine: Engine)
    {

    }

    override fun update(deltaTime: Float)
    {
        elapsedTime+=deltaTime

        // Update hero
        checkWarp()
        updateHero()


    }

    /** Check whether hero enters warp area */
    private fun checkWarp()
    {
        val pos = Transform(LimbusGameObject()) // TODO
        val heroArea = createRectangle(IntRect(pos.x, pos.y, pos.width, pos.height))

        // Check whether hero enters warp area
        if(warpPoints.get(pos.layer) == null) { return }
        for (w in warpPoints.get(pos.layer))
        {
            if (heroArea.contains(w.xf, w.yf))
            {
                logDebug(TAG) { "Changing to Map ${w.targetID}" }
                ecs.changeGameArea(w.targetID, w.targetWarpPointID)
            }
        }
    }

    fun checkHeal()
    {
        val hero = World.hero
        val heroArea = createRectangle(IntRect(hero.transform.x, hero.transform.y, hero.transform.width, hero.transform.height))

        // Check whether hero enters warp area
        if(healFields.get(hero.transform.layer) == null) { return }
        for (h in healFields.get(hero.transform.layer))
        {
            if (heroArea.contains(h.x + h.width / 2, h.y + h.height / 2))
            {
                // Heal Team
                logDebug(TAG) { "Entered Healing Area" }
                val team = hero.get<TeamComponent>()!!.team
                val teamHurt = false
                team.values().forEach { guardian -> guardian.stats.healCompletely() }
            }
        }
    }

    fun updateHero()
    {
        val hero = World.hero
        // Only move hero, when player is not speaking to an entity
        if (!hero.get<InputComponent>()!!.talking)
        {
            makeOneStep(

                    hero.transform,
                    hero.get<InputComponent>()!!,
                    hero.get<ColliderComponent>()!!
            )
        }
    }

    /**
     * Moves the entity by 1 tile
     * @param transform
     * @param input
     * @param collider
     */
    fun makeOneStep
    (
            transform: Transform,
            input: InputComponent,
            collider: ColliderComponent
    ) {
        // Initialize hero Movement
        if (input.startMoving && TimeUtils.timeSinceMillis(input.firstTip) > 100 && input.touchDown)
        {
            // Define potential next position according to the input direction
            when (input.skyDir)
            {
                SkyDirection.N ->
                {
                    transform.nextX = transform.x
                    transform.nextY = transform.y + Constant.TILE_SIZE
                }
                SkyDirection.W ->
                {
                    transform.nextX = transform.x - Constant.TILE_SIZE
                    transform.nextY = transform.y
                }
                SkyDirection.E ->
                {
                    transform.nextX = transform.x + Constant.TILE_SIZE
                    transform.nextY = transform.y
                }
                else ->
                {
                    transform.nextX = transform.x
                    transform.nextY = transform.y - Constant.TILE_SIZE
                }
            }

            // Check whether movement is possible or blocked by a collider
            val nextPos = IntVec2(0, 0)

            for (r in World.getAllWith("ColliderComponent", transform.layer))
            {
                val staticCollider = r.get<ColliderComponent>()

                if(staticCollider != null)
                {

                    nextPos.x = transform.nextX + Constant.TILE_SIZE/2
                    nextPos.y = transform.nextY + Constant.TILE_SIZE/2

                    if (staticCollider.asRectangle.contains(nextPos)) { return }
                }
            }

            for (r in ecs.gameArea.dynamicColliders.get(transform.layer))
            {
                nextPos.x = transform.nextX + Constant.TILE_SIZE/2
                nextPos.y = transform.nextY + Constant.TILE_SIZE/2

                if (collider.asRectangle != r.asRectangle && r.asRectangle.contains(nextPos)) { return }
            }

            // TODO update for new system
            // Update Collider Position
            transform.lastPixelStep = TimeUtils.millis()    // remember time of this iteration

            input.moving = true        // entity is moving right now
            input.startMoving = false  // because entity now started moving

            World.hero.get<CharacterSpriteComponent>()?.sprite?.changeState(input.skyDir)
        }


        // If entity is already moving, and last incremental step has completed (long enough ago)
        if (input.moving && TimeUtils.timeSinceMillis(transform.lastPixelStep) > Constant.ONE_STEPDURATION_MS)
        {
            val hero = World.hero

            when (input.skyDir)
            {
                SkyDirection.N  -> transform.y += 1
                SkyDirection.W  -> transform.x -= 1
                SkyDirection.E  -> transform.x += 1
                else            -> transform.y -= 1
            }
            transform.lastPixelStep = TimeUtils.millis()


            //println(stepsSinceLastFrameUpdate)
            if(stepsSinceLastFrameUpdate >= newFrameEveryXPixels)
            {
                stepsSinceLastFrameUpdate = 0
                hero.get<CharacterSpriteComponent>()?.sprite?.toNextFrame()
            }
            stepsSinceLastFrameUpdate++

            // Check if movement is complete
            val movementComplete = when (input.skyDir)
            {
                SkyDirection.N, SkyDirection.S  -> transform.y == transform.nextY
                SkyDirection.W, SkyDirection.E  -> transform.x == transform.nextX
                else                            -> false
            }

            if (movementComplete)
            {
                checkHeal()
                input.moving = false

                transform.updateGridSlot()

                logDebug(TAG) { "Position on Grid: ${transform.onGrid}" }
            }

            // Movement completed
            if (!input.moving)
            {
                // Continue movement when button is pressed
                if (input.touchDown)
                {
                    input.startMoving = true
                    input.skyDir = input.nextInput
                }
                else
                {
                    World.hero.get<CharacterSpriteComponent>()?.sprite?.resetAnimation()
                }

                // Check whether hero can get attacked by monsters
                return // TODO
                for (ma in ecs.gameArea.monsterAreas.get(transform.layer))
                {
                    if(transform.asRectangle.offset(Constant.TILE_SIZE/2) in ma && MathUtils.randomBoolean(ma.teamSizeProbabilities.get(0)))
                    {
                        logDebug(TAG) { "Monster appeared!" }

                        //............................................................. START BATTLE
                        // TODO change min and max levels
                        input.inBattle = true
                        val guardianProbabilities = ArrayMap<Int, Float>()
                        for (i in 0 until ma.monsters.size)
                        {
                            guardianProbabilities.put(ma.monsters.get(i), ma.monsterProbabilities.get(i))
                        }

                        val oppTeam = BattleFactory.createOpponentTeam(guardianProbabilities, ma.teamSizeProbabilities, 1, 1)

                        ecs.hud.battleScreen.initialize(

                                World.hero.get<TeamComponent>()!!.team,
                                oppTeam,
                                World.hero.get<GuardoSphereComponent>()!!.guardoSphere
                        )

                        Services.ScreenManager().pushScreen(ecs.hud.battleScreen)
                        //............................................................. START BATTLE

                        // Stop when in a battle
                        if (input.touchDown) input.startMoving = false
                    }
                }
            }
        }
    }
}
