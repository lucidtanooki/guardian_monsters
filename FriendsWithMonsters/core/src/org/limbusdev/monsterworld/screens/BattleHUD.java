package org.limbusdev.monsterworld.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import org.limbusdev.monsterworld.MonsterWorld;
import org.limbusdev.monsterworld.ecs.components.TeamComponent;
import org.limbusdev.monsterworld.geometry.IntVector2;
import org.limbusdev.monsterworld.model.Attack;
import org.limbusdev.monsterworld.model.AttackAction;
import org.limbusdev.monsterworld.model.Monster;
import org.limbusdev.monsterworld.model.MonsterInformation;
import org.limbusdev.monsterworld.utils.GlobalSettings;


/**
 * Created by georg on 03.12.15.
 */
public class BattleHUD {

    /* ............................................................................ ATTRIBUTES .. */

    /**
     * Possible Indicator positions
     */
    final static class IndPos {
        private static IntVector2 OPPO_TOP = new IntVector2(616, 340);
        private static IntVector2 OPPO_MID = new IntVector2(672, 304);
        private static IntVector2 OPPO_BOT = new IntVector2(728, 268);
        private static IntVector2 HERO_TOP = new IntVector2(184, 340);
        private static IntVector2 HERO_MID = new IntVector2(128, 304);
        private static IntVector2 HERO_BOT = new IntVector2(74, 268);
    }

    /**
     * Positions on the Battle Field
     */
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
        private static int[] positions = {0,2,1};
    }

    private Skin skin;
    public  Stage stage;
    private MonsterInformation monsterInformation;

    private final Group battleActionMenu, topLevelMenu;
    private final ImageButton fightButton, fleeButton, battleMenuButton, bagButton, backButton;
    private final Image bg;
    private final Array<Button> oppMonButtons;
    private final Label infoLabel;
    private final Array<Label> monsterLabels, monsterLvls;
    private final Array<Image> uiRings, uiRingBgs, uiHudBgs, uiNameSigns;
    private final Array<ProgressBar> HPbars, MPbars, RecovBars, ExpBars;
    private final ArrayMap<String,Button> battleButtons;
    private final Image indicatorOpp, indicatorHero;
    private Array<Monster> team, opponentTeam;
    private float elapsedTime=0;
    private Array<AttackAction> battleQueue;
    private Array<Long> waitingSince;
    private Array<Boolean> monsterReady;
    private boolean battleOver=false;

    // Attack Scroll Pane
    private final ScrollPane attacksPane;
    private VerticalGroup attacksGroup;
    private final Image attBgImg;

    private final OutdoorGameWorldScreen gameScreen;
    private final MonsterWorld game;
    private int chosenTarget=BatPos.MID;
    private int chosenTeamMonster=BatPos.MID;

    private int indicatorOppPos=0;
    private int indicatorHeroPos=0;

    // Time
    private long lastActionTime = 0;
    private boolean waiting = false;
    /* ........................................................................... CONSTRUCTOR .. */
    public BattleHUD(final MonsterWorld game, final OutdoorGameWorldScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
        this.monsterInformation = MonsterInformation.getInstance();
        this.battleButtons = new ArrayMap<String, Button>();
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

        this.bg = new Image(game.media.getBattleUITextureAtlas().findRegion("bg"));
        bg.setPosition(0, 0);
        bg.setWidth(800);
        bg.setHeight(140);
        stage.addActor(bg);

        // Top Level Menu
        this.topLevelMenu = new Group();
        fightButton = new ImageButton(skin.getDrawable("textfield"));
        fleeButton = new ImageButton(skin.getDrawable("textfield"));
        bagButton = new ImageButton(skin.getDrawable("textfield"));
        backButton = new ImageButton(skin.getDrawable("textfield"));
        setUpTopLevelMenu();

        // Battle Action Menu
        this.attacksGroup = new VerticalGroup();
        this.attacksPane = new ScrollPane(attacksGroup);
        attacksPane.setHeight(32);
        attacksPane.setPosition(0, 240);
        attBgImg = new Image(game.media.getBattleUITextureAtlas().findRegion("attPane"));
        attBgImg.setPosition(400, 0, Align.bottom);
        attBgImg.setWidth(588);
        attBgImg.setHeight(136);
        attBgImg.setVisible(false);
        infoLabel = new Label("A monster attacks you!", skin, "default");
        this.battleActionMenu = new Group();
        battleMenuButton = new ImageButton(skin.getDrawable("textfield"));
        setUpBattleActionMenu();


        // Battle HUD Status Bars
        indicatorOpp = new Image(skin.getDrawable("indicator"));
        indicatorHero = new Image(skin.getDrawable("indicator"));

        indicatorOpp.setVisible(true);
        indicatorOpp.setPosition(IndPos.OPPO_MID.x, IndPos.OPPO_MID.y, Align.center);

        indicatorHero.setVisible(true);
        indicatorHero.setPosition(IndPos.HERO_MID.x, IndPos.HERO_MID.y, Align.center);

        setUpUI();


        List attackList = new List(skin, "default");
        attackList.setHeight(200);

        // Buttons ............................................................................. END

        for(Image i : uiNameSigns) stage.addActor(i);
        for(Label l : monsterLabels) stage.addActor(l);
        for(Image i : uiHudBgs) stage.addActor(i);
        for(ProgressBar p : HPbars) stage.addActor(p);
        for(ProgressBar p : MPbars) stage.addActor(p);
        for(ProgressBar p : ExpBars) stage.addActor(p);
        for(ProgressBar p : RecovBars) stage.addActor(p);
        stage.addActor(indicatorOpp);
        stage.addActor(indicatorHero);
        for(Image i : uiRingBgs) stage.addActor(i);
        for(Image i : uiRings) stage.addActor(i);
        for(Label l : monsterLvls) stage.addActor(l);

        reset();
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
        ExpBars.get(position).setValue(monster.getExpPerc());
        HPbars.get(position).setValue(monster.HP/1.f/monster.HPfull*100);
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
        activateMonsterHUD(monster, position);
        chosenTarget=1;chosenTeamMonster=1;
        stage.addAction(Actions.fadeIn(1f));
    }

    public void init(TeamComponent team, TeamComponent opponentTeam) {
        this.team = new Array<Monster>();
        for(Monster m : team.monsters)
            if(m.HP > 0)
                this.team.add(m);
        this.opponentTeam = opponentTeam.monsters;
        fightButton.setVisible(true);
        fleeButton.setVisible(true);
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

        chosenTarget=1;chosenTeamMonster=1;
    }

    public void hide() {
        reset();
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
                    if (i == chosenTeamMonster) battleButtons.get("button10").setVisible(true);
                }

                // Update Waiting Bar
                RecovBars.get(i).setValue(TimeUtils.timeSinceMillis(waitingSince.get(i))/(1f*team
                        .get
                        (i).recovTime) * 100f);
            }

        switch(team.size) {
            case 3:
                monsterLvls.get(BatPos.HERO_TOP).setText(Integer.toString(team.get(BatPos
                        .HERO_TOP).level));
                ExpBars.get(BatPos.HERO_TOP).setValue(team.get(BatPos.HERO_TOP).getExpPerc());
            case 2:
                monsterLvls.get(BatPos.HERO_BOT).setText(Integer.toString(team.get(BatPos
                        .HERO_BOT).level));
                ExpBars.get(BatPos.HERO_BOT).setValue(team.get(BatPos.HERO_BOT).getExpPerc());
            default:
                monsterLvls.get(BatPos.HERO_MID).setText(Integer.toString(team.get(BatPos
                        .HERO_MID).level));
                ExpBars.get(BatPos.HERO_MID).setValue(team.get(BatPos.HERO_MID).getExpPerc());
                break;
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
        if(monsterReady.get(attackerPos) == true) battleButtons.get("button10").setVisible(true);
        else battleButtons.get("button10").setVisible(false);
        setUpAttacksPane();
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

    public void reset() {
        for(Label l : monsterLabels) l.setVisible(false);
        for(ProgressBar p : HPbars) p.setVisible(false);
        for(ProgressBar p : MPbars) p.setVisible(false);
        for(ProgressBar p : RecovBars) p.setVisible(false);
        for(ProgressBar p : ExpBars) p.setVisible(false);
        for(Image i : uiNameSigns) i.setVisible(false);
        for(Image i : uiHudBgs) i.setVisible(false);
        for(Image i : uiRingBgs) i.setVisible(false);
        for(Image i : uiRings) i.setVisible(false);
        for(Label l : monsterLvls) l.setVisible(false);
        oppMonButtons.clear();
        this.chosenTarget = BatPos.MID;
        this.chosenTeamMonster = BatPos.MID;
    }

    public void activateMonsterHUD(Monster monster, final int position) {
        monsterLvls.get(position).setText(Integer.toString(monster.level));
        monsterLvls.get(position).setVisible(true);
        HPbars.get(position).setVisible(true);
        MPbars.get(position).setVisible(true);
        RecovBars.get(position).setVisible(true);
        ExpBars.get(position).setVisible(true);
        uiRingBgs.get(position).setVisible(true);
        uiHudBgs.get(position).setVisible(true);
        uiNameSigns.get(position).setVisible(true);
        uiRings.get(position).setVisible(true);
    }

    /**
     * Setting up the main menu in battle mode
     *  Fight
     *  Escape
     */
    private void setUpTopLevelMenu() {
        // Tables
        this.topLevelMenu.setWidth(GlobalSettings.RESOLUTION_X);
        this.topLevelMenu.setHeight(GlobalSettings.RESOLUTION_Y / 4);

        fightButton.setWidth(140f);
        fightButton.setHeight(112f);
        fightButton.setPosition(400, 0, Align.bottom);

        fightButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                topLevelMenu.addAction(
                        Actions.sequence(Actions.alpha(0, .3f), Actions.visible(false)));
                battleActionMenu.addAction(
                        Actions.sequence(Actions.visible(true),Actions.alpha(1, .5f)));
            }
        });

        fleeButton.setWidth(148f);
        fleeButton.setHeight(112f);
        fleeButton.setPosition(527, 0, Align.bottom);

        fleeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addAction(Actions.sequence(Actions.fadeOut(1), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(gameScreen);
                    }
                })));
            }
        });

        bagButton.setWidth(140f);
        bagButton.setHeight(112f);
        bagButton.setPosition(276, 0, Align.bottom);

        bagButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO
                ;
            }
        });

        topLevelMenu.addActor(fightButton);
        topLevelMenu.addActor(fleeButton);
        topLevelMenu.addActor(bagButton);

        stage.addActor(topLevelMenu);

        ImageButton.ImageButtonStyle ibs = new ImageButton.ImageButtonStyle();
        ibs.down = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b_fight_down"));
        ibs.up = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b_fight"));
        fightButton.setStyle(ibs);

        ibs = new ImageButton.ImageButtonStyle();
        ibs = new ImageButton.ImageButtonStyle();
        ibs.down = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b_flee_down"));
        ibs.up = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b_flee"));
        fleeButton.setStyle(ibs);

        ibs = new ImageButton.ImageButtonStyle();
        ibs.down = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b_bag_down"));
        ibs.up = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b_bag"));
        bagButton.setStyle(ibs);

    }

    private void setUpBattleActionMenu() {
        this.battleActionMenu.setWidth(GlobalSettings.RESOLUTION_X);
        this.battleActionMenu.setHeight(GlobalSettings.RESOLUTION_Y / 4);
        this.battleActionMenu.setVisible(false);

        // Attack Pane .............................................................................
        attacksPane.setHeight(136);attacksPane.setWidth(500);
        attacksPane.setPosition(400, 0, Align.bottom);
        attacksPane.setVisible(false);

        // Buttons .................................................................................

        infoLabel.setHeight(64);
        infoLabel.setWidth(542);
        infoLabel.setWrap(true);
        infoLabel.setPosition(400, 72, Align.bottom);
        infoLabel.setVisible(true);
        Label.LabelStyle labs = new Label.LabelStyle();
        labs.background = new TextureRegionDrawable(game.media.getBattleUITextureAtlas()
                .findRegion("b12.up"));
        labs.font = skin.getFont("default-font");
        infoLabel.setStyle(labs);

        ImageButton.ImageButtonStyle ibs = new ImageButton.ImageButtonStyle();
        ibs.down = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b6down"));
        ibs.up = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b6up"));
        backButton.setStyle(ibs);

        backButton.setWidth(60f);
        backButton.setHeight(72f);
        backButton.setPosition(680, 0, Align.bottomLeft);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                battleActionMenu.addAction(
                        Actions.sequence(Actions.alpha(0, .3f), Actions.visible(false)));
                topLevelMenu.addAction(
                        Actions.sequence(Actions.visible(true), Actions.alpha(1, .5f)));
            }
        });

        battleActionMenu.addActor(backButton);

        battleMenuButton.setWidth(128f);
        battleMenuButton.setHeight(64f);
        battleMenuButton.setPosition(700 - 128, 74);

        battleMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                topLevelMenu.addAction(
                        Actions.sequence(Actions.alpha(0, .5f), Actions.visible(false)));
                battleActionMenu.addAction(
                        Actions.sequence(Actions.visible(true), Actions.alpha(1, .5f)));
            }
        });

        battleActionMenu.addActor(battleMenuButton);
        battleActionMenu.addActor(infoLabel);
        stage.addActor(battleActionMenu);

        // Create indicator buttons
        ibs = new ImageButton.ImageButtonStyle();
        ibs.down = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b1down"));
        ibs.up = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b1up"));
        ImageButton ib = new ImageButton(ibs);
        ib.setWidth(52);ib.setHeight(64);
        ib.setPosition(0, 64);
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                indicatorHeroPos++;
                switch (team.size) {
                    case 3:
                        if (indicatorHeroPos > 2) indicatorHeroPos = 0;
                        break;
                    case 2:
                        if (indicatorHeroPos > 1) indicatorHeroPos = 0;
                        break;
                    default:
                        if (indicatorHeroPos > 0) indicatorHeroPos = 0;
                        break;
                }
                chosenTeamMonster = BatPos.positions[indicatorHeroPos];
                changeIndicatorPosition(true, chosenTeamMonster);
                System.out.println(chosenTeamMonster);
            }
        });
        battleButtons.put("indHeroUp", ib);

        ibs = new ImageButton.ImageButtonStyle();
        ibs.down = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b2down"));
        ibs.up = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b2up"));
        ib = new ImageButton(ibs);
        ib.setWidth(52);ib.setHeight(64);
        ib.setPosition(0, 0);
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                indicatorHeroPos--;
                if (indicatorHeroPos < 0) {
                    switch (team.size) {
                        case 3:
                            indicatorHeroPos = 2;
                            break;
                        case 2:
                            indicatorHeroPos = 1;
                            break;
                        default:
                            indicatorHeroPos = 0;
                            break;
                    }
                }
                chosenTeamMonster = BatPos.positions[indicatorHeroPos];
                changeIndicatorPosition(true, chosenTeamMonster);
                System.out.println(chosenTeamMonster);
            }
        });
        battleButtons.put("indHeroDown", ib);

        // Opponent Indicator
        ibs = new ImageButton.ImageButtonStyle();
        ibs.down = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b3down"));
        ibs.up = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b3up"));
        ib = new ImageButton(ibs);
        ib.setWidth(52);ib.setHeight(64);
        ib.setPosition(748, 64);
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                indicatorOppPos++;
                switch (opponentTeam.size) {
                    case 3:
                        if (indicatorOppPos > 2) indicatorOppPos = 0;
                        break;
                    case 2:
                        if (indicatorOppPos > 1) indicatorOppPos = 0;
                        break;
                    default:
                        if (indicatorOppPos > 0) indicatorOppPos = 0;
                        break;
                }
                chosenTarget = BatPos.positions[indicatorOppPos];
                changeIndicatorPosition(false, chosenTarget);
                System.out.println(chosenTarget);
            }
        });
        battleButtons.put("indOppUp", ib);

        ibs = new ImageButton.ImageButtonStyle();
        ibs.down = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b4down"));
        ibs.up = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b4up"));
        ib = new ImageButton(ibs);
        ib.setWidth(52);ib.setHeight(64);
        ib.setPosition(748, 0);
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                indicatorOppPos--;
                if (indicatorOppPos < 0) {
                    switch (opponentTeam.size) {
                        case 3:
                            indicatorOppPos = 2;
                            break;
                        case 2:
                            indicatorOppPos = 1;
                            break;
                        default:
                            indicatorOppPos = 0;
                            break;
                    }
                }
                chosenTarget = BatPos.positions[indicatorOppPos];
                changeIndicatorPosition(false, chosenTarget);
                System.out.println(chosenTarget);
            }
        });
        battleButtons.put("indOppDown", ib);

        // Left Button
        ibs = new ImageButton.ImageButtonStyle();
        ibs.down = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b7down"));
        ibs.up = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b7up"));
        ib = new ImageButton(ibs);
        ib.setWidth(152);ib.setHeight(70);
        ib.setPosition(105, 0);
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO
                System.out.println("Button 7");
            }
        });
        battleButtons.put("button7", ib);

        // Right Button
        ibs = new ImageButton.ImageButtonStyle();
        ibs.down = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b8down"));
        ibs.up = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b8up"));
        ib = new ImageButton(ibs);
        ib.setWidth(152);ib.setHeight(70);
        ib.setPosition(543, 0);
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO
                System.out.println("Button 8");
            }
        });
        battleButtons.put("button8", ib);

        // Bottom Button
        ibs = new ImageButton.ImageButtonStyle();
        ibs.down = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b9down"));
        ibs.up = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b9up"));
        ib = new ImageButton(ibs);
        ib.setWidth(364);ib.setHeight(32);
        ib.setPosition(400, 0,Align.bottom);
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO
                System.out.println("Button 9");
            }
        });
        battleButtons.put("button9", ib);

        // Bottom Button
        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
        tbs.down = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b10down"));
        tbs.up = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b10up"));
        tbs.font = skin.getFont("default-font");
        TextButton tb = new TextButton("Attack", tbs);
        tb.setWidth(284);ib.setHeight(36);
        tb.setPosition(400, 34, Align.bottom);
        tb.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO
                System.out.println("Button 10");
                attBgImg.addAction(Actions.sequence(Actions.visible(true),Actions.fadeIn(.3f)));
                attacksPane.addAction(Actions.sequence(Actions.visible(true),Actions.fadeIn(.3f)));
                setUpAttacksPane();
            }
        });
        battleButtons.put("button10", tb);

        // Button 5
        ibs = new ImageButton.ImageButtonStyle();
        ibs.down = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b5down"));
        ibs.up = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b5up"));
        ib = new ImageButton(ibs);
        ib.setWidth(60);ib.setHeight(72);
        ib.setPosition(60, 0);
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO
                System.out.println("Button 5");
            }
        });
        battleButtons.put("button5", ib);

        for(String s : battleButtons.keys()) battleActionMenu.addActor(battleButtons.get(s));

        battleActionMenu.addActor(attBgImg);
        battleActionMenu.addActor(attacksPane);
    }

    public void changeIndicatorPosition(boolean herosTeam, int pos) {
        if(herosTeam) {
            switch(pos) {
                case 2: indicatorHero.setPosition(IndPos.HERO_TOP.x, IndPos.HERO_TOP.y, Align.center);break;
                case 1: indicatorHero.setPosition(IndPos.HERO_BOT.x, IndPos.HERO_BOT.y, Align.center);break;
                default: indicatorHero.setPosition(IndPos.HERO_MID.x, IndPos.HERO_MID.y, Align.center);break;
            }
            activateButton(pos);
        } else {
            switch(pos) {
                case 2:
                    indicatorOpp.setPosition(IndPos.OPPO_TOP.x, IndPos.OPPO_TOP.y, Align.center);
                    break;
                case 1:
                    indicatorOpp.setPosition(IndPos.OPPO_BOT.x, IndPos.OPPO_BOT.y, Align.center);
                    break;
                default:
                    indicatorOpp.setPosition(IndPos.OPPO_MID.x, IndPos.OPPO_MID.y, Align.center);
                    break;
            }
        }
    }


    public void setUpAttacksPane() {

        this.attacksGroup.clear();
        attacksGroup.setWidth(500);
        this.attacksGroup.space(4);

        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
        tbs.fontColor = Color.BLACK;
        tbs.font = skin.getFont("default-font");
        tbs.pressedOffsetY = -1;
        tbs.up = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion("b13up"));
        tbs.down = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion("b13down"));

        for(Attack a : team.get(indicatorHeroPos).attacks) {
            TextButton tb = new TextButton(a.name, tbs);
            tb.setWidth(128);
            tb.setHeight(23);
            attacksGroup.addActor(tb);
            tb.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Attack 1 by Team Member " + chosenTeamMonster + " at " +
                            chosenTarget);

                /* Calculate Damage */
                    if (opponentTeam.get(chosenTarget).HP
                            - team.get(chosenTeamMonster).attacks.get(0).damage < 0)
                        opponentTeam.get(chosenTarget).HP = 0;
                    else opponentTeam.get(chosenTarget).HP
                            -= team.get(chosenTeamMonster).attacks.get(0).damage;

                /* Make attacker wait */
                    waitingSince.set(chosenTeamMonster, TimeUtils.millis());
                    monsterReady.set(chosenTeamMonster, false);

                /* Update Health Bar */
                    HPbars.get(chosenTarget + 3).setValue(100 * opponentTeam.get(chosenTarget).HP
                            / opponentTeam.get(chosenTarget).HPfull);

                    // Handle Attack
                    handleAttack(team.get(chosenTeamMonster), opponentTeam.get(chosenTarget));

                /* Check if all enemies are KO */
                    boolean allKO = true;
                    switch (opponentTeam.size) {
                        case 3:
                            allKO = (opponentTeam.get(BatPos.TOP).HP == 0);
                            System.out.println("OppMon 3: " + opponentTeam.get(2).HP
                                    + "/" + opponentTeam.get(2).HPfull + " KO: " + allKO);
                        case 2:
                            allKO = (allKO == true && opponentTeam.get(1).HP == 0);
                            System.out.println("OppMon 2: " + opponentTeam.get(1).HP
                                    + "/" + opponentTeam.get(1).HPfull + " KO: " + allKO);
                        default:
                            allKO = (allKO == true && opponentTeam.get(0).HP == 0);
                            System.out.println("OppMon 1: " + opponentTeam.get(0).HP
                                    + "/" + opponentTeam.get(0).HPfull + " KO: " + allKO);
                            break;
                    }


                    if (allKO) game.setScreen(gameScreen);

                    attBgImg.addAction(Actions.sequence(Actions.fadeOut(.3f), Actions.visible
                            (false)));
                    attacksPane.addAction(Actions.sequence(Actions.fadeOut(.3f), Actions.visible
                            (false)));
                    battleButtons.get("button10").setVisible(false);
                }
            });
        }
        attacksGroup.addActor(new TextButton("1", tbs));
        attacksGroup.addActor(new TextButton("2", tbs));
        attacksGroup.addActor(new TextButton("3", tbs));
        attacksGroup.addActor(new TextButton("4", tbs));
        attacksGroup.addActor(new TextButton("5", tbs));
        attacksGroup.addActor(new TextButton("6", tbs));
    }

    /* ..................................................................... GETTERS & SETTERS .. */
}
