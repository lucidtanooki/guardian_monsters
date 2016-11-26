package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.enums.ButtonIDs;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.AHUD;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Widget for displaying monster status in battle: HP, MP, EXP, Name, Level
 * HINT: Don't forget calling the init() method
 * Created by georg on 03.07.16.
 */
public class BattleMainMenuWidget extends BattleWidget {

    // Buttons
    private ImageButton swordButton;
    private ImageButton runButton;


    /**
     *
     * @param skin battle UI skin
     */
    public BattleMainMenuWidget(final AHUD hud, Skin skin, CallbackHandler callbackHandler) {
        super(hud);
        this.setBounds(0,0,GS.RES_X,GS.RES_Y/4);

        Image emptyImg = new Image(skin.getDrawable("b-attack-none-down"));
        emptyImg.setSize(82*GS.zoom,32*GS.zoom);
        emptyImg.setPosition(GS.RES_X/2,32*GS.zoom+1*GS.zoom,Align.center);

        // Fight Button
        swordButton = new ImageButton(skin, "battle-fight");
        swordButton.setSize(82*GS.zoom,32*GS.zoom);
        swordButton.setPosition(GS.RES_X/2+71*GS.zoom,16*GS.zoom+1*GS.zoom,Align.center);
        swordButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hud.onButtonClicked(ButtonIDs.TOP_LEVEL_SWORD);
            }
        });

        // Escape Button
        runButton = new ImageButton(skin, "battle-flee");
        runButton.setSize(82*GS.zoom,32*GS.zoom);
        runButton.setPosition(GS.RES_X/2-71*GS.zoom,16*GS.zoom+1*GS.zoom,Align.center);
        runButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hud.onButtonClicked(ButtonIDs.TOP_LEVEL_RUN);
            }
        });

        addActor(emptyImg);
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
        public void onRunButton();
        public void onSwordButton();
    }

}
