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


import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.guardians.items.Equipment;
import de.limbusdev.guardianmonsters.guardians.items.EquipmentPotential;
import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;

/**
 * @author Georg Eckert 2017
 */

public class GuardianOverviewButton extends TextButton implements Listener<Guardian> {

    private Table subTable;
    private Item item;
    private Guardian guardian;


    // ................................................................................ CONSTRUCTORS
    public GuardianOverviewButton(Guardian guardian, Skin skin, Item item) {
        super(Services.getL18N().getLocalizedGuardianName(guardian), skin);
        construct(guardian, item);
    }

    public GuardianOverviewButton(Guardian guardian, Skin skin, String styleName, Item item) {
        super(Services.getL18N().getLocalizedGuardianName(guardian), skin, styleName);
        construct(guardian, item);
    }

    public GuardianOverviewButton(Guardian guardian, TextButtonStyle style, Item item) {
        super(Services.getL18N().getLocalizedGuardianName(guardian), style);
        construct(guardian, item);
    }

    private void construct(Guardian guardian, Item item) {
        this.item = item;
        this.guardian = guardian;
        guardian.add(this);

        getLabel().setAlignment(Align.topLeft);
        TextureRegion region = Services.getMedia().getMonsterMiniSprite(guardian.ID);
        Image monsterImg = new Image(region);
        add(monsterImg).width(16).height(region.getRegionHeight()).align(Align.topLeft);
        row();

        switch(item.getCategory()) {
            case EQUIPMENT:
                augmentButtonEquipment(guardian, (Equipment) item);
                break;
            default:
                augmentButtonMedicine(guardian, item);
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



    private void augmentButtonEquipment(Guardian guardian, Equipment equipment) {
        if(subTable != null) {
            removeActor(subTable);
            getCells().removeIndex(getCells().size-1);
            row();
        }

        EquipmentPotential pot = guardian.stat.getEquipmentPotential(equipment);

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

        if(!item.applicable(guardian)) {
            setTouchable(Touchable.disabled);
            setColor(.6f,.6f,.6f,1f);
        }
    }

    private void augmentButtonMedicine(Guardian guardian, Item item) {

        if(subTable != null) {
            removeActor(subTable);
            getCells().removeIndex(getCells().size-1);
            row();
        }

        subTable = new Table();
        subTable.add(new Image(getSkin().getDrawable("stats-symbol-hp")));
        subTable.add(new Label(guardian.stat.getHPfractionAsString(), getSkin(), "default")).width(56);
        subTable.add(new Image(getSkin().getDrawable("stats-symbol-mp")));
        subTable.add(new Label(guardian.stat.getMPfractionAsString(), getSkin(), "default")).width(56);

        add(subTable).align(Align.left);
        layout();

        if(!item.applicable(guardian)) {
            setTouchable(Touchable.disabled);
            setColor(.6f,.6f,.6f,1f);
        }
    }

    @Override
    public void receive(Signal<Guardian> signal, Guardian guardian) {
        switch(item.getCategory()) {
            case EQUIPMENT:
                augmentButtonEquipment(guardian, (Equipment) item);
                break;
            default:
                augmentButtonMedicine(guardian, item);
                break;
        }
    }
}
