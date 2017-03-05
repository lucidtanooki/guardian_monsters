package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;

import de.limbusdev.guardianmonsters.data.SkinAssets;


/**
 * @author Georg Eckert 2017
 */

public class UIManager extends AssetManager implements UI  {

    private ArrayMap<Integer,ArrayMap<Color,BitmapFont>> fonts;
    private final static Color DGREEN = Color.valueOf("3e8948");
    private final static Color DRED = Color.valueOf("9e2835");

    public UIManager(String fontPath) {

        super();

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

        ObjectMap<String, Object> skinResources = new ObjectMap<>();
        skinResources.put("default-font", fonts.get(32).get(Color.BLACK));
        skinResources.put("white", fonts.get(32).get(Color.WHITE));
        skinResources.put("font16", fonts.get(16).get(Color.BLACK));
        skinResources.put("font16w", fonts.get(16).get(Color.WHITE));
        skinResources.put("font16g", fonts.get(16).get(DGREEN));
        skinResources.put("font16r", fonts.get(16).get(DRED));

        String[] skinPaths = {
            SkinAssets.defaultSkin,
            SkinAssets.battleSkin,
            SkinAssets.inventorySkin
        };

        for(String path : skinPaths) {
            load(path + ".json", Skin.class, new SkinLoader.SkinParameter(path + ".atlas", skinResources));
        }
        finishLoading();
    }

    @Override
    public BitmapFont getFont(int color) {
        return fonts.get(32).get(Color.BLACK);
    }

    @Override
    public Skin getDefaultSkin() {
        return get(SkinAssets.defaultSkin + ".json", Skin.class);
    }

    @Override
    public Skin getBattleSkin() {
        return get(SkinAssets.battleSkin + ".json", Skin.class);
    }

    @Override
    public Skin getInventorySkin() {
        return get(SkinAssets.inventorySkin + ".json", Skin.class);
    }

    @Override
    public void dispose() {
        for(ArrayMap<Color,BitmapFont> fm : fonts.values()) {
            for(BitmapFont bmf : fm.values()) {
                bmf.dispose();
            }
        }
    }


}
