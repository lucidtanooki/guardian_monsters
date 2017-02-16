package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Inventory Screen, holds Team view, Character View, Item View, Encyclopedia
 * Copyright Georg Eckert
 */
public class InventoryScreen implements Screen {

    private Stage stage;
    private Skin skin;
    private Group toolBar;
    private ArrayMap<String,Group> views;
    private ArrayMap<Integer,Monster> team;

    public InventoryScreen(TeamComponent team) {
        this.team = team.monsters;

        FitViewport fit = new FitViewport(GS.WIDTH, GS.HEIGHT);
        this.stage = new Stage(fit);
        this.skin = Services.getUI().getInventorySkin();

        views = new ArrayMap<>();

        assembleToolbar();

        views.put("team", new TeamSubMenu(skin, team.monsters));

        stage.addActor(views.get("team"));
    }

    // ..................................................................................... TOOLBAR
    private void assembleToolbar() {
        toolBar = new Group();
        toolBar.setPosition(0,GS.HEIGHT-36,Align.bottomLeft);

        // ...................................................................................... BG
        Image bg = new Image(skin.getDrawable("toolBar-bg"));
        bg.setWidth(GS.WIDTH);
        bg.setPosition(0,0,Align.bottomLeft);
        toolBar.addActor(bg);

        // .................................................................................... TEAM
        ImageButton team = new ImageButton(skin, "b-toolbar-team");
        team.setPosition(2,4, Align.bottomLeft);
        team.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO
            }
        });
        toolBar.addActor(team);

        // ................................................................................... ITEMS
        ImageButton items = new ImageButton(skin, "b-toolbar-items");
        items.setPosition((64+4)*1+2,4, Align.bottomLeft);
        items.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO
            }
        });
        toolBar.addActor(items);

        // ................................................................................... EQUIP
        ImageButton equip = new ImageButton(skin, "b-toolbar-equip");
        equip.setPosition((64+4)*2+2,4, Align.bottomLeft);
        equip.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO
            }
        });
        toolBar.addActor(equip);

        // ................................................................................. ABILITY
        ImageButton ability = new ImageButton(skin, "b-toolbar-ability");
        ability.setPosition((64+4)*3+2,4, Align.bottomLeft);
        ability.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO
            }
        });
        toolBar.addActor(ability);

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
        toolBar.addActor(exit);


        stage.addActor(toolBar);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.1f, .1f, .1f, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
        stage.act(delta);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
