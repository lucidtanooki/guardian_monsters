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

        data class ConversationWidget(val widget: Group, val conversation: Label, val title: Label)

        fun createConversationWidget() : ConversationWidget
        {
            val widget = Group()
            makeImage(skin["dialog_bg2"],      Scene2DLayout(192f, 48f, SCREEN_WIDTH/2,       0f, Align.bottom),     widget)
            makeImage(skin["dialog_name_bg2"], Scene2DLayout( 89f, 18f, SCREEN_WIDTH/2 - 80, 46f, Align.bottomLeft), widget)
            widget.setPosition(0f, -56f)

            val conversation = makeLabel(

                    style  = skin["default"],
                    text   = "Test Label",
                    layout = LabelLayout(186f, 40f, SCREEN_WIDTH/2-90, 44f, Align.topLeft, Align.topLeft, true),
                    parent = widget
            )

            val title = makeLabel(

                    style  = skin["default"],
                    text   = "",
                    layout = LabelLayout(84f, 16f, SCREEN_WIDTH/2-78, 48f, Align.bottomLeft, Align.left),
                    parent = widget
            )

            return ConversationWidget(widget, conversation, title)
        }
    }
}