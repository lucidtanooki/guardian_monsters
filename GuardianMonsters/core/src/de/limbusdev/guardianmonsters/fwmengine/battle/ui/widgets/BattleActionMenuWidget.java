package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.enums.ButtonIDs;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.AHUD;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Widget for displaying monster status in battle: HP, MP, EXP, Name, Level
 * HINT: Don't forget calling the init() method
 * Created by georg on 03.07.16.
 */
public class BattleActionMenuWidget extends BattleWidget {

    // Buttons
    public ImageButton backButton;
    public ImageButton blueButton;
    public TextButton  greenButton;
    public ImageButton greyLButton;
    public ImageButton greyRButton;
    public ImageButton monsterButton;
    public ImageButton bagButton;
    public ImageButton extraButton;

    private Image infoBGImg;
    public Label infoLabel;




    /**
     *
     * @param skin battle action UI skin
     */
    public BattleActionMenuWidget(final AHUD hud, Skin skin) {
        super(hud);
        this.setBounds(0,0,GS.RES_X,GS.RES_Y/4);

        infoBGImg = new Image(skin.getDrawable("label"));
        infoBGImg.setSize(372*GS.zoom,62*GS.zoom);
        infoBGImg.setPosition(GS.RES_X / 2, GS.zoom *1, Align.bottom);

        Label.LabelStyle labs = new Label.LabelStyle();
        labs.font = skin.getFont("default-font");
        infoLabel = new Label("", labs);
        infoLabel.setHeight(GS.ROW*6);
        infoLabel.setWidth(GS.COL*40);
        infoLabel.setWrap(true);
        infoLabel.setPosition(GS.RES_X/2, GS.ROW*7, Align.bottom);

//        // Back to Menu Button
//        backButton= new ImageButton(skin, "b-back");
//        backButton.setPosition(GS.RES_X - GS.COL*5.5f, 0, Align.bottomRight);
//
//        // Left Button
//        greyLButton = new ImageButton(skin, "b-mouse-l");
//        greyLButton.setPosition(GS.COL*10.5f, 0, Align.bottomLeft);
//
//        // Right Button
//        greyRButton = new ImageButton(skin, "b-mouse-r");
//        greyRButton.setPosition(GS.RES_X - GS.COL*10.5f, 0, Align.bottomRight);
//
//        // Green Button
//        greenButton= new TextButton("greenButton", skin, "tb-attack");
//        greenButton.setPosition(GS.RES_X/2, 0, Align.bottom);
//
//        // Blue Button
//        blueButton = new ImageButton(skin, "b-next");
//        blueButton.setPosition(GS.COL*5.5f, 0, Align.bottomLeft);

        // Monster Button
        monsterButton = new ImageButton(skin, "b-attack-monsters");
        monsterButton.setSize(105*GS.zoom,32*GS.zoom);
        monsterButton.setPosition(6*GS.zoom, 64*GS.zoom+1*GS.zoom, Align.topLeft);

        // Extra Button
        extraButton = new ImageButton(skin, "b-attack-extra");
        extraButton.setSize(105*GS.zoom,32*GS.zoom);
        extraButton.setPosition(6*GS.zoom, 1*GS.zoom, Align.bottomLeft);

        // Back Button
        backButton = new ImageButton(skin, "b-attack-back");
        backButton.setSize(105*GS.zoom,32*GS.zoom);
        backButton.setPosition(GS.RES_X-6*GS.zoom, 1*GS.zoom, Align.bottomRight);

        // Bag Button
        bagButton = new ImageButton(skin, "b-attack-bag");
        bagButton.setSize(105*GS.zoom,32*GS.zoom);
        bagButton.setPosition(GS.RES_X-6*GS.zoom, 64*GS.zoom+1*GS.zoom, Align.topRight);

        // Add to parent
        //addActor(infoBGImg);
        //addActor(infoLabel);
        addActor(backButton);
//        addActor(blueButton);
//        addActor(greenButton);
//        addActor(greyLButton);
//        addActor(greyRButton);
        addActor(monsterButton);
        addActor(bagButton);
        addActor(extraButton);



        backButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    hud.onButtonClicked(ButtonIDs.ACTION_BACK);
                }
            }
        );

//        greyLButton.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                hud.onButtonClicked(ButtonIDs.ACTION_GREY_L);
//            }
//        });
//
//        greyRButton.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                hud.onButtonClicked(ButtonIDs.ACTION_GREY_R);
//            }
//        });
//
//        greenButton.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                hud.onButtonClicked(ButtonIDs.ACTION_ATTACK);
//            }
//        });
//
//        blueButton.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                hud.onButtonClicked(ButtonIDs.ACTION_BLUE);
//            }
//        });
    }

    public void setGreenButtonDisabled(boolean disable) {
//        if(disable) {
//            greenButton.setDisabled(true);
//            greenButton.addAction(Actions.alpha(0.5f));
//        } else {
//            greenButton.setDisabled(false);
//            greenButton.addAction(Actions.alpha(1f));
//        }
    }



    public void reset() {
//        setGreenButtonDisabled(false);
    }

    @Override
    public boolean fadeOutAndRemove() {
        infoBGImg.remove();
        infoLabel.remove();
        addActor(infoBGImg);
        addActor(infoLabel);
        return super.fadeOutAndRemove();
    }

    public void fadeOutExceptInfoLabel(Stage stage) {
        super.fadeOutAndRemove();
        stage.addActor(infoBGImg);
        stage.addActor(infoLabel);
    }

    @Override
    public void addToStageAndFadeIn(Stage newParent) {
        infoLabel.setText(Services.getL18N().l18n().get("batt_prepare"));
        infoBGImg.remove();
        infoLabel.remove();
        //addActor(infoBGImg);
        //addActor(infoLabel);
        super.addToStageAndFadeIn(newParent);
    }
}
