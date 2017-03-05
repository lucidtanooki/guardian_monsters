package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ArrayMap;


/**
 * Created by georg on 14.11.16.
 */

public class UIManager implements UI {

    private ArrayMap<Integer,ArrayMap<Color,BitmapFont>> fonts;
    private Skin defaultSkin, battleSkin, inventorySkin;
    private final static Color DGREEN = Color.valueOf("3e8948");
    private final static Color DRED = Color.valueOf("9e2835");

    public UIManager(String fontPath) {

        // Font
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
            Gdx.files.internal(fontPath));
        FreeTypeFontGenerator.FreeTypeFontParameter param
            = new FreeTypeFontGenerator.FreeTypeFontParameter();

        param.magFilter = Texture.TextureFilter.Nearest;
        param.minFilter = Texture.TextureFilter.Linear;

        fonts = new ArrayMap<>();
        int[] sizes = {12,16,32};
        Color[] colors = {Color.BLACK, Color.WHITE, DGREEN, DRED};

        for(int size : sizes) {
            ArrayMap<Color,BitmapFont> coloredFonts = new ArrayMap<>();
            param.size = size;
            for(Color c : colors) {
                param.color = c;
                BitmapFont f = gen.generateFont(param);
                coloredFonts.put(c,f);
            }
            fonts.put(size,coloredFonts);
        }

        gen.dispose();


        // Skins
        defaultSkin = new Skin();
        defaultSkin.addRegions(new TextureAtlas(Gdx.files.internal("scene2d/uiskin.atlas")));
        defaultSkin.addRegions(new TextureAtlas(Gdx.files.internal("scene2d/UI.pack")));
        defaultSkin.add("default-font", fonts.get(32).get(Color.BLACK));
        defaultSkin.add("white", fonts.get(32).get(Color.WHITE));
        defaultSkin.load(Gdx.files.internal("scene2d/uiskin.json"));

        battleSkin = new Skin();
        battleSkin.addRegions(new TextureAtlas(Gdx.files.internal("scene2d/battleUI.pack")));
        battleSkin.add("default-font", fonts.get(32).get(Color.BLACK));
        battleSkin.add("white", fonts.get(32).get(Color.WHITE));
        battleSkin.load(Gdx.files.internal("scene2d/battleuiskin.json"));

        inventorySkin = new Skin();
        inventorySkin.addRegions(new TextureAtlas(Gdx.files.internal("scene2d/inventoryUI.pack")));
        inventorySkin.add("default-font", fonts.get(16).get(Color.BLACK));
        inventorySkin.add("white", fonts.get(16).get(Color.WHITE));
        inventorySkin.add("font16", fonts.get(16).get(Color.BLACK));
        inventorySkin.add("font16w", fonts.get(16).get(Color.WHITE));
        inventorySkin.add("font16g", fonts.get(16).get(DGREEN));
        inventorySkin.add("font16r", fonts.get(16).get(DRED));
        inventorySkin.load(Gdx.files.internal("scene2d/inventoryUIskin.json"));

    }

    @Override
    public BitmapFont getFont(int color) {
        return fonts.get(32).get(Color.BLACK);
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

    @Override
    public void dispose() {
        this.defaultSkin.dispose();
        this.battleSkin.dispose();
        this.inventorySkin.dispose();
        for(ArrayMap<Color,BitmapFont> fm : fonts.values()) {
            for(BitmapFont bmf : fm.values()) {
                bmf.dispose();
            }
        }
    }


}
