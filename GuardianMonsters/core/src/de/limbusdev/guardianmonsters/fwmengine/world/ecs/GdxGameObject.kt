package de.limbusdev.guardianmonsters.fwmengine.world.ecs

class GdxGameObject(var name: String = "")
{
    val components = ArrayList<GdxBehaviour>()

    private val componentsToBeAdded = mutableListOf<GdxBehaviour>()
    private val componentsToBeRemoved = mutableListOf<GdxBehaviour>()

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

    inline fun <reified T : GdxBehaviour> getComponents() : List<T>
    {
        return components.filterIsInstance<T>()
    }

    inline fun <reified T : GdxBehaviour> get() : T?
    {
        return components.filterIsInstance<T>().first()
    }

    fun add(component: GdxBehaviour)
    {
        componentsToBeAdded.add(component)
        component.gameObject = this
    }

    fun remove(component: GdxBehaviour)
    {
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
}