package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.CoreSL
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject
import de.limbusdev.utils.logDebug
import kotlin.reflect.KClass

abstract class TriggerComponent : LimbusBehaviour()
{
    val onTriggerEntered    = mutableSetOf<((LimbusGameObject?) -> Unit)>()
    val onTriggerLeft       = mutableSetOf<((LimbusGameObject?) -> Unit)>()
    val whileInsideTrigger  = mutableSetOf<((LimbusGameObject?) -> Unit)>()

    val gameObjectsToBeAdded = mutableSetOf<LimbusGameObject>()
    val gameObjectsToBeRemoved = mutableSetOf<LimbusGameObject>()
    val currentlyOverlappingGameObjects = mutableSetOf<LimbusGameObject>()

    val triggerChannel = mutableSetOf< KClass<out LimbusBehaviour>>()

    private lateinit var collider : ColliderComponent

    override fun initialize()
    {
        super.initialize()

        collider = gameObject.getOrCreate()
    }

    open fun checkCollisions()
    {
        val triggeringObjects = CoreSL.world.getAllWith(triggerChannel)
        for(triggeringObject in triggeringObjects)
        {
            val otherCollider = triggeringObject.get<ColliderComponent>()
            if(otherCollider != null)
            {
                if (triggeringObject in currentlyOverlappingGameObjects)
                {
                    // Colliding object has been already inside trigger
                    if (doTheyCollide(collider, otherCollider))
                    {
//                        logDebug { "${triggeringObject.name} is inside trigger." }
                        // Colliding object is moving inside the trigger
                        whileInsideTrigger.forEach { it.invoke(triggeringObject) }
                    }
                    else
                    {
                        logDebug { "${triggeringObject.name} left trigger." }
                        // Colliding object left the trigger
                        gameObjectsToBeRemoved.add(triggeringObject)
                    }
                }
                else
                {
                    val rec = collider.asRectangle
                    // Colliding object has not been inside trigger yet
                    if (doTheyCollide(collider, otherCollider))
                    {
                        logDebug { "${triggeringObject.name} entered trigger." }
                        // Colliding object has entered trigger
                        gameObjectsToBeAdded.add(triggeringObject)
                    }
                }
            }
        }

        // Apply adding and removing game objects
        gameObjectsToBeRemoved.forEach { leavingGO ->

            currentlyOverlappingGameObjects.remove(leavingGO)
            onTriggerLeft.forEach { it.invoke(leavingGO) }
        }
        gameObjectsToBeRemoved.clear()

        gameObjectsToBeAdded.forEach { enteringGO ->

            currentlyOverlappingGameObjects.add(enteringGO)
            onTriggerEntered.forEach { it.invoke(enteringGO) }
        }
        gameObjectsToBeAdded.clear()
    }

    abstract fun doTheyCollide(triggerCollider: ColliderComponent, otherCollider: ColliderComponent) : Boolean

    override fun update60fps()
    {
        super.update60fps()

        checkCollisions()
    }
}