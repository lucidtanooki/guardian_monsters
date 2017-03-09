package de.limbusdev.guardianmonsters.fwmengine.world.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Extends {@link Sprite} adding a boolean for switching sprites visibility. This will cause
 * {@link ExtendedTiledMapRenderer} to not draw an
 * EntitySprite if its visibility is set to false.
 * Created by georg on 21.11.15.
 */
public class EntitySprite extends Sprite {
    /* ............................................................................ ATTRIBUTES .. */
    public boolean visible;
    /* ........................................................................... CONSTRUCTOR .. */
    public EntitySprite(TextureRegion textureRegion) {
        super(textureRegion);
        this.visible = true;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
