package de.limbusdev.guardianmonsters;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.managers.MediaManager;
import de.limbusdev.guardianmonsters.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.model.BattleFactory;
import de.limbusdev.guardianmonsters.screens.BattleScreen;
import de.limbusdev.guardianmonsters.screens.InventoryScreen;
import de.limbusdev.guardianmonsters.screens.MainMenuScreen;
import de.limbusdev.guardianmonsters.screens.OutdoorGameWorldScreen;
import de.limbusdev.guardianmonsters.utils.GS;
import de.limbusdev.guardianmonsters.utils.GameState;


public class GuardianMonsters extends Game {
	/* ............................................................................ ATTRIBUTES .. */
	public SpriteBatch batch;
    public ShapeRenderer shp;
    public BitmapFont font;
	public MediaManager media;
    private Array<Screen> stateMashine;
	
	@Override
	public void create () {
        batch = new SpriteBatch();
        shp   = new ShapeRenderer();
        font  = new BitmapFont();
        media = new MediaManager();
        stateMashine = new Array<Screen>();

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
                pushScreen(new MainMenuScreen(this));
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
        media.dispose();
    }

    // ............................................................................ GAME MODE SETUPS
    private void setUpTestInventory() {
        TeamComponent herTeam = new TeamComponent();
        herTeam.monsters.add(BattleFactory.getInstance().createMonster(1));
        herTeam.monsters.add(BattleFactory.getInstance().createMonster(2));
        herTeam.monsters.add(BattleFactory.getInstance().createMonster(3));
        InventoryScreen ivs = new InventoryScreen(this,herTeam);
        this.setScreen(ivs);
    }

    private void setUpTestBattle() {
        TeamComponent herTeam = new TeamComponent();
        herTeam.monsters.add(BattleFactory.getInstance().createMonster(1));
        herTeam.monsters.add(BattleFactory.getInstance().createMonster(2));
        herTeam.monsters.add(BattleFactory.getInstance().createMonster(3));
        TeamComponent oppTeam = new TeamComponent();
        oppTeam.monsters.add(BattleFactory.getInstance().createMonster(7));
        oppTeam.monsters.add(BattleFactory.getInstance().createMonster(4));
        oppTeam.monsters.add(BattleFactory.getInstance().createMonster(11));
        BattleScreen battleScreen = new BattleScreen(
                media, new OutdoorGameWorldScreen(this, 9, 1, false), this);
        battleScreen.init(herTeam, oppTeam);
        this.setScreen(battleScreen);
    }

    private void setUpTestWorld() {
        if(SaveGameManager.doesGameSaveExist()) {
            GameState state = SaveGameManager.loadSaveGame();
            this.setScreen(new OutdoorGameWorldScreen(this, state.map, 1, true));
        } else
            this.setScreen(new OutdoorGameWorldScreen(this, 9, 1, false));
    }

    public void pushScreen(Screen screen) {
        stateMashine.add(screen);
        this.setScreen(screen);
    }

    public void popScreen() {
        Screen oldScreen = stateMashine.pop();
        setScreen(stateMashine.peek());
        oldScreen.dispose();
    }
}
