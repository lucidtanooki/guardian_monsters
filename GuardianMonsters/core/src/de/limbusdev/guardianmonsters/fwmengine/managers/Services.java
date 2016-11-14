package de.limbusdev.guardianmonsters.fwmengine.managers;


/**
 * Services implements the Service Locator Pattern
 * http://gameprogrammingpatterns.com/service-locator.html
 *
 * Created by georg on 14.11.16.
 */

public class Services {
    private static Media media;
    private static Audio audio;
    private static ScreenManager screens;
    private static L18N l18n;
    private static Settings settings;
    private static UI ui;

    public static void provide(Media service) {
        media = service;
    }

    public static Media getMedia() {
        if(media == null) {
            System.err.println("SERVICES: No Media service injected yet with Services.provide(Media media). Returning NullMedia.");
            return new NullMedia();
        } else {
            return media;
        }
    }

    public static void provide(Audio service) {
        audio = service;
    }

    public static Audio getAudio() {
        if(audio == null) {
            System.err.println("SERVICES: No Audio service injected yet with Services.provide(Audio audio). Returning NullAudio.");
            return new NullAudio();
        } else {
            return audio;
        }
    }

    public static void provide(ScreenManager service) {
        screens = service;
    }

    public static ScreenManager getScreenManager() {
        if(screens == null) {
            System.err.println("SERVICES: No ScreenManager service injected yet with Services.provide(ScreenManager service). Returning NullScreenManager.");
            return new NullScreenManager();
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
