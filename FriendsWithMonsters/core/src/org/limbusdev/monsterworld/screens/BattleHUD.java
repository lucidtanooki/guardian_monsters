package org.limbusdev.monsterworld.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sun.crypto.provider.HmacPKCS12PBESHA1;

import org.limbusdev.monsterworld.MonsterWorld;
import org.limbusdev.monsterworld.model.BattleFactory;
import org.limbusdev.monsterworld.model.Monster;
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
    private Monster monsterOpponent1, monsterOpponent2;
    private final ProgressBar HPopponent1, HPopponent2, MPopponent1, MPopponent2;
    private final ScrollPane attacksPane;
    private BattleFactory battleFactory;

    private final OutdoorGameWorldScreen gameScreen;
    private final MonsterWorld game;
    /* ........................................................................... CONSTRUCTOR .. */
    public BattleHUD(final MonsterWorld game, final OutdoorGameWorldScreen gameScreen,
                     int monID1, int monID2) {
        this.game = game;
        this.gameScreen = gameScreen;

        this.battleFactory = new BattleFactory();
        this.monsterOpponent1 = battleFactory.createMonster(monID1);
        this.monsterOpponent2 = battleFactory.createMonster(monID2);

        // Scene2D
        FitViewport fit = new FitViewport(
                GlobalSettings.RESOLUTION_X, GlobalSettings.RESOLUTION_Y);

        this.stage = new Stage(fit);
        this.skin = game.media.skin;

        // Buttons .................................................................................
        fightButton = new TextButton("Fight", skin, "default-green");
        fightButton.setWidth(128f);
        fightButton.setHeight(64f);
        fightButton.setPosition(700 - 260, 8);
        fightButton.getStyle().fontColor = Color.RED;

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
                fleeButton.setVisible(false);
                battleMenuButton.setVisible(true);
            }
        });

        fleeButton = new TextButton("Escape", skin, "default-red");
        fleeButton.setWidth(128f);
        fleeButton.setHeight(64f);
        fleeButton.setPosition(700 - 128, 8);

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
                monsterOpponent2.HP -= monsterOpponent1.attacks.get(0).damage;
                if (monsterOpponent2.HP < 0) monsterOpponent2.HP = 0;
                HPopponent2.setValue(100 * monsterOpponent2.HP / monsterOpponent2.HPfull);
                if (monsterOpponent2.HP == 0) game.setScreen(gameScreen);
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
                battleMenuButton.setVisible(false);
                fleeButton.setVisible(true);
                fightButton.setVisible(true);

            }
        });


        // .................................................................................. LABELS

        infoLabel = new Label("A monster attacks you!", skin, "default");
        infoLabel.setHeight(64);
        infoLabel.setWidth(600);
        infoLabel.setWrap(true);
        infoLabel.setPosition(GlobalSettings.RESOLUTION_X / 2 - 300f, 74);
        infoLabel.setVisible(true);

        monster1Label = new Label("Me", skin, "beige");
        monster1Label.setWidth(128);
        monster1Label.setHeight(32);
        monster1Label.setPosition(100, GlobalSettings.RESOLUTION_Y - 72);
        monster1Label.setVisible(true);

        monster2Label = new Label("Enemy", skin, "beige");
        monster2Label.setWidth(128);
        monster2Label.setHeight(32);
        monster2Label.setPosition(700 - 256, GlobalSettings.RESOLUTION_Y - 72);
        monster2Label.setVisible(true);
        ProgressBar.ProgressBarStyle HPbarStyle = new ProgressBar.ProgressBarStyle();
        HPbarStyle.background = skin.getDrawable("default-slider");
        HPbarStyle.knobBefore= skin.getDrawable("green-slider");
        ProgressBar.ProgressBarStyle MPbarStyle = new ProgressBar.ProgressBarStyle();
        MPbarStyle.background = skin.getDrawable("default-slider");
        MPbarStyle.knobBefore= skin.getDrawable("blue-slider");
        HPopponent1 = new ProgressBar(0, 100, 1, false, HPbarStyle);
        HPopponent2 = new ProgressBar(0, 100, 1, false, HPbarStyle);
        MPopponent1 = new ProgressBar(0, 100, 1, false, MPbarStyle);
        MPopponent2 = new ProgressBar(0, 100, 1, false, MPbarStyle);

        HPopponent1.setPosition(224, GlobalSettings.RESOLUTION_Y - 56);
        HPopponent1.setWidth(128);
        HPopponent1.setVisible(true);
        HPopponent1.setValue(100);
        MPopponent1.setPosition(224, GlobalSettings.RESOLUTION_Y - 68);
        MPopponent1.setWidth(100);
        MPopponent1.setVisible(true);
        MPopponent1.setValue(100);

        HPopponent2.setPosition(700 - 132, GlobalSettings.RESOLUTION_Y - 56);
        HPopponent2.setWidth(128);
        HPopponent2.setVisible(true);
        HPopponent2.setValue(100);
        MPopponent2.setPosition(700 - 132, GlobalSettings.RESOLUTION_Y - 68);
        MPopponent2.setWidth(100);
        MPopponent2.setVisible(true);
        MPopponent2.setValue(100);

        HPopponent1.setAnimateInterpolation(Interpolation.linear);
        HPopponent1.setAnimateDuration(1f);
        HPopponent2.setAnimateInterpolation(Interpolation.linear);
        HPopponent2.setAnimateDuration(1f);

        List attackList = new List(skin, "default");
        attackList.setHeight(200);
        attackList.setItems(monsterOpponent1.attacks);
        this.attacksPane = new ScrollPane(attackList, skin, "default");
        attacksPane.setHeight(32);
        attacksPane.setPosition(0,240);

        // Buttons ............................................................................. END

        stage.addActor(HPopponent1);
        stage.addActor(MPopponent1);
        stage.addActor(HPopponent2);
        stage.addActor(MPopponent2);
        stage.addActor(fightButton);
        stage.addActor(fleeButton);
        stage.addActor(infoLabel);
        stage.addActor(monster1Label);
        stage.addActor(monster2Label);
        stage.addActor(att1Button);
        stage.addActor(att2Button);
        stage.addActor(att3Button);
        stage.addActor(att4Button);
        stage.addActor(att5Button);
        stage.addActor(att6Button);
        stage.addActor(battleMenuButton);
//        stage.addActor(attacksPane);
//        stage.addActor(attackList);


    }
    /* ............................................................................... METHODS .. */
    public void init() {
        fightButton.setVisible(true);
        fleeButton.setVisible(true);
        att1Button.setVisible(false);
        att2Button.setVisible(false);
        att3Button.setVisible(false);
        att4Button.setVisible(false);
        att5Button.setVisible(false);
        att6Button.setVisible(false);
        battleMenuButton.setVisible(false);
        infoLabel.setVisible(true);
        monster1Label.setVisible(true);
        monster2Label.setVisible(true);
        HPopponent1.setVisible(true);
        MPopponent1.setVisible(true);
        HPopponent2.setVisible(true);
        MPopponent2.setVisible(true);
    }

    public void draw() {
        this.stage.draw();
    }

    public void update(float delta) {
        stage.act(delta);
    }
    
    /* ..................................................................... GETTERS & SETTERS .. */
    public void setOpponent2(int ID) {
        monsterOpponent2 = battleFactory.createMonster(ID);
    }
}
