package de.limbusdev.guardianmonsters.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Template HUD, does nothing but setting up a stage with a FitViewport
 * Created by georg on 07.09.16.
 */
public abstract class AHUD {

    protected Stage stage;
    protected Skin skin;

    public AHUD(Skin skin) {
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

}
