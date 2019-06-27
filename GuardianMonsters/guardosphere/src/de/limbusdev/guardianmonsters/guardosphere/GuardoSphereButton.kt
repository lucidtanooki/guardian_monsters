/**
 * Copyright (C) 2019 Georg Eckert - All Rights Reserved
 */

package de.limbusdev.guardianmonsters.guardosphere

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.scene2d.SubImageImageButton
import de.limbusdev.guardianmonsters.services.Services
import ktx.style.get

/**
 * GuardoSphereButton
 *
 * @author Georg Eckert 2017
 */

class GuardoSphereButton
(
        skin: Skin,
        guardian: AGuardian?
)
    : SubImageImageButton(skin, "button-gs-selection", construct(skin, guardian))
{
    companion object
    {
        private fun construct(skin: Skin, guardian: AGuardian?): Image
        {
            val drawable = when(guardian == null)
            {
                true  -> TextureRegionDrawable(skin.get<TextureRegion>("transparent"))
                false -> TextureRegionDrawable(Services.Media().getMonsterMiniSprite(guardian.speciesID, guardian.currentForm))
            }

            return Image(drawable)
        }
    }
}
