package de.limbusdev.guardianmonsters.fwmengine.world.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.Constant;
import de.limbusdev.guardianmonsters.assets.paths.AssetPath;
import de.limbusdev.guardianmonsters.media.IMediaManager;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.utils.geometry.IntVec2;

/**
 * @author Georg Eckert 2017
 */

public class WeatherAnimator {

    private interface WeatherRenderer {
        void render(Batch batch, float elapsedTime);
    }

    private WeatherRenderer renderer;

    private IMediaManager media;
    private boolean weatherOn;

    public WeatherAnimator(TiledMap map) {
        media = Services.Media();
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
     * @param batch {@link Batch} of the {@link ExtendedTiledMapRenderer}
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
        private Animation<TextureRegion> rainAnimation;
        private FogRenderer fogRenderer;
        private Array<Float> randomRaindropOffset;
        private Array<IntVec2> randomRaindropPosition;
        private int lastFrameIndex;

        public RainRenderer(TiledMap map) {
            super(map);
            fogRenderer = new FogRenderer(map,1);
            rainAnimation = media.getObjectAnimation("rain0");
            rainAnimation.setFrameDuration(0.2f);
            lastFrameIndex = 8;

            randomRaindropOffset = new Array<>();
            randomRaindropPosition = new Array<>();
            for(int n = 0; n<width*height/2; n++) {
                randomRaindropOffset.add(MathUtils.random(3f));
                IntVec2 dropPos = new IntVec2(0,0);
                randomRaindropPosition.add(setRandomDropPosition(dropPos));
            }
        }

        private IntVec2 setRandomDropPosition(IntVec2 dropPos) {
            dropPos.setX(MathUtils.random(width) * Constant.TILE_SIZE + MathUtils.random(-8, 7));
            dropPos.setY(MathUtils.random(height) * Constant.TILE_SIZE + MathUtils.random(-8, 7));
            return dropPos;
        }

        @Override
        public void render(Batch batch, float elapsedTime) {
            fogRenderer.render(batch, elapsedTime);
            for(int n=0; n<randomRaindropPosition.size; n++) {
                TextureRegion r = rainAnimation.getKeyFrame(elapsedTime+randomRaindropOffset.get(n),true);
                IntVec2 pos = randomRaindropPosition.get(n);
                batch.draw(r, pos.getX(), pos.getY());
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
                case 1:     fogTexture = media.getTexture(AssetPath.Textures.INSTANCE.getWEATHER()[3]); break;
                default:    fogTexture = media.getTexture(AssetPath.Textures.INSTANCE.getWEATHER()[1]); break;
            }
        }

        @Override
        public void render(Batch batch, float elapsedTime) {
            batch.draw(fogTexture, 0, 0, super.width*Constant.TILE_SIZE, super.height*Constant.TILE_SIZE);
        }
    }

    private class WoodsRenderer extends AWeatherRenderer {
        private Texture woodTexture;

        public WoodsRenderer(TiledMap map) {
            super(map);
            woodTexture = media.getTexture(AssetPath.Textures.INSTANCE.getWEATHER()[2]);
        }

        @Override
        public void render(Batch batch, float elapsedTime) {
            batch.draw(woodTexture, 0, 0, Constant.RES_X, Constant.RES_Y);
        }
    }

    private class CloudRenderer extends AWeatherRenderer {
        private Texture cloudTexture;
        private Array<Vector2> weatherTiles;
        private int width, height;

        public CloudRenderer(TiledMap map) {
            super(map);

            cloudTexture = media.getTexture(AssetPath.Textures.INSTANCE.getWEATHER()[0]);

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
