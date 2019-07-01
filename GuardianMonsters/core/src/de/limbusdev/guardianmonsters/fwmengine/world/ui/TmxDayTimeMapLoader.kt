package de.limbusdev.guardianmonsters.fwmengine.world.ui

import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.maps.ImageResolver
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.TimeUtils
import com.badlogic.gdx.utils.XmlReader

import java.io.IOException
import java.util.Calendar
import java.util.Date

/**
 * @author Georg Eckert 2017-02-12
 */

class TmxDayTimeMapLoader : TmxMapLoader()
{
    /**
     * Loads the [TiledMap] from the given file. The file is resolved via the [FileHandleResolver]
     * set in the constructor of this class. By default it will resolve to an internal file.
     *
     * @param fileName the filename
     * @param parameters specifies whether to use y-up, generate mip maps etc.
     * @return the TiledMap
     */
    override fun load(fileName: String, parameters: Parameters): TiledMap
    {
        try
        {
            convertObjectToTileSpace = parameters.convertObjectToTileSpace
            flipY = parameters.flipY
            val tmxFile = resolve(fileName)
            root = xml.parse(tmxFile)
            root = manipulateSpritesheetPaths(root)
            val textures = ObjectMap<String, Texture>()
            val textureFiles = loadTilesets(root, tmxFile)
            textureFiles.addAll(loadImages(root, tmxFile))

            for (textureFile in textureFiles)
            {
                val texture = Texture(textureFile, parameters.generateMipMaps)
                texture.setFilter(parameters.textureMinFilter, parameters.textureMagFilter)
                textures.put(textureFile.path(), texture)
            }

            val imageResolver = ImageResolver.DirectImageResolver(textures)
            val map = loadTilemap(root, tmxFile, imageResolver)
            map.setOwnedResources(textures.values().toArray())
            return map
        }
        catch (e: IOException)
        {
            throw GdxRuntimeException("Couldn't load tilemap '$fileName'", e)
        }
    }


    @Throws(IOException::class)
    private fun manipulateSpritesheetPaths(root: XmlReader.Element): XmlReader.Element
    {
        for (tileset: XmlReader.Element in root.getChildrenByName("tileset"))
        {
            val source = tileset.getAttribute("source", null)
            if (source != null)
            {
                val newSource = manipulatePath(source)
                tileset.setAttribute("source", newSource)
            }
            else
            {
                val imageElement = tileset.getChildByName("image")
                if (imageElement != null)
                {
                    val imageSource = tileset.getChildByName("image").getAttribute("source")
                    tileset.getChildByName("image").setAttribute("source", manipulatePath(imageSource))
                }
                else
                {
                    for (tile: XmlReader.Element in tileset.getChildrenByName("tile"))
                    {
                        val imageSource = tile.getChildByName("image").getAttribute("source")
                        tileset.getChildByName("image").setAttribute("source", manipulatePath(imageSource))
                    }
                }
            }
        }
        return root
    }

    private fun manipulatePath(source: String): String
    {
        // No need to manipulate path for inner maps (rooms, houses) - same lighting the whole day
        if (source.contains("interior")) { return source }

        val daytime = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
        {
            in 0..5, in 21..24 -> EDayTime.NIGHT
            in 6..9            -> EDayTime.MORNING
            in 18..20          -> EDayTime.AFTERNOON
            else               -> EDayTime.DAY
        }

        val daytimeString: String = when (daytime)
        {
            EDayTime.MORNING   -> "morning"
            EDayTime.DAY       -> "day"
            EDayTime.AFTERNOON -> "afternoon"
            EDayTime.NIGHT     -> "night"
        }

        return source.substring(0, source.length - 4) + "_$daytimeString.png"
    }

    private enum class EDayTime { DAY, AFTERNOON, NIGHT, MORNING }
}
