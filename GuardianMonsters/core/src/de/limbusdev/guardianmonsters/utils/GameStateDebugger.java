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
import de.limbusdev.guardianmonsters.fwmengine.world.ui.OutdoorGameWorldScreen;
import de.limbusdev.guardianmonsters.model.Attack;
import de.limbusdev.guardianmonsters.model.AttackInfo;
import de.limbusdev.guardianmonsters.model.Inventory;
import de.limbusdev.guardianmonsters.model.Item;
import de.limbusdev.guardianmonsters.model.ItemInfo;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInfo;
import de.limbusdev.guardianmonsters.model.MonsterStatusInformation;

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
        mon.activateAbilityNode(1);
        mon.activateAbilityNode(2);
        mon.activateAbilityNode(3);
        herTeam.monsters.put(0,mon);
        herTeam.monsters.put(1,BattleFactory.getInstance().createMonster(2));
        herTeam.monsters.put(2,BattleFactory.getInstance().createMonster(3));

        Inventory inventory = new Inventory();

        inventory.putItemInInventory(ItemInfo.getInst().getItem("sword-barb-steel"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("sword-excalibur"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("bread"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("bread"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("bread"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("bread"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("potion-blue"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("potion-blue"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("potion-blue"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("potion-red"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("medicine-blue"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("angel-tear"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("sword-wood"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("claws-rusty"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("sword-silver"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("sword-knightly-steel"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("relict-earth"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("relict-demon"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("relict-lightning"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("relict-water"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("helmet-iron"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("shield-iron"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("shoes-leather"));

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
            game.setScreen(new OutdoorGameWorldScreen(state.map, 1, true));
        } else
            game.setScreen(new OutdoorGameWorldScreen(GS.startMap, 1, false));
    }

    private void setUpTestWorldUI() {
        game.setScreen(new OutdoorGameWorldScreen(1, 1, false));
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
            public void onAttack(Monster attacker, Monster target, Attack attack, AttackCalculationReport rep) {
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
            int att = MathUtils.random(0,m.attacks.size-1);
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
        MonsterInfo mi = MonsterInfo.getInstance();
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
