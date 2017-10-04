package de.limbusdev.guardianmonsters.guardians.abilities;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;

import de.limbusdev.guardianmonsters.guardians.Element;
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;

/**
 * Contains all existing attacks, sorted by element
 * @author Georg Eckert 2017
 */
public class AbilityService implements IAbilityService
{
    private static AbilityService instance;
    private static ArrayMap<Element, ArrayMap<Integer, Ability>> abilities;

    private AbilityService(ArrayMap<Element,String> jsonAbilitiesResources)
    {
        abilities = new ArrayMap<>();

        for(Element key : jsonAbilitiesResources.keys())
        {
            ArrayMap<Integer,Ability> elAbilities = readAbilitiesFromJsonString(jsonAbilitiesResources.get(key));
            abilities.put(key, elAbilities);
        }
    }

    private static ArrayMap<Integer, Ability> readAbilitiesFromJsonString(String jsonString)
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

    /**
     * Best practice: Use only once, when providing {@link IAbilityService} to
     * {@link GuardiansServiceLocator}, afterwards always retrieve it from there.
     *
     * @param jsonAbilitiesResources
     * @return
     */
    public static synchronized AbilityService getInstance(ArrayMap<Element,String> jsonAbilitiesResources)
    {
        if(instance == null) {
            instance = new AbilityService(jsonAbilitiesResources);
        }
        return instance;
    }

    /**
     * Best practice: Use only once, when providing {@link IAbilityService} to
     * {@link GuardiansServiceLocator}, afterwards always retrieve it from there.
     *
     * @param jsonFilePaths Files that contain Abilities in Json format
     * @return
     */
    public static synchronized AbilityService getInstanceFromFile(ArrayMap<Element,String> jsonFilePaths)
    {
        ArrayMap<Element,String> jsonResources = new ArrayMap<>();
        for(Element key : jsonFilePaths.keys()) {

            FileHandle handleJson = Gdx.files.internal(jsonFilePaths.get(key));
            String jsonString = handleJson.readString();
            jsonResources.put(key, jsonString);
        }

        return getInstance(jsonResources);
    }

    /**
     * Returns attack of the given element and index
     * @param e
     * @param index
     * @return
     */
    @Override
    public Ability getAbility(Element e, int index)
    {
        return abilities.get(e).get(index);
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