package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Array
import de.limbusdev.guardianmonsters.guardians.Side

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

class BattleQueueWidget : BattleWidget, Observer
{
    // .................................................................................. Properties
    private val startx          : Int = 0
    private val starty          : Int = 0
    private val previewYoffset  : Int = 36
    private val queueOffset     : Int = 4
    internal val align          : Int

    private val bgIndicator: Image


    constructor(skin: Skin, align: Int) : super()
    {
        this.align = align
        bgIndicator = Image(skin.getDrawable("monster-preview-active"))
    }

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

        for (i: Int in round.size - 1 downTo 0)
        {
            val guardian: AGuardian = round.get(i)
            val side: Side = queue.getTeamSideFor(guardian)

            val previewWidget = MonsterPreviewWidget(Services.getUI().battleSkin)
            previewWidget.setPreview(guardian.speciesDescription.ID, guardian.abilityGraph.currentForm, side)

            if (greyOut) { previewWidget.color = Color.GRAY }

            previewWidget.setPosition(startx.f(), (starty + startSlot * previewYoffset + queueOffset).f(), align)

            this.addActor(previewWidget)

            slot++
        }

        return slot
    }

    fun updateQueue(queue: BattleQueue)
    {
        clear()

        bgIndicator.setPosition(-4f, 0f, align)
        this.addActor(bgIndicator)

        var pos: Int = 0
        pos = addPreviewImagesToWidget(queue, queue.currentRound, pos, false)
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
