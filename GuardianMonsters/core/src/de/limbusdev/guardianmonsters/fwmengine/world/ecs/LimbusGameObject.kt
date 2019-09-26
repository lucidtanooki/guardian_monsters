package de.limbusdev.guardianmonsters.fwmengine.world.ecs

import de.limbusdev.guardianmonsters.CoreSL
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.RenderingLimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Transform
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.KClass

class LimbusGameObject
{
    companion object
    {
        val objectByID = mutableMapOf<UUID,LimbusGameObject>()
        val tiledIDtoObjectID = mutableMapOf<Int,UUID>()

        private val typeSignatures: MutableMap<String, List<String>> = mutableMapOf()

        fun registerType(type: String, signature: List<String>)
        {
            typeSignatures[type] = signature
        }
    }

    constructor(name: String = "")
    {
        UUID = java.util.UUID.randomUUID()
        this.name = name
        objectByID[UUID] = this
    }

    constructor(ID: Int, name: String = "")
    {
        UUID = java.util.UUID.randomUUID()
        this.name = name
        objectByID[UUID] = this
        tiledIDtoObjectID[ID] = UUID
    }

    val UUID: UUID
    var name: String

    // LimbusGameObjects must have a transform
    var transform : Transform = Transform(this)

    val signature = mutableListOf<KClass<out LimbusBehaviour>>()

    val components = ArrayList<LimbusBehaviour>()

    private val componentsToBeAdded = mutableListOf<LimbusBehaviour>()
    private val componentsToBeRemoved = mutableListOf<LimbusBehaviour>()

    var enabled : Boolean = true
        private set

    fun render()
    {
        if(!enabled) { return }

        for(component in components)
        {
            if(!component.initialized) { component.initialize() }
            if(component.enabled && component is RenderingLimbusBehaviour)
            {
                component.render()
            }
        }
    }

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
                signature.add(c::class)
            }
        }
        componentsToBeAdded.clear()

        for(c in componentsToBeRemoved)
        {
            if(components.contains(c))
            {
                components.remove(c)
                c.gameObject = LimbusGameObject()
                signature.remove(c::class)
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
            component = Components.parsers[T::class]!!.createComponent() as T
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
        componentsToBeAdded.add(component)
    }

    fun remove(component: LimbusBehaviour)
    {
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
        objectByID.remove(UUID)
        val it = components.iterator()
        for(component in it)
        {
            it.remove()
            component.dispose()
        }
    }
}