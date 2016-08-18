package de.limbusdev.guardianmonsters.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Widget for displaying monster status in battle: HP, MP, EXP, Name, Level
 * HINT: Don't forget calling the init() method
 * Created by georg on 03.07.16.
 */
public class EndOfBattleWidget extends WidgetGroup implements BattleWidget {

    private Image labelBGImg;
    private Image bgImg;
    public  Label messageLabel;
    public ImageButton backButton;

    /**
     *
     * @param skin battle action UI skin
     */
    public EndOfBattleWidget(Skin skin) {
        this.setBounds(0,0,0,0);

        labelBGImg = new Image(skin.getDrawable("b-long-up"));
        labelBGImg.setPosition(GS.RES_X / 2, GS.ROW*7, Align.bottom);
        bgImg      = new Image(skin.getDrawable("eob-pane"));
        bgImg.setPosition(GS.RES_X / 2, 0, Align.bottom);

        addActor(labelBGImg);
        addActor(bgImg);

        Label.LabelStyle labs = new Label.LabelStyle();
        labs.font = skin.getFont("default-font");
        messageLabel = new Label("Game Over", labs);
        messageLabel.setHeight(64);
        messageLabel.setWidth(500);
        messageLabel.setWrap(true);
        messageLabel.setPosition(GS.RES_X / 2, GS.ROW*8, Align.bottom);
        addActor(messageLabel);

        // Change Screen
        backButton = new ImageButton(skin, "b-back-eob");
        backButton.setPosition(GS.RES_X, 0, Align.bottomRight);
        addActor(backButton);
    }


    @Override
    public void addFadeOutAction(float duration) {
        addAction(Actions.sequence(Actions.alpha(0, duration), Actions.visible(false)));
    }

    @Override
    public void addFadeInAction(float duration) {
        addAction(Actions.sequence(Actions.visible(true), Actions.alpha(1, duration)));
    }

    @Override
    public void addFadeOutAndRemoveAction(float duration) {
        addAction(Actions.sequence(Actions.alpha(0, duration), Actions.visible(false), Actions.run(new Runnable() {
            @Override
            public void run() {
                remove();
            }
        })));
    }

    @Override
    public void addFadeInAndAddToStageAction(float duration, Stage newParent) {
        newParent.addActor(this);
        addAction(Actions.sequence(Actions.visible(true), Actions.alpha(1, duration)));
    }
}
