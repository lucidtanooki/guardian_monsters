package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.CoreSL
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.parsers.IComponentParser
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get
import de.limbusdev.guardianmonsters.guardians.battle.BattleFactory
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.utils.geometry.IntVec2
import de.limbusdev.utils.logDebug


/**
 * MonsterArea
 *
 * @author Georg Eckert 2015-12-17
 */
class RandomBattleAreaComponent() : LimbusBehaviour()
{
    companion object
    {
        const val className = "RandomBattleAreaComponent"
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
    private val triggerCallback : (IntVec2) -> Unit = { initializeBattle() }


    override fun initialize()
    {
        super.initialize()

        val trigger = gameObject.getOrCreate<BoxTrigger2DComponent>()
        trigger.onTriggerEntered.add { go -> go?.get<TileWiseMovementComponent>()?.onGridSlotChanged?.add(triggerCallback) }
        trigger.onTriggerLeft.add { go -> go?.get<TileWiseMovementComponent>()?.onGridSlotChanged?.remove(triggerCallback) }
    }

    private fun initializeBattle()
    {
        if(MathUtils.randomBoolean(teamSizeProbabilities.get(0)))
        {
            logDebug(HeroComponent.TAG) { "Monster appeared!" }

            //............................................................. START BATTLE
            // TODO change min and max levels
            CoreSL.world.hero.get<InputComponent>()!!.inBattle = true
            val guardianProbabilities = ArrayMap<Int, Float>()
            for (i in 0 until monsters.size) {
                guardianProbabilities.put(monsters.get(i), monsterProbabilities.get(i))
            }

            val oppTeam = BattleFactory.createOpponentTeam(guardianProbabilities, teamSizeProbabilities, 1, 1)

            CoreSL.ecs.hud.battleScreen.initialize(

                    CoreSL.world.hero.get<TeamComponent>()!!.team,
                    oppTeam,
                    CoreSL.world.hero.get<GuardoSphereComponent>()!!.guardoSphere
            )

            Services.ScreenManager().pushScreen(CoreSL.ecs.hud.battleScreen)
            //............................................................. START BATTLE

            // Stop when in a battle
            //TODO if (inputComponent.touchDown) { inputComponent.startMoving = false }
        }
    }

    // --------------------------------------------------------------------------------------------- Parser
    object Parser : IComponentParser<RandomBattleAreaComponent>
    {
        private data class Data
        (
                var enabled: Boolean = true,
                var monsters: String = "",
                var probability: Float = 0.05f,
                var probability2: Float = 0.2f,
                var probability3: Float = 0.01f
        )

        override fun createComponent() = RandomBattleAreaComponent()

        override fun parseComponent(json: Json, mapObject: MapObject): RandomBattleAreaComponent?
        {
            // MapObject must contain proper component
            if(!mapObject.properties.containsKey(className)) { return null }

            val jsonStringWithoutBrackets = mapObject.properties[className, defaultJson]
            val area = json.fromJson(Data::class.java, "{$jsonStringWithoutBrackets}")

            val areaComponent = RandomBattleAreaComponent()
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
