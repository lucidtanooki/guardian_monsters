package de.limbusdev.guardianmonsters.geometry;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

/**
 * Rectangle with integers for exact positioning
 *
 * Created by Georg Eckert on 25.11.15.
 */
public class IntRect extends IntVec2
{
    //................................................................................... ATTRIBUTES
    public int width,height;
    public int ID;
    public static int IDcount=0;
    //.................................................................................. CONSTRUCTOR

    public IntRect(int x, int y, int width, int height) {
        super(x,y);
        this.ID = IDcount;
        IntRect.IDcount++;
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
        super(MathUtils.round(r.x), MathUtils.round(r.y));
        this.ID = IDcount;
        IntRect.IDcount++;
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
