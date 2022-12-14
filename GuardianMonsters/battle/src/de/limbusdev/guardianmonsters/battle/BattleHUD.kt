package de.limbusdev.guardianmonsters.battle

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.viewport.FitViewport

import de.limbusdev.guardianmonsters.battle.ui.widgets.*
import de.limbusdev.guardianmonsters.battle.utils.BattleMessages
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.abilities.Ability
import de.limbusdev.guardianmonsters.guardians.battle.AttackCalculationReport
import de.limbusdev.guardianmonsters.guardians.battle.BattleCalculator
import de.limbusdev.guardianmonsters.guardians.battle.BattleSystem
import de.limbusdev.guardianmonsters.guardians.items.ChakraCrystalItem
import de.limbusdev.guardianmonsters.guardians.items.Inventory
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics.StatusEffect
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.inventory.ui.widgets.items.ItemChoice
import de.limbusdev.guardianmonsters.services.Services

import de.limbusdev.guardianmonsters.guardians.Side
import de.limbusdev.guardianmonsters.guardians.monsters.GuardoSphere
import de.limbusdev.utils.extensions.toLCString
import de.limbusdev.utils.extensions.toggle
import de.limbusdev.utils.logDebug
import ktx.actors.plusAssign

/**
 * BattleHUD manages all actions and UI elements in the [BattleScreen]
 *
 * For a readable code, the callback initialization has been separated to various initialization
 * methods. Fot this reason **lateinit** is used on all the properties. The developer has to make
 * sure, all objects are initialized, before calling show().
 *
 * BattleHUD is a state machine, that keeps track of the current state with an instance of
 * [State].
 *
 * @author Georg Eckert 2015
 */
class BattleHUD(private val inventory: Inventory) : ABattleHUD()
{
    // .................................................................................. Properties
    // .................................................................. Logic
    private lateinit var battleSystem                   : BattleSystem

    private lateinit var leftTeam                       : Team
    private lateinit var rightTeam                      : Team

    private lateinit var guardoSphere                   : GuardoSphere

    private lateinit var battleAnimationStage           : Stage
    private lateinit var stateMachine                   : BattleStateMachine


    // ................................................................. Groups
    // BattleWidgets
    private lateinit var mainMenu                       : BattleMainMenuWidget
    private lateinit var actionMenu                     : BattleActionMenuWidget
    private lateinit var animationWidget                : BattleAnimationWidget
    private lateinit var statusWidget                   : BattleStatusOverviewWidget
    private lateinit var battleQueueWidget              : BattleQueueWidget
    private lateinit var abilityDetailBackButton        : BattleActionMenuWidget
    private lateinit var abilityInfoMenuFrame           : BattleActionMenuWidget
    private lateinit var switchActiveGuardianWidget     : SwitchActiveGuardianWidget

    private lateinit var abilityMenuAddOn               : SevenButtonsWidget.CentralHalfButtonsAddOn

    // SevenButtonsWidget
    private lateinit var abilityMenu                    : AbilityMenuWidget
    private lateinit var abilityInfoMenu                : AbilityMenuWidget
    private lateinit var targetMenuWidget               : TargetMenuWidget
    private lateinit var targetAreaMenuWidget           : TargetMenuWidget

    // InfoLabelWidget
    private lateinit var infoLabelWidget                : InfoLabelWidget
    private lateinit var abilityDetailWidget            : AbilityInfoLabelWidget


    // ...................................................... Callback Handlers
    private lateinit var onActionMenuBackButton         : () -> Unit
    private lateinit var onActionMenuBagButton          : () -> Unit
    private lateinit var onActionMenuTeamButton         : () -> Unit
    private lateinit var onActionMenuExtraButton        : () -> Unit
    private lateinit var onInfoLabelBackButton          : () -> Unit
    private lateinit var onBattleStartLabelBackButton   : () -> Unit
    private lateinit var onStatusEffectLabelBackButton  : () -> Unit
    private lateinit var onAbilityDetailLabelBackButton : () -> Unit
    private lateinit var onEndOfBattleLabelBackButton   : () -> Unit
    private lateinit var onBackToActionMenu             : () -> Unit
    private lateinit var onEscapeSuccessLabelBackButton : () -> Unit
    private lateinit var onEscapeFailedLabelBackButton  : () -> Unit
    private lateinit var onMainMenuSwordButton          : () -> Unit
    private lateinit var onMainMenuRunButton            : () -> Unit
    private lateinit var onTeamMenuBackButton           : () -> Unit
    private lateinit var onTeamMenuSwitchButton         : () -> Unit
    private lateinit var onBanSuccessBackButton         : () -> Unit
    private lateinit var onBanFailureBackButton         : () -> Unit

    private lateinit var battleEventHandler             : BattleSystem.EventHandler
    private lateinit var onBattleAnimationHitComplete   : () -> Unit
    private lateinit var onBattleAnimationDying         : () -> Unit
    private lateinit var onBattleAnimationDoNothing     : () -> Unit

    private lateinit var onAbilityMenuButton            : (Int) -> Unit
    private lateinit var onTargetMenuButton             : (Int) -> Unit
    private lateinit var onTargetAreaMenuButton         : (Int) -> Unit
    private lateinit var onAbilityMenuInfoButton        : (Int) -> Unit
    private lateinit var onAbilityInfoMenuButton        : (Int) -> Unit


    // ................................................................................ Constructors
    init
    {
        setUpCallbacks()
        initializeWidgets()
    }


    // .............................................................................. Initialization
    /** Initializes the battle screen with the given teams */
    fun initialize(heroTeam: Team, opponentTeam: Team, guardoSphere: GuardoSphere, wildEncounter: Boolean = true)
    {
        logDebug(TAG) { "initialize(...)" }

        reset()

        // keep Guardian teams
        this.leftTeam = heroTeam
        this.rightTeam = opponentTeam
        this.guardoSphere = guardoSphere

        // initialize independent battle system
        battleSystem = BattleSystem(heroTeam, opponentTeam, battleEventHandler, wildEncounter)
        battleSystem.queue.addObserver(battleQueueWidget)

        // initialize attack menu with active monster
        abilityMenu.initialize(battleSystem.activeGuardian, true)

        // initialize other widgets
        statusWidget.initialize(battleSystem)
        animationWidget.initialize(battleSystem)
        targetMenuWidget.initialize(battleSystem)
        targetAreaMenuWidget.initialize(battleSystem, true)

        // run first queue update
        battleQueueWidget.updateQueue(battleSystem.queue)
    }

    /** Resets the UI into a state where it can be initialized for a new battle */
    override fun reset()
    {
        logDebug(TAG) { "reset()" }

        super.reset()
        actionMenu.clearActions()
        mainMenu.clearActions()
    }


    // ............................................................................. libGDX's Screen
    override fun show()
    {
        logDebug(TAG) { "show()" }

        super.show()
        stateMachine.to(State.BATTLE_START)
    }


    // ..................................................................................... Methods



    // ............................................................................... Inner Classes
    /**
     * BattleStateMachine represents a state machine. It handles changing the states and activation
     * as well as deactivation of the necessary widgets.
     */
    private inner class BattleStateMachine
    {
        var state: State = State.BATTLE_START
            private set

        private val TAG : String = "BattleStateMachine"

        /** Use only named parameters. */
        fun to
        (
                newState        : State,
                winnerSide      : Boolean? = null,
                aID             : Ability.aID? = null,
                bannedGuardian  : AGuardian? = null,
                crystalItem     : ChakraCrystalItem? = null,
                fieldPos        : Int? = null
        ) {
            when(newState)
            {
                State.ANIMATION         -> toAnimation()
                State.ABILITY_DETAIL    -> toAbilityDetail(aID)
                State.ABILITY_INFO_MENU -> toAbilityInfoMenu()
                State.ABILITY_MENU      -> toAbilityMenu()
                State.BATTLE_START      -> toBattleStart()
                State.END_OF_BATTLE     -> toEndOfBattle(winnerSide)
                State.MAIN_MENU         -> toMainMenu()
                State.TARGET_AREA_CHOICE-> toTargetAreaChoice()
                State.TARGET_CHOICE     -> toTargetChoice()
                State.TEAM_MENU         -> toTeamMenu()
                State.BANNED_LAST       -> toEndOfBattleByBanningLastOpponent(bannedGuardian, crystalItem)
                State.STATUS_EFFECT_INFO-> toStatusEffectInfoLabel()
                State.ESCAPE_SUCCESS    -> toEscapeSuccessInfo()
                State.ESCAPE_FAILURE    -> toEscapeFailInfo()
                State.BAN_SUCCESS       -> toBanSuccess(bannedGuardian, crystalItem, fieldPos)
                State.BAN_FAILURE       -> toBanFailure(bannedGuardian, crystalItem, fieldPos)
            }
        }

        private fun toBattleStart()                                                        // TESTED
        {
            logDebug(TAG) { "${"toBattleStart()".padEnd(40)} -> new State: ${State.BATTLE_START}" }

            reset()

            // Add Widgets
            battleAnimationStage += animationWidget
            battleAnimationStage += statusWidget
            stage += infoLabelWidget
            stage += actionMenu

            // Set Callbacks
            actionMenu.setCallbacksAndAutoEnable(onBackButton = onBattleStartLabelBackButton)
            infoLabelWidget.typeWrite(Services.I18N().Battle("battle_start"))

            state = State.BATTLE_START
        }

        private fun toMainMenu()
        {
            logDebug(TAG) { "${"toMainMenu()".padEnd(40)} -> new State: ${State.MAIN_MENU}" }

            reset()
            actionMenu.disable()

            battleAnimationStage += animationWidget
            battleAnimationStage += statusWidget
            stage += actionMenu
            stage += mainMenu

            state = State.MAIN_MENU
        }

        private fun toEscapeSuccessInfo()                                                  // TESTED
        {
            logDebug(TAG) { "${"toEscapeSuccessInfo()".padEnd(40)} -> new State: ${State.ESCAPE_SUCCESS}" }

            showInfoLabel()
            infoLabelWidget.typeWrite(Services.I18N().Battle("escape_success"))
            actionMenu.setCallbacks(onBackButton = onEscapeSuccessLabelBackButton)

            state = State.ESCAPE_SUCCESS
        }

        private fun toEscapeFailInfo()                                                     // TESTED
        {
            logDebug(TAG) { "${"toEscapeFailInfo()".padEnd(40)} -> new State: ${State.ESCAPE_FAILURE}" }

            showInfoLabel()
            infoLabelWidget.typeWrite(Services.I18N().Battle("escape_fail"))
            actionMenu.setCallbacks(onBackButton = onEscapeFailedLabelBackButton)

            state = State.ESCAPE_FAILURE
        }

        private fun toBanSuccess(bannedGuardian: AGuardian?, crystal: ChakraCrystalItem?, fieldPos: Int?)
        {
            checkNotNull(bannedGuardian)
            checkNotNull(crystal)
            checkNotNull(fieldPos)
            logDebug(TAG) { "${"toBanningSuccess()".padEnd(40)} -> new State: ${State.BAN_SUCCESS}" }

            // Display info label and disable all buttons
            showInfoLabel()
            actionMenu.disableAllChildButtons()

            // Set, what the back button will do and write the ban success message
            infoLabelWidget.typeWrite(BattleMessages.banGuardianSuccess(bannedGuardian, crystal))

            // Animate banning success and re-enable back button after animation
            animationWidget.animateBanning(fieldPos, Side.RIGHT, bannedGuardian)
            { actionMenu.setCallbacksAndAutoEnable(onBackButton = onBanSuccessBackButton) }

            state = State.BAN_SUCCESS

            check(!guardoSphere.isFull()) { "If GuardoSphere is full, banning should be impossible." }
            guardoSphere += bannedGuardian
        }

        // TESTED
        private fun toBanFailure(bannedGuardian: AGuardian?, crystal: ChakraCrystalItem?, fieldPos: Int?)
        {
            checkNotNull(bannedGuardian)
            checkNotNull(crystal)
            checkNotNull(fieldPos)
            logDebug(TAG) { "${"toBanningFailure()".padEnd(40)} -> new State: ${State.BAN_FAILURE}" }

            // Display info label and disable all buttons
            showInfoLabel()
            actionMenu.disableAllChildButtons()

            // Set the back button to continue with the next Guardian, if ban fails
            infoLabelWidget.typeWrite(BattleMessages.banGuardianFailure(bannedGuardian, crystal))

            // Animate banning failure and re-enable back button after animation
            animationWidget.animateBanningFailure(fieldPos, Side.RIGHT, bannedGuardian)
            { actionMenu.setCallbacksAndAutoEnable(onBackButton = onBanSuccessBackButton) }

            state = State.BAN_FAILURE
        }

        private fun toAbilityMenu()
        {
            logDebug(TAG) { "${"toAbilityMenu()".padEnd(40)} -> new State: ${State.ABILITY_MENU}" }

            showActionMenu()
            state = State.ABILITY_MENU
        }

        private fun toAbilityInfoMenu()
        {
            logDebug(TAG) { "${"toAbilityInfoMenu()".padEnd(40)} -> new State: ${State.ABILITY_INFO_MENU}" }

            reset()

            battleAnimationStage += animationWidget
            battleAnimationStage += statusWidget
            stage += battleQueueWidget
            stage += abilityInfoMenu
            stage += abilityMenuAddOn
            stage += abilityInfoMenuFrame

            abilityInfoMenu.initialize(battleSystem.activeGuardian, false)
            abilityInfoMenu.toAttackInfoStyle()

            state = State.ABILITY_INFO_MENU
        }

        private fun toAbilityDetail(aID: Ability.aID?)
        {
            checkNotNull(aID)
            logDebug(TAG) { "${"toAbilityDetail($aID)".padEnd(40)} -> new State: ${State.ABILITY_DETAIL}" }

            reset()

            battleAnimationStage += animationWidget
            battleAnimationStage += statusWidget
            stage += battleQueueWidget
            stage += abilityMenuAddOn
            stage += abilityDetailWidget
            stage += abilityDetailBackButton

            abilityDetailWidget.initialize(aID)

            state = State.ABILITY_DETAIL
        }

        private fun toEndOfBattle(winnerSide: Boolean?)
        {
            checkNotNull(winnerSide)
            logDebug(TAG) { "${"toEndOfBattle($winnerSide)".padEnd(40)} -> new State: ${State.END_OF_BATTLE}" }

            reset()

            showInfoLabel()
            val textKey = if(winnerSide) "battle_you_won" else "battle_game_over"
            infoLabelWidget.typeWrite(Services.I18N().Battle(textKey))
            actionMenu.setCallbacks(onBackButton = onEndOfBattleLabelBackButton)

            statusWidget.addToStage(stage)

            stage.addAction(Services.Audio().createEndOfBattleMusicSequence())

            state = State.END_OF_BATTLE
        }

        private fun toEndOfBattleByBanningLastOpponent(bannedGuardian: AGuardian?, crystal: ChakraCrystalItem?)
        {
            checkNotNull(bannedGuardian)
            checkNotNull(crystal)
            logDebug(TAG) { "${"toEndOfBattleByBanningLastOpponent(...)".padEnd(40)} -> new State: ${State.END_OF_BATTLE}" }

            reset()

            showInfoLabel()
            infoLabelWidget.typeWrite(BattleMessages.banGuardianSuccess(bannedGuardian, crystal))
            actionMenu.setCallbacks(onBackButton = onEndOfBattleLabelBackButton)

            stage += statusWidget

            stage.addAction(Services.Audio().createEndOfBattleMusicSequence())

            state = State.END_OF_BATTLE
        }

        private fun toAnimation()
        {
            logDebug(TAG) { "${"toAnimation()".padEnd(40)} -> new State: ${State.ANIMATION}" }

            reset()
            showInfoLabel()
            actionMenu.disableAllChildButtons()
            stage += battleQueueWidget
            actionMenu.setCallbacks(onBackButton = onInfoLabelBackButton)

            state = State.ANIMATION
        }

        private fun toTargetChoice()
        {
            logDebug(TAG) { "${"toTargetChoice()".padEnd(40)} -> new State: ${State.TARGET_CHOICE}" }

            reset()

            battleAnimationStage += animationWidget
            battleAnimationStage += statusWidget
            stage += actionMenu
            stage += targetMenuWidget
            stage += battleQueueWidget

            actionMenu.setCallbacksAndAutoEnable(onBackButton = onBackToActionMenu)

            state = State.TARGET_CHOICE
        }

        private fun toTargetAreaChoice()
        {
            logDebug(TAG) { "${"toTargetAreaChoice()".padEnd(40)} -> new State: ${State.TARGET_AREA_CHOICE}" }

            reset()

            battleAnimationStage += animationWidget
            battleAnimationStage += statusWidget
            stage += actionMenu
            stage += targetAreaMenuWidget
            stage += battleQueueWidget

            actionMenu.setCallbacksAndAutoEnable(onBackButton = onBackToActionMenu)

            state = State.TARGET_AREA_CHOICE
        }

        private fun toTeamMenu()
        {
            logDebug(TAG) { "${"toTeamMenu()".padEnd(40)} -> new State: ${State.TEAM_MENU}" }

            reset()

            stage += switchActiveGuardianWidget

            state = State.TEAM_MENU
        }

        private fun toStatusEffectInfoLabel()
        {
            logDebug(TAG) { "${"toStatusEffectInfoLabel()".padEnd(40)} -> new State: ${State.STATUS_EFFECT_INFO}" }

            reset()
            showInfoLabel()
            stage += battleQueueWidget
            actionMenu.setCallbacks(onBackButton = onStatusEffectLabelBackButton)

            state = State.STATUS_EFFECT_INFO
        }


        /** Helper function for displaying the action menu. */
        private fun showActionMenu()
        {
            logDebug(TAG) { "showActionMenu()" }

            reset()


            // Add Widgets
            battleAnimationStage += animationWidget
            battleAnimationStage += statusWidget
            stage += battleQueueWidget
            stage += actionMenu
            stage += abilityMenu
            stage += abilityMenuAddOn

            // Setup Widgets
            actionMenu.setCallbacks(

                    onBackButton  = onActionMenuBackButton,
                    onBagButton   = onActionMenuBagButton,
                    onTeamButton  = onActionMenuTeamButton,
                    onExtraButton = onActionMenuExtraButton
            )
            abilityMenu.initialize(battleSystem.activeGuardian, true)
        }


        /** Helper function for displaying information. */
        private fun showInfoLabel()
        {
            logDebug(TAG) { "showInfoLabel()" }

            reset()

            battleAnimationStage += animationWidget
            battleAnimationStage += statusWidget
            stage += infoLabelWidget
            stage += actionMenu

            actionMenu.disableAllButBackButton()
        }

        /** Removes all widgets and enables the action menu. */
        private fun reset()
        {
            infoLabelWidget.remove()
            animationWidget.remove()
            mainMenu.remove()
            targetMenuWidget.remove()
            targetAreaMenuWidget.remove()
            abilityMenu.remove()
            statusWidget.remove()
            actionMenu.remove()
            battleQueueWidget.remove()
            abilityMenuAddOn.remove()
            abilityDetailBackButton.remove()
            abilityDetailWidget.remove()
            abilityInfoMenuFrame.remove()
            abilityInfoMenu.remove()
            switchActiveGuardianWidget.remove()

            actionMenu.enable()
        }
    }



    /** State is used to store what state [BattleHUD] is currently in. */
    private enum class State
    {
        MAIN_MENU,                  // the menu widget with: Fight and Run buttons
        ABILITY_MENU,               // a 7 buttons menu widget, that allows choosing abilities
        END_OF_BATTLE,              // when the battle has come to an end
        ANIMATION,                  // state of ability animation
        TARGET_CHOICE,              // a menu that allows choosing an ability target from one of the teams
        BATTLE_START,               // the first widget to be shown in a battle
        TARGET_AREA_CHOICE,         // a menu that allows choosing a whole team as ability target
        ABILITY_INFO_MENU,           // a menu that shows the chosen Guardian's abilities and opens details
        ABILITY_DETAIL,             // a widget, that shows information about a chosen ability
        TEAM_MENU,                  // the menu that shows all Guardians of the hero's team
        BANNED_LAST,                // when the battle ended due to banning the last opponent
        STATUS_EFFECT_INFO,         // shows an information label about new status effect
        ESCAPE_SUCCESS,             // shown, when escaping succeeds
        ESCAPE_FAILURE,             // shown, when escaping fails
        BAN_SUCCESS,            // shown, when banning succeeds
        BAN_FAILURE             // shown, when banning fails
    }


    //////////////////////////////////////////////////////////////////////////////////////////////// SETUP
    // ...................................................................................... Layout
    /** Setting up HUD elements */
    private fun initializeWidgets()
    {
        logDebug(TAG) { "initializeWidgets()" }

        // Second stage
        val viewport = FitViewport(640f, 360f)
        battleAnimationStage = Stage(viewport)
        addAdditionalStage(battleAnimationStage)

        // Widgets
        mainMenu        = BattleMainMenuWidget(onMainMenuSwordButton, onMainMenuRunButton)
        statusWidget    = BattleStatusOverviewWidget()

        animationWidget = BattleAnimationWidget(

                onHitAnimationComplete  = onBattleAnimationHitComplete,
                onDying                 = onBattleAnimationDying,
                onDoingNothing          = onBattleAnimationDoNothing
        )

        abilityMenuAddOn = SevenButtonsWidget.CentralHalfButtonsAddOn(onAbilityMenuInfoButton)

        abilityMenu          = AbilityMenuWidget(callbacks = { ID -> onAbilityMenuButton(ID)     })
        abilityInfoMenu      = AbilityMenuWidget(callbacks = { ID -> onAbilityInfoMenuButton(ID) })
        targetMenuWidget     = TargetMenuWidget(callbacks  = { ID -> onTargetMenuButton(ID)      })
        targetAreaMenuWidget = TargetMenuWidget(callbacks  = { ID -> onTargetAreaMenuButton(ID)  })

        abilityInfoMenuFrame    = BattleActionMenuWidget()
        abilityDetailBackButton = BattleActionMenuWidget(onBackButton = onAbilityDetailLabelBackButton)

        actionMenu = BattleActionMenuWidget(

                onBackButton  = onActionMenuBackButton,
                onBagButton   = onActionMenuBagButton,
                onTeamButton  = onActionMenuTeamButton,
                onExtraButton = onActionMenuExtraButton
        )

        battleQueueWidget = BattleQueueWidget(Align.bottomLeft)
        battleQueueWidget.setPosition(1f, 65f, Align.bottomLeft)

        infoLabelWidget     = InfoLabelWidget()
        abilityDetailWidget = AbilityInfoLabelWidget()

        abilityDetailBackButton.disableAllButBackButton()
        abilityInfoMenuFrame.disableAllChildButtons()

        switchActiveGuardianWidget = SwitchActiveGuardianWidget()
        switchActiveGuardianWidget.setCallbacks(

                onBack = onTeamMenuBackButton,
                onSwitch = onTeamMenuSwitchButton
        )
    }


    // ................................................................................... Callbacks
    private fun setUpCallbacks()
    {
        logDebug(TAG) { "setUpCallbacks()" }

        stateMachine = BattleStateMachine()


        // ............................................................................... main menu
        onMainMenuSwordButton = {

            logDebug(TAG) { "onMainMenuSwordButton" }
            battleSystem.continueBattle() // calls onPlayersTurn() or onAIPlayersTurn()
        }

        onMainMenuRunButton = {

            logDebug(TAG) { "onMainMenuRunButton" }
            when(BattleCalculator.runSucceeds(leftTeam, rightTeam))
            {
                true  -> stateMachine.to(State.ESCAPE_SUCCESS)
                false -> stateMachine.to(State.ESCAPE_FAILURE)
            }
        }


        // ............................................................................... team menu
        onTeamMenuBackButton    = {

            logDebug(TAG) { "onTeamMenuBackButton" }
            stateMachine.to(State.ABILITY_MENU)
        }

        onTeamMenuSwitchButton  = {

            logDebug(TAG) { "onTeamMenuSwitchButton" }
            stateMachine.to(State.ANIMATION)
            val substituteNr = switchActiveGuardianWidget.chosenSubstitute
            val substitute   = battleSystem.queue.left[substituteNr]
            battleSystem.replaceActiveMonsterWith(substitute)
        }


        // ...................................................................... battle start label
        onBattleStartLabelBackButton  = {

            logDebug(TAG) { "onBattleStartLabelBackButton" }
            stateMachine.to(State.MAIN_MENU)
        }


        // ...................................................................... battle action menu
        onActionMenuBackButton = {

            logDebug(TAG) { "onActionMenuBackButton" }
            stateMachine.to(State.MAIN_MENU)
        }

        onActionMenuExtraButton = {

            logDebug(TAG) { "onActionMenuExtraButton" }
            battleSystem.defend()
        }

        onActionMenuBagButton = {

            logDebug(TAG) { "onActionMenuBagButton" }
            stage.addActor(ItemChoice(inventory, leftTeam, battleSystem, guardoSphere))
        }

        onActionMenuTeamButton = {

            logDebug(TAG) { "onActionMenuTeamButton" }
            switchActiveGuardianWidget.initialize(

                    battleSystem.activeGuardian,
                    battleSystem.queue.left,
                    battleSystem.queue.combatTeamLeft
            )
            stateMachine.to(State.TEAM_MENU)
        }


        // .............................................................................. info label
        onInfoLabelBackButton = {

            logDebug(TAG) { "onInfoLabelBackButton" }
            when(battleSystem.activeGuardian.stats.statusEffect)
            {
                StatusEffect.HEALTHY ->
                {
                    battleSystem.nextGuardian()
                    battleSystem.continueBattle()
                }
                else ->
                {
                    stateMachine.to(State.STATUS_EFFECT_INFO)
                    battleSystem.applyStatusEffect()
                }
            }
        }


        // ..................................................................... status effect label
        onStatusEffectLabelBackButton = {

            logDebug(TAG) { "onStatusEffectLabelBackButton" }
            actionMenu.setCallbacks(onBackButton = onInfoLabelBackButton)
            battleSystem.nextGuardian()
            battleSystem.continueBattle()
        }


        // ........................................................................... end of battle
        onEndOfBattleLabelBackButton = {

            logDebug(TAG) { "onEndOfBattleLabelBackButton" }

            // Battle is lost, when all active Guardians are KO
            // Revive them, to prevent this. Even fit Guardians
            // in the team do not help. Only those currently in
            // the battle are taken into account.
            if(battleSystem.queue.combatTeamLeft.isKO())
            {
                // Battle is lost, restart game
                Services.Audio().stopMusic()
                Services.ScreenManager().game.create()
            }
            else
            {
                // Battle is won, show result screen
                Services.Audio().stopMusic()
                val resultScreen = BattleResultScreen(leftTeam, battleSystem.result)
                Services.ScreenManager().pushScreen(resultScreen)
            }
        }


        // ............................................................................. attack menu
        onAbilityMenuButton = { buttonID ->

            logDebug(TAG) { "onAbilityMenuButton" }
            val activeGuardian = battleSystem.activeGuardian
            logDebug(TAG) { "AbilityMenuButtons: User has chosen ability $buttonID" }

            battleSystem.setChosenAttack(buttonID)

            val abilityID         = activeGuardian.abilityGraph.activeAbilities[buttonID]
            checkNotNull(abilityID)
            val appliesAreaDamage = GuardiansServiceLocator.abilities.getAbility(abilityID).areaDamage

            if(appliesAreaDamage) { stateMachine.to(State.TARGET_AREA_CHOICE) }
            else                  { stateMachine.to(State.TARGET_CHOICE)      }
        }


        // ........................................................................ attack info menu
        onAbilityInfoMenuButton = { buttonID ->

            logDebug(TAG) { "onAbilityInfoMenuButton" }
            val abilityID = battleSystem.activeGuardian.abilityGraph.activeAbilities[buttonID]
            checkNotNull(abilityID)
            stateMachine.to(State.ABILITY_DETAIL, aID = abilityID)
        }


        // ..................................................................... attack detail label
        onAbilityDetailLabelBackButton = {

            logDebug(TAG) { "onAbilityDetailLabelBackButton" }
            stateMachine.to(State.ABILITY_INFO_MENU)
        }


        // ................................................................. attack menu info switch
        onAbilityMenuInfoButton = object : (Int) -> Unit
        {
            private var checked = false

            override fun invoke(buttonID: Int)
            {
                logDebug(TAG) { "onAbilityMenuInfoButton.invoke()" }
                checked = checked.toggle()
                if(buttonID == BattleHUDTextButton.CENTER_TOP)
                {
                    if(checked) { stateMachine.to(State.ABILITY_INFO_MENU) }
                    else        { stateMachine.to(State.ABILITY_MENU)      }
                }
            }
        }


        // ........................................................................... battle system
        battleEventHandler = object : BattleSystem.EventHandler()
        {
            override fun onPlayersTurn()
            {
                logDebug("BattleSystem.battleEventHandler") { "onPlayersTurn()" }

                stateMachine.to(State.ABILITY_MENU)
            }

            override fun onBanning(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int, continueBanning: () -> Unit)
            {
                logDebug("BattleSystem.battleEventHandler") { "onBanning()" }

                stateMachine.to(State.ANIMATION)
                infoLabelWidget.typeWrite(BattleMessages.tryingToBan(bannedGuardian, crystal))

                animationWidget.animateBanning(fieldPos, Side.RIGHT, bannedGuardian) { continueBanning.invoke() }
            }

            override fun onBanningFailure(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int)
            {
                logDebug("BattleSystem.battleEventHandler") { "onBanningFailure()" }

                stateMachine.to(

                        State.BAN_FAILURE,
                        bannedGuardian = bannedGuardian,
                        crystalItem = crystal,
                        fieldPos = fieldPos
                )
            }

            override fun onBanningSuccess(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int)
            {
                logDebug("BattleSystem.battleEventHandler") { "onBanningSuccess()" }

                stateMachine.to(

                        State.BAN_SUCCESS,
                        bannedGuardian = bannedGuardian,
                        crystalItem = crystal,
                        fieldPos = fieldPos
                )
            }

            override fun onBattleEnds(winnerSide: Boolean)
            {
                logDebug("BattleSystem.battleEventHandler") { "onBattleEnds()" }

                stateMachine.to(State.END_OF_BATTLE, winnerSide = winnerSide)
            }

            override fun onDoingNothing(guardian: AGuardian)
            {
                logDebug("BattleSystem.battleEventHandler") { "onDoingNothing()" }

                val guardianName = Services.I18N().getGuardianNicknameIfAvailable(guardian)
                stateMachine.to(State.ANIMATION)

                val message = when(guardian.stats.statusEffect)
                {
                    StatusEffect.PETRIFIED ->
                    {
                        val petrified = Services.I18N().Battle("batt_petrified")
                        Services.I18N().Battle("batt_message_failed", guardianName, petrified)
                    }
                    else ->
                    {
                        Services.I18N().Battle("batt_item_usage", guardianName)
                    }
                }

                infoLabelWidget.typeWrite(message)
                animationWidget.animateItemUsage()
            }

            override fun onGuardianDefeated(guardian: AGuardian)
            {
                logDebug("BattleSystem.battleEventHandler") { "onGuardianDefeated()" }

                val side = battleSystem.queue.getTeamSideFor(guardian)
                val pos  = battleSystem.queue.getFieldPositionFor(guardian)
                animationWidget.animateMonsterKO(pos, side)
            }


            override fun onAttack(attacker: AGuardian, target: AGuardian, ability: Ability, report: AttackCalculationReport)
            {
                logDebug("BattleSystem.battleEventHandler") { "onAttack()" }

                stateMachine.to(State.ANIMATION)
                infoLabelWidget.typeWrite(BattleMessages.givenDamage(attacker, target, report))


                if(!report.statusEffectPreventedAttack)
                {
                    // Start ability animation
                    val activeSide = battleSystem.queue.getTeamSideFor(attacker)
                    val passiveSide = battleSystem.queue.getTeamSideFor(target)

                    val attPos = battleSystem.queue.getFieldPositionFor(attacker)
                    val defPos = battleSystem.queue.getFieldPositionFor(target)

                    animationWidget.animateAttack(attPos, defPos, activeSide, passiveSide, ability)
                }
            }

            override fun onAreaAttack
                    (
                            attacker: AGuardian,
                            targets: ArrayMap<Int, AGuardian>,
                            ability: Ability,
                            reports: Array<AttackCalculationReport>
                    ){
                logDebug("BattleSystem.battleEventHandler") { "onAreaAttack()" }

                stateMachine.to(State.ANIMATION)
                infoLabelWidget.typeWrite(BattleMessages.givenDamage(attacker, reports))

                if(!reports.first().statusEffectPreventedAttack)
                {
                    // Start ability animation
                    val activeSide = battleSystem.queue.getTeamSideFor(attacker)
                    val passiveSide = battleSystem.queue.getTeamSideFor(targets.firstValue())

                    val attPos = battleSystem.queue.getFieldPositionFor(attacker)

                    animationWidget.animateAreaAttack(attPos, activeSide, passiveSide, ability)
                }
            }

            override fun onApplyStatusEffect(guardian: AGuardian)
            {
                logDebug("BattleSystem.battleEventHandler") { "onApplyStatusEffect()" }

                val nickName = Services.I18N().getGuardianNicknameIfAvailable(guardian)
                val statusEffect = guardian.stats.statusEffect.toLCString()
                val message  = Services.I18N().Battle("batt_info_status_effect_$statusEffect")
                infoLabelWidget.typeWrite("$nickName $message")
            }

            override fun onDefense(defensiveGuardian: AGuardian)
            {
                logDebug("BattleSystem.battleEventHandler") { "onDefense()" }

                stateMachine.to(State.ANIMATION)
                infoLabelWidget.typeWrite(BattleMessages.selfDefense(defensiveGuardian))
                animationWidget.animateSelfDefense()
            }

            override fun onGuardianSubstituted(substituted: AGuardian, substitute: AGuardian, fieldPos: Int)
            {
                logDebug("BattleSystem.battleEventHandler") { "onGuardianSubstituted()" }

                stateMachine.to(State.ANIMATION)
                infoLabelWidget.typeWrite(BattleMessages.substitution(substituted, substitute))

                val substitutedSide = battleSystem.queue.getTeamSideFor(substituted)

                animationWidget.animateGuardianSubstitution(

                        fieldPos,
                        substitutedSide,
                        { actionMenu.enable(actionMenu.backButton) },
                        substituted,
                        substitute
                )

                statusWidget.updateStatusWidgetToSubstitute(fieldPos, substitutedSide, substitute)

                targetMenuWidget.initialize(battleSystem)
                targetAreaMenuWidget.initialize(battleSystem, true)
            }

            override fun onReplacingDefeatedGuardian(substituted: AGuardian, substitute: AGuardian, fieldPos: Int)
            {
                logDebug("BattleSystem.battleEventHandler") { "onReplacingDefeatedGuardian()" }

                stateMachine.to(State.ANIMATION)
                infoLabelWidget.typeWrite(BattleMessages.replacingDefeated(substituted, substitute))

                animationWidget.animateReplacingDefeatedGuardian(

                        fieldPos,
                        battleSystem.queue.getTeamSideFor(substituted),
                        { actionMenu.enable(actionMenu.backButton) },
                        substituted,
                        substitute
                )

                statusWidget.updateStatusWidgetToSubstitute(

                        fieldPos,
                        battleSystem.queue.getTeamSideFor(substituted),
                        substitute
                )

                targetMenuWidget.initialize(battleSystem)
                targetAreaMenuWidget.initialize(battleSystem, true)
            }
        }


        // ......................................................................................... target menu
        onTargetMenuButton = { buttonID ->

            logDebug(TAG) { "onTargetMenuButton.invoke($buttonID)" }
            val target = targetMenuWidget.getMonsterOfIndex(buttonID)
            battleSystem.setChosenTarget(target)
            battleSystem.calculateAttack()
        }


        // ......................................................................................... target area menu
        onTargetAreaMenuButton = { buttonID ->

            logDebug(TAG) { "onTargetAreaMenuButton.invoke($buttonID)" }
            battleSystem.setChosenArea(targetAreaMenuWidget.getCombatTeamOfIndex(buttonID))
            battleSystem.calculateAttack()
        }


        // ......................................................................................... back to action menu
        onBackToActionMenu = {

            logDebug(TAG) { "onBackToActionMenu" }
            stateMachine.to(State.ABILITY_MENU)
        }

        // ......................................................................................... ban success / fail
        onBanSuccessBackButton = {

            logDebug(TAG) { "onBanSuccessBackButton"}
            battleSystem.finishBattleByBanning()
        }

        onBanFailureBackButton = {

            logDebug(TAG) { "onBanFailureBackButton" }
            battleSystem.nextGuardian()
            battleSystem.continueBattle()
        }


        // ......................................................................................... escape success / fail
        onEscapeSuccessLabelBackButton = {

            logDebug(TAG) { "onEscapeSuccessLabelBackButton" }
            goToPreviousScreen()
        }

        onEscapeFailedLabelBackButton = {

            logDebug(TAG) { "onEscapeFailedLabelBackButton" }

            // Allow running at the beginning, without losing a turn
            if(stateMachine.state != State.BATTLE_START) { battleSystem.nextGuardian() }
            battleSystem.continueBattle()
        }


        // ......................................................................................... battle animation
        onBattleAnimationHitComplete = {

            logDebug(TAG) { "onBattleAnimationHitComplete" }
            val defeated = battleSystem.applyAttack()
            if(!defeated || battleSystem.queue.right.allKO || battleSystem.queue.left.allKO)
            {
                actionMenu.enable(actionMenu.backButton)
            }
        }

        onBattleAnimationDying = {

            logDebug(TAG) { "onBattleAnimationDying" }
            actionMenu.enable(actionMenu.backButton)
        }

        onBattleAnimationDoNothing = {

            logDebug(TAG) { "onBattleAnimationDoNothing" }
            actionMenu.enable(actionMenu.backButton)
        }
    }


    companion object
    {
        private const val TAG: String = "BattleHUD"
    }
}
