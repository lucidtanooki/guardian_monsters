package org.limbusdev.monsterworld.geometry;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import org.limbusdev.monsterworld.utils.GS;

/**
 * Created by georg on 23.11.15.
 */
public class WarpPoint {
    /* ............................................................................ ATTRIBUTES .. */
    public int x,y;
    public int targetID;
    public int targetWarpPointID;
    /* ........................................................................... CONSTRUCTOR .. */

    public WarpPoint(int targetWarpPointID, Rectangle warpField, int targetID) {
        this.targetWarpPointID = targetWarpPointID;
        this.x = MathUtils.round(warpField.x + GS.TILE_SIZE/2);
        this.y = MathUtils.round(warpField.y + GS.TILE_SIZE/2);
        this.targetID = targetID;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
