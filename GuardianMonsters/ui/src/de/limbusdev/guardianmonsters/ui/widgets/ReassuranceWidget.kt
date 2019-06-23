package de.limbusdev.guardianmonsters.ui.widgets

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align

import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.Constant

class ReassuranceWidget(skin: Skin = Services.UI().inventorySkin) : Group()
{
    var buttonYes: TextButton
    var buttonNo: TextButton
    var question: Label

    init
    {
        setSize(Constant.WIDTH.toFloat(), Constant.HEIGHT.toFloat())

        val bg = Image(skin.getDrawable("black-a80"))
        bg.setSize(Constant.WIDTH.toFloat(), Constant.HEIGHT.toFloat())
        bg.setPosition(0f, 0f, Align.bottomLeft)
        addActor(bg)

        question = Label(Services.I18N().Inventory().get("reassurance"), skin, "paper")
        question.setSize(292f, 64f)
        question.setPosition((Constant.WIDTH / 2 - 292 / 2).toFloat(), (Constant.HEIGHT / 2 - 32).toFloat(), Align.bottomLeft)
        addActor(question)

        buttonNo = TextButton(Services.I18N().General().get("no"), skin, "button-sandstone")
        buttonNo.setSize(64f, 24f)
        buttonNo.setPosition((Constant.WIDTH / 2 + 2).toFloat(), 40f, Align.bottomLeft)
        buttonNo.addListener(SimpleClickListener { remove() })
        addActor(buttonNo)

        buttonYes = TextButton(Services.I18N().General().get("yes"), skin, "button-sandstone")
        buttonYes.setSize(64f, 24f)
        buttonYes.setPosition((Constant.WIDTH / 2 - 2).toFloat(), 40f, Align.bottomRight)
        addActor(buttonYes)
    }
}
