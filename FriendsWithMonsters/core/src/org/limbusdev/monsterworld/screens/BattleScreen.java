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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.limbusdev.monsterworld.MonsterWorld;
import org.limbusdev.monsterworld.ecs.components.TeamComponent;
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
    private BattleHUD battleHUD;
    private Array<TextureRegion> teamSprites, oppTeamSprites;
    private Texture background;
    private MediaManager media;
    private float elapsedTime=0;
    private boolean initialized=false;
    /* ........................................................................... CONSTRUCTOR .. */

    public BattleScreen(MediaManager media, OutdoorGameWorldScreen gameScreen, MonsterWorld game) {
        this.battleHUD = new BattleHUD(game, gameScreen);
        setUpRendering();
        setUpInputProcessor();
        this.media = media;
        this.teamSprites = new Array<TextureRegion>();
        this.oppTeamSprites = new Array<TextureRegion>();
        this.background = media.getBackgroundTexture(0);
    }
    /* ............................................................................... METHODS .. */

    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {
        if(!initialized) System.err.println("BattleScreen must get initialized before drawn.");
        this.batch = new SpriteBatch();
        setUpInputProcessor();
    }

    /**
     * BattleScreen must get initialized before being shown
     * @param team
     * @param opponentTeam
     */
    public void init (TeamComponent team, TeamComponent opponentTeam) {
        this.initialized = true;

        // Hero Team
        TextureRegion monsterSprite = media.getMonsterSprite(team.monsters.get(0).ID);
        monsterSprite.flip(true, false);
        this.teamSprites.add(monsterSprite);
        if(team.monsters.size >= 2) {
            monsterSprite = media.getMonsterSprite(team.monsters.get(1).ID);
            monsterSprite.flip(true, false);
            this.teamSprites.add(monsterSprite);
        }
        if(team.monsters.size == 3) {
            monsterSprite = media.getMonsterSprite(team.monsters.get(2).ID);
            monsterSprite.flip(true, false);
            this.teamSprites.add(monsterSprite);
        }

        // Opponent Team
        monsterSprite = media.getMonsterSprite(opponentTeam.monsters.get(0).ID);
        this.oppTeamSprites.add(monsterSprite);
        System.out.println(oppTeamSprites.size);
        if(opponentTeam.monsters.size >=2) {
            monsterSprite = media.getMonsterSprite(opponentTeam.monsters.get(1).ID);
            this.oppTeamSprites.add(monsterSprite);
        }
        System.out.println(oppTeamSprites.size);
        if(opponentTeam.monsters.size == 3) {
            monsterSprite = media.getMonsterSprite(opponentTeam.monsters.get(2).ID);
            this.oppTeamSprites.add(monsterSprite);
        }
        System.out.println(oppTeamSprites.size);
        this.battleHUD.init(team, opponentTeam);
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

        // Hero Team
        if(teamSprites.size == 3)
            batch.draw(
                teamSprites.get(2), 120,
                212 + 2*MathUtils.sin(elapsedTime),
                    128,128);
        batch.draw(
                teamSprites.get(0), 64,
                176 + 2 * MathUtils.cos(elapsedTime),
                128,128);
        if(teamSprites.size >= 2)
            batch.draw(
                    teamSprites.get(1), 8,
                    140 + 2 * MathUtils.sin(elapsedTime),
                    128,128);

        // Opponent Team
        if(oppTeamSprites.size == 3)
            batch.draw(
                    oppTeamSprites.get(2), 800-120-256,
                    212 + 2*MathUtils.cos(elapsedTime),
                    128,128);
        batch.draw(
                oppTeamSprites.get(0), 800-64-256,
                176 + 2*MathUtils.sin(elapsedTime),
                128,128);
        if(oppTeamSprites.size >= 2)
            batch.draw(
                    oppTeamSprites.get(1), 800-8-256,
                    140 + 2*MathUtils.cos(elapsedTime),
                    128,128);

        batch.end();

        // process Updates
        updateCamera();

        battleHUD.stage.getViewport().apply();
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
        battleHUD.stage.getViewport().update(width, height, true);
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
        this.oppTeamSprites.clear();
        this.teamSprites.clear();
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
        Gdx.input.setInputProcessor(battleHUD.stage);
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
