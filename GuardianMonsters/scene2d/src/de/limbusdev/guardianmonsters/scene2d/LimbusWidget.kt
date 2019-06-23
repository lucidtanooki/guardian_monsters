package de.limbusdev.guardianmonsters.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ArrayMap

/**
 * LimbusWidget
 * @author Georg Eckert 2019
 */
// Extension to libGDX ArrayMap to use square brackets
operator fun <K, V> ArrayMap<K, V>.set(key: K, value: V)
{
    put(key, value)
}

fun Actor.lSetSize(width: Float, height: Float): Actor
{
    this.setSize(width, height)
    return this
}

fun Actor.lSetPosition(x: Float, y: Float, align: Int): Actor
{
    this.setPosition(x, y, align)
    return this
}

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


fun Actor.setup(width: Float, height: Float, x: Float, y: Float, align: Int, parent: Group?)
{
    this.setSize(width, height)
    this.setPosition(x, y, align)
    parent?.addActor(this)
}

fun Actor.setup(layout: Layout2D, parent: Group? = null)
{
    this.setup(layout.width, layout.height, layout.x, layout.y, layout.align ?: Align.center, parent)
}

fun Label.setup(layout2D: LabelLayout2D, parent: Group? = null)
{
    this.setSize(layout2D.width, layout2D.height)
    this.setPosition(layout2D.x, layout2D.y, layout2D.align ?: Align.left)
    this.setAlignment(layout2D.align ?: Align.left, layout2D.line)
    this.setWrap(layout2D.wrap)
    parent?.addActor(this)
}

fun Actor.setup(position: Position2D, parent: Group? = null)
{
    this.setPosition(position.x, position.y, position.align ?: Align.center)
    parent?.addActor(this)
}

inline var Actor.position: Position2D
    get() = Position2D(x, y, null)
    set(value) {setPosition(value.x, value.y, value.align ?: Align.center)}

inline var Actor.setup: Layout2D
    get() = Layout2D(width, height, x, y, null)
    set(value) {setup(value)}

class Position2D(val x: Float = 0f, val y: Float = 0f, val align: Int? = Align.center)

open class Layout2D(val width: Float, val height: Float, val x: Float, val y: Float, val align: Int?)

class LabelLayout2D(width: Float, height: Float, x: Float, y: Float, align: Int, val line: Int, val wrap: Boolean)
    : Layout2D(width, height, x, y, align)