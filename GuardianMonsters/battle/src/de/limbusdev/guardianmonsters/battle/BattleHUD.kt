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
 * @author Georg Eckert 2015
 */
class BattleHUD(private val inventory: Inventory) : ABattleHUD(Services.getUI().battleSkin)
{
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ ATTRIBUTES

    // Logic
    private lateinit var battleSystem: BattleSystem

    private lateinit var battleAnimationStage   : Stage
    private lateinit var battleStateSwitcher    : BattleStateSwitcher

    // Groups
    private lateinit var mainMenu                   : BattleMainMenuWidget
    private lateinit var actionMenu                 : BattleActionMenuWidget
    private lateinit var attackMenu                 : AttackMenuWidget
    private lateinit var attackInfoMenu             : AttackMenuWidget
    private lateinit var animationWidget            : BattleAnimationWidget
    private lateinit var statusWidget               : BattleStatusOverviewWidget
    private lateinit var battleQueueWidget          : BattleQueueWidget
    private lateinit var infoLabelWidget            : InfoLabelWidget
    private lateinit var targetMenuWidget           : TargetMenuWidget
    private lateinit var targetAreaMenuWidget       : TargetMenuWidget
    private lateinit var attackDetailWidget         : AbilityInfoLabelWidget
    private lateinit var attackDetailBackButton     : BattleActionMenuWidget
    private lateinit var attackInfoMenuFrame        : BattleActionMenuWidget
    private lateinit var switchActiveGuardianWidget : SwitchActiveGuardianWidget

    private lateinit var attackMenuAddOn            : SevenButtonsWidget.CentralHalfButtonsAddOn

    // CallbackHandlers
    private lateinit var actionMenuBackCB           : () -> Unit
    private lateinit var actionMenuBagCB            : () -> Unit
    private lateinit var actionMenuMonsterCB        : () -> Unit
    private lateinit var actionMenuExtraCB          : () -> Unit
    private lateinit var infoLabelBackCB            : () -> Unit
    private lateinit var battleStartLabelBackCB     : () -> Unit
    private lateinit var statusEffectLabelBackCB    : () -> Unit
    private lateinit var attackDetailLabelBackCB    : () -> Unit
    private lateinit var endOfBattleLabelBackCB     : () -> Unit
    private lateinit var backToActionMenuCB         : () -> Unit
    private lateinit var escapeSuccessLabelBackCB   : () -> Unit
    private lateinit var escapeFailedLabelBackCB    : () -> Unit
    private lateinit var mainMenuOnSwordButton      : () -> Unit
    private lateinit var mainMenuOnRunButton        : () -> Unit
    private lateinit var teamMenuOnBackButton       : () -> Unit
    private lateinit var teamMenuOnSwitchButton     : () -> Unit

    private lateinit var battleSystemCallbacks                  : BattleSystem.Callbacks
    private lateinit var battleAnimationOnHitCompleteCallback   : () -> Unit
    private lateinit var battleAnimationOnDieingCallback        : () -> Unit
    private lateinit var battleAnimationOnDoingNothingCallback  : () -> Unit

    private lateinit var attackMenuCallbacks        : (Int) -> Unit
    private lateinit var targetMenuCallbacks        : (Int) -> Unit
    private lateinit var targetAreaMenuCallbacks    : (Int) -> Unit
    private lateinit var attackMenuAddOnCallbacks   : (Int) -> Unit
    private lateinit var attackInfoMenuCallbacks    : (Int) -> Unit

    private lateinit var leftTeam   : Team
    private lateinit var rightTeam  : Team


    init
    {
        setUpCallbacks()
        setUpUI()
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ METHODS
    // ..................................................................... game loop
    override fun update(delta: Float)
    {
        super.update(delta)
    }

    // .............................................................. initialize arena
    /**
     * Resets the UI into a state where it can be initialized for a new battle
     */
    override fun reset()
    {
        super.reset()
        actionMenu.clearActions()
        mainMenu.clearActions()
    }

    fun init(heroTeam: Team, opponentTeam: Team)
    {
        this.init(heroTeam, opponentTeam, true)
    }

    /**
     * Initializes the battle screen with the given teams
     * @param heroTeam
     * @param opponentTeam
     */
    fun init(heroTeam: Team, opponentTeam: Team, wildEncounter: Boolean)
    {
        reset()

        // Keep monster teams
        this.leftTeam = heroTeam
        this.rightTeam = opponentTeam

        // initialize independent battle system
        battleSystem = BattleSystem(heroTeam, opponentTeam, battleSystemCallbacks, wildEncounter)
        battleSystem.queue.addObserver(battleQueueWidget)

        // initialize attack menu with active monster
        attackMenu.init(battleSystem.activeMonster, true)

        statusWidget.init(battleSystem)
        animationWidget.initialize(battleSystem)
        targetMenuWidget.init(battleSystem)
        targetAreaMenuWidget.init(battleSystem, true)

        battleQueueWidget.updateQueue(battleSystem.queue)

        show()
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // .............................................................................. LibGDX METHODS

    override fun show()
    {
        super.show()
        battleStateSwitcher.toBattleStart()
    }

    // ........................................................................ setup
    /**
     * Setting up HUD elements:
     */
    fun setUpUI()
    {
        // Second stage
        val viewport = FitViewport(640f, 360f)
        battleAnimationStage = Stage(viewport)
        addAdditionalStage(battleAnimationStage)

        // Widgets
        mainMenu        = BattleMainMenuWidget(skin, mainMenuOnSwordButton, mainMenuOnRunButton)
        statusWidget    = BattleStatusOverviewWidget(skin)
        animationWidget = BattleAnimationWidget(
                battleAnimationOnHitCompleteCallback,
                battleAnimationOnDieingCallback,
                battleAnimationOnDoingNothingCallback)
        attackMenuAddOn = SevenButtonsWidget.CentralHalfButtonsAddOn(skin, attackMenuAddOnCallbacks)

        attackMenu           = AttackMenuWidget(skin) { ID -> attackMenuCallbacks(ID)     }
        attackInfoMenu       = AttackMenuWidget(skin) { ID -> attackInfoMenuCallbacks(ID) }
        targetMenuWidget     = TargetMenuWidget(skin) { ID -> targetMenuCallbacks(ID)     }
        targetAreaMenuWidget = TargetMenuWidget(skin) { ID -> targetAreaMenuCallbacks(ID) }

        attackInfoMenuFrame    = BattleActionMenuWidget(skin)
        attackDetailBackButton = BattleActionMenuWidget(skin, backCB = attackDetailLabelBackCB )
        actionMenu             = BattleActionMenuWidget(
                skin,
                backCB    = actionMenuBackCB,
                bagCB     = actionMenuBagCB,
                monsterCB = actionMenuMonsterCB,
                extraCB   = actionMenuExtraCB)

        battleQueueWidget = BattleQueueWidget(skin, Align.bottomLeft)
        battleQueueWidget.setPosition(1f, 65f, Align.bottomLeft)

        infoLabelWidget    = InfoLabelWidget(skin)
        attackDetailWidget = AbilityInfoLabelWidget(skin, Services.getUI().inventorySkin)

        attackDetailBackButton.disableAllButBackButton()
        attackInfoMenuFrame.disableAllChildButtons()

        switchActiveGuardianWidget = SwitchActiveGuardianWidget(skin, Services.getUI().inventorySkin)
        switchActiveGuardianWidget.setCallbacks(teamMenuOnBackButton, teamMenuOnSwitchButton)
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ CALLBACKS

    private fun setUpCallbacks()
    {
        battleStateSwitcher = BattleStateSwitcher()

        // ......................................................................................... main menu
        mainMenuOnSwordButton = { battleSystem.continueBattle() }
        mainMenuOnRunButton = {

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
        teamMenuOnBackButton    = { battleStateSwitcher.toAttackMenu() }
        teamMenuOnSwitchButton  = {

            battleStateSwitcher.toAnimation()
            val substituteNr = switchActiveGuardianWidget.chosenSubstitute
            val substitute = battleSystem.queue.left.get(substituteNr)
            battleSystem.replaceActiveMonster(substitute)
        }

        // ......................................................................................... battle start label
        battleStartLabelBackCB  = { battleStateSwitcher.toMainMenu() }

        // ......................................................................................... battle action menu
        actionMenuBackCB        = { battleStateSwitcher.toMainMenu() }
        actionMenuExtraCB       = { battleSystem.defend() }

        actionMenuBagCB         = { stage.addActor(ItemChoice(Services.getUI().inventorySkin, inventory, leftTeam, battleSystem)) }

        actionMenuMonsterCB     = {

            switchActiveGuardianWidget.initialize(

                    battleSystem.activeMonster,
                    battleSystem.queue.left,
                    battleSystem.queue.combatTeamLeft
            )
            battleStateSwitcher.toTeamMenu()
        }

        // ......................................................................................... info label
        infoLabelBackCB = {

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
        statusEffectLabelBackCB = {

            actionMenu.setCallbacks(backCB = infoLabelBackCB)
            battleSystem.nextMonster()
            battleSystem.continueBattle()
        }

        // ......................................................................................... end of battle
        endOfBattleLabelBackCB = {

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
        attackMenuCallbacks = {

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
        attackInfoMenuCallbacks = {

            battleStateSwitcher.toAttackDetail(battleSystem
                    .activeMonster
                    .abilityGraph
                    .getActiveAbility(it))
        }

        // ......................................................................................... attack detail label
        attackDetailLabelBackCB = { battleStateSwitcher.toAttackInfoMenu() }

        // ......................................................................................... attack menu info switch
        attackMenuAddOnCallbacks = object : (Int) -> Unit {

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
            override fun onBanningWildGuardian(bannedGuardian: AGuardian, item: ChakraCrystalItem, fieldPos: Int)
            {
                battleStateSwitcher.toAnimation()
                infoLabelWidget.setWholeText(BattleStringBuilder.tryingToBanGuardian(bannedGuardian, item))
                infoLabelWidget.animateTextAppearance()

                val callback = {

                    val success = BattleCalculator.banSucceeds(bannedGuardian, item)
                    if(success)
                    {
                        battleSystemCallbacks.onBanningWildGuardianSuccess(bannedGuardian, item, fieldPos)
                    }
                    else
                    {
                        battleSystemCallbacks.onBanningWildGuardianFailure(bannedGuardian, item, fieldPos)
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
        targetMenuCallbacks = {

            val target = targetMenuWidget.getMonsterOfIndex(it)
            battleSystem.setChosenTarget(target)
            battleSystem.attack()
        }

        // ......................................................................................... target area menu
        targetAreaMenuCallbacks = {

            battleSystem.setChosenArea(targetAreaMenuWidget.getCombatTeamOfIndex(it))
            battleSystem.attack()
        }

        // ......................................................................................... back to action menu
        backToActionMenuCB = { battleStateSwitcher.toActionMenu() }

        // ......................................................................................... escape success / fail
        escapeSuccessLabelBackCB = { goToPreviousScreen() }
        escapeFailedLabelBackCB = { battleSystem.continueBattle() }

        // ......................................................................................... battle animation
        battleAnimationOnHitCompleteCallback = {

            val defeated = battleSystem.applyAttack()
            if(!defeated || battleSystem.queue.right.teamKO() || battleSystem.queue.left.teamKO()) {
                actionMenu.enable(actionMenu.backButton)
            }
        }

        battleAnimationOnDieingCallback = { actionMenu.enable(actionMenu.backButton) }

        battleAnimationOnDoingNothingCallback = { actionMenu.enable(actionMenu.backButton) }

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
            actionMenu.setCallbacks(backCB = battleStartLabelBackCB)
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
            attackMenu.addToStage(stage)
            attackMenuAddOn.addToStage(stage)

            // Setup Widgets
            actionMenu.setCallbacks(actionMenuBackCB, actionMenuBagCB, actionMenuMonsterCB, actionMenuExtraCB)
            attackMenu.init(battleSystem.activeMonster, true)

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
            attackInfoMenu.addToStage(stage)
            attackMenuAddOn.addToStage(stage)
            attackInfoMenuFrame.addToStage(stage)

            attackInfoMenu.init(battleSystem.activeMonster, false)
            attackInfoMenu.toAttackInfoStyle()

            state = BattleState.ATTACK_INFO_MENU
        }

        fun toAttackDetail(aID: Ability.aID)
        {
            reset()

            animationWidget.addToStage(battleAnimationStage)
            statusWidget.addToStage(battleAnimationStage)
            battleQueueWidget.addToStage(stage)
            attackMenuAddOn.addToStage(stage)
            attackDetailWidget.addToStage(stage)
            attackDetailBackButton.addToStage(stage)

            attackDetailWidget.init(aID)

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
            actionMenu.setCallbacks(backCB = endOfBattleLabelBackCB)

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
            actionMenu.setCallbacks(backCB = endOfBattleLabelBackCB)

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
            actionMenu.setCallbacks(backCB = infoLabelBackCB)

            state = BattleState.ANIMATION
        }

        fun toTargetChoice()
        {
            reset()
            animationWidget.addToStage(battleAnimationStage)
            statusWidget.addToStage(battleAnimationStage)
            actionMenu.disableAllButBackButton()
            actionMenu.addToStage(stage)
            actionMenu.setCallbacks(backCB = backToActionMenuCB)
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
            actionMenu.setCallbacks(backCB = backToActionMenuCB)
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
            actionMenu.setCallbacks(backCB = statusEffectLabelBackCB)

            state = BattleState.ANIMATION
        }

        fun toEscapeSuccessInfo()
        {
            toInfoLabel()
            val wholeText = Services.getL18N().Battle().get("escape_success")
            infoLabelWidget.setWholeText(wholeText)
            infoLabelWidget.animateTextAppearance()
            actionMenu.setCallbacks(backCB = escapeSuccessLabelBackCB)
        }

        fun toEscapeFailInfo()
        {
            toInfoLabel()
            val wholeText = Services.getL18N().Battle().get("escape_fail")
            infoLabelWidget.setWholeText(wholeText)
            infoLabelWidget.animateTextAppearance()
            actionMenu.setCallbacks(backCB = escapeFailedLabelBackCB)
        }

        fun reset()
        {
            // Remove all Widgets
            infoLabelWidget.remove()
            animationWidget.remove()
            mainMenu.remove()
            targetMenuWidget.remove()
            targetAreaMenuWidget.remove()
            attackMenu.remove()
            statusWidget.remove()
            actionMenu.remove()
            battleQueueWidget.remove()
            attackMenuAddOn.remove()
            attackDetailBackButton.remove()
            attackDetailWidget.remove()
            attackInfoMenuFrame.remove()
            attackInfoMenu.remove()
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
