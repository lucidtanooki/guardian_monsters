package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour

import de.limbusdev.guardianmonsters.guardians.monsters.Team

/**
 * TeamComponent
 *
 * @author Georg Eckert 2016
 */
class TeamComponent : LimbusBehaviour()
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    var team = Team(7, 1, 1)
}
