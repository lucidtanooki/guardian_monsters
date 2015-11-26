package org.limbusdev.monsterworld.geometry;

/**
 * Created by georg on 25.11.15.
 */
public class IntRectangle {
    /* ............................................................................ ATTRIBUTES .. */
    public int x,y,width,height;
    /* ........................................................................... CONSTRUCTOR .. */

    public IntRectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    /* ............................................................................... METHODS .. */
    public boolean contains(IntVector2 point) {
        if(point.x > x && point.x < x+width && point.y > y && point.y < y+height) return true;
        else return false;
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
