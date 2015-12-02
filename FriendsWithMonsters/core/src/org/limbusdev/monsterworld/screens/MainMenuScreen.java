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
import org.limbusdev.monsterworld.utils.GlobalSettings;

/**
 * Created by georg on 21.11.15.
 */
public class MainMenuScreen implements Screen {

    /* ............................................................................ ATTRIBUTES .. */
    public final MonsterWorld game;

    public OrthographicCamera camera;
    public Viewport viewport;

    private Texture bGImg;
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
        game.setScreen(new OutdoorGameWorldScreen(game, 3, 1));
    }

    public void setUpUI() {
        // Scene2D
        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        this.skin = new Skin(Gdx.files.internal("scene2d/uiskin.json"));

        // Buttons .................................................................................
        final TextButton button = new TextButton("Start Game", skin, "default");
        button.setWidth(128f);
        button.setHeight(32f);
        button.setPosition(Gdx.graphics.getWidth() / 2 - 64f, 64f);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setUpGame();
            }
        });

        // Buttons ............................................................................. END

        stage.addActor(button);
    }
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
