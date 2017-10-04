package de.limbusdev.guardianmonsters.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.czyzby.autumn.fcs.scanner.DesktopClassScanner;

import de.limbusdev.guardianmonsters.GuardianMonsters;

public class DesktopLauncher
{
	public static void main (String[] arg)
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;
		config.title = "GuardianMonsters";
		new LwjglApplication(new GuardianMonsters(new DesktopClassScanner()), config);
	}
}
