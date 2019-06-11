package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align

/**
 * BattleHUDMenuButton
 *
 * @author Georg Eckert 2017
 */

class BattleHUDMenuButton(skin: Skin, position: Int) : ImageButton(skin, construct(position))
{
    // ................................................................................ Constructors
    init
    {
        setPosition(SLOTS[position][X].toFloat(), SLOTS[position][Y].toFloat(), ALIGN)
    }


    // ............................................................................ Companion Object
    companion object
    {
        const val X = 0
        const val Y = 1
        const val DEFEND = 0
        const val TEAM = 1
        const val BACK = 2
        const val BAG = 3
        const val SWORD = 4
        const val ESCAPE = 5
        const val EMPTY = 6
        const val ALIGN = Align.bottomLeft

        val SLOTS = arrayOf(

                intArrayOf(4, 1),
                intArrayOf(4, 33),
                intArrayOf(319, 1),
                intArrayOf(319, 33),
                intArrayOf(245, 1),
                intArrayOf(101, 1),
                intArrayOf(173, 17)
        )

        private fun construct(position: Int): String
        {
            return when (position)
            {
                DEFEND  -> "b-attack-extra"
                TEAM    -> "b-attack-monsters"
                BACK    -> "b-attack-back"
                BAG     -> "b-attack-bag"
                SWORD   -> "battle-fight"
                ESCAPE  -> "battle-flee"
                else    -> "battle-empty"
            }
        }
    }
}
