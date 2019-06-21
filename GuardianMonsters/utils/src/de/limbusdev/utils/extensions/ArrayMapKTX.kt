package de.limbusdev.utils.extensions

import com.badlogic.gdx.utils.*

// Extension to libGDX ArrayMap to use square brackets
operator fun <K, V> ArrayMap<K, V>.set(key: K, value: V)
{
    put(key, value)
}

fun <Key, Value> gdxMapOf(): ArrayMap<Key, Value> = ArrayMap()

inline fun <Key, Value> arrayMapOf(vararg keysToValues: Pair<Key, Value>): ArrayMap<Key, Value>
{
    val map = ArrayMap<Key, Value>()
    keysToValues.forEach { map[it.first] = it.second }
    return map
}
