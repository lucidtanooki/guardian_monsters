package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.limbusdev.guardianmonsters.data.BundleAssets;
import de.limbusdev.guardianmonsters.data.TextureAssets;
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.widgets.AnimatedImage;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.widgets.CreditsScreenWidget;
import de.limbusdev.guardianmonsters.fwmengine.world.ui.WorldScreen;
import de.limbusdev.guardianmonsters.utils.GS;
import de.limbusdev.guardianmonsters.utils.GameState;


/**
 * @author Georg Eckert 2017
 */
public class MainMenuScreen implements Screen {

    /* ............................................................................ ATTRIBUTES .. */

    // Scene2D.ui
    private Stage stage;
    private Image black;

    private ArrayMap<String,TextButton> buttons;
    private Group startMenu, logoScreen, introScreen;
    private CreditsScreenWidget credits;
    
    /* ........................................................................... CONSTRUCTOR .. */
    public MainMenuScreen() {
        Skin skin = Services.getUI().getDefaultSkin();

        Image bg = new Image(skin.getDrawable("black"));
        bg.setWidth(GS.WIDTH);
        bg.setHeight(GS.HEIGHT);
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

    public void setUpGame() {
        if(SaveGameManager.doesGameSaveExist()) {
            GameState state = SaveGameManager.loadSaveGame();
            Services.getScreenManager().pushScreen(new WorldScreen(state.map, 1, true));
        } else
            Services.getScreenManager().pushScreen(new WorldScreen(25, 1, false));
    }


    public void setUpUI(Skin skin) {

        // Scene2D
        FitViewport fit = new FitViewport(GS.WIDTH, GS.HEIGHT);
        this.stage = new Stage(fit);
        Gdx.input.setInputProcessor(stage);

        this.logoScreen = new Group();

        Animation bgAnim = new Animation(.1f, Services.getMedia()
            .getTextureAtlas(TextureAssets.bigAnimations).findRegions("mainMenuAnimation"));
        bgAnim.setPlayMode(Animation.PlayMode.LOOP);
        AnimatedImage bgAnimation = new AnimatedImage(bgAnim);
        bgAnimation.setColor(1,1,1,.3f);
        bgAnimation.setPosition(35,-30,Align.bottomLeft);
        stage.addActor(bgAnimation);

        Image logo = new Image(Services.getMedia().getTexture(TextureAssets.mainMenuBGImgFile));
        logo.setPosition(GS.WIDTH / 2, GS.HEIGHT / 2, Align.center);
        logoScreen.addActor(logo);

        Label creatorLabel = new Label("by Georg Eckert", Services.getUI().getDefaultSkin(),"trans-white");
        creatorLabel.setPosition(GS.WIDTH/2,76,Align.bottomLeft);
        creatorLabel.setAlignment(Align.center,Align.center);
        logoScreen.addActor(creatorLabel);

        I18NBundle i18n = Services.getL18N().l18n(BundleAssets.GENERAL);

        // Buttons ......................................................................... BUTTONS
        // Start Button
        TextButton button = new TextButton(i18n.get("main_menu_touch_start"), skin, "button-96x32");
        button.setSize(96,32);
        button.setPosition(GS.WIDTH/2 - 96/2, 16f, Align.bottomLeft);

        button.addListener(new ClickListener() {
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
        logoScreen.addActor(button);


        // Buttons ............................................................................. END
        stage.addActor(logoScreen);
    }

    public void setUpStartMenu(Skin skin) {
        this.startMenu = new Group();

        // .................................................................................. IMAGES
        Image bg = new Image(skin.getDrawable("black"));
        bg.setWidth(GS.WIDTH);bg.setHeight(GS.HEIGHT);
        bg.setPosition(0, 0);
        bg.addAction(Actions.alpha(.75f));
        startMenu.addActor(bg);

        Image mon = new Image(Services.getMedia().getMonsterSprite(100));
        mon.setPosition(GS.WIDTH - 8, 8, Align.bottomRight);
        startMenu.addActor(mon);

        // ................................................................................. BUTTONS
        this.buttons = new ArrayMap<>();

        I18NBundle i18n = Services.getL18N().l18n(BundleAssets.GENERAL);
        String startButton;
        if(SaveGameManager.doesGameSaveExist()) {
            startButton = i18n.get("main_menu_load_saved");
        } else {
            startButton = i18n.get("main_menu_start_new");
        }

        // ............................................................................ START BUTTON
        TextButton button = new TextButton(startButton, skin, "button-96x32");
        button.setSize(96,32);
        button.setPosition(16,GS.HEIGHT-16,Align.topLeft);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addAction(Actions.sequence(
                        Actions.fadeOut(1), Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                setUpGame();
                            }
                        })
                ));
            }
        });
        buttons.put("start", button);


        // .......................................................................... CREDITS BUTTON
        credits = new CreditsScreenWidget(skin);

        button = new TextButton(i18n.get("main_menu_credits"), skin, "button-96x32");
        button.setSize(96,32);
        button.setPosition(16,GS.HEIGHT-16-48,Align.topLeft);

        button.addListener(new ClickListener() {
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
        buttons.put("credits", button);


        for(String key : buttons.keys()) startMenu.addActor(buttons.get(key));
        startMenu.setVisible(false);
        stage.addActor(startMenu);
    }


    public void setUpIntro(Skin skin) {
        TextureAtlas logos = Services.getMedia().getTextureAtlas(TextureAssets.logosSpriteSheetFile);
        this.introScreen = new Group();
        Image bg = new Image(skin.getDrawable("black"));
        bg.setWidth(GS.WIDTH);
        bg.setHeight(GS.HEIGHT);
        bg.setPosition(0,0);
        Image logo = new Image(logos.findRegion("limbusdevIntro"));
        logo.setPosition(GS.WIDTH / 2, GS.HEIGHT/ 2,Align.center);
        introScreen.addActor(bg);
        introScreen.addActor(logo);
    }
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
