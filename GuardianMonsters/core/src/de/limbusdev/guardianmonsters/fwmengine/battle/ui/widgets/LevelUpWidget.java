package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.Constant;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.widgets.OverlayWidget;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.LevelUpReport;
import de.limbusdev.guardianmonsters.scene2d.AnimatedImage;
import de.limbusdev.guardianmonsters.services.Services;

/**
 * LevelUpWidget
 *
 * @author Georg Eckert 2017
 */

public class LevelUpWidget extends OverlayWidget
{
    private Image monsterImg;

    public LevelUpWidget(Skin skin, AGuardian guardian) {
        super(skin);

        Label bg = new Label("", skin, "paper");
        bg.setSize(300, 180);
        bg.setPosition(Constant.WIDTH/2-150,30,Align.bottomLeft);
        addActor(bg);

        monsterImg = new Image(Services.getMedia().getMonsterSprite(guardian.getID()));
        monsterImg.setPosition(64,64, Align.bottomLeft);
        addActor(monsterImg);

        ImageButton ok = new ImageButton(skin, "button-back");
        ok.setPosition(Constant.WIDTH-64-4, 32+4, Align.bottomRight);
        addActor(ok);
        ok.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                remove();
            }
        });

        Label info = new Label(Services.getL18N().Battle().format("level_up", guardian.getNickname()), skin, "default");
        info.setSize(140,32);
        info.setPosition(128+64,140,Align.bottomLeft);
        info.setWrap(true);
        info.setAlignment(Align.topLeft, Align.topLeft);
        addActor(info);


        // Values

        LevelUpReport lvlUp = guardian.getStatistics().getLatestLevelUpReport();

        Table values = new Table();
        values.align(Align.topLeft);
        values.setSize(140,72);
        values.setPosition(128+64,64,Align.bottomLeft);

        String[] attributes = {"exp", "hp", "mp", "pstr", "pdef", "mstr", "mdef", "speed"};
        int[] oldAttribVals = {lvlUp.oldLevel, lvlUp.oldHP, lvlUp.oldMP, lvlUp.oldPStr, lvlUp.oldPDef, lvlUp.oldMStr, lvlUp.oldMDef, lvlUp.oldSpeed};
        int[] newAttribVals = {lvlUp.newLevel, lvlUp.newHP, lvlUp.newMP, lvlUp.newPStr, lvlUp.newPDef, lvlUp.newMStr, lvlUp.newMDef, lvlUp.newSpeed};
        for(int i=0; i<attributes.length; i++) {
            values.add(new Image(skin.getDrawable("stats-symbol-" + attributes[i]))).size(16,16);
            values.add(new Label(Integer.toString(oldAttribVals[i]) + " > ", skin, "default")).height(16);
            values.add(new Label(Integer.toString(newAttribVals[i]), skin, "green")).height(16).width(32);
             if(i%2 == 1) values.row();
        }


        addActor(values);

        Animation lvlUpAnimation = new Animation(.15f, skin.getRegions("level-up-animation"));
        lvlUpAnimation.setPlayMode(Animation.PlayMode.LOOP);
        AnimatedImage ai = new AnimatedImage(lvlUpAnimation);
        ai.setPosition(128+32,128-32,Align.topLeft);
        addActor(ai);
    }
}
