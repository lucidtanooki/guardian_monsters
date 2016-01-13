package org.limbusdev.monsterworld.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
import org.limbusdev.monsterworld.model.BattlePositionQueue;
import org.limbusdev.monsterworld.model.Monster;
import org.limbusdev.monsterworld.model.MonsterInformation;
import org.limbusdev.monsterworld.utils.DebugOutput;
import org.limbusdev.monsterworld.utils.GlobalSettings;

import java.util.Iterator;


/**
 * BattleHUD manages all actions and UI elements in the {@link BattleScreen}
 *
 * Created by georg on 03.12.15.
 */
public class BattleHUD {

    /* ............................................................................ ATTRIBUTES .. */
    private final OutdoorGameWorldScreen gameScreen;
    private final MonsterWorld game;

    public  Stage stage;
    private Skin skin;
    private MonsterInformation monsterInformation;

    // Groups
    private Array<Group> statusUIElements;
    private Group battleActionMenu, topLevelMenu, monsterStatusUI, attackPaneGroup, gameOverUI;
    private VerticalGroup attackVGroup; // Group of attack buttons

    // Buttons
    private ArrayMap<String,Button> battleButtons;
    private ArrayMap<String,ImageButton> topLevelMenuButtons;

    // Labels
    private Label infoLabel, gameOverLabel;
    private Array<Label> monsterLabels, monsterLvls;

    // Images
    private ArrayMap<String,Array<Image>> monScreenElems;
    private Image indicatorOpp, indicatorHero, battleUIbg, attackScrollPaneBg, blackCourtain;
    private ArrayMap<String, Image> battleMenuImgs;
    private ArrayMap<Integer,Image> monsterImgs;

    // ProgressBars
    private ArrayMap<String,Array<ProgressBar>> progressBars;

    // Scroll Panes
    private ScrollPane attackScroll;


    // Not Scene2D related
    private Array<Monster> team, opponentTeam;
    private Array<AttackAction> battleQueue;
    private Array<Long> waitingSince;
    private Array<Boolean> monsterReady;
    private Array<Boolean> monsterKO;

    private int chosenTarget=BatPos.MID;
    private int chosenTeamMonster=BatPos.MID;

    private int indicatorHeroPos=0;
    private boolean allKO, allHeroKO;

    private BattlePositionQueue heroPosQueue, opponentPosQueue;

    private AIPlayer aiPlayer;

    /* ........................................................................... CONSTRUCTOR .. */
    public BattleHUD(final MonsterWorld game, final OutdoorGameWorldScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;

        initializeAttributes();
        setUpUI();

        // Top Level Menu
        setUpTopLevelMenu();

        // Battle Action Menu
        setUpBattleActionMenu();

        setUpMonsterImages();

        setUpGameOverUI();

        addElementsToStage();

        reset();
    }
    /* ............................................................................... METHODS .. */

    /**
     * Resets the UI into a state where it can be initialized for a new battle
     */
    public void reset() {
        // Attributes
        this.chosenTarget = BatPos.MID;
        this.chosenTeamMonster = BatPos.MID;

        waitingSince.clear();
        monsterReady.clear();
        monsterKO.clear();
        for(int i=0;i<6;i++) {
            waitingSince.add(new Long(0));
            monsterReady.add(true);
            monsterKO.add(false);
        }

        for (String key : progressBars.keys())
            for(ProgressBar b : progressBars.get(key)) b.setValue(100);

        indicatorHeroPos=0;
        allKO = false;

        // Clear Actions
        for(Group g : statusUIElements) g.clearActions();

        for(Integer key : monsterImgs.keys()) monsterImgs.get(key).clearActions();
        battleActionMenu.clearActions();
        topLevelMenu.clearActions();
        monsterStatusUI.clearActions();
        attackPaneGroup.clearActions();
        blackCourtain.clearActions();
        gameOverUI.clearActions();

        resetUIElementVisibility();
    }

    private void resetUIElementVisibility() {
        // Visibility
        // Reset Status UIs
        for(Group g : statusUIElements) {
            g.addAction(Actions.alpha(1));
            g.setVisible(false);
        }

        for(Integer key : monsterImgs.keys()) {
            monsterImgs.get(key).addAction(Actions.alpha(1));
            monsterImgs.get(key).setVisible(false);
        }

        battleActionMenu.addAction(Actions.alpha(1));
        battleActionMenu.setVisible(false);
        topLevelMenu.addAction(Actions.alpha(1));
        topLevelMenu.setVisible(true);
        monsterStatusUI.addAction(Actions.alpha(1));
        monsterStatusUI.setVisible(true);

        attackPaneGroup.setVisible(false);
        blackCourtain.setVisible(true);
        gameOverUI.setVisible(false);
    }

    /**
     * Initializes the battle screen with the given teams
     * @param team
     * @param opponentTeam
     */
    public void init(TeamComponent team, TeamComponent opponentTeam) {
        reset();

        // Initializes Battle Queues ...............................................................
        this.heroPosQueue = new BattlePositionQueue(team.monsters.size);
        this.opponentPosQueue = new BattlePositionQueue(opponentTeam.monsters.size);

        //Initialize Teams .........................................................................
        // Hero Team
        this.team = team.monsters;
        changeIndicatorPosition(true, BatPos.MID);

        // Choose fit monsters for battle
        int i=0;
        for(Monster m : team.monsters) {
            if (m.HP <= 0) {
                this.monsterKO.set(i, true);
                kickOutMonster(i);
            }
            i++;
        }

        // Opponent Team
        changeIndicatorPosition(false, BatPos.MID);
        this.opponentTeam = opponentTeam.monsters;

        this.aiPlayer = new AIPlayer(this.opponentTeam,this.team);
        this.aiPlayer.havePause(true);


        // Initialize Status UIs ...................................................................
        // Hero Team
        switch(team.monsters.size) {
            case 3:initStatusUI(team.monsters.get(2), BatPos.HERO_TOP);
            case 2:initStatusUI(team.monsters.get(1), BatPos.HERO_BOT);
            default:initStatusUI(team.monsters.get(0), BatPos.HERO_MID);break;
        }

        // Opponent Team
        switch(opponentTeam.monsters.size) {
            case 3: initStatusUI(opponentTeam.monsters.get(2), BatPos.OPPO_TOP);
            case 2: initStatusUI(opponentTeam.monsters.get(1), BatPos.OPPO_BOT);
            default:initStatusUI(opponentTeam.monsters.get(0), BatPos.OPPO_MID);break;
        }

//        initMonsterImages();
        show();
        System.out.println(topLevelMenu.isVisible());
        for(String k : topLevelMenuButtons.keys())
            System.out.println(topLevelMenuButtons.get(k).isVisible());
    }

    /**
     * Initializes the graphical representation of a monsters status on the screen
     * @param monster   the given monster
     * @param position  the position on the battle field
     */
    public void initStatusUI(Monster monster, final int position) {

        // Set Monster Information in UI and set Signs visible
        monsterLabels.get(position).setText(monsterInformation.monsterNames.get(monster.ID - 1));

        progressBars.get("EXP").get(position).setValue(monster.getExpPerc());
        progressBars.get("HP").get(position).setValue(monster.HP / 1.f / monster.HPfull * 100);

        setMonsterStatusUIvisible(monster, position);

        stage.addAction(Actions.fadeIn(1f));
    }

    /**
     * Initializes the HUD for the given battle field position
     * @param monster
     * @param position
     */
    public void setMonsterStatusUIvisible(Monster monster, final int position) {
        statusUIElements.get(position).addAction(Actions.sequence(
                Actions.alpha(1), Actions.visible(true)
        ));

        monsterLvls.get(position).setText(Integer.toString(monster.level));

        initMonsterImage(monster, position);
    }

    private void initMonsterImage(Monster monster, int position) {
        Image monImg; TextureRegion monReg;
        monImg = monsterImgs.get(position);
        monReg = game.media.getMonsterSprite(monster.ID);
        if(position < 3) if(!monReg.isFlipX()) monReg.flip(true, false);
        monImg.setDrawable(new TextureRegionDrawable(monReg));
        monImg.addAction(Actions.sequence(Actions.alpha(1), Actions.visible(true)));
    }

    /**
     * Action that should take place when the screen gets hidden
     */
    public void hide() {
        stage.act(100);
        reset();
    }

    public void show() {
        blackCourtain.addAction(Actions.sequence(Actions.fadeOut(1), Actions.visible(false)));
    }

    /**
     * Draw the HUD to the screen
     */
    public void draw() {
        this.stage.draw();
    }

    /**
     * Update the HUD
     * @param delta
     */
    public void update(float delta) {
        stage.act(delta);
        aiPlayer.act();

        // ........................................................................... END OF BATTLE
        if (allKO) handleEndOfBattle();


        // .............................................................................. RECOVERING
        // Activate monsters if they have recovered
        for(int i=0; i<6; i++)
            if(!monsterReady.get(i)) {
                if (i<3 && TimeUtils.timeSinceMillis(waitingSince.get(i)) > team.get(i).recovTime) {
                    monsterReady.set(i, true);

                    // Reactivate Attack Button
                    if (i == chosenTeamMonster) {
                        battleButtons.get("attack").setDisabled(false);
                        battleButtons.get("attack").addAction(
                                Actions.sequence(Actions.alpha(1,.2f)));
                    }
                }

                if(GlobalSettings.DEBUGGING_ON) System.out.println(i + " ws: " + waitingSince.get
                        (i) + " tsm: " + TimeUtils.timeSinceMillis(waitingSince.get(i))
                        + " rt: " + opponentTeam.get(i-3).recovTime)
                ;
                if(i>2 && TimeUtils.timeSinceMillis(waitingSince.get(i))
                        > opponentTeam.get(i-3).recovTime)
                            monsterReady.set(i,true);

                if(i<3) {
                    // Update Waiting Bar
                    progressBars.get("Recov").get(i).setValue(TimeUtils.timeSinceMillis(
                            waitingSince.get(i)) / (1f * team.get(i).recovTime) * 100f);
                } else {
                    // Update Waiting Bar
                    progressBars.get("Recov").get(i).setValue(TimeUtils.timeSinceMillis(
                            waitingSince.get(i)) / (1f * opponentTeam.get(i-3).recovTime) * 100f);
                }
            }

        switch(team.size) {
            case 3:
                monsterLvls.get(BatPos.HERO_TOP).setText(Integer.toString(team.get(BatPos
                        .HERO_TOP).level));
                progressBars.get("EXP").get(BatPos.HERO_TOP).setValue(team.get(BatPos.HERO_TOP)
                        .getExpPerc());
            case 2:
                monsterLvls.get(BatPos.HERO_BOT).setText(Integer.toString(team.get(BatPos
                        .HERO_BOT).level));
                progressBars.get("EXP").get(BatPos.HERO_BOT).setValue(team.get(BatPos.HERO_BOT)
                        .getExpPerc());
            default:
                monsterLvls.get(BatPos.HERO_MID).setText(Integer.toString(team.get(BatPos
                        .HERO_MID).level));
                progressBars.get("EXP").get(BatPos.HERO_MID).setValue(team.get(BatPos.HERO_MID)
                        .getExpPerc());
                break;
        }
    }

    private void addElementsToStage() {
        stage.addActor(battleUIbg);
        stage.addActor(topLevelMenu);
        stage.addActor(battleActionMenu);

        for(Integer key : monsterImgs.keys())
            stage.addActor(monsterImgs.get(key));

        stage.addActor(monsterStatusUI);
        for(Group g : statusUIElements) stage.addActor(g);

        stage.addActor(gameOverUI);

        stage.addActor(blackCourtain);
    }

    /**
     * Setting up HUD elements:
     *  team and opponent information screens
     *  labels
     *  levels
     *  progressbars like HP, MP and so on
     */
    public void setUpUI() {
        // Scene2D
        FitViewport fit = new FitViewport(GlobalSettings.RESOLUTION_X, GlobalSettings.RESOLUTION_Y);
        this.stage = new Stage(fit);
        this.skin = game.media.skin;
        if(GlobalSettings.DEBUGGING_ON) stage.setDebugAll(true);

        this.monsterStatusUI = new Group();
        this.statusUIElements = new Array<Group>();

        // Battle UI Black transparent Background
        this.battleUIbg = new Image(game.media.getBattleUITextureAtlas().findRegion("bg"));
        battleUIbg.setPosition(0, 0);
        battleUIbg.setWidth(GlobalSettings.RESOLUTION_X);
        battleUIbg.setHeight(140);

        // Black Courtain for fade-in and -out
        this.blackCourtain = new Image(game.media.getBattleUITextureAtlas().findRegion("black"));
        this.blackCourtain.setWidth(GlobalSettings.RESOLUTION_X);
        this.blackCourtain.setHeight(GlobalSettings.RESOLUTION_Y);
        this.blackCourtain.setPosition(0, 0);

        // Hero Team ###############################################################################
        statusUIElements.add(addMonsterHUD(58, GlobalSettings.RESOLUTION_Y - 67));
        statusUIElements.add(addMonsterHUD(26, GlobalSettings.RESOLUTION_Y - 100));
        statusUIElements.add(addMonsterHUD(90, GlobalSettings.RESOLUTION_Y - 36));

        // Opponent Team ###########################################################################
        statusUIElements.add(addMonsterHUD(GlobalSettings.RESOLUTION_X - 256 - 16 - 32,
                GlobalSettings.RESOLUTION_Y - 67));
        statusUIElements.add(addMonsterHUD(GlobalSettings.RESOLUTION_X - 256 - 16, GlobalSettings
                .RESOLUTION_Y - 100));
        statusUIElements.add(addMonsterHUD(GlobalSettings.RESOLUTION_X - 256 - 16 - 64, GlobalSettings
                .RESOLUTION_Y - 36));

        // Battle HUD Monster Indicators
        indicatorOpp = new Image(skin.getDrawable("indicator"));
        indicatorOpp.setPosition(IndPos.OPPO_MID.x, IndPos.OPPO_MID.y, Align.center);
        indicatorHero = new Image(skin.getDrawable("indicator"));
        indicatorHero.setPosition(IndPos.HERO_MID.x, IndPos.HERO_MID.y, Align.center);

        monsterStatusUI.addActor(indicatorOpp);
        monsterStatusUI.addActor(indicatorHero);

    }

    private void setUpGameOverUI() {
        Image i = new Image(game.media.getBattleUITextureAtlas().findRegion("b12.down"));
        i.setWidth(542);
        i.setHeight(64);
        i.setPosition(GlobalSettings.RESOLUTION_X / 2, 72, Align.bottom);
        gameOverUI.addActor(i);

        Label.LabelStyle labs = new Label.LabelStyle();
        labs.font = skin.getFont("default-font");
        gameOverLabel = new Label("Game Over", labs);
        gameOverLabel.setHeight(64);
        gameOverLabel.setWidth(500);
        gameOverLabel.setWrap(true);
        gameOverLabel.setPosition(400, 70, Align.bottom);
        gameOverUI.addActor(gameOverLabel);

        // Change Screen
        TextButton.TextButtonStyle ibs = new TextButton.TextButtonStyle();
        ibs.down = new TextureRegionDrawable(game.media.getUITextureAtlas().findRegion("b1down"));
        ibs.up = new TextureRegionDrawable(game.media.getUITextureAtlas().findRegion("b1up"));
        ibs.font = skin.getFont("default-font");
        ibs.pressedOffsetY = -1;
        final TextButton exitButton = new TextButton("OK",ibs);
        exitButton.setWidth(128);exitButton.setHeight(48);
        exitButton.setPosition(GlobalSettings.RESOLUTION_X / 2, 32, Align.center);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Exit Battle Screen
                blackCourtain.addAction(Actions.sequence(
                        Actions.visible(true), Actions.alpha(1, .5f), Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                game.setScreen(gameScreen);
                            }
                        })
                ));
            }
        });
        gameOverUI.addActor(exitButton);
        gameOverUI.setVisible(false);
    }

    private void setUpMonsterImages() {
        Image monImg;

        // Hero Team
        monImg = new Image(game.media.getBattleUITextureAtlas().findRegion("black"));
        monImg.setWidth(128);monImg.setHeight(128);monImg.setPosition(120, 212);
        monImg.addAction(Actions.forever(Actions.sequence(
                Actions.moveBy(0, 2, .5f), Actions.moveBy(0, -2, .5f))));
        monImg.setVisible(false);
        monsterImgs.put(BatPos.HERO_TOP, monImg);

        monImg = new Image(game.media.getBattleUITextureAtlas().findRegion("black"));
        monImg.setWidth(128);monImg.setHeight(128);monImg.setPosition(64,176);
        monImg.addAction(Actions.forever(Actions.sequence(
                Actions.moveBy(0, -2, .5f), Actions.moveBy(0, 2, .5f))));
        monImg.setVisible(false);
        monsterImgs.put(BatPos.HERO_MID, monImg);

        monImg = new Image(game.media.getBattleUITextureAtlas().findRegion("black"));
        monImg.setWidth(128);monImg.setHeight(128);monImg.setPosition(8, 140);
        monImg.addAction(Actions.forever(Actions.sequence(
                Actions.moveBy(0, 2, .5f), Actions.moveBy(0, -2, .5f))));
        monImg.setVisible(false);
        monsterImgs.put(BatPos.HERO_BOT, monImg);


        // Opponent Team
        monImg = new Image(game.media.getBattleUITextureAtlas().findRegion("black"));
        monImg.setWidth(128);
        monImg.setHeight(128);monImg.setPosition(GlobalSettings.RESOLUTION_X-120-128,212);
        monImg.addAction(Actions.forever(Actions.sequence(
                Actions.moveBy(0, 2, .5f), Actions.moveBy(0, -2, .5f))));
        monImg.setVisible(false);
        monsterImgs.put(BatPos.OPPO_TOP, monImg);

        monImg = new Image(game.media.getBattleUITextureAtlas().findRegion("black"));
        monImg.setWidth(128);monImg.setHeight(128);
        monImg.setPosition(GlobalSettings.RESOLUTION_X - 64 - 128, 176);
        monImg.addAction(Actions.forever(Actions.sequence(
                Actions.moveBy(0, -2, .5f), Actions.moveBy(0, 2, .5f))));
        monImg.setVisible(false);
        monsterImgs.put(BatPos.OPPO_MID, monImg);

        monImg = new Image(game.media.getBattleUITextureAtlas().findRegion("black"));
        monImg.setWidth(128);monImg.setHeight(128);
        monImg.setPosition(GlobalSettings.RESOLUTION_X-8-128,140);
        monImg.addAction(Actions.forever(Actions.sequence(
                Actions.moveBy(0, 2, .5f), Actions.moveBy(0, -2, .5f))));
        monImg.setVisible(false);
        monsterImgs.put(BatPos.OPPO_BOT, monImg);
    }

    /**
     * Make attack button visible as soon as a monster gets ready again
     * @param attackerPos
     */
    private void activateButton(int attackerPos) {
        if(monsterReady.get(attackerPos) == true) {
            battleButtons.get("attack").setDisabled(false);
            battleButtons.get("attack").addAction(Actions.sequence(Actions.alpha(1,.2f)));
        }
        else {
            battleButtons.get("attack").setDisabled(true);
            battleButtons.get("attack").addAction(Actions.sequence(Actions.alpha(0.5f, .2f)));
        }
        setUpAttacksPane();
    }


    /**
     * Check whether hero lost combat
     */
    public void handleEndOfBattle() {
        aiPlayer.havePause(true);

        boolean heroLost = true;
        for(Monster m : team)
            if(m.HP > 0) heroLost = false;

        // Hide UI Elements
        battleActionMenu.addAction(Actions.sequence(
                Actions.fadeOut(1), Actions.visible(false), Actions.alpha(1)
        ));
        topLevelMenu.addAction(Actions.sequence(
                Actions.fadeOut(1), Actions.visible(false), Actions.alpha(1)
        ));

        // Activate Label
        gameOverUI.setVisible(true);

        // Set message
        if(heroLost) gameOverLabel.setText("Game Over");
        else gameOverLabel.setText("You won!");
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
            int i = 0;
            for(Monster m : team) {
                if(m.HP == 0) kickOutMonster(i);
                i++;
            }

            i = 3;
            for(Monster m : opponentTeam) {
                if(m.HP == 0) kickOutMonster(i);
                i++;
            }
        }

    }

    /**
     * Setting up the main menu in battle mode
     *  Fight
     *  Escape
     */
    private void setUpTopLevelMenu() {

        this.topLevelMenu = new Group();
        this.topLevelMenu.setWidth(GlobalSettings.RESOLUTION_X);
        this.topLevelMenu.setHeight(GlobalSettings.RESOLUTION_Y / 4);

        // Fight Button
        ImageButton ib = new ImageButton(skin.getDrawable("textfield"));
        ib.setWidth(140f);
        ib.setHeight(112f);
        ib.setPosition(400, 0, Align.bottom);
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                topLevelMenu.addAction(
                        Actions.sequence(Actions.alpha(0, .3f), Actions.visible(false)));
                battleActionMenu.addAction(
                        Actions.sequence(Actions.visible(true), Actions.alpha(1, .5f)));
                System.out.println("Button: fight");
                aiPlayer.havePause(false);
            }
        });
        topLevelMenuButtons.put("fight", ib);

        // Escape Button
        ib = new ImageButton(skin.getDrawable("textfield"));
        ib.setWidth(148f);
        ib.setHeight(112f);
        ib.setPosition(527, 0, Align.bottom);
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                blackCourtain.addAction(Actions.sequence(Actions.visible(true),Actions.fadeIn(1),
                        Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(gameScreen);
                    }
                })));
                System.out.println("Button: escape");
            }
        });
        topLevelMenuButtons.put("escape", ib);

        // Bag Button
        ib = new ImageButton(skin.getDrawable("textfield"));
        ib.setWidth(140f);
        ib.setHeight(112f);
        ib.setPosition(276, 0, Align.bottom);
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO
                System.out.println("Button: bag");
            }
        });
        topLevelMenuButtons.put("bag", ib);

        // Add buttons to the group
        for(String key : topLevelMenuButtons.keys())
            topLevelMenu.addActor(topLevelMenuButtons.get(key));

        // Set Button Styles
        ImageButton.ImageButtonStyle ibs = new ImageButton.ImageButtonStyle();
        ibs.down = new TextureRegionDrawable(
                game.media.getBattleUITextureAtlas().findRegion("b_fight_down"));
        ibs.up = new TextureRegionDrawable(
                game.media.getBattleUITextureAtlas().findRegion("b_fight"));
        topLevelMenuButtons.get("fight").setStyle(ibs);

        ibs = new ImageButton.ImageButtonStyle();
        ibs.down = new TextureRegionDrawable(
                game.media.getBattleUITextureAtlas().findRegion("b_flee_down"));
        ibs.up = new TextureRegionDrawable(
                game.media.getBattleUITextureAtlas().findRegion("b_flee"));
        topLevelMenuButtons.get("escape").setStyle(ibs);

        ibs = new ImageButton.ImageButtonStyle();
        ibs.down = new TextureRegionDrawable(
                game.media.getBattleUITextureAtlas().findRegion("b_bag_down"));
        ibs.up = new TextureRegionDrawable(
                game.media.getBattleUITextureAtlas().findRegion("b_bag"));
        topLevelMenuButtons.get("bag").setStyle(ibs);
    }

    /**
     * Settings up all elements for the battle action menu
     */
    private void setUpBattleActionMenu() {

        this.battleActionMenu = new Group();
        this.battleActionMenu.setWidth(GlobalSettings.RESOLUTION_X);
        this.battleActionMenu.setHeight(GlobalSettings.RESOLUTION_Y / 4);
        this.battleActionMenu.setVisible(false);

        // Images ..................................................................................
        this.attackScrollPaneBg =
                new Image(game.media.getBattleUITextureAtlas().findRegion("attPane"));
        attackScrollPaneBg.setPosition(400, 0, Align.bottom);
        attackScrollPaneBg.setWidth(588);
        attackScrollPaneBg.setHeight(136);
        attackScrollPaneBg.setVisible(true);
        attackPaneGroup.addActor(attackScrollPaneBg);

        // Attack Pane .............................................................................
        this.attackVGroup = new VerticalGroup();
        this.attackScroll = new ScrollPane(attackVGroup);
        attackScroll.setHeight(32);
        attackScroll.setPosition(0, 240);
        attackScroll.setHeight(136);
        attackScroll.setWidth(500);
        attackScroll.setPosition(400, 0, Align.bottom);
        attackPaneGroup.addActor(attackScroll);

        Image i = new Image(game.media.getBattleUITextureAtlas().findRegion("b12.down"));
        i.setWidth(542);
        i.setHeight(64);
        i.setPosition(GlobalSettings.RESOLUTION_X / 2, 72, Align.bottom);
        this.battleMenuImgs.put("infoLabelBg", i);

        Label.LabelStyle labs = new Label.LabelStyle();
        labs.font = skin.getFont("default-font");
        infoLabel = new Label("A monster attacks you!", labs);
        infoLabel.setHeight(64);
        infoLabel.setWidth(500);
        infoLabel.setWrap(true);
        infoLabel.setPosition(400, 70, Align.bottom);


        // Buttons .................................................................................
        // Back to Menu Button
        ImageButton ib = new ImageButton(skin.getDrawable("textfield"));
        ImageButton.ImageButtonStyle ibs = new ImageButton.ImageButtonStyle();
        ibs.down = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion("b6down"));
        ibs.up = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion("b6up"));
        ib.setStyle(ibs);

        ib.setWidth(60f);
        ib.setHeight(72f);
        ib.setPosition(680, 0, Align.bottomLeft);

        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                battleActionMenu.addAction(
                        Actions.sequence(Actions.alpha(0, .3f), Actions.visible(false)));
                topLevelMenu.addAction(
                        Actions.sequence(Actions.visible(true), Actions.alpha(1, .5f)));
                System.out.println("Button: back to top level menu");
                aiPlayer.havePause(true);
            }
        });
        battleActionMenu.addActor(ib);

        // Create indicator buttons
        // ....................................................................... HERO INDICATOR UP
        ibs = new ImageButton.ImageButtonStyle();
        ibs.down = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b1down"));
        ibs.up = new TextureRegionDrawable(game.media.getBattleUITextureAtlas().findRegion
                ("b1up"));
        ib = new ImageButton(ibs);
        ib.setWidth(52);ib.setHeight(64);
        ib.setPosition(0, 64);
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                chosenTeamMonster = heroPosQueue.getNext();
                changeIndicatorPosition(true, chosenTeamMonster);
                System.out.println("HEROQUEUE: " + chosenTeamMonster);
            }
        });
        battleButtons.put("indHeroUp", ib);

        // ..................................................................... HERO INDICATOR DOWN
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
                chosenTeamMonster = heroPosQueue.getPrevious();
                changeIndicatorPosition(true, chosenTeamMonster);
                System.out.println("HEROQUEUE: " + chosenTeamMonster);
            }
        });
        battleButtons.put("indHeroDown", ib);

        // Opponent Indicator
        // ....................................................................... OPPO INDICATOR UP
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
                chosenTarget = opponentPosQueue.getNext();
                changeIndicatorPosition(false, chosenTarget);
                System.out.println("OPPOQUEUE: " + chosenTarget);
            }
        });
        battleButtons.put("indOppUp", ib);

        // ..................................................................... OPPO INDICATOR DOWN
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
                chosenTarget = opponentPosQueue.getPrevious();
                changeIndicatorPosition(false, chosenTarget);
                System.out.println("OPPOQUEUE: " + chosenTarget);
            }
        });
        battleButtons.put("indOppDown", ib);


        // Left Button .............................................................................
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


        // Right Button.............................................................................
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


        // Bottom Button ...........................................................................
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


        // Attack Button ...........................................................................
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
                if(battleButtons.get("attack").isDisabled()) return;
                System.out.println("Button: attack");
                battleButtons.get("attack").setDisabled(true);
                battleButtons.get("attack").addAction(Actions.sequence(Actions.alpha(0.5f, .2f)));
                attackPaneGroup.addAction(Actions.sequence(Actions.visible(true),Actions.fadeIn(.3f)));
                setUpAttacksPane();
            }
        });
        battleButtons.put("attack", tb);


        // Button 5 ................................................................................
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

        for(String key : battleMenuImgs.keys()) battleActionMenu.addActor(battleMenuImgs.get(key));
        for(String s : battleButtons.keys()) battleActionMenu.addActor(battleButtons.get(s));

        battleActionMenu.addActor(infoLabel);
        battleActionMenu.addActor(attackPaneGroup);
    }

    /**
     * Changes the indicators position of the given team to the given position
     * @param herosTeam
     * @param pos
     */
    public void changeIndicatorPosition(boolean herosTeam, int pos) {
        if(herosTeam) {
            switch(pos) {
                case 2: indicatorHero.setPosition(IndPos.HERO_TOP.x, IndPos.HERO_TOP.y, Align.center);break;
                case 1: indicatorHero.setPosition(IndPos.HERO_BOT.x, IndPos.HERO_BOT.y, Align.center);break;
                default: indicatorHero.setPosition(IndPos.HERO_MID.x, IndPos.HERO_MID.y, Align.center);break;
            }
            activateButton(pos);
            chosenTeamMonster=pos;
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
            chosenTarget=pos;
        }
    }

    /**
     * Initializes Attributes and especially Arrays and Maps
     */
    public void initializeAttributes() {
        this.monsterInformation = MonsterInformation.getInstance();
        this.allKO = false;

        this.heroPosQueue = new BattlePositionQueue(3);
        this.opponentPosQueue = new BattlePositionQueue(3);

        this.monsterImgs = new ArrayMap<Integer,Image>();

        this.progressBars = new ArrayMap<String, Array<ProgressBar>>();
        progressBars.put("HP", new Array<ProgressBar>());
        progressBars.put("MP", new Array<ProgressBar>());
        progressBars.put("Recov", new Array<ProgressBar>());
        progressBars.put("EXP", new Array<ProgressBar>());

        this.monScreenElems = new ArrayMap<String, Array<Image>>();
        this.monScreenElems.put("nameSigns", new Array<Image>());
        this.monScreenElems.put("hudBgs", new Array<Image>());
        this.monScreenElems.put("rings", new Array<Image>());
        this.monScreenElems.put("ringBgs", new Array<Image>());

        this.battleMenuImgs = new ArrayMap<String, Image>();

        this.battleButtons = new ArrayMap<String, Button>();
        this.topLevelMenuButtons = new ArrayMap<String, ImageButton>();
        this.monsterLabels = new Array<Label>();
        this.monsterLvls = new Array<Label>();

        this.battleQueue = new Array<AttackAction>();
        this.waitingSince = new Array<Long>();
        for(int i=0;i<6;i++) waitingSince.add(new Long(0));


        this.team = new Array<Monster>();
        this.opponentTeam = new Array<Monster>();

        this.monsterReady = new Array<Boolean>();
        for(int i=0;i<6;i++) monsterReady.add(new Boolean(true));
        this.monsterKO = new Array<Boolean>();
        for(int i=0;i<6;i++) monsterKO.add(new Boolean(false));

        this.attackPaneGroup = new Group();
        this.gameOverUI = new Group();

        this.aiPlayer = new AIPlayer(this.opponentTeam,this.team);
    }


    /* .................................................................... CREATE UI ELEMENTS .. */
    /**
     * Adds monster status UI at the given position
     * @param x
     * @param y
     */
    private Group addMonsterHUD(int x, int y) {
        Group statusUI = new Group();
        ProgressBar.ProgressBarStyle HPbarStyle = new ProgressBar.ProgressBarStyle();
        HPbarStyle.background = skin.getDrawable("invis");
        HPbarStyle.knobBefore = skin.getDrawable("HP-slider");
        ProgressBar.ProgressBarStyle MPbarStyle = new ProgressBar.ProgressBarStyle();
        MPbarStyle.background = skin.getDrawable("invis");
        MPbarStyle.knobBefore = skin.getDrawable("MP-slider");
        ProgressBar.ProgressBarStyle RecovBarStyle = new ProgressBar.ProgressBarStyle();
        RecovBarStyle.background = skin.getDrawable("invis");
        RecovBarStyle.knobBefore = skin.getDrawable("red-slider-vert");
        ProgressBar.ProgressBarStyle ExpBarStyle = new ProgressBar.ProgressBarStyle();
        ExpBarStyle.background = skin.getDrawable("invis");
        ExpBarStyle.knobBefore = skin.getDrawable("yellow-slider-hor");

        Image hudBg;
        hudBg = new Image();
        hudBg.setDrawable(skin, "hud-bg");
        hudBg.setPosition(x + 132, y + 5, Align.center);
        hudBg.setHeight(22);
        hudBg.setWidth(78);
        monScreenElems.get("hudBgs").add(hudBg);

        Image ringBg = new Image();
        ringBg.setDrawable(skin, "ring-bg");
        ringBg.setPosition(x + 112, y + 1, Align.center);
        ringBg.setHeight(24);
        ringBg.setWidth(24);
        monScreenElems.get("ringBgs").add(ringBg);

        Image ring = new Image();
        ring.setDrawable(skin, "ring");
        ring.setPosition(x + 108, y - 3, Align.center);
        ring.setHeight(32);
        ring.setWidth(32);
        monScreenElems.get("rings").add(ring);

        Image nameSign = new Image();
        nameSign.setDrawable(skin, "hud-bg3");
        nameSign.setPosition(x - 14, y + 5);
        nameSign.setWidth(200);
        nameSign.setHeight(22);
        monScreenElems.get("nameSigns").add(nameSign);

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
        ep.setPosition(x - 9, y);
        ep.setWidth(120);
        ep.setValue(100);

        hp.setAnimateInterpolation(Interpolation.linear);
        hp.setAnimateDuration(1f);
        rp.setAnimateInterpolation(Interpolation.linear);
        rp.setAnimateDuration(.1f);
        ep.setAnimateInterpolation(Interpolation.linear);
        ep.setAnimateDuration(.1f);

        progressBars.get("HP").add(hp);
        progressBars.get("MP").add(mp);
        progressBars.get("Recov").add(rp);
        progressBars.get("EXP").add(ep);

        ls.font = skin.getFont("white");
        Label lvl = new Label("0", ls);
        lvl.setPosition(x + 125, y + 13, Align.center);
        monsterLvls.add(lvl);

        // Sorting
        statusUI.addActor(nameSign);
        statusUI.addActor(hudBg);
        statusUI.addActor(hp);
        statusUI.addActor(mp);
        statusUI.addActor(rp);
        statusUI.addActor(ep);
        statusUI.addActor(ringBg);
        statusUI.addActor(ring);
        statusUI.addActor(monsterLabel);
        statusUI.addActor(lvl);

        return statusUI;
    }

    public void setUpAttacksPane() {

        this.attackVGroup.clear();
        attackVGroup.setWidth(500);
        this.attackVGroup.space(4);

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
            attackVGroup.addActor(tb);
            tb.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {

                    // ----------------------------------------------------------------------- DEBUG
                    DebugOutput.printAttack(
                            team.get(chosenTeamMonster),
                            chosenTeamMonster,
                            opponentTeam.get(chosenTarget),
                            chosenTarget,
                            team.get(chosenTeamMonster).attacks.get(0),
                            opponentTeam.get(chosenTarget).HP
                                    - team.get(chosenTeamMonster).attacks.get(0).damage);
                    // ----------------------------------------------------------------------- DEBUG

                    // Animate Attack
                    animateAttack(chosenTeamMonster,chosenTarget+3);

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
                    progressBars.get("HP").get(chosenTarget + 3).setValue(100 * opponentTeam.get
                            (chosenTarget).HP
                            / opponentTeam.get(chosenTarget).HPfull);

                    // Handle Attack
                    handleAttack(team.get(chosenTeamMonster), opponentTeam.get(chosenTarget));

                /* Check if all enemies are KO */
                    allKO = true;
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
                    attackPaneGroup.addAction(Actions.sequence(Actions.fadeOut(.3f),
                            Actions.visible(false)));
                }
            });
        }
        attackVGroup.addActor(new TextButton("1", tbs));
        attackVGroup.addActor(new TextButton("2", tbs));
        attackVGroup.addActor(new TextButton("3", tbs));
        attackVGroup.addActor(new TextButton("4", tbs));
        attackVGroup.addActor(new TextButton("5", tbs));
        attackVGroup.addActor(new TextButton("6", tbs));
    }


    /* ......................................................................... BATTLE METHODS ..*/
    /**
     * Removes the monster with the given position on the battle field by setting it KO and
     * fading out the monster sprite and monsters status UI
     * @param pos   position on which the beaten monster was
     */
    private void kickOutMonster(int pos) {
        monsterImgs.get(pos).addAction(
                Actions.sequence(Actions.alpha(0, 2), Actions.visible(false)));
        statusUIElements.get(pos).addAction(
                Actions.sequence(Actions.alpha(0, 2), Actions.visible(false)));
        monsterKO.set(pos, true);
        System.out.println("Killed: " + pos);

        // Change the indicator position to an active fighter
        if(pos<3) {
            // Hero Team
            int p = heroPosQueue.remove(pos);
            System.out.println("KILL QUEUE: " + p);
            if(p>=0)changeIndicatorPosition(true, p);
        }
        if(pos>2) {
            // Opponent Team
            int p =  opponentPosQueue.remove(pos-3);
            System.out.println("KILL QUEUE: " + p);
            if(p>=0)changeIndicatorPosition(false, p);
        }

    }

    /**
     * Animate an attack of the given monster
     * @param attPos    position of attacker
     * @param defPos    position of defender
     */
    private void animateAttack(int attPos, int defPos) {
        Image attIm,defIm;
        IntVector2 startPos,endPos;

        switch(attPos) {
            case 5:  startPos = ImPos.OPPO_TOP;break;
            case 4:  startPos = ImPos.OPPO_BOT;break;
            case 3:  startPos = ImPos.OPPO_MID;break;
            case 2:  startPos = ImPos.HERO_TOP;break;
            case 1:  startPos = ImPos.HERO_BOT;break;
            default: startPos = ImPos.HERO_MID;break;
        }

        switch(defPos) {
            case 5:  endPos = ImPos.OPPO_TOP;break;
            case 4:  endPos = ImPos.OPPO_BOT;break;
            case 3:  endPos = ImPos.OPPO_MID;break;
            case 2:  endPos = ImPos.HERO_TOP;break;
            case 1:  endPos = ImPos.HERO_BOT;break;
            default: endPos = ImPos.HERO_MID;break;
        }


        attIm = monsterImgs.get(attPos);
        attIm.addAction(Actions.sequence(
                Actions.moveTo(endPos.x, endPos.y, .6f, Interpolation.pow2In),
                Actions.moveTo(startPos.x, startPos.y, .3f, Interpolation.pow2Out)
        ));
        defIm = monsterImgs.get(defPos);
        defIm.addAction(Actions.sequence(
                Actions.delay(.6f), Actions.moveBy(0, 15, .1f, Interpolation.bounceIn),
                Actions.moveBy(0,-15,.1f,Interpolation.bounceIn)
        ));

    }


    /* ..................................................................... GETTERS & SETTERS .. */

    /* ......................................................................... INNER CLASSES .. */

    private class AIPlayer {
        private Array<Monster> AIteam, AIoppTeam;
        private int AIchosenMember, AIchosenTarget;
        private Array<Integer> AIteamPositions,AIopponentPositions;
        private boolean AIpaused;

        public AIPlayer(Array<Monster> team, Array<Monster> oppTeam) {
            this.AIteam = team;
            this.AIoppTeam = oppTeam;
            AIchosenMember = 0;
            AIchosenTarget = 0;
            AIpaused = false;

            // Remember Team Positions
            AIteamPositions = new Array<Integer>();
            int i=0;
            for(Monster m : this.AIteam) {
                if(m.HP > 0) AIteamPositions.add(i);
                i++;
            }

            // Remember Opponent Positions
            AIopponentPositions = new Array<Integer>();
            int j=0;
            for(Monster m : AIoppTeam) {
                if(m.HP > 0) AIopponentPositions.add(j);
                j++;
            }
            System.out.println(AIopponentPositions.size);
        }

        public void act() {
            if(AIpaused) return;

            // Remove KO monsters
            Iterator<Integer> it1 = AIteamPositions.iterator();
            while(it1.hasNext()) {
                if(AIteam.get(it1.next()).HP == 0) it1.remove();
            }

            Iterator<Integer> it2 = AIopponentPositions.iterator();
            while(it2.hasNext()) {
                if(AIoppTeam.get(it2.next()).HP == 0) it2.remove();
            }



            // Check if monster is ready
            for(int j=0;j<AIteam.size;j++)
                if(monsterReady.get(j+3) && AIteam.get(j).HP > 0) {
                    // Choose Target
                    AIchosenMember = j;
                    if(AIopponentPositions.size > 0)
                        AIchosenTarget = MathUtils.random(0,AIopponentPositions
                                .size-1);
                    System.out.println("#####################");
                    System.out.println(AIoppTeam);
                    System.out.println(AIchosenTarget);
                    System.out.println(AIteam);
                    System.out.println(AIchosenMember);
                    System.out.println("#####################");
                    AIoppTeam.get(AIchosenTarget).HP -= AIteam.get(AIchosenMember).attacks.get(
                            MathUtils.random(AIteam.get(AIchosenMember).attacks.size-1)
                    ).damage;

                    if(AIoppTeam.get(AIchosenTarget).HP<0) AIoppTeam.get(AIchosenTarget).HP=0;

                    System.out.println("Monster No. " + AIchosenMember + " is ready. Attacking "
                            + AIchosenTarget);


                    /* Make attacker wait */
                    waitingSince.set(AIchosenMember+3, TimeUtils.millis());
                    monsterReady.set(AIchosenMember+3, false);

                    /* Update Health Bar */
                    progressBars.get("HP").get(AIchosenTarget).setValue(100 * AIoppTeam.get
                            (AIchosenTarget).HP
                            / AIoppTeam.get(AIchosenTarget).HPfull);

                    // Handle Attack
                    handleAttack(AIteam.get(AIchosenMember), AIoppTeam.get(AIchosenTarget));

                    checkEnemiesDeath();
                }
        }

        public void havePause(boolean paused) {
            this.AIpaused = paused;
        }

        public void checkEnemiesDeath() {
            /* Check if all enemies are KO */
            allHeroKO = true;
            switch (AIoppTeam.size) {
                case 3:
                    allHeroKO = (AIoppTeam.get(2).HP == 0);
                    System.out.println("HeroMon 3: " + AIoppTeam.get(2).HP
                            + "/" + AIoppTeam.get(2).HPfull + " KO: " + allHeroKO);
                case 2:
                    allHeroKO = (allHeroKO == true && AIoppTeam.get(1).HP == 0);
                    System.out.println("HeroMon 2: " + AIoppTeam.get(1).HP
                            + "/" + AIoppTeam.get(1).HPfull + " KO: " + allHeroKO);
                default:
                    allHeroKO = (allHeroKO == true && AIoppTeam.get(0).HP == 0);
                    System.out.println("HeroMon 1: " + AIoppTeam.get(0).HP
                            + "/" + AIoppTeam.get(0).HPfull + " KO: " + allHeroKO);
                    break;
            }

            if(allHeroKO) handleEndOfBattle();
        }
    }

    /**
     * Possible Indicator coordinates
     */
    final static class IndPos {
        private static IntVector2 OPPO_TOP = new IntVector2(616, 340);
        private static IntVector2 OPPO_MID = new IntVector2(672, 304);
        private static IntVector2 OPPO_BOT = new IntVector2(728, 268);
        private static IntVector2 HERO_TOP = new IntVector2(184, 340);
        private static IntVector2 HERO_MID = new IntVector2(128, 304);
        private static IntVector2 HERO_BOT = new IntVector2(74, 268);
    }

    final static class ImPos {
        private static IntVector2 OPPO_TOP = new IntVector2(GlobalSettings.RESOLUTION_X-120-128,212);
        private static IntVector2 OPPO_MID = new IntVector2(GlobalSettings.RESOLUTION_X - 64 - 128, 176);
        private static IntVector2 OPPO_BOT = new IntVector2(GlobalSettings.RESOLUTION_X-8-128,140);
        private static IntVector2 HERO_TOP = new IntVector2(120, 212);
        private static IntVector2 HERO_MID = new IntVector2(64,176);
        private static IntVector2 HERO_BOT = new IntVector2(8, 140);
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
        private static int convertFromCounterToPosition(int counter) {
            return positions[counter];
        }
    }
}
