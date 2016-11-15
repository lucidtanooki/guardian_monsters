package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

/**
 * ScreenManager following the Game State Stack Pattern
 * https://gamedevelopment.tutsplus.com/articles/how-to-build-a-jrpg-a-primer-for-game-developers--gamedev-6676
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
