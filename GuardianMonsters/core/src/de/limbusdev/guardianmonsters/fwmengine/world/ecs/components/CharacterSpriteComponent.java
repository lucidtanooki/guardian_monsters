package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.fwmengine.world.ui.AnimatedPersonSprite;
import de.limbusdev.guardianmonsters.fwmengine.world.ui.EntitySprite;


/**
 * Special {@link Component} which holds an {@link EntitySprite} for a visible actor. This component
 * also holds {@link Animation}s which are used by the {@link package CharacterSpriteSystem}
 * to animate and update an entity's sprite.
 *
 * Created by georg on 22.11.15.
 */
public class CharacterSpriteComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public AnimatedPersonSprite sprite;
    /* ........................................................................... CONSTRUCTOR .. */
    public CharacterSpriteComponent (AnimatedPersonSprite sprite) {
        this.sprite = sprite;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
