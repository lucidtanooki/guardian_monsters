package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup

import de.limbusdev.guardianmonsters.ui.AHUD


/**
 * For all children of a BattleWidget, callbacks have to be added. When adding a widget to the
 * [AHUD], the method onButtonClicked() can be used, in that case you can enter the button ID
 * into the Enumeration [ButtonIDs]
 *
 * @author Georg Eckert 2019
 */
abstract class BattleWidget : WidgetGroup()
{
    private val runnableRemove: () -> Unit

    init
    {
        // Controller
        runnableRemove = { superRemove() }
    }

    fun addFadeOutAction(duration: Float)
    {
        addAction(Actions.sequence(Actions.alpha(0f, duration), Actions.visible(false)))
    }

    fun addFadeInAction(duration: Float)
    {
        addAction(Actions.sequence(Actions.visible(true), Actions.alpha(1f, duration)))
    }


    open fun addToStageAndFadeIn(newParent: Stage)
    {
        clearActions()
        addToStage(newParent)
        addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(.5f)))
    }

    open fun fadeOutAndRemove(): Boolean
    {
        parent?.addAction(Actions.sequence(Actions.alpha(1f), Actions.alpha(0f, .3f), Actions.run(runnableRemove)))

        return (parent != null)
    }

    fun addToStage(stage: Stage)
    {
        stage.addActor(this)
    }

    private fun superRemove(): Boolean = super.remove()

    fun enable()
    {
        color = Color.WHITE
        touchable = Touchable.enabled

        children.forEach{ child -> enable(child) }
    }

    fun disable()
    {
        color = Color.GRAY
        touchable = Touchable.disabled

        children.forEach{ child -> disable(child) }
    }

    fun enable(child: Actor)
    {
        child.color = Color.WHITE
        child.touchable = Touchable.enabled
    }

    fun disable(child: Actor)
    {
        child.color = Color.GRAY
        child.touchable = Touchable.disabled
    }

}
