package de.limbusdev.guardianmonsters.fwmengine.world.ui;

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

import de.limbusdev.guardianmonsters.data.TextureAssets;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.InputComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.entities.HeroEntity;
import de.limbusdev.guardianmonsters.enums.HUDElements;
import de.limbusdev.guardianmonsters.enums.SkyDirection;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.BattleScreen;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.InventoryScreen;
import de.limbusdev.guardianmonsters.geometry.IntVec2;
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.entities.EntityFamilies;
import de.limbusdev.guardianmonsters.utils.GS;


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
    public final BattleScreen battleScreen;
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
    public HUD(final BattleScreen battleScreen, final SaveGameManager saveGameManager, final Entity hero, Engine engine) {

        this.engine = engine;
        this.battleScreen = battleScreen;
        this.saveGameManager = saveGameManager;
        this.openHUDELement = HUDElements.NONE;
        this.hero = hero;
        TextureAtlas UItextures = Services.getMedia().getTextureAtlas(TextureAssets.UISpriteSheetFile);


        // Scene2D
        FitViewport fit = new FitViewport(GS.RES_X, GS.RES_Y);
        this.stage = new Stage(fit);
        this.skin = Services.getUI().getDefaultSkin();

        setUpConversation(UItextures);
        setUpTopLevelButtons(UItextures);
        setUpDpad(UItextures);


        // Images ............................................................................ START
        this.blackCourtain = new Image(UItextures.findRegion("black"));
        this.blackCourtain.setWidth(GS.RES_X);
        this.blackCourtain.setHeight(GS.RES_Y);
        this.blackCourtain.setPosition(0, 0);
        // Images .............................................................................. END

        stage.addActor(blackCourtain);
    }



    /* ............................................................................... METHODS .. */

    /**
     * Creates main menu buttons and the dpad
     */
    private void setUpTopLevelButtons(TextureAtlas UItextures) {

        // Menu Button
        TextButton menu = new TextButton(Services.getL18N().l18n().get("hud_menu"), skin, "open-menu");
        menu.setPosition(GS.RES_X, GS.RES_Y+12, Align.topRight);
        menu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(conversationLabel.isVisible()) return;
                if (menuButtons.isVisible()) {
                    menuButtons.addAction(Actions.sequence(
                        Actions.moveBy(144,0,.5f,Interpolation.pow2In), Actions.visible(false)));
                } else {
                    menuButtons.addAction(Actions.sequence(
                            Actions.visible(true), Actions.moveBy(-144,0,.5f,Interpolation.pow2In)
                    ));
                }
            }
        });

        // Group containing buttons: Save, Quit, Monsters
        this.menuButtons = new Group();

        // Save Button
        TextButton save = new TextButton(Services.getL18N().l18n().get("hud_save"), skin, "menu-entry");
        save.setPosition(0, 0, Align.bottomLeft);
        save.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveGameManager.saveGame();
            }
        });
        this.menuButtons.addActor(save);

        // Quit Button
        TextButton quit = new TextButton(Services.getL18N().l18n().get("hud_quit"), skin, "menu-entry");
        quit.setPosition(0, -4.5f*GS.ROW , Align.bottomLeft);
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

        // Team Button
        TextButton teamButton = new TextButton(Services.getL18N().l18n().get("hud_team"), skin, "menu-entry");
        teamButton.setPosition(0, -9f*GS.ROW , Align.bottomLeft);
        teamButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Services.getScreenManager().pushScreen(new InventoryScreen(Components.team.get(hero)));
            }
        });
        this.menuButtons.addActor(teamButton);


        // ................................................................................ CONTROLS
        // A Button
        ImageButton A = new ImageButton(skin, "a");
        A.setPosition(GS.RES_X - 3*GS.COL, 9*GS.ROW, Align.bottomRight);
        A.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Input: Button Action");
                touchEntity();
            }
        });
        this.stage.addActor(A);
        this.stage.setDebugAll(GS.DEBUGGING_ON);

        // B Button
        ImageButton B = new ImageButton(skin, "b");
        B.setPosition(GS.RES_X - GS.COL, GS.ROW, Align.bottomRight);
        B.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Input: Button Cancel");
                switch(openHUDELement) {
                    case CONVERSATION:;
                    case SIGN: closeConversation();break;
                    default:break;
                }
            }
        });
        this.stage.addActor(B);

        this.menuButtons.setVisible(false);
        this.menuButtons.setPosition(GS.RES_X,GS.RES_Y-GS.ROW*9,Align.bottomLeft);
        stage.addActor(menu);
        stage.addActor(menuButtons);
    }

    private boolean walk(SkyDirection dir, InputComponent input) {
        if (!input.moving) {
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

    public void openConversation(String text, String name) {
        this.menuButtons.setVisible(false);
        this.convText.setText(Services.getL18N().l18n().get(text));
        String nm = "";
        if(!name.isEmpty()) {
            nm = Services.getL18N().l18n().get(name);
        }
        this.titleLabel.setText(nm);
        this.titleLabel.setVisible(true);
        this.conversationLabel.setVisible(true);
        conversationLabel.addAction(Actions.moveTo(0,0,.5f, Interpolation.exp10Out));
    }

    public void closeConversation() {
        conversationLabel.addAction(Actions.sequence(Actions.moveTo(0,-256,.5f, Interpolation.exp10In), Actions.visible(false)));
        Components.getInputComponent(hero).talking = false;
    }

    public void openSign(String title, String text) {
        openConversation(text,title);

    }

    public void show() {
        blackCourtain.addAction(Actions.sequence(Actions.fadeOut(1), Actions.visible(false)));
    }

    public void hide() {
        blackCourtain.addAction(Actions.sequence(Actions.visible(true),Actions.fadeIn(1)));
    }

    private void setUpConversation(TextureAtlas UItextures) {
        Label.LabelStyle lbs = new Label.LabelStyle();

        this.conversationLabel = new Group();

        Image convImg = new Image(UItextures.findRegion("dialog_bg2"));
        convImg.setWidth(576); convImg.setHeight(144);
        convImg.setPosition(GS.RES_X/2,0,Align.bottom);

        Image convImg2 = new Image(UItextures.findRegion("dialog_name_bg2"));
        convImg2.setWidth(267); convImg2.setHeight(54);
        convImg2.setPosition(GS.RES_X/2-267,132,Align.bottomLeft);

        conversationLabel.addActor(convImg2);
        conversationLabel.addActor(convImg);

        lbs = new Label.LabelStyle();
        lbs.font=skin.getFont("default-font");
        lbs.background=new TextureRegionDrawable(UItextures.findRegion("transparent"));
        convText = new Label("Test label", lbs);
        convText.setHeight(130);
        convText.setWidth(540);
        convText.setWrap(true);
        convText.setAlignment(Align.topLeft,Align.topLeft);
        convText.setPosition(GS.RES_X / 2 - 270, 0);
        conversationLabel.addActor(convText);
        conversationLabel.setVisible(false);

        lbs = new Label.LabelStyle();
        lbs.font = skin.getFont("default-font");
        lbs.background = new TextureRegionDrawable(UItextures.findRegion("transparent"));
        titleLabel = new Label("", lbs);
        titleLabel.setHeight(48);
        titleLabel.setWidth(256);
        titleLabel.setVisible(false);
        titleLabel.setPosition(GS.RES_X / 2 -267/2,140,Align.bottom);
        titleLabel.setAlignment(Align.center);
        conversationLabel.addActor(titleLabel);
        conversationLabel.setPosition(0,-256,Align.bottomLeft);

        stage.addActor(conversationLabel);
    }

    public Entity checkForNearInteractiveObjects(Entity hero) {
        PositionComponent pos = Components.position.get(hero);
        SkyDirection dir = Components.input.get(hero).skyDir;

        Entity nearEntity=null;
        IntVec2 checkGridCell = new IntVec2(pos.onGrid.x,pos.onGrid.y);

        switch(dir) {
            case N: checkGridCell.y+=1;break;
            case S: checkGridCell.y-=1;break;
            case E: checkGridCell.x+=1;break;
            case W: checkGridCell.x-=1;break;
            default: break;
        }

        if(GS.DEBUGGING_ON)
            System.out.println("Grid cell to be checked: ("+checkGridCell.x+"|"+checkGridCell.y+")");

        for(Entity e : engine.getEntitiesFor(Family.all(PositionComponent.class).get())) {

            if (Components.position.get(e) != null && !(e instanceof HeroEntity)) {
                PositionComponent p = Components.position.get(e);

                if(GS.DEBUGGING_ON)
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
                openConversation(
                    Components.conversation.get(touchedEntity).text,
                    Components.conversation.get(touchedEntity).name
                );
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
    private void setUpDpad(TextureAtlas UItextures) {
        // Initialize DPAD
        this.touchPos = new Vector2();
        float borderDist = GS.RES_X *0.0125f;
        this.dPadArea = new Rectangle(
                borderDist,
                borderDist,
                0.225f* GS.RES_X +borderDist,
                0.225f* GS.RES_X +borderDist);
        this.dPadCenter = dPadArea.getCenter(new Vector2());
        this.dPadCenterDist = new Vector2();

        this.dPadImgs = new Array<TextureRegion>();
        this.dPadImgs.add(UItextures.findRegion("dpad_idle"));
        this.dPadImgs.add(UItextures.findRegion("dpad_up"));
        this.dPadImgs.add(UItextures.findRegion("dpad_right"));
        this.dPadImgs.add(UItextures.findRegion("dpad_down"));
        this.dPadImgs.add(UItextures.findRegion("dpad_left"));
        dpadImage = new Image(dPadImgs.first());
        dpadImage.setSize(dPadArea.width, dPadArea.height);
        dpadImage.setPosition(dPadCenter.x, dPadCenter.y, Align.center);

        this.stage.addActor(dpadImage);
    }
}
