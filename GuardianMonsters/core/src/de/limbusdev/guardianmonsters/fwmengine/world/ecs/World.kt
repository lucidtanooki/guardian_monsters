package de.limbusdev.guardianmonsters.fwmengine.world.ecs

object World
{
    val hero = GdxGameObject()

    private val gameObjects = mutableListOf<GdxGameObject>()

    private val gameObjectsToBeAdded = mutableListOf<GdxGameObject>()
    private val gameObjectsToBeRemoved = mutableListOf<GdxGameObject>()

    init
    {
        add(hero)
    }

    fun update(deltaTime: Float)
    {
        for(gameObject in gameObjects)
        {
            if(gameObject.enabled)
            {
                gameObject.update(deltaTime)
            }
        }

        for(go in gameObjectsToBeAdded)
        {
            if(!gameObjects.contains(go)) { gameObjects.add(go) }
        }
        gameObjectsToBeAdded.clear()

        for(go in gameObjectsToBeRemoved)
        {
            if(gameObjects.contains(go)) { gameObjects.remove(go) }
        }
        gameObjectsToBeRemoved.clear()
    }

    fun add(gameObject: GdxGameObject)
    {
        gameObjectsToBeAdded.add(gameObject)
    }

    fun remove(gameObject: GdxGameObject)
    {
        gameObjectsToBeRemoved.add(gameObject)
    }
}