package de.limbusdev.guardianmonsters;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import de.limbusdev.guardianmonsters.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.managers.MediaManager;
import de.limbusdev.guardianmonsters.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.managers.ScreenManager;
import de.limbusdev.guardianmonsters.model.BattleFactory;
import de.limbusdev.guardianmonsters.screens.BattleScreen;
import de.limbusdev.guardianmonsters.screens.InventoryScreen;
import de.limbusdev.guardianmonsters.screens.MainMenuScreen;
import de.limbusdev.guardianmonsters.screens.OutdoorGameWorldScreen;
import de.limbusdev.guardianmonsters.utils.GS;
import de.limbusdev.guardianmonsters.utils.GameState;


public class GuardianMonsters extends Game{
	/* ............................................................................ ATTRIBUTES .. */
	public SpriteBatch batch;
    public ShapeRenderer shp;
    public BitmapFont font;
	
	@Override
	public void create () {
        // Initialize ScreenManager
        ScreenManager.get().init(this);

        batch = new SpriteBatch();
        shp   = new ShapeRenderer();
        font  = new BitmapFont();

        switch(GS.DEBUG_MODE) {
            case WORLD:
                setUpTestWorld();
                break;
            case BATTLE:
                setUpTestBattle();
                break;
            case INVENTORY:
                setUpTestInventory();
                break;
            default:
                // Release
                ScreenManager.get().pushScreen(new MainMenuScreen());
                break;
        }
	}

	@Override
	public void render () {
		super.render();
	}

    @Override
    public void dispose() {
        batch.dispose();
        shp.dispose();
        font.dispose();
        MediaManager.get().dispose();
    }

    // ............................................................................ GAME MODE SETUPS
    private void setUpTestInventory() {
        TeamComponent herTeam = new TeamComponent();
        herTeam.monsters.add(BattleFactory.getInstance().createMonster(1));
        herTeam.monsters.add(BattleFactory.getInstance().createMonster(2));
        herTeam.monsters.add(BattleFactory.getInstance().createMonster(3));
        InventoryScreen ivs = new InventoryScreen(herTeam);
        this.setScreen(ivs);
    }

    private void setUpTestBattle() {
        TeamComponent herTeam = new TeamComponent();
        herTeam.monsters.add(BattleFactory.getInstance().createMonster(1));
        herTeam.monsters.add(BattleFactory.getInstance().createMonster(17));
        herTeam.monsters.add(BattleFactory.getInstance().createMonster(3));
        TeamComponent oppTeam = new TeamComponent();
        oppTeam.monsters.add(BattleFactory.getInstance().createMonster(7));
        oppTeam.monsters.add(BattleFactory.getInstance().createMonster(4));
        oppTeam.monsters.add(BattleFactory.getInstance().createMonster(11));
        BattleScreen battleScreen = new BattleScreen();
        battleScreen.init(herTeam, oppTeam);
        this.setScreen(battleScreen);
    }

    private void setUpTestWorld() {
        if(SaveGameManager.doesGameSaveExist()) {
            GameState state = SaveGameManager.loadSaveGame();
            this.setScreen(new OutdoorGameWorldScreen(state.map, 1, true));
        } else
            this.setScreen(new OutdoorGameWorldScreen(9, 1, false));
    }
}
