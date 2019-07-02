package de.limbusdev.guardianmonsters.fwmengine.world.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.MapProperties
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Sort

import de.limbusdev.guardianmonsters.media.IMediaManager
import de.limbusdev.guardianmonsters.scene2d.SpriteZComparator
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.utils.extensions.removeLast
import de.limbusdev.utils.extensions.set
import de.limbusdev.utils.log
import de.limbusdev.utils.logDebug
import de.limbusdev.utils.logError
import ktx.ashley.mapperFor
import ktx.style.defaultStyle

/**
 * Renderer for *.tmx files. This renderer renders map files of the FWM-Engine.
 *
 * Map Structure: Documents/TiledMapStructure.md
 *
 * @author Georg Eckert 2017
 *
 * Orthogonal Tiled Map Renderer for tiled maps with entities and other objects. For structure
 * description see Documents/TiledMapStructure.md
 *
 *
 * ## Layer Names
 *
 * Layer names follow the pattern: `<layerType><ID>`, for example `people1`
 */
class ExtendedTiledMapRenderer(map: TiledMap) : OrthogonalTiledMapRenderer(map, 1f)
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    companion object { const val TAG = "ExtendedTiledMapRenderer" }

    private val sprites = Array<AnimatedPersonSprite>()
    private val objectAnimations = ArrayMap<String, Animation<TextureRegion>>()
    private val tileAnimations = ArrayMap<Int, Animation<TextureRegion>>()
    private var elapsedTime: Float = 0.toFloat()
    private val weatherAnimator: WeatherAnimator


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        this.elapsedTime = 0f
        this.weatherAnimator = WeatherAnimator(map)

        for (layer in map.layers)
        {
            when(layer.name.removeLast(0))
            {
                "animatedObjects" -> setUpObjectAnimations(layer)
                "animatedTiles"   -> setUpTileAnimations(layer)
            }
        }
    }

    // --------------------------------------------------------------------------------------------- METHODS
    override fun render()
    {
        // Elapsed Time for Animations
        elapsedTime += Gdx.graphics.deltaTime

        // Sprite Z-Sorting
        sortSpritesByDepth()

        beginRender()

        for (layer in map.layers)
        {
            when(layer)
            {
                is TiledMapTileLayer -> renderTileLayer(layer) // Render graphical layer
                else ->
                {
                    // Handle object layer
                    layer.objects.forEach { renderObject(it) }

                    when(layer.name.removeLast(0))
                    {

                        "people" -> renderPeople()
                        else -> {}
                    }
                }
            }
        }

        // Render Weather Effects
        weatherAnimator.render(batch, elapsedTime)

        endRender()
    }

    private fun renderPeople()
    {
        // Draw entity sprites if visible
        sprites.forEach { it.updateAndDrawIfVisible(elapsedTime, batch) }
    }

    override fun renderObject(mapObject: MapObject)
    {
        when(mapObject.name)
        {
            "animatedObject" -> renderAnimatedObject(mapObject)
            null             -> {} // Don't render objects with empty nameID
            else             -> {}
        }

        when(mapObject.properties["type", "unknown"])
        {
            "animatedTile" -> renderAnimatedTile(mapObject)
            else           -> {}
        }
    }

    // ........................................................................... Getters & Setters
    fun addEntitySprite(animatedSprite: AnimatedPersonSprite) = sprites.add(animatedSprite)

    /**
     * Handles layers containing "animatedTiles" in their nameID and loads the needed animations
     * @param mapLayer  animatedTiles at number layer
     */
    private fun setUpTileAnimations(mapLayer: MapLayer)
    {
        try
        {
            logDebug(TAG) { "Setting up tile animation for layer: ${mapLayer.name}" }

            for (mapObject in mapLayer.objects)
            {
                val index: Int = mapObject.properties["index", 0]

                // If this tile animation is not setup already
                if (!tileAnimations.containsKey(index))
                {
                    val a = Services.Media().getTileAnimation(index)
                    a.frameDuration = mapObject.properties["frameDuration", "1.0"].toFloat()
                    tileAnimations[index] = a
                }
            }
        }
        catch (e: Exception)
        {
            logError(TAG) { "No Animated Tiles Layer available" }
            e.printStackTrace()
        }
    }

    /**
     * Handles layers containing "animatedObjects" in their nameID and loads the needed animations
     * @param mapLayer  animatedObjects at number layer
     */
    private fun setUpObjectAnimations(mapLayer: MapLayer)
    {
        try
        {
            logDebug(TAG) { "Setting up object animation for layer: ${mapLayer.name}" }

            mapLayer.objects.forEach { mapObject ->

                val id = mapObject.properties["index", "0"]

                // If this object animation is not setup already
                if (!objectAnimations.containsKey(id))
                {
                    val a = Services.Media().getObjectAnimation(id)
                    a.frameDuration = mapObject.properties["frameDuration", "1.0"].toFloat()
                    objectAnimations[id] = a
                }
            }

        } catch (e: Exception)
        {
            logError(TAG) { "No Animated Objects Layer available" }
            e.printStackTrace()
        }
    }


    // .............................................................................. RENDER METHODS
    private fun renderAnimatedTile(o: MapObject)
    {
        val r = o as RectangleMapObject
        val index: Int = o.getProperties()["index", 0]
        val a = tileAnimations.get(index)

        // Render multiple tiles
        val cols: Int
        val rows: Int
        val objWidth: Int
        val objHeight: Int
        objWidth = MathUtils.round(r.rectangle.getWidth())
        objHeight = MathUtils.round(r.rectangle.getHeight())
        cols = objWidth / 16
        rows = objHeight / 16

        for (i in 0 until rows)
        {
            for (j in 0 until cols)
            {
                val x = r.rectangle.getX() + j * 16
                val y = r.rectangle.getY() + i * 16
                if(a != null)renderAnimation(a, x, y)
            }
        }
    }

    private fun renderAnimatedObject(o: MapObject)
    {
        o as RectangleMapObject

        val a = objectAnimations[o.properties["index", "0"]]
        renderAnimation(a, o.rectangle.getX(), o.rectangle.getY())
    }

    /**
     * Draws the given animation at the position given by the rectangles corner
     * @param anim
     * @param x
     * @param y
     */
    private fun renderAnimation(anim: Animation<TextureRegion>, x: Float, y: Float)
    {
        try
        {
            this.batch.draw(anim.getKeyFrame(elapsedTime), x, y)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    /** Sorts sprites, so people in the background are drawn behind those in the front */
    private fun sortSpritesByDepth()
    {
        Sort.instance().sort(sprites, SpriteZComparator())
    }
}

inline operator fun <reified Resource : Any> MapProperties.get(key: String, default: Resource): Resource
{
    return this.get(key, Resource::class.java) ?: default
}