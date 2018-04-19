package de.limbusdev.guardianmonsters.ui.metamorphosis;

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

import de.limbusdev.guardianmonsters.assets.paths.AssetPath;
import de.limbusdev.guardianmonsters.media.IMediaManager;
import de.limbusdev.guardianmonsters.scene2d.AnimatedImage;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.ui.AHUD;
import de.limbusdev.guardianmonsters.ui.Constant;
import de.limbusdev.guardianmonsters.ui.widgets.ParticleEffectActor;

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

    public MetamorphosisHUD(Skin skin, int speciesID, int formerMetaForm, int newMetaForm) {
        super(skin);

        I18NBundle bundle = Services.getL18N().General();
        final IMediaManager media = Services.getMedia();

        String[] monsterNames = {
            Services.getL18N().getLocalizedGuardianName(speciesID, formerMetaForm),
            Services.getL18N().getLocalizedGuardianName(speciesID, newMetaForm)
        };

        final String[] messages =  {
            bundle.format("monster_metamorphs", monsterNames[0]),
            bundle.format("monster_metamorph_complete", monsterNames[0], monsterNames[1])
        };


        // TODO metamorph only at node activation

        // Actor Creation
        Image bg = media.getMetamorphosisBackground();
        animation = media.getMetamorphosisAnimation();
        imgBefore = new Image(media.getMonsterSprite(formerMetaForm,0));
        imgAfter  = new Image(media.getMonsterSprite(newMetaForm,0));
        ok = new ImageButton(skin, "burgund-close");
        label = new Label(messages[0], skin, "burgund");
        label.setSize(420,64);

        ParticleEffectActor particleActor = new ParticleEffectActor("metamorphosis");
        particleActor.setPosition(214,136);


        // Listeners
        ok.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addAction(Actions.sequence(
                    Services.getAudio().getMuteAudioAction(AssetPath.Audio.Music.METAMORPHOSIS),
                    Actions.fadeOut(1),
                    Actions.delay(1),
                    Actions.run(() -> Services.getScreenManager().popScreen())
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
            Services.getAudio().getMuteAudioAction(AssetPath.Audio.Music.METAMORPHOSIS),
            Actions.delay(1),
            Actions.run(() -> {
                label.remove();
                stage.addActor(animation);
                Services.getAudio().playSound(AssetPath.Audio.SFX.METAMORPHOSIS);
                stage.addActor(label);
            }),
            Actions.delay(2),
            Actions.run(() -> {
                animation.remove();
                imgBefore.remove();
                label.remove();
                stage.addActor(imgAfter);
                stage.addActor(animation);
                stage.addActor(label);
            }),
            Actions.delay(3),
            Actions.run(() -> {
                Services.getAudio().playMusic(AssetPath.Audio.Music.VICTORY_FANFARE);
                label.setText(messages[1]);
            }),
            Actions.delay(5.5f),
            Services.getAudio().getFadeInMusicAction(AssetPath.Audio.Music.METAMORPHOSIS),
            Actions.run(() -> stage.addActor(ok))
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
        Services.getAudio().playLoopMusic(AssetPath.Audio.Music.METAMORPHOSIS);
    }

    @Override
    public void hide() {
        super.hide();
        Services.getAudio().stopMusic(AssetPath.Audio.Music.METAMORPHOSIS);
    }
}
