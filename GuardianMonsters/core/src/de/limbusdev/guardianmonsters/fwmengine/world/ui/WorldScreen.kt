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
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.GameArea
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager
import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.CoreServiceLocator
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.World


/**
 * WorldScreen is the main class for all tiled map worlds.
 *
 * @author Georg Eckert 2016
 */
class WorldScreen(mapID: Int, startPosID: Int, fromSave: Boolean) : Screen
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    // Renderers and Cameras
    lateinit var camera: OrthographicCamera

    private lateinit var viewport   : Viewport
    private lateinit var batch      : SpriteBatch
    private lateinit var shpRend    : ShapeRenderer
    private lateinit var font       : BitmapFont

    private val gameArea            : GameArea
    private val inputMultiplexer    : InputMultiplexer


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        CoreServiceLocator.provide(World())

        setUpRendering()
        gameArea = GameArea(mapID, startPosID)
        val saveGameManager = SaveGameManager(this.gameArea)
        CoreServiceLocator.provide(EntityComponentSystem(viewport, gameArea, fromSave, this, saveGameManager))

        inputMultiplexer = InputMultiplexer()
        setUpInputProcessor()

        CoreServiceLocator.world.start()
    }


    // --------------------------------------------------------------------------------------------- METHODS
    // ...................................................................................... libGDX
    /** Called when this screen becomes the current screen for a [Game]. */
    override fun show()
    {
        batch = SpriteBatch()
        setUpInputProcessor()
        gameArea.playMusic()
        CoreServiceLocator.ecs.hud.show()
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
        // Tiled Map
        gameArea.render(camera)
        CoreServiceLocator.ecs.render(batch, shpRend)
        if (Constant.DEBUGGING_ON) gameArea.renderDebugging(shpRend)

        CoreServiceLocator.ecs.draw()

        // ............................................................................... RENDERING

        CoreServiceLocator.ecs.update(delta)
    }

    /**
     * @param width
     * @param height
     * @see ApplicationListener.resize
     */
    override fun resize(width: Int, height: Int)
    {
        viewport.update(width, height)
        CoreServiceLocator.ecs.hud.stage.viewport.update(width, height, true)
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
        CoreServiceLocator.ecs.hud.hide()
    }

    /** Called when this screen should release all resources. */
    override fun dispose()
    {
        batch.dispose()
        font.dispose()
        gameArea.dispose()
    }


    // ..................................................................................... Helpers
    private fun setUpRendering()
    {
        camera = OrthographicCamera()    // set up the camera and viewport
        viewport = FitViewport(Constant.WIDTHf, Constant.HEIGHTf, camera)
        viewport.apply()
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f) // center camera

        batch = SpriteBatch()
        shpRend = ShapeRenderer()
        font = BitmapFont()
        font.color = Color.WHITE
    }

    private fun updateCamera()
    {
        // project to camera
        batch.projectionMatrix = camera.combined
        shpRend.projectionMatrix = camera.combined
        camera.update()
    }

    private fun setUpInputProcessor()
    {
        inputMultiplexer.addProcessor(CoreServiceLocator.ecs.hud.inputProcessor)
        inputMultiplexer.addProcessor(CoreServiceLocator.ecs.inputProcessor)
        Gdx.input.inputProcessor = inputMultiplexer
    }
}
