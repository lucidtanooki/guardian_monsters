package de.limbusdev.guardianmonsters.fwmengine.world.ui

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions.run as runThis
import com.badlogic.gdx.scenes.scene2d.actions.Actions.show as showActor
import com.badlogic.gdx.scenes.scene2d.actions.Actions.hide as hideActor
import com.badlogic.gdx.utils.TimeUtils
import com.badlogic.gdx.utils.viewport.FitViewport

import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.battle.BattleScreen
import de.limbusdev.guardianmonsters.enums.Compass4
import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.*
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.entities.EntityFamilies
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.entities.HeroEntity
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.GameArea
import de.limbusdev.guardianmonsters.fwmengine.world.ui.widgets.ConversationWidget
import de.limbusdev.guardianmonsters.fwmengine.world.ui.widgets.DPad
import de.limbusdev.guardianmonsters.inventory.InventoryScreen
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.utils.getComponent
import de.limbusdev.utils.extensions.f
import de.limbusdev.utils.geometry.IntVec2
import de.limbusdev.utils.logDebug
import ktx.actors.then


/**
 * HUD creates a UI which is displayed on top of the level screen, when not in battle.
 * It includes the main menu (save, quit, monsters, ...), controls (dpad, A, B) and text display.
 *
 * @author Georg Eckert 2017
 */
class HUD
(
        val battleScreen    : BattleScreen,
        val saveGameManager : SaveGameManager,
        val hero            : Entity,
        var engine          : Engine,
        private val gameArea: GameArea
)
    : InputAdapter()
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    companion object{ const val TAG = "HUD" }

    val stage               : Stage

    private val conversationWidget  : ConversationWidget
    private val blackCurtain        : Image

    private lateinit var mainMenuButton : Button
    private lateinit var menuButtons    : VerticalGroup

    private var currentlyShownHUDWidget = HUDWidgets.NONE

    private val dPad = DPad()


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        // Scene2D
        val fit = FitViewport(Constant.WIDTHf, Constant.HEIGHTf)
        stage = Stage(fit)

        conversationWidget = ConversationWidget()
        stage.addActor(conversationWidget)

        setUpTopLevelButtons()
        stage.addActor(dPad)

        blackCurtain = GMWorldFactory.HUDBP.createBlackCurtainImg()

        stage.addActor(blackCurtain)

        this.stage.isDebugAll = Constant.DEBUGGING_ON
    }


    // --------------------------------------------------------------------------------------------- METHODS
    // ............................................................................. Input Processor
    val inputProcessor: InputProcessor get() = this.stage


    // ......................................................................... Constructor Helpers
    /** Creates main menu buttons and the dpad */
    private fun setUpTopLevelButtons()
    {
        // Menu Button
        mainMenuButton = GMWorldFactory.HUDBP.createHUDMenuButton({ onMainMenuButton() })

        // Group containing buttons: Save, Quit, Monsters
        menuButtons = GMWorldFactory.HUDBP.createHUDMainMenu(

                saveButtonCB = { saveGameManager.saveGame() },
                quitButtonCB = { onQuitGameButton() },
                teamButtonCB = { onShowInventoryButton() }
        )


        // ................................................................................ CONTROLS
        val aButton = GMWorldFactory.HUDBP.createAButton {

            logDebug(TAG) { "A Button clicked." }
            when(currentlyShownHUDWidget)
            {
                HUDWidgets.CONVERSATION, HUDWidgets.SIGN -> proceedConversation()
                else -> touchEntity()
            }
        }

        val bButton = GMWorldFactory.HUDBP.createBButton {

            logDebug(TAG) { "B Button clicked." }
            when(currentlyShownHUDWidget)
            {
                HUDWidgets.CONVERSATION -> closeConversation()
                HUDWidgets.SIGN         -> closeConversation()
                else                     -> {}
            }
        }

        menuButtons.isVisible = false

        stage.addActor(aButton)
        stage.addActor(bButton)
        stage.addActor(mainMenuButton)
        stage.addActor(menuButtons)
    }

    private fun walk(dir: SkyDirection, input: InputComponent): Boolean
    {
        when(input.moving)
        {
            true -> input.nextInput = dir
            false ->
            {
                input.startMoving = true
                input.skyDir = dir
                input.nextInput = dir
                input.touchDown = true
            }
        }

        return !input.moving
    }

    private fun stop(input: InputComponent)
    {
        logDebug(TAG) { "stop()" }
        input.touchDown = false
    }


    fun draw() = stage.draw()

    /**
     * Handles touch down events, especially for the digital steering pad
     * @param screenX
     * @param screenY
     * @param pointer
     * @param button
     * @return
     */
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean
    {
        hero.getComponent<InputComponent>()!!.firstTip = TimeUtils.millis()
        return touchDragged(screenX, screenY, pointer)
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean
    {
        val touchPos = stage.viewport.unproject(Vector2(screenX.f(), screenY.f()))
        val input = hero.getComponent<InputComponent>()!!

        val (dPadValid, dPadDirection) = dPad.touchDown(touchPos)

        if(dPadValid)
        {
            input.touchDown = true
            when(dPadDirection)
            {
                Compass4.N -> walk(SkyDirection.N, input)
                Compass4.E -> walk(SkyDirection.E, input)
                Compass4.S -> walk(SkyDirection.S, input)
                Compass4.W -> walk(SkyDirection.W, input)
            }

            if (!input.moving) { input.startMoving = true }
        }

        return dPadValid
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean
    {
        stop(Components.input.get(hero))
        dPad.touchUp()
        return true
    }

    fun update(delta: Float) = stage.act(delta)

    fun proceedConversation()
    {
        if(!conversationWidget.nextSection()) { closeConversation() }
    }

    fun openConversation(text: String, name: String, mapID: Int)
    {
        mainMenuButton.isVisible = false
        menuButtons.isVisible = false

        val conversationText = Services.I18N().i18nMap(mapID).get(text)
        val conversationTitle = if (name.isNotEmpty()) { Services.I18N().i18nMap(mapID).get(name) } else { "" }
        conversationWidget.setContent(conversationText, conversationTitle)

        conversationWidget.isVisible = true
        conversationWidget.addAction(moveTo(0f, 0f, .5f, Interpolation.exp10Out))
        currentlyShownHUDWidget = HUDWidgets.CONVERSATION
    }

    fun closeConversation()
    {
        mainMenuButton.isVisible = true
        conversationWidget.addAction(moveTo(0f, -50f, .5f, Interpolation.exp10In) then hideActor())
        hero.getComponent<InputComponent>()!!.talking = false
        currentlyShownHUDWidget = HUDWidgets.NONE
    }

    fun openSign(title: String, text: String, mapID: Int) = openConversation(text, title, mapID)

    fun show()
    {
        blackCurtain.addAction(fadeOut(1f) then hideActor())
    }

    fun hide()
    {
        blackCurtain.addAction(showActor() then fadeIn(1f))
    }

    fun checkForNearInteractiveObjects(hero: Entity): Entity?
    {
        val pos = hero.getComponent<TransformComponent>()!!
        val dir = hero.getComponent<InputComponent>()!!.skyDir

        var nearEntity: Entity? = null
        val checkGridCell = IntVec2(pos.onGrid.x, pos.onGrid.y)

        checkGridCell += when (dir)
        {
            SkyDirection.N -> IntVec2( 0, +1)
            SkyDirection.S -> IntVec2( 0, -1)
            SkyDirection.E -> IntVec2(+1,  0)
            SkyDirection.W -> IntVec2(-1,  0)
            else           -> IntVec2( 0,  0)
        }

        logDebug(TAG) { "Grid cell to be checked: $checkGridCell" }


        for (e in engine.getEntitiesFor(Family.all(TransformComponent::class.java).get()))
        {
            // Only if interactive object is found and it's not the hero
            val posComp = e.getComponent<TransformComponent>()
            if (posComp != null && e !is HeroEntity)
            {
                logDebug(TAG) { "Grid Cell of tested Entity: ${posComp.onGrid}" }

                // Is there an entity?
                if (posComp.onGrid == checkGridCell) { nearEntity = e }
            }
        }

        return nearEntity
    }

    fun touchEntity()
    {
        val touchedEntity = checkForNearInteractiveObjects(hero) ?: return

        var touchedSpeaker = false
        var touchedSign = false

        // If there is an entity near enough
        // Living Entity
        if (EntityFamilies.living.matches(touchedEntity))
        {
            logDebug(TAG) { "Touched speaker" }
            touchedSpeaker = true
            val pathComp = touchedEntity.getComponent<PathComponent>()!!
            pathComp.talking = true
            pathComp.talkDir = when(Components.input.get(hero).skyDir)
            {
                SkyDirection.N -> SkyDirection.SSTOP
                SkyDirection.S -> SkyDirection.NSTOP
                SkyDirection.W -> SkyDirection.ESTOP
                SkyDirection.E -> SkyDirection.WSTOP
                else           -> SkyDirection.SSTOP
            }

            val conversationComp = touchedEntity.getComponent<ConversationComponent>()!!
            openConversation(conversationComp.text, conversationComp.name, gameArea.areaID)

            currentlyShownHUDWidget = HUDWidgets.CONVERSATION
        }

        // Sign Entity
        if (EntityFamilies.signs.matches(touchedEntity))
        {
            logDebug(TAG) { "Touched sign" }
            touchedSign = true
            openSign(
                    touchedEntity.getComponent<TitleComponent>()!!.text,
                    touchedEntity.getComponent<ConversationComponent>()!!.text,
                    gameArea.areaID
            )
            currentlyShownHUDWidget = HUDWidgets.SIGN
        }

        if (touchedSpeaker || touchedSign) { hero.getComponent<InputComponent>()!!.talking = true }
    }

    // ............................................................................. SET UP CONTROLS
    // --------------------------------------------------------------------------------------------- CALLBACKS
    private fun onQuitGameButton()
    {
        blackCurtain.addAction(alpha(0f) then showActor() then fadeIn(2f) then runThis{ Gdx.app.exit() })
    }

    private fun onShowInventoryButton()
    {
        val inventory = hero.getComponent<InventoryComponent>()!!.inventory
        val team      = hero.getComponent<TeamComponent>()!!.team
        Services.ScreenManager().pushScreen(InventoryScreen(team, inventory))
    }

    private fun onMainMenuButton()
    {
        // Menu Button not working in conversation
        when(currentlyShownHUDWidget)
        {
            HUDWidgets.CONVERSATION, HUDWidgets.SIGN -> return
            else -> {}
        }

        when(menuButtons.isVisible)
        {
            true  -> menuButtons.addAction(moveBy(120f, 0f, .5f, Interpolation.pow2In) then hideActor())
            false -> menuButtons.addAction(showActor() then moveBy(-120f, 0f, .5f, Interpolation.pow2In))
        }
    }
}
