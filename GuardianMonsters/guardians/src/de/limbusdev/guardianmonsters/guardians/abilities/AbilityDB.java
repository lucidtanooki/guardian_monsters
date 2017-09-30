package de.limbusdev.guardianmonsters.guardians.abilities;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;

import de.limbusdev.guardianmonsters.guardians.Element;

/**
 * Contains all existing attacks, sorted by element
 * @author Georg Eckert 2017
 */
public class AbilityDB
{
    private static AbilityDB instance;
    private ArrayMap<Element, ArrayMap<Integer, Ability>> abilities;

    private AbilityDB()
    {
        abilities = new ArrayMap<>();

        String[] elements = {"None", "Fire", "Earth", "Water"};
        for(String el : elements)
        {
            FileHandle handleJson = Gdx.files.internal("data/abilities" + el + ".json");
            String jsonString = handleJson.readString();
            ArrayMap<Integer,Ability> elAbilities = readAbilitiesFromJsonString(jsonString);
            abilities.put(Element.valueOf(el.toUpperCase()), elAbilities);
        }
    }

    public static ArrayMap<Integer, Ability> readAbilitiesFromJsonString(String jsonString)
    {
        ArrayMap<Integer,Ability> elAbilities = new ArrayMap<>();
        Json json = new Json();
        ArrayList<JsonValue> elementList = json.fromJson(ArrayList.class, jsonString);

        JsonAbility jsa;
        Ability ability;
        for (JsonValue v : elementList) {
            jsa = json.readValue(JsonAbility.class, v);
            ability = new Ability(
                jsa.ID,
                Ability.DamageType.valueOf(jsa.damageType.toUpperCase()),
                Element.valueOf(jsa.element.toUpperCase()),
                jsa.damage,
                jsa.name,
                jsa.MPcost
            );
            elAbilities.put(ability.ID, ability);
        }

        return elAbilities;
    }

    public static synchronized AbilityDB getInstance()
    {
        if(instance == null) {
            instance = new AbilityDB();
        }
        return instance;
    }

    /**
     * Returns attack of the given element and index
     * @param e
     * @param index
     * @return
     */
    public static Ability getAbility(Element e, int index)
    {
        AbilityDB db = getInstance();
        return db.abilities.get(e).get(index);
    }

    /**
     * Simple Container for JSON parsed Object
     */
    private static class JsonAbility
    {
        public int ID;
        public String name;
        public int damage;
        public int MPcost;
        public String damageType;
        public String element;

        @Override
        public String toString()
        {
            String out = "Ability:\n";
            out += ID + " " + name + "\n" + "Damage: " + damage;
            out += " " + " MPcost: " + MPcost;
            return out;
        }
    }
}