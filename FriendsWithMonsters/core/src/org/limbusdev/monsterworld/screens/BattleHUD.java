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

import org.limbusdev.monsterworld.FriendsWithMonsters;
import org.limbusdev.monsterworld.ecs.components.TeamComponent;
import org.limbusdev.monsterworld.enums.SFXType;
import org.limbusdev.monsterworld.geometry.IntVector2;
import org.limbusdev.monsterworld.model.Attack;
import org.limbusdev.monsterworld.model.BattlePositionQueue;
import org.limbusdev.monsterworld.model.Monster;
import org.limbusdev.monsterworld.model.MonsterInformation;
import org.limbusdev.monsterworld.ui.MonsterStateWidget;
import org.limbusdev.monsterworld.utils.GS;
import org.limbusdev.monsterworld.utils.MonsterManager;


/**
 * BattleHUD manages all actions and UI elements in the {@link BattleScreen}
 *
 * Created by georg on 03.12.15.
 */
public class BattleHUD {

    /* ............................................................................ ATTRIBUTES .. */
    private final OutdoorGameWorldScreen gameScreen;
    private final FriendsWithMonsters game;

    private MonsterInformation monsterInformation;

    // ------------------------------------------------------------------------------------ SCENE 2D
    public  Stage stage;
    private Skin skin, battleSkin;

    // Groups
    private Array<Group> statusUIElements;                      // status bars above monster
    private Group battleActionMenu, topLevelMenu, monsterStatusUI, attackPaneGroup, gameOverUI;
    private VerticalGroup attackVGroup;                         // Group of attack buttons

    // Buttons
    private ArrayMap<String,Button> battleMenuButtons;          // buttons of battle menu
    private ArrayMap<String,ImageButton> topLevelMenuButtons;   // buttons of the top level menu

    // Labels
    private Label infoLabel, gameOverLabel;
    private Array<Label> monsterLabels, monsterLvls;

    // Images
    private ArrayMap<String,Array<Image>> monScreenElems;       // elements of status bars
    private Image indicatorOpp, indicatorHero, battleUIbg, attackScrollPaneBg, blackCourtain;
    private ArrayMap<String, Image> battleMenuImgs;
    private ArrayMap<Integer,Image> monsterImgs;

    // ProgressBars
    private ArrayMap<String,Array<ProgressBar>> progressBars;   // HP, MP, EXP, Waiting

    // Scroll Panes
    private ScrollPane attackScroll;


    // --------------------------------------------------------------------------------------- OTHER
    // Not Scene2D related
    private Array<Monster> team, opponentTeam;      // hold monsters of one team
    private Array<Monster> attackerQueue;

    private int chosenTarget=BatPos.OPPO_MID;            // position of active target
    private int chosenTeamMonster=BatPos.MID;       // position of active attacker

    private int indicatorHeroPos=0;                 // position of the indicator
    private boolean allKO, allHeroKO;               // whether a whole team is KO

    private BattlePositionQueue heroPosQueue, opponentPosQueue;

    private AIPlayer aiPlayer;                      // Artificial Intelligence

    /* ........................................................................... CONSTRUCTOR .. */
    public BattleHUD(final FriendsWithMonsters game, final OutdoorGameWorldScreen gameScreen) {
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

        MonsterStateWidget statW = new MonsterStateWidget(battleSkin);
        statW.setPosition(400,400);
        stage.addActor(statW);

        reset();
    }
    /* ............................................................................... METHODS .. */

    // ################################################################################### GAME LOOP

    /**
     * Update the HUD
     * @param delta
     */
    public void update(float delta) {
        stage.act(delta);
        aiPlayer.act();
        updateMonsters();
        updateAttackerQueue();

        // ........................................................................... END OF BATTLE
        if (allKO) handleEndOfBattle();

        reActivateMonsters();
        updateExpBars();
    }

    /**
     * Draw the HUD to the screen
     */
    public void draw() {
        this.stage.draw();
    }

    public void updateMonsters() {
        for(Monster m : this.team)         m.update();
        for(Monster m : this.opponentTeam) m.update();
        updateRecovBars(team);
        updateRecovBars(opponentTeam);
    }

    /**
     * Updates the recovering bars as visual feedback how long a monster has to wait
     * @param team
     */
    private void updateRecovBars(Array<Monster> team) {
        for(Monster m : team) {
            if(!m.ready) {
                // Update Waiting Bar
                progressBars.get("Recov").get(m.battleFieldPosition).setValue(
                        TimeUtils.timeSinceMillis(m.waitingSince)
                                / (1f * m.recovTime) * 100f);
            }
        }
    }

    private void updateExpBars() {
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

    /**
     * Sets Attack button active as soon as monsters have recovered
     */
    private void reActivateMonsters() {
        // .............................................................................. RECOVERING
        // Activate monsters if they have recovered
        for(int i=0; i<6; i++) {
            if (i < 3 && (
                    team.get(i).ready
                            && !team.get(i).waitingInQueue
                            && !team.get(i).attackingRightNow
                            && battleMenuButtons.get("attack").isDisabled())
                    && i == chosenTeamMonster) {
                System.out.println("Setting Attack Button active");
                battleMenuButtons.get("attack").setDisabled(false);
                battleMenuButtons.get("attack").addAction(
                        Actions.sequence(Actions.alpha(1, .2f)));
            }
        }
    }

    /**
     * Handles all monsters in the queue and starts/finishes their attacks
     */
    public void updateAttackerQueue() {
        if(attackerQueue.size != 0) {
            Monster firstInQueue = attackerQueue.first();
            // If first in queue is not attacking right now
            if(firstInQueue.attackingRightNow) {
                // If first in queue had enough time to finish attack
                if(TimeUtils.timeSinceMillis(firstInQueue.attackStarted)
                        > firstInQueue.getNextAttackDuration()) {
                    // Remove him from the queue
                    firstInQueue.finishAttack();
                    attackerQueue.removeIndex(0);
                }
            }
            // If it is waiting
            else {
                carryOutAttack(firstInQueue);
            }
        }
    }


    // #############################################################################################

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // ............................................................................ INITIALIZE ARENA

    /**
     * Resets the UI into a state where it can be initialized for a new battle
     */
    public void reset() {
        // Attributes
        this.chosenTarget = BatPos.OPPO_MID;
        this.chosenTeamMonster = BatPos.MID;

        for (String key : progressBars.keys())
            for(ProgressBar b : progressBars.get(key)) b.setValue(100);

        indicatorHeroPos=0;
        allKO = false;
        allHeroKO = false;

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
        this.heroPosQueue = new BattlePositionQueue(team.monsters.size, true);
        this.opponentPosQueue = new BattlePositionQueue(opponentTeam.monsters.size, false);

        //Initialize Teams .........................................................................
        // Hero Team
        this.team = team.monsters;
        changeIndicatorPosition(true, BatPos.MID);

        // Choose fit monsters for battle
        int i=0;
        for(Monster m : team.monsters) {
            if (m.HP <= 0) {
                m.KO = true;
                kickOutMonster(i);
            }
            i++;
        }

        // Opponent Team
        changeIndicatorPosition(false, BatPos.OPPO_MID);
        this.opponentTeam = opponentTeam.monsters;

        this.aiPlayer = new AIPlayer(this.opponentTeam,this.team);
        this.aiPlayer.havePause(true);

        // Initialize Monsters for Battle
        for(int j=0;j<this.team.size;j++) this.team.get(j).initBattle(j);
        for(int j=0;j<this.opponentTeam.size;j++) this.opponentTeam.get(j).initBattle(j+3);


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

        show();
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

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // .............................................................................. LIBGDX METHODS
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
     * Make attack button visible as soon as a monster gets ready again
     * @param attackerPos
     */
    private void activateButton(int attackerPos) {
        if(!team.get(attackerPos).ready ||
                team.get(attackerPos).waitingInQueue ||
                team.get(attackerPos).attackingRightNow) {
            battleMenuButtons.get("attack").setDisabled(true);
            battleMenuButtons.get("attack").addAction(Actions.sequence(Actions.alpha(0.5f, .2f)));
        }
        else {
            battleMenuButtons.get("attack").setDisabled(false);
            battleMenuButtons.get("attack").addAction(Actions.sequence(Actions.alpha(1,.2f)));
        }
        setUpAttacksPane();
    }


    /**
     * Handle End of Battle
     */
    public void handleEndOfBattle() {
        // Stop AI
        aiPlayer.havePause(true);

        // Check if Hero lost fight
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
        // Remove killed monsters
        if(def.HP == 0) kickOutMonster(def.battleFieldPosition);

        // Spread EXP
        if(def.battleFieldPosition > 2 && def.HP == 0) {
            // Defeated Monster was part of opponents team
            int exp = def.level * (def.HPfull + def.physStrength);
            exp /= team.size;
            for (Monster m : team) if (m.HP > 0) MonsterManager.earnEXP(m,exp);
        }
    }

    /**
     * Changes the indicators position of the given team to the given position
     * @param heroesTeam
     * @param pos
     */
    public void changeIndicatorPosition(boolean heroesTeam, int pos) {
        if(heroesTeam) {
            switch(pos) {
                case 2: indicatorHero.setPosition(IndPos.HERO_TOP.x, IndPos.HERO_TOP.y, Align.center);break;
                case 1: indicatorHero.setPosition(IndPos.HERO_BOT.x, IndPos.HERO_BOT.y, Align.center);break;
                default: indicatorHero.setPosition(IndPos.HERO_MID.x, IndPos.HERO_MID.y, Align.center);break;
            }
            chosenTeamMonster=pos;
            activateButton(pos);
        } else {
            switch(pos) {
                case 5:
                    indicatorOpp.setPosition(IndPos.OPPO_TOP.x, IndPos.OPPO_TOP.y, Align.center);
                    break;
                case 4:
                    indicatorOpp.setPosition(IndPos.OPPO_BOT.x, IndPos.OPPO_BOT.y, Align.center);
                    break;
                default:
                    indicatorOpp.setPosition(IndPos.OPPO_MID.x, IndPos.OPPO_MID.y, Align.center);
                    break;
            }
            chosenTarget=pos;
        }
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // ....................................................................................... SETUP
    /**
     * Initializes Attributes and especially Arrays and Maps
     */
    public void initializeAttributes() {
        this.monsterInformation = MonsterInformation.getInstance();
        this.allKO = false;

        this.heroPosQueue = new BattlePositionQueue(3, true);
        this.opponentPosQueue = new BattlePositionQueue(3, false);

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

        this.battleMenuButtons = new ArrayMap<String, Button>();
        this.topLevelMenuButtons = new ArrayMap<String, ImageButton>();
        this.monsterLabels = new Array<Label>();
        this.monsterLvls = new Array<Label>();

        this.attackerQueue = new Array<Monster>();

        this.team = new Array<Monster>();
        this.opponentTeam = new Array<Monster>();

        this.attackPaneGroup = new Group();
        this.gameOverUI = new Group();

        this.aiPlayer = new AIPlayer(this.opponentTeam,this.team);
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
        FitViewport fit = new FitViewport(GS.RES_X, GS.RES_Y);
        this.stage = new Stage(fit);
        this.skin = game.media.skin;
        this.battleSkin = game.media.battleSkin;
        if(GS.DEBUGGING_ON) stage.setDebugAll(true);

        this.monsterStatusUI = new Group();
        this.statusUIElements = new Array<Group>();

        // Battle UI Black transparent Background
        this.battleUIbg = new Image(game.media.getBattleUITextureAtlas().findRegion("bg"));
        battleUIbg.setPosition(0, 0);
        battleUIbg.setSize(GS.RES_X, GS.ROW*14);

        // Black Courtain for fade-in and -out
        this.blackCourtain = new Image(game.media.getBattleUITextureAtlas().findRegion("black"));
        this.blackCourtain.setSize(GS.RES_X,GS.RES_Y);
        this.blackCourtain.setPosition(0, 0);

        // Hero Team ###############################################################################
        statusUIElements.add(addMonsterHUD(58, GS.RES_Y - 67));
        statusUIElements.add(addMonsterHUD(26, GS.RES_Y - 100));
        statusUIElements.add(addMonsterHUD(90, GS.RES_Y - 36));

        // Opponent Team ###########################################################################
        statusUIElements.add(addMonsterHUD(GS.RES_X - 256 - 16 - 32,
                GS.RES_Y - 67));
        statusUIElements.add(addMonsterHUD(GS.RES_X - 256 - 16, GS.RES_Y- 100));
        statusUIElements.add(addMonsterHUD(GS.RES_X - 256 - 16 - 64, GS.RES_Y - 36));

        // Battle HUD Monster Indicators
        indicatorOpp = new Image(skin.getDrawable("indicator"));
        indicatorOpp.setPosition(IndPos.OPPO_MID.x, IndPos.OPPO_MID.y, Align.center);
        indicatorHero = new Image(skin.getDrawable("indicator"));
        indicatorHero.setPosition(IndPos.HERO_MID.x, IndPos.HERO_MID.y, Align.center);

        monsterStatusUI.addActor(indicatorOpp);
        monsterStatusUI.addActor(indicatorHero);

    }

    private void setUpGameOverUI() {
        Image i = new Image(battleSkin.getDrawable("b-long-down"));
        i.setPosition(GS.RES_X / 2, GS.ROW*7, Align.bottom);
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
        exitButton.setPosition(GS.RES_X / 2, 32, Align.center);
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
        monImg.setHeight(128);monImg.setPosition(GS.RES_X-120-128,212);
        monImg.addAction(Actions.forever(Actions.sequence(
                Actions.moveBy(0, 2, .5f), Actions.moveBy(0, -2, .5f))));
        monImg.setVisible(false);
        monsterImgs.put(BatPos.OPPO_TOP, monImg);

        monImg = new Image(game.media.getBattleUITextureAtlas().findRegion("black"));
        monImg.setWidth(128);monImg.setHeight(128);
        monImg.setPosition(GS.RES_X - 64 - 128, 176);
        monImg.addAction(Actions.forever(Actions.sequence(
                Actions.moveBy(0, -2, .5f), Actions.moveBy(0, 2, .5f))));
        monImg.setVisible(false);
        monsterImgs.put(BatPos.OPPO_MID, monImg);

        monImg = new Image(game.media.getBattleUITextureAtlas().findRegion("black"));
        monImg.setWidth(128);monImg.setHeight(128);
        monImg.setPosition(GS.RES_X-8-128,140);
        monImg.addAction(Actions.forever(Actions.sequence(
                Actions.moveBy(0, 2, .5f), Actions.moveBy(0, -2, .5f))));
        monImg.setVisible(false);
        monsterImgs.put(BatPos.OPPO_BOT, monImg);
    }

    /**
     * Setting up the main menu in battle mode
     *  Fight
     *  Escape
     */
    private void setUpTopLevelMenu() {

        this.topLevelMenu = new Group();
        this.topLevelMenu.setWidth(GS.RES_X);
        this.topLevelMenu.setHeight(GS.RES_Y / 4);

        // Fight Button
        ImageButton ib = new ImageButton(battleSkin, "battle-fight");
        ib.setPosition(GS.RES_X/2, 0, Align.bottom);
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Input: Button Fight");
                topLevelMenu.addAction(
                        Actions.sequence(Actions.alpha(0, .3f), Actions.visible(false)));
                battleActionMenu.addAction(
                        Actions.sequence(Actions.visible(true), Actions.alpha(1, .5f)));
                aiPlayer.havePause(false);
            }
        });
        topLevelMenuButtons.put("fight", ib);

        // Escape Button
        ib = new ImageButton(battleSkin, "battle-flee");
        ib.setPosition(GS.RES_X - GS.ROW*20, 0, Align.bottomRight);
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Input: Button Escape");
                blackCourtain.addAction(Actions.sequence(Actions.visible(true),Actions.fadeIn(1),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                game.setScreen(gameScreen);
                            }
                        })));
            }
        });
        topLevelMenuButtons.put("escape", ib);

        // Bag Button
        ib = new ImageButton(battleSkin, "battle-bag");
        ib.setPosition(GS.ROW*20, 0, Align.bottomLeft);
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO
                System.out.println("Input: Button Bag");
            }
        });
        topLevelMenuButtons.put("bag", ib);

        // Add buttons to the group
        for(String key : topLevelMenuButtons.keys())
            topLevelMenu.addActor(topLevelMenuButtons.get(key));
    }

    /**
     * Settings up all elements for the battle action menu
     */
    private void setUpBattleActionMenu() {

        this.battleActionMenu = new Group();
        this.battleActionMenu.setSize(GS.RES_X,GS.RES_Y / 4);
        this.battleActionMenu.setVisible(false);

        // Images ..................................................................................
        this.attackScrollPaneBg = new Image(battleSkin.getDrawable("attPane"));
        attackScrollPaneBg.setPosition(GS.RES_X/2, 0, Align.bottom);
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

        Image i = new Image(battleSkin.getDrawable("b-long-down"));
        i.setPosition(GS.RES_X / 2, GS.ROW*7, Align.bottom);
        this.battleMenuImgs.put("infoLabelBg", i);

        Label.LabelStyle labs = new Label.LabelStyle();
        labs.font = skin.getFont("default-font");
        infoLabel = new Label("A monster attacks you!", labs);
        infoLabel.setHeight(GS.ROW*6);
        infoLabel.setWidth(GS.COL*40);
        infoLabel.setWrap(true);
        infoLabel.setPosition(GS.RES_X/2, GS.ROW*7, Align.bottom);


        // Buttons .................................................................................
        // Back to Menu Button
        ImageButton ib = new ImageButton(battleSkin, "b-back");
        ib.setPosition(GS.RES_X - GS.COL*5.5f, 0, Align.bottomRight);
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
        ib = new ImageButton(battleSkin, "scroll-up-l");
        ib.setPosition(0, GS.ROW*7, Align.bottomLeft);
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                chosenTeamMonster = heroPosQueue.getNext();
                changeIndicatorPosition(true, chosenTeamMonster);
                System.out.println("HEROQUEUE: " + chosenTeamMonster);
            }
        });
        battleMenuButtons.put("indHeroUp", ib);

        // ..................................................................... HERO INDICATOR DOWN
        ib = new ImageButton(battleSkin, "scroll-down-l");
        ib.setPosition(0, 0, Align.bottomLeft);
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                chosenTeamMonster = heroPosQueue.getPrevious();
                changeIndicatorPosition(true, chosenTeamMonster);
                System.out.println("HEROQUEUE: " + chosenTeamMonster);
            }
        });
        battleMenuButtons.put("indHeroDown", ib);

        // Opponent Indicator
        // ....................................................................... OPPO INDICATOR UP
        ib = new ImageButton(battleSkin, "scroll-up-r");
        ib.setPosition(GS.RES_X, GS.ROW*7, Align.bottomRight);
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                chosenTarget = opponentPosQueue.getNext();
                changeIndicatorPosition(false, chosenTarget);
                System.out.println("OPPOQUEUE: " + chosenTarget);
            }
        });
        battleMenuButtons.put("indOppUp", ib);

        // ..................................................................... OPPO INDICATOR DOWN
        ib = new ImageButton(battleSkin, "scroll-down-r");
        ib.setPosition(GS.RES_X, 0, Align.bottomRight);
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                chosenTarget = opponentPosQueue.getPrevious();
                changeIndicatorPosition(false, chosenTarget);
                System.out.println("OPPOQUEUE: " + chosenTarget);
            }
        });
        battleMenuButtons.put("indOppDown", ib);


        // Left Button .............................................................................
        ib = new ImageButton(battleSkin, "b-mouse-l");
        ib.setPosition(GS.COL*10.5f, 0, Align.bottomLeft);
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO
                System.out.println("Input: button mouse l");
            }
        });
        battleMenuButtons.put("button7", ib);


        // Right Button.............................................................................
        ib = new ImageButton(battleSkin, "b-mouse-r");
        ib.setPosition(GS.RES_X - GS.COL*10.5f, 0, Align.bottomRight);
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO
                System.out.println("Input: button mouse r");
            }
        });
        battleMenuButtons.put("button8", ib);


        // Attack Button ...........................................................................
        TextButton tb = new TextButton("Attack", battleSkin, "tb-attack");
        tb.setPosition(GS.RES_X/2, 0, Align.bottom);
        tb.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(battleMenuButtons.get("attack").isDisabled()) return;
                System.out.println("Button: attack");
                attackPaneGroup.addAction(Actions.sequence(Actions.visible(true),Actions.fadeIn(.3f)));
                setUpAttacksPane();
            }
        });
        battleMenuButtons.put("attack", tb);


        // Button 5 ................................................................................
        ib = new ImageButton(battleSkin, "b-next");
        ib.setPosition(GS.COL*5.5f, 0, Align.bottomLeft);
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO
                System.out.println("Button 5");
            }
        });
        battleMenuButtons.put("button5", ib);

        for(String key : battleMenuImgs.keys()) battleActionMenu.addActor(battleMenuImgs.get(key));
        for(String s : battleMenuButtons.keys()) battleActionMenu.addActor(battleMenuButtons.get(s));

        battleActionMenu.addActor(infoLabel);
        battleActionMenu.addActor(attackPaneGroup);
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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


        // Create Attack Buttons
        for(Attack a : team.get(chosenTeamMonster).attacks) {
            TextButton tb = new TextButton(a.name + "(" + a.damage + ")", tbs);
            tb.setWidth(128);
            tb.setHeight(23);
            attackVGroup.addActor(tb);
            tb.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    battleMenuButtons.get("attack").setDisabled(true);
                    battleMenuButtons.get("attack").addAction(Actions.alpha(0.5f));
                    // Hide Attack Menu
                    attackPaneGroup.setVisible(false);
                    // Add Monster to the Queue
                    lineUpForAttack(team.get(chosenTeamMonster), chosenTarget, 0);
                }
            });
        }
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /* ......................................................................... BATTLE METHODS ..*/

    public void carryOutAttack(Monster attacker) {
        System.out.println(attacker.ID + " " + team.contains(attacker, false) + " " + attacker
                .nextTarget);
        Monster defender = (attacker.battleFieldPosition<3) ?
                opponentTeam.get(attacker.nextTarget-3) : team.get(attacker.nextTarget);

        attacker.startAttack();    // attackStarted = Time, attackingRightNow=true

        // Animate Attack
        animateAttack(attacker.battleFieldPosition, attacker.nextTarget);

        /* Calculate Damage */
        if (defender.HP - attacker.nextAttack.damage < 0) defender.HP = 0;
        else                                              defender.HP -= attacker.nextAttack.damage;

        /* Update Health Bar */
        progressBars.get("HP").get(defender.battleFieldPosition)
                .setValue(100 * defender.HP / defender.HPfull);

        // Handle Attack
        handleAttack(attacker, defender);

        // Choose Team
        if(attacker.battleFieldPosition < 3) {
            // Monster from Team Hero ......................................................... HERO

            /* Check if all enemies are KO */
            this.allKO = checkIfWholeTeamKO(opponentTeam);

            attackPaneGroup.addAction(Actions.sequence(Actions.fadeOut(.3f), Actions.visible(false)));
        }
    }

    public boolean checkIfWholeTeamKO(Array<Monster> team) {
        boolean allKO = true;
        switch (team.size) {
            case 3: allKO = (team.get(2).HP == 0);
            case 2: allKO = (allKO == true && team.get(1).HP == 0);
            default:allKO = (allKO == true && team.get(0).HP == 0);
                break;
        }

        return allKO;
    }

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

        System.out.println("Killed: " + pos);

        // Change the indicator position to an active fighter
        if(pos<3) {
            // Hero Team
            team.get(pos).KO = true;
            int p = heroPosQueue.remove(pos);
            System.out.println("KILL QUEUE: " + p);
            if(p>=0)changeIndicatorPosition(true, p);
        }
        if(pos>2) {
            // Opponent Team
            opponentTeam.get(pos-3).KO = true;
            int p =  opponentPosQueue.remove(pos);
            System.out.println("KILL QUEUE: " + p);
            if(p>=0)changeIndicatorPosition(false, p);
        }

    }

    /**
     * Animate an attack of the given monster
     * @param attPos    position of attacker
     * @param defPos    position of defender
     */
    private void animateAttack(final int attPos, int defPos) {
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
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        game.media.getSFX(SFXType.HIT, 0).play();
                    }
                }),
                Actions.moveTo(startPos.x, startPos.y, .3f, Interpolation.pow2Out)
        ));
        defIm = monsterImgs.get(defPos);
        defIm.addAction(Actions.sequence(
                Actions.delay(.6f), Actions.moveBy(0, 15, .1f, Interpolation.bounceIn),
                Actions.moveBy(0, -15, .1f, Interpolation.bounceIn)
        ));

    }

    /**
     * Put monster in line
     * @param m
     */
    public void lineUpForAttack(Monster m, int chosenTarget, int attack) {
        m.prepareForAttack(chosenTarget, attack);

        if(GS.DEBUGGING_ON) {
            System.out.println("--- lineUpForAttack() ---");
            System.out.println("Preparing monster " + m.ID + "at pos " + m.battleFieldPosition);
            System.out.println(m.ready + "," + m.attacks.get(attack) + ", target:" + chosenTarget
                    + ", waitingInQueue: " + m.waitingInQueue);
        }

        attackerQueue.add(m);
    }


    /* ..................................................................... GETTERS & SETTERS .. */


    // #############################################################################################
    // #############################################################################################
    // #############################################################################################
    // #############################################################################################
    /* ......................................................................... INNER CLASSES .. */

    private class AIPlayer {
        private Array<Monster> AIteam, AIoppTeam;
        private int AIchosenMember, AIchosenTarget;
        private Array<Integer> AIteamPositions,AIopponentPositions;
        private boolean AIpaused;
        private boolean thinking;
        private long thinkingSince;
        private long thinkingDuration;

        public AIPlayer(Array<Monster> team, Array<Monster> oppTeam) {
            this.AIteam = team;
            this.AIoppTeam = oppTeam;
            AIchosenMember = 0;
            AIchosenTarget = 0;
            AIpaused = false;
            thinking = false;
            thinkingSince = TimeUtils.millis();
            thinkingDuration = 0;

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
        }

        public void act() {
            if(AIpaused) return;

            // Remove KO monsters
            for(int i=0; i<AIteam.size; i++)
                if(AIteam.get(i).HP == 0) {
                    AIteam.get(i).KO = true;
                    AIteamPositions.removeValue(i, true);
                }

            for(int i=0; i<AIoppTeam.size; i++)
                if(AIoppTeam.get(i).HP == 0) {
                    AIopponentPositions.removeValue(i, true);
                }


            if(!thinking) {
                // Check if monster is ready
                for (int j = 0; j < AIteam.size; j++)
                    if (AIteam.get(j).ready && AIteam.get(j).HP > 0) {
                        // Choose Target
                        AIchosenMember = j;
                        think();
                    }
            }

            if(thinking && TimeUtils.timeSinceMillis(thinkingSince) > thinkingDuration) {
                attack();
                this.thinking = false;
            }

        }

        public void think() {
            this.thinking = true;
            this.thinkingSince = TimeUtils.millis();
            this.thinkingDuration = MathUtils.random(1000,3000);
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

        public void attack() {
            // If there are enemies left
            if(AIopponentPositions.size > 0) {
                // Choose target randomly
                AIchosenTarget = AIopponentPositions.get(
                        MathUtils.random(0, AIopponentPositions.size - 1));
            }

            // ............................................................................... DEBUG
            if(GS.DEBUGGING_ON) {
                System.out.println("#####################");
                System.out.println(AIoppTeam);
                System.out.println(AIchosenTarget);
                System.out.println(AIteam);
                System.out.println(AIchosenMember);
                System.out.println("#####################");
            }

            // PRINT QUEUE
            System.out.println("QUEUE -------------------------------");
            System.out.println("Size: " + attackerQueue.size);
            for(Monster m : attackerQueue) {
                System.out.println("Monster: " + m.ID);
                System.out.println("Position: " + m.battleFieldPosition);
                System.out.println("Target: " + m.nextTarget);
                System.out.println("Ready: " + m.ready);
                System.out.println("In Action: "+ m.attackingRightNow);
                System.out.println("In Queue" + m.waitingInQueue);
            }
            // ............................................................................... DEBUG

            lineUpForAttack(AIteam.get(AIchosenMember), AIchosenTarget,
                    MathUtils.random(AIteam.get(AIchosenMember).attacks.size - 1));

            checkEnemiesDeath();
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
        private static IntVector2 OPPO_TOP = new IntVector2(GS.RES_X-120-128,212);
        private static IntVector2 OPPO_MID = new IntVector2(GS.RES_X - 64 - 128, 176);
        private static IntVector2 OPPO_BOT = new IntVector2(GS.RES_X-8-128,140);
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
