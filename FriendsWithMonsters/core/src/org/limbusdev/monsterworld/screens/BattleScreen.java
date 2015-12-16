package org.limbusdev.monsterworld.screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.limbusdev.monsterworld.MonsterWorld;
import org.limbusdev.monsterworld.managers.MediaManager;
import org.limbusdev.monsterworld.utils.GlobalSettings;

/**
 * Created by georg on 03.12.15.
 */
public class BattleScreen implements Screen {
    /* ............................................................................ ATTRIBUTES .. */
    // Renderers and Cameras
    public OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shpRend;
    private BitmapFont font;
    private BattleHUD hud;
    private TextureAtlas.AtlasRegion opponent1, opponent2;
    private Texture background;
    private MediaManager media;
    private float elapsedTime=0;
    /* ........................................................................... CONSTRUCTOR .. */

    public BattleScreen(MediaManager media, OutdoorGameWorldScreen gameScreen, MonsterWorld game) {
        this.hud = new BattleHUD(game, gameScreen,1,2);
        setUpRendering();
        setUpInputProcessor();
        this.media = media;
        this.opponent1 = media.getMonsterSprite(1);
        this.opponent1.flip(true,false);
        this.opponent2 = media.getMonsterSprite(2);
        this.background = media.getBackgroundTexture(0);
    }
    /* ............................................................................... METHODS .. */

    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {
        int opp = MathUtils.random(1,GlobalSettings.MONSTER_SPRITES);
        this.opponent2 = media.getMonsterSprite(opp);
        this.batch = new SpriteBatch();
        this.hud.init();
        setUpInputProcessor();
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        elapsedTime += 4*delta;
        // Clear screen
        Gdx.gl.glClearColor(.3f, .3f, .3f, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.begin();
        batch.draw(background, 0, 0);
        batch.draw(
                opponent1, 200 - opponent1.getRegionWidth(),
                150 + 2*MathUtils.sin(elapsedTime),
                opponent1.getRegionWidth()*GlobalSettings.zoom,
                opponent1.getRegionHeight()*GlobalSettings.zoom);
        batch.draw(
                opponent2, 800 - 200 - opponent2.getRegionWidth(),
                150 + 2*MathUtils.cos(elapsedTime),
                opponent2.getRegionHeight()*GlobalSettings.zoom,
                opponent2.getRegionHeight()*GlobalSettings.zoom);
        batch.end();

        // process Updates
        updateCamera();

        hud.stage.getViewport().apply();
        hud.update(delta);
        hud.draw();
    }

    /**
     * @param width
     * @param height
     * @see ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        hud.stage.getViewport().update(width, height, true);
    }

    /**
     * @see ApplicationListener#pause()
     */
    @Override
    public void pause() {

    }

    /**
     * @see ApplicationListener#resume()
     */
    @Override
    public void resume() {

    }

    /**
     * Called when this screen is no longer the current screen for a {@link Game}.
     */
    @Override
    public void hide() {

    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {
        this.batch.dispose();
        this.font.dispose();
    }

    private void setUpRendering() {
        // Rendering ...............................................................................
        camera   = new OrthographicCamera();    // set up the camera and viewport
        viewport = new FitViewport(
                GlobalSettings.RESOLUTION_X,
                GlobalSettings.RESOLUTION_Y,
                camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0); // center camera

        batch   = new SpriteBatch();
        shpRend = new ShapeRenderer();
        font    = new BitmapFont();
        font.setColor(Color.WHITE);
    }

    private void updateCamera() {
        // project to camera
        batch.  setProjectionMatrix(camera.combined);
        shpRend.setProjectionMatrix(camera.combined);
        camera.update();
    }

    public void setUpInputProcessor() {
        Gdx.input.setInputProcessor(hud.stage);
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
