import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.JsonValue

import org.junit.Test

import de.limbusdev.guardianmonsters.guardians.Constant
import de.limbusdev.guardianmonsters.guardians.Element
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.ModuleGuardians
import de.limbusdev.guardianmonsters.guardians.StatCalculator
import de.limbusdev.guardianmonsters.guardians.abilities.Ability
import de.limbusdev.guardianmonsters.guardians.abilities.AbilityService
import de.limbusdev.guardianmonsters.guardians.abilities.IAbilityService
import de.limbusdev.guardianmonsters.guardians.battle.AttackCalculationReport
import de.limbusdev.guardianmonsters.guardians.battle.BattleCalculator
import de.limbusdev.guardianmonsters.guardians.battle.BattleSystem
import de.limbusdev.guardianmonsters.guardians.items.IItemService
import de.limbusdev.guardianmonsters.guardians.items.Item
import de.limbusdev.guardianmonsters.guardians.items.ItemService
import de.limbusdev.guardianmonsters.guardians.items.KeyItem
import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyEquipment
import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyPart
import de.limbusdev.guardianmonsters.guardians.items.equipment.FootEquipment
import de.limbusdev.guardianmonsters.guardians.items.equipment.HandEquipment
import de.limbusdev.guardianmonsters.guardians.items.equipment.HeadEquipment
import de.limbusdev.guardianmonsters.guardians.items.medicine.MedicalItem
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardianFactory
import de.limbusdev.guardianmonsters.guardians.monsters.CommonStatistics
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics
import de.limbusdev.guardianmonsters.guardians.monsters.JSONGuardianParser
import de.limbusdev.guardianmonsters.guardians.monsters.SpeciesDescription
import de.limbusdev.guardianmonsters.guardians.monsters.Team

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue

/**
 * GuardiansJUnitTestAbilities
 *
 * @author Georg Eckert 2017
 */

class ModuleGuardiansJUnitTest
{
    @Test fun `Test parse Guardians`()
    {
        ModuleGuardians.destroyModule()

        val jsonString = """
            {"guardians": [
                    {
                        "id":1,
                        "metamorphosisNodes":[91,92],
                        "abilities":[
                            {"abilityID":2,"element":"none","abilityPos":0},
                            {"abilityID":2,"element":"earth","abilityPos":13},
                            {"abilityID":3,"element":"earth","abilityPos":11},
                            {"abilityID":4,"element":"earth","abilityPos":15}
                        ],
                        "basestats":{"hp":300,"mp":50,"speed":10,"pstr":11,"pdef":12,"mstr":13,"mdef":14},
                        "equipmentCompatibility":{"head":"bridle","hands":"claws","body":"barding","feet":"shinprotection"},
                        "abilityGraphEquip":{"head":21,"hands":23,"body":89,"feet":90},
                        "metaForms":[
                            {"form":0,"nameID":"gm001_0_fordin","elements":["earth"]},
                            {"form":1,"nameID":"gm001_1_stegofor","elements":["earth","forest"]},
                            {"form":2,"nameID":"gm001_2_brachifor","elements":["earth","forest"]}
                        ]
            }]}
            """.trimIndent()

        val value = JSONGuardianParser.parseGuardianList(jsonString)
        val spec = JSONGuardianParser.parseGuardian(value.get(0))

        assertEquals(1, spec.ID)
        assertEquals(2, spec.metamorphosisNodes.size)
        assertTrue(spec.metamorphosisNodes.contains(91, true))
        assertTrue(spec.metamorphosisNodes.contains(92, true))
        assertEquals(4, spec.abilityNodes.size)

        val aID1 = Ability.aID(2, Element.NONE)
        val aID2 = Ability.aID(2, Element.EARTH)
        val aID3 = Ability.aID(3, Element.EARTH)
        val aID4 = Ability.aID(4, Element.EARTH)

        assertTrue(spec.abilityNodes.containsValue(aID1, false))
        assertTrue(spec.abilityNodes.containsValue(aID2, false))
        assertTrue(spec.abilityNodes.containsValue(aID3, false))
        assertTrue(spec.abilityNodes.containsValue(aID4, false))
        assertEquals(300, spec.commonStatistics.getBaseHP())
        assertEquals(50, spec.commonStatistics.getBaseMP())
        assertEquals(11, spec.commonStatistics.getBasePStr())
        assertEquals(12, spec.commonStatistics.getBasePDef())
        assertEquals(13, spec.commonStatistics.getBaseMStr())
        assertEquals(14, spec.commonStatistics.getBaseMDef())
        assertEquals(10, spec.commonStatistics.getBaseSpeed())
        assertEquals(HeadEquipment.Type.BRIDLE, spec.headType)
        assertEquals(BodyEquipment.Type.BARDING, spec.bodyType)
        assertEquals(HandEquipment.Type.CLAWS, spec.handType)
        assertEquals(FootEquipment.Type.SHINPROTECTION, spec.footType)
        assertTrue(21 == spec.equipmentNodes.getKey(BodyPart.HEAD, true))
        assertTrue(23 == spec.equipmentNodes.getKey(BodyPart.HANDS, true))
        assertTrue(89 == spec.equipmentNodes.getKey(BodyPart.BODY, true))
        assertTrue(90 == spec.equipmentNodes.getKey(BodyPart.FEET, true))

        assertTrue(spec.metaForms.size == 3)
        val f1: SpeciesDescription.MetaForm
        val f2: SpeciesDescription.MetaForm
        val f3: SpeciesDescription.MetaForm

        val e1 = Array<Element>()
        val e2 = Array<Element>()
        val e3 = Array<Element>()

        e1.add(Element.EARTH)
        e2.add(Element.EARTH)
        e2.add(Element.FOREST)
        e3.add(Element.EARTH)
        e3.add(Element.FOREST)
        f1 = SpeciesDescription.MetaForm(0, "gm001_0_fordin", e1)
        f2 = SpeciesDescription.MetaForm(1, "gm001_1_stegofor", e2)
        f3 = SpeciesDescription.MetaForm(2, "gm001_2_brachifor", e3)

        assertEquals(f1, spec.metaForms.get(0))
        assertEquals(f2, spec.metaForms.get(1))
        assertEquals(f3, spec.metaForms.get(2))

        println("[Test 1] Guardian parsed correctly")
    }

    @Test fun `Test Ability Parsing`()
    {
        ModuleGuardians.destroyModule()

        val testJson = """
            [
                {
                    "ID": 1,
                    "element": "none",
                    "name": "attNone1_selfdef",
                    "damage": 0,
                    "MPcost": 0,
                    "damageType": "physical",
                    "canChangeStatusEffect": false,
                    "statusEffect": "healthy",
                    "probabilityToChangeStatusEffect": 0,
                    "areaDamage": false,
                    "modifiedStats": {"PStr": 0, "PDef": 0, "MStr": 0, "MDef": 0, "Speed": 0},
                    "healedStats": {"HP": 0, "MP": 0}
                },
                {
                    "ID": 2,
                    "element": "earth",
                    "name": "attNone2_kick",
                    "damage": 50,
                    "MPcost": 10,
                    "damageType": "magical",
                    "canChangeStatusEffect": false,
                    "statusEffect": "healthy",
                    "probabilityToChangeStatusEffect": 0,
                    "areaDamage": false,
                    "modifiedStats": {"PStr": 0, "PDef": 0, "MStr": 0, "MDef": 0, "Speed": 0},
                    "healedStats": {"HP": 0, "MP": 0}
                }
            ]
        """.trimIndent()

        val jsonStrings = ArrayMap<Element, String>()
        jsonStrings.put(Element.NONE, testJson)

        GuardiansServiceLocator.provide(AbilityService.getInstance(jsonStrings))
        val abilities = GuardiansServiceLocator.abilities

        // ................................................. Ability 1
        var ability = abilities.getAbility(Element.NONE, 1)

        assertEquals("attNone1_selfdef",    ability.name)
        assertEquals(Element.NONE,                  ability.element)
        assertEquals(Ability.DamageType.PHYSICAL,   ability.damageType)
        assertEquals(IndividualStatistics.StatusEffect.HEALTHY, ability.statusEffect)

        assertEquals(1,     ability.ID)
        assertEquals(0,     ability.MPcost)
        assertEquals(false, ability.areaDamage)
        assertEquals(false, ability.canChangeStatusEffect)
        assertEquals(0,     ability.probabilityToChangeStatusEffect)

        assertEquals(false, ability.changesStats)
        assertEquals(0,     ability.addsPStr)
        assertEquals(0,     ability.addsPDef)
        assertEquals(0,     ability.addsMStr)
        assertEquals(0,     ability.addsMDef)
        assertEquals(0,     ability.addsSpeed)
        assertEquals(false, ability.curesStats)
        assertEquals(0,     ability.curesHP)
        assertEquals(0,     ability.curesMP)


        // ................................................. Ability 2
        ability = abilities.getAbility(Element.NONE, 2)

        assertEquals("attNone2_kick",       ability.name)
        assertEquals(Element.EARTH,                 ability.element)
        assertEquals(Ability.DamageType.MAGICAL,    ability.damageType)
        assertEquals(IndividualStatistics.StatusEffect.HEALTHY, ability.statusEffect)

        assertEquals(2,     ability.ID)
        assertEquals(10,    ability.MPcost)
        assertEquals(false, ability.areaDamage)
        assertEquals(false, ability.canChangeStatusEffect)
        assertEquals(0,     ability.probabilityToChangeStatusEffect)

        assertEquals(false, ability.changesStats)
        assertEquals(0,     ability.addsPStr)
        assertEquals(0,     ability.addsPDef)
        assertEquals(0,     ability.addsMStr)
        assertEquals(0,     ability.addsMDef)
        assertEquals(0,     ability.addsSpeed)
        assertEquals(false, ability.curesStats)
        assertEquals(0,     ability.curesHP)
        assertEquals(0,     ability.curesMP)

        println("[Test 2] Ability parsed correctly")
    }

    @Test fun `Test Item Parsing`()
    {
        ModuleGuardians.destroyModule()

        val jsonItemStrings = ArrayMap<String, String>()

        jsonItemStrings.put("itemsKey",
                "{\"items\":[{\"nameID\":\"relict-earth\",\"category\":\"key\"}]}")

        jsonItemStrings.put("itemsMedicine",
                "{\"items\":[" +
                        "{\"nameID\":\"bread\",\"value\":100,\"type\":\"HPcure\",\"category\":\"medicine\"}," +
                        "{\"nameID\":\"medicine-blue\",\"value\":10,\"type\":\"MPcure\",\"category\":\"medicine\"}," +
                        "{\"nameID\":\"angel-tear\",\"value\":50,\"type\":\"revive\",\"category\":\"medicine\"}]}")

        jsonItemStrings.put("itemsEquipment",
                "{\"items\":[{\"nameID\":\"sword-wood\",\"body-part\":\"hands\",\"type\":\"sword\"," +
                        "\"addsPStr\":1,\"addsPDef\":0,\"addsMStr\":0,\"addsMDef\":0,\"addsSpeed\":0," +
                        "\"category\":\"equipment\"}]}")


        GuardiansServiceLocator.provide(ItemService.getInstance(jsonItemStrings))

        val itemService = GuardiansServiceLocator.items

        var item = itemService.getItem("relict-earth")
        assertEquals(KeyItem("relict-earth"), item)

        item = GuardiansServiceLocator.items.getItem("bread")
        assertEquals(MedicalItem("bread", 100, MedicalItem.Type.HP_CURE), item)

        item = GuardiansServiceLocator.items.getItem("medicine-blue")
        assertEquals(MedicalItem("medicine-blue", 10, MedicalItem.Type.MP_CURE), item)

        item = GuardiansServiceLocator.items.getItem("angel-tear")
        assertEquals(MedicalItem("angel-tear", 50, MedicalItem.Type.REVIVE), item)

        item = GuardiansServiceLocator.items.getItem("sword-wood")
        assertEquals(HandEquipment("sword-wood", HandEquipment.Type.SWORD, 1, 0, 0, 0, 0, 0, 0, 0), item)

        println("[Test 3] Item parsed correctly")
    }

    @Test fun `Test common Statistics`()
    {
        ModuleGuardians.destroyModule()
        ModuleGuardians.initModuleForTesting()

        val description = GuardiansServiceLocator.species.getSpeciesDescription(1)
        val commonStatistics = description.commonStatistics

        assertEquals(300, commonStatistics.getBaseHP().toLong())
        assertEquals(50, commonStatistics.getBaseMP().toLong())
        assertEquals(11, commonStatistics.getBasePStr().toLong())
        assertEquals(12, commonStatistics.getBasePDef().toLong())
        assertEquals(13, commonStatistics.getBaseMStr().toLong())
        assertEquals(14, commonStatistics.getBaseMDef().toLong())
        assertEquals(10, commonStatistics.getBaseSpeed().toLong())

        ModuleGuardians.destroyModule()
    }

    @Test fun `Test Module`()
    {
        ModuleGuardians.destroyModule()
        ModuleGuardians.initModuleForTesting()
        ModuleGuardians.destroyModule()
    }

    @Test fun `Test Guardian Factory`()
    {
        ModuleGuardians.destroyModule()
        ModuleGuardians.initModuleForTesting()

        val factory = GuardiansServiceLocator.guardianFactory
        val guardian = factory.createGuardian(1, 1)
        assertEquals(GuardiansServiceLocator.species.getSpeciesDescription(1), guardian.speciesDescription)
        assertEquals(1, guardian.individualStatistics.level.toLong())
        assertEquals(0, guardian.individualStatistics.abilityLevels.toLong())

        print("Level " + guardian.individualStatistics.level + ":\t")
        println(guardian.individualStatistics)

        val guardian2 = factory.createGuardian(1, 1)
        assertNotEquals(guardian2, guardian)

        val guardian3 = factory.createGuardian(1, 5)
        assertEquals(125, guardian3.individualStatistics.exp)

        print("Level " + guardian3.individualStatistics.level + ":\t")
        println(guardian3.individualStatistics)
        assertEquals(5, guardian3.individualStatistics.level.toLong())
        assertEquals(4, guardian3.individualStatistics.abilityLevels.toLong())


        ModuleGuardians.destroyModule()
    }

    @Test fun `Test Stat Calculator`()
    {
        assertEquals(0, StatCalculator.calcEXPavailableAtLevel(0))
        assertEquals(0, StatCalculator.calcEXPtoReachLevel(0))
        assertEquals(8, StatCalculator.calcEXPavailableAtLevel(1))
        assertEquals(0, StatCalculator.calcEXPtoReachLevel(1))
        assertEquals(19, StatCalculator.calcEXPavailableAtLevel(2))
        assertEquals(8, StatCalculator.calcEXPtoReachLevel(2))
        assertEquals(27, StatCalculator.calcEXPtoReachLevel(3))
        assertEquals(125, StatCalculator.calcEXPtoReachLevel(5))
        assertEquals(
                StatCalculator.calcEXPavailableAtLevel(2) + StatCalculator.calcEXPavailableAtLevel(1),
                StatCalculator.calcEXPtoReachLevel(3)
        )
        assertEquals(
                StatCalculator.calcEXPavailableAtLevel(5),
                StatCalculator.calcEXPtoReachLevel(6) - StatCalculator.calcEXPtoReachLevel(5)
        )
    }

    @Test fun `Test Guardian Leveling`()
    {
        ModuleGuardians.destroyModule()
        ModuleGuardians.initModuleForTesting()

        val factory = GuardiansServiceLocator.guardianFactory
        val guardian = factory.createGuardian(1, 1)

        print("Level " + 1 + ":\t")
        println(guardian.individualStatistics)

        for(i in 2..99) {
            print("Level $i:\t")
            guardian.individualStatistics.earnEXP(guardian.individualStatistics.expToNextLevel)
            guardian.individualStatistics.levelUp()

            assertEquals(i.toLong(), guardian.individualStatistics.level.toLong())

            println(guardian.individualStatistics.latestLevelUpReport!!.newStats)
        }

        assertEquals(
                StatCalculator.calculateHP(IndividualStatistics.Growth.MED, 99, 300, 0, 0).toLong(),
                guardian.individualStatistics.hpMax.toLong()
        )
        assertEquals(
                StatCalculator.calculateMP(IndividualStatistics.Growth.MED, 99, 50, 0, 0).toLong(),
                guardian.individualStatistics.mPmax.toLong()
        )
        assertEquals(
                StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 99, 10, 0, 0).toLong(),
                guardian.individualStatistics.pStrMax.toLong()
        )
        assertEquals(
                StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 99, 11, 0, 0).toLong(),
                guardian.individualStatistics.pDefMax.toLong()
        )
        assertEquals(
                StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 99, 12, 0, 0).toLong(),
                guardian.individualStatistics.mStrMax.toLong()
        )
        assertEquals(
                StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 99, 13, 0, 0).toLong(),
                guardian.individualStatistics.mDefMax.toLong()
        )
        assertEquals(
                StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 99, 14, 0, 0).toLong(),
                guardian.individualStatistics.speedMax.toLong()
        )

        ModuleGuardians.destroyModule()
    }

    @Test fun `Test EXP Calculation`()
    {
        ModuleGuardians.destroyModule()
        ModuleGuardians.initModuleForTesting()

        val factory = GuardiansServiceLocator.guardianFactory
        val winner = factory.createGuardian(1, 1)
        val looser = factory.createGuardian(1, 1)

        val EXP = BattleCalculator.calculateEarnedEXP(winner, looser)

        assertEquals(MathUtils.floor(200f * 1.5f / 6f).toLong(), EXP.toLong())

        ModuleGuardians.destroyModule()
    }

    @Test fun `Test Damage Calculation`()
    {
        ModuleGuardians.destroyModule()
        ModuleGuardians.initModuleForTesting()

        val factory = GuardiansServiceLocator.guardianFactory
        val winner = factory.createGuardian(1, 1)
        val looser = factory.createGuardian(1, 1)

        val expectedDamage = MathUtils.ceil(1.0f * ((0.5f * 1 + 1) * 30f * (53f / 53f) + 50) / 5f)
        println("Expected Damage: $expectedDamage")
        val report = BattleCalculator.calcAttack(winner, looser,
                Ability.aID(2, Element.NONE))
        assertEquals(expectedDamage.toLong(), report.damage.toLong())

        BattleCalculator.apply(report)

        assertEquals(StatCalculator.calculateHP(IndividualStatistics.Growth.MED, 1, 300, 0, 0) - 19,
                looser.individualStatistics.hp)

        ModuleGuardians.destroyModule()
    }

    @Test fun `Test Equipment`()
    {
        ModuleGuardians.destroyModule()
        ModuleGuardians.initModuleForTesting()

        val factory = GuardiansServiceLocator.guardianFactory
        val guardian = factory.createGuardian(1, 1)


        assertEquals(
                StatCalculator.calculateHP(IndividualStatistics.Growth.MED, 1, 300, 0, 0).toLong(),
                guardian.individualStatistics.hpMax.toLong()
        )

        assertEquals(
                StatCalculator.calculateMP(IndividualStatistics.Growth.MED, 1, 50, 0, 0).toLong(),
                guardian.individualStatistics.mPmax.toLong()
        )

        assertEquals(
                StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 1, 10, 0, 0).toLong(),
                guardian.individualStatistics.pStrMax.toLong()
        )

        assertEquals(
                StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 1, 11, 0, 0).toLong(),
                guardian.individualStatistics.pDefMax.toLong()
        )

        assertEquals(
                StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 1, 12, 0, 0).toLong(),
                guardian.individualStatistics.mStrMax.toLong()
        )

        assertEquals(
                StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 1, 13, 0, 0).toLong(),
                guardian.individualStatistics.mDefMax.toLong()
        )

        assertEquals(
                StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 1, 14, 0, 0).toLong(),
                guardian.individualStatistics.speedMax.toLong()
        )

        guardian.individualStatistics.giveEquipment(BodyEquipment("", BodyEquipment.Type.ARMOR, 1, 2, 3, 4, 5, 6, 7, 0))

        assertEquals(
                MathUtils.floor(StatCalculator.calculateHP(IndividualStatistics.Growth.MED, 1, 300, 0, 0) * 1.06f).toLong(),
                guardian.individualStatistics.hpMax.toLong()
        )

        assertEquals(
                MathUtils.floor(StatCalculator.calculateMP(IndividualStatistics.Growth.MED, 1, 50, 0, 0) * 1.07f).toLong(),
                guardian.individualStatistics.mPmax.toLong()
        )

        assertEquals(
                (StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 1, 10, 0, 0) + 1).toLong(),
                guardian.individualStatistics.pStrMax.toLong()
        )

        assertEquals(
                (StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 1, 11, 0, 0) + 2).toLong(),
                guardian.individualStatistics.pDefMax.toLong()
        )

        assertEquals(
                (StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 1, 12, 0, 0) + 3).toLong(),
                guardian.individualStatistics.mStrMax.toLong()
        )

        assertEquals(
                (StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 1, 13, 0, 0) + 4).toLong(),
                guardian.individualStatistics.mDefMax.toLong()
        )

        assertEquals(
                (StatCalculator.calculateStat(IndividualStatistics.Growth.MED, 1, 14, 0, 0) + 5).toLong(),
                guardian.individualStatistics.speedMax.toLong()
        )

        ModuleGuardians.destroyModule()
    }

    @Test fun `Test Ability Graph`()
    {
        // TODO
    }

    @Test fun `Test Battle System`()
    {
        // TODO add real tests
        printTestLabel("Battle System")

        ModuleGuardians.destroyModule()
        ModuleGuardians.initModuleForTesting()

        val factory = GuardiansServiceLocator.guardianFactory

        // Team Creation
        val team = Team(3, 3, 3)
        team.put(0, factory.createGuardian(1, 1))
        team.put(1, factory.createGuardian(1, 1))
        team.put(2, factory.createGuardian(1, 1))
        val oppTeam = Team(3, 3, 3)
        oppTeam.put(0, factory.createGuardian(2, 1))
        oppTeam.put(1, factory.createGuardian(2, 1))
        oppTeam.put(2, factory.createGuardian(2, 1))

        // Battle System Initialization
        val battleEnds = booleanArrayOf(false)
        val bs = BattleSystem(team, oppTeam, null, true)

        val bsCB = object : BattleSystem.Callbacks() {
            override fun onBattleEnds(winnerSide: Boolean) {
                println("\n=== Battle ends, winner is: " + (if(winnerSide) "Hero" else "Opponent") + " ===\n")
                battleEnds[0] = true

                // Check, that one team is KO
                var teamKO = true
                var oppKO = true
                for(g in team.values()) if(g.individualStatistics.isFit) teamKO = false
                for(g in oppTeam.values()) if(g.individualStatistics.isFit) oppKO = false
                assertTrue(teamKO || oppKO)
            }

            override fun onPlayersTurn() {
                playersTurn(bs, team, oppTeam)
            }
        }

        bs.setCallbacks(bsCB)


        while(!battleEnds[0]) {
            println(bs.queue.toString())
            bs.continueBattle()
            bs.applyAttack()
        }

        ModuleGuardians.destroyModule()
    }

    @Test fun `Test if Debugging is on`()
    {
        assertTrue(Constant.DEBUGGING_ON)
    }

    companion object
    {
        fun printTestLabel(name: String)
        {
            println("\n\n#############################################################")
            println("#                                                           #")
            print("#          Test: $name")
            for(i in 0 until (43 - name.length)) print(" ")
            println("#")
            println("#                                                           #")
            println("#############################################################")
        }

        fun playersTurn(bs: BattleSystem, heroTeam: Team, oppTeam: Team)
        {
            println("\n### Player's turn ###")
            val m = bs.activeMonster
            var att = 0
            var abilityID: Ability.aID? = null
            while(abilityID == null)
            {
                att = MathUtils.random(0, m.abilityGraph.activeAbilities.size - 1)
                abilityID = m.abilityGraph.getActiveAbility(att)
            }

            val targets = Array<AGuardian>()
            for(h in oppTeam.values())
            {
                if(h.individualStatistics.isFit)
                {
                    targets.add(h)
                }
            }
            val target = targets.get(MathUtils.random(0, targets.size - 1))
            System.out.println("Hero chooses target: " + target.uuid)

            assertEquals("Active Monster is in Hero's team", true, heroTeam.isMember(m))
            assertEquals("Target is in Opponent's team", true, oppTeam.isMember(target))

            bs.setChosenTarget(target)
            bs.setChosenAttack(att)
            bs.attack()
            bs.applyAttack()
        }
    }
}
