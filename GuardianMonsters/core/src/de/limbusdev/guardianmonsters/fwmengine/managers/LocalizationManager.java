package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

/**
 * Created by georg on 14.11.16.
 */

public class LocalizationManager implements L18N {
    private static I18NBundle l18n;

    private BitmapFont font;

    private static final String FONT_PATH_RUSSIAN = "fonts/multi.fnt";

    public LocalizationManager() {
        System.out.println("Language: " + Locale.getDefault().getLanguage());
        FileHandle baseFileHandle = Gdx.files.internal("l18n/guardianmonsters");
        l18n = I18NBundle.createBundle(baseFileHandle);

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

    public I18NBundle l18n() {
        return l18n;
    }

    public BitmapFont getFont() {
        return font;
    }
}
