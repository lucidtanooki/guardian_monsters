package de.limbusdev.guardianmonsters.fwmengine.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static de.limbusdev.guardianmonsters.Constant.HEIGHT;
import static de.limbusdev.guardianmonsters.Constant.WIDTH;

/**
 * AScreen
 *
 * @author Georg Eckert 2017
 */

public abstract class AScreen implements Screen {

    // Renderers and Cameras
    private OrthographicCamera  camera;
    private Viewport            viewport;
    private SpriteBatch         batch;
    private ShapeRenderer       shpRend;

    private AHUD hud;

    public AScreen() {
        camera   = new OrthographicCamera();
        viewport = new FitViewport(WIDTH, HEIGHT, camera);
        batch   = new SpriteBatch();
        shpRend = new ShapeRenderer();
    }

    public AScreen(AHUD hud) {
        this();
        this.hud = hud;
    }

    public void show() {
        this.batch = new SpriteBatch();
        this.shpRend = new ShapeRenderer();
        if(hud != null) Gdx.input.setInputProcessor(hud.getStage());
    }

    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(.3f, .3f, .3f, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(hud != null) hud.update(delta);

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        shpRend.setProjectionMatrix(camera.combined);
        camera.update();
        if(hud != null) {
            hud.getStage().getViewport().apply();
            hud.draw();
        }
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        if(hud != null) hud.resize(width, height);
    }

    public void pause() {

    }

    public void resume() {

    }

    public void hide() {
        if(hud != null) hud.hide();
    }

    public void dispose() {
        this.batch.dispose();
        this.shpRend.dispose();
    }

    public AHUD getHud() {
        return hud;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public ShapeRenderer getShpRend() {
        return shpRend;
    }
}
