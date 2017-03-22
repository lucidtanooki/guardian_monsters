package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.fwmengine.world.model.SkyDirection;

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

    @Override
    public ArrayMap<SkyDirection, Animation<AtlasRegion>> getPersonAnimationSet(boolean gender, int index) {
        return null;
    }

    @Override
    public ArrayMap<SkyDirection, Animation<AtlasRegion>> getPersonAnimationSet(String name) {
        return null;
    }

    @Override
    public ArrayMap<SkyDirection, Animation<AtlasRegion>> getPersonAnimationSet(TextureAtlas atlas) {
        return null;
    }

    @Override
    public Texture getTexture(String path) {
        return null;
    }

    public TextureAtlas.AtlasRegion getMonsterSprite(int index) {
        return null;
    }

    @Override
    public TextureAtlas.AtlasRegion getMonsterMiniSprite(int index) {
        return null;
    }

    @Override
    public Image getMonsterFace(int id) {
        return null;
    }

    public TextureRegion getBackgroundTexture(int index) {
        return null;
    }

    @Override
    public TextureAtlas getTextureAtlas(String path) {
        return null;
    }

    public Animation getAttackAnimation(String attack) {
        return null;
    }

    public Animation getTileAnimation(int index) {
        return null;
    }

    public Animation getObjectAnimation(String id) {return null;}

}
