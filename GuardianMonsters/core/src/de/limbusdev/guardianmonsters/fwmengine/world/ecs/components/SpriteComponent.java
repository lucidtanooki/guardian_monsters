package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components;

import com.badlogic.ashley.core.Component;

import de.limbusdev.guardianmonsters.fwmengine.world.ui.AnimatedPersonSprite;
import de.limbusdev.guardianmonsters.fwmengine.world.ui.EntitySprite;

/**
 * Simple {@link Component} to hold a sprite. This one is for simple things which do not need the
 * possibility to be turned into different directions or being animated.
 * Created by georg on 21.11.15.
 */
public class SpriteComponent implements Component{
    /* ............................................................................ ATTRIBUTES .. */
    public AnimatedPersonSprite sprite;
    /* ........................................................................... CONSTRUCTOR .. */
    public SpriteComponent (boolean male, int index) {
        this.sprite = new AnimatedPersonSprite(male, index);
        this.sprite.setSize(1, 1);
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
