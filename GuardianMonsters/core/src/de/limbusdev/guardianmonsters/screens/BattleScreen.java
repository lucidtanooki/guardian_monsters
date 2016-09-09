package de.limbusdev.guardianmonsters.screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.limbusdev.guardianmonsters.GuardianMonsters;
import de.limbusdev.guardianmonsters.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.enums.MusicType;
import de.limbusdev.guardianmonsters.managers.MediaManager;
import de.limbusdev.guardianmonsters.utils.GS;

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
    private de.limbusdev.guardianmonsters.ui.BattleHUD battleHUD;
    private Texture background;
    private MediaManager media;
    private boolean initialized=false;
    private Music bgMusic;
    /* ........................................................................... CONSTRUCTOR .. */

    public BattleScreen(MediaManager media, OutdoorGameWorldScreen gameScreen, GuardianMonsters game) {
        this.battleHUD = new de.limbusdev.guardianmonsters.ui.BattleHUD(game);
        setUpRendering();
        setUpInputProcessor();
        this.media = media;
        this.background = media.getBackgroundTexture(0);
        this.bgMusic = media.getBGMusic(MusicType.BATTLE, 0);
        this.bgMusic.setLooping(true);
    }
    /* ............................................................................... METHODS .. */

    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {
        if(!initialized) System.err.println("BattleScreen must get initialized before drawn.");
        this.batch = new SpriteBatch();
        bgMusic.play();
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
        batch.draw(background, 0, 0, GS.RES_X, GS.RES_Y);

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
        this.bgMusic.stop();
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
                GS.RES_X,
                GS.RES_Y,
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
