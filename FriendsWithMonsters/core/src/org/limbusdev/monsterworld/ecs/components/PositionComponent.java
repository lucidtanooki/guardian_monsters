package org.limbusdev.monsterworld.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
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
