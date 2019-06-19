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
 * [BattleState].
 *
 * @author Georg Eckert 2015
 */
class BattleHUD(private val inventory: Inventory) : ABattleHUD(Services.getUI().battleSkin)
{
    // .................................................................................. Properties
    // .................................................................. Logic
    private lateinit var battleSystem                   : BattleSystem

    private lateinit var leftTeam                       : Team
    private lateinit var rightTeam                      : Team

    private lateinit var battleAnimationStage           : Stage
    private lateinit var battleStateMachine             : BattleStateMachine


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

    private lateinit var battleSystemEventHandler       : BattleSystem.EventHandler
    private lateinit var onBattleAnimationHitComplete   : () -> Unit
    private lateinit var onBattleAnimationDieing        : () -> Unit
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
    fun initialize(heroTeam: Team, opponentTeam: Team, wildEncounter: Boolean = true)
    {
        reset()

        // keep Guardian teams
        this.leftTeam = heroTeam
        this.rightTeam = opponentTeam

        // initialize independent battle system
        battleSystem = BattleSystem(heroTeam, opponentTeam, battleSystemEventHandler, wildEncounter)
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

        show()
    }

    /** Resets the UI into a state where it can be initialized for a new battle */
    override fun reset()
    {
        super.reset()
        actionMenu.clearActions()
        mainMenu.clearActions()
    }


    // ............................................................................. libGDX's Screen
    override fun show()
    {
        super.show()
        battleStateMachine.toBattleStart()
    }


    // ..................................................................................... Methods



    // ............................................................................... Inner Classes
    /**
     * BattleStateMachine represents a state machine. It handles changing the states and activation
     * as well as deactivation of the necessary widgets.
     */
    private inner class BattleStateMachine
    {
        private var state: BattleState = BattleState.BATTLE_START

        fun toBattleStart()
        {
            reset()

            // Add Widgets
            animationWidget.addToStageAndFadeIn(battleAnimationStage)
            statusWidget.addToStageAndFadeIn(battleAnimationStage)
            infoLabelWidget.addToStageAndFadeIn(stage)
            actionMenu.addToStageAndFadeIn(stage)

            // Set Widget State
            actionMenu.disableAllButBackButton()

            // Set Callbacks
            actionMenu.setCallbacks(onBackButton = onBattleStartLabelBackButton)
            infoLabelWidget.typeWrite(Services.getL18N().Battle("battle_start"))

            state = BattleState.BATTLE_START
        }

        fun toActionMenu()
        {
            reset()


            // Add Widgets
            animationWidget.addToStage(battleAnimationStage)
            statusWidget.addToStage(battleAnimationStage)
            battleQueueWidget.addToStage(stage)
            actionMenu.addToStage(stage)
            abilityMenu.addToStage(stage)
            abilityMenuAddOn.addToStage(stage)

            // Setup Widgets
            actionMenu.setCallbacks(onActionMenuBackButton, onActionMenuBagButton, onActionMenuTeamButton, onActionMenuExtraButton)
            abilityMenu.initialize(battleSystem.activeGuardian, true)

            state = BattleState.ACTION_MENU
        }

        fun toAttackMenu()
        {
            toActionMenu()
            state = BattleState.ACTION_MENU
        }

        fun toAttackInfoMenu()
        {
            reset()

            animationWidget.addToStage(battleAnimationStage)
            statusWidget.addToStage(battleAnimationStage)
            battleQueueWidget.addToStage(stage)
            abilityInfoMenu.addToStage(stage)
            abilityMenuAddOn.addToStage(stage)
            abilityInfoMenuFrame.addToStage(stage)

            abilityInfoMenu.initialize(battleSystem.activeGuardian, false)
            abilityInfoMenu.toAttackInfoStyle()

            state = BattleState.ATTACK_INFO_MENU
        }

        fun toAttackDetail(aID: Ability.aID)
        {
            reset()

            animationWidget.addToStage(battleAnimationStage)
            statusWidget.addToStage(battleAnimationStage)
            battleQueueWidget.addToStage(stage)
            abilityMenuAddOn.addToStage(stage)
            abilityDetailWidget.addToStage(stage)
            abilityDetailBackButton.addToStage(stage)

            abilityDetailWidget.initialize(aID)

            state = BattleState.ATTACK_DETAIL
        }

        fun toEndOfBattle(winnerSide: Boolean)
        {
            reset()

            toInfoLabel()
            val textKey = if(winnerSide) "batt_you_won" else "batt_game_over"
            infoLabelWidget.typeWrite(Services.getL18N().Battle(textKey))
            actionMenu.setCallbacks(onBackButton = onEndOfBattleLabelBackButton)

            statusWidget.addToStage(stage)

            stage.addAction(Services.getAudio().createEndOfBattleMusicSequence())

            state = BattleState.END_OF_BATTLE
        }

        fun toEndOfBattleByBanningLastOpponent(bannedGuardian: AGuardian, crystal: ChakraCrystalItem)
        {
            reset()

            toInfoLabel()
            infoLabelWidget.typeWrite(BattleMessages.banGuardianSuccess(bannedGuardian, crystal))
            actionMenu.setCallbacks(onBackButton = onEndOfBattleLabelBackButton)

            statusWidget.addToStage(stage)

            stage.addAction(Services.getAudio().createEndOfBattleMusicSequence())

            // TODO put banned guardian into the guardo sphere

            state = BattleState.END_OF_BATTLE
        }

        fun toAnimation()
        {
            reset()
            toInfoLabel()
            actionMenu.disableAllChildButtons()
            battleQueueWidget.addToStage(stage)
            actionMenu.setCallbacks(onBackButton = onInfoLabelBackButton)

            state = BattleState.ANIMATION
        }

        fun toTargetChoice()
        {
            reset()
            animationWidget.addToStage(battleAnimationStage)
            statusWidget.addToStage(battleAnimationStage)
            actionMenu.disableAllButBackButton()
            actionMenu.addToStage(stage)
            actionMenu.setCallbacks(onBackButton = onBackToActionMenu)
            targetMenuWidget.addToStage(stage)
            battleQueueWidget.addToStage(stage)

            state = BattleState.TARGET_CHOICE
        }

        fun toTargetAreaChoice()
        {
            reset()
            animationWidget.addToStage(battleAnimationStage)
            statusWidget.addToStage(battleAnimationStage)
            actionMenu.disableAllButBackButton()
            actionMenu.addToStage(stage)
            actionMenu.setCallbacks(onBackButton = onBackToActionMenu)
            targetAreaMenuWidget.addToStage(stage)
            battleQueueWidget.addToStage(stage)

            state = BattleState.TARGET_AREA_CHOICE
        }

        fun toTeamMenu()
        {
            reset()
            switchActiveGuardianWidget.addToStage(stage)

            state = BattleState.TEAM_MENU
        }

        fun toMainMenu()
        {
            reset()
            animationWidget.addToStage(battleAnimationStage)
            statusWidget.addToStage(battleAnimationStage)
            actionMenu.disable()
            actionMenu.addToStage(stage)
            mainMenu.addToStageAndFadeIn(stage)

            state = BattleState.MAIN_MENU
        }

        fun toInfoLabel()
        {
            reset()

            animationWidget.addToStage(battleAnimationStage)
            statusWidget.addToStage(battleAnimationStage)
            infoLabelWidget.addToStage(stage)
            actionMenu.addToStage(stage)

            actionMenu.disableAllButBackButton()
        }

        fun toStatusEffectInfoLabel()
        {
            reset()
            toInfoLabel()
            battleQueueWidget.addToStage(stage)
            actionMenu.setCallbacks(onBackButton = onStatusEffectLabelBackButton)

            state = BattleState.ANIMATION
        }

        fun toEscapeSuccessInfo()
        {
            toInfoLabel()
            infoLabelWidget.typeWrite(Services.getL18N().Battle("escape_success"))
            actionMenu.setCallbacks(onBackButton = onEscapeSuccessLabelBackButton)
        }

        fun toEscapeFailInfo()
        {
            toInfoLabel()
            infoLabelWidget.typeWrite(Services.getL18N().Battle("escape_fail"))
            actionMenu.setCallbacks(onBackButton = onEscapeFailedLabelBackButton)
        }

        /** Removes all widgets and enables the action menu. */
        fun reset()
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



    /**
     * BattleState is used to store what state [BattleHUD] is currently in.
     */
    private enum class BattleState
    {
        MAIN_MENU,          // the menu widget with: Fight and Run buttons
        ACTION_MENU,        // the menu widget with: Team, Bag, Extra and Back buttons
        ATTACK_MENU,        // a 7 buttons menu widget, that allows choosing abilities
        END_OF_BATTLE,      // when the battle has come to an end
        ANIMATION,          // state of ability animation
        TARGET_CHOICE,      // a menu that allows choosing an ability target from one of the teams
        BATTLE_START,       // the first widget to be shown in a battle
        TARGET_AREA_CHOICE, // a menu that allows choosing a whole team as ability target
        ATTACK_INFO_MENU,   // a menu that shows the chosen Guardian's abilities and opens details
        ATTACK_DETAIL,      // a widget, that shows information about a chosen ability
        TEAM_MENU           // the menu that shows all Guardians of the hero's team
    }


    // ....................................................................................... Setup
    // ................................................................. Layout
    /** Setting up HUD elements */
    private fun initializeWidgets()
    {
        // Second stage
        val viewport = FitViewport(640f, 360f)
        battleAnimationStage = Stage(viewport)
        addAdditionalStage(battleAnimationStage)

        // Widgets
        mainMenu        = BattleMainMenuWidget(onMainMenuSwordButton, onMainMenuRunButton)
        statusWidget    = BattleStatusOverviewWidget()

        animationWidget = BattleAnimationWidget(

                onHitAnimationComplete  = onBattleAnimationHitComplete,
                onDieing                = onBattleAnimationDieing,
                onDoingNothing          = onBattleAnimationDoNothing
        )

        abilityMenuAddOn = SevenButtonsWidget.CentralHalfButtonsAddOn(onAbilityMenuInfoButton)

        abilityMenu          = AbilityMenuWidget() { ID -> onAbilityMenuButton(ID)     }
        abilityInfoMenu      = AbilityMenuWidget() { ID -> onAbilityInfoMenuButton(ID) }
        targetMenuWidget     = TargetMenuWidget()  { ID -> onTargetMenuButton(ID)     }
        targetAreaMenuWidget = TargetMenuWidget()  { ID -> onTargetAreaMenuButton(ID) }

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

        switchActiveGuardianWidget = SwitchActiveGuardianWidget(skin, Services.getUI().inventorySkin)
        switchActiveGuardianWidget.setCallbacks(onTeamMenuBackButton, onTeamMenuSwitchButton)
    }


    // .............................................................. Callbacks
    private fun setUpCallbacks()
    {
        battleStateMachine = BattleStateMachine()


        // ............................................................................... main menu
        onMainMenuSwordButton = { battleSystem.continueBattle() }
        onMainMenuRunButton = {

            if(BattleCalculator.runSucceeds(leftTeam, rightTeam))
            {
                battleStateMachine.toEscapeSuccessInfo()
            }
            else
            {
                battleStateMachine.toEscapeFailInfo()
            }
        }


        // ............................................................................... team menu
        onTeamMenuBackButton    = { battleStateMachine.toAttackMenu() }
        onTeamMenuSwitchButton  = {

            battleStateMachine.toAnimation()
            val substituteNr = switchActiveGuardianWidget.chosenSubstitute
            val substitute   = battleSystem.queue.left[substituteNr]
            battleSystem.replaceActiveMonsterWith(substitute)
        }


        // ...................................................................... battle start label
        onBattleStartLabelBackButton  = { battleStateMachine.toMainMenu() }


        // ...................................................................... battle action menu
        onActionMenuBackButton        = { battleStateMachine.toMainMenu() }
        onActionMenuExtraButton       = { battleSystem.defend() }

        onActionMenuBagButton         = { stage.addActor(ItemChoice(inventory, leftTeam, battleSystem)) }

        onActionMenuTeamButton = {

            switchActiveGuardianWidget.initialize(

                    battleSystem.activeGuardian,
                    battleSystem.queue.left,
                    battleSystem.queue.combatTeamLeft
            )
            battleStateMachine.toTeamMenu()
        }


        // .............................................................................. info label
        onInfoLabelBackButton = {

            if(battleSystem.activeGuardian.stats.statusEffect == StatusEffect.HEALTHY)
            {
                battleSystem.nextGuardian()
                battleSystem.continueBattle()
            }
            else
            {
                battleStateMachine.toStatusEffectInfoLabel()
                battleSystem.applyStatusEffect()
            }
        }


        // ..................................................................... status effect label
        onStatusEffectLabelBackButton = {

            // TODO replace with separate widget
            actionMenu.setCallbacks(onBackButton = onInfoLabelBackButton)
            battleSystem.nextGuardian()
            battleSystem.continueBattle()
        }


        // ........................................................................... end of battle
        onEndOfBattleLabelBackButton = {

            // Is any of the team's Guardians still fit?
            val teamOk = leftTeam.values().any { m -> m.stats.isFit }

            if(teamOk)
            {
                // Battle is won, show result screen
                Services.getAudio().stopMusic()
                val resultScreen = BattleResultScreen(leftTeam, battleSystem.result)
                Services.getScreenManager().pushScreen(resultScreen)
            }
            else
            {
                // Battle is lost, restart game
                Services.getAudio().stopMusic()
                Services.getScreenManager().game.create()
            }
        }


        // ............................................................................. attack menu
        onAbilityMenuButton = { buttonID ->

            val activeGuardian = battleSystem.activeGuardian
            println("AbilityMenuButtons: User has chosen ability $buttonID")

            battleSystem.setChosenAttack(buttonID)

            val abilityID         = activeGuardian.abilityGraph.getActiveAbility(buttonID)
            val appliesAreaDamage = GuardiansServiceLocator.abilities.getAbility(abilityID).areaDamage

            if(appliesAreaDamage) { battleStateMachine.toTargetAreaChoice() }
            else                  { battleStateMachine.toTargetChoice()     }
        }


        // ........................................................................ attack info menu
        onAbilityInfoMenuButton = { buttonID ->

            val abilityID = battleSystem.activeGuardian.abilityGraph.getActiveAbility(buttonID)
            battleStateMachine.toAttackDetail(abilityID)
        }


        // ..................................................................... attack detail label
        onAbilityDetailLabelBackButton = { battleStateMachine.toAttackInfoMenu() }


        // ................................................................. attack menu info switch
        onAbilityMenuInfoButton = object : (Int) -> Unit
        {
            private var checked = false

            override fun invoke(buttonID: Int)
            {
                checked = checked.toggle()
                if(buttonID == BattleHUDTextButton.CENTER_TOP)
                {
                    if(checked) { battleStateMachine.toAttackInfoMenu() }
                    else        { battleStateMachine.toAttackMenu() }
                }
            }
        }


        // ........................................................................... battle system
        battleSystemEventHandler = object : BattleSystem.EventHandler()
        {
            override fun onBanning(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int)
            {
                info("BattleSystem.EventHandler") { "onBanning()" }

                battleStateMachine.toAnimation()
                infoLabelWidget.typeWrite(BattleMessages.tryingToBan(bannedGuardian, crystal))

                animationWidget.animateBanning(fieldPos, Side.RIGHT, bannedGuardian) {

                    if(BattleCalculator.banSucceeds(bannedGuardian, crystal))
                    {
                        battleSystemEventHandler.onBanningSuccess(bannedGuardian, crystal, fieldPos)
                    }
                    else
                    {
                        battleSystemEventHandler.onBanningFailure(bannedGuardian, crystal, fieldPos)
                    }
                }
            }

            override fun onBanningFailure(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int)
            {
                info("BattleSystem.EventHandler") { "onBanningFailure()" }

                battleStateMachine.toAnimation()
                infoLabelWidget.typeWrite(BattleMessages.banGuardianFailure(bannedGuardian, crystal))
                animationWidget.animateBanningFailure(fieldPos, Side.RIGHT, bannedGuardian)
            }

            override fun onBanningSuccess(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int)
            {
                info("BattleSystem.EventHandler") { "onBanningSuccess()" }

                battleStateMachine.toAnimation()
                infoLabelWidget.typeWrite(BattleMessages.banGuardianSuccess(bannedGuardian, crystal))

                battleStateMachine.toEndOfBattleByBanningLastOpponent(bannedGuardian, crystal)
            }

            override fun onBattleEnds(winnerSide: Boolean)
            {
                info("BattleSystem.EventHandler") { "onBattleEnds()" }

                battleStateMachine.toEndOfBattle(winnerSide)
            }

            override fun onDoingNothing(guardian: AGuardian)
            {
                info("BattleSystem.EventHandler") { "onDoingNothing()" }

                val guardianName = Services.getL18N().getGuardianNicknameIfAvailable(guardian)
                battleStateMachine.toAnimation()

                val message = when(guardian.stats.statusEffect)
                {
                    StatusEffect.PETRIFIED ->
                    {
                        val petrified = Services.getL18N().Battle("batt_petrified")
                        Services.getL18N().Battle("batt_message_failed", guardianName, petrified)
                    }
                    else ->
                    {
                        Services.getL18N().Battle("batt_item_usage", guardianName)
                    }
                }

                infoLabelWidget.typeWrite(message)
                animationWidget.animateItemUsage()
            }

            override fun onPlayersTurn()
            {
                info("BattleSystem.EventHandler") { "onBattleEnds()" }

                battleStateMachine.toActionMenu()
            }

            override fun onGuardianDefeated(guardian: AGuardian)
            {
                info("BattleSystem.EventHandler") { "onGuardianDefeated()" }

                val side = battleSystem.queue.getTeamSideFor(guardian)
                val pos  = battleSystem.queue.getFieldPositionFor(guardian)
                animationWidget.animateMonsterKO(pos, side)
            }


            override fun onAttack(attacker: AGuardian, target: AGuardian, ability: Ability, report: AttackCalculationReport)
            {
                info("BattleSystem.EventHandler") { "onAttack()" }

                battleStateMachine.toAnimation()
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
                info("BattleSystem.EventHandler") { "onAreaAttack()" }

                battleStateMachine.toAnimation()
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
                info("BattleSystem.EventHandler") { "onApplyStatusEffect()" }

                val nickName = Services.getL18N().getGuardianNicknameIfAvailable(guardian)
                val statusEffect = guardian.stats.statusEffect.toLCString()
                val message  = Services.getL18N().Battle("batt_info_status_effect_$statusEffect")
                infoLabelWidget.typeWrite("$nickName $message")
            }

            override fun onDefense(defensiveGuardian: AGuardian)
            {
                info("BattleSystem.EventHandler") { "onDefense()" }

                battleStateMachine.toAnimation()
                infoLabelWidget.typeWrite(BattleMessages.selfDefense(defensiveGuardian))
                animationWidget.animateSelfDefense()
            }

            override fun onGuardianSubstituted(substituted: AGuardian, substitute: AGuardian, fieldPos: Int)
            {
                info("BattleSystem.EventHandler") { "onGuardianSubstituted()" }

                battleStateMachine.toAnimation()
                infoLabelWidget.typeWrite(BattleMessages.substitution(substituted, substitute))

                animationWidget.animateGuardianSubstitution(

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

            override fun onReplacingDefeatedGuardian(substituted: AGuardian, substitute: AGuardian, fieldPos: Int)
            {
                info("BattleSystem.EventHandler") { "onReplacingDefeatedGuardian()" }

                battleStateMachine.toAnimation()
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

            val target = targetMenuWidget.getMonsterOfIndex(buttonID)
            battleSystem.setChosenTarget(target)
            battleSystem.calculateAttack()
        }


        // ......................................................................................... target area menu
        onTargetAreaMenuButton = { buttonID ->

            battleSystem.setChosenArea(targetAreaMenuWidget.getCombatTeamOfIndex(buttonID))
            battleSystem.calculateAttack()
        }


        // ......................................................................................... back to action menu
        onBackToActionMenu = { battleStateMachine.toActionMenu() }


        // ......................................................................................... escape success / fail
        onEscapeSuccessLabelBackButton = { goToPreviousScreen() }
        onEscapeFailedLabelBackButton = { battleSystem.continueBattle() }


        // ......................................................................................... battle animation
        onBattleAnimationHitComplete = {

            val defeated = battleSystem.applyAttack()
            if(!defeated || battleSystem.queue.right.allKO || battleSystem.queue.left.allKO)
            {
                actionMenu.enable(actionMenu.backButton)
            }
        }

        onBattleAnimationDieing = { actionMenu.enable(actionMenu.backButton) }

        onBattleAnimationDoNothing = { actionMenu.enable(actionMenu.backButton) }
    }
}
