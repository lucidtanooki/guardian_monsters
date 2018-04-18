package de.limbusdev.guardianmonsters.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

/**
 * Created by georg on 05.03.17.
 */

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
