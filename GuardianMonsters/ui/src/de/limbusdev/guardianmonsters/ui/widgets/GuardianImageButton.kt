/**
 * Copyright (C) 2019 Georg Eckert - All Rights Reserved
 */

package de.limbusdev.guardianmonsters.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

import de.limbusdev.guardianmonsters.guardians.monsters.Guardian
import de.limbusdev.guardianmonsters.scene2d.SubImageImageButton
import de.limbusdev.guardianmonsters.services.Services

/**
 * GuardianImageButton
 *
 * @author Georg Eckert 2017
 */

class GuardianImageButton
(
        skin: Skin,
        style: String,
        guardian: Guardian
)
    : SubImageImageButton(skin, style, construct(guardian))
{
    companion object
    {
        private fun construct(guardian: Guardian): Image
        {
            val id = guardian.speciesDescription.ID
            val form = guardian.abilityGraph.currentForm
            val drawable = TextureRegionDrawable(Services.Media().getMonsterMiniSprite(id, form))
            return Image(drawable)
        }
    }
}