package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


/**
 * Created by georg on 14.11.16.
 */

public class UIManager implements UI {

    public static final int FONT_COLOR_WHITE=0;
    public static final int FONT_COLOR_BLACK=1;

    private BitmapFont font32white, font32;
    private Skin defaultSkin, battleSkin, inventorySkin;

    public UIManager(
        String fontPath
    ) {

        // Font
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
            Gdx.files.internal(fontPath));
        FreeTypeFontGenerator.FreeTypeFontParameter param
            = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.color = Color.BLACK;
        param.size = 32;
        param.magFilter = Texture.TextureFilter.Nearest;
        param.minFilter = Texture.TextureFilter.Linear;
        font32 = gen.generateFont(param);
        param.color = Color.WHITE;
        param.size = 32;
        param.magFilter = Texture.TextureFilter.Nearest;
        param.minFilter = Texture.TextureFilter.Linear;
        font32white = gen.generateFont(param);
        gen.dispose();


        // Skins
        defaultSkin = new Skin();
        defaultSkin.addRegions(new TextureAtlas(Gdx.files.internal("scene2d/uiskin.atlas")));
        defaultSkin.addRegions(new TextureAtlas(Gdx.files.internal("scene2d/UI.pack")));
        defaultSkin.add("default-font", font32);
        defaultSkin.add("white", font32white);
        defaultSkin.load(Gdx.files.internal("scene2d/uiskin.json"));

        battleSkin = new Skin();
        battleSkin.addRegions(new TextureAtlas(Gdx.files.internal("scene2d/battleUI.pack")));
        battleSkin.add("default-font", font32);
        battleSkin.add("white", font32white);
        battleSkin.load(Gdx.files.internal("scene2d/battleuiskin.json"));

        inventorySkin = new Skin();
        inventorySkin.addRegions(new TextureAtlas(Gdx.files.internal("scene2d/inventoryUI.pack")));
        inventorySkin.add("default-font", font32);
        inventorySkin.add("white", font32white);
        inventorySkin.load(Gdx.files.internal("scene2d/inventoryUIskin.json"));

    }

    @Override
    public BitmapFont getFont(int color) {
        switch (color) {
            case FONT_COLOR_WHITE:
                return font32white;
            default:
                return font32;
        }
    }

    @Override
    public Skin getDefaultSkin() {
        return defaultSkin;
    }

    @Override
    public Skin getBattleSkin() {
        return battleSkin;
    }

    @Override
    public Skin getInventorySkin() {
        return inventorySkin;
    }


}
