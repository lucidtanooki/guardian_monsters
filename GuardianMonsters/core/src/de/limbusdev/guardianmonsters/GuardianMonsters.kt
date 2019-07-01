package de.limbusdev.guardianmonsters

import com.badlogic.gdx.Game
import de.limbusdev.guardianmonsters.Constant.DEBUGGING_ON
import de.limbusdev.guardianmonsters.assets.paths.AssetPath
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.MainMenuScreen
import de.limbusdev.guardianmonsters.guardians.ModuleGuardians
import de.limbusdev.guardianmonsters.media.AudioManager
import de.limbusdev.guardianmonsters.media.MediaManager
import de.limbusdev.guardianmonsters.scene2d.ConcreteScreenManager
import de.limbusdev.guardianmonsters.services.*
import de.limbusdev.guardianmonsters.utils.GameStateDebugger
import de.limbusdev.utils.logInfo
import ktx.inject.Context
import ktx.log.info


class GuardianMonsters : Game()
{
    override fun create()
    {
        // TODO Enable Kotlin Co-routines for async tasks

        // Inject Dependencies: MediaManager, AudioManager, ScreenManager, SaveGameManager, ...
        injectDependencies()

        when(DEBUGGING_ON)
        {
            // Start Debugging
            true  -> GameStateDebugger(this).startDebugging()
            // Start normal game from Main Menu
            false -> Services.ScreenManager().pushScreen(MainMenuScreen())
        }
    }

    override fun dispose() {

        Services.UI().dispose()
        Services.Media().dispose()
        Services.Audio().dispose()
        Services.I18N().dispose()
        Services.ScreenManager().dispose()
        CoreServiceLocator.destroy()
        super.dispose()

        // Dispose of Modules
        ModuleGuardians.destroyModule()
    }

    // ............................................................................. SERVICE LOCATOR
    /**
     * Initializes the Service Locator
     */
    private fun injectDependencies()
    {
        // Service Locator: Dependency Injection
        logInfo(TAG) { "injecting dependencies ..." }

        Services.provide(MediaManager())
        Services.provide(AudioManager())
        Services.provide(ConcreteScreenManager(this))
        Services.provide(LocalizationManager())
        Services.provide(SettingsService())
        Services.provide(UIManager(AssetPath.Skin.FONT))

        // ....................................................................... module: guardians
        ModuleGuardians.initModule()

        CoreServiceLocator.provide(GameStateService(SaveGameManager()))
    }

    companion object { const val TAG = "GuardianMonsters" }
}
