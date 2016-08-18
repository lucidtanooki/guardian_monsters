package de.limbusdev.guardianmonsters.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Widget for displaying monster status in battle: HP, MP, EXP, Name, Level
 * HINT: Don't forget calling the init() method
 * Created by georg on 03.07.16.
 */
public class BattleActionMenuWidget extends WidgetGroup implements BattleWidget {

    // Buttons
    public ImageButton backButton;
    public ImageButton blueButton;
    public TextButton  greenButton;
    public ImageButton greyLButton;
    public ImageButton greyRButton;

    private Image infoBGImg;
    public Label infoLabel;


    /**
     *
     * @param skin battle action UI skin
     */
    public BattleActionMenuWidget(Skin skin) {
        this.setBounds(0,0,GS.RES_X,GS.RES_Y/4);

        infoBGImg = new Image(skin.getDrawable("b-long-down"));
        infoBGImg.setPosition(GS.RES_X / 2, GS.ROW*7, Align.bottom);

        Label.LabelStyle labs = new Label.LabelStyle();
        labs.font = skin.getFont("default-font");
        infoLabel = new Label("", labs);
        infoLabel.setHeight(GS.ROW*6);
        infoLabel.setWidth(GS.COL*40);
        infoLabel.setWrap(true);
        infoLabel.setPosition(GS.RES_X/2, GS.ROW*7, Align.bottom);

        // Back to Menu Button
        backButton= new ImageButton(skin, "b-back");
        backButton.setPosition(GS.RES_X - GS.COL*5.5f, 0, Align.bottomRight);

        // Left Button
        greyLButton = new ImageButton(skin, "b-mouse-l");
        greyLButton.setPosition(GS.COL*10.5f, 0, Align.bottomLeft);

        // Right Button
        greyRButton = new ImageButton(skin, "b-mouse-r");
        greyRButton.setPosition(GS.RES_X - GS.COL*10.5f, 0, Align.bottomRight);

        // Green Button
        greenButton= new TextButton("greenButton", skin, "tb-attack");
        greenButton.setPosition(GS.RES_X/2, 0, Align.bottom);

        // Blue Button
        blueButton = new ImageButton(skin, "b-next");
        blueButton.setPosition(GS.COL*5.5f, 0, Align.bottomLeft);

        // Add to parent
        addActor(infoBGImg);
        addActor(infoLabel);
        addActor(backButton);
        addActor(blueButton);
        addActor(greenButton);
        addActor(greyLButton);
        addActor(greyRButton);
    }

    public void setGreenButtonDisabled(boolean disable) {
        if(disable) {
            greenButton.setDisabled(true);
            greenButton.addAction(Actions.alpha(0.5f));
        } else {
            greenButton.setDisabled(false);
            greenButton.addAction(Actions.alpha(1f));
        }
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
