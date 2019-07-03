package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component

import de.limbusdev.guardianmonsters.fwmengine.world.ui.AnimatedPersonSprite
import de.limbusdev.guardianmonsters.fwmengine.world.ui.EntitySprite

/**
 * Simple [Component] to hold a sprite. This one is for simple things which do not need the
 * possibility to be turned into different directions or being animated.
 *
 * @author Georg Eckert 2015-11-21
 */
class SpriteComponent(male: Boolean, index: Int) : Component
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    var sprite: AnimatedPersonSprite


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        sprite = AnimatedPersonSprite(male, index)
        sprite.setSize(1f, 1f)
    }
}
