package de.limbusdev.guardianmonsters.fwmengine.world.ui.widgets

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import de.limbusdev.guardianmonsters.scene2d.LabelLayout
import de.limbusdev.guardianmonsters.scene2d.Scene2DLayout
import de.limbusdev.guardianmonsters.scene2d.makeImage
import de.limbusdev.guardianmonsters.scene2d.makeLabel
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.Constant
import ktx.actors.txt
import ktx.style.get

class ConversationWidget(skin: Skin = Services.UI().defaultSkin) : Group()
{
    private val conversationTextLabel  : Label
    private val conversationTitleLabel : Label

    init
    {
        makeImage(skin["dialog_bg2"],      Scene2DLayout(192f, 48f, Constant.WIDTHf /2,       0f, Align.bottom),     this)
        makeImage(skin["dialog_name_bg2"], Scene2DLayout( 89f, 18f, Constant.WIDTHf /2 - 80, 46f, Align.bottomLeft), this)
        setPosition(0f, -64f)

        conversationTextLabel = makeLabel(

                style  = skin["default"],
                text   = "Test Label",
                layout = LabelLayout(186f, 40f, Constant.WIDTHf/2-90, 44f, Align.topLeft, Align.topLeft, true),
                parent = this
        )

        conversationTitleLabel = makeLabel(

                style  = skin["default"],
                text   = "",
                layout = LabelLayout(84f, 16f, Constant.WIDTHf/2-78, 48f, Align.bottomLeft, Align.left),
                parent = this
        )
    }

    fun setContent(mainText: String = "", title: String = "")
    {
        conversationTextLabel.txt = mainText
        conversationTitleLabel.txt = title
    }
}