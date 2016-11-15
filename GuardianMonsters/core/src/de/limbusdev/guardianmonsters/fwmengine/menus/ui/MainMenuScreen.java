package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.limbusdev.guardianmonsters.data.TextureAssets;
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.fwmengine.world.ui.OutdoorGameWorldScreen;
import de.limbusdev.guardianmonsters.utils.GS;
import de.limbusdev.guardianmonsters.utils.GameState;


/**
 * Created by georg on 21.11.15.
 */
public class MainMenuScreen implements Screen {

    /* ............................................................................ ATTRIBUTES .. */

    // Scene2D.ui
    private Skin skin;
    private Stage stage;
    private Image black;

    private ArrayMap<String,TextButton> buttons;
    private Group startMenu, logoScreen, creditsScreen, introScreen;
    
    /* ........................................................................... CONSTRUCTOR .. */
    public MainMenuScreen() {
        TextureAtlas uiTA = Services.getMedia().getTextureAtlas(TextureAssets.UISpriteSheetFile);

        Image bg = new Image(uiTA.findRegion("black"));
        bg.setWidth(GS.RES_X);
        bg.setHeight(GS.RES_Y);
        bg.setPosition(0,0);
        black = bg;

        setUpIntro(uiTA);
        setUpUI(uiTA);
        setUpStartMenu(uiTA);
        setUpCredits(uiTA);

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
            Services.getScreenManager().pushScreen(new OutdoorGameWorldScreen(state.map, 1, true));
        } else
            Services.getScreenManager().pushScreen(new OutdoorGameWorldScreen(9, 1, false));
    }


    public void setUpUI(TextureAtlas uiTA) {

        // Scene2D
        FitViewport fit = new FitViewport(
                GS.RES_X, GS.RES_Y);
        this.stage = new Stage(fit);
        Gdx.input.setInputProcessor(stage);
        this.skin = Services.getUI().getDefaultSkin();

        this.logoScreen = new Group();

        Image bg = new Image(Services.getMedia().getTexture(TextureAssets.mainMenuBGImgFile2));
        bg.setWidth(1024);
        bg.setHeight(1024);
        bg.setPosition(900,300,Align.center);
        bg.setOrigin(Align.center);
        bg.addAction(Actions.alpha(0));
        bg.act(1);
        bg.addAction(Actions.parallel(Actions.alpha(1,30),Actions.forever(Actions.rotateBy(.3f))));
        stage.addActor(bg);

        Image logo = new Image(Services.getMedia().getTexture(TextureAssets.mainMenuBGImgFile));
        logo.setWidth(1080);
        logo.setHeight(270);
        logo.setPosition(GS.RES_X / 2, GS.RES_Y / 2, Align.center);
        logo.addAction(Actions.forever(Actions.sequence(
                        Actions.moveBy(0,-7, 3, Interpolation.sine),
                        Actions.moveBy(0,7, 3, Interpolation.sine)
                )));
        logoScreen.addActor(logo);


        // Buttons ......................................................................... BUTTONS
        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
        tbs.font = skin.getFont("default-font");
        tbs.unpressedOffsetY = +1;
        tbs.down = new TextureRegionDrawable(uiTA.findRegion("b192down"));
        tbs.up   = new TextureRegionDrawable(uiTA.findRegion("b192up"));

        // Start Button
        TextButton button = new TextButton(Services.getL18N().l18n().get("main_menu_touch_start"), tbs);
        button.setWidth(300);
        button.setHeight(75);
        button.setPosition(GS.RES_X / 2, 92f, Align.center);

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

    public void setUpStartMenu(TextureAtlas uiTA) {
        this.startMenu = new Group();

        // .................................................................................. IMAGES
        Image bg = new Image(uiTA.findRegion("black"));
        bg.setWidth(GS.RES_X);bg.setHeight(GS.RES_Y);
        bg.setPosition(0, 0);
        bg.addAction(Actions.alpha(.75f));
        startMenu.addActor(bg);

        Image mon = new Image(Services.getMedia().getMonsterSprite(100));
        mon.setWidth(256);mon.setHeight(256);
        mon.setPosition(GS.RES_X - 64, 64, Align.bottomRight);
        startMenu.addActor(mon);

        // ................................................................................. BUTTONS
        this.buttons = new ArrayMap<String, TextButton>();

        String startButton = Services.getL18N().l18n().get("main_menu_start_new");
        if(SaveGameManager.doesGameSaveExist()) startButton = Services.getL18N().l18n().get("main_menu_load_saved");

        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
        tbs.font = skin.getFont("default-font");
        tbs.unpressedOffsetY = +1;
        tbs.down = new TextureRegionDrawable(uiTA.findRegion("b192down"));
        tbs.up   = new TextureRegionDrawable(uiTA.findRegion("b192up"));

        // ............................................................................ START BUTTON
        TextButton button = new TextButton(startButton, tbs);
        button.setWidth(300);
        button.setHeight(75);
        button.setPosition(32, GS.RES_Y - 32, Align.topLeft);

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
        button = new TextButton(Services.getL18N().l18n().get("main_menu_credits"), tbs);
        button.setWidth(300);
        button.setHeight(75);
        button.setPosition(32, GS.RES_Y - 128, Align.topLeft);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startMenu.addAction(Actions.sequence(
                        Actions.fadeOut(1), Actions.visible(false),
                        Actions.delay(13), Actions.visible(true),
                        Actions.fadeIn(1)
                ));
                stage.addActor(creditsScreen);
                creditsScreen.addAction(Actions.sequence(
                        Actions.moveTo(0, GS.RES_Y),
                        Actions.alpha(0), Actions.visible(true), Actions.fadeIn(2),
                        Actions.moveBy(0, 2000, 12),
                        Actions.fadeOut(2),Actions.visible(false)
                ));
            }
        });
        buttons.put("credits", button);


        for(String key : buttons.keys()) startMenu.addActor(buttons.get(key));
        startMenu.setVisible(false);
        stage.addActor(startMenu);
    }

    public void setUpCredits(TextureAtlas uiTA) {
        TextureAtlas logos = Services.getMedia().getTextureAtlas(TextureAssets.logosSpriteSheetFile);
        this.creditsScreen = new Group();
        Image bg = new Image(uiTA.findRegion("black"));
        bg.setWidth(GS.RES_X);bg.setHeight(4000);
        bg.setPosition(0, GS.RES_Y, Align.topLeft);
        bg.addAction(Actions.alpha(.75f));
        creditsScreen.addActor(bg);
        Image limbusLogo = new Image(logos.findRegion("limbusdev"));
        limbusLogo.setWidth(254);limbusLogo.setHeight(44);
        limbusLogo.setPosition(GS.RES_X / 2, -900, Align.center);
        Image libgdxLogo = new Image(logos.findRegion("libgdx"));
        libgdxLogo.setWidth(256);libgdxLogo.setHeight(43);
        libgdxLogo.setPosition(GS.RES_X / 2, -1900, Align.center);

        String creditText = "Developed by\n\n" +
                "Georg Eckert, LimbusDev 2016\n\n\n\n" +
                "Artwork\n\n" +
                "Monsters by\n" +
                "Moritz, Maria-Christin & Georg Eckert\n\n\n" +
                "Character Templates by PlayerRed-1\n" +
                "\n\n\n" +
                "Music\n\n" +
                "\n" +
                "City Loop by Homingstar (CC-BY-SA-3.0)\n\n" +
                "CalmBGM by syncopika (CC-BY-3.0)\n\n\n\n" +
                "Powered by\n\n";
        Label.LabelStyle labs = new Label.LabelStyle();
        labs.font = skin.getFont("white");
        Label text = new Label(creditText, labs);
        text.setAlignment(Align.top,Align.top);
        text.setPosition(GS.RES_X /2, -2900, Align.left);
        text.setWidth(0);text.setHeight(2300);
        text.setWrap(true);

        // Sorting
        creditsScreen.addActor(text);
        creditsScreen.addActor(limbusLogo);
        creditsScreen.addActor(libgdxLogo);
        creditsScreen.setVisible(false);
    }

    public void setUpIntro(TextureAtlas uiTA) {
        TextureAtlas logos = Services.getMedia().getTextureAtlas(TextureAssets.logosSpriteSheetFile);
        this.introScreen = new Group();
        Image bg = new Image(uiTA.findRegion("black"));
        bg.setWidth(GS.RES_X);
        bg.setHeight(GS.RES_Y);
        bg.setPosition(0,0);
        Image logo = new Image(logos.findRegion("limbusdevIntro"));
        logo.setWidth(534);logo.setHeight(336);
        logo.setPosition(GS.RES_X / 2, GS.RES_Y / 2,Align.center);
        introScreen.addActor(bg);
        introScreen.addActor(logo);
    }
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
