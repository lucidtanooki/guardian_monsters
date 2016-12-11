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
    public static final String monsterMiniSpriteSheetFile = "spritesheets/mini.pack";
    public static final String battleUISpriteSheetFile = "scene2d/battleUI.pack";
    public static final String UISpriteSheetFile = "scene2d/UI.pack";
    public static final String logosSpriteSheetFile = "spritesheets/logos.pack";
    public static final String animations = "spritesheets/animations.pack";
    public static final String battleAnimations = "spritesheets/battleAnimations.pack";
    public static final String battleMonsterPreviews = "scene2d/preview.pack";
    public static final String battleBackgrounds = "spritesheets/battleBacks.pack";

    public static Array<String> getAllTexturePackPaths() {

        Array<String> paths = new Array<String>();

        paths.add(heroSpritesheetFile);
        paths.add(monsterSpriteSheetFile);
        paths.add(battleUISpriteSheetFile);
        paths.add(battleAnimations);
        paths.add(UISpriteSheetFile);
        paths.add(logosSpriteSheetFile);
        paths.add(animations);
        paths.add(battleMonsterPreviews);
        paths.add(battleBackgrounds);
        paths.add(monsterMiniSpriteSheetFile);

        return paths;
    }

    public static Array<String> getAllTexturePaths() {

        Array<String> paths = new Array<String>();

        paths.add(mainMenuBGImgFile);
        paths.add(mainMenuBGImgFile2);

        return paths;
    }
}
