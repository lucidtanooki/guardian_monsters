package org.limbusdev.monsterworld.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sun.crypto.provider.HmacPKCS12PBESHA1;

import org.limbusdev.monsterworld.MonsterWorld;
import org.limbusdev.monsterworld.ecs.components.TeamComponent;
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

    private final TextButton fightButton, fleeButton, battleMenuButton;
    private final TextButton att1Button, att2Button, att3Button, att4Button, att5Button, att6Button;
    private final Array<Button> oppMonButtons;
    private final Label infoLabel;
    private final Array<Label> monsterLabels;
    private final Array<ProgressBar> HPbars;
    private final Array<ProgressBar> MPbars;
    private final ScrollPane attacksPane;
    private final Image indicator;
    private Array<Monster> team;
    private Array<Monster> opponentTeam;

    private final OutdoorGameWorldScreen gameScreen;
    private final MonsterWorld game;
    private int chosenTarget=0;
    /* ........................................................................... CONSTRUCTOR .. */
    public BattleHUD(final MonsterWorld game, final OutdoorGameWorldScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
        this.monsterLabels = new Array<Label>();
        this.HPbars = new Array<ProgressBar>();
        this.MPbars = new Array<ProgressBar>();
        this.oppMonButtons = new Array<Button>();

        this.opponentTeam = new Array<Monster>();
        for(int i=0;i<3;i++) this.opponentTeam.add(null);

        // Scene2D
        FitViewport fit = new FitViewport(
                GlobalSettings.RESOLUTION_X, GlobalSettings.RESOLUTION_Y);

        this.stage = new Stage(fit);
        this.skin = game.media.skin;

        // Images
        indicator = new Image(skin.getDrawable("indicator"));
        indicator.setVisible(true);
        indicator.setPosition(800-128,300, Align.center);
        stage.addActor(indicator);

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
                opponentTeam.get(0).HP -= team.get(0).attacks.get(0).damage;
                if (opponentTeam.get(0).HP < 0) opponentTeam.get(0).HP = 0;
                HPbars.get(3).setValue(100 * opponentTeam.get(0).HP / opponentTeam.get(0).HPfull);
                if (opponentTeam.get(0).HP == 0) game.setScreen(gameScreen);
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

        setUpUI();


        // .................................................................................. LABELS

        infoLabel = new Label("A monster attacks you!", skin, "default");
        infoLabel.setHeight(64);
        infoLabel.setWidth(600);
        infoLabel.setWrap(true);
        infoLabel.setPosition(GlobalSettings.RESOLUTION_X / 2 - 300f, 74);
        infoLabel.setVisible(true);

        List attackList = new List(skin, "default");
        attackList.setHeight(200);
//        attackList.setItems(opponentTeam.get(1).attacks);
        this.attacksPane = new ScrollPane(attackList, skin, "default");
        attacksPane.setHeight(32);
        attacksPane.setPosition(0, 240);

        // Buttons ............................................................................. END

        for(ProgressBar p : HPbars) stage.addActor(p);
        for(ProgressBar p : MPbars) stage.addActor(p);
        stage.addActor(fightButton);
        stage.addActor(fleeButton);
        stage.addActor(infoLabel);
        for(Label l : monsterLabels) stage.addActor(l);
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
    public void init(TeamComponent team, TeamComponent opponentTeam) {
        this.team = team.monsters;
        this.opponentTeam = opponentTeam.monsters;
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

        // Hero Team
        switch(team.monsters.size) {
            case 3:
                monsterLabels.get(2).setVisible(true);
                HPbars.get(2).setVisible(true);
                MPbars.get(2).setVisible(true);
            case 2:
                monsterLabels.get(0).setVisible(true);
                HPbars.get(0).setVisible(true);
                MPbars.get(0).setVisible(true);
            default:
                monsterLabels.get(1).setVisible(true);
                HPbars.get(1).setVisible(true);
                MPbars.get(1).setVisible(true);
                break;
        }

        // Opponent Team
        final Button monsterChooser1,monsterChooser2,monsterChooser3;
        switch(opponentTeam.monsters.size) {
            case 3:
                monsterChooser1 = new Button(skin, "transparent");
                monsterChooser1.setPosition(800-120-128,212,Align.center);
                monsterChooser1.setWidth(128);
                monsterChooser1.setHeight(128);
                monsterChooser1.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        chosenTarget = 2;
                        indicator.setPosition(800-120-64,340, Align.center);
                    }
                });
                oppMonButtons.add(monsterChooser1);
                monsterLabels.get(5).setVisible(true);
                HPbars.get(5).setVisible(true);
                MPbars.get(5).setVisible(true);
            case 2:
                monsterChooser2 = new Button(skin, "transparent");
                monsterChooser2.setPosition(800-8-128,140, Align.center);
                monsterChooser2.setWidth(128);
                monsterChooser2.setHeight(128);
                monsterChooser2.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        chosenTarget = 0;
                        indicator.setPosition(800-8-64,268, Align.center);
                    }
                });
                oppMonButtons.add(monsterChooser2);
                monsterLabels.get(3).setVisible(true);
                HPbars.get(3).setVisible(true);
                MPbars.get(3).setVisible(true);
            default:
                monsterChooser3 = new Button(skin, "transparent");
                monsterChooser3.setPosition(800-64-128,176, Align.center);
                monsterChooser3.setWidth(128);
                monsterChooser3.setHeight(128);
                monsterChooser3.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        chosenTarget = 1;
                        indicator.setPosition(800-128,304, Align.center);
                    }
                });
                oppMonButtons.add(monsterChooser3);
                monsterLabels.get(4).setVisible(true);
                HPbars.get(4).setVisible(true);
                MPbars.get(4).setVisible(true);
                break;
        }
        for(Button b : oppMonButtons) stage.addActor(b);
    }

    public void hide() {
        for(Label l : monsterLabels) l.setVisible(false);
        for(ProgressBar p : HPbars) p.setVisible(false);
        for(ProgressBar p : MPbars) p.setVisible(false);
        oppMonButtons.clear();
    }

    public void draw() {
        this.stage.draw();
    }

    public void update(float delta) {
        stage.act(delta);
    }

    public void setUpUI() {
        ProgressBar.ProgressBarStyle HPbarStyle = new ProgressBar.ProgressBarStyle();
        HPbarStyle.background = skin.getDrawable("default-slider");
        HPbarStyle.knobBefore= skin.getDrawable("green-slider");
        ProgressBar.ProgressBarStyle MPbarStyle = new ProgressBar.ProgressBarStyle();
        MPbarStyle.background = skin.getDrawable("default-slider");
        MPbarStyle.knobBefore= skin.getDrawable("blue-slider");

        ProgressBar mp, hp;

        // Hero Team ###############################################################################
        // Monster 1 ...............................................................................
        Label monster1Label = new Label("Me", skin, "beige");
        monster1Label.setWidth(128);
        monster1Label.setHeight(28);
        monster1Label.setPosition(16, GlobalSettings.RESOLUTION_Y - 100);
        monsterLabels.add(monster1Label);

        hp = new ProgressBar(0, 100, 1, false, HPbarStyle);
        mp = new ProgressBar(0, 100, 1, false, MPbarStyle);

        hp.setPosition(140, GlobalSettings.RESOLUTION_Y - 90);
        hp.setWidth(128); hp.setValue(100);
        mp.setPosition(140, GlobalSettings.RESOLUTION_Y - 100);
        mp.setWidth(100); mp.setValue(100);

        hp.setAnimateInterpolation(Interpolation.linear);
        hp.setAnimateDuration(1f);

        HPbars.add(hp); MPbars.add(mp);

        // Monster 2 ...............................................................................
        Label monster2Label = new Label("Me", skin, "beige");
        monster2Label.setWidth(128);
        monster2Label.setHeight(28);
        monster2Label.setPosition(48, GlobalSettings.RESOLUTION_Y - 68);
        monsterLabels.add(monster2Label);

        hp = new ProgressBar(0, 100, 1, false, HPbarStyle);
        mp = new ProgressBar(0, 100, 1, false, MPbarStyle);

        hp.setPosition(172, GlobalSettings.RESOLUTION_Y - 58);
        hp.setWidth(128); hp.setValue(100);
        mp.setPosition(172, GlobalSettings.RESOLUTION_Y - 68);
        mp.setWidth(100); mp.setValue(100);

        hp.setAnimateInterpolation(Interpolation.linear);
        hp.setAnimateDuration(1f);

        HPbars.add(hp); MPbars.add(mp);

        // Monster 3 ...............................................................................
        Label monster3Label = new Label("Me", skin, "beige");
        monster3Label.setWidth(128);
        monster3Label.setHeight(28);
        monster3Label.setPosition(80, GlobalSettings.RESOLUTION_Y - 36);
        monsterLabels.add(monster3Label);

        hp = new ProgressBar(0, 100, 1, false, HPbarStyle);
        mp = new ProgressBar(0, 100, 1, false, MPbarStyle);

        hp.setPosition(204, GlobalSettings.RESOLUTION_Y - 26);
        hp.setWidth(128); hp.setValue(100);
        mp.setPosition(204, GlobalSettings.RESOLUTION_Y - 36);
        mp.setWidth(100); mp.setValue(100);

        hp.setAnimateInterpolation(Interpolation.linear);
        hp.setAnimateDuration(1f);

        HPbars.add(hp); MPbars.add(mp);


        // Opponent Team ###########################################################################
        // Monster 4 ...............................................................................
        Label monster4Label = new Label("Enemy", skin, "beige-r");
        monster4Label.setWidth(128);
        monster4Label.setHeight(28);
        monster4Label.setPosition(800 - 144, GlobalSettings.RESOLUTION_Y - 100);
        monsterLabels.add(monster4Label);

        hp = new ProgressBar(0, 100, 1, false, HPbarStyle);
        mp = new ProgressBar(0, 100, 1, false, MPbarStyle);

        hp.setPosition(800 - 16 - 128 - 124, GlobalSettings.RESOLUTION_Y - 90);
        hp.setWidth(128); hp.setValue(100);
        mp.setPosition(800 - 16 - 128 - 96, GlobalSettings.RESOLUTION_Y - 100);
        mp.setWidth(100); mp.setValue(100);

        hp.setAnimateInterpolation(Interpolation.linear);
        hp.setAnimateDuration(1f);

        HPbars.add(hp); MPbars.add(mp);

        // Monster 5 ...............................................................................
        Label monster5Label = new Label("Enemy", skin, "beige-r");
        monster5Label.setWidth(128);
        monster5Label.setHeight(28);
        monster5Label.setPosition(800 - 176, GlobalSettings.RESOLUTION_Y - 68);
        monsterLabels.add(monster5Label);

        hp = new ProgressBar(0, 100, 1, false, HPbarStyle);
        mp = new ProgressBar(0, 100, 1, false, MPbarStyle);

        hp.setPosition(800 - 16 - 128 - 124 - 32, GlobalSettings.RESOLUTION_Y - 58);
        hp.setWidth(128); hp.setValue(100);
        mp.setPosition(800 - 16 - 128 - 96 - 32, GlobalSettings.RESOLUTION_Y - 68);
        mp.setWidth(100); mp.setValue(100);

        hp.setAnimateInterpolation(Interpolation.linear);
        hp.setAnimateDuration(1f);

        HPbars.add(hp); MPbars.add(mp);

        // Monster 6 ...............................................................................
        Label monster6Label = new Label("Enemy", skin, "beige-r");
        monster6Label.setWidth(128);
        monster6Label.setHeight(28);
        monster6Label.setPosition(800 - 208, GlobalSettings.RESOLUTION_Y - 36);
        monsterLabels.add(monster6Label);

        hp = new ProgressBar(0, 100, 1, false, HPbarStyle);
        mp = new ProgressBar(0, 100, 1, false, MPbarStyle);

        hp.setPosition(800 - 16 - 128 - 124 - 64, GlobalSettings.RESOLUTION_Y - 26);
        hp.setWidth(128); hp.setValue(100);
        mp.setPosition(800 - 16 - 128 - 96 - 64, GlobalSettings.RESOLUTION_Y - 36);
        mp.setWidth(100); mp.setValue(100);

        hp.setAnimateInterpolation(Interpolation.linear);
        hp.setAnimateDuration(1f);

        HPbars.add(hp); MPbars.add(mp);

        for(Label l : monsterLabels) l.setVisible(false);
        for(ProgressBar p : HPbars) p.setVisible(false);
        for(ProgressBar p : MPbars) p.setVisible(false);

    }
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
