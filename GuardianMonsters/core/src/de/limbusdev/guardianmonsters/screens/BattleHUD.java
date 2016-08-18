package de.limbusdev.guardianmonsters.screens;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.limbusdev.guardianmonsters.GuardianMonsters;
import de.limbusdev.guardianmonsters.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.model.Attack;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInBattle;
import de.limbusdev.guardianmonsters.ui.AttackMenuWidget;
import de.limbusdev.guardianmonsters.ui.BattleActionMenuWidget;
import de.limbusdev.guardianmonsters.ui.BattleAnimationWidget;
import de.limbusdev.guardianmonsters.ui.BattleMainMenuWidget;
import de.limbusdev.guardianmonsters.ui.EndOfBattleWidget;
import de.limbusdev.guardianmonsters.ui.BattleStatusOverviewWidget;
import de.limbusdev.guardianmonsters.ui.MonsterIndicatorWidget;
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
    private BattleMainMenuWidget   mainMenu;
    private BattleActionMenuWidget actionMenu;
    private AttackMenuWidget       attackMenu;
    private MonsterIndicatorWidget indicatorMenu;
    private BattleAnimationWidget  animationWidget;
    private BattleStatusOverviewWidget statusWidget;
    private EndOfBattleWidget      endOfBattleWidget;


    // Images
    private Image battleUIbg, blackCurtain;


    // --------------------------------------------------------------------------------------- OTHER
    // Not Scene2D related
    private Array<MonsterInBattle> team, oppTeam;   // hold monsters of one team
    private Array<MonsterInBattle> attackerQueue;

    private boolean allKO;                          // whether a whole team is KO
    private AIPlayer aiPlayer;                      // Artificial Intelligence

    // -------------------------------------------------------------------------------------- STATES
    // CHOOSING, BATTLE-ACTION
    private BattleState state = BattleState.MAINMENU;

    /* ........................................................................... CONSTRUCTOR .. */
    public BattleHUD(final GuardianMonsters game) {
        this.game = game;

        initializeAttributes();
        setUpUI();
        setUpTopLevelMenu();
        setUpBattleActionMenu();
        setUpEndOfBattleUI();
        addElementsToStage();
        attackMenu = new AttackMenuWidget(battleSkin);
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
    }

    /**
     * Update Method for state: BATTLEACTION
     */
    public void updateBattleAction(float delta) {
        // TODO
    }

    public void updateChoosing(float delta) {
        // TODO
    }

    /**
     * Handles all monsters in the queue and starts/finishes their attacks
     * ron only from updateBattleAction()
     */
    public void updateAttackerQueue() {
        if (attackerQueue.size > 0 && !animationWidget.attackAnimationRunning) {
            MonsterInBattle m = attackerQueue.pop();
            if (m.monster.getHP() > 0) carryOutAttack(m);
        } else if (attackerQueue.size == 0) {
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
        actionMenu.clearActions();
        mainMenu.clearActions();
        blackCurtain.clearActions();
    }

    /**
     * Initializes the battle screen with the given teams
     * @param team
     * @param opponentTeam
     */
    public void init(TeamComponent team, TeamComponent opponentTeam) {
        reset();

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

        statusWidget.init(this.team, this.oppTeam);
        animationWidget.init(this.team, this.oppTeam);
        indicatorMenu.init(this.team, this.oppTeam);

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
        blackCurtain.addAction(Actions.sequence(Actions.fadeOut(1), Actions.visible(false)));
        changeToWidgetSet(BattleState.MAINMENU);
    }

    /**
     * Refresh battle UI for new round
     */
    private void newRound() {
        changeToWidgetSet(BattleState.ACTIONMENU);
        indicatorMenu.init(team,oppTeam);
        for(MonsterInBattle m : team) m.attackChosen = false;
        for(MonsterInBattle m : oppTeam) m.attackChosen = false;
        attackerQueue.clear();
        aiPlayer.havePause(true);
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
        mainMenu.addFadeOutAction(.3f);

        // Set message
        if(heroLost) endOfBattleWidget.messageLabel.setText("Game Over");
        else endOfBattleWidget.messageLabel.setText("You won!");

        changeToWidgetSet(BattleState.ENDOFBATTLE);
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
        this.aiPlayer = new AIPlayer();
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

        // Battle UI Black transparent Background
        this.battleUIbg = new Image(battleSkin.getDrawable("bg"));
        battleUIbg.setPosition(0, 0);
        battleUIbg.setSize(GS.RES_X, GS.ROW*14);

        // Black Curtain for fade-in and -out
        this.blackCurtain = new Image(battleSkin.getDrawable("black"));
        this.blackCurtain.setSize(GS.RES_X,GS.RES_Y);
        this.blackCurtain.setPosition(0, 0);

        // Widgets
        statusWidget = new BattleStatusOverviewWidget(battleSkin);
        animationWidget = new BattleAnimationWidget(game.media);
        animationWidget.addWidgetObserver(this);
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
                    onTopLevelButtonFight();
                }
            }
        );

        mainMenu.addRunButtonListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    onTopLevelButtonRun();
                }
            }
        );

        mainMenu.addBagButtonListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    onTopLevelButtonBag();
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
                    changeToWidgetSet(BattleState.MAINMENU);
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
                changeToWidgetSet(BattleState.ATTACKMENU);
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

    /**
     * Here the order of elements on stage gets defined
     */
    private void addElementsToStage() {
        stage.addActor(battleUIbg);
        stage.addActor(animationWidget);
        stage.addActor(blackCurtain);
    }

    public void setUpAttacksPane() {
        Array<Attack> attacks = team.get(indicatorMenu.chosenMember).monster.attacks;
        attackMenu.init(attacks);
        attackMenu.addWidgetObserver(this);
    }

    private void setUpEndOfBattleUI() {
        endOfBattleWidget = new EndOfBattleWidget(battleSkin);
        endOfBattleWidget.backButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.popScreen();
                }
            }
        );
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

        // if Player has chosen attacks
        boolean allChosen = true;
        for(MonsterInBattle mib : team) allChosen = allChosen & mib.attackChosen;
        if(attackerQueue.size == countFitMonsters()) {
            aiPlayer.chooseAtttacks();
            changeToWidgetSet(BattleState.ANIMATION);
            attackerQueue.sort(new MonsterSpeedComparator());
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

    @Override
    public void getNotified(ObservableWidget ow) {

        // Monster Indicator Widget
        if(ow instanceof MonsterIndicatorWidget) {
            MonsterIndicatorWidget miw = (MonsterIndicatorWidget) ow;
            // Indicator Position changed
            if(!team.get(miw.chosenMember).attackChosen)
                actionMenu.setGreenButtonDisabled(false);
            else
                actionMenu.setGreenButtonDisabled(true);
        }

        // Attack Menu Widget
        if(ow instanceof AttackMenuWidget) {
            AttackMenuWidget amw = (AttackMenuWidget) ow;
            onAttackMenuButton(amw.chosenAttack);
        }
    }

    /**
     * Fades out current widget and changes to another one, fading it in
     * @param state
     */
    private void changeToWidgetSet(BattleState state) {
        mainMenu.remove();
        attackMenu.remove();
        statusWidget.remove();
        attackMenu.remove();
        indicatorMenu.remove();
        actionMenu.remove();

        switch(state) {
            case ACTIONMENU:
                stage.addActor(actionMenu);
                stage.addActor(indicatorMenu);
                stage.addActor(statusWidget);
                break;
            case ATTACKMENU:
                setUpAttacksPane();
                stage.addActor(indicatorMenu);
                stage.addActor(statusWidget);
                stage.addActor(attackMenu);
                break;
            case ANIMATION:
                break;
            default:
                stage.addActor(mainMenu);
                break;
        }
        this.state = state;
    }

    // ................................................................................... CALLBACKS
    private void onTopLevelButtonFight() {
        System.out.println("Input: Button Fight");
        changeToWidgetSet(BattleState.MAINMENU);
        aiPlayer.havePause(false);
        newRound();
    }

    private void onTopLevelButtonRun() {
        System.out.println("Input: Button Escape");
        blackCurtain.addAction(Actions.sequence(Actions.visible(true), Actions.fadeIn(1),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    game.popScreen();
                }
            })));
    }

    private void onTopLevelButtonBag() {
        // TODO
        System.out.println("Input: Button Bag");
    }

    private void onAttackMenuButton(int nr) {
        actionMenu.setGreenButtonDisabled(true);
        changeToWidgetSet(BattleState.ACTIONMENU);
        indicatorMenu.deactivateChoice(true,indicatorMenu.chosenMember);
        lineUpForAttack(team.get(indicatorMenu.chosenMember), indicatorMenu.chosenOpponent, nr);
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

        public void chooseAtttacks() {
            // Choose targets and attacks
            for(int i=0; i<oppTeam.size; i++) {
                attack(oppTeam.get(i));
            }
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
        MAINMENU, ACTIONMENU, ATTACKMENU, ENDOFBATTLE, ANIMATION,
    }
}
