package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;


import de.limbusdev.guardianmonsters.data.TextureAssets;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.ListObserver;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.MonsterSpeedComparator;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.AHUD;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Monster;

/**
 * Created by georg on 20.11.16.
 */

public class BattleQueueWidget extends BattleWidget {

    int startx = 0;
    int starty = 0;
    int previewYoffset = 36;
    int queueOffset = 4;
    int align;

    private Image bgIndicator;

    public BattleQueueWidget(AHUD hud, Skin skin, int align) {
        super(hud);
        this.align = align;

        bgIndicator = new Image(skin.getDrawable("monster-preview-active"));
    }

    public void update(Array<Monster> current, Array<Monster> next) {
        clear();

        bgIndicator.setPosition(-4,0,align);
        addActor(bgIndicator);

        int i=0;

        Array<Monster> tmpCurrent = new Array<Monster>();
        tmpCurrent.addAll(current);
        tmpCurrent.sort(new MonsterSpeedComparator());
        tmpCurrent.reverse();

        for(Monster m : tmpCurrent) {
            Image previewBackground = new Image(Services.getUI().getBattleSkin().getDrawable("monster-preview"));
            Group monPreview = new Group();
            System.out.println(m.ID + "_" + m.evolution+1);
            Image preview = new Image(Services.getMedia().getTextureAtlas(TextureAssets.battleMonsterPreviews).findRegion(
                Integer.toString(m.ID),
                m.evolution+1));
            preview.setPosition(4,6,align);
            monPreview.addActor(previewBackground);
            monPreview.addActor(preview);

            monPreview.setPosition(startx,starty+i*previewYoffset+queueOffset,align);

            addActor(monPreview);

            i++;
        }

        Array<Monster> tmpNext = new Array<Monster>();
        tmpNext.addAll(next);
        tmpNext.sort(new MonsterSpeedComparator());
        tmpNext.reverse();

        for(Monster m : tmpNext) {
            Image previewBackground = new Image(Services.getUI().getBattleSkin().getDrawable("monster-preview"));
            Group monPreview = new Group();
            System.out.println(m.ID + "_" + m.evolution+1);
            Image preview = new Image(Services.getMedia().getTextureAtlas(TextureAssets.battleMonsterPreviews).findRegion(
                Integer.toString(m.ID),
                m.evolution+1));
            preview.setPosition(4,6,align);
            monPreview.addActor(previewBackground);
            monPreview.addActor(preview);

            monPreview.setPosition(startx,starty+i*previewYoffset+queueOffset,align);

            addActor(monPreview);

            i++;
        }
    }
}
