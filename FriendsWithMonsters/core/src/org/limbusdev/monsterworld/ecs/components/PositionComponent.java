package org.limbusdev.monsterworld.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Simple {@link Component} which holds the {@link com.badlogic.ashley.core.Entity}'s x and y
 * coordinates, it's width and height and the next potential position to move to. The time when
 * the entity last moved is stored as well.
 * Created by georg on 22.11.15.
 */
public class PositionComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public int x,y,width,height, nextX, nextY;
    public long lastPixelStep; // ms
    /* ........................................................................... CONSTRUCTOR .. */
    public PositionComponent(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.nextX = 0;
        this.nextY = 0;
        this.lastPixelStep = 0;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
