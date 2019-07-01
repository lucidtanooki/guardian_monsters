package de.limbusdev.guardianmonsters.fwmengine.world.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.Sort

import de.limbusdev.guardianmonsters.media.IMediaManager
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.utils.logDebug
import de.limbusdev.utils.logError

/**
 * Renderer for *.tmx files. This renderer renders map files of the FWM-Engine.
 *
 * Map Structure: Documents/TiledMapStructure.md
 *
 * @author Georg Eckert 2017
 *
 * Orthogonal Tiled Map Renderer for tiled maps with entities and other objects. For structure
 * description see Documents/TiledMapStructure.md
 */
class ExtendedTiledMapRenderer(map: TiledMap) : OrthogonalTiledMapRenderer(map, 1f)
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    companion object { const val TAG = "ExtendedTiledMapRenderer" }

    private val sprites = Array<AnimatedPersonSprite>()
    private val objectAnimations = ArrayMap<String, Animation<*>>()
    private val tileAnimations = ArrayMap<Int, Animation<*>>()
    private var elapsedTime: Float = 0.toFloat()
    private val weatherAnimator: WeatherAnimator


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        this.elapsedTime = 0f
        this.weatherAnimator = WeatherAnimator(map)

        for (layer in map.layers)
        {
            if (layer.name.contains("animatedObjects"))
            {
                setUpObjectAnimations(layer)
            }
            if (layer.name.contains("animatedTiles"))
            {
                setUpTileAnimations(layer)
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

                    if (layer.name.contains("people"))
                    {
                        // Draw entity sprites if visible
                        sprites.forEach { sprite ->

                            if (sprite.visible) {
                                sprite.update(elapsedTime)
                                sprite.draw(batch)
                            }
                        }
                    }
                }
            }
        }

        // Render Weather Effects
        weatherAnimator.render(batch, elapsedTime)

        endRender()
    }

    override fun renderObject(mapObject: MapObject)
    {
        val objName = mapObject.name
        // Don't render objects with empty nameID
        if (objName != null && objName == "animatedObject")
        {
            renderAnimatedObject(mapObject)
        }

        val objType = mapObject.properties.get("type", String::class.java)
        if (objType != null && objType == "animatedTile")
        {
            renderAnimatedTile(mapObject)
        }
    }

    // ........................................................................... Getters & Setters
    fun addEntitySprite(animatedSprite: AnimatedPersonSprite) = sprites.add(animatedSprite)

    /**
     * Handles layers containing "animatedTiles" in their nameID and loads the needed animations
     * @param mapLayer  animatedTiles at number layer
     */
    fun setUpTileAnimations(mapLayer: MapLayer)
    {
        try
        {
            logDebug(TAG) { "Setting up tile animation for layer: ${mapLayer.name}" }

            for (mo in mapLayer.objects)
            {
                val index = mo.properties.get("index", String::class.java)
                val id = Integer.parseInt(index)
                if (!tileAnimations.containsKey(id))
                {
                    val a = Services.Media().getTileAnimation(id)
                    if (mo.properties.containsKey("frameDuration"))
                    {
                        val frmDur = mo.properties.get("frameDuration", String::class.java)
                        a.frameDuration = frmDur.toFloat()
                    }
                    tileAnimations.put(id, a)
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
    fun setUpObjectAnimations(mapLayer: MapLayer)
    {
        try
        {
            logDebug(TAG) { "Setting up object animation for layer: ${mapLayer.name}" }

            mapLayer.objects.forEach { mapObject ->

                val id = mapObject.properties.get("index", String::class.java)
                if (!objectAnimations.containsKey(id))
                {
                    val a = Services.Media().getObjectAnimation(id)
                    if (mapObject.properties.containsKey("frameDuration"))
                    {
                        val frmDur = mapObject.properties.get("frameDuration", String::class.java)
                        a.frameDuration = frmDur.toFloat()
                    }
                    objectAnimations.put(id, a)
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
        val index = Integer.parseInt(o.getProperties().get("index", String::class.java))
        val a = tileAnimations.get(index) as Animation<TextureRegion>

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
                renderAnimation(a, x, y)
            }
        }
    }

    private fun renderAnimatedObject(o: MapObject)
    {
        val r = o as RectangleMapObject
        val a = objectAnimations.get(o.getProperties().get("index", String::class.java)) as Animation<TextureRegion>
        renderAnimation(a, r.rectangle.getX(), r.rectangle.getY())
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
        Sort.instance().sort(sprites, de.limbusdev.guardianmonsters.scene2d.SpriteZComparator())
    }
}
