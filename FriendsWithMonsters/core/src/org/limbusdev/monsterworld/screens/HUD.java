package org.limbusdev.monsterworld.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by georg on 02.12.15.
 */
public class HUD {
    /* ............................................................................ ATTRIBUTES .. */
    private Skin skin;
    private Stage stage;

    private final TextButton menuButton;
    private final Label conversationLabel;
    private final TextButton conversationExitButton;
    
    /* ........................................................................... CONSTRUCTOR .. */
    public HUD() {
        // Scene2D
        this.stage = new Stage();
        this.skin = new Skin(Gdx.files.internal("scene2d/uiskin.json"));

        // Buttons .................................................................................
        menuButton = new TextButton("MENU", skin, "default");
        menuButton.setWidth(64f);
        menuButton.setHeight(64f);
        menuButton.setPosition(Gdx.graphics.getWidth() - 64f, Gdx.graphics.getHeight() - 64f);

        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                menuButton.setText("OPEN");
                conversationLabel.setVisible(!conversationLabel.isVisible());
            }
        });

        conversationLabel = new Label("Hello, my name is Rufus. I live here." +
                " Please visit me one day. I have many stories to tell!",
                skin, "default");
        conversationLabel.setHeight(72);
        conversationLabel.setWidth(400);
        conversationLabel.setWrap(true);
        conversationLabel.setPosition(Gdx.graphics.getWidth() / 2 - 200f, 8);
        conversationLabel.setVisible(false);

        conversationExitButton = new TextButton("X", skin, "default");
        conversationExitButton.setWidth(32f);
        conversationExitButton.setHeight(32f);
        conversationExitButton.setPosition(564, 24);
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
