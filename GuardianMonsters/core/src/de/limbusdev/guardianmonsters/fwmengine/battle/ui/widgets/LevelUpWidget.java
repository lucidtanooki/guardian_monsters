package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.data.BundleAssets;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.widgets.OverlayWidget;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterDB;
import de.limbusdev.guardianmonsters.utils.Constant;

/**
 * LevelUpWidget
 *
 * @author Georg Eckert 2017
 */

public class LevelUpWidget extends OverlayWidget {
    private Image monsterImg;

    public LevelUpWidget(Skin skin, Monster monster) {
        super(skin);

        Label bg = new Label("", skin, "paper");
        bg.setSize(300, 180);
        bg.setPosition(Constant.WIDTH/2-150,30,Align.bottomLeft);
        addActor(bg);

        monsterImg = new Image(Services.getMedia().getMonsterSprite(monster.ID));
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

        Label info = new Label(Services.getL18N().l18n(BundleAssets.BATTLE).format("level_up",
            Services.getL18N().l18n(BundleAssets.MONSTERS).get(MonsterDB.singleton().getNameById(monster.ID))), skin, "default");
        info.setSize(140,32);
        info.setPosition(128+64,140,Align.bottomLeft);
        info.setWrap(true);
        info.setAlignment(Align.topLeft, Align.topLeft);
        addActor(info);


        // Values

        Monster.LevelUpReport lvlUp = monster.levelUpReport;

        Table values = new Table();
        values.align(Align.topLeft);
        values.setSize(140,72);
        values.setPosition(128+64,60,Align.bottomLeft);
        values.add(new Image(skin.getDrawable("stats-symbol-exp"))).size(16,16);
        values.add(new Label(Integer.toString(lvlUp.oldLevel) + " > ", skin, "default")).height(16);
        values.add(new Label(Integer.toString(lvlUp.newLevel), skin, "green")).height(16);
        values.row();
        addActor(values);
    }
}
