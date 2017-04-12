package de.limbusdev.guardianmonsters.fwmengine.menus.ui.widgets;


import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.Constant;

/**
 * Created by Georg Eckert on 17.02.17.
 */

public class MainToolBar extends Group {

    private ButtonGroup<ImageButton> buttonGroup;
    private Controller callbacks;

    public MainToolBar(Skin skin, final Controller handler) {
        super();
        this.callbacks = handler;

        setSize(428,36);

        // ...................................................................................... BG
        Image bg = new Image(skin.getDrawable("toolBar-bg"));
        bg.setWidth(Constant.WIDTH);
        bg.setPosition(0,0,Align.bottomLeft);
        addActor(bg);

        // .................................................................................... TEAM
        ImageButton team = new ImageButton(skin, "b-toolbar-team");
        team.setPosition(2,4, Align.bottomLeft);
        team.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callbacks.onTeamButton();
            }
        });
        addActor(team);
        team.setChecked(true);

        // ................................................................................... ITEMS
        ImageButton items = new ImageButton(skin, "b-toolbar-items");
        items.setPosition((64+4)*1+2,4, Align.bottomLeft);
        items.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callbacks.onItemsButton();
            }
        });
        addActor(items);

        // ..................................................................................... KEY
        ImageButton key = new ImageButton(skin, "b-toolbar-key");
        key.setPosition((64+4)*2+2,4, Align.bottomLeft);
        key.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callbacks.onKeyButton();
            }
        });
        addActor(key);

        // ................................................................................. ABILITY
        ImageButton ability = new ImageButton(skin, "b-toolbar-ability");
        ability.setPosition((64+4)*3+2,4, Align.bottomLeft);
        ability.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callbacks.onAbilityButton();
            }
        });
        addActor(ability);

        // .......................................................................... ABILITY CHOICE
        ImageButton abilityChoice = new ImageButton(skin, "b-toolbar-ability-choice");
        abilityChoice.setPosition((64+4)*4+2,4, Align.bottomLeft);
        abilityChoice.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callbacks.onAbilityChoiceButton();
            }
        });
        addActor(abilityChoice);

        // ................................................................................. ENCYCLO
        ImageButton encyclo = new ImageButton(skin, "b-toolbar-encyclo");
        encyclo.setPosition(428-36,4, Align.bottomRight);
        encyclo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callbacks.onEncycloButton();
            }
        });
        addActor(encyclo);

        // .................................................................................... EXIT
        ImageButton exit = new ImageButton(skin, "b-toolbar-exit");
        exit.setPosition(428-2,4, Align.bottomRight);
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Exit Inventory
                Services.getScreenManager().popScreen();
            }
        });
        addActor(exit);

        buttonGroup = new ButtonGroup<>(team,items,ability,key,abilityChoice,encyclo);
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(1);

    }

    public interface Controller {
        void onTeamButton();
        void onItemsButton();
        void onAbilityButton();
        void onKeyButton();
        void onAbilityChoiceButton();
        void onEncycloButton();
        void onExitButton();
    }
}
