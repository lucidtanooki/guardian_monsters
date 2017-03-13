package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;

import de.limbusdev.guardianmonsters.data.BundleAssets;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.BattleHUD;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.utils.Constant;

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
    public EndOfBattleWidget(Skin skin, CallbackHandler callbackHandler) {
        super();
        this.setBounds(0,0,0,0);

        labelBGImg = new Image(skin.getDrawable("b-long-up"));
        labelBGImg.setPosition(Constant.RES_X / 2, Constant.ROW*7, Align.bottom);
        bgImg      = new Image(skin.getDrawable("eob-pane"));
        bgImg.setPosition(Constant.RES_X / 2, 0, Align.bottom);

        addActor(labelBGImg);
        addActor(bgImg);

        Label.LabelStyle labs = new Label.LabelStyle();
        labs.font = skin.getFont("default-font");
        messageLabel = new Label("---", labs);
        messageLabel.setHeight(64);
        messageLabel.setWidth(500);
        messageLabel.setWrap(true);
        messageLabel.setPosition(Constant.RES_X / 2, Constant.ROW*8, Align.bottom);
        addActor(messageLabel);

        // Change Screen
        backButton = new ImageButton(skin, "b-back-eob");
        backButton.setPosition(Constant.RES_X, 0, Align.bottomRight);
        addActor(backButton);

        setCallbackHandler(callbackHandler);
    }

    /**
     * true = hero won
     * false = opponent won
     * @param won
     */
    public void init(boolean won) {
        I18NBundle i18n = Services.getL18N().l18n(BundleAssets.BATTLE);
        String message = won ? "batt_game_over" : "batt_you_won";
        messageLabel.setText(i18n.get(message));
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
        void onBackButton();
    }


}
