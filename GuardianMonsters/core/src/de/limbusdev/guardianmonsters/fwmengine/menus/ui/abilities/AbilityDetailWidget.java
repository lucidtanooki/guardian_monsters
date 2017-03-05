package de.limbusdev.guardianmonsters.fwmengine.menus.ui.abilities;

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
import de.limbusdev.guardianmonsters.model.AbilityGraph;
import de.limbusdev.guardianmonsters.model.Equipment;
import de.limbusdev.guardianmonsters.model.Monster;


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

    public Controller callbacks;

    public interface Controller {
        /**
         * Is called, when the learn button of the detail widget is clicked.
         * @param nodeID    ID of the currently chosen graph node
         */
        void onLearn(int nodeID);
    }

    public AbilityDetailWidget(Skin skin, Controller handler) {
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


    // .............................................................................. INITIALIZATION

    private void setActorVisibility(AbilityGraph.NodeType type, boolean learnable, boolean enoughFreeLevels) {
        name.setVisible(false);
        damage.setVisible(false);
        abilityType.setVisible(false);
        learn.setVisible(false);

        switch(type) {
            case EMPTY:
                break;
            case METAMORPHOSIS:
            case EQUIPMENT:
                name.setVisible(true);
                break;
            default:    // Ability
                name.setVisible(true);
                damage.setVisible(true);
                abilityType.setVisible(true);
                break;
        }

        learn.setVisible(learnable && enoughFreeLevels);
    }

    public void init(Monster monster, int nodeID) {
        if(monster.abilityGraph.learnsSomethingAt(nodeID)) {
            if(monster.abilityGraph.learnsAbilityAt(nodeID)) {
                init(monster.abilityGraph.learnableAbilities.get(nodeID), nodeID, monster.abilityGraph, monster.getAbilityLevels() > 0);
            }
            if(monster.abilityGraph.learnsEquipmentAt(nodeID)) {
                init(monster.abilityGraph.learnableEquipment.get(nodeID), nodeID, monster.abilityGraph, monster.getAbilityLevels() > 0);
            }
            if(monster.abilityGraph.metamorphsAt(nodeID)) {
                initMetamorphosis(nodeID, monster.abilityGraph, monster.getAbilityLevels() > 0);
            }
        } else {
            initEmpty(nodeID,monster.abilityGraph, monster.getAbilityLevels() > 0);
        }
    }

    private void initMetamorphosis(int nodeID, AbilityGraph graph, boolean enoughFreeLvls) {
        this.nodeID = nodeID;

        setActorVisibility(AbilityGraph.NodeType.METAMORPHOSIS, graph.isNodeLearnable(nodeID), enoughFreeLvls);

        name.setText(Services.getL18N().l18n(BundleAssets.GENERAL).get("metamorphosis"));
    }

    /**
     * Node Type: ABILITY
     * @param ability
     * @param nodeID
     * @param graph
     */
    private void init(Ability ability, int nodeID, AbilityGraph graph, boolean enoughFreeLvls) {
        this.nodeID = nodeID;

        setActorVisibility(AbilityGraph.NodeType.ABILITY, graph.isNodeLearnable(nodeID), enoughFreeLvls);

        if(ability == null) {
            name.setText("Empty");
            damage.setText("0");
        } else {
            name.setText(Services.getL18N().l18n(BundleAssets.ATTACKS).get(ability.name));
            damage.setText(Integer.toString(ability.damage));
            Drawable drawable = ability.attackType == AttackType.PHYSICAL ? skin.getDrawable("stats-symbol-pstr") : skin.getDrawable("stats-symbol-mstr");
            abilityType.setDrawable(drawable);
        }
    }

    private void init(Equipment.EQUIPMENT_TYPE equipmentType, int nodeID, AbilityGraph graph, boolean enoughFreeLvls) {
        this.nodeID = nodeID;

        String equipment;
        I18NBundle bundle = Services.getL18N().l18n(BundleAssets.INVENTORY);

        setActorVisibility(AbilityGraph.NodeType.EQUIPMENT, graph.isNodeLearnable(nodeID), enoughFreeLvls);

        switch(equipmentType) {
            case HANDS: equipment = bundle.get("equip-hands"); break;
            case FEET: equipment = bundle.get("equip-feet"); break;
            case HEAD: equipment = bundle.get("equip-head"); break;
            default: equipment = bundle.get("equip-body"); break;
        }

        name.setText(bundle.format("ability-carry-equipment", equipment));
    }

    private void initEmpty(int nodeID, AbilityGraph graph, boolean enoughFreeLvls) {
        this.nodeID = nodeID;

        setActorVisibility(AbilityGraph.NodeType.EMPTY, graph.isNodeLearnable(nodeID), enoughFreeLvls);
    }
}