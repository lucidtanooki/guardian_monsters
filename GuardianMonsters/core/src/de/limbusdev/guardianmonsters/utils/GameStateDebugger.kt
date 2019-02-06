package de.limbusdev.guardianmonsters.utils

import com.badlogic.gdx.Game
import com.badlogic.gdx.utils.Array

import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.battle.BattleResultScreen
import de.limbusdev.guardianmonsters.battle.BattleScreen
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager
import de.limbusdev.guardianmonsters.fwmengine.world.ui.WorldScreen
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.battle.BattleResult
import de.limbusdev.guardianmonsters.guardians.items.Inventory
import de.limbusdev.guardianmonsters.guardians.items.Item
import de.limbusdev.guardianmonsters.guardians.monsters.GuardianFactory
import de.limbusdev.guardianmonsters.guardians.monsters.GuardoSphere
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.guardosphere.GuardoSphereScreen
import de.limbusdev.guardianmonsters.inventory.InventoryScreen
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.metamorphosis.MetamorphosisScreen

import de.limbusdev.guardianmonsters.Constant.DEBUG_MODE

/**
 * @author Georg Eckert 2016
 */

class GameStateDebugger(private val game: Game)
{
    private fun setUpTestInventory()
    {
        val ACTIVE_TEAM_SIZE_HERO = 3
        val gf = GuardiansServiceLocator.guardianFactory
        val heroTeam = Team(3, 3, ACTIVE_TEAM_SIZE_HERO)
        heroTeam += gf.createGuardian(1, 1)
        heroTeam += gf.createGuardian(2, 1)
        heroTeam += gf.createGuardian(3, 1)

        // Enable Abilities
        heroTeam[0].abilityGraph.activateNode(0)
        heroTeam[0].abilityGraph.activateNode(13)
        heroTeam[0].abilityGraph.activateNode(11)
        heroTeam[0].abilityGraph.activateNode(15)
        heroTeam[0].abilityGraph.setActiveAbility(1, 13)
        heroTeam[0].abilityGraph.activateNode(5)
        heroTeam[0].abilityGraph.setActiveAbility(2, 5)
        heroTeam[0].abilityGraph.activateNode(20)
        heroTeam[0].abilityGraph.setActiveAbility(3, 20)
        //        heroTeam.get(0).getAbilityGraph().activateNode(30);
        //        heroTeam.get(0).getAbilityGraph().setActiveAbility(4,30);
        //        heroTeam.get(0).getAbilityGraph().activateNode(35);
        //        heroTeam.get(0).getAbilityGraph().setActiveAbility(5,35);
        heroTeam[0].abilityGraph.activateNode(21)
        heroTeam[0].abilityGraph.activateNode(23)
        heroTeam[0].abilityGraph.activateNode(89)
        heroTeam[0].abilityGraph.activateNode(90)
        heroTeam[0].abilityGraph.activateNode(80)
        heroTeam[0].abilityGraph.activateNode(81)
        heroTeam[0].abilityGraph.activateNode(82)
        heroTeam[0].abilityGraph.activateNode(83)
        heroTeam[0].abilityGraph.activateNode(84)
        heroTeam[0].abilityGraph.activateNode(85)
        heroTeam[0].individualStatistics.earnEXP(200)
        heroTeam[0].individualStatistics.levelUp()
        heroTeam[0].individualStatistics.levelUp()
        heroTeam[0].individualStatistics.levelUp()
        heroTeam[0].individualStatistics.levelUp()
        heroTeam[0].individualStatistics.levelUp()
        heroTeam[0].individualStatistics.levelUp()
        heroTeam[0].individualStatistics.levelUp()
        heroTeam[0].individualStatistics.levelUp()
        heroTeam[0].individualStatistics.levelUp()
        heroTeam[0].individualStatistics.levelUp()
        heroTeam[0].individualStatistics.levelUp()
        heroTeam[0].individualStatistics.levelUp()
        heroTeam[0].individualStatistics.levelUp()
        heroTeam[0].individualStatistics.levelUp()

        heroTeam[0].abilityGraph.activateNode(91)


        heroTeam[1].abilityGraph.activateNode(5)
        heroTeam[1].abilityGraph.setActiveAbility(1, 5)
        heroTeam[1].abilityGraph.activateNode(60)
        heroTeam[1].abilityGraph.setActiveAbility(2, 60)
        heroTeam[2].abilityGraph.activateNode(70)
        heroTeam[2].abilityGraph.setActiveAbility(1, 70)
        heroTeam[2].abilityGraph.activateNode(30)
        heroTeam[2].abilityGraph.setActiveAbility(2, 30)

        val inventory = Inventory()

        val items = GuardiansServiceLocator.items

        inventory.putItemInInventory(items.getItem("sword-barb-steel"))
        inventory.putItemInInventory(items.getItem("bread"))
        inventory.putItemInInventory(items.getItem("bread"))
        inventory.putItemInInventory(items.getItem("potion-blue"))
        inventory.putItemInInventory(items.getItem("potion-blue"))
        inventory.putItemInInventory(items.getItem("potion-red"))
        inventory.putItemInInventory(items.getItem("medicine-blue"))
        inventory.putItemInInventory(items.getItem("angel-tear"))
        inventory.putItemInInventory(items.getItem("guardian-crystal-none"))
        inventory.putItemInInventory(items.getItem("sword-excalibur"))
        inventory.putItemInInventory(items.getItem("sword-wood"))
        inventory.putItemInInventory(items.getItem("claws-rusty"))
        inventory.putItemInInventory(items.getItem("sword-silver"))
        inventory.putItemInInventory(items.getItem("sword-knightly-steel"))
        inventory.putItemInInventory(items.getItem("relict-earth"))
        inventory.putItemInInventory(items.getItem("relict-demon"))
        inventory.putItemInInventory(items.getItem("relict-lightning"))
        inventory.putItemInInventory(items.getItem("relict-water"))
        inventory.putItemInInventory(items.getItem("helmet-iron"))
        inventory.putItemInInventory(items.getItem("shield-iron"))
        inventory.putItemInInventory(items.getItem("shoes-leather"))

        inventory.sortItemsByID()
        val ivs = InventoryScreen(heroTeam, inventory)
        game.screen = ivs
    }

    private fun setUpTestBattle()
    {
        val ACTIVE_TEAM_SIZE_HERO = 3
        val guardianFactory = GuardiansServiceLocator.guardianFactory
        val heroTeam = Team(3, 3, ACTIVE_TEAM_SIZE_HERO)
        heroTeam += guardianFactory.createGuardian(1, 1)
        heroTeam += guardianFactory.createGuardian(2, 1)
        heroTeam += guardianFactory.createGuardian(3, 1)

        // Enable Abilities
        heroTeam[0].abilityGraph.activateNode(13)
        heroTeam[0].abilityGraph.setActiveAbility(1, 13)
        heroTeam[0].abilityGraph.activateNode(5)
        heroTeam[0].abilityGraph.setActiveAbility(2, 5)
        heroTeam[0].abilityGraph.activateNode(20)
        heroTeam[0].abilityGraph.setActiveAbility(3, 20)
        heroTeam[0].abilityGraph.activateNode(30)
        heroTeam[0].abilityGraph.setActiveAbility(4, 30)
        heroTeam[0].abilityGraph.activateNode(35)
        heroTeam[0].abilityGraph.setActiveAbility(5, 35)
        heroTeam[0].individualStatistics.earnEXP(4)
        heroTeam[1].abilityGraph.activateNode(5)
        heroTeam[1].abilityGraph.setActiveAbility(1, 5)
        heroTeam[1].abilityGraph.activateNode(60)
        heroTeam[1].abilityGraph.setActiveAbility(2, 60)
        heroTeam[2].abilityGraph.activateNode(70)
        heroTeam[2].abilityGraph.setActiveAbility(1, 70)
        heroTeam[2].abilityGraph.activateNode(30)
        heroTeam[2].abilityGraph.setActiveAbility(2, 30)

        val oppoTeam = Team(3, 3, 1)
        oppoTeam += guardianFactory.createGuardian(4, 1)
        oppoTeam += guardianFactory.createGuardian(5, 1)
        oppoTeam += guardianFactory.createGuardian(6, 1)
        oppoTeam[0].abilityGraph.activateNode(13)
        oppoTeam[0].abilityGraph.setActiveAbility(1, 13)

        val inventory = Inventory()

        val itemService = GuardiansServiceLocator.items

        inventory.putItemInInventory(itemService.getItem("sword-barb-steel"))
        inventory.putItemInInventory(itemService.getItem("bread"))
        inventory.putItemInInventory(itemService.getItem("bread"))
        inventory.putItemInInventory(itemService.getItem("potion-blue"))
        inventory.putItemInInventory(itemService.getItem("potion-blue"))
        inventory.putItemInInventory(itemService.getItem("potion-red"))
        inventory.putItemInInventory(itemService.getItem("medicine-blue"))
        inventory.putItemInInventory(itemService.getItem("angel-tear"))
        inventory.putItemInInventory(itemService.getItem("guardian-crystal-none"))

        val battleScreen = BattleScreen(inventory)
        battleScreen.init(heroTeam, oppoTeam)
        game.screen = battleScreen
    }

    private fun setUpTestWorld()
    {
        if(SaveGameManager.doesGameSaveExist())
        {
            val state = SaveGameManager.loadSaveGame()
            game.screen = WorldScreen(state.map, 1, true)
        }
        else
            game.screen = WorldScreen(Constant.startMap, 1, false)
    }

    private fun setUpTestWorldUI()
    {
        game.screen = WorldScreen(1, 1, false)
    }

    private fun TestBattleSystem()
    {
        //        TeamComponent heroTeam = new TeamComponent();
        //        heroTeam.team.put(0,BattleFactory.getInstance().createGuardian(1));
        //        heroTeam.team.put(1,BattleFactory.getInstance().createGuardian(2));
        //        heroTeam.team.put(2,BattleFactory.getInstance().createGuardian(3));
        //        Team oppTeam = new Team(3,3,3);
        //        oppTeam.put(0,BattleFactory.getInstance().createGuardian(4));
        //        oppTeam.put(1,BattleFactory.getInstance().createGuardian(5));
        //        oppTeam.put(2,BattleFactory.getInstance().createGuardian(6));
        //
        //        BattleSystem bs = new BattleSystem(heroTeam.team, oppTeam, new BattleSystem.Callbacks() {});
        //
        //        boolean enemyFit = true;
        //        while(enemyFit) {
        //            System.out.println("\n### Player's turn ###");
        //            Guardian m = bs.getActiveMonster();
        //            int att = MathUtils.random(0,m.abilityGraph.learntAbilities.size-1);
        //            Array<Guardian> targets = new Array<Guardian>();
        //            for(Guardian h : oppTeam.values()) {
        //                if(h.stat.isFit()) {
        //                    targets.add(h);
        //                }
        //            }
        //            Guardian target = targets.get(MathUtils.random(0,targets.size-1));
        //            bs.attack(target,att);
        //        }
    }


    fun testMonsterParsing()
    {
        val mi = GuardianFactory
        println("Tested")
    }

    fun testResultScreen()
    {
        val guardianFactory = GuardiansServiceLocator.guardianFactory
        val heroTeam = Team(3, 3, 3)
        heroTeam += guardianFactory.createGuardian(1, 1)
        heroTeam += guardianFactory.createGuardian(2, 1)
        heroTeam += guardianFactory.createGuardian(3, 1)
        heroTeam[0].abilityGraph.activateNode(13)
        heroTeam[0].abilityGraph.setActiveAbility(6, 1)

        val oppoTeam = Team(3, 3, 3)
        oppoTeam += guardianFactory.createGuardian(4, 1)
        oppoTeam += guardianFactory.createGuardian(5, 1)
        oppoTeam += guardianFactory.createGuardian(6, 1)

        val inventory = Inventory()

        val itemService = GuardiansServiceLocator.items

        inventory.putItemInInventory(itemService.getItem("sword-barb-steel"))
        inventory.putItemInInventory(itemService.getItem("bread"))
        inventory.putItemInInventory(itemService.getItem("bread"))
        inventory.putItemInInventory(itemService.getItem("potion-blue"))
        inventory.putItemInInventory(itemService.getItem("potion-blue"))
        inventory.putItemInInventory(itemService.getItem("potion-red"))
        inventory.putItemInInventory(itemService.getItem("medicine-blue"))
        inventory.putItemInInventory(itemService.getItem("angel-tear"))

        val droppedItems = Array<Item>()
        droppedItems.add(itemService.getItem("bread"))

        val result = BattleResult(heroTeam, droppedItems)
        result.gainEXP(heroTeam[0], 222)
        result.gainEXP(heroTeam[1], 333)
        result.gainEXP(heroTeam[2], 444)
        Services.getScreenManager().pushScreen(BattleResultScreen(heroTeam, result))
    }

    private fun testMetamorphosisScreen()
    {
        game.screen = MetamorphosisScreen(5, 6)
    }

    private fun testGuardoSphereScreen()
    {

        val guardianFactory = GuardiansServiceLocator.guardianFactory
        val heroTeam = Team(3, 3, 3)
        heroTeam += guardianFactory.createGuardian(1, 1)
        heroTeam += guardianFactory.createGuardian(2, 1)
        heroTeam += guardianFactory.createGuardian(3, 1)
        heroTeam[0].abilityGraph.activateNode(13)
        heroTeam[0].abilityGraph.setActiveAbility(6, 1)

        val guardoSphere = GuardoSphere()

        guardoSphere[0] = guardianFactory.createGuardian(5,1);
        guardoSphere[2] = guardianFactory.createGuardian(7,1);
        guardoSphere[7] = guardianFactory.createGuardian(8,1);

        game.screen = GuardoSphereScreen(heroTeam, guardoSphere)
    }

    fun startDebugging()
    {
        when(DEBUG_MODE)
        {
            DebugMode.BATTLE            -> setUpTestBattle()
            DebugMode.INVENTORY         -> setUpTestInventory()
            DebugMode.WORLD_UI          -> setUpTestWorldUI()
            DebugMode.BATTLE_SYSTEM     -> TestBattleSystem()
            DebugMode.MONSTER_PARSING   -> testMonsterParsing()
            DebugMode.RESULT_SCREEN     -> testResultScreen()
            DebugMode.METAMORPHOSIS     -> testMetamorphosisScreen()
            DebugMode.GUARDOSPHERE      -> testGuardoSphereScreen()
            else                        -> setUpTestWorld()
        }
    }
}
