package org.limbusdev.monsterworld.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;

import org.limbusdev.monsterworld.graphics.EntitySprite;

/**
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
