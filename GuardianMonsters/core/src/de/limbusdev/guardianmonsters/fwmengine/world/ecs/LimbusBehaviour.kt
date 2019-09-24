package de.limbusdev.guardianmonsters.fwmengine.world.ecs

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Transform

/**
 * LimbusBehaviour is the base class from which every Component derives.
 */
abstract class LimbusBehaviour()
{
    var gameObject = LimbusGameObject()
    val transform : Transform get() = gameObject.transform

    var initialized: Boolean = false
        private set
    var enabled: Boolean = true
        set(value)
        {
            when(value)
            {
                true  -> onEnable()
                false -> onDisable()
            }
            field = value
        }

    /**
     * Start is called, when a component is enabled, just before the first time update(...) is
     * called for the first time.
     * Always call super method first.
     */
    open fun initialize() { initialized = true }

    /** Runs as often as possible */
    open fun update(deltaTime: Float) {}

    /** Runs every 1/60 s */
    open fun update60fps() {}

    open fun onEnable() {}

    open fun onDisable() {}

    open fun dispose() {}
}