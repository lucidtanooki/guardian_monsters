package de.limbusdev.guardianmonsters.media;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.enums.SkyDirection;
import de.limbusdev.guardianmonsters.scene2d.AnimatedImage;


/**
 * @author Georg Eckert 2016
 */

public interface IMediaManager
{

    void dispose();

    TextureAtlas getTextureAtlasType(TextureAtlasType type);

    /**
     * Texture Atlas for a person
     * @param male  true=male, false=female
     * @param index
     * @return
     */
    TextureAtlas getPersonTextureAtlas(boolean male, int index);

    ArrayMap<SkyDirection, Animation<AtlasRegion>> getPersonAnimationSet(boolean gender, int index);

    ArrayMap<SkyDirection, Animation<AtlasRegion>> getPersonAnimationSet(String name);

    ArrayMap<SkyDirection, Animation<AtlasRegion>> getPersonAnimationSet(TextureAtlas atlas);

    Texture getTexture(String path);

    TextureAtlas.AtlasRegion getMonsterSprite(int index, int form);

    TextureAtlas.AtlasRegion getMonsterMiniSprite(int index, int form);

    Drawable getItemDrawable(String nameID);

    Image getMonsterFace(int id, int form);

    TextureRegion getBackgroundTexture(int index);

    TextureAtlas getTextureAtlas(String path);

    Animation<AtlasRegion> getAttackAnimation(String attack);

    Animation<AtlasRegion> getSummoningAnimation();

    Animation<AtlasRegion> getBanningAnimation();

    Animation<AtlasRegion> getStatusEffectAnimation(String statusEffect);

    /**
     * 0 - Water
     * 1 - Water Inner NE
     * 2 - Water Inner NW
     * 3 - Water Inner SE
     * 4 - Water Inner SW
     * 5 - Water N
     * 6 - Water NE
     * 7 - Water NW
     * 8 - Water S
     * 9 - Water SE
     * 10 - Water SW
     * 11 - Water E
     * 12 - Water W
     * 13 - Fire 2
     * 14 - Fire 1
     * 15 - Lamp 1
     * 16 - Light 1
     * @param index
     * @return
     */
    Animation<AtlasRegion> getTileAnimation(int index);

    Animation<AtlasRegion> getObjectAnimation(String id);

    Image getMetamorphosisBackground();

    AnimatedImage getMetamorphosisAnimation();

}
