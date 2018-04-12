package de.limbusdev.guardianmonsters.guardians;

import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.abilities.AbilityService;
import de.limbusdev.guardianmonsters.guardians.items.ItemService;
import de.limbusdev.guardianmonsters.guardians.monsters.GuardianFactory;
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

        ArrayMap<String,String> jsonItemPaths = new ArrayMap<>();
        jsonItemPaths.put("itemsKey",       "data/itemsKey.json");
        jsonItemPaths.put("itemsMedicine",  "data/itemsMedicine.json");
        jsonItemPaths.put("itemsEquipment", "data/itemsEquipment.json");
        GuardiansServiceLocator.provide(ItemService.getInstanceFromFiles(jsonItemPaths));

        GuardiansServiceLocator.provide(SpeciesDescriptionService.getInstanceFromFile("data/guardians.json"));

        GuardiansServiceLocator.provide(GuardianFactory.getInstance());
    }

    public static void initModuleForTesting()
    {
        // ......................................................................................... init abilities
        ArrayMap<Element, String> jsonStrings = new ArrayMap<>();
        String testJson = "[" +
            "{\"ID\":1,\"element\":\"none\",\"name\":\"attNone1_selfdef\",\"damage\":0,\"MPcost\":0,\"damageType\":\"physical\"}," +
            "{\"ID\":2,\"element\":\"none\",\"name\":\"attNone2_kick\",\"damage\":50,\"MPcost\":0,\"damageType\":\"physical\"}]";
        jsonStrings.put(Element.NONE, testJson);

        testJson = "[" +
            "{\"ID\":1,\"element\":\"earth\",\"name\":\"attEarth1_dirt\",\"damage\":5,\"MPcost\":0,\"damageType\":\"physical\"}," +
            "{\"ID\":2,\"element\":\"earth\",\"name\":\"attEarth2_mud\",\"damage\":10,\"MPcost\":0,\"damageType\":\"physical\"}," +
            "{\"ID\":3,\"element\":\"earth\",\"name\":\"attEarth3_stones\",\"damage\":20,\"MPcost\":0,\"damageType\":\"physical\"}," +
            "{\"ID\":4,\"element\":\"earth\",\"name\":\"attEarth4_landslide\",\"damage\":40,\"MPcost\":0,\"damageType\":\"physical\"}]";
        jsonStrings.put(Element.EARTH, testJson);

        testJson = "[" +
            "{\"ID\":1,\"element\":\"fire\",\"name\":\"attFire1_embers\",\"damage\":5,\"MPcost\":1,\"damageType\":\"magical\"}," +
            "{\"ID\":2,\"element\":\"fire\",\"name\":\"attFire2_fire\",\"damage\":\"10\",\"MPcost\":2,\"damageType\":\"magical\"}," +
            "{\"ID\":3,\"element\":\"fire\",\"name\":\"attFire3_flame\",\"damage\":20,\"MPcost\":3,\"damageType\":\"magical\"}," +
            "{\"ID\":4,\"element\":\"fire\",\"name\":\"attFire4_blaze\",\"damage\":40,\"MPcost\":4,\"damageType\":\"magical\"}]";
        jsonStrings.put(Element.FIRE, testJson);

        testJson = "[" +
            "{\"ID\":1,\"element\":\"water\",\"name\":\"attWater1_sprinkle\",\"damage\":5,\"MPcost\":0,\"damageType\":\"physical\"}," +
            "{\"ID\":2,\"element\":\"water\",\"name\":\"attWater2_shower\",\"damage\":10,\"MPcost\":0,\"damageType\":\"physical\"}," +
            "{\"ID\":3,\"element\":\"water\",\"name\":\"attWater3_splash\",\"damage\":20,\"MPcost\":0,\"damageType\":\"physical\"}," +
            "{\"ID\":4,\"element\":\"water\",\"name\":\"attWater4_waterjet\",\"damage\":40,\"MPcost\":0,\"damageType\":\"physical\"}]";
        jsonStrings.put(Element.WATER,testJson);

        GuardiansServiceLocator.provide(AbilityService.getInstance(jsonStrings));


        // ......................................................................................... init guardians
        String json = "{\"guardians\":[" +
            "{\"id\":1,\"metamorphosisNodes\":[91,92]," +
            "\"abilities\":[" +
            "{\"abilityID\":2,\"element\":\"none\",\"abilityPos\":0}," +
            "{\"abilityID\":2,\"element\":\"earth\",\"abilityPos\":13}," +
            "{\"abilityID\":3,\"element\":\"earth\",\"abilityPos\":11}," +
            "{\"abilityID\":4,\"element\":\"earth\",\"abilityPos\":15}]," +
            "\"basestats\":{\"hp\":300,\"mp\":50,\"speed\":10,\"pstr\":10,\"pdef\":10,\"mstr\":10,\"mdef\":10}," +
            "\"equipmentCompatibility\":{\"head\":\"bridle\",\"hands\":\"claws\",\"body\":\"barding\",\"feet\":\"shinprotection\"}," +
            "\"abilityGraphEquip\":{\"head\":21,\"hands\":23,\"body\":89,\"feet\":90}," +
            "\"metaForms\":[" +
            "{\"form\":0,\"nameID\":\"gm001_0_fordin\",\"elements\":[\"earth\"]}," +
            "{\"form\":1,\"nameID\":\"gm001_1_stegofor\",\"elements\":[\"earth\",\"forest\"]}," +
            "{\"form\":2,\"nameID\":\"gm001_2_brachifor\",\"elements\":[\"earth\",\"forest\"]}]}]}";
        GuardiansServiceLocator.provide(SpeciesDescriptionService.getInstance(json));


        // ......................................................................................... init items
        ArrayMap<String,String> jsonItemStrings = new ArrayMap<>();

        jsonItemStrings.put("itemsKey",
            "{\"items\":[" +
                "{\"nameID\":\"relict-earth\",\"category\":\"key\"}," +
                "{\"nameID\":\"relict-flame\",\"category\":\"key\"}]}");

        jsonItemStrings.put("itemsMedicine",
            "{\"items\":[" +
                "{\"nameID\":\"bread\",\"value\":100,\"type\":\"HPcure\",\"category\":\"medicine\"}," +
                "{\"nameID\":\"medicine-blue\",\"value\":10,\"type\":\"MPcure\",\"category\":\"medicine\"}," +
                "{\"nameID\":\"angel-tear\",\"value\":50,\"type\":\"revive\",\"category\":\"medicine\"}]}");

        jsonItemStrings.put("itemsEquipment",
            "{\"items\":[" +
                "{\"nameID\":\"sword-wood\",\"body-part\":\"hands\",\"type\":\"sword\"," +
                "\"addsPStr\":1,\"addsPDef\":0,\"addsMStr\":0,\"addsMDef\":0,\"addsSpeed\":0," +
                "\"category\":\"equipment\"}," +
                "{\"nameID\":\"sword-rusty\",\"body-part\":\"hands\",\"type\":\"sword\"," +
                "\"addsPStr\":2,\"addsPDef\":0,\"addsMStr\":0,\"addsMDef\":0,\"addsSpeed\":0," +
                "\"category\":\"equipment\"}]}");

        GuardiansServiceLocator.provide(ItemService.getInstance(jsonItemStrings));

        GuardiansServiceLocator.provide(GuardianFactory.getInstance());

    }

    public static void destroyModule()
    {
        GuardiansServiceLocator.destroy();
    }
}
