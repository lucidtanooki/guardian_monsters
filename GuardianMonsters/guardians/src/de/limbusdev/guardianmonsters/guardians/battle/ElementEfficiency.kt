package de.limbusdev.guardianmonsters.guardians.battle

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.guardians.Element
import de.limbusdev.utils.log
import de.limbusdev.utils.logInfo

object ElementEfficiency
{
    private const val TAG = "ElementEfficiency"

    private var none        : ArrayMap<Element, Float>
    private var earth       : ArrayMap<Element, Float>
    private var water       : ArrayMap<Element, Float>
    private var flame       : ArrayMap<Element, Float>
    private var air         : ArrayMap<Element, Float>
    private var frost       : ArrayMap<Element, Float>
    private var mountain    : ArrayMap<Element, Float>
    private var arthropoda  : ArrayMap<Element, Float>
    private var demon       : ArrayMap<Element, Float>
    private var lindworm    : ArrayMap<Element, Float>
    private var forest      : ArrayMap<Element, Float>
    private var lightning   : ArrayMap<Element, Float>
    private var spirit      : ArrayMap<Element, Float>

    init
    {
        // Earth
        earth = ArrayMap()
        earth.put(Element.NONE,              0.0f)
        earth.put(Element.EARTH,             0.0f)
        earth.put(Element.WATER,             0.0f)
        earth.put(Element.FIRE,              0.5f)
        earth.put(Element.AIR,               0.5f)
        earth.put(Element.FOREST,           -0.5f)
        earth.put(Element.FROST,             0.0f)
        earth.put(Element.SPIRIT,            0.5f)
        earth.put(Element.DEMON,             0.0f)
        earth.put(Element.ARTHROPODA,        0.0f)
        earth.put(Element.MOUNTAIN,          0.0f)
        earth.put(Element.LIGHTNING,         1.0f)
        earth.put(Element.LINDWORM,          0.0f)

        // Water
        water = ArrayMap()
        water.put(Element.NONE,              0.0f)
        water.put(Element.EARTH,             0.0f)
        water.put(Element.WATER,            -1.5f)
        water.put(Element.FIRE,              1.0f)
        water.put(Element.AIR,               0.0f)
        water.put(Element.FOREST,           -1.5f)
        water.put(Element.FROST,             0.0f)
        water.put(Element.SPIRIT,            0.0f)
        water.put(Element.DEMON,             0.0f)
        water.put(Element.ARTHROPODA,        0.0f)
        water.put(Element.MOUNTAIN,          0.5f)
        water.put(Element.LIGHTNING,        -0.5f)
        water.put(Element.LINDWORM,          0.0f)

        // AIR
        air = ArrayMap()
        air.put(Element.NONE,                0.0f)
        air.put(Element.EARTH,              -0.5f)
        air.put(Element.WATER,               0.0f)
        air.put(Element.FIRE,               -1.5f)
        air.put(Element.AIR,                 0.0f)
        air.put(Element.FOREST,              0.5f)
        air.put(Element.FROST,              -0.5f)
        air.put(Element.SPIRIT,              0.0f)
        air.put(Element.DEMON,               0.0f)
        air.put(Element.ARTHROPODA,          0.0f)
        air.put(Element.MOUNTAIN,            0.5f)
        air.put(Element.LIGHTNING,           0.0f)
        air.put(Element.LINDWORM,            0.0f)

        // Fire
        flame = ArrayMap()
        flame.put(Element.NONE,              0.0f)
        flame.put(Element.EARTH,            -0.5f)
        flame.put(Element.WATER,            -0.5f)
        flame.put(Element.FIRE,              0.0f)
        flame.put(Element.AIR,               0.0f)
        flame.put(Element.FOREST,            0.5f)
        flame.put(Element.FROST,             0.0f)
        flame.put(Element.SPIRIT,            0.0f)
        flame.put(Element.DEMON,             0.0f)
        flame.put(Element.ARTHROPODA,        1.0f)
        flame.put(Element.MOUNTAIN,          0.0f)
        flame.put(Element.LIGHTNING,         0.0f)
        flame.put(Element.LINDWORM,         -0.5f)

        // Forest
        forest = ArrayMap()
        forest.put(Element.NONE,             0.0f)
        forest.put(Element.EARTH,            0.5f)
        forest.put(Element.WATER,            0.5f)
        forest.put(Element.FIRE,            -0.5f)
        forest.put(Element.AIR,              0.0f)
        forest.put(Element.FOREST,           0.0f)
        forest.put(Element.FROST,           -0.5f)
        forest.put(Element.SPIRIT,           0.0f)
        forest.put(Element.DEMON,            0.0f)
        forest.put(Element.ARTHROPODA,       0.0f)
        forest.put(Element.MOUNTAIN,         0.0f)
        forest.put(Element.LIGHTNING,       -0.5f)
        forest.put(Element.LINDWORM,         0.0f)

        // Frost
        frost = ArrayMap()
        frost.put(Element.NONE,              0.0f)
        frost.put(Element.EARTH,            -0.5f)
        frost.put(Element.WATER,            -1.5f)
        frost.put(Element.FIRE,             -2.0f)
        frost.put(Element.AIR,               0.0f)
        frost.put(Element.FOREST,            0.5f)
        frost.put(Element.FROST,            -1.5f)
        frost.put(Element.SPIRIT,           -0.5f)
        frost.put(Element.DEMON,             0.0f)
        frost.put(Element.ARTHROPODA,        1.0f)
        frost.put(Element.MOUNTAIN,          0.0f)
        frost.put(Element.LIGHTNING,         0.0f)
        frost.put(Element.LINDWORM,          0.5f)

        // Spirit
        spirit = ArrayMap()
        spirit.put(Element.NONE,             0.0f)
        spirit.put(Element.EARTH,            0.0f)
        spirit.put(Element.WATER,            0.0f)
        spirit.put(Element.FIRE,            -0.5f)
        spirit.put(Element.AIR,              0.0f)
        spirit.put(Element.FOREST,          -1.5f)
        spirit.put(Element.FROST,            0.5f)
        spirit.put(Element.SPIRIT,          -1.5f)
        spirit.put(Element.DEMON,            1.0f)
        spirit.put(Element.ARTHROPODA,       0.5f)
        spirit.put(Element.MOUNTAIN,         0.0f)
        spirit.put(Element.LIGHTNING,        0.0f)
        spirit.put(Element.LINDWORM,         0.0f)

        // Demon
        demon = ArrayMap()
        demon.put(Element.NONE,              0.0f)
        demon.put(Element.EARTH,             0.5f)
        demon.put(Element.WATER,             0.0f)
        demon.put(Element.FIRE,              0.0f)
        demon.put(Element.AIR,               0.5f)
        demon.put(Element.FOREST,            0.0f)
        demon.put(Element.FROST,             0.0f)
        demon.put(Element.SPIRIT,           -0.5f)
        demon.put(Element.DEMON,            -1.5f)
        demon.put(Element.ARTHROPODA,        0.5f)
        demon.put(Element.MOUNTAIN,          0.0f)
        demon.put(Element.LIGHTNING,        -0.5f)
        demon.put(Element.LINDWORM,          0.0f)

        // Arthropoda
        arthropoda = ArrayMap()
        arthropoda.put(Element.NONE,         0.0f)
        arthropoda.put(Element.EARTH,        0.0f)
        arthropoda.put(Element.WATER,        0.0f)
        arthropoda.put(Element.FIRE,         0.0f)
        arthropoda.put(Element.AIR,          0.0f)
        arthropoda.put(Element.FOREST,       0.0f)
        arthropoda.put(Element.FROST,        0.0f)
        arthropoda.put(Element.SPIRIT,       0.0f)
        arthropoda.put(Element.DEMON,        0.0f)
        arthropoda.put(Element.ARTHROPODA,   0.0f)
        arthropoda.put(Element.MOUNTAIN,     0.0f)
        arthropoda.put(Element.LIGHTNING,    0.0f)
        arthropoda.put(Element.LINDWORM,     0.0f)

        // Mountain
        mountain = ArrayMap()
        mountain.put(Element.NONE,           0.0f)
        mountain.put(Element.EARTH,          0.0f)
        mountain.put(Element.WATER,          0.0f)
        mountain.put(Element.FIRE,          -0.5f)
        mountain.put(Element.AIR,            0.0f)
        mountain.put(Element.FOREST,         1.0f)
        mountain.put(Element.FROST,         -0.5f)
        mountain.put(Element.SPIRIT,         0.5f)
        mountain.put(Element.DEMON,          0.0f)
        mountain.put(Element.ARTHROPODA,     0.0f)
        mountain.put(Element.MOUNTAIN,       0.0f)
        mountain.put(Element.LIGHTNING,      0.0f)
        mountain.put(Element.LINDWORM,      -0.5f)

        // Lightning
        lightning = ArrayMap()
        lightning.put(Element.NONE,          0.0f)
        lightning.put(Element.EARTH,        -1.0f)
        lightning.put(Element.WATER,         0.5f)
        lightning.put(Element.FIRE,          0.0f)
        lightning.put(Element.AIR,           0.5f)
        lightning.put(Element.FOREST,        0.5f)
        lightning.put(Element.FROST,         0.0f)
        lightning.put(Element.SPIRIT,        0.0f)
        lightning.put(Element.DEMON,         0.5f)
        lightning.put(Element.ARTHROPODA,    0.0f)
        lightning.put(Element.MOUNTAIN,     -0.5f)
        lightning.put(Element.LIGHTNING,    -1.5f)
        lightning.put(Element.LINDWORM,      0.0f)

        // Dragon
        lindworm = ArrayMap()
        lindworm.put(Element.NONE,           0.0f)
        lindworm.put(Element.EARTH,          0.5f)
        lindworm.put(Element.WATER,          0.0f)
        lindworm.put(Element.FIRE,          -0.5f)
        lindworm.put(Element.AIR,           -0.5f)
        lindworm.put(Element.FOREST,         0.5f)
        lindworm.put(Element.FROST,         -0.5f)
        lindworm.put(Element.SPIRIT,         0.0f)
        lindworm.put(Element.DEMON,          0.0f)
        lindworm.put(Element.ARTHROPODA,     0.5f)
        lindworm.put(Element.MOUNTAIN,       0.0f)
        lindworm.put(Element.LIGHTNING,      0.0f)
        lindworm.put(Element.LINDWORM,       0.0f)

        // None
        none = ArrayMap()
        none.put(Element.NONE,               0.0f)
        none.put(Element.EARTH,              0.0f)
        none.put(Element.WATER,              0.0f)
        none.put(Element.FIRE,               0.0f)
        none.put(Element.AIR,                0.0f)
        none.put(Element.FOREST,             0.0f)
        none.put(Element.FROST,              0.0f)
        none.put(Element.SPIRIT,             0.0f)
        none.put(Element.DEMON,              0.0f)
        none.put(Element.ARTHROPODA,         0.0f)
        none.put(Element.MOUNTAIN,           0.0f)
        none.put(Element.LIGHTNING,          0.0f)
        none.put(Element.LINDWORM,           0.0f)
    }

    /**
     * Calculates the efficiency of an attack, based on the attack element and the defenders elements
     * @param attacker attackers element
     * @param defender defenders elements
     * @return efficiency ratio
     */
    fun getElemEff(attacker: Element, defender: Array<Element>): Float
    {
        var effectiveness = 1.0f
        val table = when (attacker)
        {
            Element.EARTH       -> earth
            Element.WATER       -> water
            Element.AIR         -> air
            Element.FIRE        -> flame
            Element.FOREST      -> forest
            Element.FROST       -> frost
            Element.SPIRIT      -> spirit
            Element.DEMON       -> demon
            Element.ARTHROPODA  -> arthropoda
            Element.MOUNTAIN    -> mountain
            Element.LIGHTNING   -> lightning
            Element.LINDWORM    -> lindworm
            else                -> none
        }

        for (e in defender) { effectiveness += table.get(e) }

        logInfo(TAG) { "$attacker on " }
        for (e in defender) { log() { "$e, " } }
        log() { ": $effectiveness" }

        // Healing effects are half as effective as hurting attacks
        if (effectiveness < 0) effectiveness /= 2f

        return effectiveness
    }
}
