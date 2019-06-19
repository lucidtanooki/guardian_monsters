package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.LevelUpReport
import de.limbusdev.guardianmonsters.scene2d.AnimatedImage
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.guardianmonsters.ui.widgets.OverlayWidget
import de.limbusdev.utils.extensions.replaceOnClick


/**
 * LevelUpWidget
 *
 * @author Georg Eckert 2017
 *
 * TODO only show this at the end of a battle
 */
class LevelUpWidget(skin: Skin, guardian: AGuardian) : OverlayWidget(skin)
{
    private val monsterImg: Image

    init
    {
        val bg = Label("", skin, "paper")
        bg.setSize(300f, 180f)
        bg.setPosition((Constant.WIDTH / 2 - 150).toFloat(), 30f, Align.bottomLeft)
        addActor(bg)

        monsterImg = Image(Services.getMedia().getMonsterSprite(guardian.speciesID, guardian.abilityGraph.currentForm))
        monsterImg.setPosition(64f, 64f, Align.bottomLeft)
        addActor(monsterImg)

        val ok = ImageButton(skin, "button-back")
        ok.setPosition((Constant.WIDTH - 64 - 4).toFloat(), (32 + 4).toFloat(), Align.bottomRight)
        addActor(ok)
        ok.replaceOnClick { remove() }

        val guardianName = Services.getL18N().getGuardianNicknameIfAvailable(guardian)
        val info = Label(Services.getL18N().Battle().format("level_up", guardianName), skin, "default")
        info.setSize(140f, 32f)
        info.setPosition((128 + 64).toFloat(), 140f, Align.bottomLeft)
        info.setWrap(true)
        info.setAlignment(Align.topLeft, Align.topLeft)
        addActor(info)


        // Values
        val lvlUp = guardian.individualStatistics.latestLevelUpReport

        val values = Table()
        values.align(Align.topLeft)
        values.setSize(140f, 72f)
        values.setPosition(128f + 64f, 64f, Align.bottomLeft)

        val attributes = arrayOf("exp", "hp", "mp", "pstr", "pdef", "mstr", "mdef", "speed")
        val oldAttribVals = intArrayOf(
                lvlUp!!.oldLevel,
                lvlUp.oldStats.HP,
                lvlUp.oldStats.MP,
                lvlUp.oldStats.PDef,
                lvlUp.oldStats.PStr,
                lvlUp.oldStats.MStr,
                lvlUp.oldStats.MDef,
                lvlUp.oldStats.Speed)
        val newAttribVals = intArrayOf(
                lvlUp.newLevel,
                lvlUp.newStats.HP,
                lvlUp.newStats.MP,
                lvlUp.newStats.PDef,
                lvlUp.newStats.PStr,
                lvlUp.newStats.MStr,
                lvlUp.newStats.MDef,
                lvlUp.newStats.Speed)

        for (i in attributes.indices)
        {
            values.add(Image(skin.getDrawable("stats-symbol-" + attributes[i]))).size(16f, 16f)
            values.add(Label(Integer.toString(oldAttribVals[i]) + " > ", skin, "default")).height(16f)
            values.add(Label(Integer.toString(newAttribVals[i]), skin, "green")).height(16f).width(32f)
            if (i % 2 == 1) values.row()
        }

        addActor(values)

        val lvlUpAnimation = Animation(.15f, skin.getRegions("level-up-animation"))
        lvlUpAnimation.playMode = Animation.PlayMode.LOOP
        val ai = AnimatedImage(lvlUpAnimation)
        ai.setPosition((128 + 32).toFloat(), (128 - 32).toFloat(), Align.topLeft)
        addActor(ai)
    }
}
