package de.limbusdev.guardianmonsters.data;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.enums.SFXType;

/**
 * Simple Convenience Class for file path storage
 * Created by georg on 14.11.16.
 */

public class AudioAssets {

    private static final String TAG = AudioAssets.class.getSimpleName();

    private String SFXdir = "sfx/";
    private ArrayMap<SFXType,Array<String>> battleSFX;
    private Array<String> bgMusicTown;
    private Array<String> battleMusic;
    public static final String victoryFanfareMusic = "music/victory_fanfare.ogg";
    public static final String victorySongMusic = "music/victory_song.ogg";

    private static AudioAssets instance;

    public static AudioAssets get() {
        if(instance == null) {

            //Making Singleton Thread Safe
            //http://www.journaldev.com/1377/java-singleton-design-pattern-best-practices-examples

            synchronized (AudioAssets.class) {
                if(instance == null) {
                    instance = new AudioAssets();
                }
            }
        }
        return instance;
    }

    private AudioAssets() {
        // Music
        bgMusicTown = new Array<String>();
        bgMusicTown.add("music/town_loop_1.wav");
        bgMusicTown.add("music/town_loop_2.ogg");

        battleMusic = new Array<String>();
        battleMusic.add("music/battle_1.ogg");

        // SFX
        battleSFX = new ArrayMap<SFXType,Array<String>>();

        Array<String> sfxHits = new Array<String>();
        for(int i=1;i<=18;i++) sfxHits.add(SFXdir + "hits/" + i + ".ogg");
        battleSFX.put(SFXType.HIT, sfxHits);

        Array<String> sfxWater = new Array<String>();
        for(int i=1;i<=1;i++) sfxWater.add(SFXdir + "water/" + i + ".ogg");
        battleSFX.put(SFXType.WATER, sfxWater);
    }

    public ArrayMap<SFXType, Array<String>> getBattleSFX() {
        return battleSFX;
    }

    public String getBattleSFX(SFXType type, int index) {
        try {
            return battleSFX.get(type).get(index);
        } catch (Exception e) {
            System.err.println(TAG + " couldn't find SFX of type " + type.toString() + " index " + index);
            e.printStackTrace();
            return battleSFX.get(SFXType.HIT).get(0);
        }
    }

    public Array<String> getBgMusicTown() {
        return bgMusicTown;
    }

    public String getBgMusicTown(int index) {
        return bgMusicTown.get(index);
    }

    public Array<String> getBattleMusic() {
        return battleMusic;
    }

    public String getBattleMusic(int index) {
        return battleMusic.get(index);
    }

    public Array<String> getAllSfxPaths() {
        Array<String> sfxPaths = new Array<String>();
        for(Array<String> a : battleSFX.values()) {
            sfxPaths.addAll(a);
        }
        return sfxPaths;
    }

    public Array<String> getAllMusicPaths() {
        Array<String> musicPaths = new Array<String>();
        musicPaths.addAll(battleMusic);
        musicPaths.addAll(bgMusicTown);
        musicPaths.add(victoryFanfareMusic);
        musicPaths.add(victorySongMusic);
        return musicPaths;
    }
}
