import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonValue;

import org.junit.Test;

import de.limbusdev.guardianmonsters.guardians.Constant;
import de.limbusdev.guardianmonsters.guardians.Element;
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.ModuleGuardians;
import de.limbusdev.guardianmonsters.guardians.StatCalculator;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.abilities.AbilityService;
import de.limbusdev.guardianmonsters.guardians.abilities.IAbilityService;
import de.limbusdev.guardianmonsters.guardians.battle.AttackCalculationReport;
import de.limbusdev.guardianmonsters.guardians.battle.BattleCalculator;
import de.limbusdev.guardianmonsters.guardians.battle.BattleSystem;
import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.guardians.items.ItemService;
import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyEquipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.FootEquipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.HandEquipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.HeadEquipment;
import de.limbusdev.guardianmonsters.guardians.items.medicine.MedicalItem;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardianFactory;
import de.limbusdev.guardianmonsters.guardians.monsters.CommonStatistics;
import de.limbusdev.guardianmonsters.guardians.monsters.JSONGuardianParser;
import de.limbusdev.guardianmonsters.guardians.monsters.SpeciesDescription;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;

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

        String jsonString = "{\"guardians\":[{\"id\":1,\"metamorphosisNodes\":[91,92],\"abilities\":[{\"abilityID\":2,\"element\":\"none\",\"abilityPos\":0},{\"abilityID\":2,\"element\":\"earth\",\"abilityPos\":13},{\"abilityID\":3,\"element\":\"earth\",\"abilityPos\":11},{\"abilityID\":4,\"element\":\"earth\",\"abilityPos\":15}],\"basestats\":{\"hp\":300,\"mp\":50,\"speed\":10,\"pstr\":10,\"pdef\":10,\"mstr\":10,\"mdef\":10},\"equipmentCompatibility\":{\"head\":\"bridle\",\"hands\":\"claws\",\"body\":\"barding\",\"feet\":\"shinprotection\"},\"abilityGraphEquip\":{\"head\":21,\"hands\":23,\"body\":89,\"feet\":90},\"metaForms\":[{\"form\":0,\"nameID\":\"gm001_0_fordin\",\"elements\":[\"earth\"]},{\"form\":1,\"nameID\":\"gm001_1_stegofor\",\"elements\":[\"earth\",\"forest\"]},{\"form\":2,\"nameID\":\"gm001_2_brachifor\",\"elements\":[\"earth\",\"forest\"]}]}]}";

        JsonValue value = JSONGuardianParser.parseGuardianList(jsonString);
        SpeciesDescription spec = JSONGuardianParser.parseGuardian(value.get(0));

        assertEquals(1, spec.getID());
        assertEquals(2, spec.getMetamorphosisNodes().size);
        assertTrue(spec.getMetamorphosisNodes().contains(91, true));
        assertTrue(spec.getMetamorphosisNodes().contains(92, true));
        assertEquals(4, spec.getAbilityNodes().size);
//        assertTrue(spec.getAbilityNodes().containsValue(new Ability.aID(2, Element.NONE), false));
//        assertTrue(spec.getAbilityNodes().containsValue(new Ability.aID(2, Element.EARTH), true));
//        assertTrue(spec.getAbilityNodes().containsValue(new Ability.aID(3, Element.EARTH), true));
//        assertTrue(spec.getAbilityNodes().containsValue(new Ability.aID(4, Element.EARTH), true));
        assertEquals(300, spec.getCommonStatistics().getBaseHP());
        assertEquals(50, spec.getCommonStatistics().getBaseMP());
        assertEquals(10, spec.getCommonStatistics().getBasePStr());
        assertEquals(10, spec.getCommonStatistics().getBasePDef());
        assertEquals(10, spec.getCommonStatistics().getBaseMStr());
        assertEquals(10, spec.getCommonStatistics().getBaseMDef());
        assertEquals(10, spec.getCommonStatistics().getBaseSpeed());
        assertEquals(HeadEquipment.Type.BRIDLE, spec.getHeadType());
        assertEquals(BodyEquipment.Type.BARDING, spec.getBodyType());
        assertEquals(HandEquipment.Type.CLAWS, spec.getHandType());
        assertEquals(FootEquipment.Type.SHINPROTECTION, spec.getFootType());


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

        assertEquals(209, guardian.getIndividualStatistics().getHPmax());
        assertEquals(158, guardian.getIndividualStatistics().getMPmax());
        assertEquals(105, guardian.getIndividualStatistics().getPStrMax());
        assertEquals(105, guardian.getIndividualStatistics().getPDefMax());
        assertEquals(105, guardian.getIndividualStatistics().getMStrMax());
        assertEquals(105, guardian.getIndividualStatistics().getMDefMax());
        assertEquals(105, guardian.getIndividualStatistics().getSpeedMax());

        ModuleGuardians.destroyModule();
    }

    @Test
    public void EXPcalculationTest()
    {
        ModuleGuardians.destroyModule();
        ModuleGuardians.initModuleForTesting();

        AGuardianFactory factory = GuardiansServiceLocator.getGuardianFactory();
        AGuardian winner = factory.createGuardian(1,1);
        AGuardian looser = factory.createGuardian(1,1);

        int EXP = BattleCalculator.calculateEarnedEXP(winner, looser);

        assertEquals(MathUtils.floor(200f*1.5f/6f), EXP);

        ModuleGuardians.destroyModule();
    }

    @Test
    public void damageCalculationTest()
    {
        ModuleGuardians.destroyModule();
        ModuleGuardians.initModuleForTesting();

        AGuardianFactory factory = GuardiansServiceLocator.getGuardianFactory();
        AGuardian winner = factory.createGuardian(1,1);
        AGuardian looser = factory.createGuardian(1,1);

        int expectedDamage = MathUtils.ceil((0.6f*1f + 2f) * 10f / 48f + 2f);
        System.out.println("Expected Damage: " + expectedDamage);
        AttackCalculationReport report = BattleCalculator.calcAttack(winner, looser, new Ability(1, Ability.DamageType.PHYSICAL, Element.NONE, 10, ""));
        assertEquals(expectedDamage, report.damage);

        BattleCalculator.apply(report);

        assertEquals(10, looser.getIndividualStatistics().getHP());

        ModuleGuardians.destroyModule();
    }

    @Test
    public void equipmentTest()
    {
        ModuleGuardians.destroyModule();
        ModuleGuardians.initModuleForTesting();

        AGuardianFactory factory = GuardiansServiceLocator.getGuardianFactory();
        AGuardian winner = factory.createGuardian(1,1);

        winner.getIndividualStatistics().giveEquipment(new BodyEquipment("", BodyEquipment.Type.ARMOR, 5, 5, 5, 5, 5, 5, 5, 0));
        assertEquals(13+MathUtils.floor(13f*0.05f), winner.getIndividualStatistics().getHPmax());
        assertEquals(7+5, winner.getIndividualStatistics().getPStrMax());

        ModuleGuardians.destroyModule();
    }

    @Test
    public void abilityGraphTest()
    {
        // TODO
    }

    @Test
    public void TestBattleSystem()
    {
        // TODO add real tests
        printTestLabel("Battle System");

        ModuleGuardians.destroyModule();
        ModuleGuardians.initModuleForTesting();

        AGuardianFactory factory = GuardiansServiceLocator.getGuardianFactory();

        // Team Creation
        Team team = new Team(3,3,3);
        team.put(0, factory.createGuardian(1,1));
        team.put(1, factory.createGuardian(1,1));
        team.put(2, factory.createGuardian(1,1));
        Team oppTeam = new Team(3,3,3);
        oppTeam.put(0,factory.createGuardian(4,1));
        oppTeam.put(1,factory.createGuardian(4,1));
        oppTeam.put(2,factory.createGuardian(4,1));

        // Battle System Initialization
        final boolean[] battleEnds = {false};
        BattleSystem bs = new BattleSystem(team, oppTeam, new BattleSystem.Callbacks()
        {
            @Override
            public void onBattleEnds(boolean winnerSide)
            {
                System.out.println("\n=== Battle ends, winner is: " + ((winnerSide) ? "Hero" : "Opponent") + " ===\n");
                battleEnds[0] = true;

                // Check, that one team is KO
                boolean teamKO = true, oppKO = true;
                for(AGuardian g : team.values()) if(g.getIndividualStatistics().isFit()) teamKO = false;
                for(AGuardian g : oppTeam.values()) if(g.getIndividualStatistics().isFit()) oppKO = false;
                assertTrue(teamKO || oppKO);
            }

            @Override
            public void onPlayersTurn()
            {
                super.onPlayersTurn();
            }
        });


        while(!battleEnds[0])
        {
            System.out.println("\n### Player's turn ###");
            AGuardian m = bs.getActiveMonster();
            int att = 0;
            Ability.aID abilityID = null;
            while(abilityID == null) {
                att = MathUtils.random(0,m.getAbilityGraph().getActiveAbilities().size-1);
                abilityID = m.getAbilityGraph().getActiveAbility(att);
            }
            Array<AGuardian> targets = new Array<>();
            for(AGuardian h : oppTeam.values()) {
                if(h.getIndividualStatistics().isFit()) {
                    targets.add(h);
                }
            }
            AGuardian target = targets.get(MathUtils.random(0,targets.size-1));
            System.out.println("Hero chooses target: " + target.getUUID());

            assertEquals("Active Monster is in Hero's team", true, team.isMember(m));
            assertEquals("Target is in Opponent's team", true, oppTeam.isMember(target));

            bs.setChosenTarget(target);
            bs.setChosenAttack(att);
            bs.attack();
            bs.applyAttack();
            bs.continueBattle();
        }

        ModuleGuardians.destroyModule();
    }

    @Test
    public void debuggingOnTest()
    {
        assertTrue(Constant.DEBUGGING_ON);
    }

    public static void printTestLabel(String name)
    {
        System.out.println("\n\n#############################################################");
        System.out.println("#                                                           #");
        System.out.print("#          Test: " + name);
        for (int i = 0; i < 43 - name.length(); i++) System.out.print(" ");
        System.out.println("#");
        System.out.println("#                                                           #");
        System.out.println("#############################################################");
    }
}
