package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align

/**
 * Created by georg on 11.12.16.
 */

class ButtonWithImage : TextButton
{
    private var childImage: Image? = null

    constructor(text: String, skin: Skin) : super(text, skin) {}

    constructor(text: String, skin: Skin, styleName: String) : super(text, skin, styleName) {}

    constructor(text: String, style: TextButton.TextButtonStyle) : super(text, style) {}

    private fun setUp()
    {
        childImage = Image()
        childImage!!.setAlign(Align.left)
        childImage!!.setSize(16f, 16f)
        childImage!!.setPosition(16f, 16f, Align.center)
        addActor(childImage!!)
    }

    fun setChildImage(drawable: Drawable)
    {
        setUp()
        this.childImage!!.drawable = drawable
    }

    override fun setSize(width: Float, height: Float)
    {
        if (childImage == null) childImage = Image()
        childImage!!.setSize(width / getWidth() * childImage!!.width, width / getWidth() * childImage!!.height)
        childImage!!.setPosition(childImage!!.x * width / getWidth(), childImage!!.y * height / getHeight())
        super.setSize(width, height)
    }

    override fun setScale(scaleXY: Float)
    {
        super.setScale(scaleXY)
        childImage!!.setScale(scaleXY)
    }
}
