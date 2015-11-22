package org.limbusdev.monsterworld.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import org.limbusdev.monsterworld.enums.TextureAtlasType;

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
        assets.load(this.heroSpritesheetFile, TextureAtlas.class);
        assets.finishLoading();
    }
    /* ............................................................................... METHODS .. */

    public void dispose() {
        this.assets.dispose();
    }
    
    /* ..................................................................... GETTERS & SETTERS .. */
    public TextureAtlas getTextureAtlasType(TextureAtlasType type) {
        TextureAtlas atlas;
        switch(type) {
            case HERO:
                atlas = assets.get(heroSpritesheetFile);break;
            default:
                atlas = null;
                System.err.println("TextureAtlasType " + type + " not found.");
                break;
        }
        return atlas;
    }
}
