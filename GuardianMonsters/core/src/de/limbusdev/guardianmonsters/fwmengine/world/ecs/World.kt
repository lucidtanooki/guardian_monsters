package de.limbusdev.guardianmonsters.fwmengine.world.ecs

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.*
import kotlin.reflect.KClass

object World
{
    val hero = LimbusGameObject()

    private val gameObjects = mutableListOf<LimbusGameObject>()

    private val gameObjectsToBeAdded = mutableListOf<LimbusGameObject>()
    private val gameObjectsToBeRemoved = mutableListOf<LimbusGameObject>()

    val componentParsers = mutableMapOf<KClass<out LimbusBehaviour>, IComponentParser<out LimbusBehaviour>>()

    init
    {
        componentParsers[ColliderComponent::class] = ColliderComponentParser
        componentParsers[ConversationComponent::class] = ConversationComponentParser
        componentParsers[CharacterSpriteComponent::class] = CharacterSpriteComponentParser

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

    fun getAllWithExactly(signature: List<String>) : List<LimbusGameObject>
    {
        val a = gameObjects
        return gameObjects.filter { it.signature.containsAll(signature) && it.signature.size == signature.size }
    }

    fun getAllWith(componentType: String) : List<LimbusGameObject>
    {
        return gameObjects.filter { it.signature.contains(componentType) }
    }
}