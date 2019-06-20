package de.limbusdev.guardianmonsters.inventory

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align

import de.limbusdev.guardianmonsters.ui.Constant


/**
 * @author Georg Eckert 2017
 */

abstract class AInventorySubMenu() : Group()
{
    // ............................................................................................. PROPERTIES
    var core: InventoryScreen? = null


    // ............................................................................................. CONSTRUCTORS
    init
    {
        setDebug(Constant.DEBUGGING_ON, true)
        setSize(Constant.WIDTHf, Constant.HEIGHTf - TOOLBAR_HEIGHT)
        setPosition(0f, 0f, Align.bottomLeft)
    }


    // ............................................................................................. METHODS
    /**
     * Used to update the currently selected Guardian of this SubMenu, if necessary.
     * @param teamPosition
     */
    open fun syncSelectedGuardian(teamPosition: Int) {}

    /**
     * Tells the core about the currently selected Guardian
     * @param teamPosition
     */
    protected fun propagateSelectedGuardian(teamPosition: Int)
    {
        core?.setCurrentlyChosenTeamMember(teamPosition)
    }

    abstract fun refresh()

    protected abstract fun layout(skin: Skin)

    companion object
    {
        val TOOLBAR_HEIGHT = 36
    }
}
