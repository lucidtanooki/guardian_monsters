package de.limbusdev.guardianmonsters.battle;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.limbusdev.guardianmonsters.assets.paths.AssetPath;
import de.limbusdev.guardianmonsters.battle.ui.widgets.AbilityInfoLabelWidget;
import de.limbusdev.guardianmonsters.battle.ui.widgets.AttackMenuWidget;
import de.limbusdev.guardianmonsters.battle.ui.widgets.BattleActionMenuWidget;
import de.limbusdev.guardianmonsters.battle.ui.widgets.BattleAnimationWidget;
import de.limbusdev.guardianmonsters.battle.ui.widgets.BattleMainMenuWidget;
import de.limbusdev.guardianmonsters.battle.ui.widgets.BattleQueueWidget;
import de.limbusdev.guardianmonsters.battle.ui.widgets.BattleStatusOverviewWidget;
import de.limbusdev.guardianmonsters.battle.ui.widgets.InfoLabelWidget;
import de.limbusdev.guardianmonsters.battle.ui.widgets.LevelUpWidget;
import de.limbusdev.guardianmonsters.battle.ui.widgets.SevenButtonsWidget;
import de.limbusdev.guardianmonsters.battle.ui.widgets.SwitchActiveGuardianWidget;
import de.limbusdev.guardianmonsters.battle.ui.widgets.TargetMenuWidget;
import de.limbusdev.guardianmonsters.battle.utils.BattleStringBuilder;
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.battle.AttackCalculationReport;
import de.limbusdev.guardianmonsters.guardians.battle.BattleCalculator;
import de.limbusdev.guardianmonsters.guardians.battle.BattleSystem;
import de.limbusdev.guardianmonsters.guardians.items.Inventory;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.ui.widgets.Callback;

import static de.limbusdev.guardianmonsters.battle.ui.widgets.BattleHUDTextButton.CENTERTOP;


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
    private AttackMenuWidget            attackInfoMenu;
    private BattleAnimationWidget       animationWidget;
    private BattleStatusOverviewWidget  statusWidget;
    private BattleQueueWidget           battleQueueWidget;
    private InfoLabelWidget             infoLabelWidget;
    private TargetMenuWidget            targetMenuWidget;
    private TargetMenuWidget            targetAreaMenuWidget;
    private AbilityInfoLabelWidget      attackDetailWidget;
    private BattleActionMenuWidget      attackDetailBackButton;
    private BattleActionMenuWidget      attackInfoMenuFrame;
    private SwitchActiveGuardianWidget  switchActiveGuardianWidget;

    private SevenButtonsWidget.CentralHalfButtonsAddOn attackMenuAddOn;

    // CallbackHandlers
    private Callback
        actionMenuBackCB, actionMenuBagCB, actionMenuMonsterCB, actionMenuExtraCB;
    private Callback infoLabelBackCB;
    private Callback battleStartLabelBackCB;
    private Callback statusEffectLabelBackCB;
    private Callback attackDetailLabelBackCB;
    private Callback endOfBattleLabelBackCB;
    private Callback backToActionMenuCB;
    private Callback escapeSuccessLabelBackCB;
    private Callback escapeFailedLabelBackCB;
    private Callback mainMenuOnSwordButton;
    private Callback mainMenuOnRunButton;
    private Callback teamMenuOnBackButton, teamMenuOnSwitchButton;

    private BattleSystem.Callbacks          battleSystemCallbacks;
    private BattleAnimationWidget.Callbacks battleAnimationCallbacks;

    private Callback.ButtonID attackMenuCallbacks;
    private Callback.ButtonID targetMenuCallbacks;
    private Callback.ButtonID targetAreaMenuCallbacks;
    private Callback.ButtonID attackMenuAddOnCallbacks;
    private Callback.ButtonID attackInfoMenuCallbacks;

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
    protected void reset()
    {
        super.reset();
        actionMenu.clearActions();
        mainMenu.clearActions();
    }

    /**
     * Initializes the battle screen with the given teams
     * @param heroTeam
     * @param opponentTeam
     */
    public void init(Team heroTeam, Team opponentTeam)
    {
        reset();

        // Keep monster teams
        this.leftTeam = heroTeam;
        this.rightTeam = opponentTeam;

        // initialize independent battle system
        battleSystem = new BattleSystem(heroTeam,opponentTeam, battleSystemCallbacks);
        battleSystem.getQueue().addObserver(battleQueueWidget);

        // initialize attack menu with active monster
        attackMenu.init(battleSystem.getActiveMonster(), true);

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
    public void show()
    {
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
        mainMenu            = new BattleMainMenuWidget(skin, mainMenuOnSwordButton, mainMenuOnRunButton);
        statusWidget        = new BattleStatusOverviewWidget(skin);
        animationWidget     = new BattleAnimationWidget(battleAnimationCallbacks);
        attackMenuAddOn     = new SevenButtonsWidget.CentralHalfButtonsAddOn(skin, attackMenuAddOnCallbacks);

        attackMenu          = new AttackMenuWidget (skin, attackMenuCallbacks       ::onClick);
        attackInfoMenu      = new AttackMenuWidget (skin, attackInfoMenuCallbacks   ::onClick);
        targetMenuWidget    = new TargetMenuWidget (skin, targetMenuCallbacks       ::onClick);
        targetAreaMenuWidget= new TargetMenuWidget (skin, targetAreaMenuCallbacks   ::onClick);

        attackInfoMenuFrame    = new BattleActionMenuWidget(skin, () -> {});
        attackDetailBackButton = new BattleActionMenuWidget(skin, attackDetailLabelBackCB::onClick);
        actionMenu             = new BattleActionMenuWidget(
            skin,
            actionMenuBackCB    :: onClick,
            actionMenuBagCB     :: onClick,
            actionMenuMonsterCB :: onClick,
            actionMenuExtraCB   :: onClick);

        battleQueueWidget = new BattleQueueWidget(skin, Align.bottomLeft);
        battleQueueWidget.setPosition(1,65, Align.bottomLeft);

        infoLabelWidget = new InfoLabelWidget(skin);
        attackDetailWidget = new AbilityInfoLabelWidget(skin, Services.getUI().getInventorySkin());

        attackDetailBackButton.disableAllButBackButton();
        attackInfoMenuFrame.disableAllChildButtons();

        switchActiveGuardianWidget = new SwitchActiveGuardianWidget(skin, Services.getUI().getInventorySkin());
        switchActiveGuardianWidget.setCallbacks(teamMenuOnBackButton, teamMenuOnSwitchButton);
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ CALLBACKS

    private void setUpCallbacks()
    {
        battleStateSwitcher = new BattleStateSwitcher();

        // ......................................................................................... main menu
        mainMenuOnSwordButton = () -> battleSystem.continueBattle();
        mainMenuOnRunButton = () ->
        {
            if(BattleCalculator.tryToRun(leftTeam, rightTeam)) {
                battleStateSwitcher.toEscapeSuccessInfo();
            } else {
                battleStateSwitcher.toEscapeFailInfo();
            }
        };

        // ......................................................................................... team menu
        teamMenuOnBackButton = () -> battleStateSwitcher.toAttackMenu();
        teamMenuOnSwitchButton = () ->
        {
            battleStateSwitcher.toAnimation();
            int substituteNr = switchActiveGuardianWidget.getChosenSubstitute();
            AGuardian substitute = battleSystem.getQueue().getLeft().get(substituteNr);
            battleSystem.replaceActiveMonster(substitute);
        };

        // ......................................................................................... battle start label
        battleStartLabelBackCB = () -> battleStateSwitcher.toMainMenu();

        // ......................................................................................... battle action menu
        actionMenuBackCB = () -> battleStateSwitcher.toMainMenu();
        actionMenuExtraCB = () -> battleSystem.defend();

        actionMenuBagCB = () ->
        {
            // TODO make new widget, as this one belongs to package inventory and is not accessible here
            /*stage.addActor(new ItemChoice(Services.getUI().getInventorySkin(), inventory, leftTeam, battleSystem));*/
        };

        actionMenuMonsterCB = () ->
        {
            switchActiveGuardianWidget.init(
                battleSystem.getActiveMonster(),
                battleSystem.getQueue().getLeft()
            );
            battleStateSwitcher.toTeamMenu();
        };

        // ......................................................................................... info label
        infoLabelBackCB = () ->
        {
            IndividualStatistics.StatusEffect statusEffect = battleSystem
                .getActiveMonster()
                .getIndividualStatistics()
                .getStatusEffect();

            if(statusEffect == IndividualStatistics.StatusEffect.HEALTHY) {
                battleSystem.nextMonster();
                battleSystem.continueBattle();
            } else {
                battleStateSwitcher.toStatusEffectInfoLabel();
                battleSystem.applyStatusEffect();
            }
        };

        // ......................................................................................... status effect label
        statusEffectLabelBackCB = () ->
        {
            actionMenu.setCallbacks(infoLabelBackCB, ()->{}, ()->{}, ()->{});
            battleSystem.nextMonster();
            battleSystem.continueBattle();
        };

        // ......................................................................................... end of battle
        endOfBattleLabelBackCB = () ->
        {
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
        };

        // ......................................................................................... attack menu
        attackMenuCallbacks = nr ->
        {
            AGuardian activeGuardian = battleSystem.getActiveMonster();
            System.out.println("AttackMenuButtons: onButtonNr("+nr+")");
            System.out.println("Input: User chose attack Nr. " + nr);
            int chosenAttackNr = activeGuardian
                .getAbilityGraph()
                .getActiveAbilities()
                .indexOfValue(activeGuardian.getAbilityGraph().getActiveAbility(nr),false);
            battleSystem.setChosenAttack(chosenAttackNr);

            Ability.aID abilityID = activeGuardian.getAbilityGraph().getActiveAbility(nr);
            boolean areaAttack = GuardiansServiceLocator.getAbilities().getAbility(abilityID).areaDamage;

            if(areaAttack) {
                battleStateSwitcher.toTargetAreaChoice();
            } else {
                battleStateSwitcher.toTargetChoice();
            }
        };

        // ......................................................................................... attack info menu
        attackInfoMenuCallbacks = nr -> battleStateSwitcher.toAttackDetail(battleSystem
                    .getActiveMonster()
                    .getAbilityGraph()
                    .getActiveAbility(nr));

        // ......................................................................................... attack detail label
        attackDetailLabelBackCB = () -> battleStateSwitcher.toAttackInfoMenu();

        // ......................................................................................... attack menu info switch
        attackMenuAddOnCallbacks = new Callback.ButtonID()
        {
            private boolean checked = false;

            @Override
            public void onClick(int buttonID)
            {
                checked = !checked;
                switch(buttonID)
                {
                    case CENTERTOP:
                        if(checked) {battleStateSwitcher.toAttackInfoMenu();}
                        else        {battleStateSwitcher.toAttackMenu();}
                        break;
                    default:
                        break;
                }
            }
        };

        // ......................................................................................... battle system
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
                String message;
                if(guardian.getIndividualStatistics().getStatusEffect() == IndividualStatistics.StatusEffect.PETRIFIED) {
                    message = Services.getL18N().Battle().format(
                        "batt_message_failed",
                        guardianName,
                        Services.getL18N().Battle().get("batt_petrified"));
                } else {
                    message = Services.getL18N().Battle().format("batt_item_usage", guardianName);
                }
                infoLabelWidget.setWholeText(message);
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


                if(rep.statusEffectPreventedAttack) {
                    return;
                } else {
                    // Start ability animation
                    int attPos, defPos;
                    boolean activeSide;
                    boolean passiveSide;

                    activeSide = battleSystem.getQueue().getTeamSideFor(attacker);
                    passiveSide = battleSystem.getQueue().getTeamSideFor(target);

                    attPos = battleSystem.getQueue().getFieldPositionFor(attacker);
                    defPos = battleSystem.getQueue().getFieldPositionFor(target);

                    animationWidget.animateAttack(attPos, defPos, activeSide, passiveSide, ability);
                }
            }

            @Override
            public void onAreaAttack(AGuardian attacker, ArrayMap<Integer,AGuardian> targets,
                                     Ability ability, Array<AttackCalculationReport> reports)
            {
                // Change widget set
                battleStateSwitcher.toAnimation();
                infoLabelWidget.setWholeText(BattleStringBuilder.givenDamage(attacker,reports));
                infoLabelWidget.animateTextAppearance();

                if(reports.first().statusEffectPreventedAttack) {
                    return;
                } else {
                    // Start ability animation
                    boolean activeSide;
                    boolean passiveSide;

                    activeSide = battleSystem.getQueue().getTeamSideFor(attacker);
                    passiveSide = battleSystem.getQueue().getTeamSideFor(targets.firstValue());

                    int attPos = battleSystem.getQueue().getFieldPositionFor(attacker);

                    animationWidget.animateAreaAttack(attPos, activeSide, passiveSide, ability);
                }
            }

            @Override
            public void onApplyStatusEffect(AGuardian guardian)
            {
                infoLabelWidget.setWholeText(
                        Services.getL18N().getGuardianNicknameIfAvailable(guardian) + " " +
                        Services.getL18N().Battle().get("batt_info_status_effect_"
                            + guardian.getIndividualStatistics().getStatusEffect().toString().toLowerCase()));
                infoLabelWidget.animateTextAppearance();
            }

            @Override
            public void onDefense(AGuardian defensiveGuardian)
            {
                battleStateSwitcher.toAnimation();
                infoLabelWidget.setWholeText(BattleStringBuilder.selfDefense(defensiveGuardian));
                infoLabelWidget.animateTextAppearance();
                animationWidget.animateSelfDefense();
            }

            @Override
            public void onGuardianSubstituted(AGuardian substituted, AGuardian substitute)
            {
                battleStateSwitcher.toAnimation();
                infoLabelWidget.setWholeText(BattleStringBuilder.substitution(substituted, substitute));
                infoLabelWidget.animateTextAppearance();
                animationWidget.animateGuardianSubstitution(
                    battleSystem.getQueue().getFieldPositionFor(substituted),
                    battleSystem.getQueue().getTeamSideFor(substituted),
                    substitute.getSpeciesID(),
                    substitute.getAbilityGraph().getCurrentForm()
                );


            }
        };

        // ......................................................................................... target menu
        targetMenuCallbacks = nr ->
        {
            AGuardian target = targetMenuWidget.getMonsterOfIndex(nr);
            battleSystem.setChosenTarget(target);
            battleSystem.attack();
        };

        // ......................................................................................... target area menu
        targetAreaMenuCallbacks = nr ->
        {
            battleSystem.setChosenArea(targetAreaMenuWidget.getCombatTeamOfIndex(nr));
            battleSystem.attack();
        };

        // ......................................................................................... back to action menu
        backToActionMenuCB = () -> battleStateSwitcher.toActionMenu();

        // ......................................................................................... escape success / fail
        escapeSuccessLabelBackCB = () -> goToPreviousScreen();
        escapeFailedLabelBackCB  = () -> battleSystem.continueBattle();

        // ......................................................................................... battle animation
        battleAnimationCallbacks = () ->
        {
            battleSystem.applyAttack();
            actionMenu.enable(actionMenu.backButton);
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

        public void toBattleStart()
        {
            reset();

            // Add Widgets
            animationWidget.addToStageAndFadeIn(battleAnimationStage);
            statusWidget.addToStageAndFadeIn(battleAnimationStage);
            infoLabelWidget.addToStageAndFadeIn(stage);
            actionMenu.addToStageAndFadeIn(stage);

            // Set Widget State
            actionMenu.disableAllButBackButton();

            // Set Callbacks
            actionMenu.setCallbacks(battleStartLabelBackCB, ()->{}, ()->{}, ()->{});
            infoLabelWidget.setWholeText(Services.getL18N().Battle().get("battle_start"));
            infoLabelWidget.animateTextAppearance();

            state = BattleState.BATTLE_START;
        }

        public void toActionMenu()
        {
            reset();

            // Add Widgets
            animationWidget.addToStage(battleAnimationStage);
            statusWidget.addToStage(battleAnimationStage);
            battleQueueWidget.addToStage(stage);
            actionMenu.addToStage(stage);
            attackMenu.addToStage(stage);
            attackMenuAddOn.addToStage(stage);

            // Setup Widgets
            actionMenu.setCallbacks(actionMenuBackCB, actionMenuBagCB, actionMenuMonsterCB, actionMenuExtraCB);
            attackMenu.init(battleSystem.getActiveMonster(), true);

            state = BattleState.ACTIONMENU;
        }

        public void toAttackMenu()
        {
            toActionMenu();
            state = BattleState.ACTIONMENU;
        }

        public void toAttackInfoMenu()
        {
            reset();

            animationWidget.addToStage(battleAnimationStage);
            statusWidget.addToStage(battleAnimationStage);
            battleQueueWidget.addToStage(stage);
            attackInfoMenu.addToStage(stage);
            attackMenuAddOn.addToStage(stage);
            attackInfoMenuFrame.addToStage(stage);

            attackInfoMenu.init(battleSystem.getActiveMonster(), false);
            attackInfoMenu.toAttackInfoStyle();

            state = BattleState.ATTACK_INFO_MENU;
        }

        public void toAttackDetail(Ability.aID aID)
        {
            reset();

            animationWidget.addToStage(battleAnimationStage);
            statusWidget.addToStage(battleAnimationStage);
            battleQueueWidget.addToStage(stage);
            attackMenuAddOn.addToStage(stage);
            attackDetailWidget.addToStage(stage);
            attackDetailBackButton.addToStage(stage);

            attackDetailWidget.init(aID);

            state = BattleState.ATTACK_DETAIL;
        }

        public void toEndOfBattle(boolean winnerSide)
        {
            reset();
            toInfoLabel();
            String textKey = !winnerSide ? "batt_you_won":"batt_game_over";
            String wholeText = Services.getL18N().Battle().get(textKey);
            infoLabelWidget.setWholeText(wholeText);
            infoLabelWidget.animateTextAppearance();
            actionMenu.setCallbacks(endOfBattleLabelBackCB, ()->{}, ()->{}, ()->{});

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

        public void toAnimation()
        {
            reset();
            toInfoLabel();
            actionMenu.disableAllChildButtons();
            battleQueueWidget.addToStage(stage);
            actionMenu.setCallbacks(infoLabelBackCB, ()->{}, ()->{}, ()->{});

            state = BattleState.ANIMATION;
        }

        public void toTargetChoice()
        {
            reset();
            animationWidget.addToStage(battleAnimationStage);
            statusWidget.addToStage(battleAnimationStage);
            actionMenu.disableAllButBackButton();
            actionMenu.addToStage(stage);
            actionMenu.setCallbacks(backToActionMenuCB, ()->{}, ()->{}, ()->{});
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
            actionMenu.setCallbacks(backToActionMenuCB, ()->{}, ()->{}, ()->{});
            targetAreaMenuWidget.addToStage(stage);
            battleQueueWidget.addToStage(stage);

            state = BattleState.TARGET_AREA_CHOICE;
        }

        public void toTeamMenu()
        {
            reset();
            switchActiveGuardianWidget.addToStage(stage);

            state = BattleState.TEAM_MENU;
        }

        public void toMainMenu()
        {
            reset();
            animationWidget.addToStage(battleAnimationStage);
            statusWidget.addToStage(battleAnimationStage);
            actionMenu.disable();
            actionMenu.addToStage(stage);
            mainMenu.addToStageAndFadeIn(stage);

            state = BattleState.MAINMENU;
        }

        public void toInfoLabel()
        {
            reset();

            animationWidget.addToStage(battleAnimationStage);
            statusWidget.addToStage(battleAnimationStage);
            infoLabelWidget.addToStage(stage);
            actionMenu.addToStage(stage);

            actionMenu.disableAllButBackButton();
        }

        public void toStatusEffectInfoLabel()
        {
            reset();
            toInfoLabel();
            battleQueueWidget.addToStage(stage);
            actionMenu.setCallbacks(statusEffectLabelBackCB, ()->{}, ()->{}, ()->{});

            state = BattleState.ANIMATION;
        }

        public void toEscapeSuccessInfo()
        {
            toInfoLabel();
            String wholeText = Services.getL18N().Battle().get("escape_success");
            infoLabelWidget.setWholeText(wholeText);
            infoLabelWidget.animateTextAppearance();
            actionMenu.setCallbacks(escapeSuccessLabelBackCB, ()->{}, ()->{}, ()->{});
        }

        public void toEscapeFailInfo()
        {
            toInfoLabel();
            String wholeText = Services.getL18N().Battle().get("escape_fail");
            infoLabelWidget.setWholeText(wholeText);
            infoLabelWidget.animateTextAppearance();
            actionMenu.setCallbacks(escapeFailedLabelBackCB, ()->{}, ()->{}, ()->{});
        }

        public void reset()
        {
            // Remove all Widgets
            infoLabelWidget.remove();
            animationWidget.remove();
            mainMenu.remove();
            targetMenuWidget.remove();
            targetAreaMenuWidget.remove();
            attackMenu.remove();
            statusWidget.remove();
            actionMenu.remove();
            battleQueueWidget.remove();
            attackMenuAddOn.remove();
            attackDetailBackButton.remove();
            attackDetailWidget.remove();
            attackInfoMenuFrame.remove();
            attackInfoMenu.remove();
            switchActiveGuardianWidget.remove();

            actionMenu.enable();
        }
    }

    private enum BattleState
    {
        MAINMENU, ACTIONMENU, ATTACKMENU, ENDOFBATTLE, ANIMATION, TARGET_CHOICE, BATTLE_START,
        TARGET_AREA_CHOICE, ATTACK_INFO_MENU, ATTACK_DETAIL, TEAM_MENU
    }

}
