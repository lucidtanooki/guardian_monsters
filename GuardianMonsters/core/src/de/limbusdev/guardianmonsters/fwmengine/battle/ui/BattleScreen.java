package de.limbusdev.guardianmonsters.fwmengine.battle.ui;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.limbusdev.guardianmonsters.data.AudioAssets;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.items.Inventory;
import de.limbusdev.guardianmonsters.utils.Constant;

/**
 * @author Georg Eckert 2016
 */
public class BattleScreen implements Screen {
    /* ............................................................................ ATTRIBUTES .. */
    // Renderers and Cameras
    public OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shpRend;
    private BitmapFont font;
    private BattleHUD battleHUD;
    private TextureRegion background;
    private boolean initialized=false;
    /* ........................................................................... CONSTRUCTOR .. */

    public BattleScreen(Inventory inventory) {
        this.battleHUD = new BattleHUD(inventory);
        setUpRendering();
        setUpInputProcessor();
        this.background = Services.getMedia().getBackgroundTexture(0);
    }
    /* ............................................................................... METHODS .. */


    @Override
    public void show() {
        if(!initialized) System.err.println("BattleScreen must get initialized before drawn.");
        this.batch = new SpriteBatch();
        Services.getAudio().playLoopMusic(AudioAssets.get().getBattleMusic(0));
        setUpInputProcessor();
    }

    /**
     * BattleScreen must get initialized before being shown
     * @param team
     * @param opponentTeam
     */
    public void init (TeamComponent team, TeamComponent opponentTeam) {
        this.initialized = true;

        this.battleHUD.init(team, opponentTeam);
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {

        // Clear screen
        Gdx.gl.glClearColor(.3f, .3f, .3f, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.begin();
        batch.draw(background, 0, 0, Constant.RES_X, Constant.RES_Y);

        batch.end();

        // process Updates
        updateCamera();

        battleHUD.getStage().getViewport().apply();
        battleHUD.update(delta);
        battleHUD.draw();
    }

    /**
     * @param width
     * @param height
     * @see ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        battleHUD.resize(width, height);
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
        initialized = false;
        Services.getAudio().stopMusic(AudioAssets.get().getBattleMusic(0));
        this.battleHUD.hide();
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
                Constant.RES_X,
                Constant.RES_Y,
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
        Gdx.input.setInputProcessor(battleHUD.getStage());
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
