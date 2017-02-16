package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.fwmengine.managers.Media;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInformation;
import de.limbusdev.guardianmonsters.utils.GS;


/*
 * Copyright (c) 2016 by Georg Eckert
 *
 * Licensed under GPL 3.0 https://www.gnu.org/licenses/gpl-3.0.en.html
 */

public class MonsterStatusInventoryWidget extends Group {
    private Label name;
    private Label pStr, pDef, mStr, mDef, speed, exp, hp, mp;

    public MonsterStatusInventoryWidget (Skin skin) {
        super();

        setSize(140,GS.HEIGHT-36);
        Image monsterStatsBg = new Image(skin.getDrawable("menu-col-bg"));
        monsterStatsBg.setPosition(2,2,Align.bottomLeft);
        addActor(monsterStatsBg);

        name = new Label("Monster Name", skin, "default");
        name.setPosition(8,200-8,Align.topLeft);
        addActor(name);

        Label key = new Label("HP", skin, "default");
        key.setPosition(8,200-8-18,Align.topLeft);
        addActor(key);

        key = new Label("MP", skin, "default");
        key.setPosition(8,200-8-18*2,Align.topLeft);
        addActor(key);

        key = new Label("EXP", skin, "default");
        key.setPosition(8,200-8-18*3,Align.topLeft);
        addActor(key);

        key = new Label("PStr", skin, "default");
        key.setPosition(8,200-8-18*4,Align.topLeft);
        addActor(key);

        key = new Label("PDef", skin, "default");
        key.setPosition(8,200-8-18*5,Align.topLeft);
        addActor(key);

        key = new Label("MStr", skin, "default");
        key.setPosition(8,200-8-18*6,Align.topLeft);
        addActor(key);

        key = new Label("MDef", skin, "default");
        key.setPosition(8,200-8-18*7,Align.topLeft);
        addActor(key);

        key = new Label("Speed", skin, "default");
        key.setPosition(8,200-8-18*8,Align.topLeft);
        addActor(key);

        hp = new Label("0", skin, "default");
        hp.setPosition(8+64,200-8-18,Align.topLeft);
        addActor(hp);

        mp = new Label("0", skin, "default");
        mp.setPosition(8+64,200-8-18*2,Align.topLeft);
        addActor(mp);

        exp = new Label("0", skin, "default");
        exp.setPosition(8+64,200-8-18*3,Align.topLeft);
        addActor(exp);

        pStr = new Label("0", skin, "default");
        pStr.setPosition(8+64,200-8-18*4,Align.topLeft);
        addActor(pStr);

        pDef = new Label("0", skin, "default");
        pDef.setPosition(8+64,200-8-18*5,Align.topLeft);
        addActor(pDef);

        mStr = new Label("0", skin, "default");
        mStr.setPosition(8+64,200-8-18*6,Align.topLeft);
        addActor(mStr);

        mDef = new Label("0", skin, "default");
        mDef.setPosition(8+64,200-8-18*7,Align.topLeft);
        addActor(mDef);

        speed = new Label("0", skin, "default");
        speed.setPosition(8+64,200-8-18*8,Align.topLeft);
        addActor(speed);



    }


    public void init(Monster m) {
        name.setText(Services.getL18N().l18n().get(MonsterInformation.getInstance().monsterNames.get(m.ID)));
        hp.setText(m.getHP() + "/" + m.getHPfull());
        mp.setText(m.getMP() + "/" + m.getMPfull());
        exp.setText(m.getExp() + "/" + m.expAvailableInThisLevel());
        pStr.setText(Integer.toString(m.pStrFull));
        pDef.setText(Integer.toString(m.pDefFull));
        mStr.setText(Integer.toString(m.mStrFull));
        mDef.setText(Integer.toString(m.mDefFull));
        speed.setText(Integer.toString(m.getSpeedFull()));

    }
}
