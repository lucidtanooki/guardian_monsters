package de.limbusdev.guardianmonsters;

import com.badlogic.gdx.Game;

import de.limbusdev.guardianmonsters.assets.paths.AssetPath;
import de.limbusdev.guardianmonsters.media.AudioManager;
import de.limbusdev.guardianmonsters.media.MediaManager;
import de.limbusdev.guardianmonsters.scene2d.ConcreteScreenManager;
import de.limbusdev.guardianmonsters.services.LocalizationManager;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.services.SettingsService;
import de.limbusdev.guardianmonsters.services.UIManager;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.MainMenuScreen;
import de.limbusdev.guardianmonsters.utils.GameStateDebugger;

import static de.limbusdev.guardianmonsters.Constant.DEBUGGING_ON;


public class GuardianMonsters extends Game{
	/* ............................................................................ ATTRIBUTES .. */
	
	@Override
	public void create () {

        // Inject Dependencies: MediaManager, AudioManager, ScreenManager, SaveGameManager, ...
        injectDependencies();

        if(!DEBUGGING_ON) {
            // Start normal game from Main Menu
            Services.getScreenManager().pushScreen(new MainMenuScreen());
        } else {
            // Start Debugging
            (new GameStateDebugger(this)).startDebugging();
        }
	}

	@Override
	public void render () {
		super.render();
	}

    @Override
    public void dispose() {
        Services.getUI().dispose();
        Services.getMedia().dispose();
        Services.getAudio().dispose();
        Services.getL18N().dispose();
        Services.getScreenManager().dispose();
        super.dispose();
    }




    // ............................................................................. SERVICE LOCATOR
    /**
     * Initializes the Service Locator
     */
    private void injectDependencies() {
        // Service Locator: Dependency Injection
        System.out.println("GuardianMonsters: injecting dependencies ...");
        Services.provide(new MediaManager());
        Services.provide(new AudioManager());
        Services.provide(new ConcreteScreenManager(this));
        Services.provide(new LocalizationManager());
        Services.provide(new SettingsService());
        Services.provide(new UIManager(AssetPath.Skin.FONT));
    }

}
