package de.limbusdev.guardianmonsters.ui

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.FitViewport

import de.limbusdev.guardianmonsters.services.Services

/**
 * Template HUD, does nothing but setting up a stage with a FitViewport
 *
 * @author Georg Eckert 2017
 */
abstract class AHUD(protected val skin: Skin)
{
    var stage: Stage
        protected set
    private val stages = Array<Stage>()

    init
    {
        val fit = FitViewport(Constant.WIDTH.toFloat(), Constant.HEIGHT.toFloat())
        stage = Stage(fit)
        stage.isDebugAll = Constant.DEBUGGING_ON
        stages.insert(0, stage)
    }

    fun draw()
    {
        for(s in stages)
        {
            s.draw()
        }
    }

    open fun update(delta: Float)
    {
        for(s in stages)
        {
            s.act(delta)
        }
    }

    protected abstract fun reset()

    abstract fun show()

    open fun hide()
    {
        for(s in stages) s.act(100f)
    }

    open fun goToPreviousScreen()
    {
        Services.getScreenManager().popScreen()
    }

    fun resize(width: Int, height: Int)
    {
        for(s in stages)
        {
            s.viewport.update(width, height)
        }
    }

    fun addAdditionalStage(stage: Stage)
    {
        stages.insert(0, stage)
    }
}
