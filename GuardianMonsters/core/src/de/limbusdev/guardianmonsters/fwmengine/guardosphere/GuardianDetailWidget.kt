package de.limbusdev.guardianmonsters.fwmengine.guardosphere

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.scene2d.lSetPosition
import de.limbusdev.guardianmonsters.scene2d.lSetSize
import de.limbusdev.guardianmonsters.services.Services
import ktx.actors.plus

/**
 * GuardianDetailWidget
 *
 * @author Georg Eckert 2017
 */
class GuardianDetailWidget(skin: Skin) : Group()
{
    private val monsterSprite: Image
    private val name: Label
    private val level: Label

    init
    {
        setSize(WIDTH, HEIGHT)

        val background = Image(skin.getDrawable("guardosphere-frame"))
        background
                .lSetSize(WIDTH, HEIGHT)
                .lSetPosition(0f, 0f, Align.bottomLeft)

        monsterSprite = Image(skin.getDrawable("transparent"))
        monsterSprite
                .lSetSize(128f, 128f)
                .lSetPosition(12f, HEIGHT - 8f, Align.topLeft)

        name = Label("Name", skin, "white")
        name
                .lSetSize(92f, 20f)
                .lSetPosition(12f, 20f, Align.bottomLeft)

        level = Label("Lvl 0", skin, "white")
        level
                .lSetSize(32f, 20f)
                .lSetPosition(12f + 96f, 20f, Align.bottomLeft)

        this+background
        this+monsterSprite
        this+name
        this+level
    }

    fun showDetails(guardian: AGuardian?)
    {
        if(guardian != null)
        {
            val region = Services.getMedia().getMonsterSprite(
                    guardian.speciesDescription.ID,
                    guardian.abilityGraph.currentForm)
            monsterSprite.drawable = TextureRegionDrawable(region)
            name.setText(Services.getL18N().getGuardianNicknameIfAvailable(guardian))
            level.setText("Lvl $guardian.individualStatistics.level")
        }
    }

    companion object
    {
        private const val TAG = "GuardianDetailWidget"
        private const val WIDTH = 152f
        private const val HEIGHT = 180f
    }
}
