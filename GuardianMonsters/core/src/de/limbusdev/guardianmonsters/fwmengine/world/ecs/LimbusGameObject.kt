package de.limbusdev.guardianmonsters.fwmengine.world.ecs

class LimbusGameObject(var name: String = "", val type: String = "general")
{
    val signature = mutableListOf<String>()

    val components = ArrayList<LimbusBehaviour>()

    private val componentsToBeAdded = mutableListOf<LimbusBehaviour>()
    private val componentsToBeRemoved = mutableListOf<LimbusBehaviour>()

    var enabled : Boolean = true
        private set

    fun update(deltaTime: Float)
    {
        if(!enabled) { return }

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

        for(c in componentsToBeAdded)
        {
            if(!components.contains(c)) { components.add(c) }
        }
        componentsToBeAdded.clear()

        for(c in componentsToBeRemoved)
        {
            if(components.contains(c)) { components.remove(c) }
        }
        componentsToBeRemoved.clear()
    }

    inline fun <reified T : LimbusBehaviour> getComponents() : List<T>
    {
        return components.filterIsInstance<T>()
    }

    inline fun <reified T : LimbusBehaviour> get() : T?
    {
        return components.filterIsInstance<T>().first()
    }

    fun add(component: LimbusBehaviour)
    {
        signature.add(component::class.simpleName ?: "Anonymous")
        componentsToBeAdded.add(component)
        component.gameObject = this
    }

    fun remove(component: LimbusBehaviour)
    {
        signature.remove(component::class.simpleName ?: "Anonymous")
        componentsToBeRemoved.add(component)
        component.gameObject = null
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
        World.remove(this)
        val it = components.iterator()
        for(component in it)
        {
            it.remove()
            component.dispose()
        }
    }

    companion object
    {
        val typeSignatures: MutableMap<String, List<String>> = mutableMapOf()

        fun registerType(type: String, signature: List<String>)
        {
            typeSignatures[type] = signature
        }
    }
}