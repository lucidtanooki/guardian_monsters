package de.limbusdev.guardianmonsters.battle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array

import de.limbusdev.guardianmonsters.battle.ui.widgets.LevelUpWidget
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.battle.BattleResult
import de.limbusdev.guardianmonsters.guardians.items.Item
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.ISpeciesDescriptionService
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.AHUD
import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.utils.extensions.replaceOnClick

import ktx.actors.plus
import ktx.actors.plusAssign


/**
 * BattleResultHUD
 *
 * @author Georg Eckert 2017
 */

class BattleResultHUD
(
        skin: Skin,
        private val team: Team,
        private var result: BattleResult
)
    : AHUD(skin)
{

    private lateinit var table: Table
    private lateinit var apply: Button
    private lateinit var next: Button
    private lateinit var reachedNextLevel: Array<AGuardian>
    private lateinit var group: Group

    init
    {
        constructLayout()
        constructMonsterTable(team, result)
        Gdx.input.inputProcessor = stage
    }

    private fun constructMonsterTable(team: Team, result: BattleResult)
    {
        table.clear()
        for(key in team.keys())
        {
            val guardian = team.get(key)

            // TODO currentForm
            table.add(
                    monsterFace(guardian.speciesID, guardian.abilityGraph.currentForm)
            ).left()
            table.add(
                    Label(commonName(guardian.speciesID, 0), skin, "default")
            ).left()
            table.add(
                    Image(skin.getDrawable("symbol-exp"))
            ).left()
            table.add(
                    Label(Integer.toString(result.getGainedEXP(guardian)), skin, "default")
            ).width(48f).left()
            table.add(
                    Image(skin.getDrawable("symbol-levelup"))
            ).left()
            table.add(
                    Label(Integer.toString(guardian.individualStatistics.remainingLevelUps), skin, "default")
            ).width(96f).left()
            table.row().space(4f)
        }
    }

    private fun constructLayout()
    {
        val container = Container<Image>()
        container.background = skin.getDrawable(bgDrawable)
        container.setSize(bgWidth, bgHeight)
        container.setPosition(bgX, bgY, bgAlign)
        stage+=container

        group = Group()
        group.setSize(groupWidth, groupHeight)
        group.setPosition(groupX, groupY, groupAlign)
        stage+=group

        val heading = Label(Services.I18N().Battle().get("results"), skin, "default")
        heading.setAlignment(headingLabelAlign, headingLineAlign)
        heading.setPosition(headingX, headingY, headingAlign)
        group+=heading

        table = Table()
        table.align(tableInnerAlign)
        table.setSize(tableWidth, tableHeight)
        table.setPosition(tableX, tableY, tableAlign)
        group+=table

        apply = TextButton(Services.I18N().General().get("apply"), skin, "default")
        apply.setSize(applyWidth, applyHeight)
        apply.setPosition(applyX, applyY, applyAlign)
        group+=apply

        apply.replaceOnClick {

            println("BattleResultScreen: APPLY pressed")
            reachedNextLevel = result.applyGainedEXPtoAll()
            result = BattleResult(team, Array<Item>()) // TODO droppped items
            constructMonsterTable(team, result)
            apply.remove()
            group+=next
            println(apply.isDisabled)
        }

        next = TextButton(Services.I18N().General().get("next"), skin, "default")
        next.setSize(nextWidth, nextHeight)
        next.setPosition(nextX, nextY, nextAlign)

        next.replaceOnClick {

            println("BattleResultScreen: NEXT pressed")
            if(reachedNextLevel.size > 0)
            {
                reachedNextLevel.first().individualStatistics.levelUp()
                val lvlUpWidget = LevelUpWidget(skin, reachedNextLevel.first())
                stage+=lvlUpWidget
                if(reachedNextLevel.first().individualStatistics.remainingLevelUps == 0)
                {
                    reachedNextLevel.removeIndex(0)
                }
                constructMonsterTable(team, result)
            }
            else
            {
                Services.ScreenManager().popScreen()
            }
        }
    }

    override fun reset() {}

    override fun show() {}

    companion object
    {
        const val bgDrawable = "label-bg-paper"
        const val bgWidth = Constant.WIDTH - 2f
        const val bgHeight = Constant.HEIGHT - 2f
        const val bgX = 1f
        const val bgY = 1f
        const val bgAlign = Align.bottomLeft

        const val groupWidth = Constant.WIDTH - 8f
        const val groupHeight = Constant.HEIGHT - 8f
        const val groupX = 4f
        const val groupY = 4f
        const val groupAlign = Align.bottomLeft

        const val headingLabelAlign = Align.topLeft
        const val headingLineAlign = Align.topLeft
        const val headingX = 4f
        const val headingY = groupHeight - 4f
        const val headingAlign = Align.topLeft

        const val tableInnerAlign = Align.topLeft
        const val tableWidth = groupWidth * 3f/4f
        const val tableHeight = groupHeight * 4f/5f
        const val tableX = 4f
        const val tableY = 8f
        const val tableAlign = Align.bottomLeft

        const val applyWidth = 72f
        const val applyHeight = 32f
        const val applyX = groupWidth - 4f
        const val applyY = 4f
        const val applyAlign = Align.bottomRight

        const val nextWidth = applyWidth
        const val nextHeight = applyHeight
        const val nextX = applyX
        const val nextY = applyY
        const val nextAlign = applyAlign

        fun species() = GuardiansServiceLocator.species
        fun monsterFace(id: Int, form: Int) = Services.Media().getMonsterFace(id, form)
        fun commonName(id: Int, form: Int) = Services.I18N().Guardians().get(species().getCommonNameById(id, form))
    }
}