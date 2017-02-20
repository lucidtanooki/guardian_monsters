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
    private ArrayMap<String,Label> valueLabels;

    public MonsterStatusInventoryWidget (Skin skin) {
        super();

        setSize(140,GS.HEIGHT-36);
        Image monsterStatsBg = new Image(skin.getDrawable("menu-col-bg"));
        monsterStatsBg.setPosition(2,2,Align.bottomLeft);
        addActor(monsterStatsBg);

        Image nameBg = new Image(skin.getDrawable("name-bg"));
        nameBg.setPosition(4,200-4,Align.topLeft);
        addActor(nameBg);

        int offX = 16;
        int offY = 200-12;
        int gap = 18;

        name = new Label("Monster Name", skin, "default");
        name.setPosition(offX ,200-10,Align.topLeft);
        addActor(name);

        valueLabels = new ArrayMap<>();
        Label value;
        String[] labels = {"hp", "mp", "exp", "pstr", "pdef", "mstr", "mdef", "speed"};
        Image key;
        for(int i=0; i<labels.length; i++) {
            Image valueBg = new Image(skin.getDrawable("key-bg"));
            valueBg.setPosition(offX-2,offY-gap*(i+1)+2,Align.topLeft);
            addActor(valueBg);

            key = new Image(skin.getDrawable("stats-symbol-" + labels[i]));
            key.setSize(16,16);
            key.setPosition(offX, offY-gap*(i+1), Align.topLeft);
            addActor(key);

            value = new Label("0", skin, "default");
            value.setPosition(offX+64, offY-gap*(i+1), Align.topLeft);
            addActor(value);
            valueLabels.put(labels[i], value);
        }

    }


    public void init(Monster m) {
        name.setText(Services.getL18N().l18n().get(MonsterInformation.getInstance().monsterNames.get(m.ID)));
        valueLabels.get("hp").setText(m.getHP() + "/" + m.getHPfull());
        valueLabels.get("mp").setText(m.getMP() + "/" + m.getMPfull());
        valueLabels.get("exp").setText(m.getExp() + "/" + m.expAvailableInThisLevel());
        valueLabels.get("pstr").setText(Integer.toString(m.pStrFull));
        valueLabels.get("pdef").setText(Integer.toString(m.pDefFull));
        valueLabels.get("mstr").setText(Integer.toString(m.mStrFull));
        valueLabels.get("mdef").setText(Integer.toString(m.mDefFull));
        valueLabels.get("speed").setText(Integer.toString(m.getSpeedFull()));
    }
}
