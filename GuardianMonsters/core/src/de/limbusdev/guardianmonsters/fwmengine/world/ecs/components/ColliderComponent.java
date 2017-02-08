package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components;

import com.badlogic.ashley.core.Component;

import de.limbusdev.guardianmonsters.geometry.IntRect;


/**
 * Simple {@link Component} to hold an {@link IntRect} to represent a moving {@link com
 * .badlogic.ashley.core.Entity}'s collider.
 * Created by georg on 30.11.15.
 */
public class ColliderComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public IntRect collider;
    /* ........................................................................... CONSTRUCTOR .. */
    public ColliderComponent(int x, int y, int width, int height) {
        this.collider = new IntRect(x,y,width,height);

    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
