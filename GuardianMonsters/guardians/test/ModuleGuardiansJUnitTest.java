import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;

import org.junit.Test;

import de.limbusdev.guardianmonsters.guardians.Element;
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.ModuleGuardians;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.abilities.AbilityService;
import de.limbusdev.guardianmonsters.guardians.abilities.IAbilityService;
import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.guardians.items.ItemService;
import de.limbusdev.guardianmonsters.guardians.items.medicine.MedicalItem;
import de.limbusdev.guardianmonsters.guardians.monsters.XMLGuardianParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * GuardiansJUnitTestAbilities
 *
 * @author Georg Eckert 2017
 */

public class ModuleGuardiansJUnitTest
{
    @Test
    public void guardianParsingTest()
    {
        ModuleGuardians.destroyModule();

        String testJsonString = "<guardians><guardian speciesID=\"1\" nameID=\"gm001_fordin\"><metamorphsTo>2</metamorphsTo><metamorphosisNodes><metamorphosisNode>91</metamorphosisNode><metamorphosisNode>92</metamorphosisNode></metamorphosisNodes><elements><element>earth</element></elements><attacks><ability element=\"none\"  abilityID=\"2\" abilityPos=\"0\" /><ability element=\"earth\" abilityID=\"2\" abilityPos=\"13\" /><ability element=\"earth\" abilityID=\"3\" abilityPos=\"11\" /><ability element=\"earth\" abilityID=\"4\" abilityPos=\"15\" /></attacks><basestats hp=\"300\" mp=\"50\" speed=\"10\" pstr=\"10\" pdef=\"10\" mstr=\"10\" mdef=\"10\" /><equipment-compatibility head=\"bridle\" hands=\"claws\" body=\"barding\" feet=\"shinprotection\" /><ability-graph-equip body=\"21\" hands=\"23\" feet=\"89\" head=\"90\" /></guardian><guardian speciesID=\"2\" nameID=\"gm002_stegofor\"><metamorphsFrom>1</metamorphsFrom><metamorphsTo>3</metamorphsTo><elements><element>earth</element><element>forest</element></elements></guardian></guardians>\n";
        XmlReader.Element rootElement = XMLGuardianParser.parseGuardianList(testJsonString);

        XmlReader.Element element = rootElement.getChild(0);

        assertEquals("gm001_fordin", XMLGuardianParser.parsenameID(element));
        assertEquals(1, XMLGuardianParser.parseSpeciesID(element));
        assertEquals(2, XMLGuardianParser.parseMetamorphsTo(element));
        assertEquals(0, XMLGuardianParser.parseMetamorphsFrom(element));
        assertTrue(XMLGuardianParser.parseMetamorphosisNodes(element).contains(91, true));
        assertTrue(XMLGuardianParser.parseMetamorphosisNodes(element).contains(92, true));
        assertTrue(XMLGuardianParser.parseElements(element).contains(Element.EARTH, true));

        System.out.println("[Test 1] Guardian parsed correctly");
    }

    @Test
    public void abilityParsingTest()
    {
        ModuleGuardians.destroyModule();

        String testJson =
            "[" +
            "  {" +
            "    \"ID\": 1," +
            "    \"element\": \"none\"," +
            "    \"name\": \"attNone1_selfdef\"," +
            "    \"damage\": 0," +
            "    \"MPcost\": 0," +
            "    \"damageType\": \"physical\"" +
            "  }," +
            "  {" +
            "    \"ID\": 2," +
            "    \"element\": \"earth\"," +
            "    \"name\": \"attNone2_kick\"," +
            "    \"damage\": 50," +
            "    \"MPcost\": 10," +
            "    \"damageType\": \"magical\"" +
            "  }" +
            "]";

        ArrayMap<Element, String> jsonStrings = new ArrayMap<>();
        jsonStrings.put(Element.NONE, testJson);

        GuardiansServiceLocator.provide(AbilityService.getInstance(jsonStrings));
        IAbilityService abilities = GuardiansServiceLocator.getAbilities();

        Ability ability = abilities.getAbility(Element.NONE, 1);

        assertEquals(ability.ID, 1);
        assertEquals(ability.element, Element.NONE);
        assertEquals(ability.name, "attNone1_selfdef");
        assertEquals(ability.MPcost, 0);
        assertEquals(ability.damageType, Ability.DamageType.PHYSICAL);

        ability = abilities.getAbility(Element.NONE, 2);

        assertEquals(ability.ID, 2);
        assertEquals(ability.element, Element.EARTH);
        assertEquals(ability.name, "attNone2_kick");
        assertEquals(ability.MPcost, 10);
        assertEquals(ability.damageType, Ability.DamageType.MAGICAL);

        System.out.println("[Test 2] Ability parsed correctly");
    }

    @Test
    public void itemParsingTest()
    {
        ModuleGuardians.destroyModule();

        String xml =
            "<items>" +
            "    <medicine>" +
            "        <nameID>bread</nameID>" +
            "        <value>100</value>" +
            "        <type>HPcure</type>" +
            "    </medicine>" +
            "</items>";

        GuardiansServiceLocator.provide(ItemService.getInstance(xml));

        Item item = GuardiansServiceLocator.getItems().getItem("bread");
        assertEquals("bread", item.getName());
        assertEquals(MedicalItem.class, item.getClass());
        assertEquals(Item.Category.MEDICINE, item.getCategory());
        assertEquals(100, ((MedicalItem)item).getValue());
        assertEquals(MedicalItem.Type.HP_CURE, ((MedicalItem)item).getType());

        System.out.println("[Test 3] Item parsed correctly");
    }

    @Test
    public void moduleTest()
    {
        ModuleGuardians.destroyModule();
        ModuleGuardians.initModuleForTesting();
        ModuleGuardians.destroyModule();
    }
}
