package de.limbusdev.utils.extensions

import com.badlogic.gdx.utils.ArrayMap

// Extension to libGDX ArrayMap to use square brackets
operator fun <K, V> ArrayMap<K, V>.set(key: K, value: V)
{
    put(key, value)
}