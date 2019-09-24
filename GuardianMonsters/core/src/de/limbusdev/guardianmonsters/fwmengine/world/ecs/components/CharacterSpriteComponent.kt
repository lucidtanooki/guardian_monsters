package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour

import de.limbusdev.guardianmonsters.fwmengine.world.ui.AnimatedPersonSprite
import de.limbusdev.guardianmonsters.fwmengine.world.ui.EntitySprite
import de.limbusdev.guardianmonsters.fwmengine.world.ui.ExtendedTiledMapRenderer


/**
 * Special [Component] which holds an [EntitySprite] for a visible actor. This component also holds
 * [Animation]s which are used by the [CharacterSpriteSystem] to animate and update an entity's
 * sprite.
 *
 * @author Georg Eckert 2015-11-22
 */
class CharacterSpriteComponent
(
        var sprite: AnimatedPersonSprite = AnimatedPersonSprite(male = true, index = 0)
)
    : LimbusBehaviour(), Component
{
    companion object
    {
        const val className ="CharacterSpriteComponent"
        const val defaultJson : String = "enabled: true, male: true, index: 0"
    }
}