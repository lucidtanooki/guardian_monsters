package org.limbusdev.monsterworld.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;

import org.limbusdev.monsterworld.geometry.IntRectangle;

/**
 * Created by georg on 30.11.15.
 */
public class ColliderComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public IntRectangle collider;
    /* ........................................................................... CONSTRUCTOR .. */
    public ColliderComponent(int x, int y, int width, int height) {
        this.collider = new IntRectangle(x,y,width,height);

    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
