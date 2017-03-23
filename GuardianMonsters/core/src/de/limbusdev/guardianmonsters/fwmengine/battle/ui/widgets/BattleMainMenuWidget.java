package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.limbusdev.guardianmonsters.Constant;

/**
 * Created by Georg Eckert 2016
 */
public class BattleMainMenuWidget extends BattleWidget {



    // Buttons
    private ImageButton swordButton;
    private ImageButton runButton;


    /**
     *
     * @param skin battle UI skin
     */
    public BattleMainMenuWidget(Skin skin, Callbacks callbacks) {
        super();
        this.setBounds(0,0, Constant.RES_X, 64);

        BattleHUDMenuButton emptyButton = new BattleHUDMenuButton(skin, BattleHUDMenuButton.EMPTY);
        addActor(emptyButton);

        // Fight Button
        swordButton = new BattleHUDMenuButton(skin, BattleHUDMenuButton.SWORD);

        // Escape Button
        runButton = new BattleHUDMenuButton(skin, BattleHUDMenuButton.ESCAPE);

        this.addActor(emptyButton);
        this.addActor(swordButton);
        this.addActor(runButton);

        setCallbackHandler(callbacks);

    }



    public void setCallbackHandler(final Callbacks callbacks) {
        runButton.addListener(
            new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    callbacks.onRunButton();
                }
            }
        );

        swordButton.addListener(
            new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    callbacks.onSwordButton();
                }
            }
        );
    }


    // INNER INTERFACE
    public interface Callbacks {
        void onRunButton();
        void onSwordButton();
    }

}
