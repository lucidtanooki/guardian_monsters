package de.limbusdev.guardianmonsters.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.limbusdev.guardianmonsters.ui.Constant;
import de.limbusdev.guardianmonsters.ui.widgets.Callback;
import de.limbusdev.guardianmonsters.ui.widgets.SimpleClickListener;


/**
 * Created by Georg Eckert 2016
 */
public class BattleMainMenuWidget extends BattleWidget
{
    // Buttons
    private ImageButton swordButton;
    private ImageButton runButton;

    /**
     *
     * @param skin battle UI skin
     */
    public BattleMainMenuWidget(Skin skin, Callback onSwordButton, Callback onRunButton)
    {
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

        setCallbackHandler(onSwordButton, onRunButton);

    }



    public void setCallbackHandler(Callback onSwordButton, Callback onRunButton)
    {
        swordButton.addListener(new SimpleClickListener(onSwordButton::onClick));
        runButton.addListener  (new SimpleClickListener(onRunButton::onClick));
    }
}
