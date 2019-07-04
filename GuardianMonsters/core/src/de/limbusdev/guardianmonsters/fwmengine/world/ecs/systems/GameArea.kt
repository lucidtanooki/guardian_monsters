package de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.assets.paths.AssetPath
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent
import de.limbusdev.guardianmonsters.fwmengine.world.model.MapDescriptionInfo
import de.limbusdev.guardianmonsters.fwmengine.world.model.MapPersonInformation
import de.limbusdev.guardianmonsters.fwmengine.world.model.MonsterArea
import de.limbusdev.guardianmonsters.fwmengine.world.model.WarpPoint
import de.limbusdev.guardianmonsters.fwmengine.world.ui.ExtendedTiledMapRenderer
import de.limbusdev.guardianmonsters.fwmengine.world.ui.TmxDayTimeMapLoader
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.utils.extensions.removeLast
import de.limbusdev.utils.extensions.set
import de.limbusdev.utils.extensions.subStringFromEnd
import de.limbusdev.utils.geometry.IntRect
import de.limbusdev.utils.geometry.IntVec2

/**
 * Contains logic and information about one game world area like a forest or a path. One
 * OutDoorGameArea per Tiled Map.
 *
 * @author Georg Eckert 2015-11-21
 */
class GameArea(val areaID: Int, startPosID: Int)
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    val tiledMap    : TiledMap
    val mapRenderer : ExtendedTiledMapRenderer

    var gridPosition    = IntVec2(0, 0)
    var startPosition   = PositionComponent(0, 0, 0, 0, 0)


    private var bgMusic: String? = null

    val colliders       = ArrayMap<Int, Array<IntRect>>()
    val dynamicColliders = ArrayMap<Int, Array<IntRect>>()
    val warpPoints      = ArrayMap<Int, Array<WarpPoint>>()
    val healFields      = ArrayMap<Int, Array<Rectangle>>()
    val mapPeople       = ArrayMap<Int, Array<MapPersonInformation>>()
    val descriptions    = ArrayMap<Int, Array<MapDescriptionInfo>>()
    val monsterAreas    = ArrayMap<Int, Array<MonsterArea>>()


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        tiledMap = setUpTiledMap(areaID, startPosID)
        mapRenderer = ExtendedTiledMapRenderer(tiledMap)
    }


    // --------------------------------------------------------------------------------------------- METHODS
    fun render(camera: OrthographicCamera)
    {
        mapRenderer.setView(camera)
        mapRenderer.render()
    }

    fun update(delta: Float)
    {
        // TODO
    }

    /**
     * Renders all colliders as white shapes
     * @param shape
     */
    fun renderDebugging(shape: ShapeRenderer)
    {
        shape.begin(ShapeRenderer.ShapeType.Line)
        shape.color = Color.WHITE

        for (a in colliders.values())
        {
            for (r in a)
            {
                shape.rect(r.xf, r.yf, r.widthf, r.heightf)
            }
        }

        shape.end()
    }

    fun setUpTiledMap(areaID: Int, startFieldID: Int): TiledMap
    {
        val tiledMap = TmxDayTimeMapLoader().load("tilemaps/$areaID.tmx")

        for (layer in tiledMap.layers)
        {
            when(layer.name.removeLast(0))
            {
                "people"        -> createPeople(layer)
                "colliderWalls" -> createColliders(layer)
                "descriptions"  -> createDescriptions(layer)
                "triggers"      -> createTriggers(layer, startFieldID)
            }
        }

        createBorderColliders(tiledMap)

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
    private fun createTriggers(layer: MapLayer, startFieldID: Int)
    {
        val layerIndex = layer.name.subStringFromEnd(0).toInt()

        val healingTriggers = Array<Rectangle>()
        healFields.put(layerIndex, healingTriggers)

        val warpTriggers = Array<WarpPoint>()
        warpPoints.put(layerIndex, warpTriggers)

        val battleTriggers = Array<MonsterArea>()
        monsterAreas.put(layerIndex, battleTriggers)

        for (mo in layer.objects)
        {
            val rect = (mo as RectangleMapObject).rectangle
            when(mo.name)
            {
                "healing" ->
                {
                    healingTriggers.add(Rectangle(
                            rect.x,
                            rect.y,
                            Constant.COLf,
                            Constant.ROWf))
                }
                "warpField" ->
                {
                    warpTriggers.add(WarpPoint(
                            mo.properties["targetWarpPointID", 0],
                            rect,
                            mo.properties["targetID", 0]))
                }
                "startField" ->
                {
                    if (mo.properties["fieldID", 0] == startFieldID)
                    {
                        startPosition.x = MathUtils.round(rect.x)
                        startPosition.y = MathUtils.round(rect.y)
                        startPosition.layer = layerIndex
                    }
                }
                "monsterArea" ->
                {
                    val r = IntRect(rect)
                    val ap = Array<Float>()
                    ap.add(mo.properties["probability",  .5f])
                    ap.add(mo.properties["probability2", .3f])
                    ap.add(mo.properties["probability3", .2f])
                    battleTriggers.add(MonsterArea(

                            r.x, r.y, r.width, r.height,
                            mo.properties["monsters", "1;0.90;2;0.05;3;0.05"],
                            ap
                    ))
                }
                else -> {}
            }
        }
    }

    private fun createDescriptions(layer: MapLayer)
    {
        val descriptionLayer = Array<MapDescriptionInfo>()
        val layerIndex = layer.name.subStringFromEnd(0).toInt()
        descriptions[layerIndex] = descriptionLayer

        // get information about signs on map
        for (mo in layer.objects)
        {
            when(mo.name)
            {
                "sign"  -> descriptionLayer.add(MapDescriptionInfo(mo))
                else    -> {}
            }
        }
    }

    /**
     * Only for Layers containing "people" in their nameID
     *
     * @param layer
     */
    private fun createPeople(layer: MapLayer)
    {
        val peopleLayer = Array<MapPersonInformation>()
        val layerIndex = layer.name.subStringFromEnd(0).toInt()
        mapPeople[layerIndex] = peopleLayer

        // get information about people on map
        for (mo in layer.objects)
        {
            peopleLayer.add(MapPersonInformation(mo))
        }
    }

    /**
     * Only for layers containing "colliderWalls" in their nameID
     * Creates rectangle objects for every collider in the given layer
     *
     * @param layer
     */
    private fun createColliders(layer: MapLayer)
    {
        val colliderLayer = Array<IntRect>()
        val layerIndex = layer.name.subStringFromEnd(0).toInt()
        colliders[layerIndex] = colliderLayer

        for (mo in layer.objects)
        {
            val r = (mo as RectangleMapObject).rectangle
            colliderLayer.add(IntRect(r.x, r.y, r.width, r.height))
        }
    }

    /**
     * Creates a wall of colliders right around the active map so character can't just walk out
     *
     * @param tiledMap
     */
    private fun createBorderColliders(tiledMap: TiledMap)
    {
        val mapWidth    = tiledMap.properties["width", 10]
        val mapHeight   = tiledMap.properties["height", 10]

        for (i in colliders.keys())
        {
            val layerColliders = colliders[i]
            layerColliders.add(IntRect(-1 * Constant.TILE_SIZE, -1 * Constant.TILE_SIZE, (mapWidth + 2) * Constant.TILE_SIZE, Constant.TILE_SIZE))
            layerColliders.add(IntRect(-1 * Constant.TILE_SIZE, mapHeight * Constant.TILE_SIZE, (mapWidth + 2) * Constant.TILE_SIZE, Constant.TILE_SIZE))
            layerColliders.add(IntRect(-1 * Constant.TILE_SIZE, 0, Constant.TILE_SIZE, mapHeight * Constant.TILE_SIZE))
            layerColliders.add(IntRect(mapWidth * Constant.TILE_SIZE, 0, Constant.TILE_SIZE, mapHeight * Constant.TILE_SIZE))
        }
    }

    fun dispose()
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

    fun addDynamicCollider(collider: IntRect, layer: Int)
    {
        if (!dynamicColliders.containsKey(layer))
        {
            dynamicColliders.put(layer, Array())
        }
        dynamicColliders.get(layer).add(collider)
    }

    fun removeDynamicCollider(collider: IntRect, layer: Int)
    {
        if (dynamicColliders.containsKey(layer))
        {
            dynamicColliders.get(layer).removeValue(collider, false)
        }
    }
}