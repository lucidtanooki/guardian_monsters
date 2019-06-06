package de.limbusdev.guardianmonsters

import android.os.Bundle

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import de.limbusdev.guardianmonsters.GuardianMonsters

class AndroidLauncher : AndroidApplication()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration()
        initialize(GuardianMonsters(), config)
    }
}
