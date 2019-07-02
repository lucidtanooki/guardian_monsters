package de.limbusdev.guardianmonsters.fwmengine.world.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array

import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.assets.paths.AssetPath
import de.limbusdev.guardianmonsters.media.IMediaManager
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.utils.geometry.IntVec2

/**
 * @author Georg Eckert 2017
 */

class WeatherAnimator(map: TiledMap)
{
    private interface WeatherRenderer { fun render(batch: Batch, elapsedTime: Float) }

    // --------------------------------------------------------------------------------------------- PROPERTIES
    private var renderer    : WeatherRenderer? = null
    private val weatherOn   : Boolean = map.properties.containsKey("weather")


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        if (weatherOn)
        {
            renderer = when (map.properties["weather", "clouds"])
            {
                "clouds" -> CloudRenderer(map)
                "fog"    -> FogRenderer(map, 0)
                "woods"  -> WoodsRenderer(map)
                "rain"   -> RainRenderer(map)
                else     -> null
            }
        }
    }


    // --------------------------------------------------------------------------------------------- METHODS
    /**
     * Renders the weather effect above a tiled map
     * @param batch [Batch] of the [ExtendedTiledMapRenderer]
     */
    fun render(batch: Batch, elapsedTime: Float)
    {
        if (weatherOn) { renderer?.render(batch, elapsedTime) }
    }


    // ............................................................................... INNER CLASSES
    private abstract inner class AWeatherRenderer(map: TiledMap) : WeatherRenderer
    {
        protected var width : Int = map.properties["width", 10]
        protected var height: Int = map.properties["height", 10]
    }

    private inner class RainRenderer(map: TiledMap) : AWeatherRenderer(map)
    {
        private val rainAnimation           : Animation<TextureRegion>
        private val fogRenderer             : FogRenderer

        private val lastFrameIndex          = 8
        private val randomRaindropOffset    = Array<Float>()
        private val randomRaindropPosition  = Array<IntVec2>()

        init
        {
            fogRenderer = FogRenderer(map, 1)
            rainAnimation = Services.Media().getObjectAnimation("rain0")
            rainAnimation.frameDuration = 0.2f

            for (n in 0 until width * height / 2)
            {
                randomRaindropOffset.add(MathUtils.random(3f))
                val dropPos = IntVec2(0, 0)
                randomRaindropPosition.add(setRandomDropPosition(dropPos))
            }
        }

        private fun setRandomDropPosition(dropPos: IntVec2): IntVec2
        {
            dropPos.x = MathUtils.random(width) * Constant.TILE_SIZE + MathUtils.random(-8, 7)
            dropPos.y = MathUtils.random(height) * Constant.TILE_SIZE + MathUtils.random(-8, 7)
            return dropPos
        }

        override fun render(batch: Batch, elapsedTime: Float)
        {
            fogRenderer.render(batch, elapsedTime)
            for (n in 0 until randomRaindropPosition.size)
            {
                val r = rainAnimation.getKeyFrame(elapsedTime + randomRaindropOffset.get(n), true)
                val pos = randomRaindropPosition.get(n)
                batch.draw(r, pos.xf, pos.yf)
                if (rainAnimation.getKeyFrameIndex(elapsedTime + randomRaindropOffset.get(n)) == lastFrameIndex)
                {
                    setRandomDropPosition(pos)
                }
            }
        }
    }

    private inner class FogRenderer(map: TiledMap, fogIndex: Int) : AWeatherRenderer(map)
    {
        private val fogTexture: Texture = when (fogIndex)
        {
            1    -> Services.Media().getTexture(AssetPath.Textures.WEATHER[3])
            else -> Services.Media().getTexture(AssetPath.Textures.WEATHER[1])
        }

        override fun render(batch: Batch, elapsedTime: Float)
        {
            batch.draw(fogTexture, 0f, 0f, super.width * Constant.TILE_SIZEf, super.height * Constant.TILE_SIZEf)
        }
    }

    private inner class WoodsRenderer(map: TiledMap) : AWeatherRenderer(map)
    {
        private val woodTexture: Texture = Services.Media().getTexture(AssetPath.Textures.WEATHER[2])

        override fun render(batch: Batch, elapsedTime: Float)
        {
            batch.draw(woodTexture, 0f, 0f, Constant.WIDTHf, Constant.HEIGHTf)
        }
    }

    private inner class CloudRenderer(map: TiledMap) : AWeatherRenderer(map)
    {
        private val cloudTexture: Texture = Services.Media().getTexture(AssetPath.Textures.WEATHER[0])
        private val weatherTiles = Array<Vector2>()

        init
        {
            for (i in -1 until width * 16 / 640 + 1)
            {
                for (j in -1 until height * 16 / 480 + 1)
                {
                    weatherTiles.add(Vector2(i * 640f, j * 480f))
                }
            }
        }

        override fun render(batch: Batch, elapsedTime: Float)
        {
            // Weather effect available, render
            for (iv in weatherTiles)
            {
                iv.x += Gdx.graphics.deltaTime * 10.0f
                if (iv.x >= (width * 16f / 640 + 1) * 640) { iv.x = -640f }
                batch.draw(cloudTexture, iv.x, iv.y)
            }
        }
    }
}
