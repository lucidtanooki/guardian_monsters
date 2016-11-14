package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

/**
 * Created by georg on 14.11.16.
 */

public class NullScreenManager implements ScreenManager {
    @Override
    public void pushScreen(Screen screen) {
        // DO NOTHING
    }

    @Override
    public void popScreen() {
        // DO NOTHING
    }

    @Override
    public Game getGame() {
        return null;
    }
}
