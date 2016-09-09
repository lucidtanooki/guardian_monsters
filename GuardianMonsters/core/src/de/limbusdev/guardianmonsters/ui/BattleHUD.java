package de.limbusdev.guardianmonsters.ui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.GuardianMonsters;
import de.limbusdev.guardianmonsters.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.enums.ButtonIDs;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInBattle;
import de.limbusdev.guardianmonsters.model.MonsterInformation;
import de.limbusdev.guardianmonsters.screens.BattleScreen;
import de.limbusdev.guardianmonsters.utils.BattleStringBuilder;
import de.limbusdev.guardianmonsters.utils.GS;
import de.limbusdev.guardianmonsters.utils.MonsterManager;
import de.limbusdev.guardianmonsters.utils.MonsterSpeedComparator;

/**
 * BattleHUD manages all actions and UI elements in the {@link BattleScreen}
 *
 * Created by georg on 03.12.15.
 */
public class BattleHUD extends ABattleHUD implements WidgetObserver {

    /* ............................................................................ ATTRIBUTES .. */

    // ------------------------------------------------------------------------------------ SCENE 2D

    // Groups
    private BattleMainMenuWidget        mainMenu;
    private BattleActionMenuWidget      actionMenu;
    private AttackMenuWidget            attackMenu;
    private MonsterIndicatorWidget      indicatorMenu;
    private BattleAnimationWidget       animationWidget;
    private BattleStatusOverviewWidget  statusWidget;
    private EndOfBattleWidget           endOfBattleWidget;


    // --------------------------------------------------------------------------------------- OTHER
    // Not Scene2D related
    private ArrayMap<Integer,MonsterInBattle> heroTeam, oppoTeam;   // hold monsters of one heroTeam
    private Array<MonsterInBattle> animationQueue, attackApplicationQueue;

    private AIPlayer aiPlayer;                      // Artificial Intelligence

    // -------------------------------------------------------------------------------------- STATES
    // CHOOSING, BATTLE-ACTION
    private BattleState state = BattleState.MAINMENU;

    /* ........................................................................... CONSTRUCTOR .. */
    public BattleHUD(final GuardianMonsters game) {
        super(game, game.media.battleSkin);
        initializeAttributes();
        setUpUI();
    }

    /* ............................................................................... METHODS .. */

    // ################################################################################### GAME LOOP

    @Override
    public void update(float delta) {
        super.update(delta);

        switch(this.state) {
            case ANIMATION:
                updateQueues();
                break;
        }
    }

    /**
     * Handles all monsters in the queue and starts/finishes their attacks
     * ron only from updateBattleAction()
     */
    private void updateQueues() {
        // Don't proceed when all monsters of one heroTeam are defeated
        if(checkIfWholeTeamKO(heroTeam) || checkIfWholeTeamKO(oppoTeam)) {
            changeToWidgetSet(BattleState.ENDOFBATTLE);
            return;
        }

        if (animationQueue.size > 0 && !animationWidget.attackAnimationRunning) {
            MonsterInBattle m = animationQueue.pop();
            if (m.monster.getHP() > 0)
                startAttackAnimation(m);
        } else if (animationQueue.size == 0 && attackApplicationQueue.size == 0) {
            newRound();
        }
    }

    // #############################################################################################
    // ............................................................................ INITIALIZE ARENA

    /**
     * Initializes Attributes and especially Arrays and Maps
     */
    private void initializeAttributes() {
        this.animationQueue = new Array<MonsterInBattle>();
        this.attackApplicationQueue = new Array<MonsterInBattle>();
        this.heroTeam = new ArrayMap<Integer,MonsterInBattle>();
        this.oppoTeam = new ArrayMap<Integer,MonsterInBattle>();
        this.aiPlayer = new AIPlayer();
    }

    /**
     * Resets the UI into a state where it can be initialized for a new battle
     */
    @Override
    protected void reset() {
        super.reset();
        actionMenu.clearActions();
        mainMenu.clearActions();
        animationQueue.clear();
        attackApplicationQueue.clear();
        heroTeam.clear();
        oppoTeam.clear();
    }

    /**
     * Initializes the battle screen with the given teams
     * @param team
     * @param opponentTeam
     */
    public void init(TeamComponent team, TeamComponent opponentTeam) {
        // Resetting Attributes for new Battle
        reset();

        // Hero Team
        int i = 0;
        for(Monster m : team.monsters) {
            if(m.getHP() > 0) {
                this.heroTeam.put(i,new MonsterInBattle(m, i, true));
                i++;
            }
        }

        // Opponent Team
        int j = 0;
        for(Monster m : opponentTeam.monsters) {
            if(m.getHP() > 0) {
                this.oppoTeam.put(j,new MonsterInBattle(m, j, false));
                j++;
            }
        }

        this.aiPlayer = new AIPlayer();

        statusWidget.init(this.heroTeam, this.oppoTeam);
        animationWidget.init(this.heroTeam, this.oppoTeam);
        indicatorMenu.init(this.heroTeam, this.oppoTeam);

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

    @Override
    public void show() {
        super.show();
        changeToWidgetSet(BattleState.MAINMENU);
    }

    /**
     * Refresh battle UI for new round
     */
    private void newRound() {
        changeToWidgetSet(BattleState.ACTIONMENU);
        indicatorMenu.init(heroTeam, oppoTeam);
        actionMenu.reset();
        for(Integer i : heroTeam.keys()) heroTeam.get(i).newRound();
        for(Integer i : oppoTeam.keys()) oppoTeam.get(i).newRound();
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
        for(Integer i : heroTeam.keys())
            if(!heroTeam.get(i).KO) heroLost = false;

        // Hide UI Elements
        actionMenu.addFadeOutAction(.5f);
        mainMenu.addFadeOutAction(.3f);

        // Set message
        if(heroLost) endOfBattleWidget.messageLabel.setText("Game Over");
        else endOfBattleWidget.messageLabel.setText("You won!");

        changeToWidgetSet(BattleState.ENDOFBATTLE);
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // ....................................................................................... SETUP

    /**
     * Setting up HUD elements:
     */
    public void setUpUI() {

        // Widgets
        statusWidget = new BattleStatusOverviewWidget(this, skin);

        animationWidget = new BattleAnimationWidget(this, game.media);
        animationWidget.addWidgetObserver(this);

        mainMenu = new BattleMainMenuWidget(this, skin);

        actionMenu = new BattleActionMenuWidget(this, skin);
        actionMenu.greenButton.setText("Attack");

        indicatorMenu = new MonsterIndicatorWidget(this, skin);
        indicatorMenu.addWidgetObserver(this);

        endOfBattleWidget = new EndOfBattleWidget(this, skin);

        attackMenu = new AttackMenuWidget(this, skin);
        attackMenu.addWidgetObserver(this);
    }


    public void setUpAttacksPane() {
        attackMenu.init(heroTeam.get(indicatorMenu.chosenMember).monster);
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
        defendingTeam = attacker.battleFieldSide ? oppoTeam : heroTeam;

        if(checkIfWholeTeamKO(defendingTeam))
            throw new IllegalStateException("The whole defending heroTeam is KO, battle should be over.");

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
        animationWidget.animateAttack(attacker.battleFieldPosition, attacker.nextTarget, attacker.battleFieldSide, attacker.nextAttack);
        attackApplicationQueue.add(attacker);
    }

    private void applyAttack(MonsterInBattle attacker) {
        MonsterInBattle defender = attacker.battleFieldSide ?
            oppoTeam.get(attacker.nextTarget) : heroTeam.get(attacker.nextTarget);
        MonsterManager.calcAttack(attacker.monster, defender.monster, attacker.nextAttack);
        if(defender.monster.getHP() == 0) {
            defender.KO = true;
            kickOutMonster(defender);
        }
    }

    /**
     * Checks whether the whole heroTeam is down and someone won
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
            heroTeam.removeValue(m, true);      // Hero Team
        else
            oppoTeam.removeValue(m, true);   // Opponent Team

        // Spread EXP
        if(!m.battleFieldSide) {
            // Defeated Monster was part of opponents heroTeam
            int exp = m.monster.level * (m.monster.getHPfull() + m.monster.physStrength);
            exp /= heroTeam.size;
            for(Integer i : heroTeam.keys())
                if(heroTeam.get(i).monster.getHP() > 0)
                    MonsterManager.earnEXP(heroTeam.get(i).monster,exp);
        }

    }

    /**
     * Put monster in line
     * @param m
     */
    public void lineUpForAttack(MonsterInBattle m, int chosenTarget, int attack) {
        m.prepareForAttack(chosenTarget, attack);

        if(GS.DEBUGGING_ON) BattleStringBuilder.printEnqueuedMonster(m,chosenTarget,attack);

        animationQueue.add(m);

        if(m.battleFieldSide) {
            // if Player has chosen attacks
            boolean allChosen = true;
            for (Integer i : heroTeam.keys()) {
                MonsterInBattle mib = heroTeam.get(i);
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

    @Override
    public void getNotified(ObservableWidget ow) {

        // Monster Indicator Widget
        if(ow instanceof MonsterIndicatorWidget) {
            onIndicatorMenuUpdate((MonsterIndicatorWidget)ow);
        }

        // Attack Menu Widget
        if(ow instanceof AttackMenuWidget) {
            AttackMenuWidget amw = (AttackMenuWidget) ow;
            onAttackMenuButton(amw.chosenAttack);
        }

        if(ow instanceof BattleAnimationWidget) {
            onBattleAnimationUpdate((BattleAnimationWidget) ow);
        }
    }

    /**
     * Fades out current widget and changes to another one, fading it in
     * @param state
     */
    private void changeToWidgetSet(BattleState state) {

        animationWidget.remove();
        animationWidget.addToStage(stage);
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
                endOfBattleWidget.init(!checkIfWholeTeamKO(heroTeam));
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

    @Override
    public void onButtonClicked(ButtonIDs id) {
        switch(id) {
            case TOP_LEVEL_SWORD:   onTopLevelButtonFight();break;
            case TOP_LEVEL_RUN:     onTopLevelButtonRun();  break;
            case TOP_LEVEL_BAG:     onTopLevelButtonBag();  break;
            case EOB_BACK:          onEndOfBattleBack();    break;
            case ACTION_ATTACK:     onActionMenuAttack();   break;
            case ACTION_BACK:       onActionMenuBack();     break;
            case ACTION_BLUE:       onActionMenuBlue();     break;
            case ACTION_GREY_L:     onActionMenuGreyL();    break;
            case ACTION_GREY_R:     onActionMenuGreyR();    break;
            default:                System.out.println("Button " + id + " unknown.");
        }
    }

    /**
     * Empty implementation, because using the other method only
     * @param id
     */
    @Override
    public void onButtonClicked(int id) {}

    // Listener Notifications
    private void onIndicatorMenuUpdate(MonsterIndicatorWidget miw) {
        // Indicator Position changed
        if(!heroTeam.get(miw.chosenMember).attackChosen) {
            actionMenu.setGreenButtonDisabled(false);
        } else {
            actionMenu.setGreenButtonDisabled(true);
        }
        if(state == BattleState.ATTACKMENU) changeToWidgetSet(BattleState.ATTACKMENU);
    }

    private void onBattleAnimationUpdate(BattleAnimationWidget baw) {
        MonsterInBattle mib = attackApplicationQueue.pop();
        MonsterInBattle defender = mib.battleFieldSide ? oppoTeam.get(mib.nextTarget) : heroTeam.get(mib.nextTarget);
        actionMenu.infoLabel.setText(
            MonsterInformation.getInstance().monsterNames.get(mib.monster.ID-1)
                + " attacks "
                + MonsterInformation.getInstance().monsterNames.get(defender.monster.ID-1)
                + " with "
                + mib.nextAttack.name
        );
        applyAttack(mib);
    }

    // Buttons
    private void onTopLevelButtonFight() {
        System.out.println("Input: Button Fight");
        changeToWidgetSet(BattleState.ACTIONMENU);
        aiPlayer.havePause(false);
    }

    private void onTopLevelButtonRun() {
        System.out.println("Input: Button Escape");
        goToPreviousScreen();
    }

    private void onTopLevelButtonBag() {
        System.out.println("Input: Button Bag");
    }

    private void onAttackMenuButton(int nr) {
        System.out.println("Input: User chose attack Nr. " + nr);
        changeToWidgetSet(BattleState.ACTIONMENU);
        if(nr >= 0) {
            actionMenu.setGreenButtonDisabled(true);
            lineUpForAttack(heroTeam.get(indicatorMenu.chosenMember), indicatorMenu.chosenOpponent, nr);
            indicatorMenu.deactivateChoice(true, indicatorMenu.chosenMember);
        }
    }

    private void onActionMenuAttack() {
        if(actionMenu.greenButton.isDisabled()) return;
        changeToWidgetSet(BattleState.ATTACKMENU);
    }

    private void onActionMenuBack() {
        changeToWidgetSet(BattleState.MAINMENU);
        aiPlayer.havePause(true);
    }

    private void onActionMenuGreyL() {
        System.out.println("Input: Action Menu - Grey L");
    }

    private void onActionMenuGreyR() {
        System.out.println("Input: Action Menu - Grey R");
    }

    private void onActionMenuBlue() {
        System.out.println("Input: Action Menu - Blue");
    }

    protected void onEndOfBattleBack() {
        if(checkIfWholeTeamKO(heroTeam)) game.create();
        else game.popScreen();
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
            for(Integer key : oppoTeam.keys()) {
                attack(oppoTeam.get(key));
            }
            havePause(true);
        }

        public void havePause(boolean paused) {
            this.AIpaused = paused;
        }

        public void checkEnemiesDeath() {
            /* Check if all enemies are KO */
            if(heroTeam.size == 0) handleEndOfBattle();
        }

        public void attack(MonsterInBattle m) {
            lineUpForAttack(
                m,
                MathUtils.random(heroTeam.size-1),
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
