package de.limbusdev.guardianmonsters.fwmengine.world.model

import com.badlogic.gdx.utils.Array

import de.limbusdev.utils.geometry.IntRect


/**
 * MonsterArea
 *
 * @author Georg Eckert 2015-12-17
 */
class MonsterArea
(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        monsterProperties: String,
        var teamSizeProbabilities: Array<Float> // for 1, 2 or 3 monsters
)
    : IntRect(x, y, width, height)
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    var monsters             = Array<Int>()     // IDs of the Guardians that appear in this area
    var monsterProbabilities = Array<Float>()   // at which probability they appear


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
