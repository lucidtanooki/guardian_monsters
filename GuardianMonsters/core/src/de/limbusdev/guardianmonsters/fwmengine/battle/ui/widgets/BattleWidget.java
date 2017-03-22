package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/**
 * For all children of a BattleWidget, callbacks have to be added. When adding a widget to the
 * @link{AHUD}, the method onButtonClicked() can be used, in that case you can enter the button ID
 * into the Enumeration @link{ButtonIDs}
 * Created by georg on 18.08.16.
 */
public abstract class BattleWidget extends WidgetGroup {

    private Runnable runnableRemove;

    public BattleWidget () {
        super();

        // Controller
        runnableRemove = new Runnable() {
            @Override
            public void run() {
                superRemove();
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

    public void enable() {
        setColor(Color.WHITE);
        setTouchable(Touchable.enabled);
        for(Actor a : getChildren()) {
            enable(a);
        }
    }

    public void disable() {
        setColor(Color.GRAY);
        setTouchable(Touchable.disabled);
        for(Actor a : getChildren()) {
            disable(a);
        }
    }

    public void enable(Actor child) {
        child.setColor(Color.WHITE);
        child.setTouchable(Touchable.enabled);
    }

    public void disable(Actor child) {
        child.setColor(Color.GRAY);
        child.setTouchable(Touchable.disabled);
    }

}
