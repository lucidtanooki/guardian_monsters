package org.limbusdev.monsterworld.screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.limbusdev.monsterworld.MonsterWorld;
import org.limbusdev.monsterworld.ecs.EntityComponentSystem;
import org.limbusdev.monsterworld.ecs.systems.OutdoorGameArea;
import org.limbusdev.monsterworld.utils.GlobalSettings;
import org.limbusdev.monsterworld.utils.UnitConverter;


import box2dLight.RayHandler;

/**
 * Created by georg on 21.11.15.
 */
public class OutdoorGameWorldScreen implements Screen {
    /* ............................................................................ ATTRIBUTES .. */
    private final MonsterWorld  game;

    // Renderers and Cameras
    public  OrthographicCamera  camera;
    private Viewport            viewport;
    private SpriteBatch         batch;
    private ShapeRenderer       shpRend;
    private BitmapFont          font;

    private OutdoorGameArea gameArea;
    private EntityComponentSystem ECS;


    /* ........................................................................... CONSTRUCTOR .. */
    public OutdoorGameWorldScreen(final MonsterWorld game, int mapID, int startPosID) {
        this.game = game;
        setUpRendering();
        this.gameArea = new OutdoorGameArea(mapID, game.media, startPosID);
        this.ECS = new EntityComponentSystem(game, viewport, gameArea);
    }

    /* ........................................................................ LIBGDX METHODS .. */

    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {
        this.batch = new SpriteBatch();
        Gdx.input.setInputProcessor(ECS.getInputProcessor());
        gameArea.playMusic();
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
        if(GlobalSettings.DEBUGGING_ON)gameArea.renderDebugging(shpRend);

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
        gameArea.stopMusic();
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
                UnitConverter.pixelsToMeters(GlobalSettings.RESOLUTION_X)/GlobalSettings.zoom,
                UnitConverter.pixelsToMeters(GlobalSettings.RESOLUTION_Y)/GlobalSettings.zoom,
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
        camera.position.set(ECS.getHeroPosition().x, ECS.getHeroPosition().y, 0);
        batch.  setProjectionMatrix(camera.combined);
        shpRend.setProjectionMatrix(camera.combined);
        camera.update();
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
