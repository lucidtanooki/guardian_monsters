package de.limbusdev.guardianmonsters.fwmengine.battle.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.Element;

/**
 * Singleton
 * Created by georg on 07.09.16.
 */
public class ElemEff {

    private static ElemEff instance;

    public static ElemEff getInstance() {
        if(instance == null)
            instance = new ElemEff();
        return instance;
    }

    public static ElemEff singelton() {
        return getInstance();
    }

    /**
     * Calculates the efficiency of an attack, based on the attack element and the defenders elements
     * @param attacker attackers element
     * @param defender defenders elements
     * @return efficiency ratio
     */
    public float getElemEff(Element attacker, Array<Element> defender) {
        float effectiveness = 1.0f;
        ArrayMap<Element, Float> table;
        switch(attacker) {
            case EARTH: table = earth; break;
            case WATER: table = water; break;
            case AIR: table = air; break;
            case FIRE: table = flame; break;
            case FOREST: table = forest; break;
            case FROST: table = frost; break;
            case SPIRIT: table = spirit; break;
            case DEMON: table = demon; break;
            case ARTHROPODA: table = arthropoda; break;
            case MOUNTAIN: table = mountain; break;
            case LIGHTNING: table = lightning; break;
            case LINDWORM: table = lindworm; break;
            default: table = none; break;
        }

        for(Element e : defender) {
            effectiveness += table.get(e);
        }

        System.out.print("Effectiveness of " + attacker + " on ");
        for(Element e : defender) {
            System.out.print(e + ", ");
        }
        System.out.println(": " + effectiveness);

        // Healing effects are half as effective as hurting attacks
        if(effectiveness < 0) effectiveness = effectiveness/2f;

        return effectiveness;
    }

    private static ArrayMap<Element, Float> none, earth, water, flame, air, frost, mountain, arthropoda, demon, lindworm, forest, lightning, spirit;

    private ElemEff() {
        // Earth
        earth = new ArrayMap<Element, Float>();
        earth.put(Element.NONE,         0f);
        earth.put(Element.EARTH,        0f);
        earth.put(Element.WATER,        0f);
        earth.put(Element.FIRE,         .5f);
        earth.put(Element.AIR,          .5f);
        earth.put(Element.FOREST,       -.5f);
        earth.put(Element.FROST,        0f);
        earth.put(Element.SPIRIT,       .5f);
        earth.put(Element.DEMON,        0f);
        earth.put(Element.ARTHROPODA,   0f);
        earth.put(Element.MOUNTAIN,     0f);
        earth.put(Element.LIGHTNING,    1f);
        earth.put(Element.LINDWORM,       0f);

        // Water
        water = new ArrayMap<Element, Float>();
        water.put(Element.NONE, 0f);
        water.put(Element.EARTH, 0f);
        water.put(Element.WATER, -1.5f);
        water.put(Element.FIRE, 1f);
        water.put(Element.AIR, 0f);
        water.put(Element.FOREST, -1.5f);
        water.put(Element.FROST, 0f);
        water.put(Element.SPIRIT, 0f);
        water.put(Element.DEMON, 0f);
        water.put(Element.ARTHROPODA, 0f);
        water.put(Element.MOUNTAIN, .5f);
        water.put(Element.LIGHTNING, -.5f);
        water.put(Element.LINDWORM, 0f);

        // AIR
        air = new ArrayMap<Element, Float>();
        air.put(Element.NONE, 0f);
        air.put(Element.EARTH, -.5f);
        air.put(Element.WATER, 0f);
        air.put(Element.FIRE, -1.5f);
        air.put(Element.AIR, 0f);
        air.put(Element.FOREST, .5f);
        air.put(Element.FROST, -.5f);
        air.put(Element.SPIRIT, 0f);
        air.put(Element.DEMON, 0f);
        air.put(Element.ARTHROPODA, 0f);
        air.put(Element.MOUNTAIN, .5f);
        air.put(Element.LIGHTNING, 0f);
        air.put(Element.LINDWORM, 0f);

        // Fire
        flame = new ArrayMap<Element, Float>();
        flame.put(Element.NONE, 0f);
        flame.put(Element.EARTH, -.5f);
        flame.put(Element.WATER, -.5f);
        flame.put(Element.FIRE, 0f);
        flame.put(Element.AIR, 0f);
        flame.put(Element.FOREST, .5f);
        flame.put(Element.FROST, 0f);
        flame.put(Element.SPIRIT, 0f);
        flame.put(Element.DEMON, 0f);
        flame.put(Element.ARTHROPODA, 1f);
        flame.put(Element.MOUNTAIN, 0f);
        flame.put(Element.LIGHTNING, 0f);
        flame.put(Element.LINDWORM, -.5f);

        // Forest
        forest = new ArrayMap<Element, Float>();
        forest.put(Element.NONE, 0f);
        forest.put(Element.EARTH, .5f);
        forest.put(Element.WATER, .5f);
        forest.put(Element.FIRE, -.5f);
        forest.put(Element.AIR, 0f);
        forest.put(Element.FOREST, 0f);
        forest.put(Element.FROST, -.5f);
        forest.put(Element.SPIRIT, 0f);
        forest.put(Element.DEMON, 0f);
        forest.put(Element.ARTHROPODA, 0f);
        forest.put(Element.MOUNTAIN, 0f);
        forest.put(Element.LIGHTNING, -.5f);
        forest.put(Element.LINDWORM, 0f);

        // Frost
        frost = new ArrayMap<Element, Float>();
        frost.put(Element.NONE, 0f);
        frost.put(Element.EARTH, -.5f);
        frost.put(Element.WATER, -1.5f);
        frost.put(Element.FIRE, -5f);
        frost.put(Element.AIR, 0f);
        frost.put(Element.FOREST, .5f);
        frost.put(Element.FROST, -1.5f);
        frost.put(Element.SPIRIT, -.5f);
        frost.put(Element.DEMON, 0f);
        frost.put(Element.ARTHROPODA, 1f);
        frost.put(Element.MOUNTAIN, 0f);
        frost.put(Element.LIGHTNING, 0f);
        frost.put(Element.LINDWORM, .5f);

        // Spirit
        spirit = new ArrayMap<Element, Float>();
        spirit.put(Element.NONE,        0f);
        spirit.put(Element.EARTH,       0f);
        spirit.put(Element.WATER,       0f);
        spirit.put(Element.FIRE,        -.5f);
        spirit.put(Element.AIR,         0f);
        spirit.put(Element.FOREST,      -1.5f);
        spirit.put(Element.FROST,       .5f);
        spirit.put(Element.SPIRIT,      -1.5f);
        spirit.put(Element.DEMON,       1f);
        spirit.put(Element.ARTHROPODA,  .5f);
        spirit.put(Element.MOUNTAIN,    0f);
        spirit.put(Element.LIGHTNING,   0f);
        spirit.put(Element.LINDWORM,      0f);

        // Demon
        demon = new ArrayMap<Element, Float>();
        demon.put(Element.NONE,        0f);
        demon.put(Element.EARTH,       .5f);
        demon.put(Element.WATER,       0f);
        demon.put(Element.FIRE,        0f);
        demon.put(Element.AIR,         .5f);
        demon.put(Element.FOREST,      0f);
        demon.put(Element.FROST,       0f);
        demon.put(Element.SPIRIT,      -.5f);
        demon.put(Element.DEMON,       -1.5f);
        demon.put(Element.ARTHROPODA,  .5f);
        demon.put(Element.MOUNTAIN,    0f);
        demon.put(Element.LIGHTNING,   -.5f);
        demon.put(Element.LINDWORM,      0f);

        // Arthropoda
        arthropoda = new ArrayMap<Element, Float>();
        arthropoda.put(Element.NONE,        0f);
        arthropoda.put(Element.EARTH,       0f);
        arthropoda.put(Element.WATER,       0f);
        arthropoda.put(Element.FIRE,        0f);
        arthropoda.put(Element.AIR,         0f);
        arthropoda.put(Element.FOREST,      0f);
        arthropoda.put(Element.FROST,       0f);
        arthropoda.put(Element.SPIRIT,      0f);
        arthropoda.put(Element.DEMON,       0f);
        arthropoda.put(Element.ARTHROPODA,  0f);
        arthropoda.put(Element.MOUNTAIN,    0f);
        arthropoda.put(Element.LIGHTNING,   0f);
        arthropoda.put(Element.LINDWORM,      0f);

        // Mountain
        mountain = new ArrayMap<Element, Float>();
        mountain.put(Element.NONE,        0f);
        mountain.put(Element.EARTH,       0f);
        mountain.put(Element.WATER,       0f);
        mountain.put(Element.FIRE,        -.5f);
        mountain.put(Element.AIR,         0f);
        mountain.put(Element.FOREST,      1f);
        mountain.put(Element.FROST,       -.5f);
        mountain.put(Element.SPIRIT,      .5f);
        mountain.put(Element.DEMON,       0f);
        mountain.put(Element.ARTHROPODA,  0f);
        mountain.put(Element.MOUNTAIN,    0f);
        mountain.put(Element.LIGHTNING,   0f);
        mountain.put(Element.LINDWORM,      -.5f);

        // Lightning
        lightning = new ArrayMap<Element, Float>();
        lightning.put(Element.NONE,        0f);
        lightning.put(Element.EARTH,       -1f);
        lightning.put(Element.WATER,       .5f);
        lightning.put(Element.FIRE,        0f);
        lightning.put(Element.AIR,         .5f);
        lightning.put(Element.FOREST,      .5f);
        lightning.put(Element.FROST,       0f);
        lightning.put(Element.SPIRIT,      0f);
        lightning.put(Element.DEMON,       .5f);
        lightning.put(Element.ARTHROPODA,  0f);
        lightning.put(Element.MOUNTAIN,    -.5f);
        lightning.put(Element.LIGHTNING,   -1.5f);
        lightning.put(Element.LINDWORM,      0f);

        // Dragon
        lindworm = new ArrayMap<Element, Float>();
        lindworm.put(Element.NONE,        0f);
        lindworm.put(Element.EARTH,       .5f);
        lindworm.put(Element.WATER,       0f);
        lindworm.put(Element.FIRE,        -.5f);
        lindworm.put(Element.AIR,         -.5f);
        lindworm.put(Element.FOREST,      .5f);
        lindworm.put(Element.FROST,       -.5f);
        lindworm.put(Element.SPIRIT,      0f);
        lindworm.put(Element.DEMON,       0f);
        lindworm.put(Element.ARTHROPODA,  .5f);
        lindworm.put(Element.MOUNTAIN,    0f);
        lindworm.put(Element.LIGHTNING,   0f);
        lindworm.put(Element.LINDWORM,      0f);

        // None
        none = new ArrayMap<Element, Float>();
        none.put(Element.NONE,        0f);
        none.put(Element.EARTH,       0f);
        none.put(Element.WATER,       0f);
        none.put(Element.FIRE,        0f);
        none.put(Element.AIR,         0f);
        none.put(Element.FOREST,      0f);
        none.put(Element.FROST,       0f);
        none.put(Element.SPIRIT,      0f);
        none.put(Element.DEMON,       0f);
        none.put(Element.ARTHROPODA,  0f);
        none.put(Element.MOUNTAIN,    0f);
        none.put(Element.LIGHTNING,   0f);
        none.put(Element.LINDWORM,      0f);
    }
}
