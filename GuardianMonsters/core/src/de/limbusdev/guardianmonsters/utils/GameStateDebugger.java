package de.limbusdev.guardianmonsters.utils;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.Constant;
import de.limbusdev.guardianmonsters.fwmengine.battle.control.BattleSystem;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.BattleResult;
import de.limbusdev.guardianmonsters.fwmengine.battleresult.BattleResultScreen;
import de.limbusdev.guardianmonsters.fwmengine.guardosphere.GuardoSphereScreen;
import de.limbusdev.guardianmonsters.fwmengine.metamorphosis.MetamorphosisScreen;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.BattleFactory;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.BattleScreen;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.InventoryScreen;
import de.limbusdev.guardianmonsters.fwmengine.world.ui.WorldScreen;
import de.limbusdev.guardianmonsters.guardians.AbilityDB;
import de.limbusdev.guardianmonsters.model.gamestate.GameState;
import de.limbusdev.guardianmonsters.guardians.items.Inventory;
import de.limbusdev.guardianmonsters.guardians.ItemDB;
import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.guardians.monsters.Monster;
import de.limbusdev.guardianmonsters.guardians.MonsterDB;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;

/**
 * @author Georg Eckert 2016
 */

public class GameStateDebugger {
    private Game game;

    public GameStateDebugger(Game game) {
        this.game = game;
    }

    private void setUpTestInventory() {
        TeamComponent herTeam = new TeamComponent();
        Monster mon = BattleFactory.getInstance().createMonster(1);
        mon.abilityGraph.activateNode(1);
        mon.abilityGraph.activateNode(5);
        mon.abilityGraph.activateNode(9);
        mon.abilityGraph.activateNode(13);
        mon.abilityGraph.activateNode(17);
        mon.abilityGraph.activateNode(21);
        mon.abilityGraph.activateNode(2);
        mon.abilityGraph.activateNode(3);
        mon.abilityGraph.activateNode(4);
        mon.abilityGraph.activateNode(3);
        mon.abilityGraph.activateNode(7);
        herTeam.team.put(0,mon);
        herTeam.team.put(1,BattleFactory.getInstance().createMonster(2));
        herTeam.team.put(2,BattleFactory.getInstance().createMonster(3));

        Inventory inventory = new Inventory();

        inventory.putItemInInventory(ItemDB.getInstance().getItem("sword-barb-steel"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("sword-excalibur"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("bread"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("bread"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("bread"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("bread"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("potion-blue"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("potion-blue"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("potion-blue"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("potion-red"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("medicine-blue"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("angel-tear"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("sword-wood"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("claws-rusty"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("sword-silver"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("sword-knightly-steel"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("relict-earth"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("relict-demon"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("relict-lightning"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("relict-water"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("helmet-iron"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("shield-iron"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("shoes-leather"));

        inventory.sortItemsByID();
        InventoryScreen ivs = new InventoryScreen(herTeam, inventory);
        game.setScreen(ivs);
    }

    private void setUpTestBattle() {
        Team heroTeam = new Team(3,3,3);
        heroTeam.put(0,BattleFactory.getInstance().createMonster(1));
        heroTeam.put(1,BattleFactory.getInstance().createMonster(2));
        heroTeam.put(2,BattleFactory.getInstance().createMonster(3));
        heroTeam.get(0).abilityGraph.activateNode(13);
        heroTeam.get(0).abilityGraph.setActiveAbility(6,1);

        Team oppTeam = new Team(3,3,3);
        oppTeam.put(0,BattleFactory.getInstance().createMonster(4));
        oppTeam.put(1,BattleFactory.getInstance().createMonster(5));
        oppTeam.put(2,BattleFactory.getInstance().createMonster(6));


        Inventory inventory = new Inventory();

        inventory.putItemInInventory(ItemDB.getInstance().getItem("sword-barb-steel"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("bread"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("bread"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("potion-blue"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("potion-blue"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("potion-red"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("medicine-blue"));
        inventory.putItemInInventory(ItemDB.getInstance().getItem("angel-tear"));

        int i = MonsterDB.getNumberOfAncestors(1);
        i = MonsterDB.getNumberOfAncestors(2);
        i = MonsterDB.getNumberOfAncestors(3);
        i = MonsterDB.getNumberOfAncestors(4);
        i = MonsterDB.getNumberOfAncestors(5);
        i = MonsterDB.getNumberOfAncestors(6);



        BattleScreen battleScreen = new BattleScreen(inventory);
        battleScreen.init(heroTeam, oppTeam);
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
        TeamComponent heroTeam = new TeamComponent();
        heroTeam.team.put(0,BattleFactory.getInstance().createMonster(1));
        heroTeam.team.put(1,BattleFactory.getInstance().createMonster(2));
        heroTeam.team.put(2,BattleFactory.getInstance().createMonster(3));
        Team oppTeam = new Team(3,3,3);
        oppTeam.put(0,BattleFactory.getInstance().createMonster(4));
        oppTeam.put(1,BattleFactory.getInstance().createMonster(5));
        oppTeam.put(2,BattleFactory.getInstance().createMonster(6));

        BattleSystem bs = new BattleSystem(heroTeam.team, oppTeam, new BattleSystem.Callbacks() {});

        boolean enemyFit = true;
        while(enemyFit) {
            System.out.println("\n### Player's turn ###");
            Monster m = bs.getActiveMonster();
            int att = MathUtils.random(0,m.abilityGraph.learntAbilities.size-1);
            Array<Monster> targets = new Array<Monster>();
            for(Monster h : oppTeam.values()) {
                if(h.stat.isFit()) {
                    targets.add(h);
                }
            }
            Monster target = targets.get(MathUtils.random(0,targets.size-1));
            bs.attack(target,att);
        }
    }

    public void testAttackParsing() {
        AbilityDB ai = AbilityDB.getInstance();
    }

    public void testMonsterParsing() {
        MonsterDB mi = MonsterDB.getInstance();
        System.out.println("Tested");
    }

    public void testResultScreen() {
        Team team = new Team(7,1,1);
        Monster mon = BattleFactory.getInstance().createMonster(1);
        mon.abilityGraph.activateNode(1);
        team.put(0,mon);
        team.put(1,BattleFactory.getInstance().createMonster(2));
        team.put(2,BattleFactory.getInstance().createMonster(3));
        Array<Item> droppedItems = new Array<>();
        droppedItems.add(ItemDB.getItem("bread"));

        BattleResult result = new BattleResult(team, droppedItems);
        result.gainEXP(mon,1200);
        result.gainEXP(team.get(1), 1200);
        result.gainEXP(team.get(2), 1200);
        game.setScreen(new BattleResultScreen(team, result));
    }

    private void testMetamorphosisScreen() {
        game.setScreen(new MetamorphosisScreen(5,6));
    }

    private void testGuardoSphereScreen() {
        Team heroTeam = new Team(3,3,3);
        heroTeam.put(0,BattleFactory.getInstance().createMonster(1));
        heroTeam.put(1,BattleFactory.getInstance().createMonster(2));
        heroTeam.put(2,BattleFactory.getInstance().createMonster(3));

        game.setScreen(new GuardoSphereScreen(heroTeam, null));
    }

    public void startDebugging() {
        switch(de.limbusdev.guardianmonsters.Constant.DEBUG_MODE) {
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
            case ATTACK_PARSING:
                testAttackParsing();
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
