package de.limbusdev.guardianmonsters.fwmengine.menus.ui.items;

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


import java.util.Observable;
import java.util.Observer;

import de.limbusdev.guardianmonsters.data.BundleAssets;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Equipment;
import de.limbusdev.guardianmonsters.model.Item;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterDB;

/**
 * Created by georg on 21.02.17.
 */

public class GuardianOverviewButton extends TextButton implements Observer{

    private Table subTable;
    private Item item;
    private Monster monster;


    public GuardianOverviewButton(Monster monster, Skin skin, Item item) {
        super(Services.getL18N().l18n(BundleAssets.MONSTERS).get(MonsterDB.singleton().getNameById(monster.ID)), skin);
        construct(monster, item);
    }

    public GuardianOverviewButton(Monster monster, Skin skin, String styleName, Item item) {
        super(Services.getL18N().l18n(BundleAssets.MONSTERS).get(MonsterDB.singleton().getNameById(monster.ID)), skin, styleName);
        construct(monster, item);
    }

    public GuardianOverviewButton(Monster monster, TextButtonStyle style, Item item) {
        super(Services.getL18N().l18n(BundleAssets.MONSTERS).get(MonsterDB.singleton().getNameById(monster.ID)), style);
        construct(monster, item);
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

    private void construct(Monster monster, Item item) {
        this.item = item;
        this.monster = monster;
        monster.addObserver(this);

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

    private void augmentButtonEquipment(Monster monster, Equipment equipment) {
        if(subTable != null) {
            removeActor(subTable);
            getCells().removeIndex(getCells().size-1);
            row();
        }

        Monster.EquipmentPotential pot = monster.getEquipmentPotential(equipment);

        String props[]  = {"hp", "mp", "speed", "exp", "pstr", "pdef", "mstr", "mdef"};
        int potValues[] = {pot.hp, pot.mp, pot.speed, pot.exp, pot.pstr, pot.pdef, pot.mstr, pot.mdef};

        String fontStyle;
        subTable = new Table();

        for(int i=0; i<props.length; i++) {
            subTable.add(new Image(getSkin().getDrawable("stats-symbol-" + props[i]))).width(16).height(16);
            fontStyle = (potValues[i] > 0 ? "green" : (pot.hp == potValues[i] ? "default" : "red"));
            subTable.add(new Label((potValues[i] > 0 ? "+" : "")  + Integer.toString(potValues[i]), getSkin(), fontStyle)).width(32);
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
        subTable.add(new Label(Integer.toString(monster.getHP()) + " / " + Integer.toString(monster.getHPfull()), getSkin(), "default")).width(56);
        subTable.add(new Image(getSkin().getDrawable("stats-symbol-mp")));
        subTable.add(new Label(Integer.toString(monster.getMP()) + " / " + Integer.toString(monster.getMPfull()), getSkin(), "default")).width(56);

        add(subTable).align(Align.left);
        layout();

        if(!item.applicable(monster)) {
            setTouchable(Touchable.disabled);
            setColor(.6f,.6f,.6f,1f);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof Monster) {
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
}
