package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.TimeUtils
import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.World
import de.limbusdev.guardianmonsters.guardians.battle.BattleFactory
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.utils.geometry.IntVec2
import de.limbusdev.utils.logDebug

class TileWiseMovementComponent() : LimbusBehaviour()
{
    override val defaultJson: String get() = ""

    private val newFrameEveryXPixels = 6
    private var stepsSinceLastFrameUpdate = 0

    override fun update(deltaTime: Float)
    {
        super.update(deltaTime)

        val transform = gameObject?.transform ?: return
        val inputComponent = gameObject?.get<InputComponent>() ?: return

        // For every new tile-step, initialize the new movement
        initializeMovement(transform, inputComponent)

        // after initializing, perform all the pixel-steps
        applyMovement(transform, inputComponent)
    }

    /**
     * Check, if there is a healing area at the current grid position
     */
    private fun checkForHealingArea()
    {
        /*val hero = World.hero
        val heroArea = createRectangle(IntRect(hero.transform.x, hero.transform.y, hero.transform.width, hero.transform.height))

        // Check whether hero enters warp area
        if(healFields.get(hero.transform.layer) == null) { return }
        for (h in healFields.get(hero.transform.layer))
        {
            if (heroArea.contains(h.x + h.width / 2, h.y + h.height / 2))
            {
                // Heal Team
                logDebug(MovementSystem.TAG) { "Entered Healing Area" }
                val team = hero.get<TeamComponent>()!!.team
                val teamHurt = false
                team.values().forEach { guardian -> guardian.stats.healCompletely() }
            }
        }*/
    }

    /**
     * Check, if the current area can cause random battle encounters
     */
    private fun checkForRandomBattles(transform: Transform, inputComponent: InputComponent)
    {
        for(battleArea in World.getAllWith("RandomBattleAreaComponent", transform.layer))
        {
            val battleAreaRectangle = battleArea.get<ColliderComponent>()?.asRectangle
            val monsterArea = battleArea.get<MonsterAreaComponent>()

            if
            (
                    battleAreaRectangle != null &&
                    monsterArea != null &&
                    battleAreaRectangle.contains(transform.asRectangle.offset(Constant.TILE_SIZE/2))
                    && MathUtils.randomBoolean(monsterArea.teamSizeProbabilities.get(0))
            ) {
                logDebug("TileWiseMovementComponent") { "Monster appeared!" }

                //............................................................. START BATTLE
                // TODO change min and max levels
                inputComponent.inBattle = true
                val guardianProbabilities = ArrayMap<Int, Float>()
                for (i in 0 until monsterArea.monsters.size)
                {
                    guardianProbabilities.put(monsterArea.monsters.get(i), monsterArea.monsterProbabilities.get(i))
                }

                val oppTeam = BattleFactory.createOpponentTeam(guardianProbabilities, monsterArea.teamSizeProbabilities, 1, 1)

                World.ecs.hud.battleScreen.initialize(

                        World.hero.get<TeamComponent>()!!.team,
                        oppTeam,
                        World.hero.get<GuardoSphereComponent>()!!.guardoSphere
                )

                Services.ScreenManager().pushScreen(World.ecs.hud.battleScreen)
                //............................................................. START BATTLE

                // Stop when in a battle
                if (inputComponent.touchDown) { inputComponent.startMoving = false }
            }
        }
    }

    private fun applyMovement(transform: Transform, inputComponent: InputComponent) : Boolean {
        // Early Exits
        if (!inputComponent.moving)
        {
            World.hero.get<CharacterSpriteComponent>()?.sprite?.resetAnimation()
            return false
        }
        if (TimeUtils.timeSinceMillis(transform.lastPixelStep) < Constant.ONE_STEPDURATION_MS) { return false }

        // If entity is already moving, and last incremental step has completed (long enough ago)
        val spriteComponent = gameObject?.get<CharacterSpriteComponent>() ?: return false

        when (inputComponent.skyDir)
        {
            SkyDirection.N -> transform.y += 1
            SkyDirection.W -> transform.x -= 1
            SkyDirection.E -> transform.x += 1
            else -> transform.y -= 1
        }
        transform.lastPixelStep = TimeUtils.millis()

        if (stepsSinceLastFrameUpdate >= newFrameEveryXPixels)
        {
            stepsSinceLastFrameUpdate = 0
            spriteComponent.sprite.toNextFrame()
        }
        stepsSinceLastFrameUpdate++

        // Check if movement is complete
        val movementComplete = when (inputComponent.skyDir)
        {
            SkyDirection.N, SkyDirection.S -> transform.y == transform.nextY
            SkyDirection.W, SkyDirection.E -> transform.x == transform.nextX
            else -> false
        }

        if (movementComplete)
        {
            checkForHealingArea()
            checkForRandomBattles(transform, inputComponent)
            inputComponent.moving = false

            logDebug("TileWiseMovementComponent") { "Position on Grid: ${transform.onGrid}" }
        }

        // Movement completed
        if (!inputComponent.moving)
        {
            // Continue movement when button is pressed
            if (inputComponent.touchDown)
            {
                inputComponent.startMoving = true
                inputComponent.skyDir = inputComponent.nextInput
            }
        }

        return true
    }


    /**
     * Turns character according to the touched direction and initializes movement, if touch is
     * longer than 100 ms and nothing blocks the way.
     */
    private fun initializeMovement(transform: Transform, inputComponent: InputComponent) : Boolean
    {
        // Initialize Movement
        if(!inputComponent.startMoving || !inputComponent.touchDown) { return false }

        // Turn Character to the chosen direction
        World.hero.get<CharacterSpriteComponent>()?.sprite?.changeState(inputComponent.skyDir)

        if(TimeUtils.timeSinceMillis(inputComponent.firstTip) > 100)
        {
            // Start movement in that direction
            val nextPosition = calculateNextPosition()
            val isBlocked = isNextPositionBlocked(nextPosition)
            if(isBlocked) { return false }

            transform.lastPixelStep = TimeUtils.millis()    // remember time of this iteration

            inputComponent.moving = true        // entity is moving right now
            inputComponent.startMoving = false  // because entity now started moving

            return true
        }

        return false
    }

    /** Returns whether the given slot is blocked by a collider. */
    private fun isNextPositionBlocked(nextPosition: IntVec2) : Boolean
    {
        val colliderComponent = gameObject?.get<ColliderComponent>() ?: return true
        val transform = gameObject?.transform ?: return true

        // Check whether movement is possible or blocked by a collider
        val nextPos = IntVec2(0, 0)

        for (r in World.getAllWith("ColliderComponent", transform.layer))
        {
            val staticCollider = r.get<ColliderComponent>()

            if(staticCollider != null)
            {
                nextPos.x = nextPosition.x + Constant.TILE_SIZE/2
                nextPos.y = nextPosition.y + Constant.TILE_SIZE/2

                if (staticCollider.asRectangle.contains(nextPos)) { return true }
            }
        }

        return false
    }

    private fun calculateNextPosition() : IntVec2
    {
        val inputComponent = gameObject?.get<InputComponent>() ?: return IntVec2()
        val transform = gameObject?.transform ?: return IntVec2()

        // Define potential next position according to the input direction
        when (inputComponent.skyDir)
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

        return IntVec2(transform.nextX, transform.nextY)
    }

    /** Check whether hero enters warp area */
    private fun checkWarp()
    {
        // TODO
        /*val pos = Transform(LimbusGameObject()) // TODO
        val heroArea = createRectangle(IntRect(pos.x, pos.y, pos.width, pos.height))

        // Check whether hero enters warp area
        if(warpPoints.get(pos.layer) == null) { return }
        for (w in warpPoints.get(pos.layer))
        {
            if (heroArea.contains(w.xf, w.yf))
            {
                logDebug(MovementSystem.TAG) { "Changing to Map ${w.targetID}" }
                ecs.changeGameArea(w.targetID, w.targetWarpPointID)
            }
        }*/
    }
}