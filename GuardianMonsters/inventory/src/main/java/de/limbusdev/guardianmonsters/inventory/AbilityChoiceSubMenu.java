package main.java.de.limbusdev.guardianmonsters.inventory;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Observable;
import java.util.Observer;

import de.limbusdev.guardianmonsters.guardians.Element;
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.abilities.AbilityGraph;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.ui.Constant;
import de.limbusdev.guardianmonsters.ui.widgets.Callback;
import de.limbusdev.guardianmonsters.ui.widgets.HoneyComb7ButtonsWidget;
import de.limbusdev.guardianmonsters.ui.widgets.SimpleClickListener;
import main.java.de.limbusdev.guardianmonsters.inventory.ui.widgets.abilities.AbilityDetailWidget;
import main.java.de.limbusdev.guardianmonsters.inventory.ui.widgets.team.TeamMemberSwitcher;

/**
 * AbilityChoiceSubMenu
 *
 * @author Georg Eckert 2017
 */

public class AbilityChoiceSubMenu extends AInventorySubMenu
    implements TeamMemberSwitcher.Callbacks, AbilityDetailWidget.Callbacks, Callback.ButtonID,
        Observer
{

    private TeamMemberSwitcher switcher;
    private ArrayMap<Integer, AGuardian> team;
    private ButtonGroup<TextButton> abilityButtons;
    private AbilityDetailWidget details;
    private HoneyComb7ButtonsWidget abilitySlotButtons;
    private ImageButton back;
    private int currentlyChosenAbility;
    private Table abilityButtonTable;

    private Group abilitySlotChoice;

    public AbilityChoiceSubMenu(Skin skin, ArrayMap<Integer, AGuardian> team) {

        super(skin);
        this.team = team;

        currentlyChosenAbility = 0;

        switcher = new TeamMemberSwitcher(skin, team, this);
        details = new AbilityDetailWidget(skin, this, "button-switch");
        abilitySlotButtons = new HoneyComb7ButtonsWidget(Services.getUI().getBattleSkin(), this, HoneyComb7ButtonsWidget.ABILITY_ORDER);
        back = new ImageButton(skin, "button-back");

        back.addListener(new SimpleClickListener(() -> {
                abilitySlotChoice.remove();
        }));

        layout(skin);
        init(team.get(0), 0);
    }

    /**
     * Initializes the menu with the data of the given monster
     * @param m
     */
    private void init(AGuardian m, int teamPosition) {

        team.get(switcher.getCurrentlyChosen()).deleteObserver(this);

        switcher.init(m, teamPosition);

        refreshAbilitySlotButtons();

        if(m.getAbilityGraph().getLearntAbilities().size != 0) {

            currentlyChosenAbility = m.getAbilityGraph().getLearntAbilities().firstKey();
            showAbilityDetails();
        }

        m.addObserver(this);
    }

    // ................................................................ TeamMemberSwitcher.Callbacks
    @Override
    public void onChanged(int position) {

        propagateSelectedGuardian(position);
    }

    @Override
    public void refresh() {

    }

    @Override
    protected void layout(Skin skin) {

        abilityButtonTable = new Table();
        abilityButtonTable.align(Align.topLeft);

        switcher.setPosition(2, 202, Align.topLeft);
        addActor(switcher);

        ScrollPane scrollPane = new ScrollPane(abilityButtonTable, skin);
        scrollPane.setSize(140,204);
        scrollPane.setPosition(100,0);
        scrollPane.setScrollBarPositions(false,true);
        addActor(scrollPane);

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

        System.out.println("REfresh Buttons");

        abilityButtons.clear();
        abilityButtonTable.clearChildren();

        I18NBundle translation = Services.getL18N().Abilities();

        AGuardian m = team.get(switcher.getCurrentlyChosen());

        for(final int key : m.getAbilityGraph().getLearntAbilities().keys()) {

            Ability.aID abilityID = m.getAbilityGraph().getLearntAbilities().get(key);

            if(abilityID != null) {

                Ability ability = GuardiansServiceLocator.getAbilities().getAbility(abilityID);
                TextButton tb = new TextButton(translation.get(ability.name), getSkin(), "item-button-sandstone");

                tb.addListener(new SimpleClickListener(() -> {

                    currentlyChosenAbility = key;
                    showAbilityDetails();
                }));

                abilityButtonTable.row().spaceBottom(1);
                abilityButtonTable.add(tb).width(140).height(32);
                abilityButtons.add(tb);

                if (key == 0) {
                    tb.setChecked(true);
                }
            }
        }
    }

    // ................................................................ SevenButtonsWidget.Callbacks
    @Override
    public void onClick(int buttonID) {

        AGuardian guardian = team.get(switcher.getCurrentlyChosen());
        guardian.getAbilityGraph().setActiveAbility(buttonID, currentlyChosenAbility);
        refreshAbilitySlotButtons();
    }

    @Override
    public void syncSelectedGuardian(int teamPosition) {

        init(team.get(teamPosition), teamPosition);
    }

    @Override
    public void update(Observable observable, Object o) {

        refresh();
        refreshAbilitySlotButtons();
    }
}
