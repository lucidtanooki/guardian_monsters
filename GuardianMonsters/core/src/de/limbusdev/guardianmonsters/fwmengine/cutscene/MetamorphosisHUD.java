package de.limbusdev.guardianmonsters.fwmengine.cutscene;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.Constant;
import de.limbusdev.guardianmonsters.data.TextureAssets;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.team.TeamMemberSwitcher;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.widgets.AnimatedImage;
import de.limbusdev.guardianmonsters.fwmengine.ui.AHUD;
import de.limbusdev.guardianmonsters.model.MonsterDB;

/**
 * MetamorphosisHUD
 *
 * @author Georg Eckert 2017
 */

public class MetamorphosisHUD extends AHUD {
    public MetamorphosisHUD(Skin skin, int before, int after) {
        super(skin);
        Animation animation = new Animation(.15f,Services.getMedia().getTextureAtlas(
            TextureAssets.bigAnimations).findRegions("metamorphosis"));
        final AnimatedImage metamorphosisAnimation = new AnimatedImage(animation);
        metamorphosisAnimation.setPosition(Constant.RES_X/2-128, Constant.RES_Y/2-128, Align.bottomLeft);
        metamorphosisAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        final Image imgBefore = new Image(Services.getMedia().getMonsterSprite(before));
        imgBefore.setPosition(Constant.RES_X/2-64,Constant.RES_Y/2-64,Align.bottomLeft);
        final Image imgAfter  = new Image(Services.getMedia().getMonsterSprite(after));
        imgAfter.setPosition(Constant.RES_X/2-64,Constant.RES_Y/2-64,Align.bottomLeft);

        final Button ok = new TextButton("OK", skin, "default");
        ok.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Services.getScreenManager().popScreen();
            }
        });

        Label label = new Label(MonsterDB.getLocalNameById(before) + " starts to metamorph!", skin, "default");
        stage.addActor(label);

        stage.addActor(imgBefore);

        Action metaAction = Actions.sequence(
            Actions.delay(5),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    stage.addActor(metamorphosisAnimation);
                }
            }),
            Actions.delay(2),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    metamorphosisAnimation.remove();
                    imgBefore.remove();
                    stage.addActor(imgAfter);
                    stage.addActor(metamorphosisAnimation);
                }
            }),
            Actions.delay(2),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    stage.addActor(ok);
                }
            })
        );

        stage.addAction(metaAction);
    }

    @Override
    protected void reset() {

    }

    @Override
    public void show() {

    }
}
