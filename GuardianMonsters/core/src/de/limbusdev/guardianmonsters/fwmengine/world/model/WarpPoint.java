package de.limbusdev.guardianmonsters.fwmengine.world.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import de.limbusdev.utils.geometry.IntRect;
import de.limbusdev.guardianmonsters.Constant;


/**
 * Rectangle containing information about place change
 *
 * Created by Georg Eckert on 23.11.15.
 */
public class WarpPoint extends IntRect {
    /* ............................................................................ ATTRIBUTES .. */
    public int targetID;
    public int targetWarpPointID;
    /* ........................................................................... CONSTRUCTOR .. */

    /**
     * Creates a rectangle containing information about a warp target
     * @param targetWarpPointID
     * @param warpField
     * @param targetID
     */
    public WarpPoint(int targetWarpPointID, Rectangle warpField, int targetID) {
        super(warpField);
        this.targetWarpPointID = targetWarpPointID;
        this.x = MathUtils.round(warpField.x + Constant.TILE_SIZE/2);
        this.y = MathUtils.round(warpField.y + Constant.TILE_SIZE/2);
        this.targetID = targetID;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
