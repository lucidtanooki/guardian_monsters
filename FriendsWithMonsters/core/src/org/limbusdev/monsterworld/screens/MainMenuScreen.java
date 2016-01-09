package org.limbusdev.monsterworld.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.limbusdev.monsterworld.MonsterWorld;
import org.limbusdev.monsterworld.utils.GameState;
import org.limbusdev.monsterworld.utils.GlobalSettings;
import org.limbusdev.monsterworld.managers.SaveGameManager;

/**
 * Created by georg on 21.11.15.
 */
public class MainMenuScreen implements Screen {

    /* ............................................................................ ATTRIBUTES .. */
    public final MonsterWorld game;

    public OrthographicCamera camera;
    public Viewport viewport;

    private Texture bGImg;
    private Texture bGImg2;
    private float elapsedTime=0;

    // Scene2D.ui
    private Skin skin;
    private Stage stage;
    
    /* ........................................................................... CONSTRUCTOR .. */
    public MainMenuScreen(final MonsterWorld game) {
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new FitViewport(GlobalSettings.RESOLUTION_X, GlobalSettings.RESOLUTION_Y, camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        bGImg = game.media.getMainMenuBGImg();
        bGImg2 = game.media.getMainMenuBGImg2();

        setUpUI();
    }
    
    /* ............................................................................... METHODS .. */
    @Override
    public void show() {
        // TODO
    }

    @Override
    public void render(float delta) {
        elapsedTime += delta;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(camera.combined);

        // Start collecting textures for OpenGL
        game.batch.begin();

        game.batch.draw(bGImg2,0,0);
        // Logo
        game.batch.draw(bGImg,
                GlobalSettings.RESOLUTION_X/2 - bGImg.getWidth()/2,
                GlobalSettings.RESOLUTION_Y/2 - bGImg.getHeight()/2
                        + MathUtils.sin(elapsedTime)*7);

        game.batch.end();

        // UI
        stage.draw();

        // Update camera
        camera.update();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        stage.getViewport().update(width, height);
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

    public void setUpGame() {
        if(SaveGameManager.doesSaveGameExist()) {
            GameState state = SaveGameManager.loadSaveGame();
            game.setScreen(new OutdoorGameWorldScreen(game, state.map, 1, true));
        } else
            game.setScreen(new OutdoorGameWorldScreen(game, 9, 1, false));
    }


    public void setUpUI() {
        // Scene2D
        FitViewport fit = new FitViewport(
                GlobalSettings.RESOLUTION_X, GlobalSettings.RESOLUTION_Y);
        this.stage = new Stage(fit);
        Gdx.input.setInputProcessor(stage);
        this.skin = game.media.skin;

        // Buttons .................................................................................
        String startButton = "Start Game";
        if(SaveGameManager.doesSaveGameExist()) startButton = "Load Saved Game";
        final TextButton button = new TextButton(startButton, skin, "default");
        button.setWidth(160f);
        button.setHeight(32f);
        button.setPosition(GlobalSettings.RESOLUTION_X / 2 - 80f, 64f);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setUpGame();
            }
        });

        String creditsButton = "Credits";


        // Buttons ............................................................................. END

        stage.addActor(button);
    }
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
