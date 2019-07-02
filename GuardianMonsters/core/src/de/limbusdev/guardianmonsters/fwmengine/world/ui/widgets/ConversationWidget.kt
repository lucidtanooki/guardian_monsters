package de.limbusdev.guardianmonsters.fwmengine.world.ui.widgets

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
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
    private var wholeText = ""
    private lateinit var textSections : List<String>
    private var currentSection = -1
    private val nameBGImg               : Image
    private val conversationBGImg       : Image

    init
    {
        conversationBGImg = makeImage(skin["label-bg-paper-sand"], Scene2DLayout(200f, 60f, Constant.WIDTHf/2-90f,  2f), this)
        nameBGImg         = makeImage(skin["label-bg-paper-sand"], Scene2DLayout(120f, 22f, Constant.WIDTHf/2-90f, 64f), this)
        setPosition(0f, -86f)

        conversationTextLabel = makeLabel(

                style  = skin["default"],
                text   = "Test Label",
                layout = LabelLayout(192f, 52f, Constant.WIDTHf/2-86f, 58f, Align.topLeft, Align.topLeft, true),
                parent = this
        )

        conversationTitleLabel = makeLabel(

                style  = skin["default"],
                text   = "",
                layout = LabelLayout(112f, 16f, Constant.WIDTHf/2-86f, 82f, Align.topLeft, Align.topLeft),
                parent = this
        )
    }

    fun setContent(mainText: String = "", title: String = "")
    {
        currentSection = -1
        wholeText = mainText
        conversationTitleLabel.txt = title
        textSections = mainText.split("|")
        nextSection()

        if(title.isEmpty())
        {
            conversationTitleLabel.remove()
            nameBGImg.remove()
        }
        else
        {
            addActor(nameBGImg)
            addActor(conversationTitleLabel)
        }
    }

    /** @return false if the last section is already shown */
    fun nextSection() : Boolean
    {
        when(currentSection)
        {
            -1                      -> currentSection = 0
            textSections.size - 1   -> { currentSection = -1; return false }
            else                    -> currentSection++
        }
        conversationTextLabel.txt = textSections[currentSection]
        return true
    }
}