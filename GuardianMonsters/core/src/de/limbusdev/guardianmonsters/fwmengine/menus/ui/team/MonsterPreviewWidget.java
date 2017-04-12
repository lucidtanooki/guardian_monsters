package de.limbusdev.guardianmonsters.fwmengine.menus.ui.team;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.fwmengine.managers.Services;

/**
 * MonsterPreviewWidget
 *
 * @author Georg Eckert 2017
 */

public class MonsterPreviewWidget extends Group {

    private Image monster;

    public MonsterPreviewWidget() {
        super();

        setSize(32,32);

        Skin skin = Services.getUI().getInventorySkin();
        Image bg = new Image(skin.getDrawable("monster-preview-frame-base"));
        bg.setPosition(0,0,Align.bottomLeft);

        monster = Services.getMedia().getMonsterFace(0);
        monster.setSize(24,24);
        monster.setPosition(4,5, Align.bottomLeft);

        Image cover = new Image(skin.getDrawable("monster-preview-frame-cover"));
        cover.setPosition(0,0,Align.bottomLeft);

        addActor(bg);
        addActor(monster);
        addActor(cover);
    }

    public void setPreview(int monsterID) {
        Image face = Services.getMedia().getMonsterFace(monsterID);
        monster.setDrawable(face.getDrawable());
    }

}
