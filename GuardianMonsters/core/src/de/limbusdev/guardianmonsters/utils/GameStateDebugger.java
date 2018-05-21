package de.limbusdev.guardianmonsters.utils;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.Constant;
import de.limbusdev.guardianmonsters.battle.BattleResultScreen;
import de.limbusdev.guardianmonsters.battle.BattleScreen;
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ui.WorldScreen;
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.battle.BattleFactory;
import de.limbusdev.guardianmonsters.guardians.battle.BattleResult;
import de.limbusdev.guardianmonsters.guardians.items.IItemService;
import de.limbusdev.guardianmonsters.guardians.items.Inventory;
import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardianFactory;
import de.limbusdev.guardianmonsters.guardians.monsters.GuardianFactory;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;
import de.limbusdev.guardianmonsters.model.gamestate.GameState;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.ui.metamorphosis.MetamorphosisScreen;
import main.java.de.limbusdev.guardianmonsters.inventory.InventoryScreen;

import static de.limbusdev.guardianmonsters.Constant.DEBUG_MODE;

/**
 * @author Georg Eckert 2016
 */

public class GameStateDebugger
{
    private Game game;

    public GameStateDebugger(Game game) {
        this.game = game;
    }

    private void setUpTestInventory()
    {
        int ACTIVE_TEAM_SIZE_HERO = 3;
        AGuardianFactory guardianFactory = GuardiansServiceLocator.getGuardianFactory();
        Team heroTeam = new Team(3,3,ACTIVE_TEAM_SIZE_HERO);
        heroTeam.put(0, guardianFactory.createGuardian(1,1));
        heroTeam.put(1, guardianFactory.createGuardian(2,1));
        heroTeam.put(2, guardianFactory.createGuardian(3,1));

        // Enable Abilities
        heroTeam.get(0).getAbilityGraph().activateNode(0);
        heroTeam.get(0).getAbilityGraph().activateNode(13);
        heroTeam.get(0).getAbilityGraph().activateNode(11);
        heroTeam.get(0).getAbilityGraph().activateNode(15);
        heroTeam.get(0).getAbilityGraph().setActiveAbility(1,13);
        heroTeam.get(0).getAbilityGraph().activateNode(5);
        heroTeam.get(0).getAbilityGraph().setActiveAbility(2,5);
        heroTeam.get(0).getAbilityGraph().activateNode(20);
        heroTeam.get(0).getAbilityGraph().setActiveAbility(3,20);
//        heroTeam.get(0).getAbilityGraph().activateNode(30);
//        heroTeam.get(0).getAbilityGraph().setActiveAbility(4,30);
//        heroTeam.get(0).getAbilityGraph().activateNode(35);
//        heroTeam.get(0).getAbilityGraph().setActiveAbility(5,35);
        heroTeam.get(0).getAbilityGraph().activateNode(21);
        heroTeam.get(0).getAbilityGraph().activateNode(23);
        heroTeam.get(0).getAbilityGraph().activateNode(89);
        heroTeam.get(0).getAbilityGraph().activateNode(90);
        heroTeam.get(0).getAbilityGraph().activateNode(80);
        heroTeam.get(0).getAbilityGraph().activateNode(81);
        heroTeam.get(0).getAbilityGraph().activateNode(82);
        heroTeam.get(0).getAbilityGraph().activateNode(83);
        heroTeam.get(0).getAbilityGraph().activateNode(84);
        heroTeam.get(0).getAbilityGraph().activateNode(85);
        heroTeam.get(0).getIndividualStatistics().earnEXP(200);
        heroTeam.get(0).getIndividualStatistics().levelUp();
        heroTeam.get(0).getIndividualStatistics().levelUp();
        heroTeam.get(0).getIndividualStatistics().levelUp();
        heroTeam.get(0).getIndividualStatistics().levelUp();
        heroTeam.get(0).getIndividualStatistics().levelUp();
        heroTeam.get(0).getIndividualStatistics().levelUp();
        heroTeam.get(0).getIndividualStatistics().levelUp();
        heroTeam.get(0).getIndividualStatistics().levelUp();
        heroTeam.get(0).getIndividualStatistics().levelUp();
        heroTeam.get(0).getIndividualStatistics().levelUp();
        heroTeam.get(0).getIndividualStatistics().levelUp();
        heroTeam.get(0).getIndividualStatistics().levelUp();
        heroTeam.get(0).getIndividualStatistics().levelUp();
        heroTeam.get(0).getIndividualStatistics().levelUp();

        heroTeam.get(0).getAbilityGraph().activateNode(91);


        heroTeam.get(1).getAbilityGraph().activateNode(5);
        heroTeam.get(1).getAbilityGraph().setActiveAbility(1,5);
        heroTeam.get(1).getAbilityGraph().activateNode(60);
        heroTeam.get(1).getAbilityGraph().setActiveAbility(2,60);
        heroTeam.get(2).getAbilityGraph().activateNode(70);
        heroTeam.get(2).getAbilityGraph().setActiveAbility(1,70);
        heroTeam.get(2).getAbilityGraph().activateNode(30);
        heroTeam.get(2).getAbilityGraph().setActiveAbility(2,30);



        Inventory inventory = new Inventory();

        IItemService itemService = GuardiansServiceLocator.getItems();

        inventory.putItemInInventory(itemService.getItem("sword-barb-steel"));
        inventory.putItemInInventory(itemService.getItem("bread"));
        inventory.putItemInInventory(itemService.getItem("bread"));
        inventory.putItemInInventory(itemService.getItem("potion-blue"));
        inventory.putItemInInventory(itemService.getItem("potion-blue"));
        inventory.putItemInInventory(itemService.getItem("potion-red"));
        inventory.putItemInInventory(itemService.getItem("medicine-blue"));
        inventory.putItemInInventory(itemService.getItem("angel-tear"));
        inventory.putItemInInventory(itemService.getItem("guardian-crystal-none"));
        inventory.putItemInInventory(itemService.getItem("sword-excalibur"));
        inventory.putItemInInventory(itemService.getItem("sword-wood"));
        inventory.putItemInInventory(itemService.getItem("claws-rusty"));
        inventory.putItemInInventory(itemService.getItem("sword-silver"));
        inventory.putItemInInventory(itemService.getItem("sword-knightly-steel"));
        inventory.putItemInInventory(itemService.getItem("relict-earth"));
        inventory.putItemInInventory(itemService.getItem("relict-demon"));
        inventory.putItemInInventory(itemService.getItem("relict-lightning"));
        inventory.putItemInInventory(itemService.getItem("relict-water"));
        inventory.putItemInInventory(itemService.getItem("helmet-iron"));
        inventory.putItemInInventory(itemService.getItem("shield-iron"));
        inventory.putItemInInventory(itemService.getItem("shoes-leather"));

        inventory.sortItemsByID();
        InventoryScreen ivs = new InventoryScreen(heroTeam, inventory);
        game.setScreen(ivs);
    }

    private void setUpTestBattle()
    {
        int ACTIVE_TEAM_SIZE_HERO = 3;
        AGuardianFactory guardianFactory = GuardiansServiceLocator.getGuardianFactory();
        Team heroTeam = new Team(3,3,ACTIVE_TEAM_SIZE_HERO);
        heroTeam.put(0, guardianFactory.createGuardian(1,1));
        heroTeam.put(1, guardianFactory.createGuardian(2,1));
        heroTeam.put(2, guardianFactory.createGuardian(3,1));

        // Enable Abilities
        heroTeam.get(0).getAbilityGraph().activateNode(13);
        heroTeam.get(0).getAbilityGraph().setActiveAbility(1,13);
        heroTeam.get(0).getAbilityGraph().activateNode(5);
        heroTeam.get(0).getAbilityGraph().setActiveAbility(2,5);
        heroTeam.get(0).getAbilityGraph().activateNode(20);
        heroTeam.get(0).getAbilityGraph().setActiveAbility(3,20);
        heroTeam.get(0).getAbilityGraph().activateNode(30);
        heroTeam.get(0).getAbilityGraph().setActiveAbility(4,30);
        heroTeam.get(0).getAbilityGraph().activateNode(35);
        heroTeam.get(0).getAbilityGraph().setActiveAbility(5,35);
        heroTeam.get(0).getIndividualStatistics().earnEXP(4);
        heroTeam.get(1).getAbilityGraph().activateNode(5);
        heroTeam.get(1).getAbilityGraph().setActiveAbility(1,5);
        heroTeam.get(1).getAbilityGraph().activateNode(60);
        heroTeam.get(1).getAbilityGraph().setActiveAbility(2,60);
        heroTeam.get(2).getAbilityGraph().activateNode(70);
        heroTeam.get(2).getAbilityGraph().setActiveAbility(1,70);
        heroTeam.get(2).getAbilityGraph().activateNode(30);
        heroTeam.get(2).getAbilityGraph().setActiveAbility(2,30);

        Team oppoTeam = new Team(3,3,1);
        oppoTeam.put(0, guardianFactory.createGuardian(4,1));
        oppoTeam.put(1, guardianFactory.createGuardian(5,1));
        oppoTeam.put(2, guardianFactory.createGuardian(6,1));
        oppoTeam.get(0).getAbilityGraph().activateNode(13);
        oppoTeam.get(0).getAbilityGraph().setActiveAbility(1,13);


        Inventory inventory = new Inventory();

        IItemService itemService = GuardiansServiceLocator.getItems();

        inventory.putItemInInventory(itemService.getItem("sword-barb-steel"));
        inventory.putItemInInventory(itemService.getItem("bread"));
        inventory.putItemInInventory(itemService.getItem("bread"));
        inventory.putItemInInventory(itemService.getItem("potion-blue"));
        inventory.putItemInInventory(itemService.getItem("potion-blue"));
        inventory.putItemInInventory(itemService.getItem("potion-red"));
        inventory.putItemInInventory(itemService.getItem("medicine-blue"));
        inventory.putItemInInventory(itemService.getItem("angel-tear"));
        inventory.putItemInInventory(itemService.getItem("guardian-crystal-none"));

        BattleScreen battleScreen = new BattleScreen(inventory);
        battleScreen.init(heroTeam, oppoTeam);
        game.setScreen(battleScreen);
    }

    private void setUpTestWorld() {
        if(SaveGameManager.doesGameSaveExist()) {
            GameState state = SaveGameManager.loadSaveGame();
            game.setScreen(new WorldScreen(state.map, 1, true));
        } else
            game.setScreen(new WorldScreen(Constant.startMap, 1, false));
    }

    private void setUpTestWorldUI() {
        game.setScreen(new WorldScreen(1, 1, false));
    }

    private void TestBattleSystem() {
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


    public void testMonsterParsing() {

        AGuardianFactory mi = GuardianFactory.getInstance();
        System.out.println("Tested");
    }

    public void testResultScreen()
    {

        AGuardianFactory guardianFactory = GuardiansServiceLocator.getGuardianFactory();
        Team heroTeam = new Team(3,3,3);
        heroTeam.put(0, guardianFactory.createGuardian(1,1));
        heroTeam.put(1, guardianFactory.createGuardian(2,1));
        heroTeam.put(2, guardianFactory.createGuardian(3,1));
        heroTeam.get(0).getAbilityGraph().activateNode(13);
        heroTeam.get(0).getAbilityGraph().setActiveAbility(6,1);

        Team oppoTeam = new Team(3,3,3);
        oppoTeam.put(0, guardianFactory.createGuardian(4,1));
        oppoTeam.put(1, guardianFactory.createGuardian(5,1));
        oppoTeam.put(2, guardianFactory.createGuardian(6,1));


        Inventory inventory = new Inventory();

        IItemService itemService = GuardiansServiceLocator.getItems();

        inventory.putItemInInventory(itemService.getItem("sword-barb-steel"));
        inventory.putItemInInventory(itemService.getItem("bread"));
        inventory.putItemInInventory(itemService.getItem("bread"));
        inventory.putItemInInventory(itemService.getItem("potion-blue"));
        inventory.putItemInInventory(itemService.getItem("potion-blue"));
        inventory.putItemInInventory(itemService.getItem("potion-red"));
        inventory.putItemInInventory(itemService.getItem("medicine-blue"));
        inventory.putItemInInventory(itemService.getItem("angel-tear"));

        Array<Item> droppedItems = new Array<>();
        droppedItems.add(itemService.getItem("bread"));

        BattleResult result = new BattleResult(heroTeam, droppedItems);
        result.gainEXP(heroTeam.get(0), 222);
        result.gainEXP(heroTeam.get(1), 333);
        result.gainEXP(heroTeam.get(2), 444);
        Services.getScreenManager().pushScreen(new BattleResultScreen(heroTeam, result));
    }

    private void testMetamorphosisScreen() {
        game.setScreen(new MetamorphosisScreen(5,6));
    }

    private void testGuardoSphereScreen() {
//        Team heroTeam = new Team(3,3,3);
//        heroTeam.put(0,BattleFactory.getInstance().createGuardian(1));
//        heroTeam.put(1,BattleFactory.getInstance().createGuardian(2));
//        heroTeam.put(2,BattleFactory.getInstance().createGuardian(3));
//
//        game.setScreen(new GuardoSphereScreen(heroTeam, null));
    }

    public void startDebugging()
    {
        switch(DEBUG_MODE)
        {
            case BATTLE:
                setUpTestBattle();
                break;
            case INVENTORY:
                setUpTestInventory();
                break;
            case WORLD_UI:
                setUpTestWorldUI();
                break;
            case BATTLE_SYSTEM:
                TestBattleSystem();
                break;
            case MONSTER_PARSING:
                testMonsterParsing();
                break;
            case RESULT_SCREEN:
                testResultScreen();
                break;
            case METAMORPHOSIS:
                testMetamorphosisScreen();
                break;
            case GUARDOSPHERE:
                testGuardoSphereScreen();
                break;
            default:
                setUpTestWorld();
                break;
        }
    }
}
