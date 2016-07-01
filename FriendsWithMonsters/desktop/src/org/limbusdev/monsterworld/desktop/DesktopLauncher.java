package org.limbusdev.monsterworld.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.limbusdev.monsterworld.MonsterWorld;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
		config.title = "Friends with Monsters";
		config.width = 640;
		config.height = 360;
		new LwjglApplication(new MonsterWorld(), config);
	}
}
