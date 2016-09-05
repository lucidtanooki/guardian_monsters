package de.limbusdev.guardianmonsters.screens;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.limbusdev.guardianmonsters.GuardianMonsters;
import de.limbusdev.guardianmonsters.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.model.Attack;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInBattle;
import de.limbusdev.guardianmonsters.model.MonsterInformation;
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
    private ArrayMap<Integer,MonsterInBattle> team, oppTeam;   // hold monsters of one team
    private Array<MonsterInBattle> animationQueue, attackApplicationQueue;

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
        switch(this.state) {
            case ANIMATION:
                updateAttackerQueue();
                break;
        }
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
        if(checkIfWholeTeamKO(team) || checkIfWholeTeamKO(oppTeam)) {
            changeToWidgetSet(BattleState.ENDOFBATTLE);
            return;
        }
        if (animationQueue.size > 0 && !animationWidget.attackAnimationRunning) {
            MonsterInBattle m = animationQueue.pop();
            if (m.monster.getHP() > 0) startAttackAnimation(m);
        } else if (animationQueue.size == 0 && attackApplicationQueue.size == 0) {
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
        this.team = new ArrayMap<Integer,MonsterInBattle>();
        int i = 0;
        for(Monster m : team.monsters) {
            if(m.getHP() > 0) {
                this.team.put(i,new MonsterInBattle(m, i, true));
                i++;
            }
        }

        // Opponent Team
        this.oppTeam = new ArrayMap<Integer,MonsterInBattle>();
        int j = 0;
        for(Monster m : opponentTeam.monsters) {
            if(m.getHP() > 0) {
                this.oppTeam.put(j,new MonsterInBattle(m, j, false));
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
        actionMenu.reset();
        for(Integer i : team.keys()) team.get(i).newRound();
        for(Integer i : oppTeam.keys()) oppTeam.get(i).newRound();
        animationQueue.clear();
        attackApplicationQueue.clear();
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
        for(Integer i : team.keys())
            if(!team.get(i).KO) heroLost = false;

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
    }



    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // ....................................................................................... SETUP
    /**
     * Initializes Attributes and especially Arrays and Maps
     */
    public void initializeAttributes() {
        this.allKO = false;
        this.animationQueue = new Array<MonsterInBattle>();
        this.attackApplicationQueue = new Array<MonsterInBattle>();
        this.team = new ArrayMap<Integer,MonsterInBattle>();
        this.oppTeam = new ArrayMap<Integer,MonsterInBattle>();
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
                    if(checkIfWholeTeamKO(team)) game.create();
                    else game.popScreen();
                }
            }
        );
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /* ......................................................................... BATTLE METHODS ..*/

    /**
     * After starting animation this method adds the attacker to the attack application queue
     * @param attacker
     * @throws IllegalStateException
     */
    public void startAttackAnimation(final MonsterInBattle attacker) throws IllegalStateException {

        // Check if attacker and defender are still there
        if(attacker.monster.getHP() == 0) return;

        ArrayMap<Integer,MonsterInBattle> defendingTeam;
        defendingTeam = attacker.battleFieldSide ? oppTeam : team;

        if(checkIfWholeTeamKO(defendingTeam))
            throw new IllegalStateException("The whole defending team is KO, battle should be over.");

        if(!defendingTeam.containsKey(attacker.nextTarget)) {
            // Target defeated already, choosing new target
            for(Integer key : defendingTeam.keys()) {
                if(!defendingTeam.get(key).KO) {
                    attacker.nextTarget = key;
                    break;
                }
            }
        }

        // Animate Attack
        animationWidget.animateAttack(attacker.battleFieldPosition, attacker.nextTarget, attacker.battleFieldSide);
        attackApplicationQueue.add(attacker);
    }

    private void applyDamage(MonsterInBattle attacker) {
        MonsterInBattle defender = attacker.battleFieldSide ? oppTeam.get(attacker.nextTarget) : team.get(attacker.nextTarget);
        /* Calculate Damage */
        if (defender.monster.getHP() - attacker.nextAttack.damage < 0) defender.monster.setHP(0);
        else defender.monster.setHP(defender.monster.getHP() - attacker.nextAttack.damage);

        // Handle Attack
        handleAttack(attacker, defender);

        // Choose Team
        if(attacker.battleFieldSide) {
            /* Check if all enemies are KO */
            this.allKO = checkIfWholeTeamKO(oppTeam);
        }
    }

    /**
     * Checks whether the whole team is down and someone won
     * @param team
     * @return
     */
    public boolean checkIfWholeTeamKO(ArrayMap<Integer,MonsterInBattle> team) {
        for(Integer i : team.keys()) {
            if(!team.get(i).KO) {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes the monster with the given position on the battle field by setting it KO and
     * fading out the monster sprite and monsters status UI
     * @param m
     */
    private void kickOutMonster(MonsterInBattle m) {
        animationWidget.animateMonsterKO(m.battleFieldPosition,m.battleFieldSide);
        statusWidget.fadeStatusWidget(m.battleFieldPosition, m.battleFieldSide);
        actionMenu.infoLabel.setText(MonsterInformation.getInstance().monsterNames.get(m.monster.ID-1) + " defeated.");

        System.out.println("Killed: " + m.battleFieldPosition);

        // Change the indicator position to an active fighter
        if(m.battleFieldSide)
            team.removeValue(m, true);      // Hero Team
        else
            oppTeam.removeValue(m, true);   // Opponent Team

        // Spread EXP
        if(!m.battleFieldSide) {
            // Defeated Monster was part of opponents team
            int exp = m.monster.level * (m.monster.getHPfull() + m.monster.physStrength);
            exp /= team.size;
            for(Integer i : team.keys())
                if(team.get(i).monster.getHP() > 0)
                    MonsterManager.earnEXP(team.get(i).monster,exp);
        }

    }

    /**
     * Put monster in line
     * @param m
     */
    public void lineUpForAttack(MonsterInBattle m, int chosenTarget, int attack) {
        m.prepareForAttack(chosenTarget, attack);


        if(GS.DEBUGGING_ON) {
            System.out.println("\n----- Monster Indicator Widget -----");
            System.out.println("Team: " + (m.battleFieldSide ? "Hero" : "Opponent"));
            System.out.println("Chosen Member: " + m.battleFieldPosition);
            System.out.println("Chosen Opponent: " + chosenTarget);
            System.out.println("----- lineUpForAttack()        -----");
            System.out.println("Position: " + m.battleFieldPosition);
            System.out.println("Ready: " + m.attackStarted + " | Attack: " + m.monster.attacks.get(attack).name + " | Target: " + chosenTarget
                    + " | Attack chosen: " + m.attackChosen);
            System.out.println();
        }

        animationQueue.add(m);

        if(m.battleFieldSide) {
            // if Player has chosen attacks
            boolean allChosen = true;
            for (Integer i : team.keys()) {
                MonsterInBattle mib = team.get(i);
                System.out.println("Chosen? " + mib.attackChosen);
                allChosen = allChosen & mib.attackChosen;
            }
            if (allChosen) {
                aiPlayer.chooseAttacks();
                changeToWidgetSet(BattleState.ANIMATION);
                animationQueue.sort(new MonsterSpeedComparator());
            }
        }
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
            if(!team.get(miw.chosenMember).attackChosen) {
                actionMenu.setGreenButtonDisabled(false);
            } else {
                actionMenu.setGreenButtonDisabled(true);
            }
            if(state == BattleState.ATTACKMENU) changeToWidgetSet(BattleState.ATTACKMENU);
        }

        // Attack Menu Widget
        if(ow instanceof AttackMenuWidget) {
            AttackMenuWidget amw = (AttackMenuWidget) ow;
            onAttackMenuButton(amw.chosenAttack);
        }

        if(ow instanceof BattleAnimationWidget) {
            MonsterInBattle mib = attackApplicationQueue.pop();
            MonsterInBattle defender = mib.battleFieldSide ? oppTeam.get(mib.nextTarget) : team.get(mib.nextTarget);
                actionMenu.infoLabel.setText(
                MonsterInformation.getInstance().monsterNames.get(mib.monster.ID-1)
                    + " attacks "
                    + MonsterInformation.getInstance().monsterNames.get(defender.monster.ID-1)
                    + " with "
                    + mib.nextAttack.name
            );
            applyDamage(mib);
        }
    }

    /**
     * Fades out current widget and changes to another one, fading it in
     * @param state
     */
    private void changeToWidgetSet(BattleState state) {

        endOfBattleWidget.remove();
        mainMenu.fadeOutAndRemove();
        if(this.state != BattleState.ATTACKMENU) attackMenu.fadeOutAndRemove();
        else attackMenu.remove();
        statusWidget.remove();
        if(this.state == BattleState.ACTIONMENU || this.state == BattleState.ATTACKMENU) indicatorMenu.remove();
        else indicatorMenu.fadeOutAndRemove();
        actionMenu.fadeOutAndRemove();

        switch(state) {
            case ACTIONMENU:
                actionMenu.addToStageAndFadeIn(stage);
                statusWidget.addToStage(stage);
                if(this.state == BattleState.ATTACKMENU) {
                    indicatorMenu.addToStage(stage);
                } else {
                    indicatorMenu.addToStageAndFadeIn(stage);
                }
                break;
            case ATTACKMENU:
                setUpAttacksPane();
                stage.addActor(indicatorMenu);
                stage.addActor(statusWidget);
                if(!(this.state == BattleState.ATTACKMENU)) attackMenu.addToStageAndFadeIn(stage);
                else attackMenu.addToStage(stage);
                break;
            case ANIMATION:
                actionMenu.addToStage(stage);
                actionMenu.fadeOutExceptInfoLabel(stage);
                statusWidget.addToStage(stage);
                break;
            case ENDOFBATTLE:
                endOfBattleWidget.init(!checkIfWholeTeamKO(team));
                endOfBattleWidget.addToStageAndFadeIn(stage);
                break;
            default:
                mainMenu.addToStageAndFadeIn(stage);
                if(!(this.state == BattleState.ACTIONMENU)) statusWidget.addToStageAndFadeIn(stage);
                else statusWidget.addToStage(stage);
                break;
        }
        this.state = state;
    }

    // ................................................................................... CALLBACKS
    private void onTopLevelButtonFight() {
        System.out.println("Input: Button Fight");
        changeToWidgetSet(BattleState.ACTIONMENU);
        aiPlayer.havePause(false);
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
        System.out.println("onAttackMenuButton(" + nr + ")");
        changeToWidgetSet(BattleState.ACTIONMENU);
        if(nr >= 0) {
            actionMenu.setGreenButtonDisabled(true);
            lineUpForAttack(team.get(indicatorMenu.chosenMember), indicatorMenu.chosenOpponent, nr);
            indicatorMenu.deactivateChoice(true, indicatorMenu.chosenMember);
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

        public void chooseAttacks() {
            // Choose targets and attacks
            for(Integer key : oppTeam.keys()) {
                attack(oppTeam.get(key));
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
                m,
                MathUtils.random(team.size-1),
                MathUtils.random(m.monster.attacks.size-1));
            checkEnemiesDeath();
        }

        public boolean isAIpaused() {
            return AIpaused;
        }
    }

    private enum BattleState {
        MAINMENU, ACTIONMENU, ATTACKMENU, ENDOFBATTLE, ANIMATION,
    }
}
