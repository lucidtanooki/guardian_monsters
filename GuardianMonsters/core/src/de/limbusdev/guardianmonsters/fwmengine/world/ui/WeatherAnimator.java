package de.limbusdev.guardianmonsters.fwmengine.world.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.data.TextureAssets;
import de.limbusdev.guardianmonsters.fwmengine.managers.Media;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.geometry.IntVec2;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Created by Georg Eckert on 05.02.17.
 */

public class WeatherAnimator {

    private interface WeatherRenderer {
        public void render(Batch batch, float elapsedTime);
    }

    private WeatherRenderer renderer;

    private Media media;
    private boolean weatherOn;

    public WeatherAnimator(TiledMap map) {
        media = Services.getMedia();
        this.weatherOn = map.getProperties().containsKey("weather");

        if(weatherOn) {
            String weatherType = map.getProperties().get("weather", String.class);

            switch(weatherType) {
                case "clouds":  renderer = new CloudRenderer(map); break;
                case "fog":     renderer = new FogRenderer(map,0); break;
                case "woods":   renderer = new WoodsRenderer(map); break;
                case "rain":    renderer = new RainRenderer(map); break;
                default:break;
            }
        }
    }

    /**
     * Renders the weather effect above a tiled map
     * @param batch {@link Batch} of the {@link OrthogonalTiledMapAndEntityRenderer}
     */
    public void render(Batch batch, float elapsedTime) {
        if(!weatherOn) {
            // No weather effect in this map
            return;
        } else {
            renderer.render(batch, elapsedTime);
        }
    }



    // ............................................................................... INNER CLASSES

    private abstract class AWeatherRenderer implements WeatherRenderer {
        protected int width, height;

        public AWeatherRenderer(TiledMap map) {
            width = map.getProperties().get("width", Integer.class);
            height = map.getProperties().get("height", Integer.class);
        }
    }

    private class RainRenderer extends AWeatherRenderer {
        private Animation rainAnimation;
        private FogRenderer fogRenderer;
        private Array<Float> randomRaindropOffset;
        private Array<IntVec2> randomRaindropPosition;
        private int lastFrameIndex;

        public RainRenderer(TiledMap map) {
            super(map);
            fogRenderer = new FogRenderer(map,1);
            rainAnimation = media.getObjectAnimation("rain0");
            rainAnimation.setFrameDuration(0.2f);
            lastFrameIndex = rainAnimation.getKeyFrames().length-1;

            randomRaindropOffset = new Array<>();
            randomRaindropPosition = new Array<>();
            for(int n = 0; n<width*height/2; n++) {
                randomRaindropOffset.add(MathUtils.random(3f));
                IntVec2 dropPos = new IntVec2(0,0);
                randomRaindropPosition.add(setRandomDropPosition(dropPos));
            }
        }

        private IntVec2 setRandomDropPosition(IntVec2 dropPos) {
            dropPos.x = MathUtils.random(width)*GS.TILE_SIZE+MathUtils.random(-8,7);
            dropPos.y = MathUtils.random(height)*GS.TILE_SIZE+MathUtils.random(-8,7);
            return dropPos;
        }

        @Override
        public void render(Batch batch, float elapsedTime) {
            fogRenderer.render(batch, elapsedTime);
            for(int n=0; n<randomRaindropPosition.size; n++) {
                TextureRegion r = rainAnimation.getKeyFrame(elapsedTime+randomRaindropOffset.get(n),true);
                IntVec2 pos = randomRaindropPosition.get(n);
                batch.draw(r, pos.x, pos.y);
                if(rainAnimation.getKeyFrameIndex(elapsedTime+randomRaindropOffset.get(n)) == lastFrameIndex) {
                    setRandomDropPosition(pos);
                }
            }
        }

    }

    private class FogRenderer extends AWeatherRenderer {
        private Texture fogTexture;

        public FogRenderer(TiledMap map, int fogIndex) {
            super(map);
            switch(fogIndex) {
                case 1:     fogTexture = media.getTexture(TextureAssets.weatherTextures[3]); break;
                default:    fogTexture = media.getTexture(TextureAssets.weatherTextures[1]); break;
            }

        }

        @Override
        public void render(Batch batch, float elapsedTime) {
            batch.draw(fogTexture, 0, 0, GS.RES_X, GS.RES_Y);
        }
    }

    private class WoodsRenderer extends AWeatherRenderer {
        private Texture woodTexture;

        public WoodsRenderer(TiledMap map) {
            super(map);
            woodTexture = media.getTexture(TextureAssets.weatherTextures[2]);
        }

        @Override
        public void render(Batch batch, float elapsedTime) {
            batch.draw(woodTexture, 0, 0, GS.RES_X, GS.RES_Y);
        }
    }

    private class CloudRenderer extends AWeatherRenderer {
        private Texture cloudTexture;
        private Array<Vector2> weatherTiles;
        private int width, height;

        public CloudRenderer(TiledMap map) {
            super(map);

            cloudTexture = media.getTexture(TextureAssets.weatherTextures[0]);

            weatherTiles = new Array<>();
            width = map.getProperties().get("width", Integer.class);
            height = map.getProperties().get("height", Integer.class);
            for(int i=-1; i<width*16/640+1; i++) {
                for(int j=-1; j<height*16/480+1; j++) {
                    weatherTiles.add(new Vector2(i*640,j*480));
                }
            }
        }

        @Override
        public void render(Batch batch, float elapsedTime) {
            // Weather effect available, render
            for(Vector2 iv : weatherTiles) {
                iv.x += Gdx.graphics.getDeltaTime()*10.0f;
                if(iv.x >= (width*16f/640+1)*640) {
                    iv.x=-640f;
                }
                batch.draw(cloudTexture,iv.x,iv.y);
            }
        }
    }
}
