package org.limbusdev.monsterworld;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import org.limbusdev.monsterworld.managers.MediaManager;
import org.limbusdev.monsterworld.screens.MainMenuScreen;

public class MonsterWorld extends Game {
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

        // switch to main menu screen
        this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        media.dispose();
    }
}
