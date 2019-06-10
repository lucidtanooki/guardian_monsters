package de.limbusdev.guardianmonsters.ui

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.Game

import de.limbusdev.guardianmonsters.services.Services

/**
 * Template HUD, does nothing but setting up a stage with a FitViewport
 *
 * @author Georg Eckert 2017
 */
abstract class AHUD(protected val skin: Skin)
{
    // .................................................................................. Properties
    var stage: Stage protected set
    private val stages = Array<Stage>()


    // ................................................................................ Constructors
    init
    {
        val fit = FitViewport(Constant.WIDTHf, Constant.HEIGHTf)
        stage = Stage(fit)
        stages.insert(0, stage)

        stage.isDebugAll = Constant.DEBUGGING_ON
    }


    // ..................................................................................... Methods
    open fun goToPreviousScreen()
    {
        Services.getScreenManager().popScreen()
    }

    fun addAdditionalStage(stage: Stage)
    {
        stages.insert(0, stage)
    }


    // ............................................................................. libGDX's Screen
    /** Called when the parent screen becomes the current screen for a [Game]. */
    abstract fun show()

    protected abstract fun reset()

    /** Calls the draw() method on all added [Stage] objects. */
    fun draw()
    {
        for(s in stages) { s.draw() }
    }

    /** Calls the act() method on all added [Stage] objects. */
    open fun update(delta: Float)
    {
        for(s in stages) { s.act(delta) }
    }

    /** Finalizes acting of all added [Stage] objects. */
    open fun hide()
    {
        for(s in stages) { s.act(100f) }
    }

    /** Updates the viewports of all added [Stage] objects. */
    fun resize(width: Int, height: Int)
    {
        for(s in stages) { s.viewport.update(width, height) }
    }
}
