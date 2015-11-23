package org.limbusdev.monsterworld.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ArrayMap;

import org.limbusdev.monsterworld.graphics.EntitySprite;
import org.limbusdev.monsterworld.utils.UnitConverter;

/**
 * Created by georg on 22.11.15.
 */
public class CharacterSpriteComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public ArrayMap<String,Animation>   animationImgs; // characters animations (N,S,W,E)
    public TextureRegion                recentIdleImg;  // alive idle image
    public Animation                    recentAnim;     // alive animation
    public EntitySprite                 sprite;         // characters sprite
    /* ........................................................................... CONSTRUCTOR .. */
    public CharacterSpriteComponent (TextureAtlas textureAtlas) {
        // load animation textures
        animationImgs = new ArrayMap<String,Animation>();
        animationImgs.put("n", new Animation(.15f, textureAtlas.findRegions("n")));
        animationImgs.put("e", new Animation(.15f, textureAtlas.findRegions("e")));
        animationImgs.put("s", new Animation(.15f, textureAtlas.findRegions("s")));
        animationImgs.put("w", new Animation(.15f, textureAtlas.findRegions("w")));

        recentAnim    = animationImgs.get("s");
        recentIdleImg = animationImgs.get("s").getKeyFrames()[0];
        this.sprite = new EntitySprite(recentIdleImg);
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
