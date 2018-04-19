package de.limbusdev.guardianmonsters.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.services.Services;

/**
 * MonsterPreviewWidget
 *
 * @author Georg Eckert 2017
 */

public class MonsterPreviewWidget extends Group {

    private Image monster;

    public MonsterPreviewWidget(Skin skin) {
        super();

        setSize(32,32);

        Image bg = new Image(skin.getDrawable("monster-preview-frame-base"));
        bg.setPosition(0,0,Align.bottomLeft);

        monster = Services.getMedia().getMonsterFace(0, 0);
        monster.setSize(24,24);
        monster.setPosition(4,5, Align.bottomLeft);

        Image cover = new Image(skin.getDrawable("monster-preview-frame-cover"));
        cover.setPosition(0,0,Align.bottomLeft);

        addActor(bg);
        addActor(monster);
        addActor(cover);
    }

    public void setPreview(int monsterID, int form) {
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
