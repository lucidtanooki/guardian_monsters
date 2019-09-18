package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.ArrayMap
import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.CoreSL
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.guardians.battle.BattleFactory
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.utils.geometry.IntVec2
import de.limbusdev.utils.logDebug

/**
 * Identifies an Entity as the hero
 *
 * @author Georg Eckert 2016-01-16
 */
class HeroComponent
(
        movementComponent: TileWiseMovementComponent
)
    : LimbusBehaviour(), Component
{
    companion object { const val TAG = "TileWiseMovementComponent" }

    override val defaultJson: String = ""

    init
    {
        movementComponent.onGridSlotChanged.add { slot -> checkWarp(slot) }
        movementComponent.onGridSlotChanged.add { slot -> checkForHealingArea(slot) }
        movementComponent.onGridSlotChanged.add { slot -> checkForRandomBattleArea(slot) }
    }

    /**
     * Check, if there is a healing area at the current grid position
     */
    private fun checkForHealingArea(slot: IntVec2)
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
    private fun checkForRandomBattleArea(slot: IntVec2)
    {
        val transform = gameObject?.transform ?: return
        val inputComponent = gameObject?.get<InputComponent>() ?: return

        for(battleArea in CoreSL.world.getAllWith("RandomBattleAreaComponent", transform.layer))
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
                logDebug(TAG) { "Monster appeared!" }

                //............................................................. START BATTLE
                // TODO change min and max levels
                inputComponent.inBattle = true
                val guardianProbabilities = ArrayMap<Int, Float>()
                for (i in 0 until monsterArea.monsters.size)
                {
                    guardianProbabilities.put(monsterArea.monsters.get(i), monsterArea.monsterProbabilities.get(i))
                }

                val oppTeam = BattleFactory.createOpponentTeam(guardianProbabilities, monsterArea.teamSizeProbabilities, 1, 1)

                CoreSL.ecs.hud.battleScreen.initialize(

                        CoreSL.world.hero.get<TeamComponent>()!!.team,
                        oppTeam,
                        CoreSL.world.hero.get<GuardoSphereComponent>()!!.guardoSphere
                )

                Services.ScreenManager().pushScreen(CoreSL.ecs.hud.battleScreen)
                //............................................................. START BATTLE

                // Stop when in a battle
                if (inputComponent.touchDown) { inputComponent.startMoving = false }
            }
        }
    }

    /** Check whether hero enters warp area */
    private fun checkWarp(slot: IntVec2)
    {
        val transform = gameObject?.transform ?: return

        val warpStartFields = CoreSL.world.getAllWith("WarpStartComponent", transform.layer)

        for(warpStart in warpStartFields)
        {
            val warpComponent = warpStart.get<WarpStartComponent>()
            val warpCollider = warpStart.get<ColliderComponent>()
            if(warpCollider != null && warpCollider.isTrigger && warpComponent != null)
            {
                if(warpCollider.asRectangle.contains(transform.x+Constant.TILE_SIZE/2, transform.y+Constant.TILE_SIZE/2))
                {
                    logDebug(TAG) { "Changing to Map ${warpComponent.targetMapID}" }
                    CoreSL.world.stop()
                    CoreSL.ecs.changeGameArea(warpComponent.targetMapID, warpComponent.warpTargetID)
                }
            }
        }
    }
}