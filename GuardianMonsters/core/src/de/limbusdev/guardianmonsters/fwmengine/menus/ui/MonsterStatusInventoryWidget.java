package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.data.BundleAssets;
import de.limbusdev.guardianmonsters.enums.Element;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInfo;
import de.limbusdev.guardianmonsters.utils.GS;


/*
 * Copyright (c) 2016 by Georg Eckert
 *
 * Licensed under GPL 3.0 https://www.gnu.org/licenses/gpl-3.0.en.html
 */

public class MonsterStatusInventoryWidget extends Group {
    private Label name;
    private ArrayMap<String,Label> valueLabels;
    private HorizontalGroup elementGroup;
    private Group equipmentGroup;
    private Skin skin;

    public MonsterStatusInventoryWidget (Skin skin) {
        super();
        this.skin = skin;

        setSize(140,GS.HEIGHT-36);
        Image monsterStatsBg = new Image(skin.getDrawable("menu-col-bg"));
        monsterStatsBg.setPosition(2,2,Align.bottomLeft);
        addActor(monsterStatsBg);

        Image nameBg = new Image(skin.getDrawable("name-bg"));
        nameBg.setPosition(4,200-4,Align.topLeft);
        addActor(nameBg);

        int offX = 16;
        int offY = 200-8;
        int gap = 18;

        name = new Label("Monster Name", skin, "default");
        name.setPosition(offX ,200-6,Align.topLeft);
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
            value.setPosition(offX+20, offY-gap*(i+1), Align.topLeft);
            addActor(value);
            valueLabels.put(labels[i], value);
        }

        for(int bg=0; bg<4; bg++) {
            Label bgl = new Label("", skin, "paper-dark-area");
            bgl.setSize(36,36);
            bgl.setPosition(100,offY-14-bg*38, Align.topLeft);
            addActor(bgl);
        }

        elementGroup = new HorizontalGroup();
        elementGroup.setSize(140,20);
        elementGroup.setPosition(6,6,Align.bottomLeft);
        addActor(elementGroup);
        equipmentGroup = new Group();
        addActor(equipmentGroup);

    }


    public void init(Monster m) {
        name.setText(Services.getL18N().l18n(BundleAssets.MONSTERS).get(MonsterInfo.getInstance().getNameById(m.ID)));
        valueLabels.get("hp").setText(m.getHP() + "/" + m.getHPfull());
        valueLabels.get("mp").setText(m.getMP() + "/" + m.getMPfull());
        valueLabels.get("exp").setText(m.getExp() + "/" + m.expAvailableInThisLevel());
        valueLabels.get("pstr").setText(Integer.toString(m.pStrFull));
        valueLabels.get("pdef").setText(Integer.toString(m.pDefFull));
        valueLabels.get("mstr").setText(Integer.toString(m.mStrFull));
        valueLabels.get("mdef").setText(Integer.toString(m.mDefFull));
        valueLabels.get("speed").setText(Integer.toString(m.getSpeedFull()));

        elementGroup.clear();
        for(Element e : m.elements) {
            String elem = e.toString().toLowerCase();
            String elemName = Services.getL18N().l18n(BundleAssets.ELEMENTS).get("element_" + elem);
            elemName = elemName.length() < 7 ? elemName : elemName.substring(0,6);
            Label l = new Label(elemName, skin, "elem-" + elem);
            elementGroup.addActor(l);
        }

        equipmentGroup.clear();
        if(m.head != null) {
            Image img = new Image(skin.getDrawable(m.head.getName()));
            img.setSize(32,32);
            img.setPosition(102,178-2,Align.topLeft);
            equipmentGroup.addActor(img);
        }
        if(m.hands != null) {
            Image img = new Image(skin.getDrawable(m.hands.getName()));
            img.setSize(32,32);
            img.setPosition(102,178-2-38,Align.topLeft);
            equipmentGroup.addActor(img);
        }
        if(m.body != null) {
            Image img = new Image(skin.getDrawable(m.body.getName()));
            img.setSize(32,32);
            img.setPosition(102,178-2-38*2,Align.topLeft);
            equipmentGroup.addActor(img);
        }
        if(m.feet != null) {
            Image img = new Image(skin.getDrawable(m.feet.getName()));
            img.setSize(32,32);
            img.setPosition(102,178-2-38*3,Align.topLeft);
            equipmentGroup.addActor(img);
        }
    }
}
