package de.limbusdev.guardianmonsters.guardosphere

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import de.limbusdev.guardianmonsters.scene2d.LabelLayout
import de.limbusdev.guardianmonsters.scene2d.Scene2DLayout
import de.limbusdev.guardianmonsters.scene2d.makeImage
import de.limbusdev.guardianmonsters.scene2d.makeLabel
import ktx.style.get

/** Guardo Sphere Factory Methods */
object GSFactory
{
    // --------------------------------------------------------------------------------------------- Layout Information
    // ............................................................ Guardian Detail Widget Blueprint
    object GuardianDetailWidgetBP
    {
        const val WIDTH = 152f
        const val HEIGHT = 180f
        const val PADDING = 6f
        const val PREVIEW_SIZE = 128f

        /** Creates the widget background: 152x180 */
        fun createBackgroundImg(skin: Skin, parent: Group? = null) : Image
        {
            return makeImage(

                    drawable = skin["guardosphere-frame"],
                    layout = Scene2DLayout(WIDTH, HEIGHT),
                    parent = parent
            )
        }

        /** Creates a preview window: 128x128 */
        fun createGuardianPreview(skin: Skin, parent: Group? = null) : Image
        {
            return makeImage(

                    drawable = skin["transparent"],
                    layout = Scene2DLayout(PREVIEW_SIZE, PREVIEW_SIZE, 12f, HEIGHT-8, Align.topLeft),
                    parent = parent
            )
        }

        fun createNameLabel(skin: Skin, parent: Group? = null) : Label
        {
            return makeLabel(

                    style  = skin["white"],
                    text   = "     ",
                    layout = LabelLayout(WIDTH - 2* PADDING - 32f, 22f, PADDING, PADDING),
                    parent = parent
            )
        }

        fun createLevelLabel(skin: Skin, parent: Group? = null) : Label
        {
            return makeLabel(

                    style  = skin["white"],
                    text   = "     ",
                    layout = LabelLayout(32f, 22f, WIDTH - PADDING, PADDING, Align.bottomRight),
                    parent = parent
            )
        }
    }


}