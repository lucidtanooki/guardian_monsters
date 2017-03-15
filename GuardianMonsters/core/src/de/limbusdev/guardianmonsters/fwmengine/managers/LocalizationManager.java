package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

import de.limbusdev.guardianmonsters.data.BundleAssets;

/**
 * Created by georg on 14.11.16.
 */

public class LocalizationManager implements L18N {

    private ArrayMap<Integer,I18NBundle> l18n;
    private ArrayMap<Integer,I18NBundle> l18nMap;

    private BitmapFont font;

    private static final String FONT_PATH_RUSSIAN = "fonts/multi.fnt";

    public LocalizationManager() {
        l18n = new ArrayMap<>();
        l18nMap = new ArrayMap<>();

        System.out.println("Language: " + Locale.getDefault().getLanguage());

        l18n.put(BundleAssets.MONSTERS,     I18NBundle.createBundle(Gdx.files.internal(BundleAssets.MONSTERS_BUNDLE)));
        l18n.put(BundleAssets.BATTLE,       I18NBundle.createBundle(Gdx.files.internal(BundleAssets.BATTLE_BUNDLE)));
        l18n.put(BundleAssets.INVENTORY,    I18NBundle.createBundle(Gdx.files.internal(BundleAssets.INVENTORY_BUNDLE)));
        l18n.put(BundleAssets.ATTACKS,      I18NBundle.createBundle(Gdx.files.internal(BundleAssets.ATTACKS_BUNDLE)));
        l18n.put(BundleAssets.ELEMENTS,     I18NBundle.createBundle(Gdx.files.internal(BundleAssets.ELEMENTS_BUNDLE)));
        l18n.put(BundleAssets.GENERAL,      I18NBundle.createBundle(Gdx.files.internal(BundleAssets.GENERAL_BUNDLE)));

        int[] mapIDs ={1,2,25,251,252,253};
        for(int i : mapIDs) {
            l18nMap.put(i, I18NBundle.createBundle(Gdx.files.internal(BundleAssets.MAP_BUNDLE_PREFIX+Integer.toString(i))));
        }


        if(Locale.getDefault().getLanguage().equals("ru")) {
            font = new BitmapFont(Gdx.files.internal(FONT_PATH_RUSSIAN));
        } else {
            // Create Bitmap Font from TTF
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PixelOperator.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 16;
            parameter.magFilter = Texture.TextureFilter.Linear;
            parameter.minFilter = Texture.TextureFilter.Linear;
            parameter.borderColor = Color.WHITE;
            parameter.borderWidth = .2f;
            parameter.color = Color.WHITE;
            this.font = generator.generateFont(parameter); // font size 16 pixels
            generator.dispose(); // don't forget to dispose to avoid memory leaks!
        }
    }

    @Override
    public I18NBundle l18n(int type) {
        return l18n.get(type);
    }

    @Override
    public I18NBundle l18nMap(int mapID) {
        return l18nMap.get(mapID);
    }

    public BitmapFont getFont() {
        return font;
    }

    @Override
    public I18NBundle i18nGeneral() {
        return this.l18n.get(BundleAssets.GENERAL);
    }

    @Override
    public I18NBundle i18nAbilities() {
        return this.l18n.get(BundleAssets.ATTACKS);
    }

    @Override
    public I18NBundle i18nMonsters() {
        return this.l18n.get(BundleAssets.MONSTERS);
    }

    @Override
    public I18NBundle i18nElements() {
        return this.l18n.get(BundleAssets.ELEMENTS);
    }

    @Override
    public I18NBundle i18nInventory() {
        return this.l18n.get(BundleAssets.INVENTORY);
    }

    @Override
    public I18NBundle i18nBattle() {
        return this.l18n.get(BundleAssets.BATTLE);
    }

    @Override
    public void dispose() {
        this.font.dispose();
    }
}
