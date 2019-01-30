package de.limbusdev.guardianmonsters.assets.paths

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.audio.Music as GdxMusic

import de.limbusdev.guardianmonsters.guardians.Element

import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset
import net.dermetfan.gdx.assets.AnnotationAssetManager

/**
 * Contains all asset paths. Uses [AnnotationAssetManager] to load them.
 */
object AssetPath
{
    object Spritesheet
    {
        private const val rootPath = "spritesheets"
        private const val extension = ".pack"

        @Asset(TextureAtlas::class) const val HERO                     = "$rootPath/hero$extension"
        @Asset(TextureAtlas::class) const val GUARDIANS                = "$rootPath/monsters$extension"
        @Asset(TextureAtlas::class) const val GUARDIANS_MINI           = "$rootPath/mini$extension"
        @Asset(TextureAtlas::class) const val GUARDIANS_PREVIEW        = "$rootPath/preview$extension"
        @Asset(TextureAtlas::class) const val LOGOS                    = "$rootPath/logos$extension"
        @Asset(TextureAtlas::class) const val ANIMATIONS               = "$rootPath/animations$extension"
        @Asset(TextureAtlas::class) const val ANIMATIONS_BATTLE        = "$rootPath/battleAnimations$extension"
        @Asset(TextureAtlas::class) const val ANIMATIONS_STATUS_EFFECT = "$rootPath/statusEffectAnimations$extension"
        @Asset(TextureAtlas::class) const val ANIMATIONS_SUMMON_BAN    = "$rootPath/animationsSummonBan$extension"
        @Asset(TextureAtlas::class) const val ANIMATIONS_BIG           = "$rootPath/bigAnimations$extension"
        @Asset(TextureAtlas::class) const val BATTLE_BG                = "$rootPath/battleBacks$extension"
        @Asset(TextureAtlas::class) const val PARTICLES                = "$rootPath/particles$extension"
        @Asset(TextureAtlas::class) const val ITEMS                    = "$rootPath/items$extension"
    }

    object Textures
    {
        private const val rootPath = "textures"

        val WEATHER = arrayOf(
                "$rootPath/weather_clouds.png",
                "$rootPath/weather_fog.png",
                "$rootPath/weather_woods.png",
                "$rootPath/weather_fog2.png")

        @Asset(Texture::class) const val MAIN_MENU_BG1 = "$rootPath/GM_logo.png"
        @Asset(Texture::class) const val MAIN_MENU_BG2 = "$rootPath/main_logo_bg.png"
        @Asset(Texture::class) val weather0 = WEATHER[0]
        @Asset(Texture::class) val weather1 = WEATHER[1]
        @Asset(Texture::class) val weather2 = WEATHER[2]
        @Asset(Texture::class) val weather3 = WEATHER[3]
    }

    object Audio
    {
        object SFX
        {
            private const val rootPath = "sfx"

            @Asset(Sound::class) val METAMORPHOSIS = "$rootPath/metamorphosis.ogg"
            @Asset(Sound::class) val battleSFXHit = Array(18) { "$rootPath/hits/${it+1}.ogg" }
            @Asset(Sound::class) val battleSFXWater = Array(1) { "$rootPath/water/${it+1}.ogg" }
            @Asset(Sound::class) val battleSFXSpell = Array(2) { "$rootPath/spell/${it+1}.ogg" }

            fun BATTLE() : Map<String, Array<String>> = mapOf(

                    "HIT"   to battleSFXHit,
                    "WATER" to battleSFXWater,
                    "SPELL" to battleSFXSpell
            )
        }

        object Music
        {
            private const val rootPath = "music"

            val BG_TOWN = arrayOf(

                    "$rootPath/town_loop_1.wav",
                    "$rootPath/town_loop_2.ogg"
            )

            val BG_BATTLE = arrayOf(

                    "$rootPath/battle_1.ogg"
            )

            @Asset(GdxMusic::class) const val VICTORY_FANFARE = "$rootPath/victory_fanfare.ogg"
            @Asset(GdxMusic::class) const val VICTORY_SONG = "$rootPath/victory_song.ogg"
            @Asset(GdxMusic::class) const val METAMORPHOSIS = "$rootPath/metamorphosis.ogg"
            @Asset(GdxMusic::class) const val GUARDOSPHERE = "$rootPath/guardosphere.ogg"
            @Asset(GdxMusic::class) val BG_TOWN_0 = BG_TOWN[0]
            @Asset(GdxMusic::class) val BG_TOWN_1 = BG_TOWN[1]
            @Asset(GdxMusic::class) val BG_BATTLE_0 = BG_BATTLE[0]
        }
    }

    object I18N
    {
        private const val rootPath = "l18n"

        const val GENERAL       = "$rootPath/general"
        const val GUARDIANS     = "$rootPath/monsters"
        const val MAP_PREFIX    = "$rootPath/map_"
        const val INVENTORY     = "$rootPath/inventory"
        const val BATTLE        = "$rootPath/battle"
        const val ATTACKS       = "$rootPath/attacks"
        const val ELEMENTS      = "$rootPath/elements"
    }

    object Skin
    {
        const val DEFAULT = "scene2d/defaultSkin"
        const val BATTLE = "scene2d/battleSkin"
        const val INVENTORY = "scene2d/inventorySkin"

        const val FONT = "fonts/PixelOperator-Bold.ttf"

        fun attackButtonStyle(element: Element) = "tb-attack-${element.toString().toLowerCase()}"
    }
}
