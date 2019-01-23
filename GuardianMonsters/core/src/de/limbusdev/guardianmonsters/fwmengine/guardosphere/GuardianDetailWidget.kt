package de.limbusdev.guardianmonsters.fwmengine.guardosphere

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.services.Services

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

    companion object
    {
        private const val WIDTH = 152f
        private const val HEIGHT = 180f
    }

    init
    {
        setSize(WIDTH, HEIGHT)
        val background = Image(skin.getDrawable("guardosphere-frame"))
        background.setSize(WIDTH, HEIGHT)
        background.setPosition(0f, 0f, Align.bottomLeft)
        addActor(background)

        monsterSprite = Image(skin.getDrawable("transparent"))
        monsterSprite.setSize(128f, 128f)
        monsterSprite.setPosition(12f, HEIGHT - 8, Align.topLeft)
        addActor(monsterSprite)

        name = Label("Name", skin, "white")
        name.setSize(92f, 20f)
        name.setPosition(12f, 20f, Align.bottomLeft)
        addActor(name)

        level = Label("Lvl 0", skin, "white")
        level.setSize(32f, 20f)
        level.setPosition(12f + 96f, 20f, Align.bottomLeft)
        addActor(level)
    }

    fun showDetails(guardian: AGuardian) {
        val region = Services.getMedia().getMonsterSprite(
                guardian.speciesDescription.id,
                guardian.abilityGraph.currentForm)
        monsterSprite.drawable = TextureRegionDrawable(region)
        name.setText(Services.getL18N().getGuardianNicknameIfAvailable(guardian))
        level.setText("Lvl " + guardian.individualStatistics.level)
    }
}
