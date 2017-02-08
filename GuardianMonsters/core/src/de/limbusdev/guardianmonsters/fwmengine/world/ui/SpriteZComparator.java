package de.limbusdev.guardianmonsters.fwmengine.world.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.Comparator;

/**
 * Created by Georg Eckert on 31.01.17.
 */

public class SpriteZComparator implements Comparator<Sprite> {

    public static final int SMALLER = -1;
    public static final int BIGGER = 1;
    public static final int EQUAL = 0;

    @Override
    public int compare(Sprite sprite1, Sprite sprite2) {

        if(sprite1.getY() > sprite2.getY()) {
            return SMALLER;
        } else if (sprite1.getY() < sprite2.getY()) {
            return BIGGER;
        }else {
            return EQUAL;
        }
    }
}
