package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour

/**
 * Identifies an Entity as the hero
 *
 * @author Georg Eckert 2016-01-16
 */
class HeroComponent(override val defaultJson: String = "") : LimbusBehaviour(), Component