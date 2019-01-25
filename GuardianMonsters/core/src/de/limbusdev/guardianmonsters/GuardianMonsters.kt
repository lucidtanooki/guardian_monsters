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
import ktx.async.enableKtxCoroutines


class GuardianMonsters : Game() {

    /* ............................................................................ ATTRIBUTES .. */

    override fun create() {

        // Enables Kotlin Co-routines for async tasks
        enableKtxCoroutines()

        // Inject Dependencies: MediaManager, AudioManager, ScreenManager, SaveGameManager, ...
        injectDependencies()

        if(DEBUGGING_ON) {

            // Start Debugging
            GameStateDebugger(this).startDebugging()
        }
        else {

            // Start normal game from Main Menu
            Services.getScreenManager().pushScreen(MainMenuScreen())
        }
    }

    override fun dispose() {

        Services.getUI().dispose()
        Services.getMedia().dispose()
        Services.getAudio().dispose()
        Services.getL18N().dispose()
        Services.getScreenManager().dispose()
        CoreServiceLocator.destroy()
        super.dispose()

        // Dispose of Modules
        ModuleGuardians.destroyModule()
    }

    // ............................................................................. SERVICE LOCATOR
    /**
     * Initializes the Service Locator
     */
    private fun injectDependencies() {

        // Service Locator: Dependency Injection
        println("GuardianMonsters: injecting dependencies ...")

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
}
