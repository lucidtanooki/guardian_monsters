package org.limbusdev.monsterworld.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
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
import org.limbusdev.monsterworld.geometry.IntVector2;
import org.limbusdev.monsterworld.model.AttackAction;
import org.limbusdev.monsterworld.model.BattleFactory;
import org.limbusdev.monsterworld.model.Monster;
import org.limbusdev.monsterworld.model.MonsterInformation;
import org.limbusdev.monsterworld.utils.GlobalSettings;

/**
 * Created by georg on 03.12.15.
 */
public class BattleHUD {
    /* ............................................................................ ATTRIBUTES .. */
    static class IndPos {
        private static IntVector2 OPPO_TOP = new IntVector2(616, 340);
        private static IntVector2 OPPO_MID = new IntVector2(672, 304);
        private static IntVector2 OPPO_BOT = new IntVector2(728, 268);
        private static IntVector2 HERO_TOP = new IntVector2(184, 340);
        private static IntVector2 HERO_MID = new IntVector2(128, 304);
        private static IntVector2 HERO_BOT = new IntVector2(74, 268);
    }

    static class BatPos {
        private static int HERO_MID = 0;    // Middle Position on the left
        private static int HERO_TOP = 2;
        private static int HERO_BOT = 1;
        private static int OPPO_MID = 3;    // Middle Position on the right
        private static int OPPO_TOP = 5;
        private static int OPPO_BOT = 4;
        private static int MID = 0;
        private static int TOP = 2;
        private static int BOT = 1;
    }

    private Skin skin;
    public Stage stage;
    private MonsterInformation monsterInformation;

    private final TextButton fightButton, fleeButton, battleMenuButton;
    private final TextButton att1Button;
    private final Array<Button> oppMonButtons;
    private final Label infoLabel;
    private final Array<Label> monsterLabels;
    private final Array<ProgressBar> HPbars;
    private final Array<ProgressBar> MPbars;
    private final ScrollPane attacksPane;
    private final Image indicatorOpp, indicatorHero;
    private Array<Monster> team;
    private Array<Monster> opponentTeam;
    private float elapsedTime=0;
    private Array<AttackAction> battleQueue;
    private Array<Long> waitingSince;
    private Array<Boolean> monsterReady;

    private final OutdoorGameWorldScreen gameScreen;
    private final MonsterWorld game;
    private int chosenTarget=BatPos.MID;
    private int chosenTeamMonster=BatPos.MID;


    // Time
    private long lastActionTime = 0;
    private boolean waiting = false;
    /* ........................................................................... CONSTRUCTOR .. */
    public BattleHUD(final MonsterWorld game, final OutdoorGameWorldScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
        this.monsterInformation = MonsterInformation.getInstance();
        this.monsterLabels = new Array<Label>();
        this.HPbars = new Array<ProgressBar>();
        this.MPbars = new Array<ProgressBar>();
        this.oppMonButtons = new Array<Button>();
        this.battleQueue = new Array<AttackAction>();
        this.waitingSince = new Array<Long>();
        for(int i=0;i<6;i++) waitingSince.add(new Long(0));

        this.opponentTeam = new Array<Monster>();

        this.monsterReady = new Array<Boolean>();
        for(int i=0;i<6;i++) monsterReady.add(new Boolean(true));

        // Scene2D
        FitViewport fit = new FitViewport(
                GlobalSettings.RESOLUTION_X, GlobalSettings.RESOLUTION_Y);

        this.stage = new Stage(fit);
        this.skin = game.media.skin;for(int i=0;i<6;i++) waitingSince.add(new Long(0));

        // Images
        indicatorOpp = new Image(skin.getDrawable("indicator"));
        indicatorOpp.setVisible(true);
        indicatorOpp.setPosition(IndPos.OPPO_MID.x, IndPos.OPPO_MID.y, Align.center);
        stage.addActor(indicatorOpp);

        indicatorHero = new Image(skin.getDrawable("indicator"));
        indicatorHero.setVisible(true);
        indicatorHero.setPosition(IndPos.HERO_MID.x, IndPos.HERO_MID.y, Align.center);
        stage.addActor(indicatorHero);

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
                System.out.println("Attack 1 by Team Member " + chosenTeamMonster + " at " +
                        chosenTarget);
                if(opponentTeam.get(chosenTarget).HP - team.get(chosenTeamMonster).attacks.get(0).damage < 0)
                    opponentTeam.get(chosenTarget).HP = 0;
                else opponentTeam.get(chosenTarget).HP -= team.get(chosenTeamMonster).attacks.get(0).damage;

                HPbars.get(chosenTarget+3).setValue(
                        100 * opponentTeam.get(chosenTarget).HP / opponentTeam.get(chosenTarget).HPfull);

                boolean allKO = true;
                switch(opponentTeam.size) {
                    case 3: allKO=(opponentTeam.get(BatPos.TOP).HP == 0);
                        System.out.println("OppMon 3: " + opponentTeam.get(2).HP
                                        + "/" + opponentTeam.get(2).HPfull + " KO: " + allKO);
                    case 2: allKO=(allKO == true && opponentTeam.get(1).HP == 0);
                        System.out.println("OppMon 2: " + opponentTeam.get(1).HP
                                + "/" + opponentTeam.get(1).HPfull + " KO: " + allKO);
                    default:allKO=(allKO == true && opponentTeam.get(0).HP == 0);
                        System.out.println("OppMon 1: " + opponentTeam.get(0).HP
                                + "/" + opponentTeam.get(0).HPfull + " KO: " + allKO);
                        break;
                }


                if(allKO) game.setScreen(gameScreen);
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
        battleMenuButton.setVisible(false);
        infoLabel.setVisible(true);

        // Hero Team
        switch(team.monsters.size) {
            case 3:
                monsterLabels.get(BatPos.HERO_TOP).setText(
                        monsterInformation.monsterNames.get(team.monsters.get(2).ID-1));
                monsterLabels.get(BatPos.HERO_TOP).setVisible(true);
                monsterLabels.get(BatPos.HERO_TOP).addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        chosenTeamMonster = BatPos.TOP;
                        indicatorHero.setPosition(IndPos.HERO_TOP.x, IndPos.HERO_TOP.y, Align
                                .center);
                    }
                });
                HPbars.get(BatPos.TOP).setVisible(true);
                MPbars.get(BatPos.TOP).setVisible(true);
            case 2:
                monsterLabels.get(BatPos.HERO_BOT).setText(
                        monsterInformation.monsterNames.get(team.monsters.get(1).ID-1));
                monsterLabels.get(BatPos.BOT).setVisible(true);
                monsterLabels.get(BatPos.HERO_BOT).addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        chosenTeamMonster = BatPos.BOT;
                        indicatorHero.setPosition(IndPos.HERO_BOT.x, IndPos.HERO_BOT.y, Align
                                .center);
                    }
                });
                HPbars.get(BatPos.BOT).setVisible(true);
                MPbars.get(BatPos.BOT).setVisible(true);
            default:
                monsterLabels.get(BatPos.HERO_MID).setText(
                        monsterInformation.monsterNames.get(team.monsters.get(0).ID-1));
                monsterLabels.get(BatPos.HERO_MID).setVisible(true);
                monsterLabels.get(BatPos.HERO_MID).addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        chosenTeamMonster = BatPos.MID;
                        indicatorHero.setPosition(IndPos.HERO_MID.x, IndPos.HERO_MID.y, Align
                                .center);
                    }
                });
                HPbars.get(BatPos.MID).setVisible(true);
                MPbars.get(BatPos.MID).setVisible(true);
                break;
        }

        // Opponent Team
        final Button monsterChooser1,monsterChooser2,monsterChooser3;
        switch(opponentTeam.monsters.size) {
            case 3:
                monsterLabels.get(BatPos.OPPO_TOP).setText(
                        monsterInformation.monsterNames.get(opponentTeam.monsters.get(2).ID-1));
                monsterLabels.get(BatPos.OPPO_TOP).setVisible(true);
                monsterLabels.get(BatPos.OPPO_TOP).addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        chosenTarget = BatPos.TOP;
                        indicatorOpp.setPosition(IndPos.OPPO_TOP.x, IndPos.OPPO_TOP.y, Align
                                .center);
                    }
                });
                HPbars.get(BatPos.OPPO_TOP).setVisible(true);
                MPbars.get(BatPos.OPPO_TOP).setVisible(true);
            case 2:
                monsterLabels.get(BatPos.OPPO_BOT).setText(
                        monsterInformation.monsterNames.get(opponentTeam.monsters.get(1).ID-1));
                monsterLabels.get(BatPos.OPPO_BOT).setVisible(true);
                monsterLabels.get(BatPos.OPPO_BOT).addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        chosenTarget = BatPos.BOT;
                        indicatorOpp.setPosition(IndPos.OPPO_BOT.x, IndPos.OPPO_BOT.y, Align
                                .center);
                    }
                });
                HPbars.get(BatPos.OPPO_BOT).setVisible(true);
                MPbars.get(BatPos.OPPO_BOT).setVisible(true);
            default:
                monsterLabels.get(BatPos.OPPO_MID).setText(
                        monsterInformation.monsterNames.get(opponentTeam.monsters.get(0).ID-1));
                monsterLabels.get(BatPos.OPPO_MID).setVisible(true);
                monsterLabels.get(BatPos.OPPO_MID).addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        chosenTarget = BatPos.MID;
                        indicatorOpp.setPosition(IndPos.OPPO_MID.x, IndPos.OPPO_MID.y, Align
                                .center);
                    }
                });
                HPbars.get(BatPos.OPPO_MID).setVisible(true);
                MPbars.get(BatPos.OPPO_MID).setVisible(true);
                break;
        }
        for(Button b : oppMonButtons) stage.addActor(b);
    }

    public void hide() {
        for(Label l : monsterLabels) l.setVisible(false);
        for(ProgressBar p : HPbars) p.setVisible(false);
        for(ProgressBar p : MPbars) p.setVisible(false);
        oppMonButtons.clear();
        this.chosenTarget = BatPos.MID;
        this.chosenTeamMonster = BatPos.MID;
    }

    public void draw() {
        this.stage.draw();
    }

    public void update(float delta) {
        elapsedTime += delta;
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
