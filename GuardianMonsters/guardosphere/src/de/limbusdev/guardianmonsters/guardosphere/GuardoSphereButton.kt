package de.limbusdev.guardianmonsters.guardosphere

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.scene2d.SubImageImageButton
import de.limbusdev.guardianmonsters.services.Services

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
            val drawable: TextureRegionDrawable
            val media = Services.getMedia();

            if(guardian == null)
            {
                drawable = TextureRegionDrawable(skin.getRegion("transparent"))
            }
            else
            {
                val form = guardian.abilityGraph.currentForm
                val id = guardian.speciesDescription.ID
                drawable = TextureRegionDrawable(media.getMonsterMiniSprite(id, form))
            }

            return Image(drawable)
        }
    }
}
