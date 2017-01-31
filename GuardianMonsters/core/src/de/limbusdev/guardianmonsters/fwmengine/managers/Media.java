package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.limbusdev.guardianmonsters.enums.TextureAtlasType;
import de.limbusdev.guardianmonsters.fwmengine.world.ui.AnimatedSprite;

/**
 * Created by georg on 14.11.16.
 */

public interface Media {

    public void dispose();

    public TextureAtlas getTextureAtlasType(TextureAtlasType type);

    /**
     * Texture Atlas for a person
     * @param male  true=male, false=female
     * @param index
     * @return
     */
    public TextureAtlas getPersonTextureAtlas(boolean male, int index);

    public Texture getTexture(String path);

    public TextureAtlas.AtlasRegion getMonsterSprite(int index);

    public TextureAtlas.AtlasRegion getMonsterMiniSprite(int index);

    public TextureRegion getBackgroundTexture(int index);

    public TextureAtlas getTextureAtlas(String path);

    public Animation getAttackAnimation(String attack);

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
    public Animation getTileAnimation(int index);

    public Animation getObjectAnimation(String id);

}
