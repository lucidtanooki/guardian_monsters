package de.limbusdev.guardianmonsters.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by georg on 18.08.16.
 */
public interface BattleWidget {
    void addFadeOutAction(float duration);
    void addFadeInAction(float duration);
    void addFadeOutAndRemoveAction(float duration);
    void addFadeInAndAddToStageAction(float duration, Stage newParent);
}
