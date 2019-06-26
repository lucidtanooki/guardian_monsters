package de.limbusdev.guardianmonsters.inventory

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.viewport.FitViewport

import de.limbusdev.guardianmonsters.guardians.items.Inventory
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.inventory.ui.widgets.items.KeyItemsSubMenu
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.guardianmonsters.ui.widgets.MainToolBar
import de.limbusdev.guardianmonsters.ui.widgets.TiledImage
import de.limbusdev.utils.extensions.set
import ktx.style.get

/**
 * Inventory Screen, holds Team view, Ability Board, Item View, Encyclopedia
 * Copyright Georg Eckert
 */
class InventoryScreen(team: Team, inventory: Inventory) : Screen, MainToolBar.Callbacks
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    companion object { private const val BG_TILE = "bg-pattern-3" }

    private val stage: Stage
    private val skin : Skin = Services.UI().inventorySkin
    private val views: ArrayMap<String, AInventorySubMenu> = ArrayMap()


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        val fit = FitViewport(Constant.WIDTHf, Constant.HEIGHTf)
        this.stage = Stage(fit)


        tileBackground()
        assembleToolbar()

        views["team"]           = TeamSubMenu(skin, team)
        views["items"]          = ItemsSubMenu(skin, inventory, team)
        views["ability"]        = AbilityGraphSubMenu(skin, team)
        views["key"]            = KeyItemsSubMenu(skin, inventory)
        views["abilityChoice"]  = AbilityChoiceSubMenu(skin, team)
        views["encyclo"]        = EncycloSubMenu(skin)


        stage.addActor(views["team"])

        // provide all SubMenus with the InventoryScreen, for synchronisation
        views.values().forEach { it.core = this }

        setCurrentlyChosenTeamMember(0)
    }


    /** Tiles the complete Background with a specific 16x16 image */
    private fun tileBackground()
    {
        val bg = TiledImage(skin[BG_TILE], 27, 13)
        bg.setPosition(0f, -4f, Align.bottomLeft)
        stage.addActor(bg)
    }

    // .............................................................................. SCREEN METHODS

    override fun show()
    {
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float)
    {
        Gdx.gl.glClearColor(.1f, .1f, .1f, 1f)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.draw()
        stage.act(delta)
    }

    override fun resize(width: Int, height: Int)
    {
        stage.viewport.update(width, height)
    }

    override fun pause()
    {
        // TODO
    }

    override fun resume()
    {
        // TODO
    }

    override fun hide()
    {
        // TODO
    }

    override fun dispose()
    {
        stage.dispose()
    }


    // ...................................................................... MAINTOOLBAR CONTROLLER

    private fun assembleToolbar()
    {
        val toolBar = MainToolBar(skin, this)
        toolBar.setPosition(0f, Constant.HEIGHTf, Align.topLeft)

        stage.addActor(toolBar)
    }

    private fun removeSubMenus()
    {
        views.values().forEach { it.remove() }
    }

    override fun onTeamButton()
    {
        removeSubMenus()
        views["team"].refresh()
        stage.addActor(views["team"])
    }

    override fun onItemsButton()
    {
        removeSubMenus()
        stage.addActor(views["items"])
    }

    override fun onAbilityButton()
    {
        removeSubMenus()
        stage.addActor(views["ability"])
    }

    override fun onKeyButton()
    {
        removeSubMenus()
        stage.addActor(views["key"])
    }

    override fun onAbilityChoiceButton()
    {
        removeSubMenus()
        views["abilityChoice"].refresh()
        stage.addActor(views["abilityChoice"])
    }

    override fun onEncycloButton()
    {
        removeSubMenus()
        stage.addActor(views["encyclo"])
    }

    override fun onExitButton()
    {
        removeSubMenus()
    }

    fun setCurrentlyChosenTeamMember(currentlyChosenTeamMember: Int)
    {
        for (ism in views.values())
        {
            ism.syncSelectedGuardian(currentlyChosenTeamMember)
        }
    }
}
