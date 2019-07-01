package de.limbusdev.guardianmonsters.fwmengine.world.ui

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion

/**
 * Extends [Sprite] adding a boolean for switching sprites visibility. This will cause
 * [ExtendedTiledMapRenderer] to not draw an
 * EntitySprite if its visibility is set to false.
 *
 * @author Georg Eckert 2015-11-21
 */
class EntitySprite(textureRegion: TextureRegion) : Sprite(textureRegion)
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    var visible: Boolean = false


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        visible = true
    }
}
