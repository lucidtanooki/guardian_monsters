package org.limbusdev.monsterworld.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
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
import org.limbusdev.monsterworld.ecs.components.PositionComponent;
import org.limbusdev.monsterworld.ecs.components.TeamComponent;
import org.limbusdev.monsterworld.ecs.entities.HeroEntity;
import org.limbusdev.monsterworld.enums.HUDElements;
import org.limbusdev.monsterworld.enums.SkyDirection;
import org.limbusdev.monsterworld.geometry.IntVector2;
import org.limbusdev.monsterworld.managers.MediaManager;
import org.limbusdev.monsterworld.model.BattleFactory;
import org.limbusdev.monsterworld.utils.EntityFamilies;
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
    private Group menuButtons, conversationLabel;
    private TextureAtlas UItextures;
    public final BattleScreen battleScreen;
    public final MonsterWorld game;
    public final SaveGameManager saveGameManager;
    public Engine engine;
    public final Entity hero;
    public Image blackCourtain, joyStickBG, joyStick;
    private HUDElements openHUDELement;
    
    /* ........................................................................... CONSTRUCTOR .. */
    public HUD(final BattleScreen battleScreen, final MonsterWorld game,
               final SaveGameManager saveGameManager, final Entity hero, MediaManager media,
               Engine engine) {
        this.openHUDELement = HUDElements.NONE;
        this.engine = engine;
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

        // Menu Button
        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
        tbs.font = skin.getFont("white");
        tbs.down = new TextureRegionDrawable(UItextures.findRegion("buttonMenuDown"));
        tbs.up   = new TextureRegionDrawable(UItextures.findRegion("buttonMenuUp"));
        tbs.unpressedOffsetX = 10; tbs.unpressedOffsetY = 0;
        tbs.pressedOffsetX = 10; tbs.pressedOffsetY = 1;
        TextButton menu = new TextButton("Menu", tbs);
        menu.setWidth(154);menu.setHeight(58);
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
        tbs.font = skin.getFont("white");
        tbs.down = new TextureRegionDrawable(UItextures.findRegion("buttonSideBarDown"));
        tbs.up   = new TextureRegionDrawable(UItextures.findRegion("buttonSideBarUp"));
        tbs.pressedOffsetY = -1;
        TextButton save = new TextButton("Save", tbs);
        save.setWidth(111);save.setHeight(52);
        save.setPosition(
                GlobalSettings.RESOLUTION_X,
                GlobalSettings.RESOLUTION_Y - 62, Align.topRight);
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
        quit.setPosition(GlobalSettings.RESOLUTION_X,
                GlobalSettings.RESOLUTION_Y - 118 , Align.topRight);
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
        battle.setPosition(GlobalSettings.RESOLUTION_X,
                GlobalSettings.RESOLUTION_Y - 174, Align.topRight);
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
        A.setWidth(112);A.setHeight(112);
        A.setPosition(GlobalSettings.RESOLUTION_X - 64, 140, Align.center);
        A.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Button A");
                touchEntity();
            }
        });
        this.stage.addActor(A);

        // B Button
        ImageButton B = new ImageButton(
                new TextureRegionDrawable(game.media.getUITextureAtlas().findRegion("button_b")),
                new TextureRegionDrawable(game.media.getUITextureAtlas().findRegion("button_bdown")));
        B.setWidth(80);B.setHeight(80);
        B.setPosition(GlobalSettings.RESOLUTION_X - 96, 48, Align.center);
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
    }

    public void closeConversation() {
        titleLabel.setVisible(false);
        conversationLabel.setVisible(false);
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
        lbs.font=skin.getFont("white");
        lbs.background=new TextureRegionDrawable(UItextures.findRegion("title"));
        titleLabel = new Label("Title", lbs);
        titleLabel.setHeight(35);
        titleLabel.setWidth(284);
        titleLabel.setVisible(false);
        titleLabel.setPosition(GlobalSettings.RESOLUTION_X / 2 - 275, 154);
        titleLabel.setAlignment(Align.center);

        this.conversationLabel = new Group();

        Image convImg = new Image(UItextures.findRegion("conversation"));
        convImg.setWidth(454); convImg.setHeight(108);
        convImg.setPosition(GlobalSettings.RESOLUTION_X / 2 + 2, 98, Align.center);

        conversationLabel.addActor(convImg);

        lbs = new Label.LabelStyle();
        lbs.font=skin.getFont("white");
        lbs.background=new TextureRegionDrawable(UItextures.findRegion("transparent"));
        convText = new Label("Test label", lbs);
        convText.setHeight(108);
        convText.setWidth(316);
        convText.setWrap(true);
        convText.setPosition(GlobalSettings.RESOLUTION_X / 2, 98, Align.center);
        conversationLabel.addActor(convText);
        conversationLabel.setVisible(false);

        stage.addActor(titleLabel);
        stage.addActor(conversationLabel);
    }

    public void updateJoyStick(float x, float y) {
        this.joyStick.setPosition(x,y,Align.center);
    }
    public void resetJoyStick() {
        joyStick.addAction(Actions.sequence(Actions.moveTo(98 - 96 / 2, 98 - 96 / 2, .2f, Interpolation.pow2In)));
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

        if(GlobalSettings.DEBUGGING_ON)
            System.out.println("Grid cell to be checked: ("+checkGridCell.x+"|"+checkGridCell.y+")");

        for(Entity e : engine.getEntitiesFor(Family.all(PositionComponent.class).get())) {

            if (Components.position.get(e) != null && !(e instanceof HeroEntity)) {
                PositionComponent p = Components.position.get(e);

                if(GlobalSettings.DEBUGGING_ON)
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
}
