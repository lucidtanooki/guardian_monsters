package de.limbusdev.guardianmonsters.scene2d

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align

/**
 * SubImageImageButton
 *
 * @author Georg Eckert 2017
 */

open class SubImageImageButton
(
        skin: Skin,
        style: String,
        image: Image
)
    : ImageButton(skin, style)
{

    init
    {
        image.setPosition(width / 2, height / 2, Align.center)
        this.addActor(image)
    }
}