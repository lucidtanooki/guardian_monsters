package org.limbusdev.monsterworld.ecs.components;

import com.badlogic.ashley.core.Component;

import org.limbusdev.monsterworld.geometry.IntVector2;
import org.limbusdev.monsterworld.utils.GlobalSettings;

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
    public IntVector2 onGrid;
    /* ........................................................................... CONSTRUCTOR .. */
    public PositionComponent(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.nextX = 0;
        this.nextY = 0;
        this.lastPixelStep = 0;
        this.onGrid = new IntVector2(x/ GlobalSettings.TILE_SIZE,y/GlobalSettings.TILE_SIZE);
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
    public IntVector2 getCenter() {
        return new IntVector2(
                onGrid.x*GlobalSettings.TILE_SIZE+GlobalSettings.TILE_SIZE/2,
                onGrid.y*GlobalSettings.TILE_SIZE+GlobalSettings.TILE_SIZE/2);
    }

    public void updateGridPosition() {
        this.onGrid.x = x/GlobalSettings.TILE_SIZE;
        this.onGrid.y = y/GlobalSettings.TILE_SIZE;
    }
}
