package de.limbusdev.guardianmonsters.fwmengine.world.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.Comparator;

/**
 * Created by georg on 31.01.17.
 */

public class SpriteZComparator implements Comparator<Sprite> {
    @Override
    public int compare(Sprite o1, Sprite o2) {

        if(o1.getY() > o2.getY()) {
            return -1;
        } else if (o1.getY() < o2.getY()) {
            return 1;
        }else {
            return 0;
        }
    }
}
