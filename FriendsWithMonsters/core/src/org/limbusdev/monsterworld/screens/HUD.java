package org.limbusdev.monsterworld.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sun.javafx.scene.control.GlobalMenuAdapter;

import org.limbusdev.monsterworld.utils.GlobalSettings;
import org.limbusdev.monsterworld.utils.UnitConverter;

/**
 * Created by georg on 02.12.15.
 */
public class HUD {
    /* ............................................................................ ATTRIBUTES .. */
    private Skin skin;
    public Stage stage;

    private final TextButton menuButton;
    private final Label conversationLabel;
    private final TextButton conversationExitButton;
    
    /* ........................................................................... CONSTRUCTOR .. */
    public HUD() {
        // Scene2D
        FitViewport fit = new FitViewport(
                GlobalSettings.RESOLUTION_X, GlobalSettings.RESOLUTION_Y);

        this.stage = new Stage(fit);
        this.skin = new Skin(Gdx.files.internal("scene2d/uiskin.json"));

        // Buttons .................................................................................
        menuButton = new TextButton("MENU", skin, "default");
        menuButton.setWidth(64f);
        menuButton.setHeight(64f);
        menuButton.setPosition(GlobalSettings.RESOLUTION_X-64f,GlobalSettings.RESOLUTION_Y-64f);

        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                menuButton.setText("OPEN");
                conversationLabel.setVisible(!conversationLabel.isVisible());
            }
        });

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
                conversationLabel.setVisible(false);
                conversationExitButton.setVisible(false);
            }
        });
        // Buttons ............................................................................. END

        stage.addActor(menuButton);
        stage.addActor(conversationLabel);
        stage.addActor(conversationExitButton);
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
}
