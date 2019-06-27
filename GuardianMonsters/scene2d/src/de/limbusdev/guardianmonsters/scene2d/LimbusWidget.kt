package de.limbusdev.guardianmonsters.scene2d

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ArrayMap
import ktx.actors.onClick
import ktx.actors.setKeyboardFocus

/**
 * LimbusWidget
 * @author Georg Eckert 2019
 */
// Extension to libGDX ArrayMap to use square brackets
operator fun <K, V> ArrayMap<K, V>.set(key: K, value: V)
{
    put(key, value)
}

/** Chain function, that always returns the actor. */
fun Actor.size(width: Float, height: Float): Actor
{
    this.setSize(width, height)
    return this
}

/** Chain function, that always returns the actor. */
fun Actor.position(x: Float = 0f, y: Float = 0f, align: Int = Align.bottomLeft): Actor
{
    this.setPosition(x, y, align)
    return this
}

fun Actor.position(position: PositionXYA) : Actor
{
    this.setPosition(position.x, position.y, position.align)
    return this
}

/**
 * Replaces all currently attached [ClickListener]s to this actor with the given one.
 * @param listener invoked each time this actor is clicked.
 * @return [ClickListener] instance.
 */
inline fun Actor.replaceOnActorClick(crossinline listener: () -> Unit): ClickListener
{
    val clickListener = object : ClickListener()
    {
        override fun clicked(event: InputEvent, x: Float, y: Float) = listener()
    }
    this.clearListeners()
    this.addListener(clickListener)
    return clickListener
}

inline fun Button.replaceOnButtonClick(crossinline listener: () -> Unit): ClickListener
{
    val clickListener = object : ClickListener()
    {
        override fun clicked(event: InputEvent, x: Float, y: Float)
        {
            listener()
        }
    }

    // Restore default listeners
    var listener0 : EventListener? = null
    var listener1 : EventListener? = null
    if(this.listeners.size >= 2)
    {
        listener0 = this.listeners.get(0)
        listener1 = this.listeners.get(1)
    }

    this.clearListeners()
    if(listener0 != null) { this.addListener(listener0) }
    if(listener1 != null) { this.addListener(listener1) }
    this.addListener(clickListener)
    return clickListener
}


fun Actor.setup(width: Float, height: Float, x: Float, y: Float, align: Int, parent: Group?)
{
    this.setSize(width, height)
    this.setPosition(x, y, align)
    parent?.addActor(this)
}

fun Actor.setup(layout: Scene2DLayout, parent: Group? = null)
{
    this.setup(layout.width, layout.height, layout.x, layout.y, layout.align, parent)
}

fun Label.setupLabel(layout: LabelLayout, parent: Group? = null)
{
    this.setSize(layout.width, layout.height)
    this.setPosition(layout.x, layout.y, layout.align)
    this.setAlignment(layout.align, layout.line)
    this.setWrap(layout.wrap)
    parent?.addActor(this)
}

fun Actor.setup(position: PositionXYA, parent: Group? = null)
{
    this.setPosition(position.x, position.y, position.align)
    parent?.addActor(this)
}

fun Image.setRegion(region: TextureRegion)
{
    this.drawable = TextureRegionDrawable(region)
}

fun Image.setup(layout: Scene2DLayout, parent: Group? = null)
{
    this.setSize(layout.width, layout.height)
    this.setPosition(layout.x, layout.y, layout.align)
    parent?.addActor(this)
}

fun Actor.setPosition(x: Int = 0, y: Int = 0, align: Int = Align.bottomLeft)
{
    this.setPosition(x.toFloat(), y.toFloat(), align)
}

fun Actor.setPosition(x: Float = 0f, y: Int = 0, align: Int = Align.bottomLeft)
{
    this.setPosition(x, y.toFloat(), align)
}

fun Actor.setPosition(x: Int = 0, y: Float = 0f, align: Int = Align.bottomLeft)
{
    this.setPosition(x.toFloat(), y, align)
}

/** Position: X, Y, Align */
class PositionXYA(val x: Float = 0f, val y: Float = 0f, val align: Int = Align.bottomLeft)

class LabelLayout(val width: Float, val height: Float, val x: Float = 0f, val y: Float = 0f,
                  val align: Int = Align.bottomLeft, val line: Int = Align.left,
                  val wrap: Boolean = false)

fun Group.setup(layout: Scene2DLayout, parent: Group? = null)
{
    this.setSize(layout.width, layout.height)
    this.setPosition(layout.x, layout.y, layout.align)
    parent?.addActor(this)
}

class Scene2DLayout(val width: Float, val height: Float, val x: Float = 0f, val y: Float = 0f,
                    val align: Int = Align.bottomLeft)
