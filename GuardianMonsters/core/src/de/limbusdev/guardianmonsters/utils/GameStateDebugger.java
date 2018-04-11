package de.limbusdev.guardianmonsters.utils;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import de.limbusdev.guardianmonsters.Constant;
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.fwmengine.metamorphosis.MetamorphosisScreen;
import de.limbusdev.guardianmonsters.fwmengine.world.ui.WorldScreen;
import de.limbusdev.guardianmonsters.guardians.monsters.JSONGuardianParser;
import de.limbusdev.guardianmonsters.model.gamestate.GameState;

/**
 * @author Georg Eckert 2016
 */

public class GameStateDebugger {
    private Game game;

    public GameStateDebugger(Game game) {
        this.game = game;
    }

    private void setUpTestInventory() {
//        TeamComponent herTeam = new TeamComponent();
//        Guardian mon = BattleFactory.getInstance().createGuardian(1);
//        mon.abilityGraph.activateNode(1);
//        mon.abilityGraph.activateNode(5);
//        mon.abilityGraph.activateNode(9);
//        mon.abilityGraph.activateNode(13);
//        mon.abilityGraph.activateNode(17);
//        mon.abilityGraph.activateNode(21);
//        mon.abilityGraph.activateNode(2);
//        mon.abilityGraph.activateNode(3);
//        mon.abilityGraph.activateNode(4);
//        mon.abilityGraph.activateNode(3);
//        mon.abilityGraph.activateNode(7);
//        herTeam.team.put(0,mon);
//        herTeam.team.put(1,BattleFactory.getInstance().createGuardian(2));
//        herTeam.team.put(2,BattleFactory.getInstance().createGuardian(3));
//
//        Inventory inventory = new Inventory();
//
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("sword-barb-steel"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("sword-excalibur"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("bread"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("bread"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("bread"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("bread"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("potion-blue"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("potion-blue"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("potion-blue"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("potion-red"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("medicine-blue"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("angel-tear"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("sword-wood"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("claws-rusty"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("sword-silver"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("sword-knightly-steel"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("relict-earth"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("relict-demon"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("relict-lightning"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("relict-water"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("helmet-iron"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("shield-iron"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("shoes-leather"));
//
//        inventory.sortItemsByID();
//        InventoryScreen ivs = new InventoryScreen(herTeam, inventory);
//        game.setScreen(ivs);
    }

    private void setUpTestBattle() {
//        Team heroTeam = new Team(3,3,3);
//        heroTeam.put(0,BattleFactory.getInstance().createGuardian(1));
//        heroTeam.put(1,BattleFactory.getInstance().createGuardian(2));
//        heroTeam.put(2,BattleFactory.getInstance().createGuardian(3));
//        heroTeam.get(0).abilityGraph.activateNode(13);
//        heroTeam.get(0).abilityGraph.setActiveAbility(6,1);
//
//        Team oppTeam = new Team(3,3,3);
//        oppTeam.put(0,BattleFactory.getInstance().createGuardian(4));
//        oppTeam.put(1,BattleFactory.getInstance().createGuardian(5));
//        oppTeam.put(2,BattleFactory.getInstance().createGuardian(6));
//
//
//        Inventory inventory = new Inventory();
//
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("sword-barb-steel"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("bread"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("bread"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("potion-blue"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("potion-blue"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("potion-red"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("medicine-blue"));
//        inventory.putItemInInventory(ItemDB.getInstance().getItem("angel-tear"));
//
//        int i = GuardianDB.getNumberOfAncestors(1);
//        i = GuardianDB.getNumberOfAncestors(2);
//        i = GuardianDB.getNumberOfAncestors(3);
//        i = GuardianDB.getNumberOfAncestors(4);
//        i = GuardianDB.getNumberOfAncestors(5);
//        i = GuardianDB.getNumberOfAncestors(6);
//
//
//
//        BattleScreen battleScreen = new BattleScreen(inventory);
//        battleScreen.init(heroTeam, oppTeam);
//        game.setScreen(battleScreen);
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
//        AGuardianFactory mi = GuardianFactory.getInstance();
//        System.out.println("Tested");


        String json = Gdx.files.internal("data/guardians.json").readString();
        JSONGuardianParser.parseGuardianList(json);
    }

    public void testResultScreen() {
//        Team team = new Team(7,1,1);
//        Guardian mon = BattleFactory.getInstance().createGuardian(1);
//        mon.abilityGraph.activateNode(1);
//        team.put(0,mon);
//        team.put(1,BattleFactory.getInstance().createGuardian(2));
//        team.put(2,BattleFactory.getInstance().createGuardian(3));
//        Array<Item> droppedItems = new Array<>();
//        droppedItems.add(ItemDB.getItem("bread"));
//
//        BattleResult result = new BattleResult(team, droppedItems);
//        result.gainEXP(mon,1200);
//        result.gainEXP(team.get(1), 1200);
//        result.gainEXP(team.get(2), 1200);
//        game.setScreen(new BattleResultScreen(team, result));
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
