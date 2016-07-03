package org.limbusdev.monsterworld;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import org.limbusdev.monsterworld.managers.MediaManager;
import org.limbusdev.monsterworld.managers.SaveGameManager;
import org.limbusdev.monsterworld.screens.MainMenuScreen;
import org.limbusdev.monsterworld.screens.OutdoorGameWorldScreen;
import org.limbusdev.monsterworld.utils.GameState;
import org.limbusdev.monsterworld.utils.GlobPref;

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

        if(GlobPref.SKIP_START_MENU) {
            if(SaveGameManager.doesGameSaveExist()) {
                GameState state = SaveGameManager.loadSaveGame();
                this.setScreen(new OutdoorGameWorldScreen(this, state.map, 1, true));
            } else
                this.setScreen(new OutdoorGameWorldScreen(this, 9, 1, false));
        } // switch to main menu screen
        else this.setScreen(new MainMenuScreen(this));
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
}
