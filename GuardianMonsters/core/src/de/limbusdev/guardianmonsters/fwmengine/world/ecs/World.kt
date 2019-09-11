package de.limbusdev.guardianmonsters.fwmengine.world.ecs

object World
{
    val hero = LimbusGameObject()

    private val gameObjects = mutableListOf<LimbusGameObject>()

    private val gameObjectsToBeAdded = mutableListOf<LimbusGameObject>()
    private val gameObjectsToBeRemoved = mutableListOf<LimbusGameObject>()

    init
    {
        add(hero)
    }

    fun addAndRemoveObjectsNow()
    {
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

    fun update(deltaTime: Float)
    {
        for(gameObject in gameObjects)
        {
            if(gameObject.enabled)
            {
                gameObject.update(deltaTime)
            }
        }

        addAndRemoveObjectsNow()
    }

    fun add(gameObject: LimbusGameObject)
    {
        gameObjectsToBeAdded.add(gameObject)
    }

    fun remove(gameObject: LimbusGameObject)
    {
        gameObjectsToBeRemoved.add(gameObject)
    }

    fun getAll(type: String) : List<LimbusGameObject>
    {
        return gameObjects.filter { it.type == type }
    }

    fun getAllWith(signature: List<String>) : List<LimbusGameObject>
    {
        val a = gameObjects
        return gameObjects.filter { it.signature.containsAll(signature) && it.signature.size == signature.size }
    }
}