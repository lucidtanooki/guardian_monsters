package de.limbusdev.guardianmonsters.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.limbusdev.guardianmonsters.GuardianMonsters;
import de.limbusdev.guardianmonsters.enums.ButtonIDs;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Template HUD, does nothing but setting up a stage with a FitViewport
 * Created by georg on 07.09.16.
 */
public abstract class AHUD {

    protected Stage stage;
    protected Skin skin;
    protected GuardianMonsters game;

    public AHUD(GuardianMonsters game, Skin skin) {
        this.game = game;
        FitViewport fit = new FitViewport(GS.RES_X, GS.RES_Y);
        stage = new Stage(fit);
        this.skin = skin;
        stage.setDebugAll(GS.DEBUGGING_ON);
    }

    public void draw() {
        stage.draw();
    }

    public void update(float delta) {
        stage.act(delta);
    }

    protected abstract void reset();

    public abstract void show();

    public void goToPreviousScreen() {
        game.popScreen();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Provides a callback for all buttons, an inheriting class must implement the single callbacks
     * and use this method to choose them according to the ID
     * @param id    ID of the clicked button of a @link{BattleWidget}
     */
    public abstract void onButtonClicked(ButtonIDs id);

    public abstract void onButtonClicked(int id);

    public Stage getStage() {
        return stage;
    }
}
