package de.limbusdev.guardianmonsters.ui.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import de.limbusdev.guardianmonsters.assets.paths.AssetPath;
import de.limbusdev.guardianmonsters.services.Services;

/**
 * ParticleEffectActor
 *
 * @author Georg Eckert 2017
 */

public class ParticleEffectActor extends Actor
{
    private ParticleEffect particleEffect;
    private Vector2 acc = new Vector2();

    public ParticleEffectActor(String name) {
        super();
        TextureAtlas particleAtlas = Services.Media().getTextureAtlas(AssetPath.Spritesheet.PARTICLES);
        ParticleEffect particles = new ParticleEffect();
        particles.load(Gdx.files.internal("particles/" + name + "-particle-effect.p"), particleAtlas);
        this.particleEffect = particles;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        particleEffect.draw(batch);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        acc.set(getWidth()/2, getHeight()/2);
        localToStageCoordinates(acc);
        particleEffect.setPosition(acc.x, acc.y);
        particleEffect.update(delta);
    }

    public void start() {
        particleEffect.start();
    }

    public void allowCompletion() {
        particleEffect.allowCompletion();
    }
}