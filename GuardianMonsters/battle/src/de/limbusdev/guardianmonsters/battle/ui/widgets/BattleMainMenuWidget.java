package de.limbusdev.guardianmonsters.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.limbusdev.guardianmonsters.ui.Constant;


/**
 * Created by Georg Eckert 2016
 */
public class BattleMainMenuWidget extends de.limbusdev.guardianmonsters.battle.ui.widgets.BattleWidget
{



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

        de.limbusdev.guardianmonsters.battle.ui.widgets.BattleHUDMenuButton emptyButton = new de.limbusdev.guardianmonsters.battle.ui.widgets.BattleHUDMenuButton(skin, de.limbusdev.guardianmonsters.battle.ui.widgets.BattleHUDMenuButton.EMPTY);
        addActor(emptyButton);

        // Fight Button
        swordButton = new de.limbusdev.guardianmonsters.battle.ui.widgets.BattleHUDMenuButton(skin, de.limbusdev.guardianmonsters.battle.ui.widgets.BattleHUDMenuButton.SWORD);

        // Escape Button
        runButton = new de.limbusdev.guardianmonsters.battle.ui.widgets.BattleHUDMenuButton(skin, de.limbusdev.guardianmonsters.battle.ui.widgets.BattleHUDMenuButton.ESCAPE);

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
