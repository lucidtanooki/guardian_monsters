package org.limbusdev.monsterworld.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import org.limbusdev.monsterworld.FriendsWithMonsters;
import org.limbusdev.monsterworld.ecs.components.Components;
import org.limbusdev.monsterworld.ecs.components.InputComponent;
import org.limbusdev.monsterworld.ecs.components.PositionComponent;
import org.limbusdev.monsterworld.ecs.components.TeamComponent;
import org.limbusdev.monsterworld.ecs.entities.HeroEntity;
import org.limbusdev.monsterworld.enums.HUDElements;
import org.limbusdev.monsterworld.enums.SkyDirection;
import org.limbusdev.monsterworld.geometry.IntVector2;
import org.limbusdev.monsterworld.managers.MediaManager;
import org.limbusdev.monsterworld.model.BattleFactory;
import org.limbusdev.monsterworld.utils.EntityFamilies;
import org.limbusdev.monsterworld.utils.GlobPref;
import org.limbusdev.monsterworld.managers.SaveGameManager;

/**
 * Copyright @ Georg Eckert
 *
 * This Class creates a HUD which is displayed on top of the level screen, when not in battle.
 * It includes the main menu (save, quit, monsters, ...), controls (dpad, A, B) and text display.
 */
public class HUD extends InputAdapter {
    /* ............................................................................ ATTRIBUTES .. */
    private Skin skin;
    public Stage stage;

    public Vector2 touchPos;

    private Label convText;
    private Label titleLabel;
    private Group menuButtons, conversationLabel;
    private TextureAtlas UItextures;
    public final BattleScreen battleScreen;
    public final FriendsWithMonsters game;
    public final SaveGameManager saveGameManager;
    public Engine engine;
    public final Entity hero;
    public Image blackCourtain;
    private HUDElements openHUDELement;

    // Digital Pad
    private Array<TextureRegion> dPadImgs;
    private Image dpadImage;
    private Rectangle dPadArea;
    private Vector2 dPadCenter, dPadCenterDist;
    
    /* ........................................................................... CONSTRUCTOR .. */
    public HUD(final BattleScreen battleScreen, final FriendsWithMonsters game,
               final SaveGameManager saveGameManager, final Entity hero, MediaManager media,
               Engine engine) {

        this.game = game;
        this.engine = engine;
        this.battleScreen = battleScreen;
        this.saveGameManager = saveGameManager;
        this.openHUDELement = HUDElements.NONE;
        this.hero = hero;
        this.UItextures = game.media.getUITextureAtlas();


        // Scene2D
        FitViewport fit = new FitViewport(GlobPref.RES_X, GlobPref.RES_Y);
        this.stage = new Stage(fit);
        this.skin = media.skin;

        setUpConversation();
        setUpTopLevelButtons();
        setUpDpad();


        // Images ............................................................................ START
        this.blackCourtain = new Image(game.media.getBattleUITextureAtlas().findRegion("black"));
        this.blackCourtain.setWidth(GlobPref.RES_X);
        this.blackCourtain.setHeight(GlobPref.RES_Y);
        this.blackCourtain.setPosition(0, 0);
        // Images .............................................................................. END

        stage.addActor(blackCourtain);
    }



    /* ............................................................................... METHODS .. */

    /**
     * Creates main menu buttons and the dpad
     */
    private void setUpTopLevelButtons() {

        // Button Style
        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
        tbs.font = skin.getFont("white");
        tbs.down = new TextureRegionDrawable(UItextures.findRegion("buttonMenuDown"));
        tbs.up   = new TextureRegionDrawable(UItextures.findRegion("buttonMenuUp"));
        tbs.unpressedOffsetX = 10; tbs.unpressedOffsetY = 0;
        tbs.pressedOffsetX = 10; tbs.pressedOffsetY = 1;

        // Menu Button
        TextButton menu = new TextButton("Menu", tbs);
        menu.setWidth(154);menu.setHeight(58);
        menu.setPosition(GlobPref.RES_X, GlobPref.RES_Y, Align.topRight);
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

        // Group containing buttons: Save, Quit, Monsters
        this.menuButtons = new Group();

        // Save Button
        tbs = new TextButton.TextButtonStyle();
        tbs.font = skin.getFont("white");
        tbs.down = new TextureRegionDrawable(UItextures.findRegion("buttonSideBarDown"));
        tbs.up   = new TextureRegionDrawable(UItextures.findRegion("buttonSideBarUp"));
        tbs.pressedOffsetY = -1;
        TextButton save = new TextButton("Save", tbs);
        save.setWidth(111);save.setHeight(52);
        save.setPosition(
                GlobPref.RES_X,
                GlobPref.RES_Y - 62, Align.topRight);
        save.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveGameManager.saveGame();
            }
        });
        this.menuButtons.addActor(save);

        // Quit Button
        TextButton quit = new TextButton("Quit", tbs);
        quit.setWidth(111);quit.setHeight(52);
        quit.setPosition(GlobPref.RES_X,
                GlobPref.RES_Y - 118 , Align.topRight);
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
        battle.setWidth(111);battle.setHeight(52);
        battle.setPosition(GlobPref.RES_X,
                GlobPref.RES_Y - 174, Align.topRight);
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

        // Team Button
        TextButton teamButton = new TextButton("Team", tbs);
        teamButton.setWidth(111);quit.setHeight(52);
        teamButton.setPosition(GlobPref.RES_X,
                GlobPref.RES_Y - 230 , Align.topRight);
        teamButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO
            }
        });
        this.menuButtons.addActor(teamButton);


        // ................................................................................ CONTROLS
        // A Button
        ImageButton A = new ImageButton(
                new TextureRegionDrawable(game.media.getUITextureAtlas().findRegion("button_a")),
                new TextureRegionDrawable(game.media.getUITextureAtlas().findRegion("button_adown")));
        A.setSize(GlobPref.RES_X *.125f, GlobPref.RES_X *.125f);
        A.setPosition(GlobPref.RES_X - GlobPref.RES_X *.0375f,
                GlobPref.RES_X *.1125f, Align.bottomRight);
        A.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Button A");
                touchEntity();
            }
        });
        this.stage.addActor(A);
        this.stage.setDebugAll(true);

        // B Button
        ImageButton B = new ImageButton(
                new TextureRegionDrawable(game.media.getUITextureAtlas().findRegion("button_b")),
                new TextureRegionDrawable(game.media.getUITextureAtlas().findRegion("button_bdown")));
        B.setWidth(GlobPref.RES_X *.1f);B.setHeight(GlobPref.RES_X *.1f);
        B.setPosition(GlobPref.RES_X - GlobPref.RES_X *.0125f,
                GlobPref.RES_X *.0125f, Align.bottomRight);
        B.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Button B");
                switch(openHUDELement) {
                    case CONVERSATION:;
                    case SIGN: closeConversation();break;
                    default:break;
                }
            }
        });
        this.stage.addActor(B);

        this.menuButtons.setVisible(false);
        this.menuButtons.addAction(Actions.alpha(0));
        stage.addActor(menu);
        stage.addActor(menuButtons);
    }

    private boolean walk(SkyDirection dir, InputComponent input) {
        if (!input.moving) {
            System.out.println("Move");
            input.startMoving = true;
            input.skyDir = dir;
            input.nextInput = dir;
            input.touchDown = true;
            return true;
        } else {
            input.nextInput = dir;
            return false;
        }
    }

    private void stop(InputComponent input) {
        System.out.println("Stop");
        input.touchDown = false;
    }



    public void draw() {
        this.stage.draw();
    }

    /**
     * Handles touch down events, especially for the digital steering pad
     * @param screenX
     * @param screenY
     * @param pointer
     * @param button
     * @return
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Components.input.get(hero).firstTip = TimeUtils.millis();
        return touchDragged(screenX,screenY,pointer);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        touchPos.x = screenX;
        touchPos.y = screenY;
        touchPos = stage.getViewport().unproject(touchPos);
        InputComponent input = Components.input.get(hero);
        if(dPadArea.contains(touchPos)) {
            input.touchDown = true;
            // Touch within digital pad constraints
            // decide direction
            dPadCenterDist.x = Math.abs(dPadCenter.x - touchPos.x);
            dPadCenterDist.y = Math.abs(dPadCenter.y - touchPos.y);
            if(dPadCenterDist.x > dPadCenterDist.y) {
                // Horizontal
                if(touchPos.x < dPadCenter.x) {
                    // Left
                    walk(SkyDirection.W, input);
                    dpadImage.setDrawable(new TextureRegionDrawable(dPadImgs.get(4)));
                } else {
                    // Right
                    walk(SkyDirection.E, input);
                    dpadImage.setDrawable(new TextureRegionDrawable(dPadImgs.get(2)));
                }
            } else {
                // Vertical
                if(touchPos.y < dPadCenter.y) {
                    // Down
                    walk(SkyDirection.S, input);
                    dpadImage.setDrawable(new TextureRegionDrawable(dPadImgs.get(3)));
                } else {
                    // Up
                    walk(SkyDirection.N, input);
                    dpadImage.setDrawable(new TextureRegionDrawable(dPadImgs.get(1)));
                }
            }
            if(!input.moving) input.startMoving = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        stop(Components.input.get(hero));
        dpadImage.setDrawable(new TextureRegionDrawable(dPadImgs.get(0)));
        return true;
    }

    public void update(float delta) {
        stage.act(delta);
    }

    // ............................................................................... INPUT ADAPTER


    /* ..................................................................... GETTERS & SETTERS .. */
    public InputProcessor getInputProcessor() {
        return this.stage;
    }

    public void openConversation(String text) {
        this.menuButtons.setVisible(false);
        this.convText.setText(text);
        this.conversationLabel.setVisible(true);
        conversationLabel.addAction(Actions.moveTo(0,0,.5f, Interpolation.exp10Out));
    }

    public void closeConversation() {
        conversationLabel.addAction(Actions.sequence(Actions.moveTo(0,-256,.5f, Interpolation.exp10In), Actions.visible(false)));
        Components.getInputComponent(hero).talking = false;
    }

    public void openSign(String title, String text) {
        openConversation(text);
        this.titleLabel.setText(title);
        this.titleLabel.setVisible(true);
    }

    public void show() {
        blackCourtain.addAction(Actions.sequence(Actions.fadeOut(1), Actions.visible(false)));
    }

    public void hide() {
        blackCourtain.addAction(Actions.sequence(Actions.visible(true),Actions.fadeIn(1)));
    }

    private void setUpConversation() {
        Label.LabelStyle lbs = new Label.LabelStyle();

        this.conversationLabel = new Group();

        Image convImg = new Image(UItextures.findRegion("text_bg_L"));
        convImg.setWidth(640); convImg.setHeight(256);
        convImg.setPosition(0,0,Align.bottomLeft);
        Image convImg2 = new Image(UItextures.findRegion("text_bg_R"));
        convImg2.setWidth(640); convImg2.setHeight(256);
        convImg2.setPosition(GlobPref.RES_X,0,Align.bottomRight);

        conversationLabel.addActor(convImg);
        conversationLabel.addActor(convImg2);

        lbs = new Label.LabelStyle();
        lbs.font=skin.getFont("white");
        lbs.background=new TextureRegionDrawable(UItextures.findRegion("transparent"));
        convText = new Label("Test label", lbs);
        convText.setHeight(108);
        convText.setWidth(316);
        convText.setWrap(true);
        convText.setPosition(GlobPref.RES_X / 2, 98, Align.center);
        conversationLabel.addActor(convText);
        conversationLabel.setVisible(false);

        titleLabel = new Label("", lbs);
        titleLabel.setHeight(35);
        titleLabel.setWidth(284);
        titleLabel.setVisible(false);
        titleLabel.setPosition(GlobPref.RES_X / 3.3f, GlobPref.RES_Y /4);
        titleLabel.setAlignment(Align.center);
        conversationLabel.addActor(titleLabel);
        conversationLabel.setPosition(0,-256,Align.bottomLeft);

        stage.addActor(conversationLabel);
    }

    public Entity checkForNearInteractiveObjects(Entity hero) {
        PositionComponent pos = Components.position.get(hero);
        SkyDirection dir = Components.input.get(hero).skyDir;

        Entity nearEntity=null;
        IntVector2 checkGridCell = new IntVector2(pos.onGrid.x,pos.onGrid.y);

        switch(dir) {
            case N: checkGridCell.y+=1;break;
            case S: checkGridCell.y-=1;break;
            case E: checkGridCell.x+=1;break;
            case W: checkGridCell.x-=1;break;
            default: break;
        }

        if(GlobPref.DEBUGGING_ON)
            System.out.println("Grid cell to be checked: ("+checkGridCell.x+"|"+checkGridCell.y+")");

        for(Entity e : engine.getEntitiesFor(Family.all(PositionComponent.class).get())) {

            if (Components.position.get(e) != null && !(e instanceof HeroEntity)) {
                PositionComponent p = Components.position.get(e);

                if(GlobPref.DEBUGGING_ON)
                    System.out.println("Grid Cell of tested Entity: ("+p.onGrid.x+"|"+p.onGrid.y+")");

                // Is there an entity?
                if (p.onGrid.x == checkGridCell.x && p.onGrid.y == checkGridCell.y)
                    nearEntity = e;
            }
        }

        return nearEntity;
    }

    public void touchEntity() {
        Entity touchedEntity = checkForNearInteractiveObjects(hero);
        boolean touchedSpeaker = false;
        boolean touchedSign = false;

        // If there is an entity near enough
        if (touchedEntity != null) {

            // Living Entity
            if (EntityFamilies.living.matches(touchedEntity)) {
                System.out.print("Touched speaker\n");
                touchedSpeaker = true;
                Components.path.get(touchedEntity).talking = true;
                SkyDirection talkDir;
                switch (Components.input.get(hero).skyDir) {
                    case N:
                        talkDir = SkyDirection.SSTOP;
                        break;
                    case S:
                        talkDir = SkyDirection.NSTOP;
                        break;
                    case W:
                        talkDir = SkyDirection.ESTOP;
                        break;
                    case E:
                        talkDir = SkyDirection.WSTOP;
                        break;
                    default:
                        talkDir = SkyDirection.SSTOP;
                }
                Components.path.get(touchedEntity).talkDir = talkDir;
                openConversation(Components.conversation.get(touchedEntity).text);
                openHUDELement = HUDElements.CONVERSATION;
            }

            // Sign Entity
            if (EntityFamilies.signs.matches(touchedEntity)) {
                System.out.print("Touched sign\n");
                touchedSign = true;
                openSign(Components.title.get(touchedEntity).text,
                        Components.conversation.get(touchedEntity).text);
                openHUDELement = HUDElements.SIGN;
            }
        }
        if (touchedSpeaker || touchedSign) Components.getInputComponent(hero).talking = true;
    }

    // ............................................................................. SET UP CONTROLS
    private void setUpDpad() {
        // Initialize DPAD
        this.touchPos = new Vector2();
        float borderDist = GlobPref.RES_X *0.0125f;
        this.dPadArea = new Rectangle(
                borderDist,
                borderDist,
                0.225f* GlobPref.RES_X +borderDist,
                0.225f* GlobPref.RES_X +borderDist);
        this.dPadCenter = dPadArea.getCenter(new Vector2());
        this.dPadCenterDist = new Vector2();

        this.dPadImgs = new Array<TextureRegion>();
        this.dPadImgs.add(game.media.getUITextureAtlas().findRegion("dpad_idle"));
        this.dPadImgs.add(game.media.getUITextureAtlas().findRegion("dpad_up"));
        this.dPadImgs.add(game.media.getUITextureAtlas().findRegion("dpad_right"));
        this.dPadImgs.add(game.media.getUITextureAtlas().findRegion("dpad_down"));
        this.dPadImgs.add(game.media.getUITextureAtlas().findRegion("dpad_left"));
        dpadImage = new Image(dPadImgs.first());
        dpadImage.setSize(dPadArea.width, dPadArea.height);
        dpadImage.setPosition(dPadCenter.x, dPadCenter.y, Align.center);

        this.stage.addActor(dpadImage);
    }
}
