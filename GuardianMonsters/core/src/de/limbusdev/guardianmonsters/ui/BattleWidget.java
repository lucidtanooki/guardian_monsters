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
        addAction(Actions.alpha(0));
        act(1);
        addToStage(newParent);
        addAction(Actions.sequence(Actions.alpha(1,.3f)));
    }

    @Override
    public boolean remove () {
        if (getParent() != null) {
            addAction(Actions.sequence(Actions.alpha(0, .3f), Actions.run(runnableRemove),
                Actions.alpha(1), Actions.run(runnableAct)));
            return true;
        }
        else return false;
    }

    private void addToStage(Stage stage) {
        if(getParent() == null) stage.addActor(this);
    }
    private boolean superRemove() {
        return super.remove();
    }
}
