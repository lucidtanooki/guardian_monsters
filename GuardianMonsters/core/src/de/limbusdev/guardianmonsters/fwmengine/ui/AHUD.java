package de.limbusdev.guardianmonsters.fwmengine.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.utils.Constant;

/**
 * Template HUD, does nothing but setting up a stage with a FitViewport
 * Created by georg on 07.09.16.
 */
public abstract class AHUD {

    protected Stage stage;
    protected Skin skin;
    private Array<Stage> stages;

    public AHUD(Skin skin) {
        stages = new Array<>();
        FitViewport fit = new FitViewport(Constant.WIDTH, Constant.HEIGHT);
        stage = new Stage(fit);
        this.skin = skin;
        stage.setDebugAll(Constant.DEBUGGING_ON);

        stages.insert(0,stage);
    }

    public void draw() {
        for(Stage s : stages) {
            s.draw();
        }
    }

    public void update(float delta) {
        for(Stage s : stages) {
            s.act(delta);
        }
    }

    protected abstract void reset();

    public abstract void show();

    public void goToPreviousScreen() {
        Services.getScreenManager().popScreen();
    }

    public void resize(int width, int height) {
        for(Stage s : stages) {
            s.getViewport().update(width, height);
        }
    }

    public Stage getStage() {
        return stage;
    }

    public void addAddtitionalStage(Stage stage) {
        stages.insert(0,stage);
    }
}
