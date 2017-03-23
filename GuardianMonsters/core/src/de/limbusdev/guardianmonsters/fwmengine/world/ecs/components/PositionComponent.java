package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components;

import com.badlogic.ashley.core.Component;

import de.limbusdev.guardianmonsters.geometry.IntRect;
import de.limbusdev.guardianmonsters.geometry.IntVec2;
import de.limbusdev.guardianmonsters.Constant;


/**
 * Simple {@link Component} which holds the {@link com.badlogic.ashley.core.Entity}'s x and y
 * coordinates, it's width and height and the next potential position to move to. The time when
 * the entity last moved is stored as well.
 * Created by georg on 22.11.15.
 */
public class PositionComponent extends IntRect implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public int nextX, nextY;
    public long lastPixelStep; // ms
    public IntVec2 onGrid;
    public int layer;
    /* ........................................................................... CONSTRUCTOR .. */
    public PositionComponent(int x, int y, int width, int height, int layer) {
        super(x,y,width,height);
        this.nextX = 0;
        this.nextY = 0;
        this.lastPixelStep = 0;
        this.onGrid = new IntVec2(x/ Constant.TILE_SIZE,y/ Constant.TILE_SIZE);
        this.layer = layer;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
    public IntVec2 getCenter() {
        return new IntVec2(
                onGrid.x* Constant.TILE_SIZE+ Constant.TILE_SIZE/2,
                onGrid.y* Constant.TILE_SIZE+ Constant.TILE_SIZE/2);
    }

    public void updateGridPosition() {
        this.onGrid.x = x/ Constant.TILE_SIZE;
        this.onGrid.y = y/ Constant.TILE_SIZE;
    }
}
