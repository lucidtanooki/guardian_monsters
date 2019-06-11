package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions.run as runThis
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup

import ktx.actors.then


/**
 * BattleWidget adds some convenience methods to [WidgetGroup], like:
 *
 *  + fading in and out
 *  + automated adding and removing of the Widget from and to parent or stage
 *  + enabling and disabling of the widget and all children
 *  + visualizing enabled and disabled states
 *
 * @author Georg Eckert 2019
 */
abstract class BattleWidget : WidgetGroup()
{
    // .................................................................................. Properties
    private val runnableRemove: () -> Unit


    // ................................................................................ Constructors
    init
    {
        // Controller
        runnableRemove = { superRemove() }
    }


    // ..................................................................................... Methods
    /** Smoothly turns the actors alpha to 0 and then makes it invisible. */
    fun addFadeOutAction(duration: Float)
    {
        addAction(alpha(0f, duration) then visible(false))
    }

    /** Makes the actor visible and smoothly turns it's alpha to 1. */
    fun addFadeInAction(duration: Float)
    {
        addAction(visible(true) then alpha(1f, duration))
    }

    /** Clears all actions, adds this actor to the given stage and fades it in. */
    open fun addToStageAndFadeIn(newParent: Stage)
    {
        clearActions()
        addToStage(newParent)
        addAction(alpha(0f) then fadeIn(.5f))
    }

    /** Fades out actor and removes it from it's parent. Returns false, if actor had no parent. */
    open fun fadeOutAndRemove(): Boolean
    {
        parent?.addAction(alpha(1f) then alpha(0f, .3f) then runThis(runnableRemove))

        return (parent != null)
    }

    /** Adds this actor to the given [Stage]. */
    fun addToStage(stage: Stage)
    {
        stage.addActor(this)
    }

    /** Resets the tint (white), makes the actor touchable and enables all children. */
    fun enable()
    {
        color = Color.WHITE
        touchable = Touchable.enabled

        children.forEach{ child -> enable(child) }
    }

    /** Changes the tint to gray, disables touch and disables all children as well */
    fun disable()
    {
        color = Color.GRAY
        touchable = Touchable.disabled

        children.forEach{ child -> disable(child) }
    }

    /** Resets the tint (white) of a child and makes it touchable. */
    fun enable(child: Actor)
    {
        child.color = Color.WHITE
        child.touchable = Touchable.enabled
    }

    /** Changes the child's tint to gray and disables touch for it. */
    fun disable(child: Actor)
    {
        child.color = Color.GRAY
        child.touchable = Touchable.disabled
    }


    // ............................................................................... Super Methods
    /** Makes super's remove() method visible. */
    private fun superRemove(): Boolean = super.remove()
}
