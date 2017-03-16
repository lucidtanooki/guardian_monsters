package de.limbusdev.guardianmonsters.fwmengine.menus.ui.team;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.geometry.IntVec2;
import de.limbusdev.guardianmonsters.model.monsters.Monster;

/**
 * Draws a monsters values as a pentaggon of PStrength, PDefense,
 * MStrength, MDefense, Speed. All values are given as a fraction
 * of 999.
 *
 * @author Georg Eckert 2017
 */

public class StatusPentagonWidget extends Group {

    private ShapeRenderer shpRend;
    private float[] points;
    private IntVec2 center;
    private Color color = Color.valueOf("eb8931ff");
    private boolean initialized;
    private Image statPentagon;

    public StatusPentagonWidget(Skin skin) {
        setSize(100,80);
        shpRend = new ShapeRenderer();
        center = new IntVec2(50,32);
        initialized = false;

        statPentagon = new Image();
        statPentagon.setSize(100,80);
        statPentagon.setPosition(0, 0, Align.bottomLeft);
        statPentagon.setDrawable(skin.getDrawable("statPentagram-symbols"));
        addActor(statPentagon);
    }

    public void init(Monster monster) {
        float pstr = monster.stat.getPStrMax() / monster.stat.getMaxPossiblePStr()*32f;
        float pdef = monster.stat.getPDefMax() / monster.stat.getMaxPossiblePDef()*32f;
        float mstr = monster.stat.getMStrMax() / monster.stat.getMaxPossibleMStr()*32f;
        float mdef = monster.stat.getMDefMax() / monster.stat.getMaxPossibleMDef()*32f;
        float speed= monster.stat.getSpeedMax()/ monster.stat.getMaxPossibleSpeed()*32f;

        points = new float[10];

        points[0] = center.x;
        points[1] = center.y + pstr;

        points[2] = center.x + MathUtils.cosDeg(18)*pdef;
        points[3] = center.y + MathUtils.sinDeg(18)*pdef;

        points[4] = center.x + MathUtils.cosDeg(54)*speed;
        points[5] = center.y - MathUtils.sinDeg(54)*speed;

        points[6] = center.x - MathUtils.cosDeg(54)*mstr;
        points[7] = center.y - MathUtils.sinDeg(54)*mstr;

        points[8] = center.x - MathUtils.cosDeg(18)*mdef;
        points[9] = center.y + MathUtils.sinDeg(18)*mdef;

        initialized = true;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(!initialized) return;

        batch.end();

        shpRend.setProjectionMatrix(batch.getProjectionMatrix());
        shpRend.setTransformMatrix(batch.getTransformMatrix());
        shpRend.translate(getX(), getY(), 0);

        shpRend.begin(ShapeRenderer.ShapeType.Filled);

        // Draw Pentagon from triangles
        shpRend.setColor(color);
        for(int i=0; i<10; i+=2) {
            shpRend.triangle(
                center.x, center.y,
                points[i%10], points[(i+1)%10],
                points[(i+2)%10], points[(i+3)%10]
            );

        }
        shpRend.end();

        batch.begin();
        super.draw(batch,parentAlpha);
    }

}
