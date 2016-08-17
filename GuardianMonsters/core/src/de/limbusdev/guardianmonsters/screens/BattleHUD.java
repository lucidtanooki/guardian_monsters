package de.limbusdev.guardianmonsters.screens;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
import de.limbusdev.guardianmonsters.geometry.IntVector2;
import de.limbusdev.guardianmonsters.model.Attack;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInBattle;
import de.limbusdev.guardianmonsters.ui.AttackMenuWidget;
import de.limbusdev.guardianmonsters.ui.BattleActionMenuWidget;
import de.limbusdev.guardianmonsters.ui.BattleAnimationWidget;
import de.limbusdev.guardianmonsters.ui.BattleMainMenuWidget;
import de.limbusdev.guardianmonsters.ui.BattleStatusOverviewWidget;
import de.limbusdev.guardianmonsters.ui.MonsterIndicatorWidget;
import de.limbusdev.guardianmonsters.ui.MonsterStateWidget;
import de.limbusdev.guardianmonsters.ui.ObservableWidget;
import de.limbusdev.guardianmonsters.ui.WidgetObserver;
import de.limbusdev.guardianmonsters.utils.GS;
import de.limbusdev.guardianmonsters.utils.MonsterManager;
import de.limbusdev.guardianmonsters.utils.MonsterSpeedComparator;

/**
 * BattleHUD manages all actions and UI elements in the {@link BattleScreen}
 *
 * Created by georg on 03.12.15.
 */
public class BattleHUD implements WidgetObserver {

    /* ............................................................................ ATTRIBUTES .. */
    private final GuardianMonsters game;

    // ------------------------------------------------------------------------------------ SCENE 2D
    public  Stage stage;
    private Skin skin, battleSkin;

    // Groups
    private Group monsterStatusUI, gameOverUI;
    private BattleMainMenuWidget   mainMenu;
    private BattleActionMenuWidget actionMenu;
    private AttackMenuWidget       attackMenu;
    private MonsterIndicatorWidget indicatorMenu;
    private BattleAnimationWidget  animationWidget;
    private BattleStatusOverviewWidget statusWidget;

    // Labels
    private Label gameOverLabel;

    // Images
    private Image battleUIbg, blackCourtain;


    // --------------------------------------------------------------------------------------- OTHER
    // Not Scene2D related
    private Array<MonsterInBattle> team, oppTeam;   // hold monsters of one team
    private Array<MonsterInBattle> attackerQueue;

    private boolean allKO, allHeroKO;               // whether a whole team is KO

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
        setUpBattleAnimation();
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
            if (attackerQueue.size > 0 && !animationWidget.attackAnimationRunning) {
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
        allKO = false;
        allHeroKO = false;

        actionMenu.clearActions();
        mainMenu.clearActions();
        monsterStatusUI.clearActions();
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
                this.team.add(new MonsterInBattle(m, i, true));
                i++;
            }
        }

        // Opponent Team
        this.oppTeam = new Array<MonsterInBattle>();
        int j = 0;
        for(Monster m : opponentTeam.monsters) {
            if(m.getHP() > 0) {
                this.oppTeam.add(new MonsterInBattle(m, j, false));
                j++;
            }
        }

        this.aiPlayer = new AIPlayer();

        animationWidget.init(this.team, this.oppTeam);

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
        indicatorMenu.init(team,oppTeam);
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

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // ....................................................................................... SETUP
    /**
     * Initializes Attributes and especially Arrays and Maps
     */
    public void initializeAttributes() {
        this.allKO = false;
        this.attackerQueue = new Array<MonsterInBattle>();
        this.team = new Array<MonsterInBattle>();
        this.oppTeam = new Array<MonsterInBattle>();
        this.gameOverUI = new Group();
        this.aiPlayer = new AIPlayer();
    }

    /**
     * Here the order of elements on stage gets defined
     */
    private void addElementsToStage() {
        stage.addActor(battleUIbg);
        stage.addActor(animationWidget);
        stage.addActor(indicatorMenu);
        stage.addActor(mainMenu);
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

    private void setUpBattleAnimation() {
        animationWidget = new BattleAnimationWidget(game.media);
        animationWidget.addWidgetObserver(this);
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

        indicatorMenu = new MonsterIndicatorWidget(battleSkin);
        indicatorMenu.addWidgetObserver(this);
    }



    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /* .................................................................... CREATE UI ELEMENTS .. */
    public void setUpAttacksPane() {

        Array<Attack> attacks = team.get(indicatorMenu.chosenMember).monster.attacks;
        attackMenu = new AttackMenuWidget(battleSkin);
        attackMenu.init(attacks);
        attackMenu.addListenerToAllButtons(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    actionMenu.setGreenButtonDisabled(true);
                    attackMenu.addFadeOutAction(.5f);
                    attackMenu.remove();
                    indicatorMenu.deactivateChoice(true,indicatorMenu.chosenMember);
                }
            }
        );

        if (attacks.size >= 1)
            attackMenu.addListenerToButton(0, new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    lineUpForAttack(team.get(indicatorMenu.chosenMember), indicatorMenu.chosenOpponent, 0);
                }
            });

        if(attacks.size >= 2)
            attackMenu.addListenerToButton(1, new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    lineUpForAttack(team.get(indicatorMenu.chosenMember), indicatorMenu.chosenOpponent, 1);
                }
            });

        if(attacks.size >= 3)
            attackMenu.addListenerToButton(2, new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    lineUpForAttack(team.get(indicatorMenu.chosenMember), indicatorMenu.chosenOpponent, 2);
                }
            });

        if(attacks.size >= 4)
            attackMenu.addListenerToButton(3, new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    lineUpForAttack(team.get(indicatorMenu.chosenMember), indicatorMenu.chosenOpponent, 3);
                }
            });

        if(attacks.size >= 5)
            attackMenu.addListenerToButton(4, new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    lineUpForAttack(team.get(indicatorMenu.chosenMember), indicatorMenu.chosenOpponent, 4);
                }
            });

        if(attacks.size >= 6)
            attackMenu.addListenerToButton(5, new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    lineUpForAttack(team.get(indicatorMenu.chosenMember), indicatorMenu.chosenOpponent, 5);
                }
            });

        stage.addActor(attackMenu);
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /* ......................................................................... BATTLE METHODS ..*/

    public void carryOutAttack(MonsterInBattle attacker) {
        MonsterInBattle defender = (attacker.battleFieldPosition<3) ?
                oppTeam.get(attacker.nextTarget) : team.get(attacker.nextTarget);

        attacker.startAttack();    // attackStarted = Time, attackingRightNow=true

        // Animate Attack
        animationWidget.animateAttack(attacker.battleFieldPosition, attacker.nextTarget, attacker.battleFieldSide);

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
        }
    }

    /**
     * Checks whether the whole team is down and someone won
     * @param team
     * @return
     */
    public boolean checkIfWholeTeamKO(Array<MonsterInBattle> team) {
        return team.size == 0;
    }

    /**
     * Removes the monster with the given position on the battle field by setting it KO and
     * fading out the monster sprite and monsters status UI
     * @param m
     */
    private void kickOutMonster(MonsterInBattle m) {
        animationWidget.animateMonsterKO(m.battleFieldPosition,m.battleFieldSide);
        statusWidget.fadeStatusWidget(m.battleFieldPosition, m.battleFieldSide);

        System.out.println("Killed: " + m.battleFieldPosition);

        // Change the indicator position to an active fighter
        if(m.battleFieldSide)
            team.removeValue(m, true);      // Hero Team
        else
            oppTeam.removeValue(m, true);   // Opponent Team

        indicatorMenu.deactivateChoice(m.battleFieldSide,m.battleFieldPosition);
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
        } else {
            indicatorMenu.addFadeInAction(.3f);
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

    public void initStatusUI() {
        stage.addAction(Actions.fadeIn(1f));
    }


    private void resetUIElementVisibility() {
        // Visibility
        // Reset Status UIs

        actionMenu.addFadeOutAction(.1f);
        mainMenu.addFadeInWidgetAction(.1f);
        monsterStatusUI.addAction(Actions.alpha(1));
        monsterStatusUI.setVisible(true);

        blackCourtain.setVisible(true);
        gameOverUI.setVisible(false);
        stage.act(1);
    }

    @Override
    public void getNotified(ObservableWidget ow) {
        System.out.println("Got notified by " + ow.getClass());

        // Monster Indicator Widget
        if(ow instanceof MonsterIndicatorWidget) {
            MonsterIndicatorWidget miw = (MonsterIndicatorWidget) ow;
            // Indicator Position changed
            if(!team.get(miw.chosenMember).attackChosen)
                actionMenu.setGreenButtonDisabled(false);
            else
                actionMenu.setGreenButtonDisabled(true);
        }

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
     * Positions on the Battle Field
     */
    private final static class BatPos {
        private static int MID = 0;
        private static int TOP = 2;
        private static int BOT = 1;
        private static int[] positions = {0, 2, 1};
    }

    private enum BattleState {
        CHOOSING, BATTLEACTION,
    }
}
