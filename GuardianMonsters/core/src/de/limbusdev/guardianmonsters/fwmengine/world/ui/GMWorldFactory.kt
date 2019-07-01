package de.limbusdev.guardianmonsters.fwmengine.world.ui

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import de.limbusdev.guardianmonsters.scene2d.*
import de.limbusdev.guardianmonsters.services.Services
import ktx.style.get

internal object GMWorldFactory
{
    /** HUD Blueprint */
    internal object HUDBP
    {
        const val SCREEN_WIDTH = 428f
        const val SCREEN_HEIGHT = 240f

        private val skin get() = Services.UI().defaultSkin

        fun createBlackCurtainImg(parent: Group? = null) : Image
        {
            return makeImage(

                    drawable = skin["black"],
                    layout = Scene2DLayout(SCREEN_WIDTH, SCREEN_HEIGHT),
                    parent = parent
            )
        }

        fun createHUDMenuButton(onMainMenuButton: () -> Unit, parent: Group? = null) : TextButton
        {
            val menu = TextButton(Services.I18N().General("hud_menu"), skin, "open-menu")
            menu.setPosition(SCREEN_WIDTH, SCREEN_HEIGHT - 2, Align.topRight)

            menu.replaceOnButtonClick(onMainMenuButton)

            return menu
        }

        fun createHUDSaveButton(skin: Skin, parent: Group? = null) : TextButton
        {
            return TextButton(Services.I18N().General("hud_save"), skin, "menu-entry")
        }

        fun createHUDQuitButton(skin: Skin, parent: Group? = null) : TextButton
        {
            return TextButton(Services.I18N().General("hud_quit"), skin, "menu-entry")
        }

        /** Group containing buttons: Save, Quit, Monsters */
        fun createHUDMainMenu
        (
                saveButtonCB: () -> Unit,
                quitButtonCB: () -> Unit,
                teamButtonCB: () -> Unit
        )
                : VerticalGroup
        {
            val skin = Services.UI().defaultSkin

            val menuButtons = VerticalGroup()
            menuButtons.space(2f)
            menuButtons.columnAlign(Align.topRight)
            menuButtons.setPosition(SCREEN_WIDTH + 80f, SCREEN_HEIGHT - 44f, Align.topLeft)

            val saveButton = TextButton(Services.I18N().General("hud_save"), skin, "menu-entry")
            val quitButton = TextButton(Services.I18N().General("hud_quit"), skin, "menu-entry")
            val teamButton = TextButton(Services.I18N().General("hud_team"), skin, "menu-entry")

            saveButton.replaceOnButtonClick(saveButtonCB)
            quitButton.replaceOnButtonClick(quitButtonCB)
            teamButton.replaceOnButtonClick(teamButtonCB)

            menuButtons.addActor(saveButton)
            menuButtons.addActor(quitButton)
            menuButtons.addActor(teamButton)

            return menuButtons
        }

        fun createAButton(onAButton: () -> Unit) : ImageButton
        {
            val aButton = makeImageButton(skin["a"], PositionXYA(SCREEN_WIDTH - 4, 56f, Align.bottomRight))
            aButton.replaceOnButtonClick(onAButton)
            return aButton
        }

        fun createBButton(onBButton: () -> Unit) : ImageButton
        {
            val bButton = makeImageButton(skin["b"], PositionXYA(SCREEN_WIDTH - 48, 8f, Align.bottomRight))
            bButton.replaceOnButtonClick(onBButton)
            return bButton
        }
    }
}