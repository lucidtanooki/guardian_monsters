package de.limbusdev.guardianmonsters.services;

import java.util.HashMap;
import java.util.Map;

import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics;
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

    public static void provide(IMediaManager service) {
        media = service;
    }

    public static Map<IndividualStatistics.StatusEffect, String> statusEffectAssetNames = new HashMap<>();
    static
    {
        statusEffectAssetNames.put(IndividualStatistics.StatusEffect.HEALTHY, "healthy");
        statusEffectAssetNames.put(IndividualStatistics.StatusEffect.BLIND, "blind");
    }

    public static IMediaManager Media() {
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

    public static IAudioManager Audio()
    {
        if(audio == null)
        {
            System.err.println("SERVICES: No Audio service injected yet with Services.provide(Audio audio). Returning NullAudio.");
            return new NullAudioManager();
        }
        else
        {
            return audio;
        }
    }

    public static void provide(IScreenManager service) {
        screens = service;
    }

    public static IScreenManager ScreenManager() {
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

    public static L18N I18N() {
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

    public static Settings Settings() {
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

    public static UI UI() {
        if(ui == null) {
            System.err.println("SERVICES: No UI service injected yet with Services.provide(UI service). Returning NullSettings.");
            return new NullUI();
        } else {
            return ui;
        }
    }
}
