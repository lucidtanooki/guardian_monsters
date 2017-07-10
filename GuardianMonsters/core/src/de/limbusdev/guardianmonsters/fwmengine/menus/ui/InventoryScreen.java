package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.limbusdev.guardianmonsters.fwmengine.menus.ui.items.KeyItemsSubMenu;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.widgets.MainToolBar;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.widgets.TiledImage;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.model.items.Inventory;
import de.limbusdev.guardianmonsters.Constant;

/**
 * Inventory Screen, holds Team view, Ability Board, Item View, Encyclopedia
 * Copyright Georg Eckert
 */
public class InventoryScreen implements Screen, MainToolBar.Callbacks
{

    private static final String BG_TILE = "bg-pattern-3";

    private Stage stage;
    private Skin skin;
    private ArrayMap<String,AInventorySubMenu> views;

    public InventoryScreen(TeamComponent team, Inventory inventory) {
        FitViewport fit = new FitViewport(Constant.WIDTH, Constant.HEIGHT);
        this.stage = new Stage(fit);
        this.skin = Services.getUI().getInventorySkin();

        views = new ArrayMap<>();

        tileBackground();
        assembleToolbar();

        views.put("team",       new TeamSubMenu(skin, team));
        views.put("items",      new ItemsSubMenu(skin, inventory, team.team));
        views.put("ability",    new AbilityGraphSubMenu(skin, team.team));
        views.put("key",        new KeyItemsSubMenu(skin, inventory));
        views.put("abilityChoice", new AbilityChoiceSubMenu(skin, team.team));
        views.put("encyclo",    new EncycloSubMenu(skin));

        stage.addActor(views.get("team"));
    }


    /**
     * Tiles the complete Background with a specific 16x16 image
     */
    private void tileBackground() {
        TiledImage bg = new TiledImage(skin.getDrawable(BG_TILE), 27, 13);
        bg.setPosition(0, -4, Align.bottomLeft);
        stage.addActor(bg);
    }

    // .............................................................................. SCREEN METHODS

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta)
    {
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
        // TODO
    }

    @Override
    public void resume() {
        // TODO
    }

    @Override
    public void hide() {
        // TODO
    }

    @Override
    public void dispose() {
        stage.dispose();
    }


    // ...................................................................... MAINTOOLBAR CONTROLLER

    private void assembleToolbar() {

        MainToolBar toolBar = new MainToolBar(skin, this);
        toolBar.setPosition(0, Constant.HEIGHT,Align.topLeft);

        stage.addActor(toolBar);
    }

    private void removeSubMenus() {
        for(Actor a : views.values()) a.remove();
    }

    @Override
    public void onTeamButton() {
        removeSubMenus();
        views.get("team").refresh();
        stage.addActor(views.get("team"));
    }

    @Override
    public void onItemsButton() {
        removeSubMenus();
        stage.addActor(views.get("items"));
    }

    @Override
    public void onAbilityButton() {
        removeSubMenus();
        stage.addActor(views.get("ability"));
    }

    @Override
    public void onKeyButton() {
        removeSubMenus();
        stage.addActor(views.get("key"));
    }

    @Override
    public void onAbilityChoiceButton() {
        removeSubMenus();
        views.get("abilityChoice").refresh();
        stage.addActor(views.get("abilityChoice"));
    }

    @Override
    public void onEncycloButton() {
        removeSubMenus();
        stage.addActor(views.get("encyclo"));
    }

    @Override
    public void onExitButton() {
        removeSubMenus();
    }
}
