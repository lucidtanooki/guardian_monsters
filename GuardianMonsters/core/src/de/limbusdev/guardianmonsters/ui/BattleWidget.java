package de.limbusdev.guardianmonsters.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/**
 * Created by georg on 18.08.16.
 */
public abstract class BattleWidget extends WidgetGroup {

    private Runnable runnableRemove;
    private Runnable runnableAct;
    private Runnable runnableAdd;

    public BattleWidget () {
        super();

        // Callbacks
        runnableRemove = new Runnable() {
            @Override
            public void run() {
                superRemove();
            }
        };

        runnableAct = new Runnable() {
            @Override
            public void run() {
                act(1);
            }
        };
    }

    public void addFadeOutAction(float duration) {
        addAction(Actions.sequence(Actions.alpha(0, duration), Actions.visible(false)));
    }

    public void addFadeInAction(float duration) {
        addAction(Actions.sequence(Actions.visible(true), Actions.alpha(1, duration)));
    }


    public void addToStageAndFadeIn(Stage newParent) {
        clearActions();
        addToStage(newParent);
        addAction(Actions.sequence(Actions.alpha(0),Actions.fadeIn(.5f)));
    }

    public boolean fadeOutAndRemove () {
        if (getParent() != null) {
            addAction(Actions.sequence(Actions.alpha(1), Actions.alpha(0, .3f), Actions.run(runnableRemove)));
            return true;
        }
        else return false;
    }

    public void addToStage(Stage stage) {
        if(getParent() == null) stage.addActor(this);
    }
    private boolean superRemove() {
        return super.remove();
    }
}
