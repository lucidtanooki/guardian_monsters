package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
import de.limbusdev.guardianmonsters.model.Inventory;
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

    public InventoryScreen(TeamComponent team, Inventory inventory) {
        this.team = team.monsters;

        FitViewport fit = new FitViewport(GS.WIDTH, GS.HEIGHT);
        this.stage = new Stage(fit);
        this.skin = Services.getUI().getInventorySkin();

        views = new ArrayMap<>();

        assembleToolbar();

        views.put("team", new TeamSubMenu(skin, team));
        views.put("items", new ItemsSubMenu(skin, inventory));

        stage.addActor(views.get("team"));
    }

    // ..................................................................................... TOOLBAR
    private void assembleToolbar() {
        MainToolBar.CallbackHandler callbacks = new MainToolBar.CallbackHandler() {

            private void removeSubMenus() {
                for(Actor a : views.values()) a.remove();
            }

            @Override
            public void onTeamButton() {
                removeSubMenus();
                stage.addActor(views.get("team"));
            }

            @Override
            public void onItemsButton() {
                removeSubMenus();
                stage.addActor(views.get("items"));
            }

            @Override
            public void onEquipButton() {
                removeSubMenus();
            }

            @Override
            public void onAbilityButton() {
                removeSubMenus();
            }

            @Override
            public void onExitButton() {
                removeSubMenus();
            }
        };

        MainToolBar toolBar = new MainToolBar(skin, callbacks);
        toolBar.setPosition(0,GS.HEIGHT-36,Align.bottomLeft);

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
