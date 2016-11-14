package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

/**
 * Created by georg on 14.11.16.
 */

public interface ScreenManager {
    /**
     * Pushes a new screen onto the game state stack and changes to it
     * @param screen
     */
    public void pushScreen(Screen screen);

    /**
     * Removes the current screen from the stack and returns to the previous one
     */
    public void popScreen();

    public Game getGame();
}
