package de.limbusdev.guardianmonsters.battle.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import java.util.Observable;
import java.util.Observer;

import de.limbusdev.guardianmonsters.guardians.Side;
import de.limbusdev.guardianmonsters.guardians.battle.BattleQueue;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.ui.widgets.MonsterPreviewWidget;

/**
 * @author Georg Eckert
 */

public class BattleQueueWidget extends BattleWidget implements Observer
{

    private int startx = 0;
    private int starty = 0;
    private int previewYoffset = 36;
    private int queueOffset = 4;
    int align;

    private Image bgIndicator;

    public BattleQueueWidget(Skin skin, int align)
    {
        super();
        this.align = align;

        bgIndicator = new Image(skin.getDrawable("monster-preview-active"));
    }

    /**
     * Adds the given monsters to the widget, beginning at the provided slot
     * @param round
     * @param startSlot
     * @param greyOut whether preview should look deactivated
     * @return next free slot
     */
    private int addPreviewImagesToWidget(BattleQueue queue, Array<AGuardian> round, int startSlot, boolean greyOut)
    {
        for(int i = round.size-1; i>=0; i--)
        {
            AGuardian m = round.get(i);
            Side side = queue.getTeamSideFor(m);

            MonsterPreviewWidget previewWidget = new MonsterPreviewWidget(Services.getUI().getBattleSkin());
            previewWidget.setPreview(m.getSpeciesDescription().getID(), m.getAbilityGraph().getCurrentForm(), side);

            if(greyOut) {
                previewWidget.setColor(Color.GRAY);
            }

            previewWidget.setPosition(startx,starty+startSlot*previewYoffset+queueOffset,align);

            addActor(previewWidget);

            startSlot++;
        }

        return startSlot;
    }

    public void updateQueue(BattleQueue queue)
    {
        clear();

        bgIndicator.setPosition(-4,0,align);
        addActor(bgIndicator);

        int pos=0;
        pos = addPreviewImagesToWidget(queue, queue.getCurrentRound(),pos,false);
        addPreviewImagesToWidget(queue, queue.getNextRound(),pos,true);
    }

    /**
     * Re-adds all monsters to the widget in the correct order
     */
    @Override
    public void update(Observable observable, Object o)
    {
        if(observable instanceof BattleQueue && o instanceof BattleQueue.QueueSignal)
        {
            BattleQueue queue = (BattleQueue) observable;
            BattleQueue.QueueSignal signal = (BattleQueue.QueueSignal) o;

            updateQueue(queue);
        }
    }
}
