package de.limbusdev.guardianmonsters.guardosphere

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import de.limbusdev.guardianmonsters.scene2d.LabelLayout
import de.limbusdev.guardianmonsters.scene2d.Scene2DLayout
import de.limbusdev.guardianmonsters.scene2d.makeImage
import de.limbusdev.guardianmonsters.scene2d.makeLabel
import ktx.scene2d.table
import ktx.style.get

/** Guardo Sphere Factory Methods */
internal object GSFactory
{
    // --------------------------------------------------------------------------------------------- BLUEPRINTS
    // ....................................................... Guardo Sphere Choice Widget Blueprint
    internal object GuardoSphereChoiceWidgetBP
    {
        const val WIDGET_WIDTH = 252f
        const val WIDGET_HEIGHT = 6*32f + 16f + 16f + 4f
        const val SPHERE_AREA_HEIGHT = 180f
        const val TEAM_AREA_HEIGHT = 32f+16f

        fun createSphereBackgroundImg(skin: Skin, parent: Group? = null) : Image
        {
            return makeImage(

                    drawable = skin["guardosphere-frame"],
                    layout = Scene2DLayout(WIDGET_WIDTH, SPHERE_AREA_HEIGHT, 0f, WIDGET_HEIGHT, Align.topLeft),
                    parent = parent
            )
        }

        fun createTeamBackgroundImg(skin: Skin, parent: Group? = null) : Image
        {
            return makeImage(

                    drawable = skin["guardosphere-frame"],
                    layout = Scene2DLayout(WIDGET_WIDTH, TEAM_AREA_HEIGHT),
                    parent = parent
            )
        }

        fun createGuardoSphereGrid(parent: Group? = null) : Table
        {
            val tbl = table {

                setSize(7*32f, 5*32f + 16f + 32f)
                setPosition(14f, 10f)
                align(Align.bottomLeft)
            }
            parent?.addActor(tbl)
            return tbl
        }
    }


    // ............................................................ Guardian Detail Widget Blueprint
    internal object GuardianDetailWidgetBP
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