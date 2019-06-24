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
import de.limbusdev.utils.extensions.arrayMapOf
import de.limbusdev.utils.extensions.toLCString
import de.limbusdev.utils.extensions.toggle
import ktx.log.info


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
    lateinit var battleSystem                   : BattleSystem

    lateinit var leftTeam                       : Team
    lateinit var rightTeam                      : Team

    lateinit var guardoSphere                   : GuardoSphere

    lateinit var battleAnimationStage           : Stage
    private lateinit var stateMachine                   : BattleStateMachine


    // ................................................................. Groups
    // BattleWidgets
    lateinit var mainMenu                       : BattleMainMenuWidget
    lateinit var actionMenu                     : BattleActionMenuWidget
    lateinit var animationWidget                : BattleAnimationWidget
    lateinit var battleQueueWidget              : BattleQueueWidget
    lateinit var abilityDetailBackButton        : BattleActionMenuWidget

    val statusWidget                   : BattleStatusOverviewWidget = BattleStatusOverviewWidget()
    val abilityInfoMenuFrame           : BattleActionMenuWidget     = BattleActionMenuWidget()
    val switchActiveGuardianWidget     : SwitchActiveGuardianWidget = SwitchActiveGuardianWidget()

    lateinit var abilityMenuAddOn               : SevenButtonsWidget.CentralHalfButtonsAddOn

    // SevenButtonsWidget
    lateinit var abilityMenu                    : AbilityMenuWidget
    lateinit var abilityInfoMenu                : AbilityMenuWidget
    lateinit var targetMenuWidget               : TargetMenuWidget
    lateinit var targetAreaMenuWidget           : TargetMenuWidget

    // InfoLabelWidget
    val infoLabelWidget                : InfoLabelWidget            = InfoLabelWidget()
    val abilityDetailWidget            : AbilityInfoLabelWidget     = AbilityInfoLabelWidget()


    // ...................................................... Callback Handlers
    lateinit var onActionMenuBackButton         : () -> Unit
    lateinit var onActionMenuBagButton          : () -> Unit
    lateinit var onActionMenuTeamButton         : () -> Unit
    lateinit var onActionMenuExtraButton        : () -> Unit
    lateinit var onInfoLabelBackButton          : () -> Unit
    lateinit var onBattleStartLabelBackButton   : () -> Unit
    lateinit var onStatusEffectLabelBackButton  : () -> Unit
    lateinit var onAbilityDetailLabelBackButton : () -> Unit
    lateinit var onEndOfBattleLabelBackButton   : () -> Unit
    lateinit var onBackToActionMenu             : () -> Unit
    lateinit var onEscapeSuccessLabelBackButton : () -> Unit
    lateinit var onEscapeFailedLabelBackButton  : () -> Unit
    lateinit var onMainMenuSwordButton          : () -> Unit
    lateinit var onMainMenuRunButton            : () -> Unit
    lateinit var onTeamMenuBackButton           : () -> Unit
    lateinit var onTeamMenuSwitchButton         : () -> Unit
    lateinit var onBanSuccessBackButton         : () -> Unit
    lateinit var onBanFailureBackButton         : () -> Unit

    lateinit var battleEventHandler             : BattleSystem.EventHandler
    lateinit var onBattleAnimationHitComplete   : () -> Unit
    lateinit var onBattleAnimationDying         : () -> Unit
    lateinit var onBattleAnimationDoNothing     : () -> Unit

    lateinit var onAbilityMenuButton            : (Int) -> Unit
    lateinit var onTargetMenuButton             : (Int) -> Unit
    lateinit var onTargetAreaMenuButton         : (Int) -> Unit
    lateinit var onAbilityMenuInfoButton        : (Int) -> Unit
    lateinit var onAbilityInfoMenuButton        : (Int) -> Unit


    // ................................................................................ Constructors
    init
    {
        setUpCallbacks()
        initializeWidgets()
        stateMachine.initialize()
    }


    // .............................................................................. Initialization
    /** Initializes the battle screen with the given teams */
    fun initialize(heroTeam: Team, opponentTeam: Team, guardoSphere: GuardoSphere, wildEncounter: Boolean = true)
    {
        info(TAG) { "initialize(...)" }

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
        info(TAG) { "reset()" }

        super.reset()
        actionMenu.clearActions()
        mainMenu.clearActions()
    }


    // ............................................................................. libGDX's Screen
    override fun show()
    {
        info(TAG) { "show()" }

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

        private lateinit var battleStartWidgets     : WidgetCollection
        private lateinit var mainMenuWidgets        : WidgetCollection
        private lateinit var escapeSuccessWidgets   : WidgetCollection
        private lateinit var escapeFailureWidgets   : WidgetCollection
        private lateinit var banSuccessWidgets      : ABanningWidgetCollection
        private lateinit var banFailureWidgets      : ABanningWidgetCollection
        private lateinit var abilityMenuWidgets     : WidgetCollection
        private lateinit var abilityInfoMenuWidgets : WidgetCollection

        private val TAG : String = "BattleStateMachine"

        fun initialize()
        {
            battleStartWidgets      = BattleStartWidgetCollection(this@BattleHUD)
            mainMenuWidgets         = MainMenuWidgetCollection(this@BattleHUD)
            escapeSuccessWidgets    = EscapeSuccessInfoWidgetCollection(this@BattleHUD)
            escapeFailureWidgets    = EscapeFailInfoWidgetCollection(this@BattleHUD)
            banSuccessWidgets       = BanSuccessWidgetCollection(this@BattleHUD)
            banFailureWidgets       = BanFailureWidgetCollection(this@BattleHUD)
            abilityMenuWidgets      = AbilityMenuWidgetCollection(this@BattleHUD)
            abilityInfoMenuWidgets  = AbilityInfoMenuWidgetCollection(this@BattleHUD)
        }

        /** Use only named parameters. */
        fun to
        (
                newState: State,
                winnerSide: Boolean? = null,
                aID: Ability.aID? = null,
                bannedGuardian: AGuardian? = null,
                crystalItem: ChakraCrystalItem? = null,
                fieldPos: Int? = null
        ) {
            when(newState)
            {
                State.BATTLE_START      -> { reset(); state = battleStartWidgets.enable() }
                State.ANIMATION         -> toAnimation()
                State.ABILITY_DETAIL    -> toAbilityDetail(aID)
                State.ABILITY_INFO_MENU -> { reset(); state = abilityInfoMenuWidgets.enable() }
                State.ABILITY_MENU      -> { reset(); state = abilityMenuWidgets.enable() }
                State.END_OF_BATTLE     -> toEndOfBattle(winnerSide)
                State.MAIN_MENU         -> { reset(); state = mainMenuWidgets.enable() }
                State.TARGET_AREA_CHOICE-> toTargetAreaChoice()
                State.TARGET_CHOICE     -> toTargetChoice()
                State.TEAM_MENU         -> toTeamMenu()
                State.BANNED_LAST       -> toEndOfBattleByBanningLastOpponent(bannedGuardian, crystalItem)
                State.STATUS_EFFECT_INFO-> toStatusEffectInfoLabel()
                State.ESCAPE_SUCCESS    -> { reset(); state = escapeSuccessWidgets.enable() }
                State.ESCAPE_FAILURE    -> { reset(); state = escapeSuccessWidgets.enable() }
                State.BAN_SUCCESS       -> { reset(); state = banSuccessWidgets.enable(bannedGuardian, crystalItem, fieldPos) }
                State.BAN_FAILURE       -> { reset(); state = banFailureWidgets.enable(bannedGuardian, crystalItem, fieldPos) }
            }
        }

        private fun toAbilityDetail(aID: Ability.aID?)
        {
            checkNotNull(aID)
            info(TAG) { "${"toAbilityDetail($aID)".padEnd(40)} -> new State: ${State.ABILITY_DETAIL}" }

            reset()

            animationWidget.addToStage(battleAnimationStage)
            statusWidget.addToStage(battleAnimationStage)
            battleQueueWidget.addToStage(stage)
            abilityMenuAddOn.addToStage(stage)
            abilityDetailWidget.addToStage(stage)
            abilityDetailBackButton.addToStage(stage)

            abilityDetailWidget.initialize(aID)

            state = State.ABILITY_DETAIL
        }

        private fun toEndOfBattle(winnerSide: Boolean?)
        {
            checkNotNull(winnerSide)
            info(TAG) { "${"toEndOfBattle($winnerSide)".padEnd(40)} -> new State: ${State.END_OF_BATTLE}" }

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
            info(TAG) { "${"toEndOfBattleByBanningLastOpponent(...)".padEnd(40)} -> new State: ${State.END_OF_BATTLE}" }

            reset()

            showInfoLabel()
            infoLabelWidget.typeWrite(BattleMessages.banGuardianSuccess(bannedGuardian, crystal))
            actionMenu.setCallbacks(onBackButton = onEndOfBattleLabelBackButton)

            statusWidget.addToStage(stage)

            stage.addAction(Services.Audio().createEndOfBattleMusicSequence())

            state = State.END_OF_BATTLE
        }

        private fun toAnimation()
        {
            info(TAG) { "${"toAnimation()".padEnd(40)} -> new State: ${State.ANIMATION}" }

            reset()
            showInfoLabel()
            actionMenu.disableAllChildButtons()
            battleQueueWidget.addToStage(stage)
            actionMenu.setCallbacks(onBackButton = onInfoLabelBackButton)

            state = State.ANIMATION
        }

        private fun toTargetChoice()
        {
            info(TAG) { "${"toTargetChoice()".padEnd(40)} -> new State: ${State.TARGET_CHOICE}" }

            reset()
            animationWidget.addToStage(battleAnimationStage)
            statusWidget.addToStage(battleAnimationStage)
            actionMenu.disableAllButBackButton()
            actionMenu.addToStage(stage)
            actionMenu.setCallbacks(onBackButton = onBackToActionMenu)
            targetMenuWidget.addToStage(stage)
            battleQueueWidget.addToStage(stage)

            state = State.TARGET_CHOICE
        }

        private fun toTargetAreaChoice()
        {
            info(TAG) { "${"toTargetAreaChoice()".padEnd(40)} -> new State: ${State.TARGET_AREA_CHOICE}" }

            reset()
            animationWidget.addToStage(battleAnimationStage)
            statusWidget.addToStage(battleAnimationStage)
            actionMenu.disableAllButBackButton()
            actionMenu.addToStage(stage)
            actionMenu.setCallbacks(onBackButton = onBackToActionMenu)
            targetAreaMenuWidget.addToStage(stage)
            battleQueueWidget.addToStage(stage)

            state = State.TARGET_AREA_CHOICE
        }

        private fun toTeamMenu()
        {
            info(TAG) { "${"toTeamMenu()".padEnd(40)} -> new State: ${State.TEAM_MENU}" }

            reset()
            switchActiveGuardianWidget.addToStage(stage)

            state = State.TEAM_MENU
        }

        private fun toStatusEffectInfoLabel()
        {
            info(TAG) { "${"toStatusEffectInfoLabel()".padEnd(40)} -> new State: ${State.STATUS_EFFECT_INFO}" }

            reset()
            showInfoLabel()
            battleQueueWidget.addToStage(stage)
            actionMenu.setCallbacks(onBackButton = onStatusEffectLabelBackButton)

            state = State.STATUS_EFFECT_INFO
        }


        /** Helper function for displaying the action menu. */
        private fun showActionMenu()
        {
            info(TAG) { "showActionMenu()" }

            reset()


            // Add Widgets
            animationWidget.addToStage(battleAnimationStage)
            statusWidget.addToStage(battleAnimationStage)
            battleQueueWidget.addToStage(stage)
            actionMenu.addToStage(stage)
            abilityMenu.addToStage(stage)
            abilityMenuAddOn.addToStage(stage)

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
            info(TAG) { "showInfoLabel()" }

            reset()

            animationWidget.addToStage(battleAnimationStage)
            statusWidget.addToStage(battleAnimationStage)
            infoLabelWidget.addToStage(stage)
            actionMenu.addToStage(stage)

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
    enum class State
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
        info(TAG) { "initializeWidgets()" }

        // Second stage
        val viewport = FitViewport(640f, 360f)
        battleAnimationStage = Stage(viewport)
        addAdditionalStage(battleAnimationStage)

        // Widgets
        mainMenu        = BattleMainMenuWidget(onMainMenuSwordButton, onMainMenuRunButton)

        animationWidget = BattleAnimationWidget(

                onHitAnimationComplete  = onBattleAnimationHitComplete,
                onDieing                = onBattleAnimationDying,
                onDoingNothing          = onBattleAnimationDoNothing
        )

        abilityMenuAddOn = SevenButtonsWidget.CentralHalfButtonsAddOn(onAbilityMenuInfoButton)

        abilityMenu          = AbilityMenuWidget(callbacks = { ID -> onAbilityMenuButton(ID)     })
        abilityInfoMenu      = AbilityMenuWidget(callbacks = { ID -> onAbilityInfoMenuButton(ID) })
        targetMenuWidget     = TargetMenuWidget(callbacks  = { ID -> onTargetMenuButton(ID)      })
        targetAreaMenuWidget = TargetMenuWidget(callbacks  = { ID -> onTargetAreaMenuButton(ID)  })

        abilityDetailBackButton = BattleActionMenuWidget(onBackButton = onAbilityDetailLabelBackButton)

        actionMenu = BattleActionMenuWidget(

                onBackButton  = onActionMenuBackButton,
                onBagButton   = onActionMenuBagButton,
                onTeamButton  = onActionMenuTeamButton,
                onExtraButton = onActionMenuExtraButton
        )

        battleQueueWidget = BattleQueueWidget(Align.bottomLeft)
        battleQueueWidget.setPosition(1f, 65f, Align.bottomLeft)

        abilityDetailBackButton.disableAllButBackButton()
        abilityInfoMenuFrame.disableAllChildButtons()

        switchActiveGuardianWidget.setCallbacks(

                onBack = onTeamMenuBackButton,
                onSwitch = onTeamMenuSwitchButton
        )
    }


    // ................................................................................... Callbacks
    private fun setUpCallbacks()
    {
        info(TAG) { "setUpCallbacks()" }

        stateMachine = BattleStateMachine()


        // ............................................................................... main menu
        onMainMenuSwordButton = {

            info(TAG) { "onMainMenuSwordButton" }
            battleSystem.continueBattle() // calls onPlayersTurn() or onAIPlayersTurn()
        }

        onMainMenuRunButton = {

            info(TAG) { "onMainMenuRunButton" }
            when(BattleCalculator.runSucceeds(leftTeam, rightTeam))
            {
                true  -> stateMachine.to(State.ESCAPE_SUCCESS)
                false -> stateMachine.to(State.ESCAPE_FAILURE)
            }
        }


        // ............................................................................... team menu
        onTeamMenuBackButton    = {

            info(TAG) { "onTeamMenuBackButton" }
            stateMachine.to(State.ABILITY_MENU)
        }

        onTeamMenuSwitchButton  = {

            info(TAG) { "onTeamMenuSwitchButton" }
            stateMachine.to(State.ANIMATION)
            val substituteNr = switchActiveGuardianWidget.chosenSubstitute
            val substitute   = battleSystem.queue.left[substituteNr]
            battleSystem.replaceActiveMonsterWith(substitute)
        }


        // ...................................................................... battle start label
        onBattleStartLabelBackButton  = {

            info(TAG) { "onBattleStartLabelBackButton" }
            stateMachine.to(State.MAIN_MENU)
        }


        // ...................................................................... battle action menu
        onActionMenuBackButton = {

            info(TAG) { "onActionMenuBackButton" }
            stateMachine.to(State.MAIN_MENU)
        }

        onActionMenuExtraButton = {

            info(TAG) { "onActionMenuExtraButton" }
            battleSystem.defend()
        }

        onActionMenuBagButton = {

            info(TAG) { "onActionMenuBagButton" }
            stage.addActor(ItemChoice(inventory, leftTeam, battleSystem, guardoSphere))
        }

        onActionMenuTeamButton = {

            info(TAG) { "onActionMenuTeamButton" }
            switchActiveGuardianWidget.initialize(

                    battleSystem.activeGuardian,
                    battleSystem.queue.left,
                    battleSystem.queue.combatTeamLeft
            )
            stateMachine.to(State.TEAM_MENU)
        }


        // .............................................................................. info label
        onInfoLabelBackButton = {

            info(TAG) { "onInfoLabelBackButton" }
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

            info(TAG) { "onStatusEffectLabelBackButton" }
            actionMenu.setCallbacks(onBackButton = onInfoLabelBackButton)
            battleSystem.nextGuardian()
            battleSystem.continueBattle()
        }


        // ........................................................................... end of battle
        onEndOfBattleLabelBackButton = {

            info(TAG) { "onEndOfBattleLabelBackButton" }

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

            info(TAG) { "onAbilityMenuButton" }
            val activeGuardian = battleSystem.activeGuardian
            println("AbilityMenuButtons: User has chosen ability $buttonID")

            battleSystem.setChosenAttack(buttonID)

            val abilityID         = activeGuardian.abilityGraph.getActiveAbility(buttonID)
            val appliesAreaDamage = GuardiansServiceLocator.abilities.getAbility(abilityID).areaDamage

            if(appliesAreaDamage) { stateMachine.to(State.TARGET_AREA_CHOICE) }
            else                  { stateMachine.to(State.TARGET_CHOICE)      }
        }


        // ........................................................................ attack info menu
        onAbilityInfoMenuButton = { buttonID ->

            info(TAG) { "onAbilityInfoMenuButton" }
            val abilityID = battleSystem.activeGuardian.abilityGraph.getActiveAbility(buttonID)
            stateMachine.to(State.ABILITY_DETAIL, aID = abilityID)
        }


        // ..................................................................... attack detail label
        onAbilityDetailLabelBackButton = {

            info(TAG) { "onAbilityDetailLabelBackButton" }
            stateMachine.to(State.ABILITY_INFO_MENU)
        }


        // ................................................................. attack menu info switch
        onAbilityMenuInfoButton = object : (Int) -> Unit
        {
            private var checked = false

            override fun invoke(buttonID: Int)
            {
                info(TAG) { "onAbilityMenuInfoButton.invoke()" }
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
                info("BattleSystem.battleEventHandler") { "onPlayersTurn()" }

                stateMachine.to(State.ABILITY_MENU)
            }

            override fun onBanning(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int, continueBanning: () -> Unit)
            {
                info("BattleSystem.battleEventHandler") { "onBanning()" }

                stateMachine.to(State.ANIMATION)
                infoLabelWidget.typeWrite(BattleMessages.tryingToBan(bannedGuardian, crystal))

                animationWidget.animateBanning(fieldPos, Side.RIGHT, bannedGuardian) { continueBanning.invoke() }
            }

            override fun onBanningFailure(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int)
            {
                info("BattleSystem.battleEventHandler") { "onBanningFailure()" }

                stateMachine.to(

                        State.BAN_FAILURE,
                        bannedGuardian = bannedGuardian,
                        crystalItem = crystal,
                        fieldPos = fieldPos
                )
            }

            override fun onBanningSuccess(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int)
            {
                info("BattleSystem.battleEventHandler") { "onBanningSuccess()" }

                stateMachine.to(

                        State.BAN_SUCCESS,
                        bannedGuardian = bannedGuardian,
                        crystalItem = crystal,
                        fieldPos = fieldPos
                )
            }

            override fun onBattleEnds(winnerSide: Boolean)
            {
                info("BattleSystem.battleEventHandler") { "onBattleEnds()" }

                stateMachine.to(State.END_OF_BATTLE, winnerSide = winnerSide)
            }

            override fun onDoingNothing(guardian: AGuardian)
            {
                info("BattleSystem.battleEventHandler") { "onDoingNothing()" }

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
                info("BattleSystem.battleEventHandler") { "onGuardianDefeated()" }

                val side = battleSystem.queue.getTeamSideFor(guardian)
                val pos  = battleSystem.queue.getFieldPositionFor(guardian)
                animationWidget.animateMonsterKO(pos, side)
            }


            override fun onAttack(attacker: AGuardian, target: AGuardian, ability: Ability, report: AttackCalculationReport)
            {
                info("BattleSystem.battleEventHandler") { "onAttack()" }

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
                info("BattleSystem.battleEventHandler") { "onAreaAttack()" }

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
                info("BattleSystem.battleEventHandler") { "onApplyStatusEffect()" }

                val nickName = Services.I18N().getGuardianNicknameIfAvailable(guardian)
                val statusEffect = guardian.stats.statusEffect.toLCString()
                val message  = Services.I18N().Battle("batt_info_status_effect_$statusEffect")
                infoLabelWidget.typeWrite("$nickName $message")
            }

            override fun onDefense(defensiveGuardian: AGuardian)
            {
                info("BattleSystem.battleEventHandler") { "onDefense()" }

                stateMachine.to(State.ANIMATION)
                infoLabelWidget.typeWrite(BattleMessages.selfDefense(defensiveGuardian))
                animationWidget.animateSelfDefense()
            }

            override fun onGuardianSubstituted(substituted: AGuardian, substitute: AGuardian, fieldPos: Int)
            {
                info("BattleSystem.battleEventHandler") { "onGuardianSubstituted()" }

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
                info("BattleSystem.battleEventHandler") { "onReplacingDefeatedGuardian()" }

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

            info(TAG) { "onTargetMenuButton.invoke($buttonID)" }
            val target = targetMenuWidget.getMonsterOfIndex(buttonID)
            battleSystem.setChosenTarget(target)
            battleSystem.calculateAttack()
        }


        // ......................................................................................... target area menu
        onTargetAreaMenuButton = { buttonID ->

            info(TAG) { "onTargetAreaMenuButton.invoke($buttonID)" }
            battleSystem.setChosenArea(targetAreaMenuWidget.getCombatTeamOfIndex(buttonID))
            battleSystem.calculateAttack()
        }


        // ......................................................................................... back to action menu
        onBackToActionMenu = {

            info(TAG) { "onBackToActionMenu" }
            stateMachine.to(State.ABILITY_MENU)
        }

        // ......................................................................................... ban success / fail
        onBanSuccessBackButton = {

            info(TAG) { "onBanSuccessBackButton"}
            battleSystem.finishBattleByBanning()
        }

        onBanFailureBackButton = {

            info(TAG) { "onBanFailureBackButton" }
            battleSystem.nextGuardian()
            battleSystem.continueBattle()
        }


        // ......................................................................................... escape success / fail
        onEscapeSuccessLabelBackButton = {

            info(TAG) { "onEscapeSuccessLabelBackButton" }
            goToPreviousScreen()
        }

        onEscapeFailedLabelBackButton = {

            info(TAG) { "onEscapeFailedLabelBackButton" }

            // Allow running at the beginning, without losing a turn
            if(stateMachine.state != State.BATTLE_START) { battleSystem.nextGuardian() }
            battleSystem.continueBattle()
        }


        // ......................................................................................... battle animation
        onBattleAnimationHitComplete = {

            info(TAG) { "onBattleAnimationHitComplete" }
            val defeated = battleSystem.applyAttack()
            if(!defeated || battleSystem.queue.right.allKO || battleSystem.queue.left.allKO)
            {
                actionMenu.enable(actionMenu.backButton)
            }
        }

        onBattleAnimationDying = {

            info(TAG) { "onBattleAnimationDying" }
            actionMenu.enable(actionMenu.backButton)
        }

        onBattleAnimationDoNothing = {

            info(TAG) { "onBattleAnimationDoNothing" }
            actionMenu.enable(actionMenu.backButton)
        }
    }


    companion object
    {
        private const val TAG: String = "BattleHUD"
    }
}
