package de.limbusdev.guardianmonsters.battle

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.viewport.FitViewport

import de.limbusdev.guardianmonsters.assets.paths.AssetPath
import de.limbusdev.guardianmonsters.battle.ui.widgets.*
import de.limbusdev.guardianmonsters.battle.utils.BattleStringBuilder
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.abilities.Ability
import de.limbusdev.guardianmonsters.guardians.battle.AttackCalculationReport
import de.limbusdev.guardianmonsters.guardians.battle.BattleCalculator
import de.limbusdev.guardianmonsters.guardians.battle.BattleSystem
import de.limbusdev.guardianmonsters.guardians.items.ChakraCrystalItem
import de.limbusdev.guardianmonsters.guardians.items.Inventory
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.inventory.ui.widgets.items.ItemChoice
import de.limbusdev.guardianmonsters.services.Services

import de.limbusdev.guardianmonsters.guardians.Side


/**
 * BattleHUD manages all actions and UI elements in the [BattleScreen]
 *
 * For a readable code, the callback initialization has been separated to various initialization
 * methods. Fot this reason **lateinit** is used on all the properties. The developer has to make
 * sure, all objects are initialized, before calling show().
 *
 * @author Georg Eckert 2015
 */
class BattleHUD(private val inventory: Inventory) : ABattleHUD(Services.getUI().battleSkin)
{
    // .................................................................................. Properties
    // Logic
    private lateinit var battleSystem                   : BattleSystem

    private lateinit var battleAnimationStage           : Stage
    private lateinit var battleStateSwitcher            : BattleStateSwitcher

    // Groups
    private lateinit var mainMenu                       : BattleMainMenuWidget
    private lateinit var actionMenu                     : BattleActionMenuWidget
    private lateinit var abilityMenu                    : AbilityMenuWidget
    private lateinit var abilityInfoMenu                : AbilityMenuWidget
    private lateinit var animationWidget                : BattleAnimationWidget
    private lateinit var statusWidget                   : BattleStatusOverviewWidget
    private lateinit var battleQueueWidget              : BattleQueueWidget
    private lateinit var infoLabelWidget                : InfoLabelWidget
    private lateinit var targetMenuWidget               : TargetMenuWidget
    private lateinit var targetAreaMenuWidget           : TargetMenuWidget
    private lateinit var abilityDetailWidget            : AbilityInfoLabelWidget
    private lateinit var abilityDetailBackButton        : BattleActionMenuWidget
    private lateinit var abilityInfoMenuFrame           : BattleActionMenuWidget
    private lateinit var switchActiveGuardianWidget     : SwitchActiveGuardianWidget

    private lateinit var abilityMenuAddOn               : SevenButtonsWidget.CentralHalfButtonsAddOn

    // Callback Handlers
    private lateinit var onActionMenuBackButton         : () -> Unit
    private lateinit var onActionMenuBagButton          : () -> Unit
    private lateinit var onActionMenuTeamButton         : () -> Unit
    private lateinit var onActionMenuExtraButton        : () -> Unit
    private lateinit var onInfoLabelBackButton          : () -> Unit
    private lateinit var onBattleStartLabelBackButton   : () -> Unit
    private lateinit var onStatusEffectLabelBackButton  : () -> Unit
    private lateinit var onAttackDetailLabelBackButton  : () -> Unit
    private lateinit var onEndOfBattleLabelBackButton   : () -> Unit
    private lateinit var onBackToActionMenu             : () -> Unit
    private lateinit var onEscapeSuccessLabelBackButton : () -> Unit
    private lateinit var onEscapeFailedLabelBackButton  : () -> Unit
    private lateinit var onMainMenuSwordButton          : () -> Unit
    private lateinit var onMainMenuRunButton            : () -> Unit
    private lateinit var onTeamMenuBackButton           : () -> Unit
    private lateinit var onTeamMenuSwitchButton         : () -> Unit

    private lateinit var battleSystemCallbacks          : BattleSystem.Callbacks
    private lateinit var onBattleAnimationHitComplete   : () -> Unit
    private lateinit var onBattleAnimationDieing        : () -> Unit
    private lateinit var onBattleAnimationDoNothing     : () -> Unit

    private lateinit var onAttackMenuButton             : (Int) -> Unit
    private lateinit var onTargetMenuButton             : (Int) -> Unit
    private lateinit var onTargetAreaMenuButton         : (Int) -> Unit
    private lateinit var onAttackMenuAddOnButton        : (Int) -> Unit
    private lateinit var onAttackInfoMenuButton         : (Int) -> Unit

    private lateinit var leftTeam                       : Team
    private lateinit var rightTeam                      : Team


    // ................................................................................ Constructors
    init
    {
        setUpCallbacks()
        setUpUI()
    }


    // ..................................................................................... Methods
    // ..................................................................... game loop
    override fun update(delta: Float)
    {
        super.update(delta)
    }

    // .............................................................. initialize arena
    /** Resets the UI into a state where it can be initialized for a new battle */
    override fun reset()
    {
        super.reset()
        actionMenu.clearActions()
        mainMenu.clearActions()
    }

    /**
     * Initializes the battle screen with the given teams
     * @param heroTeam
     * @param opponentTeam
     */
    fun initialize(heroTeam: Team, opponentTeam: Team, wildEncounter: Boolean = true)
    {
        reset()

        // Keep monster teams
        this.leftTeam = heroTeam
        this.rightTeam = opponentTeam

        // initialize independent battle system
        battleSystem = BattleSystem(heroTeam, opponentTeam, battleSystemCallbacks, wildEncounter)
        battleSystem.queue.addObserver(battleQueueWidget)

        // initialize attack menu with active monster
        abilityMenu.initialize(battleSystem.activeMonster, true)

        statusWidget.init(battleSystem)
        animationWidget.initialize(battleSystem)
        targetMenuWidget.init(battleSystem)
        targetAreaMenuWidget.init(battleSystem, true)

        battleQueueWidget.updateQueue(battleSystem.queue)

        show()
    }


    // ............................................................................. libGDX's Screen
    override fun show()
    {
        super.show()
        battleStateSwitcher.toBattleStart()
    }

    // ........................................................................ setup
    /** Setting up HUD elements: */
    fun setUpUI()
    {
        // Second stage
        val viewport = FitViewport(640f, 360f)
        battleAnimationStage = Stage(viewport)
        addAdditionalStage(battleAnimationStage)

        // Widgets
        mainMenu        = BattleMainMenuWidget(skin, onMainMenuSwordButton, onMainMenuRunButton)
        statusWidget    = BattleStatusOverviewWidget(skin)

        animationWidget = BattleAnimationWidget(
                onBattleAnimationHitComplete,
                onBattleAnimationDieing,
                onBattleAnimationDoNothing)

        abilityMenuAddOn = SevenButtonsWidget.CentralHalfButtonsAddOn(skin, onAttackMenuAddOnButton)

        abilityMenu          = AbilityMenuWidget(skin) { ID -> onAttackMenuButton(ID)     }
        abilityInfoMenu      = AbilityMenuWidget(skin) { ID -> onAttackInfoMenuButton(ID) }
        targetMenuWidget     = TargetMenuWidget(skin)  { ID -> onTargetMenuButton(ID)     }
        targetAreaMenuWidget = TargetMenuWidget(skin)  { ID -> onTargetAreaMenuButton(ID) }

        abilityInfoMenuFrame    = BattleActionMenuWidget(skin)
        abilityDetailBackButton = BattleActionMenuWidget(skin, onBackButton = onAttackDetailLabelBackButton )
        actionMenu             = BattleActionMenuWidget(
                skin,
                onBackButton  = onActionMenuBackButton,
                onBagButton   = onActionMenuBagButton,
                onTeamButton  = onActionMenuTeamButton,
                onExtraButton = onActionMenuExtraButton)

        battleQueueWidget = BattleQueueWidget(skin, Align.bottomLeft)
        battleQueueWidget.setPosition(1f, 65f, Align.bottomLeft)

        infoLabelWidget     = InfoLabelWidget()
        abilityDetailWidget = AbilityInfoLabelWidget()

        abilityDetailBackButton.disableAllButBackButton()
        abilityInfoMenuFrame.disableAllChildButtons()

        switchActiveGuardianWidget = SwitchActiveGuardianWidget(skin, Services.getUI().inventorySkin)
        switchActiveGuardianWidget.setCallbacks(onTeamMenuBackButton, onTeamMenuSwitchButton)
    }


    // ................................................................................... Callbacks
    private fun setUpCallbacks()
    {
        battleStateSwitcher = BattleStateSwitcher()

        // ......................................................................................... main menu
        onMainMenuSwordButton = { battleSystem.continueBattle() }
        onMainMenuRunButton = {

            if(BattleCalculator.tryToRun(leftTeam, rightTeam))
            {
                battleStateSwitcher.toEscapeSuccessInfo()
            }
            else
            {
                battleStateSwitcher.toEscapeFailInfo()
            }
        }

        // ......................................................................................... team menu
        onTeamMenuBackButton    = { battleStateSwitcher.toAttackMenu() }
        onTeamMenuSwitchButton  = {

            battleStateSwitcher.toAnimation()
            val substituteNr = switchActiveGuardianWidget.chosenSubstitute
            val substitute = battleSystem.queue.left.get(substituteNr)
            battleSystem.replaceActiveMonster(substitute)
        }

        // ......................................................................................... battle start label
        onBattleStartLabelBackButton  = { battleStateSwitcher.toMainMenu() }

        // ......................................................................................... battle action menu
        onActionMenuBackButton        = { battleStateSwitcher.toMainMenu() }
        onActionMenuExtraButton       = { battleSystem.defend() }

        onActionMenuBagButton         = { stage.addActor(ItemChoice(Services.getUI().inventorySkin, inventory, leftTeam, battleSystem)) }

        onActionMenuTeamButton     = {

            switchActiveGuardianWidget.initialize(

                    battleSystem.activeMonster,
                    battleSystem.queue.left,
                    battleSystem.queue.combatTeamLeft
            )
            battleStateSwitcher.toTeamMenu()
        }

        // ......................................................................................... info label
        onInfoLabelBackButton = {

            val statusEffect = battleSystem
                    .activeMonster
                    .individualStatistics
                    .statusEffect

            if(statusEffect === IndividualStatistics.StatusEffect.HEALTHY)
            {
                battleSystem.nextMonster()
                battleSystem.continueBattle()
            }
            else
            {
                battleStateSwitcher.toStatusEffectInfoLabel()
                battleSystem.applyStatusEffect()
            }
        }

        // ......................................................................................... status effect label
        onStatusEffectLabelBackButton = {

            actionMenu.setCallbacks(onBackButton = onInfoLabelBackButton)
            battleSystem.nextMonster()
            battleSystem.continueBattle()
        }

        // ......................................................................................... end of battle
        onEndOfBattleLabelBackButton = {

            var teamOk = false
            for(m in leftTeam.values())
            {
                teamOk = teamOk || m.individualStatistics.isFit
            }
            if(!teamOk)
            {
                Services.getAudio().stopMusic()
                Services.getScreenManager().game.create()
            }
            else
            {
                Services.getAudio().stopMusic()
                val resultScreen = BattleResultScreen(leftTeam, battleSystem.result)
                Services.getScreenManager().pushScreen(resultScreen)
            }
        }

        // ......................................................................................... attack menu
        onAttackMenuButton = {

            val activeGuardian = battleSystem.activeMonster
            println("AttackMenuButtons: onButtonNr($it)")
            println("Input: User chose attack Nr. $it")
            val chosenAttackNr = activeGuardian
                    .abilityGraph
                    .activeAbilities
                    .indexOfValue(activeGuardian.abilityGraph.getActiveAbility(it), false)
            battleSystem.setChosenAttack(chosenAttackNr)

            val abilityID = activeGuardian.abilityGraph.getActiveAbility(it)
            val areaAttack = GuardiansServiceLocator.abilities.getAbility(abilityID).areaDamage

            if(areaAttack) {
                battleStateSwitcher.toTargetAreaChoice()
            }
            else {
                battleStateSwitcher.toTargetChoice()
            }
        }

        // ......................................................................................... attack info menu
        onAttackInfoMenuButton = {

            battleStateSwitcher.toAttackDetail(battleSystem
                    .activeMonster
                    .abilityGraph
                    .getActiveAbility(it))
        }

        // ......................................................................................... attack detail label
        onAttackDetailLabelBackButton = { battleStateSwitcher.toAttackInfoMenu() }

        // ......................................................................................... attack menu info switch
        onAttackMenuAddOnButton = object : (Int) -> Unit {

            private var checked = false

            override fun invoke(buttonID: Int)
            {
                checked = !checked
                when(buttonID)
                {
                    BattleHUDTextButton.CENTERTOP -> if(checked)
                    {
                        battleStateSwitcher.toAttackInfoMenu()
                    }
                    else
                    {
                        battleStateSwitcher.toAttackMenu()
                    }
                    else -> { }
                }
            }
        }

        // ......................................................................................... battle system
        battleSystemCallbacks = object : BattleSystem.Callbacks()
        {
            override fun onBanningWildGuardian(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int)
            {
                battleStateSwitcher.toAnimation()
                infoLabelWidget.setWholeText(BattleStringBuilder.tryingToBanGuardian(bannedGuardian, crystal))
                infoLabelWidget.animateTextAppearance()

                val callback = {

                    val success = BattleCalculator.banSucceeds(bannedGuardian, crystal)
                    if(success)
                    {
                        battleSystemCallbacks.onBanningWildGuardianSuccess(bannedGuardian, crystal, fieldPos)
                    }
                    else
                    {
                        battleSystemCallbacks.onBanningWildGuardianFailure(bannedGuardian, crystal, fieldPos)
                    }
                }
                animationWidget.animateBanning(fieldPos, Side.RIGHT, bannedGuardian, callback)
            }

            override fun onBanningWildGuardianFailure(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int)
            {
                val callback = { animationWidget.animateItemUsage() }
                battleStateSwitcher.toAnimation()
                infoLabelWidget.setWholeText(BattleStringBuilder.banGuardianFailure(bannedGuardian, crystal))
                infoLabelWidget.animateTextAppearance()
                animationWidget.animateBanningFailure(fieldPos, Side.RIGHT, bannedGuardian, callback)
            }

            override fun onBanningWildGuardianSuccess(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int)
            {
                battleStateSwitcher.toAnimation()
                infoLabelWidget.setWholeText(BattleStringBuilder.banGuardianSuccess(bannedGuardian, crystal))
                infoLabelWidget.animateTextAppearance()

                battleStateSwitcher.toEndOfBattleByBanningLastOpponent(bannedGuardian, crystal)
            }

            override fun onBattleEnds(winnerSide: Boolean)
            {
                battleStateSwitcher.toEndOfBattle(winnerSide)
            }

            override fun onDoingNothing(guardian: AGuardian)
            {
                val guardianName = Services.getL18N().getGuardianNicknameIfAvailable(guardian)
                battleStateSwitcher.toAnimation()
                val message: String
                if(guardian.individualStatistics.statusEffect === IndividualStatistics.StatusEffect.PETRIFIED)
                {
                    message = Services.getL18N().Battle().format(
                            "batt_message_failed",
                            guardianName,
                            Services.getL18N().Battle().get("batt_petrified"))
                }
                else
                {
                    message = Services.getL18N().Battle().format("batt_item_usage", guardianName)
                }
                infoLabelWidget.setWholeText(message)
                infoLabelWidget.animateTextAppearance()
                animationWidget.animateItemUsage()
            }

            override fun onPlayersTurn()
            {
                battleStateSwitcher.toActionMenu()
            }

            override fun onMonsterKilled(m: AGuardian)
            {
                val side = battleSystem.queue.getTeamSideFor(m)
                val pos = battleSystem.queue.getFieldPositionFor(m)
                animationWidget.animateMonsterKO(pos, side)
            }


            override fun onAttack(attacker: AGuardian, target: AGuardian, ability: Ability, rep: AttackCalculationReport)
            {
                // Change widget set
                battleStateSwitcher.toAnimation()
                infoLabelWidget.setWholeText(BattleStringBuilder.givenDamage(attacker, target, rep))
                infoLabelWidget.animateTextAppearance()


                if(rep.statusEffectPreventedAttack)
                {
                    return
                }
                else
                {
                    // Start ability animation
                    val attPos: Int
                    val defPos: Int

                    val activeSide = battleSystem.queue.getTeamSideFor(attacker)
                    val passiveSide = battleSystem.queue.getTeamSideFor(target)

                    attPos = battleSystem.queue.getFieldPositionFor(attacker)
                    defPos = battleSystem.queue.getFieldPositionFor(target)

                    animationWidget.animateAttack(attPos, defPos, activeSide, passiveSide, ability)
                }
            }

            override fun onAreaAttack(attacker: AGuardian, targets: ArrayMap<Int, AGuardian>,
                                      ability: Ability, reports: Array<AttackCalculationReport>)
            {
                // Change widget set
                battleStateSwitcher.toAnimation()
                infoLabelWidget.setWholeText(BattleStringBuilder.givenDamage(attacker, reports))
                infoLabelWidget.animateTextAppearance()

                if(reports.first().statusEffectPreventedAttack)
                {
                    return
                }
                else
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
                infoLabelWidget.setWholeText(
                        Services.getL18N().getGuardianNicknameIfAvailable(guardian) + " " +
                                Services.getL18N().Battle().get("batt_info_status_effect_" + guardian.individualStatistics.statusEffect.toString().toLowerCase()))
                infoLabelWidget.animateTextAppearance()
            }

            override fun onDefense(defensiveGuardian: AGuardian)
            {
                battleStateSwitcher.toAnimation()
                infoLabelWidget.setWholeText(BattleStringBuilder.selfDefense(defensiveGuardian))
                infoLabelWidget.animateTextAppearance()
                animationWidget.animateSelfDefense()
            }

            override fun onGuardianSubstituted(substituted: AGuardian, substitute: AGuardian, fieldPos: Int)
            {
                battleStateSwitcher.toAnimation()
                infoLabelWidget.setWholeText(BattleStringBuilder.substitution(substituted, substitute))
                infoLabelWidget.animateTextAppearance()
                animationWidget.animateGuardianSubstitution(
                        fieldPos,
                        battleSystem.queue.getTeamSideFor(substituted),
                        substitute.speciesID,
                        substitute.abilityGraph.currentForm,
                        { actionMenu.enable(actionMenu.backButton) },
                        substituted,
                        substitute
                )
                statusWidget.updateStatusWidgetToSubstitute(
                        fieldPos,
                        battleSystem.queue.getTeamSideFor(substituted),
                        substitute
                )
                targetMenuWidget.init(battleSystem, false)
                targetAreaMenuWidget.init(battleSystem, true)
            }

            override fun onReplacingDefeatedGuardian(substituted: AGuardian, substitute: AGuardian, fieldPos: Int)
            {
                battleStateSwitcher.toAnimation()
                infoLabelWidget.setWholeText(BattleStringBuilder.replacingDefeated(substituted, substitute))
                infoLabelWidget.animateTextAppearance()
                animationWidget.animateReplacingDefeatedGuardian(
                        fieldPos,
                        battleSystem.queue.getTeamSideFor(substituted),
                        substitute.speciesID,
                        substitute.abilityGraph.currentForm,
                        { actionMenu.enable(actionMenu.backButton) },
                        substituted,
                        substitute
                )
                statusWidget.updateStatusWidgetToSubstitute(
                        fieldPos,
                        battleSystem.queue.getTeamSideFor(substituted),
                        substitute
                )
                targetMenuWidget.init(battleSystem, false)
                targetAreaMenuWidget.init(battleSystem, true)
            }
        }

        // ......................................................................................... target menu
        onTargetMenuButton = {

            val target = targetMenuWidget.getMonsterOfIndex(it)
            battleSystem.setChosenTarget(target)
            battleSystem.attack()
        }

        // ......................................................................................... target area menu
        onTargetAreaMenuButton = {

            battleSystem.setChosenArea(targetAreaMenuWidget.getCombatTeamOfIndex(it))
            battleSystem.attack()
        }

        // ......................................................................................... back to action menu
        onBackToActionMenu = { battleStateSwitcher.toActionMenu() }

        // ......................................................................................... escape success / fail
        onEscapeSuccessLabelBackButton = { goToPreviousScreen() }
        onEscapeFailedLabelBackButton = { battleSystem.continueBattle() }

        // ......................................................................................... battle animation
        onBattleAnimationHitComplete = {

            val defeated = battleSystem.applyAttack()
            if(!defeated || battleSystem.queue.right.teamKO() || battleSystem.queue.left.teamKO()) {
                actionMenu.enable(actionMenu.backButton)
            }
        }

        onBattleAnimationDieing = { actionMenu.enable(actionMenu.backButton) }

        onBattleAnimationDoNothing = { actionMenu.enable(actionMenu.backButton) }

    }

    private fun showLevelUp(m: AGuardian)
    {
        stage.addActor(LevelUpWidget(Services.getUI().inventorySkin, m))
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ GETTERS & SETTERS

    // Inner Classes
    private inner class BattleStateSwitcher
    {
        var state: BattleState = BattleState.BATTLE_START

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
            infoLabelWidget.setWholeText(Services.getL18N().Battle().get("battle_start"))
            infoLabelWidget.animateTextAppearance()

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
            abilityMenu.initialize(battleSystem.activeMonster, true)

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

            abilityInfoMenu.initialize(battleSystem.activeMonster, false)
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
            val wholeText = Services.getL18N().Battle().get(textKey)
            infoLabelWidget.setWholeText(wholeText)
            infoLabelWidget.animateTextAppearance()
            actionMenu.setCallbacks(onBackButton = onEndOfBattleLabelBackButton)

            statusWidget.addToStage(stage)

            state = BattleState.END_OF_BATTLE

            val endOfBattleMusicSequence = Actions.sequence(

                    Services.getAudio().getMuteAudioAction(AssetPath.Audio.Music.VICTORY_SONG),
                    Actions.run { Services.getAudio().playMusic(AssetPath.Audio.Music.VICTORY_FANFARE)},
                    Actions.delay(5f),
                    Actions.run { Services.getAudio().playMusic(AssetPath.Audio.Music.VICTORY_SONG) }
            )


            stage.addAction(endOfBattleMusicSequence)
        }

        fun toEndOfBattleByBanningLastOpponent(bannedGuardian: AGuardian, crystal: ChakraCrystalItem)
        {
            reset()
            toInfoLabel()
            infoLabelWidget.setWholeText(BattleStringBuilder.banGuardianSuccess(bannedGuardian, crystal))

            infoLabelWidget.animateTextAppearance()
            actionMenu.setCallbacks(onBackButton = onEndOfBattleLabelBackButton)

            statusWidget.addToStage(stage)

            state = BattleState.END_OF_BATTLE

            val endOfBattleMusicSequence = Actions.sequence(

                    Services.getAudio().getMuteAudioAction(AssetPath.Audio.Music.VICTORY_SONG),
                    Actions.run { Services.getAudio().playMusic(AssetPath.Audio.Music.VICTORY_FANFARE)},
                    Actions.delay(5f),
                    Actions.run { Services.getAudio().playMusic(AssetPath.Audio.Music.VICTORY_SONG) }
            )

            stage.addAction(endOfBattleMusicSequence)

            // TODO put banned guardian into the guardo sphere
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
            val wholeText = Services.getL18N().Battle().get("escape_success")
            infoLabelWidget.setWholeText(wholeText)
            infoLabelWidget.animateTextAppearance()
            actionMenu.setCallbacks(onBackButton = onEscapeSuccessLabelBackButton)
        }

        fun toEscapeFailInfo()
        {
            toInfoLabel()
            val wholeText = Services.getL18N().Battle().get("escape_fail")
            infoLabelWidget.setWholeText(wholeText)
            infoLabelWidget.animateTextAppearance()
            actionMenu.setCallbacks(onBackButton = onEscapeFailedLabelBackButton)
        }

        fun reset()
        {
            // Remove all Widgets
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

    private enum class BattleState
    {
        MAIN_MENU,
        ACTION_MENU,
        ATTACK_MENU,
        END_OF_BATTLE,
        ANIMATION,
        TARGET_CHOICE,
        BATTLE_START,
        TARGET_AREA_CHOICE,
        ATTACK_INFO_MENU,
        ATTACK_DETAIL,
        TEAM_MENU
    }

}
