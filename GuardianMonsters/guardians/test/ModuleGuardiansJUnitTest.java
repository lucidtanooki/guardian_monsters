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
import de.limbusdev.guardianmonsters.guardians.items.IItemService;
import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.guardians.items.ItemService;
import de.limbusdev.guardianmonsters.guardians.items.KeyItem;
import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyEquipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyPart;
import de.limbusdev.guardianmonsters.guardians.items.equipment.FootEquipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.HandEquipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.HeadEquipment;
import de.limbusdev.guardianmonsters.guardians.items.medicine.MedicalItem;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardianFactory;
import de.limbusdev.guardianmonsters.guardians.monsters.CommonStatistics;
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics;
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

        String jsonString = "{\"guardians\":[" +
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
            "{\"form\":2,\"nameID\":\"gm001_2_brachifor\",\"elements\":[\"earth\",\"forest\"]}]}]}";

        JsonValue value = JSONGuardianParser.parseGuardianList(jsonString);
        SpeciesDescription spec = JSONGuardianParser.parseGuardian(value.get(0));

        assertEquals(1, spec.getID());
        assertEquals(2, spec.getMetamorphosisNodes().size);
        assertTrue(spec.getMetamorphosisNodes().contains(91, true));
        assertTrue(spec.getMetamorphosisNodes().contains(92, true));
        assertEquals(4, spec.getAbilityNodes().size);
        Ability.aID aID1, aID2, aID3, aID4;
        aID1 = new Ability.aID(2, Element.NONE);
        aID2 = new Ability.aID(2, Element.EARTH);
        aID3 = new Ability.aID(3, Element.EARTH);
        aID4 = new Ability.aID(4, Element.EARTH);
        assertTrue(spec.getAbilityNodes().containsValue(aID1, false));
        assertTrue(spec.getAbilityNodes().containsValue(aID2, false));
        assertTrue(spec.getAbilityNodes().containsValue(aID3, false));
        assertTrue(spec.getAbilityNodes().containsValue(aID4, false));
        assertEquals(300, spec.getCommonStatistics().getBaseHP());
        assertEquals(50, spec.getCommonStatistics().getBaseMP());
        assertEquals(11, spec.getCommonStatistics().getBasePStr());
        assertEquals(12, spec.getCommonStatistics().getBasePDef());
        assertEquals(13, spec.getCommonStatistics().getBaseMStr());
        assertEquals(14, spec.getCommonStatistics().getBaseMDef());
        assertEquals(10, spec.getCommonStatistics().getBaseSpeed());
        assertEquals(HeadEquipment.Type.BRIDLE, spec.getHeadType());
        assertEquals(BodyEquipment.Type.BARDING, spec.getBodyType());
        assertEquals(HandEquipment.Type.CLAWS, spec.getHandType());
        assertEquals(FootEquipment.Type.SHINPROTECTION, spec.getFootType());
        assertTrue(21 == spec.getEquipmentNodes().getKey(BodyPart.HEAD, true));
        assertTrue(23 == spec.getEquipmentNodes().getKey(BodyPart.HANDS, true));
        assertTrue(89 == spec.getEquipmentNodes().getKey(BodyPart.BODY, true));
        assertTrue(90 == spec.getEquipmentNodes().getKey(BodyPart.FEET, true));

        assertTrue(spec.getMetaForms().size == 3);
        SpeciesDescription.MetaForm f1, f2, f3;
        Array<Element> e1, e2, e3;
        e1 = new Array<>(); e1.add(Element.EARTH);
        e2 = new Array<>(); e2.add(Element.EARTH); e2.add(Element.FOREST);
        e3 = new Array<>(); e3.add(Element.EARTH); e3.add(Element.FOREST);
        f1 = new SpeciesDescription.MetaForm(0, "gm001_0_fordin", e1);
        f2 = new SpeciesDescription.MetaForm(1, "gm001_1_stegofor", e2);
        f3 = new SpeciesDescription.MetaForm(2, "gm001_2_brachifor", e3);

        assertEquals(f1, spec.getMetaForms().get(0));
        assertEquals(f2, spec.getMetaForms().get(1));
        assertEquals(f3, spec.getMetaForms().get(2));


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
            "    \"damageType\": \"physical\",\n" +
                "    \"canChangeStatusEffect\": false,\n" +
                "    \"statusEffect\": \"healthy\",\n" +
                "    \"probabilityToChangeStatusEffect\": 0,\n" +
                "    \"areaDamage\": false,\n" +
                "    \"modifiedStats\": {\"PStr\": 0, \"PDef\": 0, \"MStr\": 0, \"MDef\": 0, \"Speed\": 0},\n" +
                "    \"healedStats\": {\"HP\": 0, \"MP\": 0}" +
            "  }," +
            "  {" +
            "    \"ID\": 2," +
            "    \"element\": \"earth\"," +
            "    \"name\": \"attNone2_kick\"," +
            "    \"damage\": 50," +
            "    \"MPcost\": 10," +
            "    \"damageType\": \"magical\",\n" +
                "    \"canChangeStatusEffect\": false,\n" +
                "    \"statusEffect\": \"healthy\",\n" +
                "    \"probabilityToChangeStatusEffect\": 0,\n" +
                "    \"areaDamage\": false,\n" +
                "    \"modifiedStats\": {\"PStr\": 0, \"PDef\": 0, \"MStr\": 0, \"MDef\": 0, \"Speed\": 0},\n" +
                "    \"healedStats\": {\"HP\": 0, \"MP\": 0}" +
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
        assertEquals(ability.areaDamage, false);
        assertEquals(ability.canChangeStatusEffect, false);
        assertEquals(ability.statusEffect, IndividualStatistics.StatusEffect.HEALTHY);
        assertEquals(ability.probabilityToChangeStatusEffect, 0);
        assertEquals(ability.changesStats, false);
        assertEquals(ability.addsPStr, 0);
        assertEquals(ability.addsPDef, 0);
        assertEquals(ability.addsMStr, 0);
        assertEquals(ability.addsMDef, 0);
        assertEquals(ability.addsSpeed, 0);
        assertEquals(ability.curesStats, false);
        assertEquals(ability.curesHP, 0);
        assertEquals(ability.curesMP, 0);

        ability = abilities.getAbility(Element.NONE, 2);

        assertEquals(ability.ID, 2);
        assertEquals(ability.element, Element.EARTH);
        assertEquals(ability.name, "attNone2_kick");
        assertEquals(ability.MPcost, 10);
        assertEquals(ability.damageType, Ability.DamageType.MAGICAL);
        assertEquals(ability.areaDamage, false);
        assertEquals(ability.canChangeStatusEffect, false);
        assertEquals(ability.statusEffect, IndividualStatistics.StatusEffect.HEALTHY);
        assertEquals(ability.probabilityToChangeStatusEffect, 0);
        assertEquals(ability.changesStats, false);
        assertEquals(ability.addsPStr, 0);
        assertEquals(ability.addsPDef, 0);
        assertEquals(ability.addsMStr, 0);
        assertEquals(ability.addsMDef, 0);
        assertEquals(ability.addsSpeed, 0);
        assertEquals(ability.curesStats, false);
        assertEquals(ability.curesHP, 0);
        assertEquals(ability.curesMP, 0);

        System.out.println("[Test 2] Ability parsed correctly");
    }

    @Test
    public void itemParsingTest()
    {
        ModuleGuardians.destroyModule();

        ArrayMap<String,String> jsonItemStrings = new ArrayMap<>();

        jsonItemStrings.put("itemsKey",
            "{\"items\":[{\"nameID\":\"relict-earth\",\"category\":\"key\"}]}");

        jsonItemStrings.put("itemsMedicine",
            "{\"items\":[" +
                "{\"nameID\":\"bread\",\"value\":100,\"type\":\"HPcure\",\"category\":\"medicine\"}," +
                "{\"nameID\":\"medicine-blue\",\"value\":10,\"type\":\"MPcure\",\"category\":\"medicine\"}," +
                "{\"nameID\":\"angel-tear\",\"value\":50,\"type\":\"revive\",\"category\":\"medicine\"}]}");

        jsonItemStrings.put("itemsEquipment",
            "{\"items\":[{\"nameID\":\"sword-wood\",\"body-part\":\"hands\",\"type\":\"sword\"," +
                "\"addsPStr\":1,\"addsPDef\":0,\"addsMStr\":0,\"addsMDef\":0,\"addsSpeed\":0," +
                "\"category\":\"equipment\"}]}");


        GuardiansServiceLocator.provide(ItemService.getInstance(jsonItemStrings));

        IItemService itemService = GuardiansServiceLocator.getItems();

        Item item = itemService.getItem("relict-earth");
        assertEquals(new KeyItem("relict-earth"), item);

        item = GuardiansServiceLocator.getItems().getItem("bread");
        assertEquals(new MedicalItem("bread", 100, MedicalItem.Type.HP_CURE), item);

        item = GuardiansServiceLocator.getItems().getItem("medicine-blue");
        assertEquals(new MedicalItem("medicine-blue", 10, MedicalItem.Type.MP_CURE), item);

        item = GuardiansServiceLocator.getItems().getItem("angel-tear");
        assertEquals(new MedicalItem("angel-tear", 50, MedicalItem.Type.REVIVE), item);

        item = GuardiansServiceLocator.getItems().getItem("sword-wood");
        assertEquals(new HandEquipment("sword-wood", HandEquipment.Type.SWORD, 1, 0, 0, 0, 0, 0, 0, 0), item);

        System.out.println("[Test 3] Item parsed correctly");
    }

    @Test
    public void commonStatisticsTest()
    {
        ModuleGuardians.destroyModule();
        ModuleGuardians.initModuleForTesting();

        SpeciesDescription description = GuardiansServiceLocator.getSpecies().getSpeciesDescription(1);
        CommonStatistics commonStatistics = description.getCommonStatistics();

        assertEquals(300, commonStatistics.getBaseHP());
        assertEquals(50, commonStatistics.getBaseMP());
        assertEquals(11, commonStatistics.getBasePStr());
        assertEquals(12, commonStatistics.getBasePDef());
        assertEquals(13, commonStatistics.getBaseMStr());
        assertEquals(14, commonStatistics.getBaseMDef());
        assertEquals(10, commonStatistics.getBaseSpeed());

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
        assertEquals(
            StatCalculator.calcEXPavailableAtLevel(2) + StatCalculator.calcEXPavailableAtLevel(1),
            StatCalculator.calcEXPtoReachLevel(3)
        );
        assertEquals(
            StatCalculator.calcEXPavailableAtLevel(5),
            StatCalculator.calcEXPtoReachLevel(6)-StatCalculator.calcEXPtoReachLevel(5)
        );
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
            guardian.getIndividualStatistics().levelUp();

            assertEquals(i, guardian.getIndividualStatistics().getLevel());

            System.out.println(guardian.getIndividualStatistics().getLatestLevelUpReport().newStats);
        }

        assertEquals(
            StatCalculator.calculateHP(IndividualStatistics.Growth.MED, 99, 300, 0, 0),
            guardian.getIndividualStatistics().getHPmax()
        );
        assertEquals(
            StatCalculator.calculateMP(IndividualStatistics.Growth.MED, 99, 50, 0, 0),
            guardian.getIndividualStatistics().getMPmax()
        );
        assertEquals(
            StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 99, 10, 0, 0),
            guardian.getIndividualStatistics().getPStrMax()
        );
        assertEquals(
            StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 99, 11, 0, 0),
            guardian.getIndividualStatistics().getPDefMax()
        );
        assertEquals(
            StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 99, 12, 0, 0),
            guardian.getIndividualStatistics().getMStrMax()
        );
        assertEquals(
            StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 99, 13, 0, 0),
            guardian.getIndividualStatistics().getMDefMax()
        );
        assertEquals(
            StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 99, 14, 0, 0),
            guardian.getIndividualStatistics().getSpeedMax()
        );

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

        int expectedDamage = MathUtils.ceil(1.0f * ((0.5f * 1 + 1) * 30 * (53f / 53f) + 50) / 5f);
        System.out.println("Expected Damage: " + expectedDamage);
        AttackCalculationReport report = BattleCalculator.calcAttack(winner, looser,
            new Ability.aID(2, Element.NONE));
        assertEquals(expectedDamage, report.damage);

        BattleCalculator.apply(report);

        assertEquals(StatCalculator.calculateHP(IndividualStatistics.Growth.MED, 1, 300, 0, 0) - 19,
            looser.getIndividualStatistics().getHP());

        ModuleGuardians.destroyModule();
    }

    @Test
    public void equipmentTest()
    {
        ModuleGuardians.destroyModule();
        ModuleGuardians.initModuleForTesting();

        AGuardianFactory factory = GuardiansServiceLocator.getGuardianFactory();
        AGuardian guardian = factory.createGuardian(1,1);


        assertEquals(
            StatCalculator.calculateHP(IndividualStatistics.Growth.MED, 1, 300, 0, 0),
            guardian.getIndividualStatistics().getHPmax()
        );

        assertEquals(
            StatCalculator.calculateMP(IndividualStatistics.Growth.MED, 1, 50, 0, 0),
            guardian.getIndividualStatistics().getMPmax()
        );

        assertEquals(
            StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 1, 10, 0, 0),
            guardian.getIndividualStatistics().getPStrMax()
        );

        assertEquals(
            StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 1, 11, 0, 0),
            guardian.getIndividualStatistics().getPDefMax()
        );

        assertEquals(
            StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 1, 12, 0, 0),
            guardian.getIndividualStatistics().getMStrMax()
        );

        assertEquals(
            StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 1, 13, 0, 0),
            guardian.getIndividualStatistics().getMDefMax()
        );

        assertEquals(
            StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 1, 14, 0, 0),
            guardian.getIndividualStatistics().getSpeedMax()
        );

        guardian.getIndividualStatistics().giveEquipment(new BodyEquipment("", BodyEquipment.Type.ARMOR, 1, 2, 3, 4, 5, 6, 7, 0));

        assertEquals(
            MathUtils.floor(StatCalculator.calculateHP(IndividualStatistics.Growth.MED, 1, 300, 0, 0) * 1.06f),
            guardian.getIndividualStatistics().getHPmax()
        );

        assertEquals(
            MathUtils.floor(StatCalculator.calculateMP(IndividualStatistics.Growth.MED, 1, 50, 0, 0) * 1.07f),
            guardian.getIndividualStatistics().getMPmax()
        );

        assertEquals(
            StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 1, 10, 0, 0) + 1,
            guardian.getIndividualStatistics().getPStrMax()
        );

        assertEquals(
            StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 1, 11, 0, 0) + 2,
            guardian.getIndividualStatistics().getPDefMax()
        );

        assertEquals(
            StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 1, 12, 0, 0) + 3,
            guardian.getIndividualStatistics().getMStrMax()
        );

        assertEquals(
            StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 1, 13, 0, 0) + 4,
            guardian.getIndividualStatistics().getMDefMax()
        );

        assertEquals(
            StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 1, 14, 0, 0) + 5,
            guardian.getIndividualStatistics().getSpeedMax()
        );

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
        oppTeam.put(0,factory.createGuardian(2,1));
        oppTeam.put(1,factory.createGuardian(2,1));
        oppTeam.put(2,factory.createGuardian(2,1));

        // Battle System Initialization
        final boolean[] battleEnds = {false};
        final BattleSystem bs = new BattleSystem(team, oppTeam, null, true);

        BattleSystem.Callbacks bsCB = new BattleSystem.Callbacks()
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
                playersTurn(bs, team, oppTeam);
            }
        };

        bs.setCallbacks(bsCB);


        while(!battleEnds[0])
        {
            System.out.println(bs.getQueue().toString());
            bs.continueBattle();
            bs.applyAttack();
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

    public static void playersTurn(BattleSystem bs, Team heroTeam, Team oppTeam)
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

        assertEquals("Active Monster is in Hero's team", true, heroTeam.isMember(m));
        assertEquals("Target is in Opponent's team", true, oppTeam.isMember(target));

        bs.setChosenTarget(target);
        bs.setChosenAttack(att);
        bs.attack();
        bs.applyAttack();
    }
}
