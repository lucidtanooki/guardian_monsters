package de.limbusdev.guardianmonsters.fwmengine.battle.ui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.AttackMenuWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleActionMenuWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleAnimationWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleQueueWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.MonsterIndicatorWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.WidgetObserver;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.enums.ButtonIDs;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.AttackCalculationReport;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.MonsterInBattle;
import de.limbusdev.guardianmonsters.model.MonsterInformation;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleMainMenuWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleStatusOverviewWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.EndOfBattleWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.ObservableWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.control.BattleStringBuilder;
import de.limbusdev.guardianmonsters.utils.GS;
import de.limbusdev.guardianmonsters.fwmengine.battle.control.MonsterManager;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.MonsterSpeedComparator;

/**
 * BattleHUD manages all actions and UI elements in the {@link BattleScreen}
 *
 * Created by georg on 03.12.15.
 */
public class BattleHUD extends ABattleHUD implements WidgetObserver {

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ ATTRIBUTES

    // ....................................................................... scene2d
    // Groups
    private BattleMainMenuWidget        mainMenu;
    private BattleActionMenuWidget      actionMenu;
    private AttackMenuWidget            attackMenu;
    private MonsterIndicatorWidget      indicatorMenu;
    private BattleAnimationWidget       animationWidget;
    private BattleStatusOverviewWidget  statusWidget;
    private EndOfBattleWidget           endOfBattleWidget;
    private BattleQueueWidget           battleQueueWidget;


    // ......................................................................... other
    // Not Scene2D related
    private ArrayMap<Integer,MonsterInBattle> heroTeam;
    private ArrayMap<Integer,MonsterInBattle> oppoTeam;
    private Array<MonsterInBattle> animationQueue;
    private Array<MonsterInBattle> attackApplicationQueue;

    private AIPlayer aiPlayer;
    private BattleState state;

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ CONSTRUCTOR
    public BattleHUD() {
        super(Services.getUI().getBattleSkin());
        initializeAttributes();
        setUpUI();
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ METHODS

    // ..................................................................... game loop

    @Override
    public void update(float delta) {
        super.update(delta);
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


    // .............................................................. initialize arena

    private void initializeAttributes() {
        this.animationQueue = new Array<MonsterInBattle>();
        this.attackApplicationQueue = new Array<MonsterInBattle>();
        this.heroTeam = new ArrayMap<Integer,MonsterInBattle>();
        this.oppoTeam = new ArrayMap<Integer,MonsterInBattle>();
        this.aiPlayer = new AIPlayer();
        this.state    = BattleState.MAINMENU;
    }

    /**
     * Resets the UI into a state where it can be initialized for a new battle
     */
    @Override
    protected void reset() {
        super.reset();
        actionMenu.clearActions();
        mainMenu.clearActions();
        initializeAttributes();
    }

    /**
     * Initializes the battle screen with the given teams
     * @param team
     * @param opponentTeam
     */
    public void init(TeamComponent team, TeamComponent opponentTeam) {
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
        indicatorMenu.init(this.oppoTeam);

        show();
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // .............................................................................. LIBGDX METHODS
    /**
     * Action that should take place when the screen gets hidden
     */
    public void hide() {
        stage.act(100);
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
        indicatorMenu.init(oppoTeam);
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
            if(!(heroTeam.get(i).monster.getHP() == 0))
                heroLost = false;

        // Hide UI Elements
        actionMenu.addFadeOutAction(.5f);
        mainMenu.addFadeOutAction(.3f);

        // Set message
        if(heroLost) endOfBattleWidget.messageLabel.setText(Services.getL18N().l18n().get("batt_game_over"));
        else         endOfBattleWidget.messageLabel.setText(Services.getL18N().l18n().get("batt_you_won"));

        changeToWidgetSet(BattleState.ENDOFBATTLE);
    }


    // ........................................................................ setup

    /**
     * Setting up HUD elements:
     */
    public void setUpUI() {

        // Widgets
        statusWidget      = new BattleStatusOverviewWidget(this, skin);

        animationWidget   = new BattleAnimationWidget(this);
        animationWidget   .addWidgetObserver(this);

        mainMenu          = new BattleMainMenuWidget(this, skin);

        actionMenu        = new BattleActionMenuWidget(this, skin);
//        actionMenu        .greenButton.setText(Services.getL18N().l18n().get("batt_attack"));

        indicatorMenu     = new MonsterIndicatorWidget(this, skin, Align.bottomRight);
        indicatorMenu.setPosition(GS.RES_X-1*GS.zoom,82*GS.zoom,Align.bottomLeft);
        indicatorMenu.setScale(GS.zoom);
        indicatorMenu     .addWidgetObserver(this);

        endOfBattleWidget = new EndOfBattleWidget(this, skin);

        attackMenu        = new AttackMenuWidget(this, skin);
        attackMenu        .addWidgetObserver(this);

        battleQueueWidget = new BattleQueueWidget(this, skin, Align.bottomLeft);
        battleQueueWidget.setScale(GS.zoom);
        battleQueueWidget.setPosition(GS.zoom,70*GS.zoom, Align.bottomLeft);
    }



    // ............................................................... battle methods

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

        animationWidget.animateAttack(
            attacker.battleFieldPosition,
            attacker.nextTarget,
            attacker.battleFieldSide,
            attacker.nextAttack);

        attackApplicationQueue.add(attacker);
    }

    private AttackCalculationReport applyAttack(MonsterInBattle attacker) {
        MonsterInBattle defender = attacker.battleFieldSide ?
            oppoTeam.get(attacker.nextTarget) : heroTeam.get(attacker.nextTarget);
        AttackCalculationReport rep = MonsterManager.calcAttack(
            attacker.monster, defender.monster, attacker.nextAttack);
        if(defender.monster.getHP() == 0) {
            defender.KO = true;
            kickOutMonster(defender);
        }

        return rep;
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
        actionMenu.infoLabel.setText(MonsterInformation.getInstance().monsterNames.get(m.monster.ID-1) + " " + Services.getL18N().l18n().get("batt_defeated") + ".");

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
                //animationQueue.sort(new MonsterSpeedComparator());
                updateQueues();
            }
        }
    }


    // ....................................................................... ui

    @Override
    public void getNotified(ObservableWidget ow) {

        if(ow instanceof MonsterIndicatorWidget) {
            onIndicatorMenuUpdate((MonsterIndicatorWidget)ow);
        }

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
                attackMenu.addToStageAndFadeIn(stage);
                battleQueueWidget.addToStageAndFadeIn(stage);
                Array monsters = new Array();
                monsters.addAll(heroTeam.values().toArray());
                monsters.addAll(oppoTeam.values().toArray());
                battleQueueWidget.init(monsters);
                if(this.state == BattleState.ATTACKMENU) {
                    indicatorMenu.addToStage(stage);
                } else {
                    indicatorMenu.addToStageAndFadeIn(stage);
                }
                break;

            case ATTACKMENU:
                attackMenu.init(heroTeam.get(indicatorMenu.chosen).monster);
                stage.addActor(indicatorMenu);
                stage.addActor(statusWidget);
                if(!(this.state == BattleState.ATTACKMENU)) attackMenu.addToStageAndFadeIn(stage);
                else attackMenu.addToStage(stage);
                break;

            case ANIMATION:
                actionMenu.addToStage(stage);
                actionMenu.fadeOutExceptInfoLabel(stage);
                statusWidget.addToStage(stage);
                battleQueueWidget.addToStage(stage);
                break;

            case ENDOFBATTLE:
                endOfBattleWidget.init(!checkIfWholeTeamKO(heroTeam));
                endOfBattleWidget.addToStageAndFadeIn(stage);
                statusWidget.addToStage(stage);
                break;

            default:
                mainMenu.addToStageAndFadeIn(stage);
                if(!(this.state == BattleState.ACTIONMENU)) statusWidget.addToStageAndFadeIn(stage);
                else statusWidget.addToStage(stage);
                break;
        }
        this.state = state;
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ CALLBACKS

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
            case ACTION_NEXT:       onActionMenuNext();     break;
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
    private void onIndicatorMenuUpdate(de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.MonsterIndicatorWidget miw) {
        // Indicator Position changed
        if(!heroTeam.get(miw.chosen).attackChosen) {
            actionMenu.setGreenButtonDisabled(false);
        } else {
            actionMenu.setGreenButtonDisabled(true);
        }
        if(state == BattleState.ATTACKMENU) changeToWidgetSet(BattleState.ATTACKMENU);
    }

    private void onBattleAnimationUpdate(de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleAnimationWidget baw) {
        MonsterInBattle mib = attackApplicationQueue.pop();
        MonsterInBattle defender = mib.battleFieldSide ?
            oppoTeam.get(mib.nextTarget) : heroTeam.get(mib.nextTarget);
        AttackCalculationReport report = applyAttack(mib);

        actionMenu.infoLabel.setText(BattleStringBuilder.givenDamage(mib.monster, defender.monster, report));
        animationWidget.nextAnimation();
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
            lineUpForAttack(heroTeam.get(indicatorMenu.chosen), indicatorMenu.chosen, nr);
            indicatorMenu.deactivateChoice(indicatorMenu.chosen);
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

    private void onActionMenuNext() {
        System.out.println("Input: Action Menu - Next");
        updateQueues();
    }

    protected void onEndOfBattleBack() {
        if(checkIfWholeTeamKO(heroTeam)) Services.getScreenManager().getGame().create();
        else Services.getScreenManager().popScreen();
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ GETTERS & SETTERS


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ INNER CLASSES

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
