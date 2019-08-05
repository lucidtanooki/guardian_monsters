package de.limbusdev.guardianmonsters.fwmengine.world.ecs

import kotlin.reflect.KClass

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

    fun add(gameObject: GdxGameObject)
    {
        gameObjectsToBeAdded.add(gameObject)
    }

    fun remove(gameObject: GdxGameObject)
    {
        gameObjectsToBeRemoved.add(gameObject)
    }

    fun getAll(type: String) : List<GdxGameObject>
    {
        return gameObjects.filter { it.type == type }
    }

    fun getAllWith(signature: List<String>) : List<GdxGameObject>
    {
        val a = gameObjects
        return gameObjects.filter { it.signature.containsAll(signature) && it.signature.size == signature.size }
    }
}