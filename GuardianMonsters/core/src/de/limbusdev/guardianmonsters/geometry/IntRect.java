package de.limbusdev.guardianmonsters.geometry;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

/**
 * Rectangle with integers for exact positioning
 *
 * Created by Georg Eckert on 25.11.15.
 */
public class IntRect {
    //................................................................................... ATTRIBUTES
    public int x,y,width,height;
    public int ID;
    public static int IDcount=0;
    //.................................................................................. CONSTRUCTOR

    public IntRect(int x, int y, int width, int height) {
        this.ID = IDcount;
        IntRect.IDcount++;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Rounds the given values
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public IntRect(float x, float y, float width, float height) {
        this(MathUtils.round(x), MathUtils.round(y), MathUtils.round(width), MathUtils.round(height));
    }

    public IntRect(Rectangle r) {
        this.ID = IDcount;
        IntRect.IDcount++;
        this.x = MathUtils.round(r.x);
        this.y = MathUtils.round(r.y);
        this.width = MathUtils.round(r.width);
        this.height = MathUtils.round(r.height);
    }
    /* ............................................................................... METHODS .. */
    public boolean contains(IntVec2 point) {
        if(point.x > x && point.x < x+width && point.y > y && point.y < y+height) return true;
        else return false;
    }

    public boolean equals(IntRect r) {
        if(r.ID == this.ID) return true;
        else return false;
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
