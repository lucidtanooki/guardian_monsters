package org.limbusdev.monsterworld.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.limbusdev.monsterworld.MonsterWorld;

/**
 * Created by georg on 21.11.15.
 */
public class MainMenuScreen implements Screen {

    /* ............................................................................ ATTRIBUTES .. */
    public final MonsterWorld game;

    public OrthographicCamera camera;
    public Viewport viewport;
    
    /* ........................................................................... CONSTRUCTOR .. */
    public MainMenuScreen(final MonsterWorld game) {
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new FitViewport(200, 120, camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
    }
    
    /* ............................................................................... METHODS .. */
    @Override
    public void show() {
        // TODO
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(camera.combined);

        // Start collecting textures for OpenGL
        game.batch.begin();
        game.font.draw(game.batch, "Tap to start", 10, 10);

        game.batch.end();

        // Update camera
        camera.update();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
        camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);
    }

    @Override
    public void pause() {
        // TODO
    }

    @Override
    public void resume() {
        // TODO
    }

    @Override
    public void hide() {
        // TODO
    }

    @Override
    public void dispose() {
        // TODO
    }
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
