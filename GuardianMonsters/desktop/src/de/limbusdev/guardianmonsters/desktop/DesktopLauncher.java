package de.limbusdev.guardianmonsters.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.limbusdev.guardianmonsters.GuardianMonsters;
import de.limbusdev.guardianmonsters.utils.GuardianMonstersLML;

public class DesktopLauncher
{
	public static void main (String[] arg)
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;
		config.title = "GuardianMonsters";
		//new LwjglApplication(new GuardianMonsters(), config);
		new LwjglApplication(new GuardianMonstersLML(), config);
	}
}
