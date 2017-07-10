package de.limbusdev.guardianmonsters.data.paths;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import static net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.limbusdev.guardianmonsters.enums.Element;

/**
 * Paths
 *
 * @author Georg Eckert 2017
 */

public class Path {
    public static class Spritesheet {
        private static final String rootPath = "spritesheets/";
        private static final String extension = ".pack";

        @Asset(TextureAtlas.class)
        public static final String HERO = rootPath + "hero" + extension;

        @Asset(TextureAtlas.class)
        public static final String GUARDIANS = rootPath + "monsters" + extension;

        @Asset(TextureAtlas.class)
        public static final String GUARDIANS_MINI = rootPath + "mini" + extension;

        @Asset(TextureAtlas.class)
        public static final String GUARDIANS_PREVIEW= rootPath + "preview" + extension;

        @Asset(TextureAtlas.class)
        public static final String LOGOS = rootPath + "logos" + extension;

        @Asset(TextureAtlas.class)
        public static final String ANIMATIONS = rootPath + "animations" + extension;

        @Asset(TextureAtlas.class)
        public static final String ANIMATIONS_BATTLE = rootPath + "battleAnimations" + extension;

        @Asset(TextureAtlas.class)
        public static final String ANIMATIONS_BIG = rootPath + "bigAnimations" + extension;

        @Asset(TextureAtlas.class)
        public static final String BATTLE_BG = rootPath + "battleBacks" + extension;

        @Asset(TextureAtlas.class)
        public static final String PARTICLES = rootPath + "particles" + extension;

        public static final String[] all = {
            HERO, GUARDIANS, GUARDIANS_MINI, GUARDIANS_PREVIEW, LOGOS, ANIMATIONS, ANIMATIONS_BATTLE,
            ANIMATIONS_BIG, BATTLE_BG, PARTICLES
        };
    }

    public static class Texture {
        private static final String rootPath = "textures/";

        public static final String MAIN_MENU_BG1 = rootPath + "GM_logo.png";
        public static final String MAIN_MENU_BG2 = rootPath + "main_logo_bg.png";
        public static final String[] WEATHER = {
            rootPath + "weather_clouds.png",
            rootPath + "weather_fog.png",
            rootPath + "weather_woods.png",
            rootPath + "weather_fog2.png"
        };

        public static final String[] all = {
            MAIN_MENU_BG1,
            MAIN_MENU_BG2,
            WEATHER[0],
            WEATHER[1],
            WEATHER[2],
            WEATHER[3]
        };
    }

    public static class Audio {
        public static class SFX {
            private static final String rootPath = "sfx/";

            public static final String METAMORPHOSIS = rootPath + "metamorphosis.ogg";

            public static Map<String, ArrayList<String>> BATTLE() {
                Map<String, ArrayList<String>> battleSFX = new HashMap<>();

                ArrayList<String> hits = new ArrayList<>();
                for(int i=1; i<=18; i++) hits.add(rootPath  + "hits/" + i + ".ogg");

                ArrayList<String> water = new ArrayList<>();
                for(int i=1;i<=1;i++) water.add(rootPath + "water/" + i + ".ogg");

                battleSFX.put("HIT", hits);
                battleSFX.put("WATER", water);

                return battleSFX;
            }

            public String BATTLE(String type, int index) {
                Map<String, ArrayList<String>> battleSFX = BATTLE();

                if(!battleSFX.containsKey(type) || battleSFX.get(type).size() <= index) {
                    return battleSFX.get("HIT").get(0);
                } else {
                    return battleSFX.get(type).get(index);
                }
            }

            public static ArrayList<String> all() {
                ArrayList<String> all = new ArrayList<>();
                for(ArrayList<String> al : BATTLE().values()) {
                    all.addAll(al);
                }
                all.add(METAMORPHOSIS);
                return all;
            }
        }

        public static class Music {
            private static final String rootPath = "music/";

            public static final String VICTORY_FANFARE = rootPath + "victory_fanfare.ogg";
            public static final String VICTORY_SONG = rootPath + "victory_song.ogg";
            public static final String METAMORPHOSIS = rootPath + "metamorphosis.ogg";
            public static final String GUARDOSPHERE = rootPath + "guardosphere.ogg";

            public static final String[] BG_TOWN = {
                rootPath + "town_loop_1.wav",
                rootPath + "town_loop_2.ogg"
            };

            public static final String[] BG_BATTLE = {
                rootPath + "battle_1.ogg"
            };

            public static final String[] all = {
                VICTORY_FANFARE,
                VICTORY_SONG,
                METAMORPHOSIS,
                GUARDOSPHERE,
                BG_TOWN[0],
                BG_TOWN[1],
                BG_BATTLE[0]
            };
        }
    }

    public static class I18N {
        private static final String rootPath = "l18n/";

        public static final String GENERAL      = rootPath + "general";
        public static final String GUARDIANS    = rootPath + "monsters";
        public static final String MAP_PREFIX   = rootPath + "map_";
        public static final String INVENTORY    = rootPath + "inventory";
        public static final String BATTLE       = rootPath + "battle";
        public static final String ATTACKS      = rootPath + "attacks";
        public static final String ELEMENTS     = rootPath + "elements";
    }

    public static class Skin {
        public static final String DEFAULT = "scene2d/defaultSkin";
        public static final String BATTLE = "scene2d/battleSkin";
        public static final String INVENTORY = "scene2d/inventorySkin";
        public static final String FONT = "fonts/PixelOperator-Bold.ttf";

        public static String attackButtonStyle(Element element) {
            return "tb-attack-" + element.toString().toLowerCase();
        }
    }
}
