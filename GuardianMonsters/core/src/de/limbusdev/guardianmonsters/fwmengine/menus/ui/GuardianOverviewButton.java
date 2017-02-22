package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInformation;

/**
 * Created by georg on 21.02.17.
 */

public class GuardianOverviewButton extends TextButton {

    private Table subTable;


    public GuardianOverviewButton(Monster monster, Skin skin) {
        super(Services.getL18N().l18n().get(MonsterInformation.getInstance().getNameById(monster.ID)), skin);
        augmentButton(monster);
    }

    public GuardianOverviewButton(Monster monster, Skin skin, String styleName) {
        super(Services.getL18N().l18n().get(MonsterInformation.getInstance().getNameById(monster.ID)), skin, styleName);
        augmentButton(monster);
    }

    public GuardianOverviewButton(Monster monster, TextButtonStyle style) {
        super(Services.getL18N().l18n().get(MonsterInformation.getInstance().getNameById(monster.ID)), style);
        augmentButton(monster);
    }

    @Override
    public void setColor(float r, float g, float b, float a) {
        setColor(new Color(r,g,b,a));
    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);
        for(Actor a : getChildren()) {
            a.setColor(color);
        }
        subTable.setColor(color);
        for(Actor a : subTable.getChildren()) {
            a.setColor(color);
        }
    }

    private void augmentButton(Monster monster) {

        getLabel().setAlignment(Align.topLeft);
        TextureRegion region = Services.getMedia().getMonsterMiniSprite(monster.ID);
        Image monsterImg = new Image(region);
        add(monsterImg).width(16).height(region.getRegionHeight()).align(Align.topLeft);

        row();

        subTable = new Table();
        subTable.add(new Image(getSkin().getDrawable("stats-symbol-hp")));
        subTable.add(new Label(Integer.toString(monster.getHP()) + " / " + Integer.toString(monster.getHPfull()), getSkin(), "default")).width(56);
        subTable.add(new Image(getSkin().getDrawable("stats-symbol-mp")));
        subTable.add(new Label(Integer.toString(monster.getMP()) + " / " + Integer.toString(monster.getMPfull()), getSkin(), "default")).width(56);

        add(subTable).align(Align.left);
    }
}
