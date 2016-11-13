package de.limbusdev.guardianmonsters.managers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;

/**
 * ScreenManager manages multiple screens and holds an instance of {@link Game}
 * Created by georg on 13.11.16.
 */

public class ScreenManager {

    private Array<Screen> gameStateStack;
    private Game game;
    private boolean initialized;

    private static ScreenManager instance;

    private ScreenManager() {
        initialized = false;
        gameStateStack = new Array<Screen>();
    }

    public void init(Game game) {
        this.game = game;
        initialized = true;
    }

    public static ScreenManager get() {
        if(instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }


    /**
     * Pushes a new screen onto the game state stack and changes to it
     * @param screen
     */
    public void pushScreen(Screen screen) throws IllegalStateException {
        checkInitialization();
        gameStateStack.add(screen);
        game.setScreen(screen);
    }

    /**
     * Removes the current screen from the stack and returns to the previous one
     */
    public void popScreen() throws IllegalStateException {
        checkInitialization();
        Screen oldScreen = gameStateStack.pop();
        game.setScreen(gameStateStack.peek());
        oldScreen.dispose();
    }

    public Game getGame() throws IllegalStateException {
        checkInitialization();
        return game;
    }

    /**
     * Checks whether ScreenManager has been initialized correctly and throws exception, if not.
     * @throws IllegalStateException
     */
    private void checkInitialization() throws IllegalStateException  {
        if(!initialized) {
            throw new IllegalStateException("init() method has not been called yet. Attribute 'game' is still null.");
        }
    }
}
