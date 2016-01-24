package org.limbusdev.monsterworld.ecs.components;

import com.badlogic.ashley.core.Component;
import org.limbusdev.monsterworld.graphics.EntitySprite;

/**
 * Simple {@link Component} to hold a sprite. This one is for simple things which do not need the
 * possibility to be turned into different directions or being animated.
 * Created by georg on 21.11.15.
 */
public class SpriteComponent implements Component{
    /* ............................................................................ ATTRIBUTES .. */
    public EntitySprite sprite;
    /* ........................................................................... CONSTRUCTOR .. */
    public SpriteComponent () {
        this.sprite = new EntitySprite(null);
        this.sprite.setSize(1, 1);
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
