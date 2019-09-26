package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.parsers.IComponentParser

import ktx.collections.gdxArrayOf

/**
 * A CutSceneComponent does everything you would expect from automatically running scenes. This is
 * used to tell story to the player. It is always started by a trigger.
 *
 * What it should be able to do:
 *
 * + stop player movement
 * + gather needed objects and components
 * + move people, objects and the camera
 * + open conversations
 */
class CutSceneComponent : LimbusBehaviour()
{
    /** Used to give note, when a conversation scene starts. */
    val onConversationScene = mutableSetOf<((CutSceneComponent, String, String) -> Unit)>()

    private val cutSceneElements = mutableListOf<ICutSceneElement>()
    private var elementIterator = cutSceneElements.iterator()

    override fun initialize()
    {
        super.initialize()

        // TODO create cut scene elements from tiled object data
        cutSceneElements.add(PathScene())
        cutSceneElements.add(ConversationScene())
        cutSceneElements.add(PathScene())
        elementIterator = cutSceneElements.iterator()
        nextElement()   // TODO this should be initiated by a trigger
    }

    private fun nextElement()
    {
        if(!elementIterator.hasNext()) { return }
        elementIterator.next().act(this, gameObject)
    }

    private interface ICutSceneElement
    {
        fun act(scene: CutSceneComponent, gameObject: LimbusGameObject)
    }

    private class PathScene : ICutSceneElement
    {
        // TODO hold real path data from a tiled map object
        override fun act(scene: CutSceneComponent, gameObject: LimbusGameObject)
        {
            val pathComponent = gameObject.getOrCreate<PathComponent>()
            val path = gdxArrayOf(SkyDirection.SSTOP, SkyDirection.S, SkyDirection.S, SkyDirection.ESTOP, SkyDirection.WSTOP, SkyDirection.SSTOP)

            pathComponent.path = path
            pathComponent.reset()
            pathComponent.repeat = false
            pathComponent.onPathComplete.add { println("Path Scene complete."); scene.nextElement() }
        }
    }

    private class ConversationScene : ICutSceneElement
    {
        override fun act(scene: CutSceneComponent, gameObject: LimbusGameObject)
        {
            println("Conversation started.")
            val conversationComponent = gameObject.getOrCreate<ConversationComponent>()
            conversationComponent.name = "person_name_25_15"
            conversationComponent.text = "person_25_15"
            conversationComponent.onConversationFinished.add { println("that works"); scene.nextElement() }

            scene.onConversationScene.forEach { it.invoke(scene, "person_name_25_15", "person_25_15") }
        }
    }

    private class EnableDisableScene : ICutSceneElement
    {
        override fun act(scene: CutSceneComponent, gameObject: LimbusGameObject)
        {

        }
    }

    private class PossessCameraScene : ICutSceneElement
    {
        override fun act(scene: CutSceneComponent, gameObject: LimbusGameObject)
        {

        }
    }

    object Parser : IComponentParser<CutSceneComponent>
    {
        override fun createComponent() = CutSceneComponent()

        override fun parseComponent(json: Json, mapObject: MapObject): CutSceneComponent?
        {
            return CutSceneComponent()
        }
    }
}