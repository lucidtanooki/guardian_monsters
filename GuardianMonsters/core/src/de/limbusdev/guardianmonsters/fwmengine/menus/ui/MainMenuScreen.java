package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.limbusdev.guardianmonsters.data.BundleAssets;
import de.limbusdev.guardianmonsters.data.TextureAssets;
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.widgets.CreditsScreenWidget;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.widgets.StartScreenWidget;
import de.limbusdev.guardianmonsters.fwmengine.world.ui.WorldScreen;
import de.limbusdev.guardianmonsters.utils.Constant;
import de.limbusdev.guardianmonsters.model.gamestate.GameState;


/**
 * @author Georg Eckert 2017
 */
public class MainMenuScreen implements Screen {

    /* ............................................................................ ATTRIBUTES .. */

    // Scene2D.ui
    private Stage stage;
    private Image black;

    private Group startMenu, introScreen;
    private StartScreenWidget logoScreen;
    private CreditsScreenWidget credits;
    
    /* ........................................................................... CONSTRUCTOR .. */
    public MainMenuScreen() {
        Skin skin = Services.getUI().getDefaultSkin();

        Image bg = new Image(skin.getDrawable("black"));
        bg.setWidth(Constant.WIDTH);
        bg.setHeight(Constant.HEIGHT);
        bg.setPosition(0,0);
        black = bg;

        setUpIntro(skin);
        setUpUI(skin);
        setUpStartMenu(skin);

        stage.addActor(black);
        stage.addActor(introScreen);

    }
    
    /* ............................................................................... METHODS .. */
    @Override
    public void show() {
        introScreen.addAction(Actions.sequence(
                Actions.fadeIn(1),Actions.delay(1),Actions.fadeOut(1),Actions.visible(false)
        ));
        black.addAction(Actions.sequence(
                Actions.delay(3),Actions.fadeOut(1),Actions.visible(false)
        ));
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // UI
        stage.act(delta);
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
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


    public void setUpUI(Skin skin) {

        // Scene2D
        FitViewport fit = new FitViewport(Constant.WIDTH, Constant.HEIGHT);
        this.stage = new Stage(fit);
        Gdx.input.setInputProcessor(stage);

        this.logoScreen = new StartScreenWidget(skin);

        logoScreen.startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                logoScreen.addAction(Actions.sequence(
                        Actions.fadeOut(1), Actions.visible(false)
                ));
                startMenu.addAction(Actions.sequence(
                        Actions.alpha(0),Actions.visible(true), Actions.delay(1), Actions.fadeIn(1)
                ));
            }
        });
        stage.addActor(logoScreen);
    }

    public void setUpStartMenu(Skin skin) {
        this.startMenu = new Group();

        // .................................................................................. IMAGES
        Image bg = new Image(skin.getDrawable("black"));
        bg.setWidth(Constant.WIDTH);bg.setHeight(Constant.HEIGHT);
        bg.setPosition(0, 0);
        bg.addAction(Actions.alpha(.75f));
        startMenu.addActor(bg);

        Image mon = new Image(Services.getMedia().getMonsterSprite(100));
        mon.setPosition(Constant.WIDTH - 8, 8, Align.bottomRight);
        startMenu.addActor(mon);

        // ................................................................................. BUTTONS
        I18NBundle i18n = Services.getL18N().l18n(BundleAssets.GENERAL);

        // ............................................................................ START BUTTON
        String label = i18n.get("main_menu_start_new");
        TextButton buttonStart = new TextButton(label, skin, "button-96x32");

        buttonStart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addAction(Actions.sequence(
                    Actions.fadeOut(1), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            SaveGameManager.newSaveGame();
                            Services.getScreenManager().pushScreen(new WorldScreen(25, 1, false));
                        }
                    })
                ));
            }
        });

        label = i18n.get("main_menu_load_saved");
        TextButton buttonContinue = new TextButton(label, skin, "button-96x32");

        buttonContinue.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addAction(Actions.sequence(
                    Actions.fadeOut(1), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            GameState state = SaveGameManager.loadSaveGame();
                            Services.getScreenManager().pushScreen(new WorldScreen(state.map, 1, true));
                        }
                    })
                ));
            }
        });


        // .......................................................................... CREDITS BUTTON
        credits = new CreditsScreenWidget(skin);

        TextButton buttonCredtis = new TextButton(i18n.get("main_menu_credits"), skin, "button-96x32");

        buttonCredtis.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startMenu.addAction(Actions.sequence(
                        Actions.fadeOut(1), Actions.visible(false),
                        Actions.delay(20), Actions.visible(true),
                        Actions.fadeIn(1)
                ));
                stage.addActor(credits);
                credits.start(20);
            }
        });

        // Layout
        Table tableButtons = new Table();
        tableButtons.top().left();
        tableButtons.setDebug(true);
        tableButtons.setSize(96,Constant.HEIGHT);
        tableButtons.setPosition(4, Constant.HEIGHT-4, Align.topLeft);
        if(SaveGameManager.doesGameSaveExist()) {
            tableButtons.add(buttonContinue).size(96,32).spaceBottom(2);
            tableButtons.row();
        }
        tableButtons.add(buttonStart).size(96,32).spaceBottom(2);
        tableButtons.row();
        tableButtons.add(buttonCredtis).size(96,32).spaceBottom(2);

        startMenu.addActor(tableButtons);
        startMenu.setVisible(false);
        stage.addActor(startMenu);
    }


    public void setUpIntro(Skin skin) {
        TextureAtlas logos = Services.getMedia().getTextureAtlas(TextureAssets.logosSpriteSheetFile);
        this.introScreen = new Group();
        Image bg = new Image(skin.getDrawable("black"));
        bg.setWidth(Constant.WIDTH);
        bg.setHeight(Constant.HEIGHT);
        bg.setPosition(0,0);
        Image logo = new Image(logos.findRegion("limbusdevIntro"));
        logo.setPosition(Constant.WIDTH / 2, Constant.HEIGHT/ 2,Align.center);
        introScreen.addActor(bg);
        introScreen.addActor(logo);
    }
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
