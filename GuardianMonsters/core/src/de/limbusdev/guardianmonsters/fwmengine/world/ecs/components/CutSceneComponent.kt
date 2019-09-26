package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
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

        cutSceneElements.add(PathScene())
        cutSceneElements.add(ConversationScene())
        cutSceneElements.add(PathScene())
        elementIterator = cutSceneElements.iterator()
        nextElement()
    }

    private fun nextElement()
    {
        if(!elementIterator.hasNext()) { return }
        when(elementIterator.next())
        {
            is PathScene ->
            {
                val pathComponent = gameObject.getOrCreate<PathComponent>()
                val path = gdxArrayOf(SkyDirection.SSTOP, SkyDirection.S, SkyDirection.S, SkyDirection.ESTOP, SkyDirection.WSTOP, SkyDirection.SSTOP)

                pathComponent.path = path
                pathComponent.reset()
                pathComponent.repeat = false
                pathComponent.onPathComplete.add { println("Path Scene complete."); nextElement() }
            }
            is ConversationScene ->
            {
                println("Conversation started.")
                val conversationComponent = gameObject.getOrCreate<ConversationComponent>()
                conversationComponent.name = "person_name_25_15"
                conversationComponent.text = "person_25_15"
                conversationComponent.onConversationFinished.add { println("that works"); nextElement() }

                onConversationScene.forEach { it.invoke(this, "person_name_25_15", "person_25_15") }
            }
            is EnableDisableScene ->
            {

            }
            is PossessCameraScene ->
            {

            }
        }
    }

    private interface ICutSceneElement
    {

    }

    private class PathScene : ICutSceneElement
    {

    }

    private class ConversationScene : ICutSceneElement
    {

    }

    private class EnableDisableScene : ICutSceneElement
    {

    }

    private class PossessCameraScene : ICutSceneElement
    {

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