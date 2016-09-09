package de.limbusdev.guardianmonsters.ui;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.GuardianMonsters;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Created by georg on 09.09.16.
 */
public abstract class ABattleHUD extends AHUD {

    protected ArrayMap<String, BattleWidget> widgets;

    // Images
    private Image battleUIbg, blackCurtain;

    public ABattleHUD(GuardianMonsters game, Skin skin) {
        super(game, skin);
        widgets = new ArrayMap<String,BattleWidget>();

        // Battle UI Black transparent Background
        this.battleUIbg = new Image(skin.getDrawable("bg"));
        battleUIbg.setPosition(0, 0);
        battleUIbg.setSize(GS.RES_X, GS.ROW*14);

        // Black Curtain for fade-in and -out
        this.blackCurtain = new Image(skin.getDrawable("black"));
        this.blackCurtain.setSize(GS.RES_X,GS.RES_Y);
        this.blackCurtain.setPosition(0, 0);

        stage.addActor(battleUIbg);
        stage.addActor(blackCurtain);
    }


    @Override
    protected void reset() {
        blackCurtain.clearActions();
    }

    @Override
    public void show() {
        blackCurtain.addAction(Actions.sequence(Actions.fadeOut(1), Actions.visible(false)));
    }

    /**
     * Registers a widget to the HUD, but does not add it to the stage
     * @param key
     * @param bw
     */
    public void registerBattleWidget(String key, BattleWidget bw) {
        widgets.put(key,bw);
        if(!bw.hasParent()) stage.addActor(bw);
    }

    public BattleWidget getBattleWidget(String key) {
        return widgets.get(key);
    }

    public <T extends BattleWidget> T getBattleWidget(String key, Class<T> type) {
        return (T) getBattleWidget(key);
    }

    @Override
    public void goToPreviousScreen() {
        blackCurtain.addAction(Actions.sequence(Actions.visible(true), Actions.fadeIn(1),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    superGoToPreviousScreen();
                }
            })));
    }

    private void superGoToPreviousScreen() {
        super.goToPreviousScreen();
    }
}
