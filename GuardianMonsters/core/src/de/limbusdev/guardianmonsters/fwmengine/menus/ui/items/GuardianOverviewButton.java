package de.limbusdev.guardianmonsters.fwmengine.menus.ui.items;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;


import de.limbusdev.guardianmonsters.data.BundleAssets;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.items.Equipment;
import de.limbusdev.guardianmonsters.model.items.EquipmentPotential;
import de.limbusdev.guardianmonsters.model.items.Item;
import de.limbusdev.guardianmonsters.model.monsters.Monster;
import de.limbusdev.guardianmonsters.model.MonsterDB;

/**
 * @author Georg Eckert 2017
 */

public class GuardianOverviewButton extends TextButton implements Listener<Monster> {

    private Table subTable;
    private Item item;
    private Monster monster;


    // ................................................................................ CONSTRUCTORS
    public GuardianOverviewButton(Monster monster, Skin skin, Item item) {
        super(MonsterDB.getLocalNameById(monster.ID), skin);
        construct(monster, item);
    }

    public GuardianOverviewButton(Monster monster, Skin skin, String styleName, Item item) {
        super(MonsterDB.getLocalNameById(monster.ID), skin, styleName);
        construct(monster, item);
    }

    public GuardianOverviewButton(Monster monster, TextButtonStyle style, Item item) {
        super(MonsterDB.getLocalNameById(monster.ID), style);
        construct(monster, item);
    }

    private void construct(Monster monster, Item item) {
        this.item = item;
        this.monster = monster;
        monster.add(this);

        getLabel().setAlignment(Align.topLeft);
        TextureRegion region = Services.getMedia().getMonsterMiniSprite(monster.ID);
        Image monsterImg = new Image(region);
        add(monsterImg).width(16).height(region.getRegionHeight()).align(Align.topLeft);
        row();

        switch(item.getCategory()) {
            case EQUIPMENT:
                augmentButtonEquipment(monster, (Equipment) item);
                break;
            default:
                augmentButtonMedicine(monster, item);
                break;
        }
    }


    // ..................................................................................... METHODS
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



    private void augmentButtonEquipment(Monster monster, Equipment equipment) {
        if(subTable != null) {
            removeActor(subTable);
            getCells().removeIndex(getCells().size-1);
            row();
        }

        EquipmentPotential pot = monster.stat.getEquipmentPotential(equipment);

        String props[]  = {"hp", "mp", "speed", "exp", "pstr", "pdef", "mstr", "mdef"};
        int potValues[] = {pot.hp, pot.mp, pot.speed, pot.exp, pot.pstr, pot.pdef, pot.mstr, pot.mdef};

        String fontStyle, sign, value;
        subTable = new Table();

        for(int i=0; i<props.length; i++) {
            subTable.add(new Image(getSkin().getDrawable("stats-symbol-" + props[i]))).width(16).height(16);

            fontStyle = (potValues[i] > 0 ? "green" : (pot.hp == potValues[i] ? "default" : "red"));
            sign = potValues[i] > 0 ? "+" : "";
            value = Integer.toString(potValues[i]);

            subTable.add(new Label(sign + value, getSkin(), fontStyle)).width(32);
            if(i  == 3) subTable.row();
        }


        add(subTable).align(Align.left);
        layout();

        if(!item.applicable(monster)) {
            setTouchable(Touchable.disabled);
            setColor(.6f,.6f,.6f,1f);
        }
    }

    private void augmentButtonMedicine(Monster monster, Item item) {

        if(subTable != null) {
            removeActor(subTable);
            getCells().removeIndex(getCells().size-1);
            row();
        }

        subTable = new Table();
        subTable.add(new Image(getSkin().getDrawable("stats-symbol-hp")));
        subTable.add(new Label(monster.stat.getHPfractionAsString(), getSkin(), "default")).width(56);
        subTable.add(new Image(getSkin().getDrawable("stats-symbol-mp")));
        subTable.add(new Label(monster.stat.getMPfractionAsString(), getSkin(), "default")).width(56);

        add(subTable).align(Align.left);
        layout();

        if(!item.applicable(monster)) {
            setTouchable(Touchable.disabled);
            setColor(.6f,.6f,.6f,1f);
        }
    }

    @Override
    public void receive(Signal<Monster> signal, Monster monster) {
        switch(item.getCategory()) {
            case EQUIPMENT:
                augmentButtonEquipment(monster, (Equipment) item);
                break;
            default:
                augmentButtonMedicine(monster, item);
                break;
        }
    }
}
