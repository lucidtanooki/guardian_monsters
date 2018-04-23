package de.limbusdev.guardianmonsters.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.services.Services;

import static de.limbusdev.guardianmonsters.ui.Constant.LEFT;

/**
 * MonsterPreviewWidget
 *
 * @author Georg Eckert 2017
 */

public class MonsterPreviewWidget extends Group {

    private Image monster, bgImg, coverImg;
    private Drawable bg, cover, bgOpp, coverOpp;

    public MonsterPreviewWidget(Skin skin)
    {
        super();

        setSize(32,32);

        bg = skin.getDrawable("monster-preview-frame-base");
        bgOpp = skin.getDrawable("monster-preview-frame-base-opp");
        cover = skin.getDrawable("monster-preview-frame-cover");
        coverOpp = skin.getDrawable("monster-preview-frame-cover-opp");


        bgImg = new Image(bg);
        bgImg.setPosition(0,0,Align.bottomLeft);

        monster = Services.getMedia().getMonsterFace(0, 0);
        monster.setSize(24,24);
        monster.setPosition(4,5, Align.bottomLeft);

        coverImg = new Image(cover);
        coverImg.setPosition(0,0,Align.bottomLeft);


        addActor(bgImg);
        addActor(monster);
        addActor(coverImg);
    }

    public void setPreview(int monsterID, int form, boolean side)
    {
        if(side == LEFT) {
            bgImg.setDrawable(bg);
            coverImg.setDrawable(cover);
        } else /*side == RIGHT*/ {
            bgImg.setDrawable(bgOpp);
            coverImg.setDrawable(coverOpp);
        }

        Image face = Services.getMedia().getMonsterFace(monsterID, form);
        if(face != null) {
            monster.setDrawable(face.getDrawable());
        }
    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);
        for(Actor a : this.getChildren()) {
            a.setColor(color);
        }
    }
}
