package de.limbusdev.guardianmonsters.fwmengine.world.model

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.math.MathUtils
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get

/**
 * MapDescriptionInfo
 * Holds Information for simple text objects.
 *
 * @author Georg Eckert 2015-12-03
 */
class MapDescriptionInfo : Component
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    var title   : String = ""
    var content : String = ""
    var x       : Int = 0
    var y       : Int = 0

    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    constructor(mo: MapObject)
    {
        check(mo.properties.containsKey("type") && mo.properties["type", "null"] == "objectDescription")
        { "Given MapObject is not of type \"descriptionObject\"" }

        construct(

                mo.properties["title", ""],
                mo.properties["text", ""],
                MathUtils.round(mo.properties["x", 0f]),
                MathUtils.round(mo.properties["y", 0f])
        )
    }

    constructor(title: String, content: String, x: Int, y: Int)
    {
        construct(title, content, x, y)
    }

    private fun construct(title: String, content: String, x: Int, y: Int)
    {
        this.title = title
        this.content = content
        this.x = x
        this.y = y
    }
}
