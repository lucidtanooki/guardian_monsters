package de.limbusdev.guardianmonsters.guardians

import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.guardians.abilities.AbilityService
import de.limbusdev.guardianmonsters.guardians.items.ItemService
import de.limbusdev.guardianmonsters.guardians.monsters.GuardianFactory
import de.limbusdev.guardianmonsters.guardians.monsters.SpeciesDescriptionService

/**
 * ModuleGuardians
 *
 * @author Georg Eckert 2017
 */
object ModuleGuardians
{
    fun initModule()
    {
        val jsonPaths = ArrayMap<Element, String>()
        jsonPaths.put(Element.NONE, "data/abilitiesNone.json")
        jsonPaths.put(Element.EARTH, "data/abilitiesEarth.json")
        jsonPaths.put(Element.FIRE, "data/abilitiesFire.json")
        jsonPaths.put(Element.WATER, "data/abilitiesWater.json")
        GuardiansServiceLocator.provide(AbilityService.getInstanceFromFile(jsonPaths))

        val jsonItemPaths = ArrayMap<String, String>()
        jsonItemPaths.put("itemsKey", "data/itemsKey.json")
        jsonItemPaths.put("itemsMedicine", "data/itemsMedicine.json")
        jsonItemPaths.put("itemsChakraCrystals", "data/itemsChakraCrystals.json")
        jsonItemPaths.put("itemsEquipment", "data/itemsEquipment.json")
        GuardiansServiceLocator.provide(ItemService.getInstanceFromFiles(jsonItemPaths))

        GuardiansServiceLocator.provide(SpeciesDescriptionService.getInstanceFromFile("data/guardians.json"))

        GuardiansServiceLocator.provide(GuardianFactory)
    }

    fun initModuleForTesting()
    {
        // ......................................................................................... init abilities
        val jsonStrings = ArrayMap<Element, String>()
        var testJson = "[\n" +
                "  {\n" +
                "    \"ID\": 1,\n" +
                "    \"element\": \"none\",\n" +
                "    \"name\": \"attNone1_selfdef\",\n" +
                "    \"damage\": 0,\n" +
                "    \"MPcost\": 0,\n" +
                "    \"damageType\": \"physical\",\n" +
                "    \"canChangeStatusEffect\": false,\n" +
                "    \"statusEffect\": \"healthy\",\n" +
                "    \"probabilityToChangeStatusEffect\": 0,\n" +
                "    \"areaDamage\": false,\n" +
                "    \"modifiedStats\": {\"PStr\": 0, \"PDef\": 0, \"MStr\": 0, \"MDef\": 0, \"Speed\": 0},\n" +
                "    \"healedStats\": {\"HP\": 0, \"MP\": 0}\n" +
                "  },\n" +
                "  {\n" +
                "    \"ID\": 2,\n" +
                "    \"element\": \"none\",\n" +
                "    \"name\": \"attNone2_kick\",\n" +
                "    \"damage\": 30,\n" +
                "    \"MPcost\": 0,\n" +
                "    \"damageType\": \"physical\",\n" +
                "    \"canChangeStatusEffect\": false,\n" +
                "    \"statusEffect\": \"healthy\",\n" +
                "    \"probabilityToChangeStatusEffect\": 100,\n" +
                "    \"areaDamage\": false,\n" +
                "    \"modifiedStats\": {\"PStr\": 0, \"PDef\": 0, \"MStr\": 0, \"MDef\": 0, \"Speed\": 0},\n" +
                "    \"healedStats\": {\"HP\": 0, \"MP\": 0}\n" +
                "  }" +
                "]"
        jsonStrings.put(Element.NONE, testJson)

        testJson = "[\n" +
                "  {\n" +
                "    \"ID\": 1,\n" +
                "    \"element\": \"earth\",\n" +
                "    \"name\": \"attEarth1_dirt\",\n" +
                "    \"damage\": 5,\n" +
                "    \"MPcost\": 0,\n" +
                "    \"damageType\": \"physical\",\n" +
                "    \"canChangeStatusEffect\": false,\n" +
                "    \"statusEffect\": \"healthy\",\n" +
                "    \"probabilityToChangeStatusEffect\": 0,\n" +
                "    \"areaDamage\": false,\n" +
                "    \"modifiedStats\": {\"PStr\": 0, \"PDef\": 0, \"MStr\": 0, \"MDef\": 0, \"Speed\": 0},\n" +
                "    \"healedStats\": {\"HP\": 0, \"MP\": 0}\n" +
                "  },\n" +
                "  {\n" +
                "    \"ID\": 2,\n" +
                "    \"element\": \"earth\",\n" +
                "    \"name\": \"attEarth2_mud\",\n" +
                "    \"damage\": 10,\n" +
                "    \"MPcost\": 0,\n" +
                "    \"damageType\": \"physical\",\n" +
                "    \"canChangeStatusEffect\": false,\n" +
                "    \"statusEffect\": \"healthy\",\n" +
                "    \"probabilityToChangeStatusEffect\": 0,\n" +
                "    \"areaDamage\": false,\n" +
                "    \"modifiedStats\": {\"PStr\": 0, \"PDef\": 0, \"MStr\": 0, \"MDef\": 0, \"Speed\": 0},\n" +
                "    \"healedStats\": {\"HP\": 0, \"MP\": 0}\n" +
                "  },\n" +
                "  {\n" +
                "    \"ID\": 3,\n" +
                "    \"element\": \"earth\",\n" +
                "    \"name\": \"attEarth3_stones\",\n" +
                "    \"damage\": 20,\n" +
                "    \"MPcost\": 0,\n" +
                "    \"damageType\": \"physical\",\n" +
                "    \"canChangeStatusEffect\": false,\n" +
                "    \"statusEffect\": \"healthy\",\n" +
                "    \"probabilityToChangeStatusEffect\": 0,\n" +
                "    \"areaDamage\": false,\n" +
                "    \"modifiedStats\": {\"PStr\": 0, \"PDef\": 0, \"MStr\": 0, \"MDef\": 0, \"Speed\": 0},\n" +
                "    \"healedStats\": {\"HP\": 0, \"MP\": 0}\n" +
                "  },\n" +
                "  {\n" +
                "    \"ID\": 4,\n" +
                "    \"element\": \"earth\",\n" +
                "    \"name\": \"attEarth4_landslide\",\n" +
                "    \"damage\": 40,\n" +
                "    \"MPcost\": 0,\n" +
                "    \"damageType\": \"physical\",\n" +
                "    \"canChangeStatusEffect\": false,\n" +
                "    \"statusEffect\": \"healthy\",\n" +
                "    \"probabilityToChangeStatusEffect\": 0,\n" +
                "    \"areaDamage\": false,\n" +
                "    \"modifiedStats\": {\"PStr\": 0, \"PDef\": 0, \"MStr\": 0, \"MDef\": 0, \"Speed\": 0},\n" +
                "    \"healedStats\": {\"HP\": 0, \"MP\": 0}\n" +
                "  }\n" +
                "]"
        jsonStrings.put(Element.EARTH, testJson)

        testJson = "[\n" +
                "  {\n" +
                "    \"ID\": 1,\n" +
                "    \"element\": \"fire\",\n" +
                "    \"name\": \"attFire1_embers\",\n" +
                "    \"damage\": 5,\n" +
                "    \"MPcost\": 1,\n" +
                "    \"damageType\": \"magical\",\n" +
                "    \"canChangeStatusEffect\": false,\n" +
                "    \"statusEffect\": \"healthy\",\n" +
                "    \"modifiedStats\": {\"PStr\": 0, \"PDef\": 0, \"MStr\": 0, \"MDef\": 0, \"Speed\": 0},\n" +
                "    \"healedStats\": {\"HP\": 0, \"MP\": 0}\n" +
                "  },\n" +
                "  {\n" +
                "    \"ID\": 2,\n" +
                "    \"element\": \"fire\",\n" +
                "    \"name\": \"attFire2_fire\",\n" +
                "    \"damage\": \"10\",\n" +
                "    \"MPcost\": 2,\n" +
                "    \"damageType\": \"magical\",\n" +
                "    \"canChangeStatusEffect\": false,\n" +
                "    \"statusEffect\": \"healthy\",\n" +
                "    \"modifiedStats\": {\"PStr\": 0, \"PDef\": 0, \"MStr\": 0, \"MDef\": 0, \"Speed\": 0},\n" +
                "    \"healedStats\": {\"HP\": 0, \"MP\": 0}\n" +
                "  },\n" +
                "  {\n" +
                "    \"ID\": 3,\n" +
                "    \"element\": \"fire\",\n" +
                "    \"name\": \"attFire3_flame\",\n" +
                "    \"damage\": 20,\n" +
                "    \"MPcost\": 3,\n" +
                "    \"damageType\": \"magical\",\n" +
                "    \"canChangeStatusEffect\": false,\n" +
                "    \"statusEffect\": \"healthy\",\n" +
                "    \"probabilityToChangeStatusEffect\": 0,\n" +
                "    \"areaDamage\": false,\n" +
                "    \"modifiedStats\": {\"PStr\": 0, \"PDef\": 0, \"MStr\": 0, \"MDef\": 0, \"Speed\": 0},\n" +
                "    \"healedStats\": {\"HP\": 0, \"MP\": 0}\n" +
                "  },\n" +
                "  {\n" +
                "    \"ID\": 4,\n" +
                "    \"element\": \"fire\",\n" +
                "    \"name\": \"attFire4_blaze\",\n" +
                "    \"damage\": 40,\n" +
                "    \"MPcost\": 4,\n" +
                "    \"damageType\": \"magical\",\n" +
                "    \"canChangeStatusEffect\": false,\n" +
                "    \"statusEffect\": \"healthy\",\n" +
                "    \"probabilityToChangeStatusEffect\": 0,\n" +
                "    \"areaDamage\": false,\n" +
                "    \"modifiedStats\": {\"PStr\": 0, \"PDef\": 0, \"MStr\": 0, \"MDef\": 0, \"Speed\": 0},\n" +
                "    \"healedStats\": {\"HP\": 0, \"MP\": 0}\n" +
                "  },\n" +
                "  {\n" +
                "    \"ID\": 5,\n" +
                "    \"element\": \"fire\",\n" +
                "    \"name\": \"attFire5_hellfire\",\n" +
                "    \"damage\": 80,\n" +
                "    \"MPcost\": 20,\n" +
                "    \"damageType\": \"magical\",\n" +
                "    \"canChangeStatusEffect\": false,\n" +
                "    \"statusEffect\": \"healthy\",\n" +
                "    \"probabilityToChangeStatusEffect\": 0,\n" +
                "    \"areaDamage\": true,\n" +
                "    \"modifiedStats\": {\"PStr\": 0, \"PDef\": 0, \"MStr\": 0, \"MDef\": 0, \"Speed\": 0},\n" +
                "    \"healedStats\": {\"HP\": 0, \"MP\": 0}\n" +
                "  }\n" +
                "]"
        jsonStrings.put(Element.FIRE, testJson)

        testJson = "[\n" +
                "  {\n" +
                "    \"ID\": 1,\n" +
                "    \"element\": \"water\",\n" +
                "    \"name\": \"attWater1_sprinkle\",\n" +
                "    \"damage\": 5,\n" +
                "    \"MPcost\": 0,\n" +
                "    \"damageType\": \"physical\",\n" +
                "    \"canChangeStatusEffect\": false,\n" +
                "    \"statusEffect\": \"healthy\",\n" +
                "    \"probabilityToChangeStatusEffect\": 0,\n" +
                "    \"areaDamage\": false,\n" +
                "    \"modifiedStats\": {\"PStr\": 0, \"PDef\": 0, \"MStr\": 0, \"MDef\": 0, \"Speed\": 0},\n" +
                "    \"healedStats\": {\"HP\": 0, \"MP\": 0}\n" +
                "  },\n" +
                "  {\n" +
                "    \"ID\": 2,\n" +
                "    \"element\": \"water\",\n" +
                "    \"name\": \"attWater2_shower\",\n" +
                "    \"damage\": 10,\n" +
                "    \"MPcost\": 0,\n" +
                "    \"damageType\": \"physical\",\n" +
                "    \"canChangeStatusEffect\": false,\n" +
                "    \"statusEffect\": \"healthy\",\n" +
                "    \"probabilityToChangeStatusEffect\": 0,\n" +
                "    \"areaDamage\": false,\n" +
                "    \"modifiedStats\": {\"PStr\": 0, \"PDef\": 0, \"MStr\": 0, \"MDef\": 0, \"Speed\": 0},\n" +
                "    \"healedStats\": {\"HP\": 0, \"MP\": 0}\n" +
                "  },\n" +
                "  {\n" +
                "    \"ID\": 3,\n" +
                "    \"element\": \"water\",\n" +
                "    \"name\": \"attWater3_splash\",\n" +
                "    \"damage\": 20,\n" +
                "    \"MPcost\": 0,\n" +
                "    \"damageType\": \"physical\",\n" +
                "    \"canChangeStatusEffect\": false,\n" +
                "    \"statusEffect\": \"healthy\",\n" +
                "    \"probabilityToChangeStatusEffect\": 0,\n" +
                "    \"areaDamage\": false,\n" +
                "    \"modifiedStats\": {\"PStr\": 0, \"PDef\": 0, \"MStr\": 0, \"MDef\": 0, \"Speed\": 0},\n" +
                "    \"healedStats\": {\"HP\": 0, \"MP\": 0}\n" +
                "  },\n" +
                "  {\n" +
                "    \"ID\": 4,\n" +
                "    \"element\": \"water\",\n" +
                "    \"name\": \"attWater4_waterjet\",\n" +
                "    \"damage\": 40,\n" +
                "    \"MPcost\": 0,\n" +
                "    \"damageType\": \"physical\",\n" +
                "    \"canChangeStatusEffect\": false,\n" +
                "    \"statusEffect\": \"healthy\",\n" +
                "    \"probabilityToChangeStatusEffect\": 0,\n" +
                "    \"areaDamage\": false,\n" +
                "    \"modifiedStats\": {\"PStr\": 0, \"PDef\": 0, \"MStr\": 0, \"MDef\": 0, \"Speed\": 0},\n" +
                "    \"healedStats\": {\"HP\": 0, \"MP\": 0}\n" +
                "  },\n" +
                "  {\n" +
                "    \"ID\": 5,\n" +
                "    \"element\": \"water\",\n" +
                "    \"name\": \"attWater5_icepillars\",\n" +
                "    \"damage\": 50,\n" +
                "    \"MPcost\": 0,\n" +
                "    \"damageType\": \"physical\",\n" +
                "    \"canChangeStatusEffect\": false,\n" +
                "    \"statusEffect\": \"healthy\",\n" +
                "    \"probabilityToChangeStatusEffect\": 0,\n" +
                "    \"areaDamage\": false,\n" +
                "    \"modifiedStats\": {\"PStr\": 0, \"PDef\": 0, \"MStr\": 0, \"MDef\": 0, \"Speed\": 0},\n" +
                "    \"healedStats\": {\"HP\": 0, \"MP\": 0}\n" +
                "  }\n" +
                "]"
        jsonStrings.put(Element.WATER, testJson)

        GuardiansServiceLocator.provide(AbilityService.getInstance(jsonStrings))

        // ......................................................................................... init guardians
        val json = "{\"guardians\":[" +
                "{\"id\":1,\"metamorphosisNodes\":[91,92]," +
                "\"abilities\":[" +
                "{\"abilityID\":2,\"element\":\"none\",\"abilityPos\":0}," +
                "{\"abilityID\":2,\"element\":\"earth\",\"abilityPos\":13}," +
                "{\"abilityID\":3,\"element\":\"earth\",\"abilityPos\":11}," +
                "{\"abilityID\":4,\"element\":\"earth\",\"abilityPos\":15}]," +
                "\"basestats\":{\"hp\":300,\"mp\":50,\"speed\":10,\"pstr\":11,\"pdef\":12,\"mstr\":13,\"mdef\":14}," +
                "\"equipmentCompatibility\":{\"head\":\"bridle\",\"hands\":\"claws\",\"body\":\"barding\",\"feet\":\"shinprotection\"}," +
                "\"abilityGraphEquip\":{\"head\":21,\"hands\":23,\"body\":89,\"feet\":90}," +
                "\"metaForms\":[" +
                "{\"form\":0,\"nameID\":\"gm001_0_fordin\",\"elements\":[\"earth\"]}," +
                "{\"form\":1,\"nameID\":\"gm001_1_stegofor\",\"elements\":[\"earth\",\"forest\"]}," +
                "{\"form\":2,\"nameID\":\"gm001_2_brachifor\",\"elements\":[\"earth\",\"forest\"]}]}," +
                "{\"id\":2,\"metamorphosisNodes\":[91,92]," +
                "\"abilities\":[" +
                "{\"abilityID\":2,\"element\":\"none\",\"abilityPos\":0}," +
                "{\"abilityID\":1,\"element\":\"water\",\"abilityPos\":5}]," +
                "\"basestats\":{\"hp\":300,\"mp\":50,\"speed\":10,\"pstr\":10,\"pdef\":10,\"mstr\":10,\"mdef\":10}," +
                "\"equipmentCompatibility\":{\"head\":\"helmet\",\"hands\":\"claws\",\"body\":\"breastplate\",\"feet\":\"kneepads\"}," +
                "\"abilityGraphEquip\":{\"head\":21,\"hands\":23,\"body\":89,\"feet\":90}," +
                "\"metaForms\":[" +
                "{\"form\":0,\"nameID\":\"gm002_0_kroki\",\"elements\":[\"water\"]}," +
                "{\"form\":1,\"nameID\":\"gm002_1_krokivip\",\"elements\":[\"water\",\"lindworm\"]}," +
                "{\"form\":2,\"nameID\":\"gm002_2_leviadile\",\"elements\":[\"water\",\"lindworm\"]}]}" +
                "]}"
        GuardiansServiceLocator.provide(SpeciesDescriptionService.getInstance(json))

        // ......................................................................................... init items
        val jsonItemStrings = ArrayMap<String, String>()

        jsonItemStrings.put("itemsKey",
                "{\"items\":[" +
                        "{\"nameID\":\"relict-earth\",\"category\":\"key\"}," +
                        "{\"nameID\":\"relict-flame\",\"category\":\"key\"}]}")

        jsonItemStrings.put("itemsChakraCrystals",
                "{\"items\":[" + "{\"nameID\":\"guardian-crystal-none\",\"category\":\"key\", \"element\": \"none\"}]}")

        jsonItemStrings.put("itemsMedicine",
                "{\"items\":[" +
                        "{\"nameID\":\"bread\",\"value\":100,\"type\":\"HPcure\",\"category\":\"medicine\"}," +
                        "{\"nameID\":\"medicine-blue\",\"value\":10,\"type\":\"MPcure\",\"category\":\"medicine\"}," +
                        "{\"nameID\":\"angel-tear\",\"value\":50,\"type\":\"revive\",\"category\":\"medicine\"}]}")

        jsonItemStrings.put("itemsEquipment",
                "{\"items\":[" +
                        "{\"nameID\":\"sword-wood\",\"body-part\":\"hands\",\"type\":\"sword\"," +
                        "\"addsPStr\":1,\"addsPDef\":0,\"addsMStr\":0,\"addsMDef\":0,\"addsSpeed\":0," +
                        "\"category\":\"equipment\"}," +
                        "{\"nameID\":\"sword-rusty\",\"body-part\":\"hands\",\"type\":\"sword\"," +
                        "\"addsPStr\":2,\"addsPDef\":0,\"addsMStr\":0,\"addsMDef\":0,\"addsSpeed\":0," +
                        "\"category\":\"equipment\"}]}")

        GuardiansServiceLocator.provide(ItemService.getInstance(jsonItemStrings))

        GuardiansServiceLocator.provide(GuardianFactory)

    }

    fun destroyModule()
    {
        GuardiansServiceLocator.destroy()
    }
}
