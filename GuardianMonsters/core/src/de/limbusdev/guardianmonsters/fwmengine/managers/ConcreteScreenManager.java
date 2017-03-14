package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;

/**
 * ConcreteScreenManager manages multiple screens and holds an instance of {@link Game}
 * Created by georg on 13.11.16.
 */

public class ConcreteScreenManager implements ScreenManager {

    private Array<Screen> gameStateStack;
    private Game game;

    public ConcreteScreenManager(Game game) {
        gameStateStack = new Array<Screen>();
        this.game = game;
    }

    /**
     * Pushes a new screen onto the game state stack and changes to it
     * @param screen
     */
    public void pushScreen(Screen screen) {
        gameStateStack.add(screen);
        game.setScreen(screen);
    }

    /**
     * Removes the current screen from the stack and returns to the previous one
     */
    public void popScreen() {
        Screen oldScreen = gameStateStack.pop();
        game.setScreen(gameStateStack.peek());
        oldScreen.dispose();
    }

    public Game getGame() {
        return game;
    }

    @Override
    public void dispose() {
        for(Screen s : gameStateStack) {
            s.dispose();
        }
    }

}
