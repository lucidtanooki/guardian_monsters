package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.fwmengine.world.ui.AnimatedPersonSprite
import de.limbusdev.guardianmonsters.fwmengine.world.ui.EntitySprite


/**
 * Special [Component] which holds an [EntitySprite] for a visible actor. This component also holds
 * [Animation]s which are used by the [CharacterSpriteSystem] to animate and update an entity's
 * sprite.
 *
 * @author Georg Eckert 2015-11-22
 */
class CharacterSpriteComponent(var sprite: AnimatedPersonSprite) : Component