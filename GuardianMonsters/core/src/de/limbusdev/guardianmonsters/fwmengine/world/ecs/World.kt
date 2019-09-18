package de.limbusdev.guardianmonsters.fwmengine.world.ecs

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.*
import kotlin.reflect.KClass

class World
{
    var hero = LimbusGameObject("Hero")
        private set

    private val gameObjects = mutableListOf<LimbusGameObject>()

    private val gameObjectsToBeAdded = mutableListOf<LimbusGameObject>()
    private val gameObjectsToBeRemoved = mutableListOf<LimbusGameObject>()

    val componentParsers = mutableMapOf<KClass<out LimbusBehaviour>, IComponentParser<out LimbusBehaviour>>()

    private var isStopped = true

    init
    {
        componentParsers[ColliderComponent::class] = ColliderComponentParser
        componentParsers[ConversationComponent::class] = ConversationComponentParser
        componentParsers[CharacterSpriteComponent::class] = CharacterSpriteComponentParser
        componentParsers[WarpStartComponent::class] = WarpStartComponentParser
        componentParsers[WarpTargetComponent::class] = WarpTargetComponentParser
        componentParsers[PathComponent::class] = PathComponentParser
        componentParsers[InputComponent::class] = InputComponentParser
        componentParsers[TileWiseMovementComponent::class] = TileWiseMovementComponentParser
    }

    fun start() { isStopped = false }

    fun stop() { isStopped = true }

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
        if(isStopped) { return }

        for(gameObject in gameObjects)
        {
            if(gameObject.enabled)
            {
                gameObject.update(deltaTime)
            }
            if(isStopped) { return }
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

    fun getAll() = gameObjects

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

    fun getAll(layer: Int) : List<LimbusGameObject>
    {
        return gameObjects.filter { it.transform.layer == layer }
    }

    fun getAllWithExactly(signature: List<String>, layer: Int) : List<LimbusGameObject>
    {
        val a = gameObjects
        return gameObjects.filter { it.signature.containsAll(signature) && it.signature.size == signature.size && it.transform.layer == layer }
    }

    fun getAllWith(componentType: String, layer: Int) : List<LimbusGameObject>
    {
        return gameObjects.filter { it.signature.contains(componentType) && it.transform.layer == layer }
    }
}