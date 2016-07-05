package org.limbusdev.monsterworld;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import org.limbusdev.monsterworld.ecs.components.Components;
import org.limbusdev.monsterworld.ecs.components.TeamComponent;
import org.limbusdev.monsterworld.managers.MediaManager;
import org.limbusdev.monsterworld.managers.SaveGameManager;
import org.limbusdev.monsterworld.model.BattleFactory;
import org.limbusdev.monsterworld.screens.BattleScreen;
import org.limbusdev.monsterworld.screens.MainMenuScreen;
import org.limbusdev.monsterworld.screens.OutdoorGameWorldScreen;
import org.limbusdev.monsterworld.utils.GameState;
import org.limbusdev.monsterworld.utils.GS;

public class FriendsWithMonsters extends Game {
	/* ............................................................................ ATTRIBUTES .. */
	public SpriteBatch batch;
    public ShapeRenderer shp;
    public BitmapFont font;
	public MediaManager media;
	
	@Override
	public void create () {
        batch = new SpriteBatch();
        shp   = new ShapeRenderer();
        font  = new BitmapFont();
        media = new MediaManager();

        switch(GS.DEBUG_MODE) {
            case WORLD:
                setUpTestWorld();
                break;
            case BATTLE:
                setUpTestBattle();
                break;
            default:
                // Release
                this.setScreen(new MainMenuScreen(this));
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
}
