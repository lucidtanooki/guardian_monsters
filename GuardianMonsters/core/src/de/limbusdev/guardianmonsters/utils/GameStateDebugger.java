package de.limbusdev.guardianmonsters.utils;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.fwmengine.battle.control.BattleSystem;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.AttackCalculationReport;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.BattleFactory;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.BattleScreen;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.InventoryScreen;
import de.limbusdev.guardianmonsters.fwmengine.world.ui.WorldScreen;
import de.limbusdev.guardianmonsters.model.Ability;
import de.limbusdev.guardianmonsters.model.AttackInfo;
import de.limbusdev.guardianmonsters.model.Inventory;
import de.limbusdev.guardianmonsters.model.ItemDB;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterDB;

/**
 * Created by georg on 15.11.16.
 */

public class GameStateDebugger {
    private Game game;

    public GameStateDebugger(Game game) {
        this.game = game;
    }

    private void setUpTestInventory() {
        TeamComponent herTeam = new TeamComponent();
        Monster mon = BattleFactory.getInstance().createMonster(1);
        mon.abilityGraph.activateNode(0);
        mon.abilityGraph.activateNode(1);
        mon.abilityGraph.activateNode(5);
        mon.abilityGraph.activateNode(9);
        mon.abilityGraph.activateNode(17);
        mon.abilityGraph.activateNode(21);
        mon.abilityGraph.activateNode(2);
        mon.abilityGraph.activateNode(3);
        mon.abilityGraph.activateNode(4);
        mon.abilityGraph.activateNode(3);
        mon.abilityGraph.activateNode(7);
        herTeam.monsters.put(0,mon);
        herTeam.monsters.put(1,BattleFactory.getInstance().createMonster(2));
        herTeam.monsters.put(2,BattleFactory.getInstance().createMonster(3));

        Inventory inventory = new Inventory();

        inventory.putItemInInventory(ItemDB.singleton().getItem("sword-barb-steel"));
        inventory.putItemInInventory(ItemDB.singleton().getItem("sword-excalibur"));
        inventory.putItemInInventory(ItemDB.singleton().getItem("bread"));
        inventory.putItemInInventory(ItemDB.singleton().getItem("bread"));
        inventory.putItemInInventory(ItemDB.singleton().getItem("bread"));
        inventory.putItemInInventory(ItemDB.singleton().getItem("bread"));
        inventory.putItemInInventory(ItemDB.singleton().getItem("potion-blue"));
        inventory.putItemInInventory(ItemDB.singleton().getItem("potion-blue"));
        inventory.putItemInInventory(ItemDB.singleton().getItem("potion-blue"));
        inventory.putItemInInventory(ItemDB.singleton().getItem("potion-red"));
        inventory.putItemInInventory(ItemDB.singleton().getItem("medicine-blue"));
        inventory.putItemInInventory(ItemDB.singleton().getItem("angel-tear"));
        inventory.putItemInInventory(ItemDB.singleton().getItem("sword-wood"));
        inventory.putItemInInventory(ItemDB.singleton().getItem("claws-rusty"));
        inventory.putItemInInventory(ItemDB.singleton().getItem("sword-silver"));
        inventory.putItemInInventory(ItemDB.singleton().getItem("sword-knightly-steel"));
        inventory.putItemInInventory(ItemDB.singleton().getItem("relict-earth"));
        inventory.putItemInInventory(ItemDB.singleton().getItem("relict-demon"));
        inventory.putItemInInventory(ItemDB.singleton().getItem("relict-lightning"));
        inventory.putItemInInventory(ItemDB.singleton().getItem("relict-water"));
        inventory.putItemInInventory(ItemDB.singleton().getItem("helmet-iron"));
        inventory.putItemInInventory(ItemDB.singleton().getItem("shield-iron"));
        inventory.putItemInInventory(ItemDB.singleton().getItem("shoes-leather"));

        inventory.sortItemsByID();
        InventoryScreen ivs = new InventoryScreen(herTeam, inventory);
        game.setScreen(ivs);
    }

    private void setUpTestBattle() {
        TeamComponent heroTeam = new TeamComponent();
        heroTeam.monsters.put(0,BattleFactory.getInstance().createMonster(1));
        heroTeam.monsters.put(1,BattleFactory.getInstance().createMonster(2));
        heroTeam.monsters.put(2,BattleFactory.getInstance().createMonster(3));
        TeamComponent oppTeam = new TeamComponent();
        oppTeam.monsters.put(0,BattleFactory.getInstance().createMonster(4));
        oppTeam.monsters.put(1,BattleFactory.getInstance().createMonster(5));
        oppTeam.monsters.put(2,BattleFactory.getInstance().createMonster(6));
        BattleScreen battleScreen = new BattleScreen();
        battleScreen.init(heroTeam, oppTeam);
        game.setScreen(battleScreen);
    }

    private void setUpTestWorld() {
        if(SaveGameManager.doesGameSaveExist()) {
            GameState state = SaveGameManager.loadSaveGame();
            game.setScreen(new WorldScreen(state.map, 1, true));
        } else
            game.setScreen(new WorldScreen(GS.startMap, 1, false));
    }

    private void setUpTestWorldUI() {
        game.setScreen(new WorldScreen(1, 1, false));
    }

    private void TestBattleSystem() {
        TeamComponent heroTeam = new TeamComponent();
        heroTeam.monsters.put(0,BattleFactory.getInstance().createMonster(1));
        heroTeam.monsters.put(1,BattleFactory.getInstance().createMonster(2));
        heroTeam.monsters.put(2,BattleFactory.getInstance().createMonster(3));
        TeamComponent oppTeam = new TeamComponent();
        oppTeam.monsters.put(0,BattleFactory.getInstance().createMonster(4));
        oppTeam.monsters.put(1,BattleFactory.getInstance().createMonster(5));
        oppTeam.monsters.put(2,BattleFactory.getInstance().createMonster(6));

        BattleSystem bs = new BattleSystem(heroTeam.monsters, oppTeam.monsters, new BattleSystem.CallbackHandler() {
            @Override
            public void onDefense(Monster defensiveMonster) {

            }

            @Override
            public void onMonsterKilled(Monster m) {
                // TODO
            }

            @Override
            public void onQueueUpdated() {
                // TODO
            }

            @Override
            public void onAttack(Monster attacker, Monster target, Ability attack, AttackCalculationReport rep) {
                // TODO
            }

            @Override
            public void onPlayersTurn() {
                // TODO
            }

            @Override
            public void onBattleEnds(boolean winnerSide) {
                // TODO
            }
        });

        boolean enemyFit = true;
        while(enemyFit) {
            System.out.println("\n### Player's turn ###");
            Monster m = bs.getActiveMonster();
            int att = MathUtils.random(0,m.abilityGraph.learntAbilities.size-1);
            Array<Monster> targets = new Array<Monster>();
            for(Monster h : oppTeam.monsters.values()) {
                if(h.getHP() > 0) {
                    targets.add(h);
                }
            }
            Monster target = targets.get(MathUtils.random(0,targets.size-1));
            bs.attack(target,att);
        }
    }

    public void testAttackParsing() {
        AttackInfo ai = AttackInfo.getInst();
    }

    public void testMonsterParsing() {
        MonsterDB mi = MonsterDB.singleton();
        System.out.println("Tested");
    }

    public void startDebugging() {
        switch(GS.DEBUG_MODE) {
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
            default:
                setUpTestWorld();
                break;
        }
    }
}
