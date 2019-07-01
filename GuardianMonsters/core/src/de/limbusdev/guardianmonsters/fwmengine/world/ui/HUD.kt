package de.limbusdev.guardianmonsters.fwmengine.world.ui

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
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
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.InputComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.entities.EntityFamilies
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.entities.HeroEntity
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.GameArea
import de.limbusdev.guardianmonsters.inventory.InventoryScreen
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.utils.extensions.f
import de.limbusdev.utils.geometry.IntVec2
import de.limbusdev.utils.logDebug
import ktx.actors.then
import ktx.actors.txt


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

    var stage: Stage

    private lateinit var conversationWidget: ConversationWidget

    private lateinit var mainMenuButton : Button
    private lateinit var menuButtons: VerticalGroup
    var blackCourtain: Image
    private var openHUDELement: HUDElements?

    private val dPad = DPad()


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        openHUDELement = HUDElements.NONE

        // Scene2D
        val fit = FitViewport(Constant.WIDTHf, Constant.HEIGHTf)
        stage = Stage(fit)
        val skin = Services.UI().defaultSkin

        conversationWidget = ConversationWidget()
        stage.addActor(conversationWidget)

        setUpTopLevelButtons()
        stage.addActor(dPad)

        blackCourtain = GMWorldFactory.HUDBP.createBlackCurtainImg()

        stage.addActor(blackCourtain)

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
        this.menuButtons = GMWorldFactory.HUDBP.createHUDMainMenu(

                saveButtonCB = { saveGameManager.saveGame() },
                quitButtonCB = { onQuitGameButton() },
                teamButtonCB = { onShowInventoryButton() }
        )


        // ................................................................................ CONTROLS
        val aButton = GMWorldFactory.HUDBP.createAButton {

            logDebug(TAG) { "A Button clicked." }
            touchEntity()
        }

        val bButton = GMWorldFactory.HUDBP.createBButton {

            logDebug(TAG) { "B Button clicked." }
            when(openHUDELement)
            {
                HUDElements.CONVERSATION -> closeConversation()
                HUDElements.SIGN         -> closeConversation()
                else                     -> {}
            }
        }

        this.menuButtons.isVisible = false

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
        Components.input.get(hero).firstTip = TimeUtils.millis()
        return touchDragged(screenX, screenY, pointer)
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean
    {
        val touchPos = stage.viewport.unproject(Vector2(screenX.f(), screenY.f()))
        val input = Components.input.get(hero)

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

    fun openConversation(text: String, name: String, mapID: Int)
    {
        mainMenuButton.isVisible = false
        menuButtons.isVisible = false

        val conversationText = Services.I18N().i18nMap(mapID).get(text)
        val conversationTitle = if (name.isNotEmpty()) { Services.I18N().i18nMap(mapID).get(name) } else { "" }
        conversationWidget.setContent(conversationText, conversationTitle)

        this.conversationWidget.isVisible = true
        conversationWidget.addAction(moveTo(0f, 0f, .5f, Interpolation.exp10Out))
    }

    fun closeConversation()
    {
        mainMenuButton.isVisible = true
        conversationWidget.addAction(moveTo(0f, -50f, .5f, Interpolation.exp10In) then hideActor())
        Components.getInputComponent(hero).talking = false
    }

    fun openSign(title: String, text: String, mapID: Int) = openConversation(text, title, mapID)

    fun show()
    {
        blackCourtain.addAction(fadeOut(1f) then hideActor())
    }

    fun hide()
    {
        blackCourtain.addAction(showActor() then fadeIn(1f))
    }

    fun checkForNearInteractiveObjects(hero: Entity): Entity?
    {
        val pos = Components.position.get(hero)
        val dir = Components.input.get(hero).skyDir

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


        for (e in engine.getEntitiesFor(Family.all(PositionComponent::class.java).get()))
        {
            // Only if interactive object is found and it's not the hero
            if (Components.position.get(e) != null && e !is HeroEntity)
            {
                val p = Components.position.get(e)

                logDebug(TAG) { "Grid Cell of tested Entity: ${p.onGrid}" }

                // Is there an entity?
                if (p.onGrid.x == checkGridCell.x && p.onGrid.y == checkGridCell.y){ nearEntity = e }
            }
        }

        return nearEntity
    }

    fun touchEntity()
    {
        val touchedEntity = checkForNearInteractiveObjects(hero)
        var touchedSpeaker = false
        var touchedSign = false

        // If there is an entity near enough
        if (touchedEntity != null)
        {
            // Living Entity
            if (EntityFamilies.living.matches(touchedEntity))
            {
                logDebug(TAG) { "Touched speaker" }
                touchedSpeaker = true
                Components.path.get(touchedEntity).talking = true
                Components.path.get(touchedEntity).talkDir = when(Components.input.get(hero).skyDir)
                {
                    SkyDirection.N -> SkyDirection.SSTOP
                    SkyDirection.S -> SkyDirection.NSTOP
                    SkyDirection.W -> SkyDirection.ESTOP
                    SkyDirection.E -> SkyDirection.WSTOP
                    else           -> SkyDirection.SSTOP
                }

                openConversation(

                        Components.conversation.get(touchedEntity).text,
                        Components.conversation.get(touchedEntity).name,
                        gameArea.getAreaID()
                )
                openHUDELement = HUDElements.CONVERSATION
            }

            // Sign Entity
            if (EntityFamilies.signs.matches(touchedEntity))
            {
                logDebug(TAG) { "Touched sign" }
                touchedSign = true
                openSign(

                        Components.title.get(touchedEntity).text,
                        Components.conversation.get(touchedEntity).text,
                        gameArea.getAreaID()
                )
                openHUDELement = HUDElements.SIGN
            }
        }
        if (touchedSpeaker || touchedSign) { Components.getInputComponent(hero).talking = true }
    }

    // ............................................................................. SET UP CONTROLS
    // --------------------------------------------------------------------------------------------- CALLBACKS
    private fun onQuitGameButton()
    {
        blackCourtain.addAction(alpha(0f) then showActor() then fadeIn(2f) then runThis{ Gdx.app.exit() })
    }

    private fun onShowInventoryButton()
    {
        val inventory = Components.inventory.get(hero).inventory
        val team = Components.team.get(hero).team
        Services.ScreenManager().pushScreen(InventoryScreen(team, inventory))
    }

    private fun onMainMenuButton()
    {
        // Menu Button not working in conversation
        if(conversationWidget.isVisible) { return }

        when(menuButtons.isVisible)
        {
            true  -> menuButtons.addAction(moveBy(120f, 0f, .5f, Interpolation.pow2In) then hideActor())
            false -> menuButtons.addAction(showActor() then moveBy(-120f, 0f, .5f, Interpolation.pow2In))
        }
    }
}
