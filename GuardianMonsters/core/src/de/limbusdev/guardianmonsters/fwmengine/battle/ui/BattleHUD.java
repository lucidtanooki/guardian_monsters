package de.limbusdev.guardianmonsters.fwmengine.battle.ui;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.data.AudioAssets;
import de.limbusdev.guardianmonsters.data.BundleAssets;
import de.limbusdev.guardianmonsters.fwmengine.battle.control.BattleStringBuilder;
import de.limbusdev.guardianmonsters.fwmengine.battle.control.BattleSystem;
import de.limbusdev.guardianmonsters.fwmengine.battle.control.MonsterManager;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.AttackCalculationReport;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.AttackMenuWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleActionMenuWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleAnimationWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleQueueWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.InfoLabelWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.MonsterMenuWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.SevenButtonsWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.TargetMenuWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.WidgetObserver;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.enums.ButtonIDs;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Ability;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleMainMenuWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleStatusOverviewWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.ObservableWidget;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * BattleHUD manages all actions and UI elements in the {@link BattleScreen}
 *
 * Created by georg on 03.12.15.
 */
public class BattleHUD extends ABattleHUD implements WidgetObserver {

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ ATTRIBUTES

    // Statics
    private final static boolean LEFT = true, RIGHT = false;

    // Logic
    private BattleSystem battleSystem;

    // ..................................................................................... scene2d
    // Groups
    private BattleMainMenuWidget        mainMenu;
    private BattleActionMenuWidget      actionMenu;
    private AttackMenuWidget            attackMenu;
    private BattleAnimationWidget       animationWidget;
    private BattleStatusOverviewWidget  statusWidget;
    private BattleQueueWidget           battleQueueWidget;
    private InfoLabelWidget             infoLabelWidget;
    private TargetMenuWidget            targetMenuWidget;
    private MonsterMenuWidget           monsterMenuWidget;

    // CallbackHandlers
    private BattleActionMenuWidget.CallbackHandler  battleActionCallbacks;
    private BattleMainMenuWidget.CallbackHandler    mainMenuCallbacks;
    private BattleActionMenuWidget.CallbackHandler  infoLabelCallbacks;
    private BattleActionMenuWidget.CallbackHandler  endOfBattleCallbacks;
    private SevenButtonsWidget.CallbackHandler      attackMenuCallbacks;
    private BattleSystem.CallbackHandler            battleSystemCallbacks;
    private SevenButtonsWidget.CallbackHandler      targetMenuCallbacks;
    private BattleStateSwitcher                     battleStateSwitcher;
    private BattleActionMenuWidget.CallbackHandler  battleStartLabelCallbacks;
    private BattleActionMenuWidget.CallbackHandler  backToActionMenuCallbacks;
    private BattleActionMenuWidget.CallbackHandler  escapeSuccessCallbacks;
    private BattleActionMenuWidget.CallbackHandler  escapeFailCallbacks;
    private BattleAnimationWidget.CallbackHandler   battleAnimationCallbacks;
    private SevenButtonsWidget.CallbackHandler      monsterMenuCallbacks;


    // ....................................................................................... other

    private TeamComponent leftTeam, rightTeam;


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ CONSTRUCTOR
    public BattleHUD() {
        super(Services.getUI().getBattleSkin());
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

    /**
     * Resets the UI into a state where it can be initialized for a new battle
     */
    @Override
    protected void reset() {
        super.reset();
        actionMenu.clearActions();
        mainMenu.clearActions();
    }

    /**
     * Initializes the battle screen with the given teams
     * @param heroTeam
     * @param opponentTeam
     */
    public void init(TeamComponent heroTeam, TeamComponent opponentTeam) {
        reset();

        // Keep monster teams
        this.leftTeam = heroTeam;
        this.rightTeam = opponentTeam;

        // initialize independent battle system
        battleSystem = new BattleSystem(heroTeam.monsters,opponentTeam.monsters, battleSystemCallbacks);
        battleQueueWidget.update(battleSystem.getCurrentRound(),battleSystem.getNextRound());

        // initialize attack menu with active monster
        attackMenu.init(battleSystem.getActiveMonster());

        statusWidget.init(battleSystem);
        animationWidget.init(battleSystem);
        targetMenuWidget.init(battleSystem);

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


    // ........................................................................ setup

    /**
     * Setting up HUD elements:
     */
    public void setUpUI() {

        // Widgets
        statusWidget      = new BattleStatusOverviewWidget(this, skin);

        animationWidget   = new BattleAnimationWidget(this, battleAnimationCallbacks);
        animationWidget   .addWidgetObserver(this);

        mainMenu          = new BattleMainMenuWidget(   this, skin, mainMenuCallbacks);
        actionMenu        = new BattleActionMenuWidget( this, skin, battleActionCallbacks);
        attackMenu        = new AttackMenuWidget(       this, skin, attackMenuCallbacks);
        targetMenuWidget  = new TargetMenuWidget(       this, skin, targetMenuCallbacks);
        monsterMenuWidget = new MonsterMenuWidget(      this, skin, monsterMenuCallbacks);

        battleQueueWidget = new BattleQueueWidget(this, skin, Align.bottomLeft);
        battleQueueWidget.setScale(GS.zoom);
        battleQueueWidget.setPosition(GS.zoom,70*GS.zoom, Align.bottomLeft);

        infoLabelWidget = new InfoLabelWidget(this,skin);
    }


    // ....................................................................... ui

    @Override
    public void getNotified(ObservableWidget ow) {
        // TODO
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ CALLBACKS

    private void setUpCallbacks() {

        battleStateSwitcher = new BattleStateSwitcher();

        battleStartLabelCallbacks = new BattleActionMenuWidget.CallbackHandler() {
            @Override
            public void onMonsterButton() {
                // Not needed
            }

            @Override
            public void onBagButton() {
                // not needed
            }

            @Override
            public void onBackButton() {
                System.out.println("BattleStartLabel: onBackButton()");
                battleStateSwitcher.toMainMenu();
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
                monsterMenuWidget.init(battleSystem, LEFT);
                battleStateSwitcher.toTeamMenu();
            }

            @Override
            public void onBagButton() {
                System.out.println("Show Bag Menu");
            }

            @Override
            public void onBackButton() {
                System.out.println("BattleActionButtons: onBackButton()");
                battleStateSwitcher.toMainMenu();
            }

            @Override
            public void onExtraButton() {
                battleSystem.defend();
            }
        };

        mainMenuCallbacks = new BattleMainMenuWidget.CallbackHandler() {
            @Override
            public void onRunButton() {
                System.out.println("Input: Run Button");
                if(MonsterManager.tryToRun(leftTeam.monsters, rightTeam.monsters)) {
                    battleStateSwitcher.toEscapeSuccessInfo();
                } else {
                    battleStateSwitcher.toEscapeFailInfo();
                }
            }

            @Override
            public void onSwordButton() {
                System.out.println("MainMenuButtons: onSwordButton()");
                System.out.println("Input: Sword Button");
                battleSystem.continueBattle();
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
                System.out.println("InfoLabelButtons: onBackButton()");
                battleSystem.continueBattle();
            }

            @Override
            public void onExtraButton() {
                // not needed
            }
        };

        endOfBattleCallbacks = new BattleActionMenuWidget.CallbackHandler() {
            @Override
            public void onMonsterButton() {
                // TODO
            }

            @Override
            public void onBagButton() {
                // TODO
            }

            @Override
            public void onBackButton() {
                boolean teamOk = false;
                for(Monster m : leftTeam.monsters.values()) {
                    if(m.getHP() > 0) {
                        teamOk = true || teamOk;
                    } else {
                        teamOk = false || teamOk;
                    }
                }
                if(!teamOk) Services.getScreenManager().getGame().create();
                else Services.getScreenManager().popScreen();
            }

            @Override
            public void onExtraButton() {
                // TODO
            }
        };

        attackMenuCallbacks = new SevenButtonsWidget.CallbackHandler() {
            @Override
            public void onButtonNr(int nr) {
                System.out.println("AttackMenuButtons: onButtonNr("+nr+")");
                System.out.println("Input: User chose attack Nr. " + nr);
                battleSystem.setChosenAttack(nr);
                battleStateSwitcher.toTargetChoice();
            }
        };

        battleSystemCallbacks = new BattleSystem.CallbackHandler() {

            @Override
            public void onBattleEnds(boolean winnerSide) {
                battleStateSwitcher.toEndOfBattle(winnerSide);
            }

            @Override
            public void onPlayersTurn() {
                battleStateSwitcher.toActionMenu();
            }

            @Override
            public void onMonsterKilled(Monster m) {

                boolean side = battleSystem.getLeftInBattle().containsValue(m,false);
                int pos = side ?
                    battleSystem.getLeftInBattle().getKey(m,false)
                    : battleSystem.getRightInBattle().getKey(m,false);

                animationWidget.animateMonsterKO(pos,side);
            }

            @Override
            public void onQueueUpdated() {
                battleQueueWidget.update(battleSystem.getCurrentRound(),battleSystem.getNextRound());
            }

            @Override
            public void onAttack(Monster attacker, Monster target, Ability ability, AttackCalculationReport rep) {

                // Change widget set
                battleStateSwitcher.toAnimation();
                infoLabelWidget.setWholeText(BattleStringBuilder.givenDamage(attacker,target,rep));
                infoLabelWidget.animateTextAppearance();

                // Start ability animation
                int attPos, defPos;
                boolean activeSide;
                boolean passiveSide;

                activeSide =  battleSystem.getBattleFieldSideFor(attacker);
                passiveSide = battleSystem.getBattleFieldSideFor(target);

                attPos = battleSystem.getBattleFieldPositionFor(attacker);
                defPos = battleSystem.getBattleFieldPositionFor(target);

                animationWidget.animateAttack(attPos, defPos, activeSide, passiveSide, ability);
            }

            @Override
            public void onDefense(Monster defensiveMonster) {
                battleStateSwitcher.toAnimation();
                infoLabelWidget.setWholeText(BattleStringBuilder.selfDefense(defensiveMonster));
                infoLabelWidget.animateTextAppearance();
                animationWidget.animateSelfDefense();
            }
        };

        targetMenuCallbacks = new SevenButtonsWidget.CallbackHandler() {
            @Override
            public void onButtonNr(int nr) {
                System.out.println("TargetMenuButtons: onButtonNr("+nr+")");
                Monster target = targetMenuWidget.getMonsterOfIndex(nr);
                battleSystem.setChosenTarget(target);
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
                System.out.println("BackToActionMenuButtons: onBackButton()");
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
                System.out.println("EscapeSuccessButtons: onBackButton()");
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
                System.out.println("EscapeFailButtons: onBackButton()");
                battleSystem.continueBattle();
            }

            @Override
            public void onExtraButton() {
                // not needed
            }
        };

        battleAnimationCallbacks = new BattleAnimationWidget.CallbackHandler() {
            @Override
            public void onHitAnimationComplete() {
                battleSystem.applyAttack();
                actionMenu.enable(actionMenu.backButton);
            }
        };

        monsterMenuCallbacks = new SevenButtonsWidget.CallbackHandler() {
            @Override
            public void onButtonNr(int nr) {
                System.out.println("Teammember " + nr);
                battleSystem.replaceActiveMonster(leftTeam.monsters.get(nr));
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
            infoLabelWidget.setWholeText(Services.getL18N().l18n(BundleAssets.BATTLE).get("battle_start"));
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

            state = BattleState.ACTIONMENU;
        }

        public void toAttackMenu() {
            toActionMenu();

            state = BattleState.ACTIONMENU;
        }

        public void toEndOfBattle(boolean winnerSide) {
            reset();
            toInfoLabel();
            String textKey = !winnerSide ? "batt_you_won":"batt_game_over";
            String wholeText = Services.getL18N().l18n(BundleAssets.BATTLE).get(textKey);
            infoLabelWidget.setWholeText(wholeText);
            infoLabelWidget.animateTextAppearance();
            actionMenu.setCallbackHandler(endOfBattleCallbacks);

            statusWidget.addToStage(stage);

            state = BattleState.ENDOFBATTLE;

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Services.getAudio().playMusic(AudioAssets.victoryFanfareMusic);
                }
            };
            Runnable runnable2 = new Runnable() {
                @Override
                public void run() {
                    Services.getAudio().playMusic(AudioAssets.victorySongMusic);
                }
            };
            Action endOfBattleMusicSequence = Actions.sequence(
                Services.getAudio().getMuteAudioAction(),
                Actions.run(runnable),
                Actions.delay(5),
                Actions.run(runnable2)
            );

            stage.addAction(endOfBattleMusicSequence);
        }

        public void toAnimation() {
            reset();
            toInfoLabel();
            actionMenu.disableAllChildButtons();
            battleQueueWidget.addToStage(stage);
            actionMenu.setCallbackHandler(infoLabelCallbacks);

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

        public void toTeamMenu() {
            reset();
            animationWidget.addToStage(stage);
            actionMenu.disableAllButBackButton();
            actionMenu.addToStage(stage);
            actionMenu.setCallbackHandler(backToActionMenuCallbacks);
            monsterMenuWidget.addToStage(stage);
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
            String wholeText = Services.getL18N().l18n(BundleAssets.BATTLE).get("escape_success");
            infoLabelWidget.setWholeText(wholeText);
            infoLabelWidget.animateTextAppearance();
            actionMenu.setCallbackHandler(escapeSuccessCallbacks);
        }

        public void toEscapeFailInfo() {
            toInfoLabel();
            String wholeText = Services.getL18N().l18n(BundleAssets.BATTLE).get("escape_fail");
            infoLabelWidget.setWholeText(wholeText);
            infoLabelWidget.animateTextAppearance();
            actionMenu.setCallbackHandler(escapeFailCallbacks);
        }

        public void reset() {
            // Remove all Widgets
            monsterMenuWidget.remove();
            infoLabelWidget.remove();
            animationWidget.remove();
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
