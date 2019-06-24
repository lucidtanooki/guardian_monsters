package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.scenes.scene2d.Stage
import de.limbusdev.guardianmonsters.battle.BattleHUD
import de.limbusdev.guardianmonsters.battle.utils.BattleMessages
import de.limbusdev.guardianmonsters.guardians.Side
import de.limbusdev.guardianmonsters.guardians.items.ChakraCrystalItem
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.services.Services
import ktx.log.info

/** Order matters. */
interface WidgetCollection
{
    fun enable() : BattleHUD.State
    val state: BattleHUD.State
}

abstract class ABanningWidgetCollection(hud: BattleHUD) : AInfoLabelWidgetCollection(hud)
{
    abstract fun enable(bannedGuardian: AGuardian?, crystal: ChakraCrystalItem?, fieldPos: Int?) : BattleHUD.State
}

abstract class AWidgetCollection(private val hud : BattleHUD) : WidgetCollection
{
    val animationWidget      get() = hud.animationWidget
    val statusWidget         get() = hud.statusWidget
    val infoLabelWidget      get() = hud.infoLabelWidget
    val actionMenu           get() = hud.actionMenu
    val defaultStage         get() = hud.stage
    val battleAnimationStage get() = hud.battleAnimationStage
    val mainMenu             get() = hud.mainMenu
    val battleQueueWidget    get() = hud.battleQueueWidget
    val abilityMenu          get() = hud.abilityMenu
    val abilityMenuAddOn     get() = hud.abilityMenuAddOn
    val abilityInfoMenu      get() = hud.abilityInfoMenu
    val abilityInfoMenuFrame get() = hud.abilityInfoMenuFrame

    val onBattleStartLabelBackButton    get() = hud.onBattleStartLabelBackButton
    val onEscapeSuccessLabelBackButton  get() = hud.onEscapeSuccessLabelBackButton
    val onEscapeFailedLabelBackButton   get() = hud.onEscapeFailedLabelBackButton
    val onBanSuccessBackButton          get() = hud.onBanSuccessBackButton
    val onBanFailureBackButton          get() = hud.onBanFailureBackButton
    val onActionMenuBackButton          get() = hud.onActionMenuBackButton
    val onActionMenuBagButton           get() = hud.onActionMenuBagButton
    val onActionMenuTeamButton          get() = hud.onActionMenuTeamButton
    val onActionMenuExtraButton         get() = hud.onActionMenuExtraButton

    val guardoSphere get() = hud.guardoSphere
    val battleSystem get() = hud.battleSystem
}

abstract class AInfoLabelWidgetCollection(hud : BattleHUD) : AWidgetCollection(hud)
{
    override val state = BattleHUD.State.STATUS_EFFECT_INFO

    override fun enable() : BattleHUD.State
    {
        info() { "showInfoLabel()" }

        animationWidget.addToStage(battleAnimationStage)
        statusWidget.addToStage(battleAnimationStage)
        infoLabelWidget.addToStage(defaultStage)
        actionMenu.addToStage(defaultStage)

        actionMenu.disableAllButBackButton()

        return state
    }
}

/** Helper class for displaying the action menu. */
abstract class AActionMenuWidgetCollection(hud: BattleHUD) : AWidgetCollection(hud)
{
    override val state = BattleHUD.State.ABILITY_MENU

    override fun enable() : BattleHUD.State
    {
        info() { "showActionMenu()" }

        // Add Widgets
        animationWidget.addToStage(battleAnimationStage)
        statusWidget.addToStage(battleAnimationStage)
        battleQueueWidget.addToStage(defaultStage)
        actionMenu.addToStage(defaultStage)
        abilityMenu.addToStage(defaultStage)
        abilityMenuAddOn.addToStage(defaultStage)

        // Setup Widgets
        actionMenu.setCallbacks(

                onBackButton  = onActionMenuBackButton,
                onBagButton   = onActionMenuBagButton,
                onTeamButton  = onActionMenuTeamButton,
                onExtraButton = onActionMenuExtraButton
        )
        abilityMenu.initialize(battleSystem.activeGuardian, true)

        return state
    }
}

class AbilityMenuWidgetCollection(hud: BattleHUD) : AActionMenuWidgetCollection(hud)
{
    override val state = BattleHUD.State.ABILITY_MENU

    override fun enable() : BattleHUD.State
    {
        super.enable()

        info() { "${"toAbilityMenu()".padEnd(40)} -> new State: $state" }

        return state
    }
}

class AbilityInfoMenuWidgetCollection(hud: BattleHUD) : AWidgetCollection(hud)
{
    override val state = BattleHUD.State.ABILITY_INFO_MENU

    override fun enable() : BattleHUD.State
    {
        info() { "${"toAbilityInfoMenu()".padEnd(40)} -> new State: $state" }

        animationWidget.addToStage(battleAnimationStage)
        statusWidget.addToStage(battleAnimationStage)
        battleQueueWidget.addToStage(defaultStage)
        abilityInfoMenu.addToStage(defaultStage)
        abilityMenuAddOn.addToStage(defaultStage)
        abilityInfoMenuFrame.addToStage(defaultStage)

        abilityInfoMenu.initialize(battleSystem.activeGuardian, false)
        abilityInfoMenu.toAttackInfoStyle()

        return state
    }
}

class EscapeSuccessInfoWidgetCollection(hud: BattleHUD) : AInfoLabelWidgetCollection(hud)
{
    override val state = BattleHUD.State.ESCAPE_SUCCESS

    override fun enable() : BattleHUD.State
    {
        super.enable()
        info() { "${"toEscapeSuccessInfo()".padEnd(40)} -> new State: $state" }

        infoLabelWidget.typeWrite(Services.I18N().Battle("escape_success"))
        actionMenu.setCallbacks(onBackButton = onEscapeSuccessLabelBackButton)

        return state
    }
}

class EscapeFailInfoWidgetCollection(hud: BattleHUD) : AInfoLabelWidgetCollection(hud)
{
    override val state = BattleHUD.State.ESCAPE_FAILURE

    override fun enable() : BattleHUD.State
    {
        super.enable()
        info() { "${"toEscapeFailInfo()".padEnd(40)} -> new State: $state" }

        infoLabelWidget.typeWrite(Services.I18N().Battle("escape_fail"))
        actionMenu.setCallbacks(onBackButton = onEscapeFailedLabelBackButton)

        return state
    }
}

class BattleStartWidgetCollection(hud : BattleHUD) : AWidgetCollection(hud)
{
    override val state = BattleHUD.State.BATTLE_START

    override fun enable() : BattleHUD.State
    {
        info() { "${"toBattleStart()".padEnd(40)} -> new State: $state" }

        // Add Widgets
        animationWidget.addToStageAndFadeIn(battleAnimationStage)
        statusWidget.addToStageAndFadeIn(battleAnimationStage)
        infoLabelWidget.addToStageAndFadeIn(defaultStage)
        actionMenu.addToStageAndFadeIn(defaultStage)

        // Set Widget State
        actionMenu.disableAllButBackButton()

        // Set Callbacks
        actionMenu.setCallbacks(onBackButton = onBattleStartLabelBackButton)
        infoLabelWidget.typeWrite(Services.I18N().Battle("battle_start"))

        return state
    }
}

class MainMenuWidgetCollection(hud : BattleHUD) : AWidgetCollection(hud)
{
    override val state = BattleHUD.State.MAIN_MENU

    override fun enable(): BattleHUD.State
    {
        info() { "${"toMainMenu()".padEnd(40)} -> new State: ${BattleHUD.State.MAIN_MENU}" }

        animationWidget.addToStage(battleAnimationStage)
        statusWidget.addToStage(battleAnimationStage)
        actionMenu.disable()
        actionMenu.addToStage(defaultStage)
        mainMenu.addToStageAndFadeIn(defaultStage)

        return state
    }
}

class BanSuccessWidgetCollection(hud: BattleHUD) : ABanningWidgetCollection(hud)
{
    override val state = BattleHUD.State.BAN_SUCCESS

    override fun enable() = state

    override fun enable(bannedGuardian: AGuardian?, crystal: ChakraCrystalItem?, fieldPos: Int?): BattleHUD.State
    {
        super.enable()

        checkNotNull(bannedGuardian)
        checkNotNull(crystal)
        checkNotNull(fieldPos)
        info() { "${"toBanningSuccess()".padEnd(40)} -> new State: $state" }

        // Display info label and disable all buttons
        actionMenu.disableAllChildButtons()

        // Set, what the back button will do and write the ban success message
        actionMenu.setCallbacks(onBackButton = onBanSuccessBackButton)
        infoLabelWidget.typeWrite(BattleMessages.banGuardianSuccess(bannedGuardian, crystal))

        // Animate banning success and re-enable back button after animation
        animationWidget.animateBanning(fieldPos, Side.RIGHT, bannedGuardian)
        { actionMenu.enable(actionMenu.backButton) }

        check(!guardoSphere.isFull()) { "If GuardoSphere is full, banning should be impossible." }
        guardoSphere += bannedGuardian

        return state
    }
}

class BanFailureWidgetCollection(hud: BattleHUD) : ABanningWidgetCollection(hud)
{
    override val state = BattleHUD.State.BAN_FAILURE

    override fun enable() = state

    override fun enable(bannedGuardian: AGuardian?, crystal: ChakraCrystalItem?, fieldPos: Int?): BattleHUD.State
    {
        super.enable()

        checkNotNull(bannedGuardian)
        checkNotNull(crystal)
        checkNotNull(fieldPos)
        info() { "${"toBanningFailure()".padEnd(40)} -> new State: $state" }

        // Display info label and disable all buttons
        actionMenu.disableAllChildButtons()

        // Set the back button to continue with the next Guardian, if ban fails
        actionMenu.setCallbacks(onBackButton = onBanFailureBackButton)
        infoLabelWidget.typeWrite(BattleMessages.banGuardianFailure(bannedGuardian, crystal))

        // Animate banning failure and re-enable back button after animation
        animationWidget.animateBanningFailure(fieldPos, Side.RIGHT, bannedGuardian)
        { actionMenu.enable(actionMenu.backButton) }

        return state
    }
}