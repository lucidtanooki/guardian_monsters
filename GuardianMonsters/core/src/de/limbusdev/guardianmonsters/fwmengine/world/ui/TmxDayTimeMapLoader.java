package de.limbusdev.guardianmonsters.fwmengine.world.ui;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.ImageResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by georg on 12.02.17.
 */

public class TmxDayTimeMapLoader extends TmxMapLoader {



    /** Loads the {@link TiledMap} from the given file. The file is resolved via the {@link FileHandleResolver} set in the
     * constructor of this class. By default it will resolve to an internal file.
     * @param fileName the filename
     * @param parameters specifies whether to use y-up, generate mip maps etc.
     * @return the TiledMap */
    @Override
    public TiledMap load (String fileName, TmxMapLoader.Parameters parameters) {
        try {
            this.convertObjectToTileSpace = parameters.convertObjectToTileSpace;
            this.flipY = parameters.flipY;
            FileHandle tmxFile = resolve(fileName);
            root = xml.parse(tmxFile);
            root = manipulateSpritesheetPaths(root);
            ObjectMap<String, Texture> textures = new ObjectMap<String, Texture>();
            Array<FileHandle> textureFiles = loadTilesets(root, tmxFile);
            textureFiles.addAll(loadImages(root, tmxFile));

            for (FileHandle textureFile : textureFiles) {
                Texture texture = new Texture(textureFile, parameters.generateMipMaps);
                texture.setFilter(parameters.textureMinFilter, parameters.textureMagFilter);
                textures.put(textureFile.path(), texture);
            }

            ImageResolver.DirectImageResolver imageResolver = new ImageResolver.DirectImageResolver(textures);
            TiledMap map = loadTilemap(root, tmxFile, imageResolver);
            map.setOwnedResources(textures.values().toArray());
            return map;
        } catch (IOException e) {
            throw new GdxRuntimeException("Couldn't load tilemap '" + fileName + "'", e);
        }
    }


    protected XmlReader.Element manipulateSpritesheetPaths (XmlReader.Element root) throws IOException {
        for (XmlReader.Element tileset : root.getChildrenByName("tileset")) {
            String source = tileset.getAttribute("source", null);
            if (source != null) {
                String newSource = manipulatePath(source);
                tileset.setAttribute("source", newSource);
            } else {
                XmlReader.Element imageElement = tileset.getChildByName("image");
                if (imageElement != null) {
                    String imageSource = tileset.getChildByName("image").getAttribute("source");
                    tileset.getChildByName("image").setAttribute("source", manipulatePath(imageSource));
                } else {
                    for (XmlReader.Element tile : tileset.getChildrenByName("tile")) {
                        String imageSource = tile.getChildByName("image").getAttribute("source");
                        tileset.getChildByName("image").setAttribute("source", manipulatePath(imageSource));
                    }
                }
            }
        }
        return root;
    }

    private String manipulatePath(String source) {
        if(source.contains("interior")) {
            return source;
        }


        final int DAY = 0;
        final int AFTERNOON = 1;
        final int NIGHT = 2;
        final int MORNING = 3;
        int daytime = 0;

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if(hour < 6 || hour > 20) {
            daytime = NIGHT;
        } else if (hour >= 6 && hour < 10) {
            daytime = MORNING;
        } else if(hour >= 10 && hour < 18) {
            daytime = DAY;
        } else {
            daytime = AFTERNOON;
        }

        String daytimeString;

        switch(daytime) {
            case MORNING:   daytimeString = "morning";      break;
            case DAY:       daytimeString = "day";          break;
            case AFTERNOON: daytimeString = "afternoon";    break;
            case NIGHT:     daytimeString = "night";        break;
            default:        daytimeString = "day";          break;
        }


        String newSource = source.substring(0,source.length()-4) + "_" + daytimeString + ".png";
        return newSource;
    }
}
