package de.limbusdev.guardianmonsters.fwmengine.battle.ui;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.limbusdev.guardianmonsters.assets.paths.AssetPath;
import de.limbusdev.guardianmonsters.fwmengine.battle.control.BattleStringBuilder;
import de.limbusdev.guardianmonsters.fwmengine.battle.control.BattleSystem;
import de.limbusdev.guardianmonsters.fwmengine.battle.control.MonsterManager;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.AttackCalculationReport;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.AttackMenuWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleActionMenuWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleAnimationWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleQueueWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.InfoLabelWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.ItemChoice;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.LevelUpWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.MonsterMenuWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.SevenButtonsWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.TargetMenuWidget;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.model.abilities.Ability;
import de.limbusdev.guardianmonsters.model.items.Inventory;
import de.limbusdev.guardianmonsters.model.monsters.Monster;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleMainMenuWidget;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleStatusOverviewWidget;
import de.limbusdev.guardianmonsters.model.monsters.Team;

import static de.limbusdev.guardianmonsters.Constant.LEFT;

/**
 * BattleHUD manages all actions and UI elements in the {@link BattleScreen}
 *
 * @author Georg Eckert 2015
 */
public class BattleHUD extends ABattleHUD {

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ ATTRIBUTES

    // Logic
    private BattleSystem battleSystem;

    private Stage battleAnimationStage;
    private BattleStateSwitcher battleStateSwitcher;

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
    private BattleActionMenuWidget.Callbacks    battleActionCallbacks;
    private BattleMainMenuWidget.Callbacks      mainMenuCallbacks;
    private BattleActionMenuWidget.Callbacks    infoLabelCallbacks;
    private BattleActionMenuWidget.Callbacks    endOfBattleCallbacks;
    private SevenButtonsWidget.Callbacks        attackMenuCallbacks;
    private BattleSystem.Callbacks              battleSystemCallbacks;
    private SevenButtonsWidget.Callbacks        targetMenuCallbacks;
    private BattleActionMenuWidget.Callbacks    battleStartLabelCallbacks;
    private BattleActionMenuWidget.Callbacks    backToActionMenuCallbacks;
    private BattleActionMenuWidget.Callbacks    escapeSuccessCallbacks;
    private BattleActionMenuWidget.Callbacks    escapeFailCallbacks;
    private BattleAnimationWidget.Callbacks     battleAnimationCallbacks;
    private SevenButtonsWidget.Callbacks        monsterMenuCallbacks;

    private Team leftTeam, rightTeam;
    private Inventory inventory;


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ CONSTRUCTOR
    public BattleHUD(Inventory inventory) {
        super(Services.getUI().getBattleSkin());
        this.inventory = inventory;
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
    public void init(Team heroTeam, Team opponentTeam) {
        reset();

        // Keep monster teams
        this.leftTeam = heroTeam;
        this.rightTeam = opponentTeam;

        // initialize independent battle system
        battleSystem = new BattleSystem(heroTeam,opponentTeam, battleSystemCallbacks);
        battleSystem.getQueue().add(battleQueueWidget);

        // initialize attack menu with active monster
        attackMenu.init(battleSystem.getActiveMonster());

        statusWidget.init(battleSystem);
        animationWidget.init(battleSystem);
        targetMenuWidget.init(battleSystem);

        show();
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // .............................................................................. LibGDX METHODS

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
        // Second stage
        FitViewport viewport = new FitViewport(640,360);
        battleAnimationStage = new Stage(viewport);
        addAddtitionalStage(battleAnimationStage);

        // Widgets
        statusWidget      = new BattleStatusOverviewWidget(skin);
        animationWidget   = new BattleAnimationWidget(battleAnimationCallbacks);
        mainMenu          = new BattleMainMenuWidget(skin, mainMenuCallbacks);
        actionMenu        = new BattleActionMenuWidget(skin, battleActionCallbacks);
        attackMenu        = new AttackMenuWidget(skin, attackMenuCallbacks);
        targetMenuWidget  = new TargetMenuWidget(skin, targetMenuCallbacks);
        monsterMenuWidget = new MonsterMenuWidget(skin, monsterMenuCallbacks);

        battleQueueWidget = new BattleQueueWidget(skin, Align.bottomLeft);
        battleQueueWidget.setPosition(1,65, Align.bottomLeft);

        infoLabelWidget = new InfoLabelWidget(skin);
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ CALLBACKS

    private void setUpCallbacks() {

        battleStateSwitcher = new BattleStateSwitcher();

        battleStartLabelCallbacks = new BattleActionMenuWidget.Callbacks() {
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

        battleActionCallbacks = new BattleActionMenuWidget.Callbacks() {
            @Override
            public void onMonsterButton() {
                System.out.println("Show Monster Menu");
                monsterMenuWidget.init(battleSystem, LEFT);
                battleStateSwitcher.toTeamMenu();
            }

            @Override
            public void onBagButton() {
                stage.addActor(new ItemChoice(Services.getUI().getInventorySkin(), inventory, leftTeam, battleSystem));
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

        mainMenuCallbacks = new BattleMainMenuWidget.Callbacks() {
            @Override
            public void onRunButton() {
                System.out.println("Input: Run Button");
                if(MonsterManager.tryToRun(leftTeam, rightTeam)) {
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

        infoLabelCallbacks = new BattleActionMenuWidget.Callbacks() {
            @Override
            public void onBackButton() {
                System.out.println("InfoLabelButtons: onBackButton()");
                battleSystem.continueBattle();
            }
        };

        endOfBattleCallbacks = new BattleActionMenuWidget.Callbacks() {
            @Override
            public void onBackButton() {
                boolean teamOk = false;
                for(Monster m : leftTeam.values()) {
                    if(m.stat.isFit()) {
                        teamOk = true || teamOk;
                    } else {
                        teamOk = false || teamOk;
                    }
                }
                if(!teamOk) Services.getScreenManager().getGame().create();
                else {
                    Services.getAudio().stopMusic();
                    Services.getScreenManager().popScreen();
                }
            }
        };

        attackMenuCallbacks = new SevenButtonsWidget.Callbacks() {
            @Override
            public void onButtonNr(int nr) {
                Monster activeMonster = battleSystem.getActiveMonster();
                System.out.println("AttackMenuButtons: onButtonNr("+nr+")");
                System.out.println("Input: User chose attack Nr. " + nr);
                int chosenAttackNr = activeMonster.abilityGraph.learntAbilities.indexOfValue(activeMonster.abilityGraph.getActiveAbility(nr),false);
                battleSystem.setChosenAttack(chosenAttackNr);
                battleStateSwitcher.toTargetChoice();
            }
        };

        battleSystemCallbacks = new BattleSystem.Callbacks() {

            @Override
            public void onBattleEnds(boolean winnerSide) {
                battleStateSwitcher.toEndOfBattle(winnerSide);
            }

            @Override
            public void onDoingNothing(Monster monster) {
                battleStateSwitcher.toAnimation();
                infoLabelWidget.setWholeText(
                    Services.getL18N().Battle().format("batt_item_usage", monster.getName())
                );
                infoLabelWidget.animateTextAppearance();
                animationWidget.animateItemUsage();
            }

            @Override
            public void onPlayersTurn() {
                battleStateSwitcher.toActionMenu();
            }

            @Override
            public void onMonsterKilled(Monster m) {
                boolean side = battleSystem.getQueue().getTeamSideFor(m);
                int pos = battleSystem.getQueue().getFieldPositionFor(m);
                animationWidget.animateMonsterKO(pos,side);
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

                activeSide =  battleSystem.getQueue().getTeamSideFor(attacker);
                passiveSide = !activeSide;

                attPos = battleSystem.getQueue().getFieldPositionFor(attacker);
                defPos = battleSystem.getQueue().getFieldPositionFor(target);

                animationWidget.animateAttack(attPos, defPos, activeSide, passiveSide, ability);
            }

            @Override
            public void onDefense(Monster defensiveMonster) {
                battleStateSwitcher.toAnimation();
                infoLabelWidget.setWholeText(BattleStringBuilder.selfDefense(defensiveMonster));
                infoLabelWidget.animateTextAppearance();
                animationWidget.animateSelfDefense();
            }

            @Override
            public void onLevelup(Monster m) {
                showLevelUp(m);
            }
        };

        targetMenuCallbacks = new SevenButtonsWidget.Callbacks() {
            @Override
            public void onButtonNr(int nr) {
                System.out.println("TargetMenuButtons: onButtonNr("+nr+")");
                Monster target = targetMenuWidget.getMonsterOfIndex(nr);
                battleSystem.setChosenTarget(target);
                battleSystem.attack();
            }
        };

        backToActionMenuCallbacks = new BattleActionMenuWidget.Callbacks() {
            @Override
            public void onBackButton() {
                System.out.println("BackToActionMenuButtons: onBackButton()");
                battleStateSwitcher.toActionMenu();
            }
        };

        escapeSuccessCallbacks = new BattleActionMenuWidget.Callbacks() {
            @Override
            public void onBackButton() {
                System.out.println("EscapeSuccessButtons: onBackButton()");
                goToPreviousScreen();
            }
        };

        escapeFailCallbacks = new BattleActionMenuWidget.Callbacks() {
            @Override
            public void onBackButton() {
                System.out.println("EscapeFailButtons: onBackButton()");
                battleSystem.continueBattle();
            }
        };

        battleAnimationCallbacks = new BattleAnimationWidget.Callbacks() {
            @Override
            public void onHitAnimationComplete() {
                battleSystem.applyAttack();
                actionMenu.enable(actionMenu.backButton);
            }
        };

        monsterMenuCallbacks = new SevenButtonsWidget.Callbacks() {
            @Override
            public void onButtonNr(int nr) {
                System.out.println("Teammember " + nr);
                battleSystem.replaceActiveMonster(leftTeam.get(nr));
            }
        };
    }

    private void showLevelUp(Monster m) {
        stage.addActor(new LevelUpWidget(Services.getUI().getInventorySkin(), m));
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ GETTERS & SETTERS

    // Inner Classes
    private class BattleStateSwitcher {

        public BattleState state;

        public void toBattleStart() {
            reset();

            // Add Widgets
            animationWidget.addToStageAndFadeIn(battleAnimationStage);
            statusWidget.addToStageAndFadeIn(battleAnimationStage);
            infoLabelWidget.addToStageAndFadeIn(stage);
            actionMenu.addToStageAndFadeIn(stage);

            // Set Widget State
            actionMenu.disableAllButBackButton();

            // Set Callbacks
            actionMenu.setCallbacks(battleStartLabelCallbacks);
            infoLabelWidget.setWholeText(Services.getL18N().Battle().get("battle_start"));
            infoLabelWidget.animateTextAppearance();

            state = BattleState.BATTLE_START;
        }

        public void toActionMenu() {
            reset();

            // Add Widgets
            animationWidget.addToStage(battleAnimationStage);
            statusWidget.addToStage(battleAnimationStage);
            battleQueueWidget.addToStage(stage);
            actionMenu.addToStage(stage);
            attackMenu.addToStage(stage);

            // Setup Widgets
            actionMenu.setCallbacks(battleActionCallbacks);
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
            String wholeText = Services.getL18N().Battle().get(textKey);
            infoLabelWidget.setWholeText(wholeText);
            infoLabelWidget.animateTextAppearance();
            actionMenu.setCallbacks(endOfBattleCallbacks);

            statusWidget.addToStage(stage);

            state = BattleState.ENDOFBATTLE;

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Services.getAudio().playMusic(AssetPath.Audio.Music.VICTORY_FANFARE);
                }
            };
            Runnable runnable2 = new Runnable() {
                @Override
                public void run() {
                    Services.getAudio().playMusic(AssetPath.Audio.Music.VICTORY_SONG);
                }
            };
            Action endOfBattleMusicSequence = Actions.sequence(
                Services.getAudio().getMuteAudioAction(AssetPath.Audio.Music.VICTORY_SONG),
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
            actionMenu.setCallbacks(infoLabelCallbacks);

            state = BattleState.ANIMATION;
        }

        public void toTargetChoice() {
            reset();
            animationWidget.addToStage(battleAnimationStage);
            statusWidget.addToStage(battleAnimationStage);
            actionMenu.disableAllButBackButton();
            actionMenu.addToStage(stage);
            actionMenu.setCallbacks(backToActionMenuCallbacks);
            targetMenuWidget.addToStage(stage);
            battleQueueWidget.addToStage(stage);

            state = BattleState.TARGET_CHOICE;
        }

        public void toTeamMenu() {
            reset();
            animationWidget.addToStage(battleAnimationStage);
            statusWidget.addToStage(battleAnimationStage);
            actionMenu.disableAllButBackButton();
            actionMenu.addToStage(stage);
            actionMenu.setCallbacks(backToActionMenuCallbacks);
            monsterMenuWidget.addToStage(stage);

            battleQueueWidget.addToStage(stage);

            state = BattleState.TARGET_CHOICE;
        }

        public void toMainMenu() {
            reset();
            animationWidget.addToStage(battleAnimationStage);
            statusWidget.addToStage(battleAnimationStage);
            actionMenu.disable();
            actionMenu.addToStage(stage);
            mainMenu.addToStageAndFadeIn(stage);

            state = BattleState.MAINMENU;
        }

        public void toInfoLabel() {
            reset();

            animationWidget.addToStage(battleAnimationStage);
            statusWidget.addToStage(battleAnimationStage);
            infoLabelWidget.addToStage(stage);
            actionMenu.addToStage(stage);

            actionMenu.disableAllButBackButton();
        }

        public void toEscapeSuccessInfo() {
            toInfoLabel();
            String wholeText = Services.getL18N().Battle().get("escape_success");
            infoLabelWidget.setWholeText(wholeText);
            infoLabelWidget.animateTextAppearance();
            actionMenu.setCallbacks(escapeSuccessCallbacks);
        }

        public void toEscapeFailInfo() {
            toInfoLabel();
            String wholeText = Services.getL18N().Battle().get("escape_fail");
            infoLabelWidget.setWholeText(wholeText);
            infoLabelWidget.animateTextAppearance();
            actionMenu.setCallbacks(escapeFailCallbacks);
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

    private enum BattleState {
        MAINMENU, ACTIONMENU, ATTACKMENU, ENDOFBATTLE, ANIMATION, TARGET_CHOICE, BATTLE_START,
    }

}
