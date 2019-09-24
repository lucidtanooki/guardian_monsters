package de.limbusdev.guardianmonsters.fwmengine.world.ecs

import de.limbusdev.guardianmonsters.CoreSL
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Transform
import kotlin.collections.ArrayList

class LimbusGameObject(var name: String = "", val type: String = "general")
{
    // LimbusGameObjects must have a transform
    var transform : Transform = Transform(this)

    val signature = mutableListOf<String>()

    val components = ArrayList<LimbusBehaviour>()

    private val componentsToBeAdded = mutableListOf<LimbusBehaviour>()
    private val componentsToBeRemoved = mutableListOf<LimbusBehaviour>()

    var enabled : Boolean = true
        private set

    fun update(deltaTime: Float)
    {
        if(!enabled) { return }

        transform.update(deltaTime)

        for(component in components)
        {
            if(!component.initialized)
            {
                component.initialize()
            }
            if(component.enabled)
            {
                component.update(deltaTime)
            }
        }

        addAndRemoveComponentsNow()
    }

    fun update60fps()
    {
        if(!enabled) { return }

        transform.update60fps()
        for(component in components)
        {
            if(component.initialized && component.enabled)
            {
                component.update60fps()
            }
        }
    }

    fun addAndRemoveComponentsNow()
    {
        for(c in componentsToBeAdded)
        {
            if(!components.contains(c))
            {
                components.add(c)
                c.gameObject = this
            }
        }
        componentsToBeAdded.clear()

        for(c in componentsToBeRemoved)
        {
            if(components.contains(c))
            {
                components.remove(c)
                c.gameObject = LimbusGameObject()
            }
        }
        componentsToBeRemoved.clear()
    }

    inline fun <reified T : LimbusBehaviour> getComponents() : List<T>
    {
        return components.filterIsInstance<T>()
    }

    /** Returns a component of the given type if this [LimbusGameObject] has one or else null. */
    inline fun <reified T : LimbusBehaviour> get() : T?
    {
        val components = getComponents<T>()
        if(components.isEmpty()) { return null }
        return components.filterIsInstance<T>().first()
    }

    /**
     * Returns an already existing component of the given type or creates a new one and adds it to
     * the [LimbusGameObject] and returns that one.
     */
    inline fun <reified T : LimbusBehaviour> getOrCreate() : T
    {
        var component = get<T>()
        if(component != null)
        {
            return component
        }
        else
        {
            component = CoreSL.world.componentParsers[T::class]!!.createComponent() as T
            add(component)
        }

        return component
    }

    inline fun <reified T : LimbusBehaviour> has() : Boolean
    {
        return components.filterIsInstance<T>().isNotEmpty()
    }

    fun add(component: LimbusBehaviour)
    {
        signature.add(component::class.java.simpleName)
        componentsToBeAdded.add(component)
    }

    fun remove(component: LimbusBehaviour)
    {
        signature.remove(component::class.java.simpleName)
        componentsToBeRemoved.add(component)
    }

    fun enable()
    {
        enabled = true
    }

    fun disable()
    {
        enabled = false
    }

    fun dispose()
    {
        CoreSL.world.remove(this)
        val it = components.iterator()
        for(component in it)
        {
            it.remove()
            component.dispose()
        }
    }

    companion object
    {
        private val typeSignatures: MutableMap<String, List<String>> = mutableMapOf()

        fun registerType(type: String, signature: List<String>)
        {
            typeSignatures[type] = signature
        }
    }
}