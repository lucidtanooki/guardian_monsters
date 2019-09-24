package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get

import de.limbusdev.utils.geometry.IntRect


/**
 * MonsterArea
 *
 * @author Georg Eckert 2015-12-17
 */
class MonsterAreaComponent() : LimbusBehaviour()
{
    companion object
    {
        const val className = "MonsterAreaComponent"
        val defaultJson = """
            monsters: 28;0.9;13;0.05;32;0.05
            probability: 0.05
            probability2: 0.02
            probability3: 0.01
            """.trimIndent()
    }

    // --------------------------------------------------------------------------------------------- PROPERTIES
    val monsters             = Array<Int>()     // IDs of the Guardians that appear in this area
    val monsterProbabilities = Array<Float>()   // at which probability they appear
    val teamSizeProbabilities = Array<Float>()  // for 1, 2 or 3 monsters

    // Inherits Extent from RectangleMapObject



    // --------------------------------------------------------------------------------------------- Parser
    object Parser : IComponentParser<MonsterAreaComponent>
    {
        private data class Data
        (
                var enabled: Boolean = true,
                var monsters: String,
                var probability: Float,
                var probability2: Float,
                var probability3: Float
        )

        override fun createComponent() = MonsterAreaComponent()

        override fun parseComponent(json: Json, mapObject: MapObject): MonsterAreaComponent?
        {
            // MapObject must contain proper component
            if(!mapObject.properties.containsKey(className)) { return null }

            val jsonStringWithoutBrackets = mapObject.properties[className, defaultJson]
            val area = json.fromJson(Data::class.java, "{$jsonStringWithoutBrackets}")

            val areaComponent = MonsterAreaComponent()
            areaComponent.teamSizeProbabilities.add(area.probability)
            areaComponent.teamSizeProbabilities.add(area.probability2)
            areaComponent.teamSizeProbabilities.add(area.probability3)

            val atts = area.monsters.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            var i = 0
            while (i < atts.size)
            {
                areaComponent.monsters.add(atts[i].toInt())
                areaComponent.monsterProbabilities.add(atts[i + 1].toFloat())
                i += 2
            }

            return areaComponent
        }
    }
}
