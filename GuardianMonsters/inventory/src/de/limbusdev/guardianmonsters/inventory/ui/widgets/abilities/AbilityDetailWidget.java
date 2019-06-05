package de.limbusdev.guardianmonsters.inventory.ui.widgets.abilities;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.abilities.IAbilityGraph;
import de.limbusdev.guardianmonsters.guardians.abilities.Node;
import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyPart;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.ui.widgets.SimpleClickListener;


/**
 * @author Georg Eckert 2017
 */

public class AbilityDetailWidget extends Container {

    private int nodeID;

    private Skin skin;
    private Group group;

    private Label name;
    private Label damage;
    private Label element;
    private Image abilityType;
    protected ImageButton learn;

    public Callbacks callbacks;

    public AbilityDetailWidget(Skin skin, Callbacks handler, String buttonStyle) {

        super();
        this.skin = skin;

        this.callbacks = handler;
        setBackground(skin.getDrawable("label-bg-sandstone"));
        setSize(170,64);

        group = new Group();
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

        learn = new ImageButton(skin, buttonStyle);
        learn.setPosition(160,0,Align.bottomRight);
        group.addActor(learn);

        // Callbacks
        learn.addListener(new SimpleClickListener(() -> callbacks.onLearn(nodeID)));
    }


    // .............................................................................. INITIALIZATION

    private void setLayout(Node.Type type, boolean showLearnButton) {
        // Hide Everything
        if(element != null) {
            element.remove();
        }
        name.setVisible(false);
        damage.setVisible(false);
        abilityType.setVisible(false);
        learn.setVisible(false);

        // Unhide needed actors
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

        learn.setVisible(showLearnButton);
    }

    public void init(AGuardian guardian, int nodeID, boolean forceShowLearnButton) {
        this.nodeID = nodeID;

        IAbilityGraph graph = guardian.getAbilityGraph();
        Node.Type type = graph.nodeTypeAt(nodeID);
        IndividualStatistics statistics = guardian.getIndividualStatistics();

        boolean showLearnButton = ((statistics.hasAbilityPoints() && graph.isNodeEnabled(nodeID)) || forceShowLearnButton);
        setLayout(type, showLearnButton);

        switch(type) {
            case ABILITY:
                initAbilityDetails(graph.getAbilityNodes().get(nodeID));
                break;
            case EQUIPMENT:
                initEquipmentDetails(graph.getEquipmentNodes().get(nodeID));
                break;
            case METAMORPHOSIS:
                initMetamorphosis();
                break;
            default:    // EMPTY NODE
                initEmpty();
                break;
        }
    }

    private void initMetamorphosis() {
        String text = Services.getL18N().General().get("metamorphosis");
        name.setText(text);
    }


    private void initAbilityDetails(Ability.aID abilityID) {

        Ability ability = GuardiansServiceLocator.INSTANCE.getAbilities().getAbility(abilityID);
        if(ability == null) {
            name.setText("Empty");
            damage.setText("0");
        } else {
            name.setText(Services.getL18N().Abilities().get(ability.getName()));
            damage.setText(Integer.toString(ability.getDamage()));

            String drawableID = ability.getDamageType() == Ability.DamageType.PHYSICAL ? "pstr" : "mstr";
            Drawable drawable = skin.getDrawable("stats-symbol-" + drawableID);

            abilityType.setDrawable(drawable);
            String elem = ability.getElement().toString().toLowerCase();
            String elemName = Services.getL18N().Elements().get("element_" + elem);
            elemName = elemName.length() < 7 ? elemName : elemName.substring(0,6);
            element = new Label(elemName, skin, "elem-" + elem);
            element.setPosition(124,0,Align.bottomRight);
            group.addActor(element);
        }
    }

    private void initEquipmentDetails(BodyPart equipmentType) {
        String equipment;
        I18NBundle bundle = Services.getL18N().Inventory();

        switch(equipmentType) {
            case HANDS: equipment = bundle.get("equip-hands"); break;
            case FEET:  equipment = bundle.get("equip-feet"); break;
            case HEAD:  equipment = bundle.get("equip-head"); break;
            default:    equipment = bundle.get("equip-body"); break;
        }

        name.setText(bundle.format("ability-carry-equipment", equipment));
    }

    private void initEmpty() {
        // DO NOTHING
    }



    // .................................................................... CLICK LISTENER INTERFACE
    public interface Callbacks {
        /**
         * Is called, when the learn button of the detail widget is clicked.
         * @param nodeID    ID of the currently chosen graph node
         */
        void onLearn(int nodeID);
    }
}
