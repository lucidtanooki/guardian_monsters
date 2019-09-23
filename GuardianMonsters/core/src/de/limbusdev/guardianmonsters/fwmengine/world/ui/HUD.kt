package de.limbusdev.guardianmonsters.fwmengine.world.ui

import com.badlogic.ashley.core.Engine
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
import de.limbusdev.guardianmonsters.CoreSL
import de.limbusdev.guardianmonsters.battle.BattleScreen
import de.limbusdev.guardianmonsters.enums.Compass4
import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.*
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.GameArea
import de.limbusdev.guardianmonsters.fwmengine.world.ui.widgets.ConversationWidget
import de.limbusdev.guardianmonsters.fwmengine.world.ui.widgets.DPad
import de.limbusdev.guardianmonsters.inventory.InventoryScreen
import de.limbusdev.guardianmonsters.services.Services
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
        val hero            : LimbusGameObject,
        var engine          : Engine,
        private val gameArea: GameArea
)
    : InputAdapter()
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    companion object{ const val TAG = "HUD" }

    val         stage                   = Stage(FitViewport(Constant.WIDTHf, Constant.HEIGHTf))
    private val conversationWidget      = ConversationWidget()
    private val dPad                    = DPad()
    private val blackCurtain            : Image
    private var mainMenuButton          : Button
    private var menuButtons             : VerticalGroup

    val         inputProcessor          : InputProcessor get() = stage

    private var dpadTouchDownStart      : Long = 0

    private var currentlyShownHUDWidget = HUDWidgets.NONE

    private var interactionGameObject   : LimbusGameObject? = null


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        stage.addActor(conversationWidget)

        mainMenuButton = GMWorldFactory.HUDBP.createHUDMenuButton({ onMainMenuButton() })
        menuButtons = setUpTopLevelButtons()
        stage.addActor(dPad)

        blackCurtain = GMWorldFactory.HUDBP.createBlackCurtainImg()
        stage.addActor(blackCurtain)

        stage.isDebugAll = Constant.DEBUGGING_ON
    }


    // --------------------------------------------------------------------------------------------- METHODS
    fun update(delta: Float)
    {
        stage.act(delta)
        val input = hero.get<InputComponent>() ?: return
        if(dPad.isTouched && input.direction.isStop())
        {
            if(TimeUtils.timeSinceMillis(dpadTouchDownStart) > 100)
            {
                input.direction = input.direction.nostop()
            }
        }
    }

    fun show()
    {
        blackCurtain.addAction(fadeOut(1f) then hideActor())
    }

    fun hide()
    {
        blackCurtain.addAction(showActor() then fadeIn(1f))
    }

    // ......................................................................... Constructor Helpers
    /** Creates main menu buttons and the dpad */
    private fun setUpTopLevelButtons() : VerticalGroup
    {
        // Group containing buttons: Save, Quit, Monsters
        val menuButtons = GMWorldFactory.HUDBP.createHUDMainMenu(

                saveButtonCB = { saveGameManager.saveGame() },
                quitButtonCB = { onQuitGameButton() },
                teamButtonCB = { onShowInventoryButton() }
        )

        val aButton = GMWorldFactory.HUDBP.createAButton {

            logDebug(TAG) { "A Button clicked." }
            when(currentlyShownHUDWidget)
            {
                HUDWidgets.CONVERSATION, HUDWidgets.SIGN -> proceedConversation()
                else -> interactWithProximity()
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

        return menuButtons
    }

    fun draw() = stage.draw()


    // --------------------------------------------------------------------------------------------- Interaction
    private fun interactWithProximity()
    {
        val interactiveObject = findAdjacentObject(hero, ConversationComponent::class.simpleName!!) ?: return
        val conversation = interactiveObject.get<ConversationComponent>() ?: return
        openConversation(conversation.text, conversation.name, gameArea.areaID)
        currentlyShownHUDWidget = HUDWidgets.SIGN
    }

    /** Finds a LimbusGameObject on the next cell in looking direction, if there is one. */
    private fun findAdjacentObject(hero: LimbusGameObject, signature: String): LimbusGameObject?
    {
        val dir = hero.get<InputComponent>()?.direction ?: return null

        val adjacentGridSlot = hero.transform.onGrid + IntVec2(dir.x, dir.y)

        logDebug(TAG) { "Grid cell to be checked: $adjacentGridSlot" }

        val interactiveObjects = CoreSL.world.getAllWith(signature)
        for(interactiveObject in interactiveObjects)
        {
            logDebug(TAG) { "Grid Cell of tested Entity: ${interactiveObject.transform.onGrid}" }

            if (adjacentGridSlot == interactiveObject.transform.onGrid) { return interactiveObject }
        }

        return null
    }

    private fun openConversation(text: String, name: String, mapID: Int)
    {
        // Hide Menus
        mainMenuButton.isVisible = false
        menuButtons.isVisible = false

        // Retrieve and set conversation content
        val conversationText = Services.I18N().i18nMap(mapID).get(text)
        val conversationTitle = Services.I18N().i18nMap(mapID).get(name)
        conversationWidget.setContent(conversationText, conversationTitle)

        // Show conversation
        conversationWidget.isVisible = true
        conversationWidget.addAction(moveTo(0f, 0f, .5f, Interpolation.exp10Out))
        currentlyShownHUDWidget = HUDWidgets.CONVERSATION

        // Stop player movement
        val heroInputComponent = hero.get<InputComponent>() ?: return
        heroInputComponent.talking = true

        // If other object has input component, stop it too
        val otherInputComponent = interactionGameObject?.get<InputComponent>() ?: return
        otherInputComponent.talking = true
        otherInputComponent.talkDirection = heroInputComponent.direction.invert()
        heroInputComponent.talkDirection = heroInputComponent.direction
    }

    private fun proceedConversation()
    {
        if(!conversationWidget.nextSection()) { closeConversation() }
    }

    private fun closeConversation()
    {
        mainMenuButton.isVisible = true
        conversationWidget.addAction(moveTo(0f, -50f, .5f, Interpolation.exp10In) then hideActor())
        hero.get<InputComponent>()!!.talking = false
        currentlyShownHUDWidget = HUDWidgets.NONE

        val otherInputComponent = interactionGameObject?.get<InputComponent>() ?: return
        otherInputComponent.talking = false
    }


    // ............................................................................. SET UP CONTROLS
    // --------------------------------------------------------------------------------------------- CALLBACKS
    private fun onQuitGameButton()
    {
        blackCurtain.addAction(alpha(0f) then showActor() then fadeIn(2f) then runThis{ Gdx.app.exit() })
    }

    private fun onShowInventoryButton()
    {
        val inventory = hero.get<InventoryComponent>()?.inventory ?: return
        val team      = hero.get<TeamComponent>()?.team ?: return
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


    // --------------------------------------------------------------------------------------------- InputAdapter
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
        dpadTouchDownStart = TimeUtils.millis()
        return touchDragged(screenX, screenY, pointer)
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean
    {
        val touchPos = stage.viewport.unproject(Vector2(screenX.f(), screenY.f()))
        val input = hero.get<InputComponent>()!!

        val (dPadValid, dPadDirection) = dPad.touchDown(touchPos)

        if(!dPadValid) { return false }

        val moving = hero.get<TileWiseMovementComponent>()?.moving ?: false
        input.direction = when(dPadDirection)
        {
            Compass4.N -> SkyDirection.NSTOP
            Compass4.E -> SkyDirection.ESTOP
            Compass4.S -> SkyDirection.SSTOP
            Compass4.W -> SkyDirection.WSTOP
        }

        if(TimeUtils.timeSinceMillis(dpadTouchDownStart) > 100)
        {
            input.direction = input.direction.nostop()
        }

        return dPadValid
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean
    {
        dPad.touchUp()
        val inputComponent = hero.get<InputComponent>() ?: return false
        inputComponent.direction = inputComponent.direction.stop()
        return true
    }
}
