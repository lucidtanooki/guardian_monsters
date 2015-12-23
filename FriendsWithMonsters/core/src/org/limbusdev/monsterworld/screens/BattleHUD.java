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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
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
    final static class IndPos {
        private static IntVector2 OPPO_TOP = new IntVector2(616, 340);
        private static IntVector2 OPPO_MID = new IntVector2(672, 304);
        private static IntVector2 OPPO_BOT = new IntVector2(728, 268);
        private static IntVector2 HERO_TOP = new IntVector2(184, 340);
        private static IntVector2 HERO_MID = new IntVector2(128, 304);
        private static IntVector2 HERO_BOT = new IntVector2(74, 268);
    }

    final static class BatPos {
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
    private final Array<Label> monsterLvls;
    private final Array<Image> uiRings, uiRingBgs, uiHudBgs, uiNameSigns;
    private final Array<ProgressBar> HPbars, MPbars, RecovBars, ExpBars;
    private final ScrollPane attacksPane;
    private final Image indicatorOpp, indicatorHero;
    private Array<Monster> team;
    private Array<Monster> opponentTeam;
    private float elapsedTime=0;
    private Array<AttackAction> battleQueue;
    private Array<Long> waitingSince;
    private Array<Boolean> monsterReady;
    private boolean battleOver=false;

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
        this.monsterLvls = new Array<Label>();
        this.HPbars = new Array<ProgressBar>();
        this.MPbars = new Array<ProgressBar>();
        this.uiRings = new Array<Image>();
        this.uiRingBgs = new Array<Image>();
        this.uiHudBgs = new Array<Image>();
        this.uiNameSigns = new Array<Image>();
        this.RecovBars = new Array<ProgressBar>();
        this.ExpBars = new Array<ProgressBar>();
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

                /* Calculate Damage */
                if(opponentTeam.get(chosenTarget).HP
                        - team.get(chosenTeamMonster).attacks.get(0).damage < 0)
                    opponentTeam.get(chosenTarget).HP = 0;
                else opponentTeam.get(chosenTarget).HP
                        -= team.get(chosenTeamMonster).attacks.get(0).damage;

                /* Make attacker wait */
                waitingSince.set(chosenTeamMonster, TimeUtils.millis());
                monsterReady.set(chosenTeamMonster, false);
                att1Button.setVisible(false);

                /* Update Health Bar */
                HPbars.get(chosenTarget+3).setValue(100 * opponentTeam.get(chosenTarget).HP
                        / opponentTeam.get(chosenTarget).HPfull);

                // Handle Attack
                handleAttack(team.get(chosenTeamMonster), opponentTeam.get(chosenTarget));

                /* Check if all enemies are KO */
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

        for(Image i : uiNameSigns) stage.addActor(i);
        for(Label l : monsterLabels) stage.addActor(l);
        for(Image i : uiHudBgs) stage.addActor(i);
        for(ProgressBar p : HPbars) stage.addActor(p);
        for(ProgressBar p : MPbars) stage.addActor(p);
        for(ProgressBar p : ExpBars) stage.addActor(p);
        for(ProgressBar p : RecovBars) stage.addActor(p);
        stage.addActor(fightButton);
        stage.addActor(fleeButton);
        stage.addActor(infoLabel);
        stage.addActor(att1Button);
        stage.addActor(battleMenuButton);
//        stage.addActor(attacksPane);
//        stage.addActor(attackList);
        for(Image i : uiRingBgs) stage.addActor(i);
        for(Image i : uiRings) stage.addActor(i);
        for(Label l : monsterLvls) stage.addActor(l);


    }
    /* ............................................................................... METHODS .. */

    /**
     * Initializes the graphical representation of a monster on the screen
     * @param monster   the given monster
     * @param position  the position on the battle field
     * @param teamPos   the position in the respective team
     * @param indPos    the indicator position
     * @param team      whether hero (true) or opponent (false)
     */
    public void initHUD(Monster monster, final int position, final int teamPos, final IntVector2
            indPos, boolean team) {
        monsterLabels.get(position).setText(
                monsterInformation.monsterNames.get(monster.ID - 1));
        monsterLabels.get(position).setVisible(true);
        if(team)
            monsterLabels.get(position).addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    chosenTeamMonster = teamPos;
                    indicatorHero.setPosition(indPos.x, indPos.y, Align.center);
                    activateButton(chosenTeamMonster);
                }
            });
        else
            monsterLabels.get(position).addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                        chosenTarget = position-3;
                        indicatorOpp.setPosition(indPos.x, indPos.y, Align.center);
                    }
                });

        monsterLvls.get(position).setText(
                Integer.toString(monster.level));
        monsterLvls.get(position).setVisible(true);
        HPbars.get(position).setVisible(true);
        MPbars.get(position).setVisible(true);
        RecovBars.get(position).setVisible(true);
        ExpBars.get(position).setVisible(true);
    }

    public void init(TeamComponent team, TeamComponent opponentTeam) {
        this.team = new Array<Monster>();
        for(Monster m : team.monsters)
            if(m.HP > 0)
                this.team.add(m);
        this.opponentTeam = opponentTeam.monsters;
        fightButton.setVisible(true);
        fleeButton.setVisible(true);
        att1Button.setVisible(false);
        battleMenuButton.setVisible(false);
        infoLabel.setVisible(true);

        // Hero Team
        switch(team.monsters.size) {
            case 3:
                initHUD(team.monsters.get(2), BatPos.HERO_TOP, BatPos.TOP, IndPos.HERO_TOP, true);
            case 2:
                initHUD(team.monsters.get(1), BatPos.HERO_BOT, BatPos.BOT, IndPos.HERO_BOT, true);
            default:
                initHUD(team.monsters.get(0), BatPos.HERO_MID, BatPos.MID, IndPos.HERO_MID, true);
                break;
        }

        // Opponent Team
        final Button monsterChooser1,monsterChooser2,monsterChooser3;
        switch(opponentTeam.monsters.size) {
            case 3:
                initHUD(opponentTeam.monsters.get(2), BatPos.OPPO_TOP, BatPos.TOP, IndPos
                        .OPPO_TOP, false);
            case 2:
                initHUD(opponentTeam.monsters.get(1), BatPos.OPPO_BOT, BatPos.BOT, IndPos
                        .OPPO_BOT, false);
            default:
                initHUD(opponentTeam.monsters.get(0), BatPos.OPPO_MID, BatPos.MID, IndPos
                        .OPPO_MID, false);
                break;
        }
        for(Button b : oppMonButtons) stage.addActor(b);
        for(ProgressBar b : RecovBars) b.setValue(100);
    }

    public void hide() {
        for(Label l : monsterLabels) l.setVisible(false);
        for(ProgressBar p : HPbars) p.setVisible(false);
        for(ProgressBar p : MPbars) p.setVisible(false);
        for(ProgressBar p : RecovBars) p.setVisible(false);
        for(ProgressBar p : ExpBars) p.setVisible(false);
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

        // Activate monsters if they have recovered
        for(int i=0; i<6; i++)
            if(!monsterReady.get(i)) {
                if (TimeUtils.timeSinceMillis(waitingSince.get(i)) > team.get(i).recovTime) {
                    monsterReady.set(i, true);
                    if (i == chosenTeamMonster) att1Button.setVisible(true);
                }

                // Update Waiting Bar
                RecovBars.get(i).setValue(TimeUtils.timeSinceMillis(waitingSince.get(i))/(1f*team
                        .get
                        (i).recovTime) * 100f);
            }
    }

    public void setUpUI() {

        // Hero Team ###############################################################################
        addMonsterHUD(58,480-67);
        addMonsterHUD(26,480-100);
        addMonsterHUD(90,480-36);

        // Opponent Team ###########################################################################
        addMonsterHUD(800-256-16-32,480-67);
        addMonsterHUD(800-256-16,480-100);
        addMonsterHUD(800-256-16-64,480-36);


        for(Label l : monsterLabels) l.setVisible(false);
        for(ProgressBar p : HPbars) p.setVisible(false);
        for(ProgressBar p : MPbars) p.setVisible(false);
        for(ProgressBar p : RecovBars) p.setVisible(false);
        for(ProgressBar p : ExpBars) p.setVisible(false);
        for(Label p : monsterLvls) p.setVisible(false);

    }

    public void activateButton(int attackerPos) {
        if(monsterReady.get(attackerPos) == true) att1Button.setVisible(true);
        else att1Button.setVisible(false);
    }

    public void addMonsterHUD(int x, int y) {
        ProgressBar.ProgressBarStyle HPbarStyle = new ProgressBar.ProgressBarStyle();
        HPbarStyle.background = skin.getDrawable("invis");
        Drawable HPbar = skin.getDrawable("HP-slider");
        HPbarStyle.knobBefore = HPbar;
        ProgressBar.ProgressBarStyle MPbarStyle = new ProgressBar.ProgressBarStyle();
        MPbarStyle.background = skin.getDrawable("invis");
        MPbarStyle.knobBefore = skin.getDrawable("MP-slider");
        ProgressBar.ProgressBarStyle RecovBarStyle = new ProgressBar.ProgressBarStyle();
        RecovBarStyle.background = skin.getDrawable("invis");
        RecovBarStyle.knobBefore = skin.getDrawable("red-slider-vert");
        ProgressBar.ProgressBarStyle ExpBarStyle = new ProgressBar.ProgressBarStyle();
        ExpBarStyle.background = skin.getDrawable("invis");
        ExpBarStyle.knobBefore = skin.getDrawable("yellow-slider-hor");

        Image hudEl;
        hudEl = new Image();
        hudEl.setDrawable(skin, "hud-bg");
        hudEl.setPosition(x + 132, y + 5, Align.center);
        hudEl.setVisible(true);
        hudEl.setHeight(22);
        hudEl.setWidth(78);
        uiHudBgs.add(hudEl);
        hudEl = new Image();
        hudEl.setDrawable(skin, "ring-bg");
        hudEl.setPosition(x + 112, y + 1, Align.center);
        hudEl.setVisible(true);
        hudEl.setHeight(24);
        hudEl.setWidth(24);
        uiRingBgs.add(hudEl);
        hudEl = new Image();
        hudEl.setDrawable(skin, "ring");
        hudEl.setPosition(x + 108, y - 3, Align.center);
        hudEl.setVisible(true);
        hudEl.setHeight(32);
        hudEl.setWidth(32);
        uiRings.add(hudEl);
        hudEl = new Image();
        hudEl.setDrawable(skin, "hud-bg3");
        hudEl.setPosition(x-14, y+5);
        hudEl.setWidth(200);
        hudEl.setHeight(22);
        uiNameSigns.add(hudEl);


        ProgressBar mp, hp, rp, ep;

        Label.LabelStyle ls = new Label.LabelStyle();
        ls.background = skin.getDrawable("invis");
        ls.font = skin.getFont("default-font");
        ls.fontColor = Color.WHITE;

        Label monsterLabel = new Label("Me", ls);
        monsterLabel.setWidth(128);
        monsterLabel.setHeight(26);
        monsterLabel.setPosition(x, y + 2); // 48, 480-67
        monsterLabels.add(monsterLabel);

        hp = new ProgressBar(0, 100, 1, false, HPbarStyle);
        mp = new ProgressBar(0, 100, 1, false, MPbarStyle);
        rp = new ProgressBar(0, 100, 1, true, RecovBarStyle);
        ep = new ProgressBar(0, 100, 1, false, ExpBarStyle);

        hp.setPosition(x + 124, y + 14);
        hp.setWidth(128);
        hp.setValue(100);
        mp.setPosition(x + 124, y + 6);
        mp.setWidth(100);
        mp.setValue(100);
        rp.setPosition(x + 100, y + 5);
        rp.setHeight(22);
        mp.setValue(100);
        ep.setPosition(x-9, y);
        ep.setWidth(120);
        ep.setValue(100);

        hp.setAnimateInterpolation(Interpolation.linear);
        hp.setAnimateDuration(1f);
        rp.setAnimateInterpolation(Interpolation.linear);
        rp.setAnimateDuration(.1f);
        ep.setAnimateInterpolation(Interpolation.linear);
        ep.setAnimateDuration(.1f);

        HPbars.add(hp);
        MPbars.add(mp);
        RecovBars.add(rp);
        ExpBars.add(ep);

        ls.font = skin.getFont("white");
        Label lvl = new Label("0", ls);
        lvl.setPosition(x + 125, y + 13, Align.center);
        monsterLvls.add(lvl);
    }



    /**
     * Check whether hero lost combat
     */
    public void handleEndOfBattle() {
        boolean heroLost = true;
        for(Monster m : team)
            if(m.HP > 0) heroLost = false;
    }


    /**
     * Handle the event of a monster being killed
     */
    private void handleAttack(Monster att, Monster def) {
        if(att.HP == 0 || def.HP == 0) {
            if (team.contains(att, false) && def.HP == 0) {
                // Defeated Monster was part of opponents team
                int exp = def.level * (def.HPfull + def.physStrength);
                exp /= team.size;
                for (Monster m : team) if (m.HP > 0) m.getEXP(exp);
            }

            // Remove defeated monsters from screens
        }

    }

    /* ..................................................................... GETTERS & SETTERS .. */
}
