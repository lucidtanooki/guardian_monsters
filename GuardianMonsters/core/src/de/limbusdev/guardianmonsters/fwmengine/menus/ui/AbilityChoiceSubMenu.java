package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.I18NBundle;

import de.limbusdev.guardianmonsters.data.BundleAssets;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.SevenButtonsWidget;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.abilities.AbilityDetailWidget;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.team.TeamMemberSwitcher;
import de.limbusdev.guardianmonsters.model.abilities.Ability;
import de.limbusdev.guardianmonsters.model.monsters.Monster;
import de.limbusdev.guardianmonsters.utils.Constant;

/**
 * AbilityChoiceSubMenu
 *
 * @author Georg Eckert 2017
 */

public class AbilityChoiceSubMenu extends AInventorySubMenu
    implements TeamMemberSwitcher.Controller, AbilityDetailWidget.Controller, SevenButtonsWidget.ClickListener {

    private TeamMemberSwitcher switcher;
    private VerticalGroup abilityMenu;
    private ArrayMap<Integer, Monster> team;
    private ButtonGroup<TextButton> abilityButtons;
    private AbilityDetailWidget details;
    private SevenButtonsWidget abilitySlotButtons;

    private Group abilitySlotChoice;

    public AbilityChoiceSubMenu(Skin skin, ArrayMap<Integer, Monster> team) {
        super(skin);
        this.team = team;

        switcher = new TeamMemberSwitcher(skin, team, this);
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

        details = new AbilityDetailWidget(skin, this);
        details.setPosition(Constant.WIDTH-2, 2, Align.bottomRight);
        addActor(details);

        abilitySlotChoice = new Group();
        abilitySlotChoice.setSize(Constant.WIDTH, Constant.HEIGHT);
        abilitySlotChoice.setPosition(0,0,Align.bottomLeft);
        Image overlay = new Image(skin.getDrawable("black-a80"));
        overlay.setSize(Constant.WIDTH, Constant.HEIGHT);
        overlay.setPosition(0,0,Align.bottomLeft);
        abilitySlotChoice.addActor(overlay);
        abilitySlotButtons = new SevenButtonsWidget(Services.getUI().getBattleSkin(), this, SevenButtonsWidget.ABILITY_ORDER);
        abilitySlotButtons.setPosition(0,32,Align.bottomLeft);
        abilitySlotChoice.addActor(abilitySlotButtons);
        ImageButton back = new ImageButton(skin, "button-back");
        back.setPosition(Constant.WIDTH-4,4,Align.bottomRight);
        back.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                abilitySlotChoice.remove();
            }
        });
        abilitySlotChoice.addActor(back);


        setUpAbilityMenu(team.get(0));

    }



    private void setUpAbilityMenu(Monster m) {
        abilityMenu.clear();
        abilityButtons.clear();
        I18NBundle translation = Services.getL18N().l18n(BundleAssets.ATTACKS);
        for(int key : m.abilityGraph.learntAbilities.keys()) {
            Ability ability = m.abilityGraph.learntAbilities.get(key);
            TextButton tb = new TextButton(translation.get(ability.name), getSkin(), "item-button-sandstone");
            tb.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showAbilityDetails();
                }
            });
            tb.setSize(140,24);
            abilityMenu.addActor(tb);
            abilityButtons.add(tb);
            if(key == 0) tb.setChecked(true);
        }
    }

    @Override
    public void onChanged(int position) {
        refresh();
    }

    @Override
    public void refresh() {
        setUpAbilityMenu(team.get(switcher.getCurrentlyChosen()));
        showAbilityDetails();
    }

    private void showAbilityDetails() {
        details.initAbilityDetails(team.get(switcher.getCurrentlyChosen()).abilityGraph.learntAbilities.get(abilityButtons.getCheckedIndex()), true);
    }

    @Override
    public void onLearn(int nodeID) {
        addActor(abilitySlotChoice);
        refreshAbilitySlotButtons();
    }

    private void refreshAbilitySlotButtons() {
        for(int i=0; i<7; i++) {
            Ability ability = team.get(switcher.getCurrentlyChosen()).getActiveAbility(i);
            if(ability != null) {
                abilitySlotButtons.setButtonText(i, Services.getL18N().l18n(BundleAssets.ATTACKS).get(ability.name));
                abilitySlotButtons.setButtonStyle(i, Services.getUI().getBattleSkin(), "tb-attack-" + ability.element.toString().toLowerCase());
            } else {
                abilitySlotButtons.setButtonText(i, "");
                abilitySlotButtons.setButtonStyle(i, Services.getUI().getBattleSkin(), "tb-attack-none");
            }
        }
    }

    @Override
    public void onButtonNr(int nr) {
        team.get(switcher.getCurrentlyChosen()).setActiveAbility(nr, abilityButtons.getCheckedIndex());
        refreshAbilitySlotButtons();
    }
}
