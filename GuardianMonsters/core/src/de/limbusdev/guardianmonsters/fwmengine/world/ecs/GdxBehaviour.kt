package de.limbusdev.guardianmonsters.fwmengine.world.ecs

/**
 * GdxBehaviour is the base class from which every Component derives.
 */
abstract class GdxBehaviour
{
    var gameObject : GdxGameObject? = null
    var initialized: Boolean = false
        private set
    var enabled: Boolean = true
        private set

    /**
     * Start is called, when a component is enabled, just before the first time update(...) is
     * called for the first time.
     * Always call super method first.
     */
    open fun initialize() { initialized = true }

    open fun update(deltaTime: Float) {}

    fun enable()  { onEnable(); enabled = true }

    fun disable() { onDisable(); enabled = false }

    open fun onEnable() {}

    open fun onDisable() {}

    open fun dispose() {}
}