package de.limbusdev.guardianmonsters.fwmengine.world.ecs

import com.badlogic.ashley.core.Component

/**
 * GDXBehaviour is the base class from which every Component derives.
 */
abstract class GDXBehaviour : Component
{
    var initialized: Boolean = false
        private set
    var enabled: Boolean = true
        private set

    /**
     * Start is called, when a component is enabled, just before the first time update(...) is
     * called for the first time
     */
    open fun initialize() { initialized = true }

    open fun update(deltaTime: Float) {}

    fun enable()  { onEnable(); enabled = true }

    fun disable() { onDisable(); enabled = false }

    open fun onEnable() {}

    open fun onDisable() {}
}