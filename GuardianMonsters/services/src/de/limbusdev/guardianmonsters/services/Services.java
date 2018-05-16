package de.limbusdev.guardianmonsters.services;


import de.limbusdev.guardianmonsters.media.IAudioManager;
import de.limbusdev.guardianmonsters.media.IMediaManager;
import de.limbusdev.guardianmonsters.media.NullAudioManager;
import de.limbusdev.guardianmonsters.media.NullMediaManager;
import de.limbusdev.guardianmonsters.scene2d.IScreenManager;

/**
 * Services implements the Service Locator Pattern
 * http://gameprogrammingpatterns.com/service-locator.html
 *
 * @author Georg Eckert
 */

public class Services
{
    private static IMediaManager media;
    private static IAudioManager audio;
    private static IScreenManager screens;
    private static L18N l18n;
    private static Settings settings;
    private static UI ui;
    private static IGameStateService gameState;

    public static void provide(IGameStateService service) {
        gameState = service;
    }

    public static IGameStateService getGameState() {
        if(gameState == null) {
            System.err.println("SERVICES: No Game State service injected yet with Services.provide(IGameStateService). Returning NullGameStateService.");
            return new NullGameStateService();
        } else {
            return gameState;
        }
    }

    public static void provide(IMediaManager service) {
        media = service;
    }

    public static IMediaManager getMedia() {
        if(media == null) {
            System.err.println("SERVICES: No Media service injected yet with Services.provide(Media media). Returning NullMedia.");
            return new NullMediaManager();
        } else {
            return media;
        }
    }

    public static void provide(IAudioManager service) {
        audio = service;
    }

    public static IAudioManager getAudio() {
        if(audio == null) {
            System.err.println("SERVICES: No Audio service injected yet with Services.provide(Audio audio). Returning NullAudio.");
            return new NullAudioManager();
        } else {
            return audio;
        }
    }

    public static void provide(IScreenManager service) {
        screens = service;
    }

    public static IScreenManager getScreenManager() {
        if(screens == null) {
            System.err.println("SERVICES: No ScreenManager service injected yet with Services.provide(ScreenManager service). Returning NullScreenManager.");
            return new de.limbusdev.guardianmonsters.scene2d.NullScreenManager();
        } else {
            return screens;
        }
    }

    public static void provide(L18N service) {
        l18n = service;
    }

    public static L18N getL18N() {
        if(l18n == null) {
            System.err.println("SERVICES: No Localization service injected yet with Services.provide(L18N service). Returning NullL18N.");
            return new NullL18N();
        } else {
            return l18n;
        }
    }

    public static void provide(Settings service) {
        settings = service;
    }

    public static Settings getSettings() {
        if(settings == null) {
            System.err.println("SERVICES: No Settings service injected yet with Services.provide(Settings settings). Returning NullSettings.");
            return new NullSettings();
        } else {
            return settings;
        }
    }

    public static void provide(UI service) {
        ui = service;
    }

    public static UI getUI() {
        if(ui == null) {
            System.err.println("SERVICES: No UI service injected yet with Services.provide(UI service). Returning NullSettings.");
            return new NullUI();
        } else {
            return ui;
        }
    }
}
