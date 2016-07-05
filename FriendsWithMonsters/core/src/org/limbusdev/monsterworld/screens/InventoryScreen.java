package org.limbusdev.monsterworld.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import org.limbusdev.monsterworld.FriendsWithMonsters;
import org.limbusdev.monsterworld.ecs.components.TeamComponent;
import org.limbusdev.monsterworld.ui.MonsterStatusInventoryWidget;
import org.limbusdev.monsterworld.utils.GS;

/**
 * Inventory Screen, holds Team view, Character View, Item View, Encyclopedia
 * Copyright Georg Eckert
 */
public class InventoryScreen implements Screen {

    private Stage stage;
    private Table layout;
    private Skin skin;
    private HorizontalGroup toolBar;
    private Array<Table> views;

    public InventoryScreen(final FriendsWithMonsters game, TeamComponent team) {
        FitViewport fit = new FitViewport(GS.RES_X, GS.RES_Y);
        this.stage = new Stage(fit);
        this.skin = game.media.inventorySkin;
        this.views = new Array<Table>();
        MonsterStatusInventoryWidget msiw = new MonsterStatusInventoryWidget(skin, game.media);
        msiw.init(team);
        msiw.bottom().left();
        views.add(msiw);

        layout = new Table();
        layout.setFillParent(true);
        layout.top().left();
        toolBar = new HorizontalGroup();
        layout.add(toolBar);
        TextButton tb = new TextButton("Status", skin, "b-toolbar");
        toolBar.addActor(tb);
        tb = new TextButton("Items", skin, "b-toolbar");
        toolBar.addActor(tb);
        tb = new TextButton("Encylo", skin, "b-toolbar");
        toolBar.addActor(tb);
        ImageButton ib = new ImageButton(skin, "b-toolbar-exit");
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Exit Inventory
                game.popScreen();
            }
        });
        toolBar.addActor(ib.right());

        layout.row();

        layout.add(views.first());

        layout.validate();

        stage.addActor(layout);
        layout.setDebug(GS.DEBUGGING_ON);
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
