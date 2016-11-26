package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.enums.ButtonIDs;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.BattleHUD;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Widget for displaying monster status in battle: HP, MP, EXP, Name, Level
 * HINT: Don't forget calling the init() method
 * Created by georg on 03.07.16.
 */
public class EndOfBattleWidget extends BattleWidget {

    private Image labelBGImg;
    private Image bgImg;
    public  Label messageLabel;
    public  ImageButton backButton;

    /**
     *
     * @param skin battle action UI skin
     */
    public EndOfBattleWidget(final BattleHUD hud, Skin skin, CallbackHandler callbackHandler) {
        super(hud);
        this.setBounds(0,0,0,0);

        labelBGImg = new Image(skin.getDrawable("b-long-up"));
        labelBGImg.setPosition(GS.RES_X / 2, GS.ROW*7, Align.bottom);
        bgImg      = new Image(skin.getDrawable("eob-pane"));
        bgImg.setPosition(GS.RES_X / 2, 0, Align.bottom);

        addActor(labelBGImg);
        addActor(bgImg);

        Label.LabelStyle labs = new Label.LabelStyle();
        labs.font = skin.getFont("default-font");
        messageLabel = new Label("---", labs);
        messageLabel.setHeight(64);
        messageLabel.setWidth(500);
        messageLabel.setWrap(true);
        messageLabel.setPosition(GS.RES_X / 2, GS.ROW*8, Align.bottom);
        addActor(messageLabel);

        // Change Screen
        backButton = new ImageButton(skin, "b-back-eob");
        backButton.setPosition(GS.RES_X, 0, Align.bottomRight);
        addActor(backButton);

        setCallbackHandler(callbackHandler);
    }

    /**
     * true = hero won
     * false = opponent won
     * @param won
     */
    public void init(boolean won) {
        if(won) {
            messageLabel.setText("You won!");
        } else {
            messageLabel.setText("Game Over!");
        }
    }

    public void setCallbackHandler(final CallbackHandler callbackHandler) {
        backButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    callbackHandler.onBackButton();
                }
            }
        );
    }


    // INNER INTERFACE
    public interface CallbackHandler {
        public void onBackButton();
    }


}
