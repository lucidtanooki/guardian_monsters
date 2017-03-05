package de.limbusdev.guardianmonsters;

import com.badlogic.gdx.Game;

import de.limbusdev.guardianmonsters.data.AudioAssets;
import de.limbusdev.guardianmonsters.data.SkinAssets;
import de.limbusdev.guardianmonsters.data.TextureAssets;
import de.limbusdev.guardianmonsters.fwmengine.managers.AudioManager;
import de.limbusdev.guardianmonsters.fwmengine.managers.ConcreteScreenManager;
import de.limbusdev.guardianmonsters.fwmengine.managers.LocalizationManager;
import de.limbusdev.guardianmonsters.fwmengine.managers.MediaManager;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.fwmengine.managers.SettingsService;
import de.limbusdev.guardianmonsters.fwmengine.managers.UIManager;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.MainMenuScreen;
import de.limbusdev.guardianmonsters.utils.GS;
import de.limbusdev.guardianmonsters.utils.GameStateDebugger;


public class GuardianMonsters extends Game{
	/* ............................................................................ ATTRIBUTES .. */
	
	@Override
	public void create () {

        // Inject Dependencies: MediaManager, AudioManager, ScreenManager, SaveGameManager, ...
        injectDependencies();

        if(!GS.DEBUGGING_ON) {
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
        Services.provide(new MediaManager(
            TextureAssets.getAllTexturePackPaths(),
            TextureAssets.getAllTexturePaths(),
            TextureAssets.monsterSpriteSheetFile,
            TextureAssets.monsterMiniSpriteSheetFile,
            TextureAssets.heroSpritesheetFile,
            TextureAssets.animations
        ));
        Services.provide(new AudioManager(
            AudioAssets.get().getAllSfxPaths(),
            AudioAssets.get().getAllMusicPaths()
        ));
        Services.provide(new ConcreteScreenManager(this));
        Services.provide(new LocalizationManager());
        Services.provide(new SettingsService());
        Services.provide(new UIManager(SkinAssets.defaultFont));
    }

}
