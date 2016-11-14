package de.limbusdev.guardianmonsters.data;

import com.badlogic.gdx.utils.Array;

/**
 * Created by georg on 14.11.16.
 */

public class TextureAssets {

    public static final String mainMenuBGImgFile = "spritesheets/GM_logo.png";
    public static final String mainMenuBGImgFile2 = "spritesheets/main_logo_bg.png";
    public static final String heroSpritesheetFile = "spritesheets/hero.pack";
    public static final String monsterSpriteSheetFile = "spritesheets/monsters.pack";
    public static final String battleUISpriteSheetFile = "spritesheets/battleUI.pack";
    public static final String UISpriteSheetFile = "spritesheets/UI.pack";
    public static final String logosSpriteSheetFile = "spritesheets/logos.pack";
    public static final String animations = "spritesheets/animations.pack";
    public static final String battleAnimations = "spritesheets/battleAnimations.pack";

    public static Array<String> getAllTexturePackPaths() {

        Array<String> paths = new Array<String>();

        paths.add(heroSpritesheetFile);
        paths.add(monsterSpriteSheetFile);
        paths.add(battleUISpriteSheetFile);
        paths.add(battleAnimations);
        paths.add(UISpriteSheetFile);
        paths.add(logosSpriteSheetFile);
        paths.add(animations);

        return paths;
    }

    public static Array<String> getAllTexturePaths() {

        Array<String> paths = new Array<String>();

        paths.add(mainMenuBGImgFile);
        paths.add(mainMenuBGImgFile2);

        return paths;
    }
}
