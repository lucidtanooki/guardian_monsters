package de.limbusdev.guardianmonsters;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.data.AudioAssets;
import de.limbusdev.guardianmonsters.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.fwmengine.managers.AudioManager;
import de.limbusdev.guardianmonsters.fwmengine.managers.ConcreteScreenManager;
import de.limbusdev.guardianmonsters.fwmengine.managers.LocalizationManager;
import de.limbusdev.guardianmonsters.fwmengine.managers.MediaManager;
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.fwmengine.managers.SettingsService;
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
        // Service Locator: Dependency Injection
        System.out.println("GuardianMonsters: injecting dependencies ...");
        Services.provide(new MediaManager());
        Services.provide(new AudioManager(AudioAssets.get().getAllSfxPaths(),AudioAssets.get().getAllMusicPaths()));
        Services.provide(new ConcreteScreenManager(this));
        Services.provide(new LocalizationManager());
        Services.provide(new SettingsService());


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
                Services.getScreenManager().pushScreen(new MainMenuScreen());
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
        Services.getMedia().dispose();
    }

    // ............................................................................ GAME MODE SETUPS
    private void setUpTestInventory() {
        TeamComponent herTeam = new TeamComponent();
        herTeam.monsters.add(BattleFactory.getInstance().createMonster(1));
        herTeam.monsters.add(BattleFactory.getInstance().createMonster(2));
        herTeam.monsters.add(BattleFactory.getInstance().createMonster(3));
        InventoryScreen ivs = new InventoryScreen(herTeam);
        setScreen(ivs);
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
        setScreen(battleScreen);
    }

    private void setUpTestWorld() {
        if(SaveGameManager.doesGameSaveExist()) {
            GameState state = SaveGameManager.loadSaveGame();
            setScreen(new OutdoorGameWorldScreen(state.map, 1, true));
        } else
            setScreen(new OutdoorGameWorldScreen(9, 1, false));
    }
}
