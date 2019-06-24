package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.scenes.scene2d.Stage
import de.limbusdev.guardianmonsters.battle.BattleHUD
import de.limbusdev.guardianmonsters.guardians.items.ChakraCrystalItem
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.services.Services
import ktx.log.info

/** Order matters. */
interface WidgetCollection
{
    fun enable() : BattleHUD.State
}

interface BanningWidgetCollection
{
    fun enable(bannedGuardian: AGuardian?, crystal: ChakraCrystalItem?, fieldPos: Int?) : BattleHUD.State
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

    val onBattleStartLabelBackButton    get() = hud.onBattleStartLabelBackButton
    val onEscapeSuccessLabelBackButton  get() = hud.onEscapeSuccessLabelBackButton
    val onEscapeFailedLabelBackButton   get() = hud.onEscapeFailedLabelBackButton
}

abstract class InfoLabelWidgetCollection(hud : BattleHUD) : AWidgetCollection(hud)
{
    open val state : BattleHUD.State = BattleHUD.State.STATUS_EFFECT_INFO

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

class EscapeSuccessInfoWidgetCollection(hud: BattleHUD) : InfoLabelWidgetCollection(hud)
{
    override val state : BattleHUD.State = BattleHUD.State.ESCAPE_SUCCESS

    override fun enable() : BattleHUD.State
    {
        super.enable()
        info() { "${"toEscapeSuccessInfo()".padEnd(40)} -> new State: $state" }

        infoLabelWidget.typeWrite(Services.I18N().Battle("escape_success"))
        actionMenu.setCallbacks(onBackButton = onEscapeSuccessLabelBackButton)

        return state
    }
}

class EscapeFailInfoWidgetCollection(hud: BattleHUD) : InfoLabelWidgetCollection(hud)
{
    override val state : BattleHUD.State = BattleHUD.State.ESCAPE_FAILURE

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
    val state : BattleHUD.State = BattleHUD.State.BATTLE_START

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
    val state: BattleHUD.State = BattleHUD.State.MAIN_MENU

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