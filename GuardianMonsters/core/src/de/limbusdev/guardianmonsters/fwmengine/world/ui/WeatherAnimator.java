package de.limbusdev.guardianmonsters.fwmengine.world.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.data.TextureAssets;
import de.limbusdev.guardianmonsters.fwmengine.managers.Media;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;

/**
 * Created by Georg Eckert on 05.02.17.
 */

public class WeatherAnimator {

    private Media media;
    private Array<Vector2> weatherTiles;
    private boolean weatherOn;
    private int width, height;

    public WeatherAnimator(TiledMap map) {
        media = Services.getMedia();
        this.weatherOn = map.getProperties().containsKey("weather");

        weatherTiles = new Array<>();
        width = map.getProperties().get("width", Integer.class);
        height = map.getProperties().get("height", Integer.class);
        for(int i=-1; i<width*16/640+1; i++) {
            for(int j=-1; j<height*16/480+1; j++) {
                weatherTiles.add(new Vector2(i*640,j*480));
            }
        }
    }

    /**
     * Renders the weather effect above a tiled map
     * @param batch {@link Batch} of the {@link OrthogonalTiledMapAndEntityRenderer}
     */
    public void render(Batch batch) {
        if(!weatherOn) {
            // No weather effect in this map
            return;
        } else {
            // Weather effect available, render
            for(Vector2 iv : weatherTiles) {
                iv.x += Gdx.graphics.getDeltaTime()*10.0f;
                if(iv.x >= (width*16f/640+1)*640) {
                    iv.x=-640f;
                }
                batch.draw(media.getTexture(TextureAssets.weatherTexture1),iv.x,iv.y);
            }
        }
    }
}
