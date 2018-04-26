package de.limbusdev.guardianmonsters.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.ui.Constant;

/**
 * Created by georg on 24.11.16.
 */

public class InfoLabelWidget extends de.limbusdev.guardianmonsters.battle.ui.widgets.BattleWidget
{

    private Image infoBGImg;
    public Label infoLabel;
    private String wholeText;
    private String currentText;

    public InfoLabelWidget(Skin skin) {
        super();

        infoBGImg = new Image(skin.getDrawable("label"));
        infoBGImg.setSize(372* Constant.zoom,62* Constant.zoom);
        infoBGImg.setPosition(Constant.RES_X / 2, Constant.zoom *2, Align.bottom);

        infoLabel = new Label("", skin, "default");
        infoLabel.setSize(200,58);
        infoLabel.setWrap(true);
        infoLabel.setPosition(Constant.RES_X/2, 3, Align.bottom);


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

    public void setWholeText(String wholeText)
    {
        this.wholeText = wholeText;
        clearActions();
    }
}
