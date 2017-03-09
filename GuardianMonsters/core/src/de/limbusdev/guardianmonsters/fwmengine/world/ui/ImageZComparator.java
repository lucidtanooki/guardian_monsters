package de.limbusdev.guardianmonsters.fwmengine.world.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.Comparator;

/**
 * ImageZComparator
 *
 * @author Georg Eckert 2017
 */

public class ImageZComparator implements Comparator<Image> {
    public static final int SMALLER = -1;
    public static final int BIGGER = 1;
    public static final int EQUAL = 0;

    @Override
    public int compare(Image img1, Image img2) {

        if(img1.getY() > img2.getY()) {
            return SMALLER;
        } else if (img1.getY() < img2.getY()) {
            return BIGGER;
        }else {
            return EQUAL;
        }
    }
}
