package de.limbusdev.guardianmonsters.guardosphere

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.services.Services
import ktx.actors.txt

/**
 * GuardianDetailWidget
 *
 * @author Georg Eckert 2017
 */
class GuardianDetailWidget(private val skin: Skin) : Group()
{
    companion object { const val TAG = "GuardianDetailWidget" }

    private val guardianPreview : Image
    private val name            : Label
    private val level           : Label

    init
    {
        setSize(GSFactory.GuardianDetailWidgetBP.WIDTH,
                GSFactory.GuardianDetailWidgetBP.HEIGHT)

        GSFactory.GuardianDetailWidgetBP.createBackgroundImg(skin, this)

        guardianPreview = GSFactory.GuardianDetailWidgetBP.createGuardianPreview(skin, this)
        name            = GSFactory.GuardianDetailWidgetBP.createNameLabel(skin, this)
        level           = GSFactory.GuardianDetailWidgetBP.createLevelLabel(skin, this)
    }

    private fun reset()
    {
        guardianPreview.drawable = skin.getDrawable("transparent")
        name.txt = ""
        level.txt = ""
    }

    fun showDetails(guardian: AGuardian?)
    {
        if(guardian == null) { reset() }
        else
        {
            val region = Services.Media().getMonsterSprite(guardian.speciesID, guardian.currentForm)
            guardianPreview.drawable = TextureRegionDrawable(region)
            name.setText(Services.I18N().getGuardianNicknameIfAvailable(guardian))
            level.setText("Lvl ${guardian.stats.level}")
        }
    }
}
