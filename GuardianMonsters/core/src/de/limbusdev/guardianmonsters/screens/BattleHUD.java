package de.limbusdev.guardianmonsters.screens;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.limbusdev.guardianmonsters.GuardianMonsters;
import de.limbusdev.guardianmonsters.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.enums.SFXType;
import de.limbusdev.guardianmonsters.geometry.IntVector2;
import de.limbusdev.guardianmonsters.model.Attack;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInBattle;
import de.limbusdev.guardianmonsters.ui.AttackMenuWidget;
import de.limbusdev.guardianmonsters.ui.BattleActionMenuWidget;
import de.limbusdev.guardianmonsters.ui.BattleMainMenuWidget;
import de.limbusdev.guardianmonsters.ui.MonsterStateWidget;
import de.limbusdev.guardianmonsters.utils.GS;
import de.limbusdev.guardianmonsters.utils.MonsterManager;
import de.limbusdev.guardianmonsters.utils.MonsterSpeedComparator;

/**
 * BattleHUD manages all actions and UI elements in the {@link BattleScreen}
 *
 * Created by georg on 03.12.15.
 */
public class BattleHUD {

    /* ............................................................................ ATTRIBUTES .. */
    private final GuardianMonsters game;

    // ------------------------------------------------------------------------------------ SCENE 2D
    public  Stage stage;
    private Skin skin, battleSkin;

    // Groups
    private Group monsterStatusUI, attackPaneGroup, gameOverUI, indicatorButtons;
    private BattleMainMenuWidget   mainMenu;
    private BattleActionMenuWidget actionMenu;
    private AttackMenuWidget       attackMenu;

    // Labels
    private Label gameOverLabel;

    // Images
    private Image indicatorOpp, indicatorHero, battleUIbg, attackScrollPaneBg, blackCourtain;
    private ArrayMap<Integer,Image> monsterImgs;

    private Array<MonsterStateWidget> monsterStateWidgets;


    // --------------------------------------------------------------------------------------- OTHER
    // Not Scene2D related
    private Array<MonsterInBattle> team, oppTeam;   // hold monsters of one team
    private Array<MonsterInBattle> attackerQueue;

    private int chosenTarget =3;                    // position of active target
    private int chosenMember =0;                    // position of active attacker
    private boolean allKO, allHeroKO;               // whether a whole team is KO
    private boolean attackAnimationRunning = false;

    private AIPlayer aiPlayer;                      // Artificial Intelligence

    // -------------------------------------------------------------------------------------- STATES
    // CHOOSING, BATTLEACTION
    private BattleState state = BattleState.CHOOSING;

    /* ........................................................................... CONSTRUCTOR .. */
    public BattleHUD(final GuardianMonsters game) {
        this.game = game;

        initializeAttributes();
        setUpUI();
        setUpTopLevelMenu();
        setUpMonsterImages();
        setUpBattleActionMenu();
        setUpGameOverUI();
        addElementsToStage();

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

        switch(state) {
            case CHOOSING: updateChoosing(delta); break;
            default:       updateBattleAction(delta); break;
        }
    }

    /**
     * Update Method for state: BATTLEACTION
     */
    public void updateBattleAction(float delta) {
        // Start one attack after the other
        updateAttackerQueue();
    }

    public void updateChoosing(float delta) {
        // Let AI-Player act
        aiPlayer.act();
        countFitMonsters();
        // ........................................................................... END OF BATTLE
        if(allKO) handleEndOfBattle();
    }

    /**
     * Handles all monsters in the queue and starts/finishes their attacks
     * ron only from updateBattleAction()
     */
    public void updateAttackerQueue() {
            if (attackerQueue.size > 0 && !attackAnimationRunning) {
                MonsterInBattle m = attackerQueue.pop();
                if(m.monster.getHP() > 0) carryOutAttack(m);
            } else if(attackerQueue.size == 0) {
                newRound();
            }
    }

    // #############################################################################################
    // ............................................................................ INITIALIZE ARENA

    /**
     * Resets the UI into a state where it can be initialized for a new battle
     */
    public void reset() {
        // Attributes
        this.chosenTarget = BatPos.OPPO_MID;
        this.chosenMember = BatPos.MID;

        allKO = false;
        allHeroKO = false;

        // Clear Actions
        for(MonsterStateWidget w : monsterStateWidgets) w.clearActions();

        for(Integer key : monsterImgs.keys()) monsterImgs.get(key).clearActions();
        actionMenu.clearActions();
        mainMenu.clearActions();
        monsterStatusUI.clearActions();
        attackPaneGroup.clearActions();
        blackCourtain.clearActions();
        gameOverUI.clearActions();

        resetUIElementVisibility();
    }

    /**
     * Initializes the battle screen with the given teams
     * @param team
     * @param opponentTeam
     */
    public void init(TeamComponent team, TeamComponent opponentTeam) {
        reset();

        //Initialize Teams .........................................................................
        // Hero Team

        this.team = new Array<MonsterInBattle>();
        int i = 0;
        for(Monster m : team.monsters) {
            if(m.getHP() > 0) {
                this.team.add(new MonsterInBattle(m, i));
                i++;
            }
        }
        setIndicatorPosition(true, 0);

        // Opponent Team
        this.oppTeam = new Array<MonsterInBattle>();
        int j = 0;
        for(Monster m : opponentTeam.monsters) {
            if(m.getHP() > 0) {
                this.oppTeam.add(new MonsterInBattle(m, j+3));
                j++;
            }
        }
        setIndicatorPosition(false, 3);

        this.aiPlayer = new AIPlayer();


        // Initialize Status UIs ...................................................................
        // Hero Team
        switch(team.monsters.size) {
            case 3: initStatusUI(team.monsters.get(2), BatPos.HERO_TOP);
            case 2: initStatusUI(team.monsters.get(1), BatPos.HERO_BOT);
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
     * Refresh battle UI for new round
     */
    private void newRound() {
        setupIndicatorButtons(true);
        state = BattleState.CHOOSING;
        for(MonsterInBattle m : team) m.attackChosen = false;
        for(MonsterInBattle m : oppTeam) m.attackChosen = false;
        attackerQueue.clear();
        aiPlayer.havePause(false);
    }

    /**
     * Handle End of Battle
     */
    public void handleEndOfBattle() {
        // Stop AI
        aiPlayer.havePause(true);

        // Check if Hero lost fight
        boolean heroLost = true;
        for(MonsterInBattle m : team)
            if(m.monster.getHP() > 0) heroLost = false;

        // Hide UI Elements
        actionMenu.addFadeOutAction(.5f);
        mainMenu.addFadeOutWidgetAction(.5f);

        // Activate Label
        gameOverUI.setVisible(true);

        // Set message
        if(heroLost) gameOverLabel.setText("Game Over");
        else gameOverLabel.setText("You won!");
    }

    /**
     * Handle the event of a monster being killed
     */
    private void handleAttack(MonsterInBattle att, MonsterInBattle def) {
        // Remove killed monsters
        if(def.monster.getHP() == 0) kickOutMonster(def);

        // Spread EXP
        if(def.battleFieldPosition > 2 && def.monster.getHP() == 0) {
            // Defeated Monster was part of opponents team
            int exp = def.monster.level * (def.monster.getHPfull() + def.monster.physStrength);
            exp /= team.size;
            for (MonsterInBattle m : team) if (m.monster.getHP() > 0) MonsterManager.earnEXP(m.monster,exp);
        }
    }

    /**
     * Changes the indicators position of the given team to the given position
     * @param heroesTeam
     * @param pos
     */
    private void setIndicatorPosition(boolean heroesTeam, int pos) {
        if(heroesTeam) {
            switch(pos) {
                case 2:  indicatorHero.setPosition(IndPos.HERO_TOP.x, IndPos.HERO_TOP.y, Align.center);break;
                case 1:  indicatorHero.setPosition(IndPos.HERO_BOT.x, IndPos.HERO_BOT.y, Align.center);break;
                default: indicatorHero.setPosition(IndPos.HERO_MID.x, IndPos.HERO_MID.y, Align.center);break;
            }
            activateButton(pos);
            chosenMember = pos;
        } else {
            switch(pos) {
                case 5:indicatorOpp.setPosition(IndPos.OPPO_TOP.x, IndPos.OPPO_TOP.y, Align.center);break;
                case 4:indicatorOpp.setPosition(IndPos.OPPO_BOT.x, IndPos.OPPO_BOT.y, Align.center);break;
                default:indicatorOpp.setPosition(IndPos.OPPO_MID.x, IndPos.OPPO_MID.y, Align.center);break;
            }
            chosenTarget = pos;
        }
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // ....................................................................................... SETUP
    /**
     * Initializes Attributes and especially Arrays and Maps
     */
    public void initializeAttributes() {
        this.allKO = false;

        this.monsterImgs = new ArrayMap<Integer,Image>();
        this.monsterStateWidgets = new Array<MonsterStateWidget>();

        this.attackerQueue = new Array<MonsterInBattle>();

        this.team = new Array<MonsterInBattle>();
        this.oppTeam = new Array<MonsterInBattle>();

        this.attackPaneGroup = new Group();
        this.gameOverUI = new Group();
        this.indicatorButtons = new Group();

        this.aiPlayer = new AIPlayer();
    }

    private void addElementsToStage() {
        stage.addActor(battleUIbg);
        stage.addActor(mainMenu);


        for(Integer key : monsterImgs.keys())
            stage.addActor(monsterImgs.get(key));

        stage.addActor(monsterStatusUI);

        stage.addActor(gameOverUI);

        stage.addActor(blackCourtain);
        stage.addActor(actionMenu);
    }

    /**
     * Setting up HUD elements:
     */
    public void setUpUI() {
        // Scene2D
        FitViewport fit = new FitViewport(GS.RES_X, GS.RES_Y);
        this.stage = new Stage(fit);
        this.skin = game.media.skin;
        this.battleSkin = game.media.battleSkin;
        stage.setDebugAll(GS.DEBUGGING_ON);

        this.monsterStatusUI = new Group();

        // Battle UI Black transparent Background
        this.battleUIbg = new Image(game.media.getBattleUITextureAtlas().findRegion("bg"));
        battleUIbg.setPosition(0, 0);
        battleUIbg.setSize(GS.RES_X, GS.ROW*14);

        // Black Courtain for fade-in and -out
        this.blackCourtain = new Image(game.media.getBattleUITextureAtlas().findRegion("black"));
        this.blackCourtain.setSize(GS.RES_X,GS.RES_Y);
        this.blackCourtain.setPosition(0, 0);

        // Hero Team ###############################################################################
        MonsterStateWidget msw = new MonsterStateWidget(battleSkin);
        msw.setPosition(IndPos.statWPos1.x,IndPos.statWPos1.y);
        monsterStateWidgets.add(msw);
        msw = new MonsterStateWidget(battleSkin);
        msw.setPosition(IndPos.statWPos2.x,IndPos.statWPos2.y);
        monsterStateWidgets.add(msw);
        msw = new MonsterStateWidget(battleSkin);
        msw.setPosition(IndPos.statWPos3.x,IndPos.statWPos3.y);
        monsterStateWidgets.add(msw);

        // Opponent Team ###########################################################################
        msw = new MonsterStateWidget(battleSkin);
        msw.setPosition(GS.RES_X-IndPos.statWPos1.x,IndPos.statWPos1.y,Align.bottomRight);
        monsterStateWidgets.add(msw);
        msw = new MonsterStateWidget(battleSkin);
        msw.setPosition(GS.RES_X-IndPos.statWPos2.x,IndPos.statWPos2.y,Align.bottomRight);
        monsterStateWidgets.add(msw);
        msw = new MonsterStateWidget(battleSkin);
        msw.setPosition(GS.RES_X-IndPos.statWPos3.x,IndPos.statWPos3.y,Align.bottomRight);
        monsterStateWidgets.add(msw);

        for(MonsterStateWidget w : monsterStateWidgets) monsterStatusUI.addActor(w);

        // Battle HUD Monster Indicators
        indicatorOpp = new Image(battleSkin.getDrawable("indicator"));
        indicatorOpp.setPosition(IndPos.OPPO_MID.x, IndPos.OPPO_MID.y, Align.bottomRight);
        indicatorHero = new Image(battleSkin.getDrawable("indicator"));
        indicatorHero.setPosition(IndPos.HERO_MID.x, IndPos.HERO_MID.y, Align.bottomLeft);

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
                                game.popScreen();
                            }
                        })
                ));
            }
        });
        gameOverUI.addActor(exitButton);
        gameOverUI.setVisible(false);
    }

    /**
     * Adds images to the stage for displaying monster sprites
     */
    private void setUpMonsterImages() {
        Image monImg;

        // Hero Team
        monImg = new Image(game.media.getBattleUITextureAtlas().findRegion("black"));
        monImg.setSize(256,256);
        monImg.setPosition(ImPos.HERO_TOP.x,ImPos.HERO_TOP.y,Align.bottomLeft);
        monImg.addAction(Actions.forever(Actions.sequence(
                Actions.moveBy(0, 2, .5f), Actions.moveBy(0, -2, .5f))));
        monImg.setVisible(false);
        monsterImgs.put(BatPos.HERO_TOP, monImg);

        monImg = new Image(game.media.getBattleUITextureAtlas().findRegion("black"));
        monImg.setSize(256,256);
        monImg.setPosition(ImPos.HERO_MID.x,ImPos.HERO_MID.y,Align.bottomLeft);
        monImg.addAction(Actions.forever(Actions.sequence(
                Actions.moveBy(0, -2, .5f), Actions.moveBy(0, 2, .5f))));
        monImg.setVisible(false);
        monsterImgs.put(BatPos.HERO_MID, monImg);

        monImg = new Image(game.media.getBattleUITextureAtlas().findRegion("black"));
        monImg.setSize(256,256);
        monImg.setPosition(ImPos.HERO_BOT.x,ImPos.HERO_BOT.y,Align.bottomLeft);
        monImg.addAction(Actions.forever(Actions.sequence(
                Actions.moveBy(0, 2, .5f), Actions.moveBy(0, -2, .5f))));
        monImg.setVisible(false);
        monsterImgs.put(BatPos.HERO_BOT, monImg);


        // Opponent Team
        monImg = new Image(game.media.getBattleUITextureAtlas().findRegion("black"));
        monImg.setSize(256,256);
        monImg.setPosition(GS.RES_X-ImPos.HERO_TOP.x,ImPos.HERO_TOP.y,Align.bottomRight);
        monImg.addAction(Actions.forever(Actions.sequence(
                Actions.moveBy(0, 2, .5f), Actions.moveBy(0, -2, .5f))));
        monImg.setVisible(false);
        monsterImgs.put(BatPos.OPPO_TOP, monImg);

        monImg = new Image(game.media.getBattleUITextureAtlas().findRegion("black"));
        monImg.setSize(256,256);
        monImg.setPosition(GS.RES_X-ImPos.HERO_MID.x,ImPos.HERO_MID.y,Align.bottomRight);
        monImg.addAction(Actions.forever(Actions.sequence(
                Actions.moveBy(0, -2, .5f), Actions.moveBy(0, 2, .5f))));
        monImg.setVisible(false);
        monsterImgs.put(BatPos.OPPO_MID, monImg);

        monImg = new Image(game.media.getBattleUITextureAtlas().findRegion("black"));
        monImg.setSize(256,256);
        monImg.setPosition(GS.RES_X-ImPos.HERO_BOT.x,ImPos.HERO_BOT.y,Align.bottomRight);
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
        mainMenu = new BattleMainMenuWidget(battleSkin);

        mainMenu.addSwordButtonListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Input: Button Fight");
                    mainMenu.addFadeOutWidgetAction(0.3f);
                    actionMenu.addFadeInAction(0.5f);
                    aiPlayer.havePause(false);
                    newRound();
                }
            }
        );

        mainMenu.addRunButtonListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Input: Button Escape");
                    blackCourtain.addAction(Actions.sequence(Actions.visible(true), Actions.fadeIn(1),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                game.popScreen();
                            }
                        })));
                }
            }
        );

        mainMenu.addBagButtonListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // TODO
                    System.out.println("Input: Button Bag");
                }
            }
        );
    }

    /**
     * Settings up all elements for the battle action menu
     */
    private void setUpBattleActionMenu() {
        actionMenu = new BattleActionMenuWidget(battleSkin);

        actionMenu.backButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    actionMenu.addFadeOutAction(0.3f);
                    mainMenu.addFadeInWidgetAction(0.3f);
                    System.out.println("Button: back to top level menu");
                    aiPlayer.havePause(true);
                }
            }
        );

        actionMenu.greyLButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO
                System.out.println("Input: button mouse l");
            }
        });

        actionMenu.greyRButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO
                System.out.println("Input: button mouse r");
            }
        });

        actionMenu.greenButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(actionMenu.greenButton.isDisabled()) return;
                System.out.println("Button: attack");
                attackPaneGroup.addAction(Actions.sequence(Actions.visible(true),Actions.fadeIn(.3f)));
                setUpAttacksPane();
            }
        });
        actionMenu.greenButton.setText("Attack");

        actionMenu.blueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO
                System.out.println("Button 5");
            }
        });
    }

    /**
     * Left Side = Hero Monsters
     * @param leftSide
     */
    private void setupIndicatorButtons(boolean leftSide) {
        if(leftSide) {
            hideIndicatorButtons();
            for(MonsterInBattle m : team)
                if(!m.attackChosen)
                    setupIndicatorButton(m.battleFieldPosition);
        } else {
            hideIndicatorButtons();
            for(MonsterInBattle m : oppTeam) setupIndicatorButton(m.battleFieldPosition);
        }
    }

    private void hideIndicatorButtons() {
        indicatorButtons.clear();
    }

    /**
     *  Adds arrows for choosing monsters
     * @param position
     */
    private void setupIndicatorButton(final int position) {
        int row, col, align;
        String texture;

        if(position < 3) {
            texture = "choice-l";
            col = 0;
            align = Align.bottomLeft;
        } else {
            texture = "choice-r";
            col = 1280;
            align = Align.bottomRight;
        }

        switch(position) {
            case 3: case 0:  row = 22;break;
            case 4: case 1:  row = 17;break;
            case 5: case 2:  row = 27;break;
            default:         row = 17;break;
        }

        ImageButton ib = new ImageButton(battleSkin, texture);
        ib.setPosition(col, GS.ROW*row, align);
        ib.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setIndicatorPosition(position<3,position);
                if(position<3) setupIndicatorButtons(false);
            }
        });

        indicatorButtons.addActor(ib);
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /* .................................................................... CREATE UI ELEMENTS .. */
    public void setUpAttacksPane() {

        Array<Attack> attacks = team.get(chosenMember).monster.attacks;
        attackMenu = new AttackMenuWidget(battleSkin);
        attackMenu.init(attacks);
        attackMenu.addListenerToAllButtons(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    actionMenu.greenButton.setDisabled(true);
                    actionMenu.greenButton.addAction(Actions.alpha(0.5f));
                    attackMenu.addFadeOutAction(.5f);
                    attackMenu.remove();
                }
            }
        );

        if (attacks.size >= 1)
            attackMenu.addListenerToButton(0, new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    lineUpForAttack(team.get(chosenMember), chosenTarget, 0);
                }
            });

        if(attacks.size >= 2)
            attackMenu.addListenerToButton(1, new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    lineUpForAttack(team.get(chosenMember), chosenTarget, 1);
                }
            });

        if(attacks.size >= 3)
            attackMenu.addListenerToButton(2, new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    lineUpForAttack(team.get(chosenMember), chosenTarget, 2);
                }
            });

        if(attacks.size >= 4)
            attackMenu.addListenerToButton(3, new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    lineUpForAttack(team.get(chosenMember), chosenTarget, 3);
                }
            });

        if(attacks.size >= 5)
            attackMenu.addListenerToButton(4, new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    lineUpForAttack(team.get(chosenMember), chosenTarget, 4);
                }
            });

        if(attacks.size >= 6)
            attackMenu.addListenerToButton(5, new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    lineUpForAttack(team.get(chosenMember), chosenTarget, 5);
                }
            });

        stage.addActor(attackMenu);
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /* ......................................................................... BATTLE METHODS ..*/

    public void carryOutAttack(MonsterInBattle attacker) {
        MonsterInBattle defender = (attacker.battleFieldPosition<3) ?
                oppTeam.get(attacker.nextTarget-3) : team.get(attacker.nextTarget);

        attacker.startAttack();    // attackStarted = Time, attackingRightNow=true

        // Animate Attack
        animateAttack(attacker.battleFieldPosition, attacker.nextTarget);

        /* Calculate Damage */
        if (defender.monster.getHP() - attacker.nextAttack.damage < 0) defender.monster.setHP(0);
        else defender.monster.setHP(defender.monster.getHP() - attacker.nextAttack.damage);

        // Handle Attack
        handleAttack(attacker, defender);

        // Choose Team
        if(attacker.battleFieldPosition < 3) {
            // Monster from Team Hero ......................................................... HERO

            /* Check if all enemies are KO */
            this.allKO = checkIfWholeTeamKO(oppTeam);

            attackPaneGroup.addAction(Actions.sequence(Actions.fadeOut(.3f), Actions.visible(false)));
        }
    }

    /**
     * Checks whether the whole team is down and someone won
     * @param team
     * @return
     */
    public boolean checkIfWholeTeamKO(Array<MonsterInBattle> team) {
        boolean allKO = true;
        switch (team.size) {
            case 3: allKO = (team.get(2).monster.getHP() == 0);
            case 2: allKO = (allKO == true && team.get(1).monster.getHP() == 0);
            default:allKO = (allKO == true && team.get(0).monster.getHP() == 0);
                break;
        }

        return allKO;
    }

    /**
     * Removes the monster with the given position on the battle field by setting it KO and
     * fading out the monster sprite and monsters status UI
     * @param m
     */
    private void kickOutMonster(MonsterInBattle m) {
        monsterImgs.get(m.battleFieldPosition).addAction(
                Actions.sequence(Actions.alpha(0, 2), Actions.visible(false)));
        monsterStateWidgets.get(m.battleFieldPosition).addAction(
                Actions.sequence(Actions.alpha(0, 2), Actions.visible(false)));

        System.out.println("Killed: " + m.battleFieldPosition);

        // Change the indicator position to an active fighter
        if(m.battleFieldPosition<3) {
            // Hero Team
            if(m.battleFieldPosition == team.get(chosenMember).battleFieldPosition)
                setIndicatorPosition(true, team.get(0).battleFieldPosition);
            team.removeValue(m, true);
        }
        if(m.battleFieldPosition>2) {
            // Opponent Team
            if(m.battleFieldPosition == oppTeam.get(chosenTarget-3).battleFieldPosition)
                setIndicatorPosition(false, oppTeam.get(0).battleFieldPosition);
            oppTeam.removeValue(m, true);
        }

    }

    /**
     * Put monster in line
     * @param m
     */
    public void lineUpForAttack(MonsterInBattle m, int chosenTarget, int attack) {
        m.prepareForAttack(chosenTarget, attack);

        if(GS.DEBUGGING_ON) {
            System.out.println("--- lineUpForAttack() ---");
            System.out.println("Preparing monster " + m.monster.ID + "at pos " + m.battleFieldPosition);
            System.out.println(m.ready + "," + m.monster.attacks.get(attack) + ", target:" + chosenTarget
                    + ", waitingInQueue: " + m.attackChosen);
        }

        attackerQueue.add(m);

        if(attackerQueue.size == countFitMonsters()) {
            state = BattleState.BATTLEACTION;
            attackerQueue.sort(new MonsterSpeedComparator());
            hideIndicatorButtons();
        } else {
            setupIndicatorButtons(true);
        }
    }

    /**
     * Finds out how many monsters are fit and ready to fight
     * @return number of fit monsters
     */
    public int countFitMonsters() {
        int i = 0;
        for(MonsterInBattle m : team)
            if(m.monster.getHP() > 0)
                i++;
        for(MonsterInBattle m : oppTeam)
            if(m.monster.getHP() > 0)
                i++;
        return i;
    }

    // .......................................................................................... UI
    /**
     * Draw the HUD to the screen
     */
    public void draw() {
        this.stage.draw();
    }

    /**
     * Make attack button visible as soon as round is over
     * @param attackerPos
     */
    private void activateButton(int attackerPos) {
        // If monster not KO and attack hasn't been chosen yet
        if(!team.get(attackerPos).attackChosen) {
            actionMenu.greenButton.setDisabled(false);
            actionMenu.greenButton.addAction(Actions.sequence(Actions.alpha(1.0f, .2f)));
        } else {
            actionMenu.greenButton.setDisabled(true);
            actionMenu.greenButton.addAction(Actions.sequence(Actions.alpha(0.5f,.2f)));
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
        int attAlign, defAlign;

        if(attPos < 3) {
            attAlign = Align.bottomLeft;
            defAlign = Align.bottomRight;
        } else {
            attAlign = Align.bottomRight;
            defAlign = Align.bottomLeft;
        }

        attackAnimationRunning = true;

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
                Actions.moveToAligned(endPos.x, endPos.y, defAlign, .6f, Interpolation.pow2In),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        game.media.getSFX(SFXType.HIT, 0).play();
                    }
                }),
                Actions.moveToAligned(startPos.x, startPos.y, attAlign, .3f, Interpolation.pow2Out),
                Actions.delay(.5f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        attackAnimationRunning=false;
                    }
                })
        ));
        defIm = monsterImgs.get(defPos);
        defIm.addAction(Actions.sequence(
                Actions.delay(.6f), Actions.moveBy(0, 15, .1f, Interpolation.bounceIn),
                Actions.moveBy(0, -15, .1f, Interpolation.bounceIn)
        ));

    }

    /**
     * Initializes the graphical representation of a monsters status on the screen
     * @param monster   the given monster
     * @param position  the position on the battle field
     */
    public void initStatusUI(Monster monster, final int position) {

        // Set Monster Information in UI and set Signs visible
        monsterStateWidgets.get(position).init(monster);

        setMonsterStatusUIvisible(monster, position);

        stage.addAction(Actions.fadeIn(1f));
    }

    /**
     * Initializes the HUD for the given battle field position
     * @param monster
     * @param position
     */
    public void setMonsterStatusUIvisible(Monster monster, final int position) {
        monsterStateWidgets.get(position).addAction(Actions.sequence(
                Actions.alpha(1), Actions.visible(true)
        ));

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

    private void resetUIElementVisibility() {
        // Visibility
        // Reset Status UIs
        for(MonsterStateWidget w : monsterStateWidgets) {
            w.addAction(Actions.alpha(1));
            w.setVisible(false);
        }

        for(Integer key : monsterImgs.keys()) {
            monsterImgs.get(key).addAction(Actions.alpha(1));
            monsterImgs.get(key).setVisible(false);
        }

        actionMenu.addFadeOutAction(.1f);
        mainMenu.addFadeInWidgetAction(.1f);
        monsterStatusUI.addAction(Actions.alpha(1));
        monsterStatusUI.setVisible(true);

        attackPaneGroup.setVisible(false);
        blackCourtain.setVisible(true);
        gameOverUI.setVisible(false);
        stage.act(1);
    }


    /* ..................................................................... GETTERS & SETTERS .. */


    // #############################################################################################
    // #############################################################################################
    // #############################################################################################
    // #############################################################################################
    /* ......................................................................... INNER CLASSES .. */

    private class AIPlayer {
        private boolean AIpaused;

        public AIPlayer() {
            AIpaused = true;
        }

        public void act() {
            if(AIpaused) return;
            // Choose targets and attacks
            for(int i=0; i<oppTeam.size; i++) {
                attack(oppTeam.get(i));
            }
            havePause(true);
        }

        public void havePause(boolean paused) {
            this.AIpaused = paused;
        }

        public void checkEnemiesDeath() {
            /* Check if all enemies are KO */
            if(team.size == 0) handleEndOfBattle();
        }

        public void attack(MonsterInBattle m) {
            lineUpForAttack(
                    m, MathUtils.random(team.size-1),MathUtils.random(m.monster.attacks.size-1));
            checkEnemiesDeath();
        }
    }

    /**
     * Possible Indicator coordinates
     */
    private final static class IndPos {
        private static final IntVector2 HERO_TOP  = new IntVector2(GS.COL*19, GS.ROW*34);
        private static final IntVector2 HERO_MID  = new IntVector2(GS.COL*13, GS.ROW*31);
        private static final IntVector2 HERO_BOT  = new IntVector2(GS.COL*7, GS.ROW*28);
        private static final IntVector2 OPPO_TOP  = new IntVector2(GS.RES_X-HERO_TOP.x, HERO_TOP.y);
        private static final IntVector2 OPPO_MID  = new IntVector2(GS.RES_X-HERO_MID.x, HERO_MID.y);
        private static final IntVector2 OPPO_BOT  = new IntVector2(GS.RES_X-HERO_BOT.x, HERO_BOT.y);
        private static final IntVector2 statWPos1 = new IntVector2(GS.COL*5,GS.RES_Y-GS.ROW*7);
        private static final IntVector2 statWPos2 = new IntVector2(GS.COL*2,GS.RES_Y-GS.ROW*10);
        private static final IntVector2 statWPos3 = new IntVector2(GS.COL*8,GS.RES_Y-GS.ROW*4);
    }

    private final static class ImPos {
        private static final IntVector2 HERO_MID = new IntVector2(GS.COL*7,GS.ROW*18);
        private static final IntVector2 HERO_BOT = new IntVector2(GS.COL*1,GS.ROW*15);
        private static final IntVector2 HERO_TOP = new IntVector2(GS.COL*13,GS.ROW*21);
        private static final IntVector2 OPPO_MID = new IntVector2(GS.RES_X-GS.COL*7,GS.ROW*18);
        private static final IntVector2 OPPO_BOT = new IntVector2(GS.RES_X-GS.COL*1,GS.ROW*15);
        private static final IntVector2 OPPO_TOP = new IntVector2(GS.RES_X-GS.COL*13,GS.ROW*21);
    }

    /**
     * Positions on the Battle Field
     */
    private final static class BatPos {
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

    private enum BattleState {
        CHOOSING, BATTLEACTION,
    }
}
