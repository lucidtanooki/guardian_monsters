package de.limbusdev.guardianmonsters.scene2d

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align

fun makeLabel(skin: Skin, style: String, layout: LabelLayout2D, text: String? = null, parent: Group? = null) : Label
{
    return makeLabel(skin, style, layout, text, parent, layout.wrap, layout.line)
}

fun makeLabel(skin: Skin, style: String, layout: Layout2D, text: String? = null, parent: Group? = null, wrap: Boolean? = null, line: Int? = null) : Label
{
    val label = makeLabel(text ?: "", skin, style, layout.width, layout.height, layout.x, layout.y, layout.align ?: Align.center, parent)
    if(wrap != null) { label.setWrap(wrap) }
    if(line != null) { label.setAlignment(layout.align ?: Align.center, line) }
    return label
}

fun makeLabel(text: String, skin: Skin, style: String, width: Float, height: Float, x: Float, y: Float, align: Int, parent: Group?) : Label
{
    val label = Label(text, skin, style)
    label.setup(width, height, x, y, align, parent)
    return label
}

fun makeImage(drawable: Drawable, layout: Layout2D, parent: Group? = null) : Image
{
    val image = Image(drawable)
    image.setSize(layout.width, layout.height)
    image.setPosition(layout.x, layout.y, layout.align ?: Align.center)
    parent?.addActor(image)
    return image
}

fun makeImage(drawable: String, skin: Skin, width: Float, height: Float, x: Float, y: Float, align: Int, parent: Group?) : Image
{
    val image = Image(skin.getDrawable(drawable))
    image.setSize(width, height)
    image.setPosition(x, y, align)
    parent?.addActor(image)
    return image
}

fun makeImageButton(skin: Skin, style: String, position: Position2D, parent: Group? = null) : ImageButton
{
    val button = ImageButton(skin, style)
    button.setPosition(position.x, position.y, position.align ?: Align.center)
    parent?.addActor(button)
    return button
}

