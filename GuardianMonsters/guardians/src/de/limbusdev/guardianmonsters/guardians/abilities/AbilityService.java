package de.limbusdev.guardianmonsters.guardians.abilities;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;

import de.limbusdev.guardianmonsters.guardians.Element;
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics;

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

    @SuppressWarnings("unchecked")
    private static ArrayMap<Integer, Ability> readAbilitiesFromJsonString(String jsonString)
    {
        ArrayMap<Integer,Ability> elAbilities = new ArrayMap<>();
        Json json = new Json();

        ArrayList<JsonValue> elementList;

        if(json.fromJson(ArrayList.class, jsonString) != null)
            elementList = json.fromJson(ArrayList.class, jsonString);
        else
            elementList = new ArrayList<>();

        JsonAbility jsa;
        Ability ability;
        for (JsonValue v : elementList)
        {
            jsa = json.readValue(JsonAbility.class, v);
            ability = new Ability(
                jsa.ID,
                Ability.DamageType.valueOf(jsa.damageType.toUpperCase()),
                Element.valueOf(jsa.element.toUpperCase()),
                jsa.damage,
                jsa.name,
                jsa.MPcost,
                jsa.areaDamage,
                jsa.canChangeStatusEffect,
                IndividualStatistics.StatusEffect.valueOf(jsa.statusEffect.toUpperCase()),
                jsa.probabilityToChangeStatusEffect,
                jsa.modifiedStats.changesStats(),
                jsa.modifiedStats.PStr,
                jsa.modifiedStats.PDef,
                jsa.modifiedStats.MStr,
                jsa.modifiedStats.MDef,
                jsa.modifiedStats.Speed,
                jsa.healedStats.curesStats(),
                jsa.healedStats.HP,
                jsa.healedStats.MP
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
        for(Element key : jsonFilePaths.keys())
        {
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

    @Override
    public Ability getAbility(Ability.aID aID)
    {
        return getAbility(aID.element, aID.ID);
    }

    @Override
    public void destroy()
    {
        instance = null;
    }

    /**
     * Simple Container for JSON parsed Object
     */
    private static class JsonAbility
    {
        private static class JsonAbilityModifiedStats
        {
            public int PStr, PDef, MStr, MDef, Speed;
            public boolean changesStats() {return (PStr!=0 || PDef!=0 || MStr!=0 || MDef!=0 || Speed!=0);}
        }

        private static class JsonAbilityHealedStats
        {
            public int HP, MP;
            public boolean curesStats() {return (HP != 0 || MP != 0);}
        }

        public int ID;
        public String name;
        public int damage;
        public int MPcost;
        public String damageType;
        public String element;
        public String statusEffect;
        public boolean canChangeStatusEffect;
        public int probabilityToChangeStatusEffect;
        public boolean areaDamage;

        public JsonAbilityModifiedStats modifiedStats;
        public JsonAbilityHealedStats healedStats;

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