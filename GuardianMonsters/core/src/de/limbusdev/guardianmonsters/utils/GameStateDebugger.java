package de.limbusdev.guardianmonsters.utils;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.fwmengine.battle.control.BattleSystem;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.BattleFactory;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.BattleScreen;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.InventoryScreen;
import de.limbusdev.guardianmonsters.fwmengine.world.ui.OutdoorGameWorldScreen;
import de.limbusdev.guardianmonsters.model.Attack;
import de.limbusdev.guardianmonsters.model.Monster;

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
        herTeam.monsters.add(BattleFactory.getInstance().createMonster(1));
        herTeam.monsters.add(BattleFactory.getInstance().createMonster(2));
        herTeam.monsters.add(BattleFactory.getInstance().createMonster(3));
        InventoryScreen ivs = new InventoryScreen(herTeam);
        game.setScreen(ivs);
    }

    private void setUpTestBattle() {
        TeamComponent heroTeam = new TeamComponent();
        heroTeam.monsters.add(BattleFactory.getInstance().createMonster(1));
        heroTeam.monsters.add(BattleFactory.getInstance().createMonster(17));
        heroTeam.monsters.add(BattleFactory.getInstance().createMonster(3));
        TeamComponent oppTeam = new TeamComponent();
        oppTeam.monsters.add(BattleFactory.getInstance().createMonster(7));
        oppTeam.monsters.add(BattleFactory.getInstance().createMonster(4));
        oppTeam.monsters.add(BattleFactory.getInstance().createMonster(11));
        BattleScreen battleScreen = new BattleScreen();
        battleScreen.init(heroTeam, oppTeam);
        game.setScreen(battleScreen);
    }

    private void setUpTestWorld() {
        if(SaveGameManager.doesGameSaveExist()) {
            GameState state = SaveGameManager.loadSaveGame();
            game.setScreen(new OutdoorGameWorldScreen(state.map, 1, true));
        } else
            game.setScreen(new OutdoorGameWorldScreen(9, 1, false));
    }

    private void setUpTestWorldUI() {
        game.setScreen(new OutdoorGameWorldScreen(1, 1, false));
    }

    private void TestBattleSystem() {
        TeamComponent heroTeam = new TeamComponent();
        heroTeam.monsters.add(BattleFactory.getInstance().createMonster(1));
        heroTeam.monsters.add(BattleFactory.getInstance().createMonster(17));
        heroTeam.monsters.add(BattleFactory.getInstance().createMonster(3));
        TeamComponent oppTeam = new TeamComponent();
        oppTeam.monsters.add(BattleFactory.getInstance().createMonster(7));
        oppTeam.monsters.add(BattleFactory.getInstance().createMonster(4));
        oppTeam.monsters.add(BattleFactory.getInstance().createMonster(11));

        BattleSystem bs = new BattleSystem(heroTeam.monsters, oppTeam.monsters, new BattleSystem.CallbackHandler() {
            @Override
            public void onNextTurn() {
                // TODO
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
            public void onAttack(Monster attacker, Monster target, Attack attack) {
                // TODO
            }

            @Override
            public void onPlayersTurn() {
                // TODO
            }
        });

        boolean enemyFit = true;
        while(enemyFit) {
            System.out.println("\n### Player's turn ###");
            Monster m = bs.getActiveMonster();
            int att = MathUtils.random(0,m.attacks.size-1);
            Array<Monster> targets = new Array<Monster>();
            for(Monster h : oppTeam.monsters) {
                if(h.getHP() > 0) {
                    targets.add(h);
                }
            }
            Monster target = targets.get(MathUtils.random(0,targets.size-1));
            bs.attack(target,att);
        }
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
            default:
                setUpTestWorld();
                break;
        }
    }
}
