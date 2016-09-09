package de.limbusdev.guardianmonsters.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.enums.ButtonIDs;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Widget for displaying monster status in battle: HP, MP, EXP, Name, Level
 * HINT: Don't forget calling the init() method
 * Created by georg on 03.07.16.
 */
public class BattleMainMenuWidget extends BattleWidget {

    // Buttons
    private ImageButton swordButton;
    private ImageButton bagButton;
    private ImageButton runButton;


    /**
     *
     * @param skin battle UI skin
     */
    public BattleMainMenuWidget(final AHUD hud, Skin skin) {
        super(hud);
        this.setBounds(0,0,GS.RES_X,GS.RES_Y/4);

        // Fight Button
        swordButton = new ImageButton(skin, "battle-fight");
        swordButton.setPosition(GS.RES_X/2, 0, Align.bottom);
        swordButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hud.onButtonClicked(ButtonIDs.TOP_LEVEL_SWORD);
            }
        });

        // Escape Button
        runButton = new ImageButton(skin, "battle-flee");
        runButton.setPosition(GS.RES_X - GS.ROW*20, 0, Align.bottomRight);
        runButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hud.onButtonClicked(ButtonIDs.TOP_LEVEL_RUN);
            }
        });

        // Bag Button
        bagButton = new ImageButton(skin, "battle-bag");
        bagButton.setPosition(GS.ROW*20, 0, Align.bottomLeft);
        bagButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hud.onButtonClicked(ButtonIDs.TOP_LEVEL_BAG);
            }
        });

        this.addActor(swordButton);
        this.addActor(runButton);
        this.addActor(bagButton);
    }

    /**
     * Adds a click listener to the sword button
     * @param cl
     */
    public void addSwordButtonListener(ClickListener cl) {
        swordButton.addListener(cl);
    }

    public void addRunButtonListener(ClickListener cl) {
        runButton.addListener(cl);
    }

    public void addBagButtonListener(ClickListener cl) {
        bagButton.addListener(cl);
    }

}
