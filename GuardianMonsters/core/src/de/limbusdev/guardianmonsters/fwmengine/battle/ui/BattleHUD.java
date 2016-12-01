package de.limbusdev.guardianmonsters.fwmengine.battle.ui;

import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.fwmengine.battle.control.BattleSystem;
import de.limbusdev.guardianmonsters.fwmengine.battle.control.MonsterManager;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.AttackMenuWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleActionMenuWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleAnimationWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleQueueWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.InfoLabelWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.SevenButtonsWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.TargetMenuWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.WidgetObserver;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.enums.ButtonIDs;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Attack;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.MonsterInBattle;
import de.limbusdev.guardianmonsters.model.MonsterInformation;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleMainMenuWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleStatusOverviewWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.EndOfBattleWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.ObservableWidget;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * BattleHUD manages all actions and UI elements in the {@link BattleScreen}
 *
 * Created by georg on 03.12.15.
 */
public class BattleHUD extends ABattleHUD implements WidgetObserver {

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ ATTRIBUTES

    // Logic
    private BattleSystem battleSystem;

    // ..................................................................................... scene2d
    // Groups
    private BattleMainMenuWidget        mainMenu;
    private BattleActionMenuWidget      actionMenu;
    private AttackMenuWidget            attackMenu;
    private BattleAnimationWidget       animationWidget;
    private BattleStatusOverviewWidget  statusWidget;
    private EndOfBattleWidget           endOfBattleWidget;
    private BattleQueueWidget           battleQueueWidget;
    private InfoLabelWidget             infoLabelWidget;
    private TargetMenuWidget            targetMenuWidget;

    // CallbackHandlers
    private BattleActionMenuWidget.CallbackHandler  battleActionCallbacks;
    private BattleMainMenuWidget.CallbackHandler    mainMenuCallbacks;
    private BattleActionMenuWidget.CallbackHandler  infoLabelCallbacks;
    private EndOfBattleWidget.CallbackHandler       endOfBattleCallbacks;
    private SevenButtonsWidget.CallbackHandler      attackMenuCallbacks;
    private BattleSystem.CallbackHandler            battleSystemCallbacks;
    private SevenButtonsWidget.CallbackHandler      targetMenuCallbacks;
    private BattleStateSwitcher                     battleStateSwitcher;
    private BattleActionMenuWidget.CallbackHandler  battleStartLabelCallbacks;
    private BattleActionMenuWidget.CallbackHandler  backToActionMenuCallbacks;
    private BattleActionMenuWidget.CallbackHandler  escapeSuccessCallbacks;
    private BattleActionMenuWidget.CallbackHandler  escapeFailCallbacks;


    // ....................................................................................... other

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
        targetMenuWidget.init(heroTeam.monsters,opponentTeam.monsters);

        show();
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // .............................................................................. LibGDX METHODS
    /**
     * Action that should take place when the screen gets hidden
     */
    public void hide() {
        stage.act(100);
    }

    @Override
    public void show() {
        super.show();
        battleStateSwitcher.toBattleStart();
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
        if(heroLost) {
            endOfBattleWidget.messageLabel.setText(Services.getL18N().l18n().get("batt_game_over"));
        } else {
            endOfBattleWidget.messageLabel.setText(Services.getL18N().l18n().get("batt_you_won"));
        }

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

        battleQueueWidget = new BattleQueueWidget(this, skin, Align.bottomLeft);
        battleQueueWidget.setScale(GS.zoom);
        battleQueueWidget.setPosition(GS.zoom,70*GS.zoom, Align.bottomLeft);

        infoLabelWidget = new InfoLabelWidget(this,skin);
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

        battleStateSwitcher.reset();

        switch(state) {
            case ACTIONMENU:
                battleStateSwitcher.toActionMenu();
                break;

            case ATTACKMENU:
                battleStateSwitcher.toAttackMenu();
                break;

            case ANIMATION:
                battleStateSwitcher.toAnimation();
                break;

            case ENDOFBATTLE:
                battleStateSwitcher.toEndOfBattle();
                break;

            case TARGET_CHOICE:
                battleStateSwitcher.toTargetChoice();
                break;

            default:
                battleStateSwitcher.toMainMenu();
                break;
        }
        this.state = state;
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ CALLBACKS

    private void setUpCallbacks() {

        battleStateSwitcher = new BattleStateSwitcher();

        battleStartLabelCallbacks = new BattleActionMenuWidget.CallbackHandler() {
            @Override
            public void onMonsterButton() {
                // not needed
            }

            @Override
            public void onBagButton() {
                // not needed
            }

            @Override
            public void onBackButton() {
                changeToWidgetSet(BattleState.MAINMENU);
            }

            @Override
            public void onExtraButton() {
                // not needed
            }
        };

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
                if(MonsterManager.tryToRun(heroTeam.monsters,opponentTeam.monsters)) {
                    battleStateSwitcher.toEscapeSuccessInfo();
                } else {
                    battleStateSwitcher.toEscapeFailInfo();
                }
            }

            @Override
            public void onSwordButton() {
                System.out.println("Input: Sword Button");
                changeToWidgetSet(BattleState.ACTIONMENU);
            }
        };

        infoLabelCallbacks = new BattleActionMenuWidget.CallbackHandler() {
            @Override
            public void onMonsterButton() {
                // not needed
            }

            @Override
            public void onBagButton() {
                // not needed
            }

            @Override
            public void onBackButton() {
                changeToWidgetSet(BattleState.ACTIONMENU);
            }

            @Override
            public void onExtraButton() {
                // not needed
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
            public void onPlayersTurn() {
                battleStateSwitcher.toActionMenu();
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

        backToActionMenuCallbacks = new BattleActionMenuWidget.CallbackHandler() {
            @Override
            public void onMonsterButton() {
                // not needed
            }

            @Override
            public void onBagButton() {
                // not needed
            }

            @Override
            public void onBackButton() {
                battleStateSwitcher.toActionMenu();
            }

            @Override
            public void onExtraButton() {
                // not needed
            }
        };

        escapeSuccessCallbacks = new BattleActionMenuWidget.CallbackHandler() {
            @Override
            public void onMonsterButton() {
                // not needed
            }

            @Override
            public void onBagButton() {
                // not needed
            }

            @Override
            public void onBackButton() {
                goToPreviousScreen();
            }

            @Override
            public void onExtraButton() {
                // not needed
            }
        };

        escapeFailCallbacks = new BattleActionMenuWidget.CallbackHandler() {
            @Override
            public void onMonsterButton() {
                // not needed
            }

            @Override
            public void onBagButton() {
                // not needed
            }

            @Override
            public void onBackButton() {
                battleSystem.nextMonster();
            }

            @Override
            public void onExtraButton() {
                // not needed
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
        MAINMENU, ACTIONMENU, ATTACKMENU, ENDOFBATTLE, ANIMATION, TARGET_CHOICE, BATTLE_START,
    }

    // Inner Classes
    private class BattleStateSwitcher {

        public BattleState state;

        public void toBattleStart() {
            reset();

            // Add Widgets
            infoLabelWidget.addToStageAndFadeIn(stage);
            animationWidget.addToStageAndFadeIn(stage);
            actionMenu.addToStageAndFadeIn(stage);
            statusWidget.addToStageAndFadeIn(stage);

            // Set Widget State
            actionMenu.disableAllButBackButton();

            // Set Callbacks
            actionMenu.setCallbackHandler(battleStartLabelCallbacks);
            infoLabelWidget.setWholeText(Services.getL18N().l18n().get("battle_start"));
            infoLabelWidget.animateTextAppearance();

            state = BattleState.BATTLE_START;
        }

        public void toActionMenu() {
            reset();

            // Add Widgets
            animationWidget.addToStage(stage);
            statusWidget.addToStage(stage);
            attackMenu.addToStage(stage);
            battleQueueWidget.addToStage(stage);
            actionMenu.addToStage(stage);

            // Setup Widgets
            actionMenu.setCallbackHandler(battleActionCallbacks);
            attackMenu.init(battleSystem.getActiveMonster());
            Array monsters = new Array();
            monsters.addAll(heroTeam.monsters);
            monsters.addAll(opponentTeam.monsters);
            battleQueueWidget.init(monsters);

            state = BattleState.ACTIONMENU;
        }

        public void toAttackMenu() {
            toActionMenu();

            state = BattleState.ACTIONMENU;
        }

        public void toEndOfBattle() {
            reset();
            //endOfBattleWidget.init(!checkIfWholeTeamKO(heroTeam));
            endOfBattleWidget.addToStageAndFadeIn(stage);
            statusWidget.addToStage(stage);

            state = BattleState.ENDOFBATTLE;
        }

        public void toAnimation() {
            reset();
            actionMenu.addToStage(stage);
            statusWidget.addToStage(stage);
            battleQueueWidget.addToStage(stage);
            infoLabelWidget.addToStage(stage);

            actionMenu.disableAllButBackButton();

            state = BattleState.ANIMATION;
        }

        public void toTargetChoice() {
            reset();
            animationWidget.addToStage(stage);
            actionMenu.disableAllButBackButton();
            actionMenu.addToStage(stage);
            actionMenu.setCallbackHandler(backToActionMenuCallbacks);
            targetMenuWidget.addToStage(stage);
            statusWidget.addToStage(stage);
            battleQueueWidget.addToStage(stage);

            state = BattleState.TARGET_CHOICE;
        }

        public void toMainMenu() {
            reset();
            animationWidget.addToStage(stage);
            actionMenu.disable();
            actionMenu.addToStage(stage);
            mainMenu.addToStageAndFadeIn(stage);
            statusWidget.addToStage(stage);

            state = BattleState.MAINMENU;
        }

        public void toInfoLabel() {
            reset();
            infoLabelWidget.addToStage(stage);
            animationWidget.addToStage(stage);
            actionMenu.addToStage(stage);
            statusWidget.addToStage(stage);

            actionMenu.disableAllButBackButton();
        }

        public void toEscapeSuccessInfo() {
            toInfoLabel();
            String wholeText = Services.getL18N().l18n().get("escape_success");
            infoLabelWidget.setWholeText(wholeText);
            infoLabelWidget.animateTextAppearance();
            actionMenu.setCallbackHandler(escapeSuccessCallbacks);
        }

        public void toEscapeFailInfo() {
            toInfoLabel();
            String wholeText = Services.getL18N().l18n().get("escape_fail");
            infoLabelWidget.setWholeText(wholeText);
            infoLabelWidget.animateTextAppearance();
            actionMenu.setCallbackHandler(escapeFailCallbacks);
        }

        public void reset() {
            // Remove all Widgets
            infoLabelWidget.remove();
            animationWidget.remove();
            endOfBattleWidget.remove();
            mainMenu.remove();
            targetMenuWidget.remove();
            attackMenu.remove();
            statusWidget.remove();
            actionMenu.remove();
            battleQueueWidget.remove();

            actionMenu.enable();
        }
    }

}