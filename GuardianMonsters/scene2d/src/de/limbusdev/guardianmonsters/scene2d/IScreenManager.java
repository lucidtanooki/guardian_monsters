package de.limbusdev.guardianmonsters.scene2d;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

/**
 * ScreenManager following the Game State Stack Pattern
 * https://gamedevelopment.tutsplus.com/articles/how-to-build-a-jrpg-a-primer-for-game-developers--gamedev-6676
 *
 * @author Georg Eckert 2017
 */

public interface IScreenManager
{
    /**
     * Pushes a new screen onto the game state stack and changes to it
     * @param screen
     */
    void pushScreen(Screen screen);

    /**
     * Removes the current screen from the stack, disposes it and returns to the previous one
     */
    void popScreen();

    Game getGame();

    void dispose();
}
