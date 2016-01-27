package org.limbusdev.monsterworld.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.viewport.FitViewport;

import org.limbusdev.monsterworld.MonsterWorld;
import org.limbusdev.monsterworld.ecs.components.Components;
import org.limbusdev.monsterworld.ecs.components.TeamComponent;
import org.limbusdev.monsterworld.managers.MediaManager;
import org.limbusdev.monsterworld.model.BattleFactory;
import org.limbusdev.monsterworld.utils.GlobalSettings;
import org.limbusdev.monsterworld.managers.SaveGameManager;

/**
 * Created by georg on 02.12.15.
 */
public class HUD {
    /* ............................................................................ ATTRIBUTES .. */
    private Skin skin;
    public Stage stage;

    private ArrayMap<String,Button> buttons;
    private Label convText;
    private Label titleLabel;
    private ImageButton conversationExitButton;
    private Group menuButtons, conversationLabel;
    private TextureAtlas UItextures;
    public final BattleScreen battleScreen;
    public final MonsterWorld game;
    public final SaveGameManager saveGameManager;
    public final Entity hero;
    public Image blackCourtain, joyStickBG, joyStick;
    
    /* ........................................................................... CONSTRUCTOR .. */
    public HUD(final BattleScreen battleScreen, final MonsterWorld game,
               final SaveGameManager saveGameManager, final Entity hero, MediaManager media) {
        this.saveGameManager = saveGameManager;
        this.battleScreen = battleScreen;
        this.game = game;
        this.hero = hero;
        this.UItextures = game.media.getUITextureAtlas();

        this.buttons = new ArrayMap<String, Button>();

        // Scene2D
        FitViewport fit = new FitViewport(GlobalSettings.RESOLUTION_X, GlobalSettings.RESOLUTION_Y);

        this.stage = new Stage(fit);
        this.skin = media.skin;

        setUpConversation();
        setUpTopLevelButtons();


        // Images ............................................................................ START
        this.blackCourtain = new Image(game.media.getBattleUITextureAtlas().findRegion("black"));
        this.blackCourtain.setWidth(GlobalSettings.RESOLUTION_X);
        this.blackCourtain.setHeight(GlobalSettings.RESOLUTION_Y);
        this.blackCourtain.setPosition(0, 0);
        // Images .............................................................................. END

        stage.addActor(blackCourtain);
    }
    /* ............................................................................... METHODS .. */
    private void setUpTopLevelButtons() {
        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
        tbs.font = skin.getFont("default-font");
        tbs.down = new TextureRegionDrawable(UItextures.findRegion("bcorner64down"));
        tbs.up   = new TextureRegionDrawable(UItextures.findRegion("bcorner64up"));
        tbs.unpressedOffsetX = 18; tbs.unpressedOffsetY = 18;
        tbs.pressedOffsetX = 18; tbs.pressedOffsetY = 16;
        TextButton menu = new TextButton("Menu", tbs);
        menu.setWidth(64);menu.setHeight(64);
        menu.setPosition(GlobalSettings.RESOLUTION_X, GlobalSettings.RESOLUTION_Y, Align.topRight);
        menu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(conversationLabel.isVisible()) return;

                if (menuButtons.isVisible()) menuButtons.addAction(Actions.sequence(
                        Actions.fadeOut(.3f), Actions.visible(false)));
                else {
                    menuButtons.addAction(Actions.sequence(
                            Actions.visible(true), Actions.fadeIn(.5f)
                    ));
                }
            }
        });

        this.menuButtons = new Group();

        // Save Button
        tbs = new TextButton.TextButtonStyle();
        tbs.font = skin.getFont("default-font");
        tbs.down = new TextureRegionDrawable(UItextures.findRegion("bround64down"));
        tbs.up   = new TextureRegionDrawable(UItextures.findRegion("bround64up"));
        tbs.pressedOffsetY = -1;
        TextButton save = new TextButton("Save", tbs);
        save.setWidth(64);save.setHeight(64);
        save.setPosition(
                GlobalSettings.RESOLUTION_X - 96,
                GlobalSettings.RESOLUTION_Y - 96, Align.center);
        save.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveGameManager.saveGame();
            }
        });

        this.menuButtons.addActor(save);

        // Quit Button
        TextButton quit = new TextButton("Quit", tbs);
        quit.setWidth(64);quit.setHeight(64);
        quit.setPosition(GlobalSettings.RESOLUTION_X - 132,
                GlobalSettings.RESOLUTION_Y - 35, Align.center);
        quit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                blackCourtain.addAction(Actions.sequence(
                        Actions.alpha(0), Actions.visible(true), Actions.fadeIn(2),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                Gdx.app.exit();
                            }
                        })
                ));
            }
        });

        this.menuButtons.addActor(quit);

        // Battle Button
        TextButton battle = new TextButton("Battle", tbs);
        battle.setWidth(64);battle.setHeight(64);
        battle.setPosition(GlobalSettings.RESOLUTION_X - 35,
                GlobalSettings.RESOLUTION_Y - 132, Align.center);
        battle.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TeamComponent oppTeam = new TeamComponent();
                oppTeam.monsters.add(BattleFactory.getInstance().createMonster(7));
                oppTeam.monsters.add(BattleFactory.getInstance().createMonster(4));
                oppTeam.monsters.add(BattleFactory.getInstance().createMonster(11));
                battleScreen.init(Components.team.get(hero), oppTeam);
                game.setScreen(battleScreen);
            }
        });
        this.menuButtons.addActor(battle);

        // A Button
        ImageButton A = new ImageButton(
                new TextureRegionDrawable(game.media.getUITextureAtlas().findRegion("button_a")),
                new TextureRegionDrawable(game.media.getUITextureAtlas().findRegion("button_adown")));
        A.setWidth(112);battle.setHeight(112);
        A.setPosition(GlobalSettings.RESOLUTION_X - 64, 140, Align.center);
        A.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Button A");
            }
        });
        this.stage.addActor(A);

        // B Button
        ImageButton B = new ImageButton(
                new TextureRegionDrawable(game.media.getUITextureAtlas().findRegion("button_b")),
                new TextureRegionDrawable(game.media.getUITextureAtlas().findRegion("button_bdown")));
        B.setWidth(80);battle.setHeight(80);
        B.setPosition(GlobalSettings.RESOLUTION_X - 96, 48, Align.center);
        B.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Button B");
            }
        });
        this.stage.addActor(B);

        this.menuButtons.setVisible(false);
        this.menuButtons.addAction(Actions.alpha(0));
        stage.addActor(menu);
        stage.addActor(menuButtons);

        // JoyStick
        this.joyStickBG = new Image(game.media.getUITextureAtlas().findRegion("stick_bg"));
        joyStickBG.setPosition(8, 8, Align.bottomLeft);
        stage.addActor(joyStickBG);

        this.joyStick = new Image(game.media.getUITextureAtlas().findRegion("stick"));
        joyStick.setPosition(98,98, Align.center);
        stage.addActor(joyStick);
    }



    public void draw() {
        this.stage.draw();
    }

    public void update(float delta) {
        stage.act(delta);
    }
    
    /* ..................................................................... GETTERS & SETTERS .. */
    public InputProcessor getInputProcessor() {
        return this.stage;
    }

    public void openConversation(String text) {
        this.menuButtons.setVisible(false);
        this.convText.setText(text);
        this.conversationLabel.setVisible(true);
        this.conversationExitButton.setVisible(true);
    }

    public void openSign(String title, String text) {
        openConversation(text);
        this.titleLabel.setText(title);
        this.titleLabel.setVisible(true);
        this.conversationExitButton.setVisible(true);
    }

    public void show() {
        blackCourtain.addAction(Actions.sequence(Actions.fadeOut(1), Actions.visible(false)));
    }

    public void hide() {
        blackCourtain.addAction(Actions.sequence(Actions.visible(true),Actions.fadeIn(1)));
    }

    private void setUpConversation() {
        Label.LabelStyle lbs = new Label.LabelStyle();
        lbs.font=skin.getFont("white");
        lbs.background=new TextureRegionDrawable(UItextures.findRegion("title"));
        titleLabel = new Label("Title", lbs);
        titleLabel.setHeight(35);
        titleLabel.setWidth(284);
        titleLabel.setVisible(false);
        titleLabel.setPosition(GlobalSettings.RESOLUTION_X / 2 - 275, 154);

        this.conversationLabel = new Group();

        Image convImg = new Image(UItextures.findRegion("conversation"));
        convImg.setWidth(454); convImg.setHeight(108);
        convImg.setPosition(GlobalSettings.RESOLUTION_X/2+2,98,Align.center);

        conversationLabel.addActor(convImg);

        lbs = new Label.LabelStyle();
        lbs.font=skin.getFont("white");
        lbs.background=new TextureRegionDrawable(UItextures.findRegion("transparent"));
        convText = new Label("Test label", lbs);
        convText.setHeight(108);
        convText.setWidth(316);
        convText.setWrap(true);
        convText.setPosition(GlobalSettings.RESOLUTION_X/2, 98, Align.center);
        conversationLabel.addActor(convText);
        conversationLabel.setVisible(false);

        ImageButton.ImageButtonStyle ibs = new ImageButton.ImageButtonStyle();
        ibs.down=new TextureRegionDrawable(UItextures.findRegion("exitConversationdown"));
        ibs.up=new TextureRegionDrawable(UItextures.findRegion("exitConversationup"));
        conversationExitButton = new ImageButton(ibs);
        conversationExitButton.setWidth(44f);
        conversationExitButton.setHeight(58f);
        conversationExitButton.setPosition(GlobalSettings.RESOLUTION_X/2+246, 0);
        conversationExitButton.setVisible(false);
        conversationExitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                titleLabel.setVisible(false);
                conversationLabel.setVisible(false);
                conversationExitButton.setVisible(false);
                Components.getInputComponent(hero).talking = false;
            }
        });

        stage.addActor(titleLabel);
        stage.addActor(conversationLabel);
        stage.addActor(conversationExitButton);
    }

    public void updateJoyStick(float x, float y) {
        this.joyStick.setPosition(x,y,Align.center);
    }
    public void resetJoyStick() {
        joyStick.addAction(Actions.sequence(Actions.moveTo(98-96/2,98-96/2,.2f, Interpolation.pow2In)));
    }
}
