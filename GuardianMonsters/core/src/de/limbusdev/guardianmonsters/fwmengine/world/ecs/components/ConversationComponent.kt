package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component
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

}
