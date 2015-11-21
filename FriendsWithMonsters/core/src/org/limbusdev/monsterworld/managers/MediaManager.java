package org.limbusdev.monsterworld.managers;

import com.badlogic.gdx.assets.AssetManager;

/**
 * Created by georg on 21.11.15.
 */
public class MediaManager {
    /* ............................................................................ ATTRIBUTES .. */
    private AssetManager assets;

    // file names
    private String mainMenuBGImgFile = "";
    private String heroSpritesheetFile = "spritesheets/hero.pack";
    
    /* ........................................................................... CONSTRUCTOR .. */
    public MediaManager() {
        this.assets = new AssetManager();
    }
    /* ............................................................................... METHODS .. */

    public void dispose() {
        this.assets.dispose();
    }
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
