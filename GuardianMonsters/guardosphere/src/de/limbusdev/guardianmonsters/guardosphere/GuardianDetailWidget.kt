package de.limbusdev.guardianmonsters.guardosphere

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.services.Services
import ktx.actors.plus
import ktx.actors.plusAssign
import ktx.actors.txt

/**
 * GuardianDetailWidget
 *
 * @author Georg Eckert 2017
 */
class GuardianDetailWidget(private val skin: Skin) : Group()
{
    private val monsterSprite: Image
    private val name: Label
    private val level: Label

    init
    {
        setSize(WIDTH, HEIGHT)

        val background = Image(skin.getDrawable("guardosphere-frame"))
        background.setSize(WIDTH, HEIGHT)
        background.setPosition(0f, 0f, Align.bottomLeft)

        monsterSprite = Image(skin, "transparent")
        monsterSprite.setSize(128f, 128f)
        monsterSprite.setPosition(12f, HEIGHT - 8f, Align.topLeft)

        name = Label("Name", skin, "white")
        name.setSize(WIDTH - 2*PADDING - 32f, 22f)
        name.setPosition(PADDING, PADDING, Align.bottomLeft)

        level = Label("Lvl 0", skin, "white")
        level.setSize(32f, 22f)
        level.setPosition(WIDTH - PADDING, PADDING, Align.bottomRight)

        this+=background
        this+=monsterSprite
        this+=name
        this+=level
    }

    private fun reset()
    {
        monsterSprite.drawable = skin.getDrawable("transparent")
        name.txt = ""
        level.txt = ""
    }

    fun showDetails(guardian: AGuardian?)
    {
        if(guardian == null) reset()
        else
        {
            val region = Services.getMedia().getMonsterSprite(
                    guardian.speciesDescription.ID,
                    guardian.abilityGraph.currentForm)
            monsterSprite.drawable = TextureRegionDrawable(region)
            name.setText(Services.getL18N().getGuardianNicknameIfAvailable(guardian))
            level.setText("Lvl ${guardian.individualStatistics.level}")
        }
    }

    companion object
    {
        private const val TAG = "GuardianDetailWidget"
        private const val WIDTH = 152f
        private const val HEIGHT = 180f
        private const val PADDING = 6f
    }
}
