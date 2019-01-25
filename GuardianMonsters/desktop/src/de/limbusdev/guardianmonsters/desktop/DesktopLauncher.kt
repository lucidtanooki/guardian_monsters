package de.limbusdev.guardianmonsters.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration

import de.limbusdev.guardianmonsters.GuardianMonsters

object DesktopLauncher {

    @JvmStatic
    fun main(arg: Array<String>) {

        val config = LwjglApplicationConfiguration()
        config.width = 1280
        config.height = 720
        config.title = "GuardianMonsters"
        LwjglApplication(GuardianMonsters(), config)
    }
}
