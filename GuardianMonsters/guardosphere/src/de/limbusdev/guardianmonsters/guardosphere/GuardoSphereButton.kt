package de.limbusdev.guardianmonsters.guardosphere

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
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
    : SubImageImageButton
(
        skin,
        if (guardian == null) "button-gs-empty" else "button-gs",
        construct(guardian, skin)
) {
    companion object
    {
        private fun construct(guardian: AGuardian?, skin: Skin): Image
        {
            val drawable: TextureRegionDrawable

            if(guardian == null)
            {
                drawable = TextureRegionDrawable(skin.getRegion("transparent"))
            }
            else
            {
                drawable = TextureRegionDrawable(
                        Services.getMedia().getMonsterMiniSprite(guardian.speciesDescription.ID, guardian.abilityGraph.currentForm))
            }
            val miniSprite = Image(drawable)
            return miniSprite
        }
    }
}
