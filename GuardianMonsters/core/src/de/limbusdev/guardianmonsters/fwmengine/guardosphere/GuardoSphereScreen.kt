package de.limbusdev.guardianmonsters.fwmengine.guardosphere

import de.limbusdev.guardianmonsters.guardians.monsters.GuardoSphere
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.AScreen

/**
 * GuardoSphereScreen
 *
 * The GuardoSphere is the place, where Guardian Monsters reside, when they are not in the material
 * world or bound to a humans chakra. To bind a monster to a chakra, a human can call it from the
 * GuardoSphere. Every human has access to a small area in the GuardoSphere - this is, where all
 * Guardian Monsters, who decided to follow him, reside.
 *
 * In game, the GuardoSphere is used as some sort of storage for Guardian Monsters, which the player
 * draws monsters from, to build a team.
 *
 * @author Georg Eckert 2017
 */

class GuardoSphereScreen(
        team: Team,
        guardoSphere: GuardoSphere)
    : AScreen(
        GuardoSphereHUD(Services.getUI().inventorySkin, team, guardoSphere))
