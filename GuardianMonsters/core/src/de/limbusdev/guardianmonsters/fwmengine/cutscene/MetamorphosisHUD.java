package de.limbusdev.guardianmonsters.fwmengine.cutscene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;

import de.limbusdev.guardianmonsters.Constant;
import de.limbusdev.guardianmonsters.data.AudioAssets;
import de.limbusdev.guardianmonsters.data.TextureAssets;
import de.limbusdev.guardianmonsters.fwmengine.managers.Media;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.widgets.AnimatedImage;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.widgets.ParticleEffectActor;
import de.limbusdev.guardianmonsters.fwmengine.ui.AHUD;
import de.limbusdev.guardianmonsters.model.MonsterDB;

/**
 * MetamorphosisHUD
 *
 * @author Georg Eckert 2017
 */

public class MetamorphosisHUD extends AHUD {

    private AnimatedImage animation;
    private Image imgBefore, imgAfter;
    private Button ok;
    private Label label;

    public MetamorphosisHUD(Skin skin, int before, int after) {
        super(skin);

        I18NBundle bundle = Services.getL18N().i18nGeneral();
        final Media media = Services.getMedia();

        String[] monsterNames = {
            MonsterDB.getLocalNameById(before),
            MonsterDB.getLocalNameById(after)
        };

        final String[] messages =  {
            bundle.format("monster_metamorphs", monsterNames[0]),
            bundle.format("monster_metamorph_complete", monsterNames[0], monsterNames[1])
        };

        // Actor Creation
        Image bg = media.getMetamorphosisBackground();
        animation = media.getMetamorphosisAnimation();
        imgBefore = new Image(media.getMonsterSprite(before));
        imgAfter  = new Image(media.getMonsterSprite(after));
        ok = new ImageButton(skin, "burgund-close");
        label = new Label(messages[0], skin, "burgund");
        label.setSize(420,64);

        TextureAtlas particleAtlas = media.getTextureAtlas(TextureAssets.particleTextures);
        ParticleEffect particles = new ParticleEffect();
        particles.load(Gdx.files.internal("particles/metamorphosis-particle-effect.p"), particleAtlas);
        ParticleEffectActor particleActor = new ParticleEffectActor(particles);
        particleActor.setPosition(214,136);


        // Listeners
        ok.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addAction(Actions.sequence(
                    Services.getAudio().getMuteAudioAction(AudioAssets.metamorphosisMusic),
                    Actions.fadeOut(1),
                    Actions.delay(1),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            Services.getScreenManager().popScreen();
                        }
                    })
                ));
            }
        });

        // Layout
        bg.setPosition(0,0,Align.bottomLeft);
        animation.setPosition(Constant.RES_X/2-128, Constant.RES_Y/2-128+30, Align.bottomLeft);
        imgBefore.setPosition(Constant.RES_X/2-64,Constant.RES_Y/2-34,Align.bottomLeft);
        imgAfter.setPosition(Constant.RES_X/2-64,Constant.RES_Y/2-34,Align.bottomLeft);
        ok.setPosition(Constant.RES_X-16,18,Align.bottomRight);
        label.setPosition(4,4,Align.bottomLeft);

        Action metaAction = Actions.sequence(
            Actions.delay(4),
            Services.getAudio().getMuteAudioAction(AudioAssets.metamorphosisMusic),
            Actions.delay(1),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    label.remove();
                    stage.addActor(animation);
                    Services.getAudio().playSound(AudioAssets.metamorphosisSFX);
                    stage.addActor(label);
                }
            }),
            Actions.delay(2),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    animation.remove();
                    imgBefore.remove();
                    label.remove();
                    stage.addActor(imgAfter);
                    stage.addActor(animation);
                    stage.addActor(label);
                }
            }),
            Actions.delay(3),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    Services.getAudio().playMusic(AudioAssets.victoryFanfareMusic);
                    label.setText(messages[1]);
                }
            }),
            Actions.delay(5.5f),
            Services.getAudio().getFadeInMusicAction(AudioAssets.metamorphosisMusic),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    stage.addActor(ok);
                }
            })
        );

        // Adding actors to stage
        stage.addActor(bg);
        stage.addActor(particleActor);
        stage.addActor(imgBefore);
        stage.addAction(metaAction);
        stage.addActor(label);

        particleActor.start();
    }

    @Override
    protected void reset() {

    }

    @Override
    public void show() {
        Services.getAudio().playLoopMusic(AudioAssets.metamorphosisMusic);
    }

    @Override
    public void hide() {
        super.hide();
        Services.getAudio().stopMusic(AudioAssets.metamorphosisMusic);
    }
}
