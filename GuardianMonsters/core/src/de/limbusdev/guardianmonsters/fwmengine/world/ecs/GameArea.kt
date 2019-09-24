package de.limbusdev.guardianmonsters.fwmengine.world.ecs

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.objects.TextureMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json

import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.CoreSL
import de.limbusdev.guardianmonsters.assets.paths.AssetPath
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.*
import de.limbusdev.guardianmonsters.fwmengine.world.ui.ExtendedTiledMapRenderer
import de.limbusdev.guardianmonsters.fwmengine.world.ui.TmxDayTimeMapLoader
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.utils.extensions.removeLast
import de.limbusdev.utils.extensions.subStringFromEnd
import de.limbusdev.utils.geometry.IntRect
import de.limbusdev.utils.geometry.IntVec2
import java.lang.Exception
import kotlin.reflect.KClass

/**
 * Contains logic and information about one game world area like a forest or a path. One
 * OutDoorGameArea per Tiled Map.
 *
 * @author Georg Eckert 2015-11-21
 */
class GameArea(val areaID: Int, startPosID: Int) : RenderingLimbusBehaviour()
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    companion object
    {
        const val defaultComponentPackage = "de.limbusdev.guardianmonsters.fwmengine.world.ecs.components."
    }

    val tiledMap    : TiledMap
    private val mapRenderer : ExtendedTiledMapRenderer
    private val shape = ShapeRenderer()

    var startPosition   = IntVec2(0, 0)
    var startLayer = 0

    private var bgMusic: String? = null


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        tiledMap = setUpTiledMap(areaID, startPosID)
        mapRenderer = ExtendedTiledMapRenderer(tiledMap)
        for(warpTargetField in CoreSL.world.getAllWith(WarpTargetComponent::class))
        {
            val warpTargetComponent = warpTargetField.get<WarpTargetComponent>()
            if(warpTargetComponent != null && warpTargetComponent.warpTargetID == startPosID)
            {
                val transform = warpTargetComponent.gameObject.transform
                startPosition.x = transform.x
                startPosition.y = transform.y
                startLayer = transform.layer
            }
        }
    }


    // --------------------------------------------------------------------------------------------- METHODS
    override fun render()
    {
        shape.projectionMatrix = CoreSL.world.mainCamera.combined

        mapRenderer.setView(CoreSL.world.mainCamera)
        mapRenderer.render()
        if (Constant.DEBUGGING_ON) renderDebugging()
    }


    override fun update(deltaTime: Float)
    {

    }

    /** Renders all colliders as white shapes */
    private fun renderDebugging()
    {
        shape.begin(ShapeRenderer.ShapeType.Line)
        shape.color = Color.WHITE

        for(collidingObject in CoreSL.world.getAllWith(ColliderComponent::class))
        {
            val transform = collidingObject.transform
            shape.color = Color.ORANGE
            shape.rect(transform.xf, transform.yf, transform.widthf, transform.heightf)

            shape.color = Color.WHITE
            collidingObject.get<ColliderComponent>()?.asRectangle?.let {

                shape.rect(it.xf, it.yf, it.widthf, it.heightf)
            }
        }

        shape.end()
    }

    private fun setUpTiledMap(areaID: Int, startFieldID: Int): TiledMap
    {
        val tiledMap = TmxDayTimeMapLoader().load("tilemaps/$areaID.tmx")

        for (layer in tiledMap.layers)
        {
            val layerID: Int = layer.name[layer.name.lastIndex].toString().toInt()
            when(layer.name.removeLast(0))
            {
                "descriptions"  -> createGameObjects(layer, layerID)
                "people"        -> createGameObjects(layer, layerID)
                "colliderWalls" ->
                {
                    createGameObjects(layer, layerID)
                    createBorderColliders(tiledMap, layerID)
                }
                "triggers"      -> createGameObjects(layer, layerID)
                "gameObjects"   -> createGameObjects(layer, layerID)
            }
        }

        // Set background music
        val musicType = tiledMap.properties["musicType", "town"]
        val musicIndex = tiledMap.properties["musicIndex", 0]

        when(musicType)
        {
            "town" -> bgMusic = AssetPath.Audio.Music.BG_TOWN[musicIndex-1]
            else -> {}
        }

        return tiledMap
    }


    // ......................................................................... Map Object Creation
    /**
     * Takes each MapObject from a layer and creates a proper LimbusGameObject from it.
     * Only for layers with game objects composed of JSON-components.
     */
    private fun createGameObjects(layer: MapLayer, layerID: Int)
    {
        val json = Json()
        for(mapObject in layer.objects)
        {
            // Only Rectangle Map Objects are supported
            if(mapObject is RectangleMapObject || mapObject is TextureMapObject)
            {
                val gameObject = LimbusGameObject(mapObject.name ?: "")

                when(mapObject.properties["enabled", true])
                {
                    true -> gameObject.enable()
                    false -> gameObject.disable()
                }

                // Transform
                val transform = gameObject.transform
                when (mapObject)
                {
                    is RectangleMapObject ->
                    {
                        transform.x = MathUtils.round(mapObject.rectangle.x)
                        transform.y = MathUtils.round(mapObject.rectangle.y)
                        transform.width = MathUtils.round(mapObject.rectangle.width)
                        transform.height = MathUtils.round(mapObject.rectangle.height)
                    }
                    is TextureMapObject ->
                    {
                        transform.x = MathUtils.round(mapObject.x)
                        transform.y = MathUtils.round(mapObject.y)
                        transform.width = mapObject.textureRegion.regionWidth
                        transform.height = mapObject.textureRegion.regionHeight
                    }
                }
                transform.layer = layerID


                // .......................................................................... Components
                for (key in mapObject.properties.keys)
                {
                    val component = generateComponent(mapObject, key, json, gameObject)
                    if (component != null) { gameObject.add(component) }
                }

                gameObject.addAndRemoveComponentsNow()
                CoreSL.world.add(gameObject)
            }
        }

        CoreSL.world.addAndRemoveObjectsNow()
    }

    private fun generateComponent(mapObject: MapObject, componentName: String, json: Json, core: LimbusGameObject) : LimbusBehaviour?
    {
        if(!componentName.contains("Component", false)) { return null }

        var component: LimbusBehaviour? = null

        try
        {
            // Components that are part of the engine can be used with simple names
            // Custom components must use their full name, like: com.me.CustomComponent
            val componentClassBasePath = when (componentName.contains("."))
            {
                true  -> ""
                false -> defaultComponentPackage
            }
            val kClass = Class.forName(componentClassBasePath + componentName).kotlin

            kClass as KClass<out LimbusBehaviour>

            component = Components.parsers[kClass]?.parseComponent(json, mapObject)
        }
        catch (e: Exception)
        {
            println("Cast not successful for $componentName.")
        }

        return component
    }

    /**
     * Creates a wall of colliders right around the active map so character can't just walk out
     *
     * @param tiledMap
     */
    private fun createBorderColliders(tiledMap: TiledMap, layerID: Int)
    {
        val mapWidth    = tiledMap.properties["width", 10]
        val mapHeight   = tiledMap.properties["height", 10]

        var borderGameObject = LimbusGameObject("BorderBottom")
        borderGameObject.transform.x = -1 * Constant.TILE_SIZE
        borderGameObject.transform.y = -1 * Constant.TILE_SIZE
        borderGameObject.add(ColliderComponent(width = (mapWidth + 2) * Constant.TILE_SIZE, height = Constant.TILE_SIZE))
        borderGameObject.transform.layer = layerID
        CoreSL.world.add(borderGameObject)

        borderGameObject = LimbusGameObject("BorderTop")
        borderGameObject.transform.x = -1 * Constant.TILE_SIZE
        borderGameObject.transform.y = mapHeight * Constant.TILE_SIZE
        borderGameObject.add(ColliderComponent(width = (mapWidth + 2) * Constant.TILE_SIZE, height = Constant.TILE_SIZE))
        borderGameObject.transform.layer = layerID
        CoreSL.world.add(borderGameObject)

        borderGameObject = LimbusGameObject("BorderLeft")
        borderGameObject.transform.x = -1 * Constant.TILE_SIZE
        borderGameObject.transform.y = 0
        borderGameObject.add(ColliderComponent(width = Constant.TILE_SIZE, height = (mapHeight + 2) * Constant.TILE_SIZE))
        borderGameObject.transform.layer = layerID
        CoreSL.world.add(borderGameObject)

        borderGameObject = LimbusGameObject("BorderRight")
        borderGameObject.transform.x = mapWidth * Constant.TILE_SIZE
        borderGameObject.transform.y = 0
        borderGameObject.add(ColliderComponent(width = Constant.TILE_SIZE, height = (mapHeight + 2) * Constant.TILE_SIZE))
        borderGameObject.transform.layer = layerID
        CoreSL.world.add(borderGameObject)
    }

    override fun dispose()
    {
        // TODO
    }

    fun playMusic()
    {
        Services.Audio().playLoopMusic(bgMusic)
    }

    fun stopMusic()
    {
        Services.Audio().stopMusic(bgMusic)
    }
}
