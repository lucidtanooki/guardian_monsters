package de.limbusdev.guardianmonsters.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
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

