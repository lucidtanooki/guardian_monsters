package de.limbusdev.guardianmonsters.fwmengine.world.ui

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.EntityComponentSystem
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.GameArea
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager
import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.CoreSL
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.World


/**
 * WorldScreen is the main class for all tiled map worlds.
 *
 * @author Georg Eckert 2016
 */
class WorldScreen(mapID: Int, startPosID: Int, fromSave: Boolean) : Screen
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    private lateinit var viewport   : Viewport
    private lateinit var batch      : SpriteBatch
    private lateinit var font       : BitmapFont

    private val gameAreaComponent : GameArea

    private val inputMultiplexer    : InputMultiplexer


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        CoreSL.provide(World())

        setUpRendering()
        val gameArea = LimbusGameObject("GameArea")
        gameAreaComponent = GameArea(mapID, startPosID)
        gameArea.add(gameAreaComponent)
        CoreSL.world.add(gameArea)
        val saveGameManager = SaveGameManager(gameAreaComponent)
        CoreSL.provide(EntityComponentSystem(viewport, gameAreaComponent, fromSave, this, saveGameManager))

        inputMultiplexer = InputMultiplexer()
        setUpInputProcessor()

        CoreSL.world.start()
    }


    // --------------------------------------------------------------------------------------------- METHODS
    // ...................................................................................... libGDX
    /** Called when this screen becomes the current screen for a [Game]. */
    override fun show()
    {
        batch = SpriteBatch()
        setUpInputProcessor()
        gameAreaComponent.playMusic()
        CoreSL.ecs.hud.show()
    }

    /** Called when the screen should render itself. */
    override fun render(delta: Float)
    {
        // Clear screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // process Updates
        updateCamera()

        // ............................................................................... RENDERING
        CoreSL.world.render()
        CoreSL.ecs.render()

        CoreSL.world.update(delta)
        CoreSL.ecs.update(delta)
        // ............................................................................... RENDERING
    }

    /**
     * @param width
     * @param height
     * @see ApplicationListener.resize
     */
    override fun resize(width: Int, height: Int)
    {
        viewport.update(width, height)
        CoreSL.ecs.hud.stage.viewport.update(width, height, true)
    }

    /** @see ApplicationListener.pause */
    override fun pause()
    {
        // TODO
    }

    /** @see ApplicationListener.resume */
    override fun resume() {}

    /** Called when this screen is no longer the current screen for a [Game]. */
    override fun hide()
    {
        CoreSL.ecs.hud.hide()
    }

    /** Called when this screen should release all resources. */
    override fun dispose()
    {
        batch.dispose()
        font.dispose()
    }


    // ..................................................................................... Helpers
    private fun setUpRendering()
    {
        CoreSL.world.mainCamera = OrthographicCamera()    // set up the camera and viewport
        val camera = CoreSL.world.mainCamera
        viewport = FitViewport(Constant.WIDTHf, Constant.HEIGHTf, camera)
        viewport.apply()
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f) // center camera

        batch = SpriteBatch()
        font = BitmapFont()
        font.color = Color.WHITE
    }

    private fun updateCamera()
    {
        // project to camera
        batch.projectionMatrix = CoreSL.world.mainCamera.combined
        CoreSL.world.mainCamera.update()
    }

    private fun setUpInputProcessor()
    {
        inputMultiplexer.addProcessor(CoreSL.ecs.hud.inputProcessor)
        inputMultiplexer.addProcessor(CoreSL.ecs.inputProcessor)
        Gdx.input.inputProcessor = inputMultiplexer
    }
}
