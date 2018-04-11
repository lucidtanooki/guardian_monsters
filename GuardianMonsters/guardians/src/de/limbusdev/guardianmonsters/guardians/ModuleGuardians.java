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

        GuardiansServiceLocator.provide(ItemService.getInstanceFromFile("data/items.xml"));

        GuardiansServiceLocator.provide(SpeciesDescriptionService.getInstanceFromFile("data/guardians.json"));

        GuardiansServiceLocator.provide(GuardianFactory.getInstance());
    }

    public static void initModuleForTesting()
    {
        ArrayMap<Element, String> jsonStrings = new ArrayMap<>();
        String testJson = "[{\"ID\":1,\"element\":\"none\",\"name\":\"attNone1_selfdef\",\"damage\":0,\"MPcost\":0,\"damageType\":\"physical\"},{\"ID\":2,\"element\":\"none\",\"name\":\"attNone2_kick\",\"damage\":50,\"MPcost\":0,\"damageType\":\"physical\"}]";
        jsonStrings.put(Element.NONE, testJson);
        testJson = "[{\"ID\":1,\"element\":\"earth\",\"name\":\"attEarth1_dirt\",\"damage\":5,\"MPcost\":0,\"damageType\":\"physical\"},{\"ID\":2,\"element\":\"earth\",\"name\":\"attEarth2_mud\",\"damage\":10,\"MPcost\":0,\"damageType\":\"physical\"},{\"ID\":3,\"element\":\"earth\",\"name\":\"attEarth3_stones\",\"damage\":20,\"MPcost\":0,\"damageType\":\"physical\"},{\"ID\":4,\"element\":\"earth\",\"name\":\"attEarth4_landslide\",\"damage\":40,\"MPcost\":0,\"damageType\":\"physical\"}]";
        jsonStrings.put(Element.EARTH, testJson);
        testJson = "[{\"ID\":1,\"element\":\"fire\",\"name\":\"attFire1_embers\",\"damage\":5,\"MPcost\":1,\"damageType\":\"magical\"},{\"ID\":2,\"element\":\"fire\",\"name\":\"attFire2_fire\",\"damage\":\"10\",\"MPcost\":2,\"damageType\":\"magical\"},{\"ID\":3,\"element\":\"fire\",\"name\":\"attFire3_flame\",\"damage\":20,\"MPcost\":3,\"damageType\":\"magical\"},{\"ID\":4,\"element\":\"fire\",\"name\":\"attFire4_blaze\",\"damage\":40,\"MPcost\":4,\"damageType\":\"magical\"}]";
        jsonStrings.put(Element.FIRE, testJson);
        testJson = "[{\"ID\":1,\"element\":\"water\",\"name\":\"attWater1_sprinkle\",\"damage\":5,\"MPcost\":0,\"damageType\":\"physical\"},{\"ID\":2,\"element\":\"water\",\"name\":\"attWater2_shower\",\"damage\":10,\"MPcost\":0,\"damageType\":\"physical\"},{\"ID\":3,\"element\":\"water\",\"name\":\"attWater3_splash\",\"damage\":20,\"MPcost\":0,\"damageType\":\"physical\"},{\"ID\":4,\"element\":\"water\",\"name\":\"attWater4_waterjet\",\"damage\":40,\"MPcost\":0,\"damageType\":\"physical\"}]";
        jsonStrings.put(Element.WATER,testJson);

        GuardiansServiceLocator.provide(AbilityService.getInstance(jsonStrings));

        String xml = "<guardians><guardian speciesID=\"1\" nameID=\"gm001_fordin\"><metamorphsTo>2</metamorphsTo><metamorphosisNodes><metamorphosisNode>91</metamorphosisNode><metamorphosisNode>92</metamorphosisNode></metamorphosisNodes><elements><element>earth</element></elements><attacks><ability element=\"none\"  abilityID=\"2\" abilityPos=\"0\" /><ability element=\"earth\" abilityID=\"2\" abilityPos=\"13\" /><ability element=\"earth\" abilityID=\"3\" abilityPos=\"11\" /><ability element=\"earth\" abilityID=\"4\" abilityPos=\"15\" /></attacks><basestats hp=\"50\" mp=\"50\" speed=\"50\" pstr=\"50\" pdef=\"50\" mstr=\"50\" mdef=\"50\" /><equipment-compatibility head=\"bridle\" hands=\"claws\" body=\"barding\" feet=\"shinprotection\" /><ability-graph-equip body=\"21\" hands=\"23\" feet=\"89\" head=\"90\" /></guardian><guardian speciesID=\"2\" nameID=\"gm002_stegofor\"><metamorphsFrom>1</metamorphsFrom><metamorphsTo>3</metamorphsTo><elements><element>earth</element><element>forest</element></elements></guardian><guardian speciesID=\"4\" nameID=\"gm004_kroki\"><metamorphsTo>5</metamorphsTo><metamorphosisNodes><metamorphosisNode>91</metamorphosisNode><metamorphosisNode>92</metamorphosisNode></metamorphosisNodes><elements><element>water</element></elements><attacks><ability element=\"none\"  abilityID=\"2\" abilityPos=\"0\" /><ability element=\"water\" abilityID=\"1\" abilityPos=\"5\" /></attacks><basestats hp=\"50\" mp=\"50\" speed=\"50\" pstr=\"50\" pdef=\"50\" mstr=\"50\" mdef=\"50\" /><equipment-compatibility head=\"helmet\" hands=\"claws\" body=\"breastplate\" feet=\"kneepads\" /><ability-graph-equip body=\"17\" hands=\"19\" feet=\"53\" head=\"54\" /></guardian><guardian speciesID=\"5\" nameID=\"gm005_krokivip\"><metamorphsFrom>4</metamorphsFrom><metamorphsTo>6</metamorphsTo><elements><element>water</element><element>lindworm</element></elements></guardian><guardian speciesID=\"7\" nameID=\"gm007_devidin\"><metamorphsTo>8</metamorphsTo><metamorphosisNodes><metamorphosisNode>91</metamorphosisNode><metamorphosisNode>92</metamorphosisNode></metamorphosisNodes><elements><element>fire</element></elements><attacks><ability element=\"none\"   abilityID=\"2\" abilityPos=\"0\" /><ability element=\"fire\" abilityID=\"1\" abilityPos=\"5\" /></attacks><basestats hp=\"50\" mp=\"50\" speed=\"50\" pstr=\"50\" pdef=\"50\" mstr=\"50\" mdef=\"50\" /><equipment-compatibility head=\"mask\" hands=\"claws\" body=\"barding\" feet=\"shinprotection\" /><ability-graph-equip body=\"17\" hands=\"19\" feet=\"53\" head=\"54\" /></guardian><guardian speciesID=\"8\" nameID=\"gm008_devidra\"><metamorphsFrom>7</metamorphsFrom><metamorphsTo>9</metamorphsTo><elements><element>fire</element><element>lindworm</element></elements></guardian></guardians>";
        GuardiansServiceLocator.provide(SpeciesDescriptionService.getInstance(xml));

        xml = "<items><Key><nameID>relict-earth</nameID></Key><medicine><nameID>bread</nameID><value>100</value><type>HPcure</type></medicine><medicine><nameID>medicine-blue</nameID><value>10</value><type>MPcure</type></medicine><medicine><nameID>angel-tear</nameID><value>50</value><type>revive</type></medicine><Equipment><nameID>sword-wood</nameID><body-part type=\"sword\">hands</body-part><addsPStr>1</addsPStr></Equipment><Equipment><nameID>claws-wood</nameID><body-part type=\"claws\">hands</body-part><addsPStr>1</addsPStr></Equipment><Equipment><nameID>helmet-iron</nameID><body-part type=\"helmet\">head</body-part><addsPDef>2</addsPDef></Equipment><Equipment><nameID>shield-iron</nameID><body-part type=\"shield\">body</body-part><addsPDef>2</addsPDef></Equipment><Equipment><nameID>shoes-leather</nameID><body-part type=\"shoes\">feet</body-part><addsPDef>1</addsPDef><addsSpeed>1</addsSpeed></Equipment></items>";
        GuardiansServiceLocator.provide(ItemService.getInstance(xml));

        GuardiansServiceLocator.provide(GuardianFactory.getInstance());

    }

    public static void destroyModule()
    {
        GuardiansServiceLocator.destroy();
    }
}
