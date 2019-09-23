package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour

import de.limbusdev.guardianmonsters.fwmengine.world.ui.AnimatedPersonSprite
import de.limbusdev.guardianmonsters.fwmengine.world.ui.EntitySprite

/**
 * Simple [Component] to hold a sprite. This one is for simple things which do not need the
 * possibility to be turned into different directions or being animated.
 *
 * @author Georg Eckert 2015-11-21
 */
open class SpriteComponent(region: TextureRegion) : LimbusBehaviour(), Component
{
    override val defaultJson: String get() = ""

    // --------------------------------------------------------------------------------------------- PROPERTIES
    var sprite = Sprite(region)
}
