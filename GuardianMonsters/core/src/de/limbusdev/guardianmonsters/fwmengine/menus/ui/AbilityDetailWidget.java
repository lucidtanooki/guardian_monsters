package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;


import de.limbusdev.guardianmonsters.data.BundleAssets;
import de.limbusdev.guardianmonsters.enums.AttackType;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Ability;
import de.limbusdev.guardianmonsters.model.Equipment;


/**
 * Created by Georg Eckert on 02.03.17.
 */

public class AbilityDetailWidget extends Container {

    public Label name;
    public Label damage;
    private Skin skin;
    private Image abilityType;
    private ImageButton learn;
    private int nodeID;

    public CallbackHandler callbacks;

    public interface CallbackHandler {
        void onLearn(int nodeID);
    }

    public AbilityDetailWidget(Skin skin, CallbackHandler handler) {
        super();
        this.skin = skin;
        this.callbacks = handler;
        setBackground(skin.getDrawable("label-bg-sandstone"));
        setSize(170,64);

        Group group = new Group();
        group.setSize(160,51);
        group.setPosition(5,6,Align.bottomLeft);
        setActor(group);

        name = new Label("Ability", skin, "default");
        name.setPosition(0,51,Align.topLeft);
        group.addActor(name);

        abilityType = new Image(skin.getDrawable("stats-symbol-pstr"));
        abilityType.setSize(16,16);
        abilityType.setPosition(0,31,Align.topLeft);
        group.addActor(abilityType);

        damage = new Label("0", skin, "default");
        damage.setPosition(18,30, Align.topLeft);
        group.addActor(damage);

        learn = new ImageButton(skin, "button-learn");
        learn.setPosition(160,0,Align.bottomRight);
        learn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callbacks.onLearn(nodeID);
            }
        });
        group.addActor(learn);

    }

    public void init(Ability ability, boolean active, boolean enabled, int nodeID) {
        this.nodeID = nodeID;

        damage.setVisible(true);
        abilityType.setVisible(true);
        if(ability == null) {
            name.setText("Empty");
            damage.setText("0");
            learn.setVisible(false);
        } else {
            learn.setVisible(!active && enabled);
            name.setText(Services.getL18N().l18n(BundleAssets.ATTACKS).get(ability.name));
            damage.setText(Integer.toString(ability.damage));
            Drawable drawable = ability.attackType == AttackType.PHYSICAL ? skin.getDrawable("stats-symbol-pstr") : skin.getDrawable("stats-symbol-mstr");
            abilityType.setDrawable(drawable);
        }
    }

    public void init(Equipment.EQUIPMENT_TYPE equipmentType, boolean active, boolean enabled, int nodeID) {
        this.nodeID = nodeID;

        String equipment;
        I18NBundle bundle = Services.getL18N().l18n(BundleAssets.INVENTORY);

        switch(equipmentType) {
            case HANDS: equipment = bundle.get("equip-hands"); break;
            case FEET: equipment = bundle.get("equip-feet"); break;
            case HEAD: equipment = bundle.get("equip-head"); break;
            default: equipment = bundle.get("equip-body"); break;
        }

        name.setText(bundle.format("ability-carry-equipment", equipment));
        damage.setVisible(false);
        abilityType.setVisible(false);
        learn.setVisible(!active && enabled);
    }

    public void initEmpty(boolean active, boolean enabled, int nodeID) {
        this.nodeID = nodeID;

        name.setVisible(false);
        damage.setVisible(false);
        abilityType.setVisible(false);
        learn.setVisible(!active && enabled);
    }
}
