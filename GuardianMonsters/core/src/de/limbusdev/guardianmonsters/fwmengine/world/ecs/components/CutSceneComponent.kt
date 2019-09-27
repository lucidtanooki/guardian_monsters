package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.utils.Array
import de.limbusdev.guardianmonsters.CoreSL
import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject
import de.limbusdev.utils.geometry.IntVec2

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
 *
 * // TODO currently it is impossible to add two ConversationScene without anything else in
 * // TODO in between. Simply add a PathScene with SSTOP or something like that between them.
 */
class CutSceneComponent(triggerID : Int = 0) : IDBasedTriggerCallbackComponent(triggerID)
{
    companion object
    {
        const val className = "CutSceneComponent"
        val defaultJson = """
            ${IDBasedTriggerCallbackComponent.defaultJson}
            triggerID: 494,
            people: [A,B]
            scenes:
            [
            "A,say(25_16_0)",
            "A,walk(ESTOP)",
            "B,say(25_17_0)",
            "A,walk(W,W,W,W)"
            ]
            """.trimIndent()
    }

    override fun onTriggerEntered(enteringGameObject: LimbusGameObject?)
    {
        nextElement()
    }

    /** Used to give note, when a conversation scene starts. */
    val onConversationScene = mutableSetOf<((ConversationComponent) -> Unit)>()

    private val cutSceneElements = mutableSetOf<Pair<LimbusGameObject, ICutSceneElement>>()
    private var elementIterator = cutSceneElements.iterator()

    override fun initialize()
    {
        super.initialize()

        // TODO create cut scene elements from tiled object data
//        cutSceneElements.add(PathScene())
//        cutSceneElements.add(ConversationScene())
//        cutSceneElements.add(PathScene())
//        elementIterator = cutSceneElements.iterator()

        // Create People to act in this cut scene
        val personA = LimbusGameObject("Person A")
        val personB = LimbusGameObject("Person B")

        personA.add(PersonComponent(true, 0, "25_16", "ESTOP"))
        personB.add(PersonComponent(true, 1, "25_17", "WSTOP"))

        // Setup their positions
        personA.transform.onGrid = transform.onGrid + IntVec2(-1,0)
        personB.transform.onGrid = transform.onGrid + IntVec2(1,0)

        CoreSL.world.add(personA)
        CoreSL.world.add(personB)

        cutSceneElements.add(Pair(personA, ConversationScene("25_16")))
        cutSceneElements.add(Pair(personA, PathScene(gdxArrayOf(SkyDirection.ESTOP))))
        cutSceneElements.add(Pair(personB, ConversationScene("25_17")))
        cutSceneElements.add(Pair(personA, PathScene(gdxArrayOf(
                SkyDirection.W,
                SkyDirection.W,
                SkyDirection.W,
                SkyDirection.W,
                SkyDirection.W))))

        elementIterator = cutSceneElements.iterator()
    }

    fun nextElement()
    {
        if(!elementIterator.hasNext()) { return }
        val next = elementIterator.next()
        val nextObject = next.first
        val nextScene = next.second
        nextScene.act(this, nextObject)
    }

    private interface ICutSceneElement
    {
        fun act(scene: CutSceneComponent, gameObject: LimbusGameObject)
    }

    private class PathScene(val path: Array<SkyDirection>) : ICutSceneElement
    {
        override fun act(scene: CutSceneComponent, gameObject: LimbusGameObject)
        {
            val pathComponent = gameObject.getOrCreate<PathComponent>()
            pathComponent.path = path
            pathComponent.reset()
            pathComponent.repeat = false
            pathComponent.onPathComplete.add { println("Path Scene complete."); scene.nextElement() }
        }
    }

    private class ConversationScene(val personID: String) : ICutSceneElement
    {
        override fun act(scene: CutSceneComponent, gameObject: LimbusGameObject)
        {
            println("Conversation started.")
            val conversationComponent = gameObject.getOrCreate<ConversationComponent>()
            conversationComponent.name = "person_name_$personID"
            conversationComponent.text = "person_$personID"
            conversationComponent.onConversationFinished.add { println("that works"); scene.nextElement() }

            scene.onConversationScene.forEach { it.invoke(conversationComponent) }
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
}