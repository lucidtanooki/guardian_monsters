package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.limbusdev.guardianmonsters.enums.TextureAtlasType;

/**
 * NullMedia, gets returned from Service Locator, when no Media Service has been injected yet.
 * Created by georg on 14.11.16.
 */

public class NullMedia implements Media {

    public void dispose() {}

    public TextureAtlas getTextureAtlasType(TextureAtlasType type) {
        return null;
    }

    public TextureAtlas getPersonTextureAtlas(boolean male, int index) {
        return null;
    }

    public Texture getMainMenuBGImg() {
        return null;
    }

    public Texture getMainMenuBGImg2() {
        return null;
    }

    public TextureAtlas.AtlasRegion getMonsterSprite(int index) {
        return null;
    }

    public Texture getBackgroundTexture(int index) {
        return null;
    }

    public TextureAtlas getBattleUITextureAtlas() {
        return null;
    }

    public TextureAtlas getUITextureAtlas() {
        return null;
    }

    public TextureAtlas getLogosTextureAtlas() {
        return null;
    }

    public Animation getAttackAnimation(String attack) {
        return null;
    }

    public Animation getTileAnimation(int index) {
        return null;
    }

    public Skin getBattleSkin() {
        return null;
    }

    public Skin getInventorySkin() {
        return null;
    }

    public Skin getSkin() {
        return null;
    }
}
