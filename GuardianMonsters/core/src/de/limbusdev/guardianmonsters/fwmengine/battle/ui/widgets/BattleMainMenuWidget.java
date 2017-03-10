package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.limbusdev.guardianmonsters.enums.Element;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.AHUD;
import de.limbusdev.guardianmonsters.utils.Constant;

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
    public BattleMainMenuWidget(AHUD hud, Skin skin, CallbackHandler callbackHandler) {
        super(hud);
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

        setCallbackHandler(callbackHandler);

    }



    public void setCallbackHandler(final CallbackHandler callbackHandler) {
        runButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    callbackHandler.onRunButton();
                }
            }
        );

        swordButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    callbackHandler.onSwordButton();
                }
            }
        );
    }


    // INNER INTERFACE
    public interface CallbackHandler {
        void onRunButton();
        void onSwordButton();
    }

}
