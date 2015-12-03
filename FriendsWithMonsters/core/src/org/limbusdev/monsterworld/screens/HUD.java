package org.limbusdev.monsterworld.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import org.limbusdev.monsterworld.MonsterWorld;
import org.limbusdev.monsterworld.ecs.components.SaveGameComponent;
import org.limbusdev.monsterworld.utils.GameState;
import org.limbusdev.monsterworld.utils.GlobalSettings;
import org.limbusdev.monsterworld.managers.SaveGameManager;

/**
 * Created by georg on 02.12.15.
 */
public class HUD {
    /* ............................................................................ ATTRIBUTES .. */
    private Skin skin;
    public Stage stage;

    private final TextButton menuButton, battleButton, saveButton;
    private final Label conversationLabel;
    private final Label titleLabel;
    private final TextButton conversationExitButton;
    public final BattleScreen battleScreen;
    public final MonsterWorld game;
    public final SaveGameManager saveGameManager;
    
    /* ........................................................................... CONSTRUCTOR .. */
    public HUD(final BattleScreen battleScreen, final MonsterWorld game, final SaveGameManager saveGameManager) {
        this.saveGameManager = saveGameManager;
        this.battleScreen = battleScreen;
        this.game = game;
        // Scene2D
        FitViewport fit = new FitViewport(
                GlobalSettings.RESOLUTION_X, GlobalSettings.RESOLUTION_Y);

        this.stage = new Stage(fit);
        this.skin = new Skin(Gdx.files.internal("scene2d/uiskin.json"));

        // Buttons .................................................................................
        menuButton = new TextButton("MENU", skin, "default");
        menuButton.setWidth(64f);
        menuButton.setHeight(32f);
        menuButton.setPosition(GlobalSettings.RESOLUTION_X - 72f, GlobalSettings.RESOLUTION_Y -
                64f);

        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.print("opening menu\n");
            }
        });

        battleButton = new TextButton("Battle", skin, "default");
        battleButton.setWidth(64f);
        battleButton.setHeight(32f);
        battleButton.setPosition(GlobalSettings.RESOLUTION_X - 72f,
                GlobalSettings.RESOLUTION_Y - 98f);

        battleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(battleScreen);
            }
        });

        saveButton = new TextButton("Save", skin, "default");
        saveButton.setWidth(64f);
        saveButton.setHeight(32f);
        saveButton.setPosition(GlobalSettings.RESOLUTION_X - 72f,
                GlobalSettings.RESOLUTION_Y - 132f);

        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveGameManager.saveGame();
            }
        });

        titleLabel = new Label("Title", skin, "default");
        titleLabel.setHeight(32);
        titleLabel.setWidth(256);
        titleLabel.setVisible(false);
        titleLabel.setPosition(GlobalSettings.RESOLUTION_X / 2 - 300f, 104);

        conversationLabel = new Label("Test label", skin, "default");
        conversationLabel.setHeight(96);
        conversationLabel.setWidth(600);
        conversationLabel.setWrap(true);
        conversationLabel.setPosition(GlobalSettings.RESOLUTION_X / 2 - 300f, 8);
        conversationLabel.setVisible(false);

        conversationExitButton = new TextButton("+", skin, "default");
        conversationExitButton.setWidth(32f);
        conversationExitButton.setHeight(32f);
        conversationExitButton.setPosition(700, 72);
        conversationExitButton.setVisible(false);
        conversationExitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                titleLabel.setVisible(false);
                conversationLabel.setVisible(false);
                conversationExitButton.setVisible(false);
            }
        });
        // Buttons ............................................................................. END

        stage.addActor(menuButton);
        stage.addActor(battleButton);
        stage.addActor(titleLabel);
        stage.addActor(conversationLabel);
        stage.addActor(conversationExitButton);
        stage.addActor(saveButton);
    }
    /* ............................................................................... METHODS .. */
    public void draw() {
        this.stage.draw();
    }
    
    /* ..................................................................... GETTERS & SETTERS .. */
    public InputProcessor getInputProcessor() {
        return this.stage;
    }

    public void openConversation(String text) {
        this.conversationLabel.setText(text);
        this.conversationLabel.setVisible(true);
        this.conversationExitButton.setVisible(true);
    }

    public void openSign(String title, String text) {
        openConversation(text);
        this.titleLabel.setText(title);
        this.titleLabel.setVisible(true);
        this.conversationExitButton.setVisible(true);
    }
}
