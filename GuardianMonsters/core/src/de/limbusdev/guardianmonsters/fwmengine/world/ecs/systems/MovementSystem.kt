package de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.TimeUtils

import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.EntityComponentSystem
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.*
import de.limbusdev.guardianmonsters.fwmengine.world.model.MonsterArea
import de.limbusdev.guardianmonsters.fwmengine.world.model.WarpPoint
import de.limbusdev.guardianmonsters.guardians.battle.BattleFactory
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.Team
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

    private lateinit var hero: Entity


    // --------------------------------------------------------------------------------------------- METHODS
    override fun addedToEngine(engine: Engine)
    {
        hero = engine.getEntitiesFor(Family.all(HeroComponent::class.java).get()).first()
    }

    override fun update(deltaTime: Float)
    {
        // Update Hero
        checkWarp()
        updateHero()
    }

    /** Check whether hero enters warp area */
    private fun checkWarp()
    {
        val pos = hero.getComponent<PositionComponent>()!!
        val heroArea = createRectangle(pos)

        // Check whether hero enters warp area
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
        val pos = hero.getComponent<PositionComponent>()!!
        val heroArea = createRectangle(pos)

        // Check whether hero enters warp area
        for (h in healFields.get(pos.layer))
        {
            if (heroArea.contains(h.x + h.width / 2, h.y + h.height / 2))
            {
                // Heal Team
                logDebug(TAG) { "Entered Healing Area" }
                val team = hero.getComponent<TeamComponent>()!!.team
                val teamHurt = false
                team.values().forEach { guardian -> guardian.stats.healCompletely() }
            }
        }
    }

    fun updateHero()
    {
        // Only move hero, when player is not speaking to an entity
        if (!hero.getComponent<InputComponent>()!!.talking)
        {
            makeOneStep(

                    hero.getComponent<PositionComponent>()!!,
                    hero.getComponent<InputComponent>()!!,
                    hero.getComponent<ColliderComponent>()!!
            )
        }
    }

    /**
     * Moves the entity by 1 tile
     * @param position
     * @param input
     * @param collider
     */
    fun makeOneStep
    (
            position: PositionComponent,
            input: InputComponent,
            collider: ColliderComponent
    ) {
        // Initialize Hero Movement
        if (input.startMoving && TimeUtils.timeSinceMillis(input.firstTip) > 100 && input.touchDown)
        {
            // Define potential next position according to the input direction
            when (input.skyDir)
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
                else ->
                {
                    position.nextX = position.x
                    position.nextY = position.y - Constant.TILE_SIZE
                }
            }

            // Check whether movement is possible or blocked by a collider
            val nextPos = IntVec2(0, 0)

            for (r in ecs.gameArea.colliders.get(position.layer))
            {
                nextPos.x = position.nextX + Constant.TILE_SIZE / 2
                nextPos.y = position.nextY + Constant.TILE_SIZE / 2

                if (r.contains(nextPos)) { return }
            }

            for (r in ecs.gameArea.dynamicColliders.get(position.layer))
            {
                nextPos.x = position.nextX + Constant.TILE_SIZE / 2
                nextPos.y = position.nextY + Constant.TILE_SIZE / 2

                if (collider != r && r.contains(nextPos)) { return }
            }

            // Update Collider Position
            collider.collider.x = position.nextX
            collider.collider.y = position.nextY
            position.lastPixelStep = TimeUtils.millis()    // remember time of this iteration

            input.moving = true        // entity is moving right now
            input.startMoving = false  // because entity now started moving
        }


        // If entity is already moving, and last incremental step has completed (long enough ago)
        if (input.moving && TimeUtils.timeSinceMillis(position.lastPixelStep) > Constant.ONE_STEPDURATION_MS)
        {
            when (input.skyDir)
            {
                SkyDirection.N  -> position.y = position.y + 1
                SkyDirection.W  -> position.x = position.x - 1
                SkyDirection.E  -> position.x = position.x + 1
                else            -> position.y = position.y - 1
            }
            position.lastPixelStep = TimeUtils.millis()

            // Check if movement is complete
            val movementComplete = when (input.skyDir)
            {
                SkyDirection.N, SkyDirection.S  -> position.y == position.nextY
                SkyDirection.W, SkyDirection.E  -> position.x == position.nextX
                else                            -> false
            }

            if (movementComplete)
            {
                checkHeal()
                input.moving = false

                // Update Grid Position of Hero
                position.onGrid += when (input.skyDir)
                {
                    SkyDirection.N  -> IntVec2(0,1)
                    SkyDirection.S  -> IntVec2(0,-1)
                    SkyDirection.E  -> IntVec2(1,0)
                    SkyDirection.W  -> IntVec2(-1,0)
                    else            -> IntVec2()
                }

                logDebug(TAG) { "Position on Grid: ${position.onGrid}" }
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

                // Check whether hero can get attacked by monsters
                for (ma in ecs.gameArea.monsterAreas.get(position.layer))
                {
                    if(position.offset(Constant.TILE_SIZE/2) in ma && MathUtils.randomBoolean(ma.teamSizeProbabilities.get(0)))
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

                                hero.getComponent<TeamComponent>()!!.team,
                                oppTeam,
                                hero.getComponent<GuardoSphereComponent>()!!.guardoSphere
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
