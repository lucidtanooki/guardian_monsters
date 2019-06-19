package de.limbusdev.utils.extensions

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

/**
 * Replaces all currently attached [ClickListener]s to this actor with the given one.
 * @param listener invoked each time this actor is clicked.
 * @return [ClickListener] instance.
 */
inline fun Actor.replaceOnClick(crossinline listener: () -> Unit): ClickListener
{
    val clickListener = object : ClickListener()
    {
        override fun clicked(event: InputEvent, x: Float, y: Float) = listener()
    }
    this.clearListeners()
    this.addListener(clickListener)
    return clickListener
}