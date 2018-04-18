package de.limbusdev.guardianmonsters.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.limbusdev.guardianmonsters.services.Services;

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

    private TextureRegion background;

    private AHUD hud;

    public AScreen() {
        camera   = new OrthographicCamera();
        viewport = new FitViewport(Constant.WIDTH, Constant.HEIGHT, camera);
        batch   = new SpriteBatch();
        shpRend = new ShapeRenderer();
    }

    public AScreen(AHUD hud) {
        this();
        this.hud = hud;
    }

    public void setBackground(int index) {
        this.background = Services.getMedia().getBackgroundTexture(index);
    }

    @Override
    public void show() {
        this.hud.show();
        this.batch = new SpriteBatch();
        this.shpRend = new ShapeRenderer();
        if(hud != null) Gdx.input.setInputProcessor(hud.getStage());
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(background != null) {
            batch.begin();
            batch.draw(background, 0, 0, Constant.WIDTH, Constant.HEIGHT);
            batch.end();
        }

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

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        if(hud != null) hud.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        if(hud != null) hud.hide();
    }

    @Override
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
