package org.limbusdev.monsterworld.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import org.limbusdev.monsterworld.MonsterWorld;
import org.limbusdev.monsterworld.utils.GlobalSettings;

/**
 * Created by georg on 03.12.15.
 */
public class BattleHUD {
    /* ............................................................................ ATTRIBUTES .. */
    private Skin skin;
    public Stage stage;

    private final TextButton fightButton;
    private final TextButton fleeButton;
    private final TextButton att1Button, att2Button, att3Button, att4Button, att5Button, att6Button;
    private final TextButton battleMenuButton;
    private final Label infoLabel;
    private final Label monster1Label;
    private final Label monster2Label;
    private final ProgressBar HPopponent1, HPopponent2, MPopponent1, MPopponent2;

    private final OutdoorGameWorldScreen gameScreen;
    private final MonsterWorld game;
    /* ........................................................................... CONSTRUCTOR .. */
    public BattleHUD(final MonsterWorld game, final OutdoorGameWorldScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
        // Scene2D
        FitViewport fit = new FitViewport(
                GlobalSettings.RESOLUTION_X, GlobalSettings.RESOLUTION_Y);

        this.stage = new Stage(fit);
        this.skin = new Skin(Gdx.files.internal("scene2d/uiskin.json"));

        // Buttons .................................................................................
        fightButton = new TextButton("Fight", skin, "default");
        fightButton.setWidth(128f);
        fightButton.setHeight(64f);
        fightButton.setPosition(100, 8);

        fightButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fightButton.setVisible(false);
                infoLabel.setVisible(false);
                att1Button.setVisible(true);
                att2Button.setVisible(true);
                att3Button.setVisible(true);
                att4Button.setVisible(true);
                att5Button.setVisible(true);
                att6Button.setVisible(true);
                battleMenuButton.setVisible(true);
            }
        });

        fleeButton = new TextButton("Flee", skin, "default");
        fleeButton.setWidth(128f);
        fleeButton.setHeight(64f);
        fleeButton.setPosition(230, 8);

        fleeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(gameScreen);
            }
        });


        att1Button = new TextButton("Att1", skin, "default");
        att1Button.setWidth(128f);
        att1Button.setHeight(64f);
        att1Button.setPosition(100, 8);
        att1Button.setVisible(false);

        att1Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Attack 1");
            }
        });

        att2Button = new TextButton("Att2", skin, "default");
        att2Button.setWidth(128f);
        att2Button.setHeight(64f);
        att2Button.setPosition(230, 8);
        att2Button.setVisible(false);

        att2Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Attack 2");
            }
        });

        att3Button = new TextButton("Att3", skin, "default");
        att3Button.setWidth(128f);
        att3Button.setHeight(64f);
        att3Button.setPosition(360, 8);
        att3Button.setVisible(false);

        att3Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Attack 3");
            }
        });

        att4Button = new TextButton("Att4", skin, "default");
        att4Button.setWidth(128f);
        att4Button.setHeight(64f);
        att4Button.setPosition(100, 74);
        att4Button.setVisible(false);

        att4Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Attack 4");
            }
        });

        att5Button = new TextButton("Att5", skin, "default");
        att5Button.setWidth(128f);
        att5Button.setHeight(64f);
        att5Button.setPosition(230, 74);
        att5Button.setVisible(false);

        att5Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Attack 5");
            }
        });

        att6Button = new TextButton("Att6", skin, "default");
        att6Button.setWidth(128f);
        att6Button.setHeight(64f);
        att6Button.setPosition(360, 74);
        att6Button.setVisible(false);

        att6Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Attack 6");
            }
        });

        battleMenuButton = new TextButton("Back", skin, "default");
        battleMenuButton.setWidth(128f);
        battleMenuButton.setHeight(64f);
        battleMenuButton.setPosition(700 - 128, 74);
        battleMenuButton.setVisible(false);

        battleMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                att1Button.setVisible(false);
                att2Button.setVisible(false);
                att3Button.setVisible(false);
                att4Button.setVisible(false);
                att5Button.setVisible(false);
                att6Button.setVisible(false);
                fleeButton.setVisible(true);
                fightButton.setVisible(true);

            }
        });


        // .................................................................................. LABELS

        infoLabel = new Label("You came across a monster!", skin, "default");
        infoLabel.setHeight(64);
        infoLabel.setWidth(600);
        infoLabel.setWrap(true);
        infoLabel.setPosition(GlobalSettings.RESOLUTION_X / 2 - 300f, 74);
        infoLabel.setVisible(true);

        monster1Label = new Label("Me", skin, "default");
        monster1Label.setWidth(128);
        monster1Label.setHeight(32);
        monster1Label.setPosition(100, GlobalSettings.RESOLUTION_Y - 72);
        monster1Label.setVisible(true);

        monster2Label = new Label("Enemy", skin, "default");
        monster2Label.setWidth(128);
        monster2Label.setHeight(32);
        monster2Label.setPosition(700 - 256, GlobalSettings.RESOLUTION_Y - 72);
        monster2Label.setVisible(true);
        HPopponent1 = new ProgressBar(0, 100, 1, false, skin);
        HPopponent2 = new ProgressBar(0, 100, 1, false, skin);
        MPopponent1 = new ProgressBar(0, 100, 1, false, skin);
        MPopponent2 = new ProgressBar(0, 100, 1, false, skin);

        HPopponent1.setPosition(228, GlobalSettings.RESOLUTION_Y - 60);
        HPopponent1.setWidth(128);
        HPopponent1.setVisible(true);
        HPopponent1.setValue(100);
        MPopponent1.setPosition(228, GlobalSettings.RESOLUTION_Y - 72);
        MPopponent1.setWidth(100);
        MPopponent1.setVisible(true);
        MPopponent1.setValue(100);

        HPopponent2.setPosition(700-128, GlobalSettings.RESOLUTION_Y - 60);
        HPopponent2.setWidth(128);
        HPopponent2.setVisible(true);
        HPopponent2.setValue(100);
        MPopponent2.setPosition(700-128, GlobalSettings.RESOLUTION_Y - 72);
        MPopponent2.setWidth(100);
        MPopponent2.setVisible(true);
        MPopponent2.setValue(100);

        // Buttons ............................................................................. END

        stage.addActor(fightButton);
        stage.addActor(fleeButton);
        stage.addActor(infoLabel);
        stage.addActor(monster1Label);
        stage.addActor(monster2Label);
        stage.addActor(HPopponent1);
        stage.addActor(MPopponent1);
        stage.addActor(HPopponent2);
        stage.addActor(MPopponent2);
        stage.addActor(att1Button);
        stage.addActor(att2Button);
        stage.addActor(att3Button);
        stage.addActor(att4Button);
        stage.addActor(att5Button);
        stage.addActor(att6Button);
        stage.addActor(battleMenuButton);
    }
    /* ............................................................................... METHODS .. */
    public void draw() {
        this.stage.draw();
    }
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
