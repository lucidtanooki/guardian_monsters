package de.limbusdev.guardianmonsters.utils;

import com.badlogic.gdx.Game;

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.BattleFactory;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.BattleScreen;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.InventoryScreen;
import de.limbusdev.guardianmonsters.fwmengine.world.ui.OutdoorGameWorldScreen;

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
            default:
                setUpTestWorld();
                break;
        }
    }
}
