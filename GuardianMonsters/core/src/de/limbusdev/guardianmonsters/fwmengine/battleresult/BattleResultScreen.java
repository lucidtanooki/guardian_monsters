package de.limbusdev.guardianmonsters.fwmengine.battleresult;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.data.TextureAssets;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.BattleResult;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.monsters.Team;


/**
 * BattleResultScreen
 *
 * @author Georg Eckert 2017
 */

public class BattleResultScreen implements Screen {

    private BattleResultHUD resultHUD;
    private Animation<TextureAtlas.AtlasRegion> anim;
    private float elapsedTime=0;

    public BattleResultScreen(Team team, BattleResult result) {
        this.resultHUD = new BattleResultHUD(Services.getUI().getInventorySkin(), team, result);

        TextureAtlas atlas = Services.getMedia().getTextureAtlas(TextureAssets.battleAnimations);
        Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions("att_alllightning");
        anim = new Animation<TextureAtlas.AtlasRegion>(.1f, regions, Animation.PlayMode.LOOP);
    }

    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {

    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        elapsedTime += delta;
        // Clear screen
        Gdx.gl.glClearColor(.3f, .3f, .3f, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        resultHUD.getStage().getViewport().apply();
        resultHUD.update(delta);
        resultHUD.draw();

        Batch batch = resultHUD.getStage().getBatch();
        batch.begin();
        batch.draw(anim.getKeyFrame(elapsedTime),0,0);
        batch.end();
    }

    /**
     * @param width
     * @param height
     * @see ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {
        resultHUD.getStage().getViewport().update(width, height);
    }

    /**
     * @see ApplicationListener#pause()
     */
    @Override
    public void pause() {

    }

    /**
     * @see ApplicationListener#resume()
     */
    @Override
    public void resume() {

    }

    /**
     * Called when this screen is no longer the current screen for a {@link Game}.
     */
    @Override
    public void hide() {

    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {

    }
}
