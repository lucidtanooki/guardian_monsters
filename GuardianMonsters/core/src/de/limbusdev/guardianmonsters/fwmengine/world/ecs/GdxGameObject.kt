package de.limbusdev.guardianmonsters.fwmengine.world.ecs

class GdxGameObject
{
    val components = ArrayList<GDXBehaviour>()
    private val componentIterator = components.iterator()

    fun update(deltaTime: Float)
    {
        for(component in componentIterator)
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
    }

    inline fun <reified T : GDXBehaviour> getComponents() : List<T>
    {
        return components.filterIsInstance<T>()
    }

    inline fun <reified T : GDXBehaviour> get() : T?
    {
        return components.filterIsInstance<T>().first()
    }

    fun add(component: GDXBehaviour)
    {
        components.add(component)
    }

    fun remove(component: GDXBehaviour)
    {
        components.remove(component)
    }
}