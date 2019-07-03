package de.limbusdev.guardianmonsters.fwmengine.world.model

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.MathUtils
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get

import de.limbusdev.utils.geometry.IntVec2

/**
 * Created by georg on 01.12.15.
 */
class MapPersonInformation
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    var path: String = ""
    var startPosition = IntVec2()
    var moves = false
    var name: String = ""
    var conversation: String = ""
    var male = false
    var spriteIndex = 0


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    constructor(mo: MapObject)
    {
        check(mo.properties.containsKey("type") && mo.properties["type", "null"] == "person")
        { "Given MapObject is not of type \"person\"" }

        mo as RectangleMapObject

        construct(

                mo.properties["path", ""],
                IntVec2(MathUtils.round(mo.rectangle.x), MathUtils.round(mo.rectangle.y)),
                mo.properties["static", true],
                mo.properties["text", ""],
                mo.properties["personName", ""],
                mo.properties["male", true],
                mo.properties["spriteIndex", 0]
        )
    }

    constructor
    (
            path            : String,
            startPosition   : IntVec2,
            moves           : Boolean,
            conv            : String,
            name            : String,
            male            : Boolean,
            spriteIndex     : Int
    ) {
        construct(path, startPosition, moves, conv, name, male, spriteIndex)
    }

    fun construct
    (
            path            : String,
            startPosition   : IntVec2,
            moves           : Boolean,
            conv            : String,
            name            : String,
            male            : Boolean,
            spriteIndex     : Int
    ) {
        this.path = path
        this.startPosition = startPosition
        this.moves = moves
        this.conversation = conv
        this.male = male
        this.spriteIndex = spriteIndex
        this.name = name
    }
}
