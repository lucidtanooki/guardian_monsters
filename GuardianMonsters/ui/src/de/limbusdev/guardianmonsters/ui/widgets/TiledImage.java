

/*
 * *************************************************************************************************
 * Copyright (c) 2018. limbusdev (Georg Eckert) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Georg Eckert <georg.eckert@limbusdev.de>
 * *************************************************************************************************
 */

package de.limbusdev.guardianmonsters.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

public class TiledImage extends Group {

    public TiledImage(Drawable tile, int cols, int rows) {
        super();

        for(int x=0; x<cols; x++) {
            for(int y=0; y<rows; y++) {
                Image bgTile = new Image(tile);
                bgTile.setPosition(x*16, y*16, Align.bottomLeft);
                addActor(bgTile);
            }
        }
    }
}
