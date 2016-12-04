package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.fwmengine.battle.ui.AHUD;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Created by georg on 24.11.16.
 */

public class InfoLabelWidget extends BattleWidget {

    private Image infoBGImg;
    public Label infoLabel;
    private String wholeText;
    private String currentText;

    public InfoLabelWidget(AHUD hud, Skin skin) {
        super(hud);

        infoBGImg = new Image(skin.getDrawable("label"));
        infoBGImg.setSize(372*GS.zoom,62*GS.zoom);
        infoBGImg.setPosition(GS.RES_X / 2, GS.zoom *2, Align.bottom);

        Label.LabelStyle labs = new Label.LabelStyle();
        labs.font = skin.getFont("default-font");
        infoLabel = new Label("", labs);
        infoLabel.setHeight(GS.ROW*8);
        infoLabel.setWidth(GS.COL*36);
        infoLabel.setWrap(true);
        infoLabel.setPosition(GS.RES_X/2, GS.ROW*4, Align.bottom);


        addActor(infoBGImg);
        addActor(infoLabel);
    }

    public void animateTextAppearance() {
        currentText = "";
        addAction(Actions.repeat(
            wholeText.length(),
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
        clearActions();
    }

}
