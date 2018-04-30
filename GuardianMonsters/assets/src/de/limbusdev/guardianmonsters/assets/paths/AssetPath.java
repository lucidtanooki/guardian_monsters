package de.limbusdev.guardianmonsters.assets.paths;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

import de.limbusdev.guardianmonsters.guardians.Element;

import static net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * Paths
 *
 * @author Georg Eckert 2017
 */

public class AssetPath
{
    public static class Spritesheet
    {
        private static final String rootPath = "spritesheets/";
        private static final String extension = ".pack";

        @Asset(TextureAtlas.class)
        public static final String
            HERO = rootPath + "hero" + extension,
            GUARDIANS = rootPath + "monsters" + extension,
            GUARDIANS_MINI = rootPath + "mini" + extension,
            GUARDIANS_PREVIEW= rootPath + "preview" + extension,
            LOGOS = rootPath + "logos" + extension,
            ANIMATIONS = rootPath + "animations" + extension,
            ANIMATIONS_BATTLE = rootPath + "battleAnimations" + extension,
            ANIMATIONS_STATUS_EFFECT = rootPath + "statusEffectAnimations" + extension,
            ANIMATIONS_BIG = rootPath + "bigAnimations" + extension,
            BATTLE_BG = rootPath + "battleBacks" + extension,
            PARTICLES = rootPath + "particles" + extension;
    }

    public static class Textures
    {
        private static final String rootPath = "textures/";

        public static final String[] WEATHER = {
            rootPath + "weather_clouds.png",
            rootPath + "weather_fog.png",
            rootPath + "weather_woods.png",
            rootPath + "weather_fog2.png"
        };

        @Asset(Texture.class)
        public static final String
            MAIN_MENU_BG1 = rootPath + "GM_logo.png",
            MAIN_MENU_BG2 = rootPath + "main_logo_bg.png",
            weather0 = WEATHER[0],
            weather1 = WEATHER[1],
            weather2 = WEATHER[2],
            weather3 = WEATHER[3];
    }

    public static class Audio
    {
        public static class SFX
        {
            private static final String rootPath = "sfx/";

            public static Map<String, Array<String>> BATTLE() {
                Map<String, Array<String>> battleSFX = new HashMap<>();

                Array<String> hits = new Array<>();
                for(int i=1; i<=18; i++) hits.add(rootPath  + "hits/" + i + ".ogg");

                Array<String> water = new Array<>();
                for(int i=1;i<=1;i++) water.add(rootPath + "water/" + i + ".ogg");

                Array<String> spell = new Array<>();
                for(int i=1;i<=2;i++) spell.add(rootPath + "spell/" + i + ".ogg");

                battleSFX.put("HIT", hits);
                battleSFX.put("WATER", water);
                battleSFX.put("SPELL", spell);

                return battleSFX;
            }

            public String BATTLE(String type, int index) {
                Map<String, Array<String>> battleSFX = BATTLE();

                if(!battleSFX.containsKey(type) || battleSFX.get(type).size <= index) {
                    return battleSFX.get("HIT").get(0);
                } else {
                    return battleSFX.get(type).get(index);
                }
            }

            public static String[] all()
            {
                Array<String> all = new Array<>();
                for(Array<String> al : BATTLE().values()) {
                    all.addAll(al);
                }
                all.add(METAMORPHOSIS);

                String[] allPaths = new String[all.size];
                for(int i=0; i<all.size; i++) allPaths[i] = all.get(i);

                return allPaths;
            }

            @Asset(Sound.class)
            public static final String METAMORPHOSIS = rootPath + "metamorphosis.ogg";

            @Asset(Sound.class)
            public static final String[] allSFX = all();
        }

        public static class Music
        {
            private static final String rootPath = "music/";

            public static final String[] BG_TOWN = {
                rootPath + "town_loop_1.wav",
                rootPath + "town_loop_2.ogg"
            };

            public static final String[] BG_BATTLE = {
                rootPath + "battle_1.ogg"
            };

            @Asset(com.badlogic.gdx.audio.Music.class)
            public static final String
                VICTORY_FANFARE = rootPath + "victory_fanfare.ogg",
                VICTORY_SONG = rootPath + "victory_song.ogg",
                METAMORPHOSIS = rootPath + "metamorphosis.ogg",
                GUARDOSPHERE = rootPath + "guardosphere.ogg",
                BG_TOWN_0 = BG_TOWN[0],
                BG_TOWN_1 = BG_TOWN[1],
                BG_BATTLE_0 = BG_BATTLE[0];
        }
    }

    public static class I18N
    {
        private static final String rootPath = "l18n/";

        public static final String GENERAL      = rootPath + "general";
        public static final String GUARDIANS    = rootPath + "monsters";
        public static final String MAP_PREFIX   = rootPath + "map_";
        public static final String INVENTORY    = rootPath + "inventory";
        public static final String BATTLE       = rootPath + "battle";
        public static final String ATTACKS      = rootPath + "attacks";
        public static final String ELEMENTS     = rootPath + "elements";
    }

    public static class Skin
    {
        public static final String
            DEFAULT = "scene2d/defaultSkin",
            BATTLE = "scene2d/battleSkin",
            INVENTORY = "scene2d/inventorySkin";

        public static final String FONT = "fonts/PixelOperator-Bold.ttf";

        public static String attackButtonStyle(Element element) {
            return "tb-attack-" + element.toString().toLowerCase();
        }
    }
}
