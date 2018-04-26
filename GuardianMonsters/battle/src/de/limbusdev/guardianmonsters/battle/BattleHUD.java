package de.limbusdev.guardianmonsters.battle;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.limbusdev.guardianmonsters.assets.paths.AssetPath;
import de.limbusdev.guardianmonsters.battle.ui.widgets.AttackMenuWidget;
import de.limbusdev.guardianmonsters.battle.ui.widgets.BattleActionMenuWidget;
import de.limbusdev.guardianmonsters.battle.ui.widgets.BattleAnimationWidget;
import de.limbusdev.guardianmonsters.battle.ui.widgets.BattleMainMenuWidget;
import de.limbusdev.guardianmonsters.battle.ui.widgets.BattleQueueWidget;
import de.limbusdev.guardianmonsters.battle.ui.widgets.BattleStatusOverviewWidget;
import de.limbusdev.guardianmonsters.battle.ui.widgets.InfoLabelWidget;
import de.limbusdev.guardianmonsters.battle.ui.widgets.LevelUpWidget;
import de.limbusdev.guardianmonsters.battle.ui.widgets.MonsterMenuWidget;
import de.limbusdev.guardianmonsters.battle.ui.widgets.SevenButtonsWidget;
import de.limbusdev.guardianmonsters.battle.ui.widgets.TargetMenuWidget;
import de.limbusdev.guardianmonsters.battle.utils.BattleStringBuilder;
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.battle.AttackCalculationReport;
import de.limbusdev.guardianmonsters.guardians.battle.BattleCalculator;
import de.limbusdev.guardianmonsters.guardians.battle.BattleSystem;
import de.limbusdev.guardianmonsters.guardians.items.Inventory;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.ui.Constant;


/**
 * BattleHUD manages all actions and UI elements in the {@link BattleScreen}
 *
 * @author Georg Eckert 2015
 */
public class BattleHUD extends ABattleHUD
{

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
    private TargetMenuWidget            targetAreaMenuWidget;
    private MonsterMenuWidget           monsterMenuWidget;

    // CallbackHandlers
    private BattleActionMenuWidget.Callbacks    battleActionCallbacks;
    private BattleMainMenuWidget.Callbacks      mainMenuCallbacks;
    private BattleActionMenuWidget.Callbacks    infoLabelCallbacks;
    private BattleActionMenuWidget.Callbacks    endOfBattleCallbacks;
    private SevenButtonsWidget.Callbacks        attackMenuCallbacks;
    private BattleSystem.Callbacks              battleSystemCallbacks;
    private SevenButtonsWidget.Callbacks        targetMenuCallbacks;
    private SevenButtonsWidget.Callbacks        targetAreaMenuCallbacks;
    private BattleActionMenuWidget.Callbacks    battleStartLabelCallbacks;
    private BattleActionMenuWidget.Callbacks    backToActionMenuCallbacks;
    private BattleActionMenuWidget.Callbacks    escapeSuccessCallbacks;
    private BattleActionMenuWidget.Callbacks    escapeFailCallbacks;
    private BattleAnimationWidget.Callbacks     battleAnimationCallbacks;
    private SevenButtonsWidget.Callbacks        monsterMenuCallbacks;

    private Team leftTeam, rightTeam;
    private Inventory inventory;


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ CONSTRUCTOR
    public BattleHUD(Inventory inventory)
    {
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
        battleSystem.getQueue().addObserver(battleQueueWidget);

        // initialize attack menu with active monster
        attackMenu.init(battleSystem.getActiveMonster());

        statusWidget.init(battleSystem);
        animationWidget.init(battleSystem);
        targetMenuWidget.init(battleSystem);
        targetAreaMenuWidget.init(battleSystem, true);

        battleQueueWidget.updateQueue(battleSystem.getQueue());

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
    public void setUpUI()
    {
        // Second stage
        FitViewport viewport = new FitViewport(640,360);
        battleAnimationStage = new Stage(viewport);
        addAddtitionalStage(battleAnimationStage);

        // Widgets
        statusWidget        = new BattleStatusOverviewWidget(skin);
        animationWidget     = new BattleAnimationWidget(battleAnimationCallbacks);
        mainMenu            = new BattleMainMenuWidget(skin, mainMenuCallbacks);
        actionMenu          = new BattleActionMenuWidget(skin, battleActionCallbacks);
        attackMenu          = new AttackMenuWidget(skin, attackMenuCallbacks::onButtonNr);
        targetMenuWidget    = new TargetMenuWidget(skin, targetMenuCallbacks);
        targetAreaMenuWidget= new TargetMenuWidget(skin, targetAreaMenuCallbacks);
        monsterMenuWidget   = new MonsterMenuWidget(skin, monsterMenuCallbacks);

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
                monsterMenuWidget.init(battleSystem, Constant.LEFT);
                battleStateSwitcher.toTeamMenu();
            }

            @Override
            public void onBagButton() {
                // TODO make new widget, as this one belongs to package inventory and is not accessible here
                /*stage.addActor(new ItemChoice(Services.getUI().getInventorySkin(), inventory, leftTeam, battleSystem));*/
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
                if(BattleCalculator.tryToRun(leftTeam, rightTeam)) {
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
                for(AGuardian m : leftTeam.values()) {
                    if(m.getIndividualStatistics().isFit()) {
                        teamOk = true || teamOk;
                    } else {
                        teamOk = false || teamOk;
                    }
                }
                if(!teamOk) Services.getScreenManager().getGame().create();
                else {
                    Services.getAudio().stopMusic();
                    BattleResultScreen resultScreen = new BattleResultScreen(leftTeam, battleSystem.getResult());
                    Services.getScreenManager().pushScreen(resultScreen);
                }
            }
        };

        attackMenuCallbacks = nr ->
        {
            AGuardian activeGuardian = battleSystem.getActiveMonster();
            System.out.println("AttackMenuButtons: onButtonNr("+nr+")");
            System.out.println("Input: User chose attack Nr. " + nr);
            int chosenAttackNr = activeGuardian.getAbilityGraph().getActiveAbilities().indexOfValue(activeGuardian.getAbilityGraph().getActiveAbility(nr),false);
            battleSystem.setChosenAttack(chosenAttackNr);

            Ability.aID abilityID = activeGuardian.getAbilityGraph().getActiveAbility(nr);
            boolean areaAttack = GuardiansServiceLocator.getAbilities().getAbility(abilityID).areaDamage;

            if(areaAttack) {
                battleStateSwitcher.toTargetAreaChoice();
            } else {
                battleStateSwitcher.toTargetChoice();
            }
        };

        battleSystemCallbacks = new BattleSystem.Callbacks()
        {
            @Override
            public void onBattleEnds(boolean winnerSide)
            {
                battleStateSwitcher.toEndOfBattle(winnerSide);
            }

            @Override
            public void onDoingNothing(AGuardian guardian)
            {
                String guardianName = Services.getL18N().getGuardianNicknameIfAvailable(guardian);
                battleStateSwitcher.toAnimation();
                infoLabelWidget.setWholeText(
                    Services.getL18N().Battle().format("batt_item_usage", guardianName)
                );
                infoLabelWidget.animateTextAppearance();
                animationWidget.animateItemUsage();
            }

            @Override
            public void onPlayersTurn()
            {
                battleStateSwitcher.toActionMenu();
            }

            @Override
            public void onMonsterKilled(AGuardian m)
            {
                boolean side = battleSystem.getQueue().getTeamSideFor(m);
                int pos = battleSystem.getQueue().getFieldPositionFor(m);
                animationWidget.animateMonsterKO(pos,side);
            }

            @Override
            public void onAttack(AGuardian attacker, AGuardian target, Ability ability, AttackCalculationReport rep)
            {
                // Change widget set
                battleStateSwitcher.toAnimation();
                infoLabelWidget.setWholeText(BattleStringBuilder.givenDamage(attacker,target,rep));
                infoLabelWidget.animateTextAppearance();

                // Start ability animation
                int attPos, defPos;
                boolean activeSide;
                boolean passiveSide;

                activeSide =  battleSystem.getQueue().getTeamSideFor(attacker);
                passiveSide = battleSystem.getQueue().getTeamSideFor(target);

                attPos = battleSystem.getQueue().getFieldPositionFor(attacker);
                defPos = battleSystem.getQueue().getFieldPositionFor(target);

                animationWidget.animateAttack(attPos, defPos, activeSide, passiveSide, ability);
            }

            @Override
            public void onAreaAttack(AGuardian attacker, ArrayMap<Integer,AGuardian> targets, Ability ability, Array<AttackCalculationReport> reports)
            {
                // Change widget set
                battleStateSwitcher.toAnimation();
                infoLabelWidget.setWholeText(BattleStringBuilder.givenDamage(attacker,reports));
                infoLabelWidget.animateTextAppearance();

                // Start ability animation
                boolean activeSide;
                boolean passiveSide;

                activeSide =  battleSystem.getQueue().getTeamSideFor(attacker);
                passiveSide = battleSystem.getQueue().getTeamSideFor(targets.firstValue());

                int attPos = battleSystem.getQueue().getFieldPositionFor(attacker);

                animationWidget.animateAreaAttack(attPos, activeSide, passiveSide, ability);
            }

            @Override
            public void onDefense(AGuardian defensiveGuardian)
            {
                battleStateSwitcher.toAnimation();
                infoLabelWidget.setWholeText(BattleStringBuilder.selfDefense(defensiveGuardian));
                infoLabelWidget.animateTextAppearance();
                animationWidget.animateSelfDefense();
            }
        };

        targetMenuCallbacks = nr ->
        {
            System.out.println("TargetMenuButtons: onButtonNr("+nr+")");
            AGuardian target = targetMenuWidget.getMonsterOfIndex(nr);
            battleSystem.setChosenTarget(target);
            battleSystem.attack();
        };

        targetAreaMenuCallbacks = nr ->
        {
            System.out.println("TargetMenuButtons: onButtonNr("+nr+")");
            battleSystem.setChosenArea(targetAreaMenuWidget.getCombatTeamOfIndex(nr));
            battleSystem.attack();
        };

        backToActionMenuCallbacks = new BattleActionMenuWidget.Callbacks()
        {
            @Override
            public void onBackButton() {
                System.out.println("BackToActionMenuButtons: onBackButton()");
                battleStateSwitcher.toActionMenu();
            }
        };

        escapeSuccessCallbacks = new BattleActionMenuWidget.Callbacks()
        {
            @Override
            public void onBackButton() {
                System.out.println("EscapeSuccessButtons: onBackButton()");
                goToPreviousScreen();
            }
        };

        escapeFailCallbacks = new BattleActionMenuWidget.Callbacks()
        {
            @Override
            public void onBackButton() {
                System.out.println("EscapeFailButtons: onBackButton()");
                battleSystem.continueBattle();
            }
        };

        battleAnimationCallbacks = () ->
        {
            battleSystem.applyAttack();
            actionMenu.enable(actionMenu.backButton);
        };

        monsterMenuCallbacks = nr ->
        {
            System.out.println("Teammember " + nr);
            battleSystem.replaceActiveMonster(leftTeam.get(nr));
        };
    }

    private void showLevelUp(AGuardian m)
    {
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

        public void toEndOfBattle(boolean winnerSide)
        {
            reset();
            toInfoLabel();
            String textKey = !winnerSide ? "batt_you_won":"batt_game_over";
            String wholeText = Services.getL18N().Battle().get(textKey);
            infoLabelWidget.setWholeText(wholeText);
            infoLabelWidget.animateTextAppearance();
            actionMenu.setCallbacks(endOfBattleCallbacks);

            statusWidget.addToStage(stage);

            state = BattleState.ENDOFBATTLE;

            Action endOfBattleMusicSequence = Actions.sequence(
                Services.getAudio().getMuteAudioAction(AssetPath.Audio.Music.VICTORY_SONG),
                Actions.run(() -> Services.getAudio().playMusic(AssetPath.Audio.Music.VICTORY_FANFARE)),
                Actions.delay(5),
                Actions.run(() -> Services.getAudio().playMusic(AssetPath.Audio.Music.VICTORY_SONG))
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

        public void toTargetAreaChoice()
        {
            reset();
            animationWidget.addToStage(battleAnimationStage);
            statusWidget.addToStage(battleAnimationStage);
            actionMenu.disableAllButBackButton();
            actionMenu.addToStage(stage);
            actionMenu.setCallbacks(backToActionMenuCallbacks);
            targetAreaMenuWidget.addToStage(stage);
            battleQueueWidget.addToStage(stage);

            state = BattleState.TARGET_AREA_CHOICE;
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

        public void reset()
        {
            // Remove all Widgets
            monsterMenuWidget.remove();
            infoLabelWidget.remove();
            animationWidget.remove();
            mainMenu.remove();
            targetMenuWidget.remove();
            targetAreaMenuWidget.remove();
            attackMenu.remove();
            statusWidget.remove();
            actionMenu.remove();
            battleQueueWidget.remove();

            actionMenu.enable();
        }
    }

    private enum BattleState
    {
        MAINMENU, ACTIONMENU, ATTACKMENU, ENDOFBATTLE, ANIMATION, TARGET_CHOICE, BATTLE_START, TARGET_AREA_CHOICE,
    }

}
