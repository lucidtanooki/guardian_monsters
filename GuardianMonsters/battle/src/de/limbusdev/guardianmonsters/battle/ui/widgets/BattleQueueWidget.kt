package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Array

import java.util.Observable
import java.util.Observer

import de.limbusdev.guardianmonsters.guardians.battle.BattleQueue
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.widgets.MonsterPreviewWidget
import de.limbusdev.utils.extensions.f

/**
 * @author Georg Eckert
 */

class BattleQueueWidget
(
        skin: Skin,
        internal var align: Int
)
    : BattleWidget(), Observer
{
    // .................................................................................. Properties
    private val startx          = 0
    private val starty          = 0
    private val previewYoffset  = 36
    private val queueOffset     = 4

    private val bgIndicator: Image = Image(skin.getDrawable("monster-preview-active"))


    // ..................................................................................... Methods
    /**
     * Adds the given monsters to the widget, beginning at the provided slot
     * @param round
     * @param startSlot
     * @param greyOut whether preview should look deactivated
     * @return next free slot
     */
    private fun addPreviewImagesToWidget
    (
            queue: BattleQueue,
            round: Array<AGuardian>,
            startSlot: Int,
            greyOut: Boolean
    )
            : Int
    {
        var slot = startSlot

        for (i in round.size - 1 downTo 0)
        {
            val guardian = round.get(i)
            val side = queue.getTeamSideFor(guardian)

            val previewWidget = MonsterPreviewWidget(Services.getUI().battleSkin)
            previewWidget.setPreview(guardian.speciesDescription.ID, guardian.abilityGraph.currentForm, side)

            if (greyOut) { previewWidget.color = Color.GRAY }

            previewWidget.setPosition(startx.f(), (starty + startSlot * previewYoffset + queueOffset).f(), align)

            addActor(previewWidget)

            slot++
        }

        return slot
    }

    fun updateQueue(queue: BattleQueue)
    {
        clear()

        bgIndicator.setPosition(-4f, 0f, align)
        addActor(bgIndicator)

        val pos = addPreviewImagesToWidget(queue, queue.currentRound, 0, false)
        addPreviewImagesToWidget(queue, queue.nextRound, pos, true)
    }

    /**
     * Re-adds all monsters to the widget in the correct order
     */
    override fun update(observable: Observable, o: Any?)
    {
        if (observable is BattleQueue && o is BattleQueue.QueueSignal)
        {
            updateQueue(observable)
        }
    }
}
