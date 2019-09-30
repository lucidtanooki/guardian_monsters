package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.CoreSL
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
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
    : LimbusBehaviour()
{
    companion object
    {
        const val TAG = "TileWiseMovementComponent"
        const val className = "TileWiseMovementComponent"
    }


    // --------------------------------------------------------------------------------------------- Properties



    init
    {
        movementComponent.onGridSlotChanged.add { slot -> checkWarp(slot) }
        movementComponent.onGridSlotChanged.add { slot -> checkForHealingArea(slot) }
    }


    // --------------------------------------------------------------------------------------------- Methods



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

    /** Check whether hero enters warp area */
    private fun checkWarp(slot: IntVec2)
    {
        val warpStartFields = CoreSL.world.getAllWith(WarpStartComponent::class, transform.layer)

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