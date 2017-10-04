package de.limbusdev.guardianmonsters.guardians;

import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.abilities.AbilityService;
import de.limbusdev.guardianmonsters.guardians.items.ItemService;
import de.limbusdev.guardianmonsters.guardians.monsters.SpeciesDescriptionService;

/**
 * ModuleGuardians
 *
 * @author Georg Eckert 2017
 */

public class ModuleGuardians
{
    public static void initModule()
    {
        ArrayMap<Element, String> jsonPaths = new ArrayMap<>();
        jsonPaths.put(Element.NONE, "data/abilitiesNone.json");
        jsonPaths.put(Element.EARTH, "data/abilitiesEarth.json");
        jsonPaths.put(Element.FIRE, "data/abilitiesFire.json");
        jsonPaths.put(Element.WATER, "data/abilitiesWater.json");
        GuardiansServiceLocator.provide(AbilityService.getInstanceFromFile(jsonPaths));

        GuardiansServiceLocator.provide(ItemService.getInstanceFromFile("data/items.xml"));

        GuardiansServiceLocator.provide(SpeciesDescriptionService.getInstanceFromFile("data/guardians.xml"));
    }
}
