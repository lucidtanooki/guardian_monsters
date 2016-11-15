package de.limbusdev.guardianmonsters.fwmengine.world.ui;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.EntityComponentSystem;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.GameArea;
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.utils.GS;
import de.limbusdev.guardianmonsters.utils.UnitConverter;


/**
 * Created by georg on 21.11.15.
 */
public class OutdoorGameWorldScreen implements Screen {
    /* ............................................................................ ATTRIBUTES .. */

    // Renderers and Cameras
    public  OrthographicCamera  camera;
    private Viewport            viewport;
    private SpriteBatch         batch;
    private ShapeRenderer       shpRend;
    private BitmapFont          font;

    private GameArea gameArea;
    private EntityComponentSystem ECS;
    private InputMultiplexer inputMultiplexer;


    /* ........................................................................... CONSTRUCTOR .. */
    public OutdoorGameWorldScreen(int mapID, int startPosID, boolean
            fromSave) {
        setUpRendering();
        this.gameArea = new GameArea(mapID, startPosID);
        SaveGameManager saveGameManager = new SaveGameManager(this.gameArea);
        this.ECS = new EntityComponentSystem(viewport, gameArea, fromSave, this, saveGameManager);

        this.inputMultiplexer = new InputMultiplexer();
        setUpInputProcessor();
    }

    /* ........................................................................ LIBGDX METHODS .. */

    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {
        this.batch = new SpriteBatch();
        setUpInputProcessor();
        gameArea.playMusic();
        ECS.hud.show();
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // process Updates
        updateCamera();

        // ............................................................................... RENDERING
        // Tiled Map
        gameArea.render(camera);
        ECS.render(batch, shpRend);
        if(GS.DEBUGGING_ON)gameArea.renderDebugging(shpRend);

        ECS.draw();

        // ............................................................................... RENDERING

        ECS.update(delta);
    }

    /**
     * @param width
     * @param height
     * @see ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
        ECS.hud.stage.getViewport().update(width, height, true);
    }

    /**
     * @see ApplicationListener#pause()
     */
    @Override
    public void pause() {
        // TODO
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
        gameArea.stopMusic();
        ECS.hud.hide();
    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {
        this.batch.dispose();
        this.font.dispose();
        this.gameArea.dispose();
    }

    /* ............................................................................... METHODS .. */

    private void setUpRendering() {
        // Rendering ...............................................................................
        camera   = new OrthographicCamera();    // set up the camera and viewport
        viewport = new FitViewport(
                UnitConverter.pixelsToMeters(GS.RES_X)/ GS.zoom,
                UnitConverter.pixelsToMeters(GS.RES_Y)/ GS.zoom,
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
        this.inputMultiplexer.addProcessor(ECS.hud.getInputProcessor());
        this.inputMultiplexer.addProcessor(ECS.getInputProcessor());
        Gdx.input.setInputProcessor(inputMultiplexer);
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
