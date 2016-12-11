package de.limbusdev.guardianmonsters.fwmengine.battle.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.enums.Element;

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
     * Calculates the effectiveness of an attack, based on the attack element and the defenders elements
     * @param attacker attackers element
     * @param defender defenders elements
     * @return effectiveness ratio
     */
    public float getElemEff(Element attacker, Array<Element> defender) {
        float effectiveness = 1.0f;
        ArrayMap<Element, Float> table;
        switch(attacker) {
            case EARTH: table = earth; break;
            case WATER: table = water; break;
            case AIR: table = air; break;
            case FIRE: table = fire; break;
            case FOREST: table = forest; break;
            case FROST: table = frost; break;
            case SPIRIT: table = spirit; break;
            case DEMON: table = demon; break;
            case ARTHROPODA: table = arthropoda; break;
            case MOUNTAIN: table = mountain; break;
            case LIGHTNING: table = lightning; break;
            case DRAGON: table = dragon; break;
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

    private static ArrayMap<Element, Float> none, earth, water, fire, air, frost, mountain, arthropoda, demon, dragon, forest, lightning, spirit;

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
        earth.put(Element.DRAGON,       0f);

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
        water.put(Element.DRAGON, 0f);

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
        air.put(Element.DRAGON, 0f);

        // Fire
        fire = new ArrayMap<Element, Float>();
        fire.put(Element.NONE, 0f);
        fire.put(Element.EARTH, -.5f);
        fire.put(Element.WATER, -.5f);
        fire.put(Element.FIRE, 0f);
        fire.put(Element.AIR, 0f);
        fire.put(Element.FOREST, .5f);
        fire.put(Element.FROST, 0f);
        fire.put(Element.SPIRIT, 0f);
        fire.put(Element.DEMON, 0f);
        fire.put(Element.ARTHROPODA, 1f);
        fire.put(Element.MOUNTAIN, 0f);
        fire.put(Element.LIGHTNING, 0f);
        fire.put(Element.DRAGON, -.5f);

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
        forest.put(Element.DRAGON, 0f);

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
        frost.put(Element.DRAGON, .5f);

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
        spirit.put(Element.DRAGON,      0f);

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
        demon.put(Element.DRAGON,      0f);

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
        arthropoda.put(Element.DRAGON,      0f);

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
        mountain.put(Element.DRAGON,      -.5f);

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
        lightning.put(Element.DRAGON,      0f);

        // Dragon
        dragon = new ArrayMap<Element, Float>();
        dragon.put(Element.NONE,        0f);
        dragon.put(Element.EARTH,       .5f);
        dragon.put(Element.WATER,       0f);
        dragon.put(Element.FIRE,        -.5f);
        dragon.put(Element.AIR,         -.5f);
        dragon.put(Element.FOREST,      .5f);
        dragon.put(Element.FROST,       -.5f);
        dragon.put(Element.SPIRIT,      0f);
        dragon.put(Element.DEMON,       0f);
        dragon.put(Element.ARTHROPODA,  .5f);
        dragon.put(Element.MOUNTAIN,    0f);
        dragon.put(Element.LIGHTNING,   0f);
        dragon.put(Element.DRAGON,      0f);

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
        none.put(Element.DRAGON,      0f);
    }
}
