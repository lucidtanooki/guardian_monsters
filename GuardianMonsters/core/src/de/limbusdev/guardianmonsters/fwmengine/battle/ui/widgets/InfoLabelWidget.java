package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.fwmengine.battle.ui.AHUD;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Created by georg on 24.11.16.
 */

public class InfoLabelWidget extends BattleWidget {

    private Image infoBGImg;
    public Label infoLabel;
    public ImageButton backButton;
    private String wholeText;
    private String currentText;

    public InfoLabelWidget(AHUD hud, Skin skin, CallbackHandler callbackHandler) {
        super(hud);

        // Back Button
        backButton = new ImageButton(skin, "b-attack-back");
        backButton.setSize(105* GS.zoom,32*GS.zoom);
        backButton.setPosition(GS.RES_X-6*GS.zoom, 1*GS.zoom, Align.bottomRight);

        infoBGImg = new Image(skin.getDrawable("label"));
        infoBGImg.setSize(372*GS.zoom,62*GS.zoom);
        infoBGImg.setPosition(GS.RES_X / 2, GS.zoom *1, Align.bottom);

        Label.LabelStyle labs = new Label.LabelStyle();
        labs.font = skin.getFont("default-font");
        infoLabel = new Label("", labs);
        infoLabel.setHeight(GS.ROW*8);
        infoLabel.setWidth(GS.COL*36);
        infoLabel.setWrap(true);
        infoLabel.setPosition(GS.RES_X/2, GS.ROW*4, Align.bottom);


        addActor(infoBGImg);
        addActor(infoLabel);
        addActor(backButton);

        setCallbackHandler(callbackHandler);
    }

    public void animateTextAppearance() {
        currentText = "";
        addAction(Actions.repeat(
            wholeText.length()-1,
            Actions.sequence(
                Actions.run(new Runnable() {
                @Override
                public void run() {
                    currentText += wholeText.substring(0,1);
                    wholeText = wholeText.substring(1,wholeText.length());
                    infoLabel.setText(currentText);
                }
            }),
                Actions.delay(.01f)
        )));
    }

    public void setWholeText(String wholeText) {
        this.wholeText = wholeText;
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
