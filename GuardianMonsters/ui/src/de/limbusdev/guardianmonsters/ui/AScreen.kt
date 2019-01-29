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
import de.limbusdev.utils.extensions.f

/**
 * AScreen
 *
 * @author Georg Eckert 2017
 */

abstract class AScreen(var hud: AHUD) : Screen
{
    // Renderers and Cameras
    private val camera:     OrthographicCamera = OrthographicCamera()
    private val viewport:   Viewport
    private var background: TextureRegion? = null
    private var batch:      SpriteBatch
    private var shpRend:    ShapeRenderer

    init
    {
        viewport = FitViewport(Constant.WIDTH.f(), Constant.HEIGHT.f(), camera)
        batch = SpriteBatch()
        shpRend = ShapeRenderer()
    }

    fun setBackground(index: Int)
    {
        this.background = Services.getMedia().getBackgroundTexture(index)
    }

    override fun show()
    {
        hud.show()
        this.batch = SpriteBatch()
        this.shpRend = ShapeRenderer()
        Gdx.input.inputProcessor = hud.stage
    }

    override fun render(delta: Float) {
        // Clear screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if(background != null)
        {
            batch.begin()
            batch.draw(background, 0f, 0f, Constant.WIDTH.toFloat(), Constant.HEIGHT.toFloat())
            batch.end()
        }

        hud.update(delta)

        viewport.apply()
        batch.projectionMatrix = camera.combined
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
        this.batch.dispose()
        this.shpRend.dispose()
    }
}
