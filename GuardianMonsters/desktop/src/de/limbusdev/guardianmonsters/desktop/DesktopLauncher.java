package de.limbusdev.guardianmonsters.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.limbusdev.guardianmonsters.GuardianMonsters;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Guardian Monsters";
		config.width = 1280;
		config.height = 720;
		new LwjglApplication(new GuardianMonsters(), config);
	}
}
