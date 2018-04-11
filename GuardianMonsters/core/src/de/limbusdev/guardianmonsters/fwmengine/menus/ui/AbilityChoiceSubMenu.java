package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Observable;
import java.util.Observer;

import de.limbusdev.guardianmonsters.Constant;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.SevenButtonsWidget;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.abilities.AbilityDetailWidget;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.team.TeamMemberSwitcher;
import de.limbusdev.guardianmonsters.guardians.Element;
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.abilities.AbilityGraph;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.services.Services;

/**
 * AbilityChoiceSubMenu
 *
 * @author Georg Eckert 2017
 */

public class AbilityChoiceSubMenu extends AInventorySubMenu
    implements TeamMemberSwitcher.Callbacks, AbilityDetailWidget.Callbacks, SevenButtonsWidget.Callbacks, Observer
{

    private TeamMemberSwitcher switcher;
    private VerticalGroup abilityMenu;
    private ArrayMap<Integer, AGuardian> team;
    private ButtonGroup<TextButton> abilityButtons;
    private AbilityDetailWidget details;
    private SevenButtonsWidget abilitySlotButtons;
    private ImageButton back;
    private int currentlyChosenAbility;

    private Group abilitySlotChoice;

    public AbilityChoiceSubMenu(Skin skin, ArrayMap<Integer, AGuardian> team) {
        super(skin);
        this.team = team;

        currentlyChosenAbility = 0;

        switcher = new TeamMemberSwitcher(skin, team, this);
        details = new AbilityDetailWidget(skin, this);
        abilitySlotButtons = new SevenButtonsWidget(Services.getUI().getBattleSkin(), this, SevenButtonsWidget.ABILITY_ORDER);
        back = new ImageButton(skin, "button-back");
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                abilitySlotChoice.remove();
            }
        });

        constructLayout();
        init(team.get(0));
    }

    /**
     * Initializes the menu with the data of the given monster
     * @param m
     */
    private void init(AGuardian m) {
        abilityMenu.clear();
        abilityButtons.clear();
        I18NBundle translation = Services.getL18N().Abilities();
        for(final int key : m.getAbilityGraph().getActiveAbilities().keys()) {

            Ability.aID abilityID = m.getAbilityGraph().getActiveAbilities().get(key);
            Ability ability = GuardiansServiceLocator.getAbilities().getAbility(abilityID);
            TextButton tb = new TextButton(translation.get(ability.name), getSkin(), "item-button-sandstone");
            tb.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    currentlyChosenAbility = key;
                    showAbilityDetails();
                }
            });
            tb.setSize(140,24);
            abilityMenu.addActor(tb);
            abilityButtons.add(tb);
            if(key == 0) tb.setChecked(true);
        }
        // TODO m.getAbilityGraph().addObserver(this);
    }

    // ................................................................ TeamMemberSwitcher.Callbacks
    @Override
    public void onChanged(int position) {
        refresh();
    }

    @Override
    public void refresh() {
        init(team.get(switcher.getCurrentlyChosen()));
        showAbilityDetails();
    }

    private void showAbilityDetails() {
        AGuardian guardian = team.get(switcher.getCurrentlyChosen());
        details.init(guardian, currentlyChosenAbility, true);
    }

    // ............................................................... AbilityDetailWidget.Callbacks
    @Override
    public void onLearn(int nodeID) {
        addActor(abilitySlotChoice);
        refreshAbilitySlotButtons();
    }

    private void refreshAbilitySlotButtons() {
        AGuardian guardian = team.get(switcher.getCurrentlyChosen());
        for(int i=0; i<7; i++) {

            Ability.aID abilityID = guardian.getAbilityGraph().getActiveAbility(i);
            Ability ability = GuardiansServiceLocator.getAbilities().getAbility(abilityID);
            if(ability != null) {
                abilitySlotButtons.setButtonText(i, ability);
                abilitySlotButtons.setButtonStyle(i, ability.element);
            } else {
                abilitySlotButtons.setButtonText(i, "");
                abilitySlotButtons.setButtonStyle(i, Element.NONE);
            }
        }
    }

    // ................................................................ SevenButtonsWidget.Callbacks
    @Override
    public void onButtonNr(int nr) {
        AGuardian guardian = team.get(switcher.getCurrentlyChosen());
        guardian.getAbilityGraph().setActiveAbility(nr, currentlyChosenAbility);
        refreshAbilitySlotButtons();
    }

    // ...................................................................... Listener<AbilityGraph>
//    @Override
    public void receive(Signal<AbilityGraph> signal, AbilityGraph object) {
        refresh();
        refreshAbilitySlotButtons();
    }


    /**
     * Do only layout construction here. Any functionality should be constructed somewhere else
     */
    private void constructLayout() {
        switcher.setPosition(2, 202, Align.topLeft);
        addActor(switcher);

        abilityMenu = new VerticalGroup();
        abilityMenu.setSize(140,200);
        abilityMenu.setPosition(100,2,Align.bottomLeft);
        abilityMenu.fill();
        addActor(abilityMenu);

        abilityButtons = new ButtonGroup<>();
        abilityButtons.setMaxCheckCount(1);
        abilityButtons.setMaxCheckCount(1);

        details.setPosition(Constant.WIDTH-2, 2, Align.bottomRight);
        addActor(details);

        abilitySlotChoice = new Group();
        abilitySlotChoice.setSize(Constant.WIDTH, Constant.HEIGHT);
        abilitySlotChoice.setPosition(0,0,Align.bottomLeft);
        Image overlay = new Image(getSkin().getDrawable("black-a80"));
        overlay.setSize(Constant.WIDTH, Constant.HEIGHT);
        overlay.setPosition(0,0,Align.bottomLeft);
        abilitySlotChoice.addActor(overlay);

        abilitySlotButtons.setPosition(0,32,Align.bottomLeft);
        abilitySlotChoice.addActor(abilitySlotButtons);

        back.setPosition(Constant.WIDTH-4,4,Align.bottomRight);
        abilitySlotChoice.addActor(back);
    }

    @Override
    public void update(Observable o, Object arg)
    {

    }
}
