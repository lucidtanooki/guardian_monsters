package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.utils.Array
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour

import de.limbusdev.utils.geometry.IntRect


/**
 * MonsterArea
 *
 * @author Georg Eckert 2015-12-17
 */
class MonsterAreaComponent() : LimbusBehaviour()
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    val monsters             = Array<Int>()     // IDs of the Guardians that appear in this area
    val monsterProbabilities = Array<Float>()   // at which probability they appear
    val teamSizeProbabilities = Array<Float>()  // for 1, 2 or 3 monsters

    var monsterProperties = ""

    // Inherits Extent from RectangleMapObject


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        val atts = monsterProperties.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        var i = 0
        while (i < atts.size)
        {
            monsters.add(atts[i].toInt())
            monsterProbabilities.add(atts[i + 1].toFloat())
            i += 2
        }
    }
}
