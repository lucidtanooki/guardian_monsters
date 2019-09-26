package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour

/**
 * ConversationComponent
 *
 * @author Georg Eckert 2015-12-02
 */
class ConversationComponent(var name : String = "", var text : String = "") : LimbusBehaviour()
{
    companion object
    {
        const val className ="ConversationComponent"
        val defaultJson =
                """
                    enabled: true,
                    name: "",
                    text: ""
            """.trimMargin()
    }

    /** Callbacks. Run when the whole conversation is complete. */
    val onConversationFinished = mutableSetOf<(() -> Unit)>()

    fun closeConversation()
    {
        onConversationFinished.forEach { it.invoke() }
    }
}
