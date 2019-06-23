package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align

import de.limbusdev.guardianmonsters.guardians.Element
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.utils.extensions.f

/**
 * BattleHUDTextButton
 *
 * @author Georg Eckert 2017
 */

class BattleHUDTextButton
(
        text     : String,
        position : Int,
        element  : Element
)
    : TextButton(text, Services.UI().battleSkin, construct(position, element))
{
    // ................................................................................ Constructors
    init
    {
        setPosition(SLOTS[position][X].f(), SLOTS[position][Y].f(), ALIGN)
    }


    // ............................................................................ Companion Object
    companion object
    {
        const val X             = 0
        const val Y             = 1
        const val LEFT          = 0
        const val TOP_LEFT      = 1
        const val BOTTOM_LEFT   = 2
        const val CENTER        = 3
        const val TOP_RIGHT     = 4
        const val BOTTOM_RIGHT  = 5
        const val RIGHT         = 6
        const val CENTER_TOP    = 7
        const val CENTER_BOTTOM = 8
        const val ALIGN = Align.bottomLeft

        val SLOTS = arrayOf(

                intArrayOf(29, 17),
                intArrayOf(101, 33),
                intArrayOf(101, 1),
                intArrayOf(173, 17),
                intArrayOf(245, 33),
                intArrayOf(245, 1),
                intArrayOf(317, 17),
                intArrayOf(173, 49),
                intArrayOf(173, 1)
        )

        private fun construct(position: Int, element: Element = Element.NONE): String
        {
            return when (position)
            {
                CENTER_TOP    -> "b-attack-question-centertop"
                CENTER_BOTTOM -> "tb-attack-none-centerbottom"
                else          -> "tb-attack-${element.toString().toLowerCase()}"
            }
        }
    }
}
