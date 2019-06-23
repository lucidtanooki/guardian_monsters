package de.limbusdev.guardianmonsters.scene2d

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import ktx.style.get


//////////////////////////////////////////////////////////////////////////////////////////////////// LABEL
fun makeLabel(skin: Skin, text: String = "", parent: Group? = null) : Label
{
    return makeLabel(style = skin["default"], text = text, parent = parent)
}

fun makeLabel(style: Label.LabelStyle, text: String = "", parent: Group? = null) : Label
{
    val label = Label(text, style)
    parent?.addActor(label)
    return label
}

fun makeLabel(skin: Skin, style: String = "default", text: String = "", position: Position2D, parent: Group? = null) : Label
{
    return makeLabel(skin[style], text, position, parent)
}

fun makeLabel(style: Label.LabelStyle, text: String = "", position: Position2D, parent: Group? = null) : Label
{
    val label = makeLabel(style, text, parent)
    label.position = position
    return label
}

fun makeLabel(style: Label.LabelStyle, text: String = "", layout: Layout2D, parent: Group? = null) : Label
{
    val label = makeLabel(style, text, parent)
    label.setup = layout
    return label
}

fun makeLabel(skin: Skin, style: String = "default", text: String = "", layout: LabelLayout2D, parent: Group? = null) : Label
{
    return makeLabel(skin[style], text, layout, parent)
}

fun makeLabel(text: String = "", skin: Skin, style: String = "default", width: Float, height: Float, x: Float, y: Float, align: Int, parent: Group?) : Label
{
    val label = Label(text, skin, style)
    label.setup(width, height, x, y, align, parent)
    return label
}

fun makeLabel(style: Label.LabelStyle, text: String = "", layout: LabelLayout2D, parent: Group? = null) : Label
{
    val label = makeLabel(style, text, parent)
    label.setup = layout
    return label
}

//////////////////////////////////////////////////////////////////////////////////////////////////// IMAGE
fun makeImage(drawable: Drawable, parent: Group? = null) : Image
{
    val image =  Image(drawable)
    parent?.addActor(image)
    return image
}

fun makeImage(drawable: Drawable, position: Position2D, parent: Group? = null) : Image
{
    val image = makeImage(drawable)
    image.position = position
    return image
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


//////////////////////////////////////////////////////////////////////////////////////////////////// ImageButton
fun makeImageButton(style: ImageButton.ImageButtonStyle, position: Position2D, parent: Group? = null) : ImageButton
{
    val button = ImageButton(style)
    button.setPosition(position.x, position.y, position.align ?: Align.center)
    parent?.addActor(button)
    return button
}

fun makeImageButton(skin: Skin, style: String, position: Position2D, parent: Group? = null) : ImageButton
{
    return makeImageButton(skin[style], position, parent)
}

