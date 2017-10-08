import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;

import org.junit.Test;

import de.limbusdev.guardianmonsters.guardians.Element;
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.ModuleGuardians;
import de.limbusdev.guardianmonsters.guardians.StatCalculator;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.abilities.AbilityService;
import de.limbusdev.guardianmonsters.guardians.abilities.IAbilityService;
import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.guardians.items.ItemService;
import de.limbusdev.guardianmonsters.guardians.items.medicine.MedicalItem;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardianFactory;
import de.limbusdev.guardianmonsters.guardians.monsters.CommonStatistics;
import de.limbusdev.guardianmonsters.guardians.monsters.SpeciesDescription;
import de.limbusdev.guardianmonsters.guardians.monsters.XMLGuardianParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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

        String xml = "<guardians><guardian speciesID=\"1\" nameID=\"gm001_fordin\"><metamorphsTo>2</metamorphsTo><metamorphosisNodes><metamorphosisNode>91</metamorphosisNode><metamorphosisNode>92</metamorphosisNode></metamorphosisNodes><elements><element>earth</element></elements><attacks><ability element=\"none\"  abilityID=\"2\" abilityPos=\"0\" /><ability element=\"earth\" abilityID=\"2\" abilityPos=\"13\" /><ability element=\"earth\" abilityID=\"3\" abilityPos=\"11\" /><ability element=\"earth\" abilityID=\"4\" abilityPos=\"15\" /></attacks><basestats hp=\"300\" mp=\"50\" speed=\"10\" pstr=\"10\" pdef=\"10\" mstr=\"10\" mdef=\"10\" /><equipment-compatibility head=\"bridle\" hands=\"claws\" body=\"barding\" feet=\"shinprotection\" /><ability-graph-equip body=\"21\" hands=\"23\" feet=\"89\" head=\"90\" /></guardian><guardian speciesID=\"2\" nameID=\"gm002_stegofor\"><metamorphsFrom>1</metamorphsFrom><metamorphsTo>3</metamorphsTo><elements><element>earth</element><element>forest</element></elements></guardian><guardian speciesID=\"4\" nameID=\"gm004_kroki\"><metamorphsTo>5</metamorphsTo><metamorphosisNodes><metamorphosisNode>91</metamorphosisNode><metamorphosisNode>92</metamorphosisNode></metamorphosisNodes><elements><element>water</element></elements><attacks><ability element=\"none\"  abilityID=\"2\" abilityPos=\"0\" /><ability element=\"water\" abilityID=\"1\" abilityPos=\"5\" /></attacks><basestats hp=\"300\" mp=\"50\" speed=\"10\" pstr=\"10\" pdef=\"10\" mstr=\"10\" mdef=\"10\" /><equipment-compatibility head=\"helmet\" hands=\"claws\" body=\"breastplate\" feet=\"kneepads\" /><ability-graph-equip body=\"17\" hands=\"19\" feet=\"53\" head=\"54\" /></guardian><guardian speciesID=\"5\" nameID=\"gm005_krokivip\"><metamorphsFrom>4</metamorphsFrom><metamorphsTo>6</metamorphsTo><elements><element>water</element><element>lindworm</element></elements></guardian><guardian speciesID=\"7\" nameID=\"gm007_devidin\"><metamorphsTo>8</metamorphsTo><metamorphosisNodes><metamorphosisNode>91</metamorphosisNode><metamorphosisNode>92</metamorphosisNode></metamorphosisNodes><elements><element>fire</element></elements><attacks><ability element=\"none\"   abilityID=\"2\" abilityPos=\"0\" /><ability element=\"fire\" abilityID=\"1\" abilityPos=\"5\" /></attacks><basestats hp=\"300\" mp=\"50\" speed=\"10\" pstr=\"10\" pdef=\"10\" mstr=\"10\" mdef=\"10\" /><equipment-compatibility head=\"mask\" hands=\"claws\" body=\"barding\" feet=\"shinprotection\" /><ability-graph-equip body=\"17\" hands=\"19\" feet=\"53\" head=\"54\" /></guardian><guardian speciesID=\"8\" nameID=\"gm008_devidra\"><metamorphsFrom>7</metamorphsFrom><metamorphsTo>9</metamorphsTo><elements><element>fire</element><element>lindworm</element></elements></guardian></guardians>\n";
        XmlReader.Element rootElement = XMLGuardianParser.parseGuardianList(xml);

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
    public void commonStatisticsTest()
    {
        ModuleGuardians.destroyModule();
        ModuleGuardians.initModuleForTesting();

        SpeciesDescription description = GuardiansServiceLocator.getSpecies().getSpeciesDescription(1);
        CommonStatistics commonStatistics = description.getCommonStatistics();

        assertEquals(50, commonStatistics.getBaseHP());
        assertEquals(50, commonStatistics.getBaseMP());
        assertEquals(50, commonStatistics.getBasePStr());
        assertEquals(50, commonStatistics.getBasePDef());
        assertEquals(50, commonStatistics.getBaseMStr());
        assertEquals(50, commonStatistics.getBaseMDef());
        assertEquals(50, commonStatistics.getBaseSpeed());

        ModuleGuardians.destroyModule();
    }

    @Test
    public void moduleTest()
    {
        ModuleGuardians.destroyModule();
        ModuleGuardians.initModuleForTesting();
        ModuleGuardians.destroyModule();
    }

    @Test
    public void guardianFactoryTest()
    {
        ModuleGuardians.destroyModule();
        ModuleGuardians.initModuleForTesting();

        AGuardianFactory factory = GuardiansServiceLocator.getGuardianFactory();
        AGuardian guardian = factory.createGuardian(1,1);
        assertEquals(GuardiansServiceLocator.getSpecies().getSpeciesDescription(1), guardian.getSpeciesDescription());
        assertEquals(1, guardian.getIndividualStatistics().getLevel());
        assertEquals(0, guardian.getIndividualStatistics().getAbilityLevels());

        System.out.print("Level " + guardian.getIndividualStatistics().getLevel() + ":\t");
        System.out.println(guardian.getIndividualStatistics());

        AGuardian guardian2 = factory.createGuardian(1,1);
        assertNotEquals(guardian2, guardian);

        AGuardian guardian3 = factory.createGuardian(1,5);
        assertEquals(125, guardian3.getIndividualStatistics().getEXP());

        System.out.print("Level " + guardian3.getIndividualStatistics().getLevel() + ":\t");
        System.out.println(guardian3.getIndividualStatistics());
        assertEquals(5, guardian3.getIndividualStatistics().getLevel());
        assertEquals(4, guardian3.getIndividualStatistics().getAbilityLevels());


        ModuleGuardians.destroyModule();
    }

    @Test
    public void statCalculatorTest()
    {
        assertEquals(0, StatCalculator.calcEXPavailableAtLevel(0));
        assertEquals(0, StatCalculator.calcEXPtoReachLevel(0));
        assertEquals(8, StatCalculator.calcEXPavailableAtLevel(1));
        assertEquals(0, StatCalculator.calcEXPtoReachLevel(1));
        assertEquals(19, StatCalculator.calcEXPavailableAtLevel(2));
        assertEquals(8, StatCalculator.calcEXPtoReachLevel(2));
        assertEquals(27, StatCalculator.calcEXPtoReachLevel(3));
        assertEquals(125, StatCalculator.calcEXPtoReachLevel(5));
        assertEquals(StatCalculator.calcEXPavailableAtLevel(2)+StatCalculator.calcEXPavailableAtLevel(1), StatCalculator.calcEXPtoReachLevel(3));
        assertEquals(StatCalculator.calcEXPavailableAtLevel(5), StatCalculator.calcEXPtoReachLevel(6)-StatCalculator.calcEXPtoReachLevel(5));
    }

    @Test
    public void guardianLevelingTest()
    {
        ModuleGuardians.destroyModule();
        ModuleGuardians.initModuleForTesting();

        AGuardianFactory factory = GuardiansServiceLocator.getGuardianFactory();
        AGuardian guardian = factory.createGuardian(1,1);

        System.out.print("Level " + 1 + ":\t");
        System.out.println(guardian.getIndividualStatistics());

        for(int i=2; i<=99; i++)
        {
            System.out.print("Level " + i + ":\t");
            guardian.getIndividualStatistics().earnEXP(guardian.getIndividualStatistics().getEXPtoNextLevel());

            assertEquals(i, guardian.getIndividualStatistics().getLevel());

            System.out.println(guardian.getIndividualStatistics().getLatestLevelUpReport().newStats);
        }

        ModuleGuardians.destroyModule();
    }

    @Test
    public void EXPcalculationTest()
    {
        ModuleGuardians.destroyModule();
        ModuleGuardians.initModuleForTesting();



        ModuleGuardians.destroyModule();
    }

    @Test
    public void damageCalculationTest()
    {
        ModuleGuardians.destroyModule();
        ModuleGuardians.initModuleForTesting();



        ModuleGuardians.destroyModule();
    }
}
