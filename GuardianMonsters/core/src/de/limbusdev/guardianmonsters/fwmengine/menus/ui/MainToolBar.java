package de.limbusdev.guardianmonsters.fwmengine.menus.ui;


import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Created by Georg Eckert on 17.02.17.
 */

public class MainToolBar extends Group {

    private ButtonGroup<ImageButton> buttonGroup;
    private CallbackHandler callbacks;

    public MainToolBar(Skin skin, final CallbackHandler handler) {
        super();
        this.callbacks = handler;

        setSize(428,36);

        // ...................................................................................... BG
        Image bg = new Image(skin.getDrawable("toolBar-bg"));
        bg.setWidth(GS.WIDTH);
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

        // ................................................................................... EQUIP
        ImageButton equip = new ImageButton(skin, "b-toolbar-equip");
        equip.setPosition((64+4)*2+2,4, Align.bottomLeft);
        equip.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callbacks.onEquipButton();
            }
        });
        addActor(equip);

        // ..................................................................................... KEY
        ImageButton key = new ImageButton(skin, "b-toolbar-key");
        key.setPosition((64+4)*3+2,4, Align.bottomLeft);
        key.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callbacks.onKeyButton();
            }
        });
        addActor(key);

        // ................................................................................. ABILITY
        ImageButton ability = new ImageButton(skin, "b-toolbar-ability");
        ability.setPosition((64+4)*4+2,4, Align.bottomLeft);
        ability.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callbacks.onAbilityButton();
            }
        });
        addActor(ability);

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

        buttonGroup = new ButtonGroup<>(team,items,equip,ability,key);
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(1);

    }

    public interface CallbackHandler {
        void onTeamButton();
        void onItemsButton();
        void onEquipButton();
        void onAbilityButton();
        void onKeyButton();
        void onExitButton();
    }
}
