package de.limbusdev.guardianmonsters.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

import de.limbusdev.guardianmonsters.assets.paths.AssetPath;
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.ISpeciesDescriptionService;


/**
 * @author Georg Eckert 2017
 */

public class LocalizationManager implements L18N
{

    private ArrayMap<String,I18NBundle> l18n;
    private ArrayMap<Integer,I18NBundle> l18nMap;

    private BitmapFont font;

    private static final String FONT_PATH_RUSSIAN = "fonts/multi.fnt";

    public LocalizationManager() {
        l18n = new ArrayMap<>();
        l18nMap = new ArrayMap<>();

        System.out.println("Language: " + Locale.getDefault().getLanguage());

        l18n.put(AssetPath.I18N.GUARDIANS,    I18NBundle.createBundle(Gdx.files.internal(AssetPath.I18N.GUARDIANS)));
        l18n.put(AssetPath.I18N.BATTLE,       I18NBundle.createBundle(Gdx.files.internal(AssetPath.I18N.BATTLE)));
        l18n.put(AssetPath.I18N.INVENTORY,    I18NBundle.createBundle(Gdx.files.internal(AssetPath.I18N.INVENTORY)));
        l18n.put(AssetPath.I18N.ATTACKS,      I18NBundle.createBundle(Gdx.files.internal(AssetPath.I18N.ATTACKS)));
        l18n.put(AssetPath.I18N.ELEMENTS,     I18NBundle.createBundle(Gdx.files.internal(AssetPath.I18N.ELEMENTS)));
        l18n.put(AssetPath.I18N.GENERAL,      I18NBundle.createBundle(Gdx.files.internal(AssetPath.I18N.GENERAL)));

        int[] mapIDs ={1,2,25,251,252,253};
        for(int i : mapIDs) {
            l18nMap.put(i, I18NBundle.createBundle(Gdx.files.internal(AssetPath.I18N.MAP_PREFIX+Integer.toString(i))));
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
    public I18NBundle l18n(String type) {
        return l18n.get(type);
    }

    @Override
    public I18NBundle l18nMap(int mapID) {
        return l18nMap.get(mapID);
    }

    public BitmapFont Font() {
        return font;
    }

    @Override
    public I18NBundle General() {
        return this.l18n.get(AssetPath.I18N.GENERAL);
    }

    @Override
    public I18NBundle Abilities() {
        return this.l18n.get(AssetPath.I18N.ATTACKS);
    }

    @Override
    public I18NBundle Guardians() {
        return this.l18n.get(AssetPath.I18N.GUARDIANS);
    }

    @Override
    public I18NBundle Elements() {
        return this.l18n.get(AssetPath.I18N.ELEMENTS);
    }

    @Override
    public I18NBundle Inventory() {
        return this.l18n.get(AssetPath.I18N.INVENTORY);
    }

    @Override
    public I18NBundle Battle() {
        return this.l18n.get(AssetPath.I18N.BATTLE);
    }

    @Override
    public String getGuardianNicknameIfAvailable(AGuardian guardian)
    {
        if(!guardian.getNickname().isEmpty()) {
            return guardian.getNickname();
        } else {
            return getLocalizedGuardianName(guardian);
        }
    }

    @Override
    public String getLocalizedGuardianName(AGuardian guardian)
    {
        int form = guardian.getAbilityGraph().getCurrentForm();
        int speciesID = guardian.getSpeciesDescription().getID();
        return getLocalizedGuardianName(speciesID, form);
    }

    @Override
    public String getLocalizedGuardianName(int speciesID, int form)
    {
        ISpeciesDescriptionService species = GuardiansServiceLocator.INSTANCE.getSpecies();
        String nameID = species.getCommonNameById(speciesID, form);
        return Guardians().get(nameID);
    }

    @Override
    public String getLocalizedGuardianDescription(int speciesID) {
        return Guardians().get("g" + speciesID + "_desc");
    }

    @Override
    public String getLocalizedAbilityName(String abilityID)
    {
        return Abilities().get(abilityID);
    }

    @Override
    public void dispose() {
        this.font.dispose();
    }
}
