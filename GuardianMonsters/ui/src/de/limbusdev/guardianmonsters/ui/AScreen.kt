package de.limbusdev.guardianmonsters.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport

import de.limbusdev.guardianmonsters.services.Services

/**
 * AScreen is an abstract class that works as the base skeleton for the screen managing framework.
 * Any screen in the game should inherit from this class.
 *
 * @author Georg Eckert 2017
 */

abstract class AScreen(var hud: AHUD) : Screen
{
    // .................................................................................. Properties
    // Renderers and Cameras
    private val camera      : OrthographicCamera = OrthographicCamera()
    private val viewport    : Viewport
    private var background  : TextureRegion = TextureRegion() // initialize with dummy
    private var batch       : SpriteBatch
    private var shpRend     : ShapeRenderer


    // ................................................................................ Constructors
    init
    {
        viewport = FitViewport(Constant.WIDTHf, Constant.HEIGHTf, camera)
        batch    = SpriteBatch()
        shpRend  = ShapeRenderer()
    }


    // ..................................................................................... Methods
    fun setBackground(index: Int)
    {
        background = Services.Media().getBackgroundTexture(index)
    }

    private fun clearScreen()
    {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }

    private fun drawBackground()
    {
        batch.begin()
        batch.draw(background, 0f, 0f, Constant.WIDTHf, Constant.HEIGHTf)
        batch.end()
    }


    // ............................................................................. libGDX's Screen
    override fun show()
    {
        hud.show()                              // enable this screen's HUD
        batch = SpriteBatch()                   // initialize SpriteBatch
        shpRend = ShapeRenderer()               // initialize ShapeRenderer
        Gdx.input.inputProcessor = hud.stage    // set this screen's HUD as input processor
    }

    override fun render(delta: Float)
    {
        clearScreen()
        drawBackground()

        hud.update(delta)

        viewport.apply()
        batch.projectionMatrix   = camera.combined
        shpRend.projectionMatrix = camera.combined
        camera.update()

        hud.stage.viewport.apply()
        hud.draw()
    }

    override fun resize(width: Int, height: Int)
    {
        viewport.update(width, height, true)
        hud.resize(width, height)
    }

    override fun pause() {}

    override fun resume() {}

    override fun hide()
    {
        hud.hide()
    }

    override fun dispose()
    {
        batch.dispose()
        shpRend.dispose()
    }
}
