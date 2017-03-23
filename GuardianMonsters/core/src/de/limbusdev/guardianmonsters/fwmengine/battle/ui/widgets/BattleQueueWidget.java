package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;


import de.limbusdev.guardianmonsters.data.TextureAssets;
import de.limbusdev.guardianmonsters.fwmengine.battle.control.BattleQueue;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.monsters.Monster;

/**
 * @author Georg Eckert
 */

public class BattleQueueWidget extends BattleWidget implements Listener<BattleQueue.QueueSignal> {

    private int startx = 0;
    private int starty = 0;
    private int previewYoffset = 36;
    private int queueOffset = 4;
    int align;

    private Image bgIndicator;

    public BattleQueueWidget(Skin skin, int align) {
        super();
        this.align = align;

        bgIndicator = new Image(skin.getDrawable("monster-preview-active"));
    }

    /**
     * Adds the given monsters to the widget, beginning at the provided slot
     * @param queue
     * @param startSlot
     * @param greyOut whether preview should look deactivated
     * @return next free slot
     */
    private int addPreviewImagesToWidget(Array<Monster> queue, int startSlot, boolean greyOut) {
        for(int i = queue.size-1; i>=0; i--) {
            Monster m = queue.get(i);
            Image previewBackground = new Image(
                Services.getUI().getBattleSkin().getDrawable("monster-preview"));
            if(greyOut) {
                previewBackground.setColor(Color.GRAY);
            }
            Group monPreview = new Group();

            TextureAtlas previewAtlas = Services.getMedia()
                .getTextureAtlas(TextureAssets.battleMonsterPreviews);

            TextureAtlas.AtlasRegion sprite = previewAtlas.findRegion(Integer.toString(m.ID));
            if(sprite == null) {
                sprite = previewAtlas.findRegion("0");
            }

            Image preview = new Image(sprite);
            preview.setPosition(4,6,align);
            monPreview.addActor(previewBackground);
            monPreview.addActor(preview);

            monPreview.setPosition(startx,starty+startSlot*previewYoffset+queueOffset,align);

            addActor(monPreview);

            startSlot++;
        }

        return startSlot;
    }

    /**
     * Re-adds all monsters to the widget in the correct order
     */
    @Override
    public void receive(Signal<BattleQueue.QueueSignal> signal, BattleQueue.QueueSignal queueSignal) {
        BattleQueue queue = queueSignal.queue;
        clear();

        bgIndicator.setPosition(-4,0,align);
        addActor(bgIndicator);

        int pos=0;
        pos = addPreviewImagesToWidget(queue.getCurrentRound(),pos,false);
        addPreviewImagesToWidget(queue.getNextRound(),pos,true);
    }
}
