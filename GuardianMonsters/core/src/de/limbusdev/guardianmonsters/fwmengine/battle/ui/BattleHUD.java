package de.limbusdev.guardianmonsters.fwmengine.battle.ui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import java.util.Observable;
import java.util.Observer;

import de.limbusdev.guardianmonsters.fwmengine.battle.control.BattleSystem;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.AttackMenuWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleActionMenuWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleAnimationWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleQueueWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.InfoLabelWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.MonsterIndicatorWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.SevenButtonsWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.TargetMenuWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.WidgetObserver;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.enums.ButtonIDs;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.AttackCalculationReport;
import de.limbusdev.guardianmonsters.model.Attack;
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

    // Logic
    private BattleSystem battleSystem;

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
    private InfoLabelWidget             infoLabelWidget;
    private TargetMenuWidget            targetMenuWidget;

    // CallbackHandlers
    private BattleActionMenuWidget.CallbackHandler  battleActionCallbacks;
    private BattleMainMenuWidget.CallbackHandler    mainMenuCallbacks;
    private InfoLabelWidget.CallbackHandler         infoLabelCallbacks;
    private EndOfBattleWidget.CallbackHandler       endOfBattleCallbacks;
    private SevenButtonsWidget.CallbackHandler      attackMenuCallbacks;
    private BattleSystem.CallbackHandler            battleSystemCallbacks;
    private SevenButtonsWidget.CallbackHandler      targetMenuCallbacks;


    // ......................................................................... other

    private TeamComponent heroTeam, opponentTeam;

    private ArrayMap<Integer, Monster> heroTeamSlots, opponentTeamSlots;

    private BattleState state;

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ CONSTRUCTOR
    public BattleHUD() {
        super(Services.getUI().getBattleSkin());
        initializeAttributes();
        setUpCallbacks();
        setUpUI();
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ METHODS

    // ..................................................................... game loop

    @Override
    public void update(float delta) {
        super.update(delta);
    }


    // .............................................................. initialize arena

    private void initializeAttributes() {
        this.state = BattleState.MAINMENU;
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
     * @param heroTeam
     * @param opponentTeam
     */
    public void init(TeamComponent heroTeam, TeamComponent opponentTeam) {
        reset();

        // Give each monster a position on the battle field
        heroTeamSlots = new ArrayMap<Integer, Monster>();
        int i = 0;
        for(Monster m : heroTeam.monsters) {
            if(m.getHP() > 0) {
                heroTeamSlots.put(i,m);
                i++;
            }
        }

        i=0;
        opponentTeamSlots = new ArrayMap<Integer, Monster>();
        for(Monster m : opponentTeam.monsters) {
            if(m.getHP() > 0) {
                opponentTeamSlots.put(i,m);
                i++;
            }
        }

        // Keep monster teams
        this.heroTeam = heroTeam;
        this.opponentTeam = opponentTeam;

        // initialize independent battle system
        battleSystem = new BattleSystem(heroTeam.monsters,opponentTeam.monsters, battleSystemCallbacks);

        // initialize attack menu with active monster
        attackMenu.init(battleSystem.getActiveMonster());

        statusWidget.init(heroTeamSlots, opponentTeamSlots);
        animationWidget.init(heroTeamSlots, opponentTeamSlots);
        indicatorMenu.init(opponentTeamSlots);
        targetMenuWidget.init(heroTeam.monsters,opponentTeam.monsters);

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
     * Handle End of Battle
     */
    public void handleEndOfBattle() {
        // Stop AI
        // Check if Hero lost fight
        boolean heroLost = true;
//        for(Integer i : heroTeam.keys())
//            if(!(heroTeam.get(i).monster.getHP() == 0))
//                heroLost = false;

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

        mainMenu          = new BattleMainMenuWidget(   this, skin, mainMenuCallbacks);
        actionMenu        = new BattleActionMenuWidget( this, skin, battleActionCallbacks);
        endOfBattleWidget = new EndOfBattleWidget(      this, skin, endOfBattleCallbacks);
        attackMenu        = new AttackMenuWidget(       this, skin, attackMenuCallbacks);
        targetMenuWidget  = new TargetMenuWidget(       this, skin, targetMenuCallbacks);

        indicatorMenu     = new MonsterIndicatorWidget(this, skin, Align.bottomRight);
        indicatorMenu.setPosition(GS.RES_X-1*GS.zoom,82*GS.zoom,Align.bottomLeft);
        indicatorMenu.setScale(GS.zoom);
        indicatorMenu     .addWidgetObserver(this);

        battleQueueWidget = new BattleQueueWidget(this, skin, Align.bottomLeft);
        battleQueueWidget.setScale(GS.zoom);
        battleQueueWidget.setPosition(GS.zoom,70*GS.zoom, Align.bottomLeft);

        infoLabelWidget = new InfoLabelWidget(this,skin, infoLabelCallbacks);
    }



    // ............................................................... battle methods


    /**
     * Removes the monster with the given position on the battle field by setting it KO and
     * fading out the monster sprite and monsters status UI
     * @param m
     */
    private void kickOutMonster(MonsterInBattle m) {
        animationWidget.animateMonsterKO(m.battleFieldPosition,m.battleFieldSide);
        statusWidget.fadeStatusWidget(m.battleFieldPosition, m.battleFieldSide);
        infoLabelWidget.infoLabel.setText(MonsterInformation.getInstance().monsterNames.get(m.monster.ID-1) + " " + Services.getL18N().l18n().get("batt_defeated") + ".");

//        System.out.println("Killed: " + m.battleFieldPosition);
//
//        // Change the indicator position to an active fighter
//        if(m.battleFieldSide)
//            heroTeam.removeValue(m, true);      // Hero Team
//        else
//            oppoTeam.removeValue(m, true);   // Opponent Team
//
//        // Spread EXP
//        if(!m.battleFieldSide) {
//            // Defeated Monster was part of opponents heroTeam
//            int exp = m.monster.level * (m.monster.getHPfull() + m.monster.physStrength);
//            exp /= heroTeam.size;
//            for(Integer i : heroTeam.keys())
//                if(heroTeam.get(i).monster.getHP() > 0)
//                    MonsterManager.earnEXP(heroTeam.get(i).monster,exp);
//        }

    }

//    public void lineUpForAttack(MonsterInBattle m, int chosenTarget, int attack) {
//        m.prepareForAttack(chosenTarget, attack);
//
//        if(GS.DEBUGGING_ON) BattleStringBuilder.printEnqueuedMonster(m,chosenTarget,attack);
//
//        animationQueue.add(m);
//
//        if(m.battleFieldSide) {
//            // if Player has chosen attacks
//            boolean allChosen = true;
//            for (Integer i : heroTeam.keys()) {
//                MonsterInBattle mib = heroTeam.get(i);
//                allChosen = allChosen & mib.attackChosen;
//            }
//            if (allChosen) {
//                aiPlayer.chooseAttacks();
//                changeToWidgetSet(BattleState.ANIMATION);
//                //animationQueue.sort(new MonsterSpeedComparator());
//                updateQueues();
//            }
//        }
//    }


    // ....................................................................... ui

    @Override
    public void getNotified(ObservableWidget ow) {
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
                monsters.addAll(heroTeam.monsters);
                monsters.addAll(opponentTeam.monsters);
                battleQueueWidget.init(monsters);
                if(this.state == BattleState.ATTACKMENU) {
                    indicatorMenu.addToStage(stage);
                } else {
                    indicatorMenu.addToStageAndFadeIn(stage);
                }
                break;

            case ATTACKMENU:
                attackMenu.init(battleSystem.getActiveMonster());
                stage.addActor(indicatorMenu);
                stage.addActor(statusWidget);
                if(!(this.state == BattleState.ATTACKMENU)) attackMenu.addToStageAndFadeIn(stage);
                else attackMenu.addToStage(stage);
                break;

            case ANIMATION:
                actionMenu.addToStage(stage);
                statusWidget.addToStage(stage);
                battleQueueWidget.addToStage(stage);
                break;

            case ENDOFBATTLE:
                //endOfBattleWidget.init(!checkIfWholeTeamKO(heroTeam));
                endOfBattleWidget.addToStageAndFadeIn(stage);
                statusWidget.addToStage(stage);
                break;

            case TARGET_CHOICE:
                targetMenuWidget.addToStageAndFadeIn(stage);
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

    private void setUpCallbacks() {
        battleActionCallbacks = new BattleActionMenuWidget.CallbackHandler() {
            @Override
            public void onMonsterButton() {
                System.out.println("Show Monster Menu");
            }

            @Override
            public void onBagButton() {
                System.out.println("Show Bag Menu");
            }

            @Override
            public void onBackButton() {
                changeToWidgetSet(BattleState.MAINMENU);
            }

            @Override
            public void onExtraButton() {
                System.out.println("Show Extra Menu");
            }
        };

        mainMenuCallbacks = new BattleMainMenuWidget.CallbackHandler() {
            @Override
            public void onRunButton() {
                System.out.println("Input: Run Button");
                goToPreviousScreen();
            }

            @Override
            public void onSwordButton() {
                System.out.println("Input: Sword Button");
                changeToWidgetSet(BattleState.ACTIONMENU);
            }
        };

        infoLabelCallbacks = new InfoLabelWidget.CallbackHandler() {
            @Override
            public void onBackButton() {
                changeToWidgetSet(BattleState.ACTIONMENU);
            }
        };

        endOfBattleCallbacks = new EndOfBattleWidget.CallbackHandler() {
            @Override
            public void onBackButton() {
                boolean teamOk = false;
                for(Monster m : heroTeam.monsters) {
                    if(m.getHP() > 0) {
                        teamOk = true || teamOk;
                    } else {
                        teamOk = false || teamOk;
                    }
                }
                if(teamOk) Services.getScreenManager().getGame().create();
                else Services.getScreenManager().popScreen();
            }
        };

        attackMenuCallbacks = new SevenButtonsWidget.CallbackHandler() {
            @Override
            public void onButtonNr(int nr) {
                System.out.println("Input: User chose attack Nr. " + nr);
                battleSystem.setChosenAttack(nr);
                changeToWidgetSet(BattleState.TARGET_CHOICE);
            }
        };

        battleSystemCallbacks = new BattleSystem.CallbackHandler() {
            @Override
            public void onNextTurn() {
                changeToWidgetSet(BattleState.ACTIONMENU);
            }

            @Override
            public void onMonsterKilled(Monster m) {
                int pos;
                boolean side;
                if(heroTeamSlots.containsValue(m,true)) {
                    side = true;
                    pos = heroTeamSlots.getKey(m,true);
                } else {
                    side = false;
                    pos = opponentTeamSlots.getKey(m,true);
                }
                animationWidget.animateMonsterKO(pos,side);
            }

            @Override
            public void onQueueUpdated() {
                // TODO
            }

            @Override
            public void onAttack(Monster attacker, Monster target, Attack attack) {
                int attPos, defPos;
                boolean side;
                if(heroTeamSlots.containsValue(attacker,false)) {
                    side = true;
                    attPos = heroTeamSlots.getKey(attacker,false);
                    defPos = opponentTeamSlots.getKey(target, false);
                } else {
                    side = false;
                    defPos = heroTeamSlots.getKey(target,false);
                    attPos = opponentTeamSlots.getKey(attacker, false);
                }
                animationWidget.animateAttack(attPos, defPos, side, attack);
            }
        };

        targetMenuCallbacks = new SevenButtonsWidget.CallbackHandler() {
            @Override
            public void onButtonNr(int nr) {
                Monster target = targetMenuWidget.getMonsterOfIndex(nr);
                battleSystem.setChosenTarget(target);
                changeToWidgetSet(BattleState.ANIMATION);
                battleSystem.attack();
            }
        };
    }

    @Deprecated
    @Override
    public void onButtonClicked(ButtonIDs id) {
        // TODO
    }

    /**
     * Empty implementation, because using the other method only
     * @param id
     */
    @Override
    public void onButtonClicked(int id) {}

    private void onBattleAnimationUpdate(BattleAnimationWidget baw) {
        // Next choice
    }



    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ GETTERS & SETTERS


    private enum BattleState {
        MAINMENU, ACTIONMENU, ATTACKMENU, ENDOFBATTLE, ANIMATION, TARGET_CHOICE,
    }

}
