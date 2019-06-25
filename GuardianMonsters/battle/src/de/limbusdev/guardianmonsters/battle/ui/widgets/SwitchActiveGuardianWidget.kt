/*
 * *************************************************************************************************
 * Copyright (c) 2018. limbusdev (Georg Eckert) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Georg Eckert <georg.eckert@limbusdev.de>
 * *************************************************************************************************
 */

package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align

import de.limbusdev.guardianmonsters.guardians.battle.CombatTeam
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.scene2d.replaceOnClick
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.guardianmonsters.ui.widgets.ATeamChoiceWidget
import de.limbusdev.guardianmonsters.ui.widgets.GuardianStatusWidget
import de.limbusdev.guardianmonsters.ui.widgets.TeamCircleWidget
import de.limbusdev.guardianmonsters.ui.widgets.TiledImage
import ktx.style.get


/**
 * SwitchActiveGuardianWidget
 *
 * @author Georg Eckert 2018
 */

class SwitchActiveGuardianWidget() : BattleWidget()
{
    // .................................................................................. Properties
    private val guardianStatusWidget    : GuardianStatusWidget
    private val teamChoiceWidget        : ATeamChoiceWidget
    private var backButtonCallback      : () -> Unit = {}
    private var switchButtonCallback    : () -> Unit = {}
    private val guardianImg             : Image
    private val switchButton            : Button
    private val backButton              : Button

    val chosenSubstitute: Int
        get() = teamChoiceWidget.currentPosition


    // ................................................................................ Constructors
    init
    {
        val battleSkin = Services.UI().battleSkin
        val inventorySkin = Services.UI().inventorySkin

        setSize(Constant.WIDTHf, Constant.HEIGHTf)
        val bgImg = TiledImage(inventorySkin["bg-pattern-3"], 27, 16)
        bgImg.setPosition(0f, 0f, Align.bottomLeft)
        addActor(bgImg)

        guardianStatusWidget = GuardianStatusWidget()
        guardianStatusWidget.setPosition(2f, Constant.HEIGHTf, Align.topLeft)
        addActor(guardianStatusWidget)

        backButton = ImageButton(inventorySkin, "button-back")
        backButton.setPosition((Constant.WIDTHf - 2), 2f, Align.bottomRight)
        addActor(backButton)

        switchButton = ImageButton(inventorySkin, "button-switch")
        switchButton.setPosition(2f, 2f, Align.bottomLeft)
        addActor(switchButton)

        teamChoiceWidget = TeamCircleWidget(Services.UI().inventorySkin) {}
        teamChoiceWidget.setPosition(Constant.WIDTHf - 2, Constant.HEIGHTf / 2, Align.right)
        addActor(teamChoiceWidget)

        val guardianView = Group()
        guardianView.setSize(140f, Constant.HEIGHTf - 36)
        guardianView.setPosition(Constant.WIDTHf / 2, Constant.HEIGHTf, Align.top)
        val monsterViewBg = Image(inventorySkin.get<Drawable>("menu-col-bg"))
        monsterViewBg.setPosition(2f, 2f, Align.bottomLeft)
        guardianView.addActor(monsterViewBg)
        guardianImg = Image()
        guardianImg.setSize(128f, 128f)
        guardianImg.setPosition(6f, 202f, Align.topLeft)
        guardianView.addActor(guardianImg)
        addActor(guardianView)
    }


    // ..................................................................................... Methods
    fun setCallbacks(onBack: () -> Unit, onSwitch: () -> Unit)
    {
        this.backButtonCallback = onBack
        this.switchButtonCallback = onSwitch

        backButton.replaceOnClick(onBack)
        switchButton.replaceOnClick(onSwitch)
    }

    fun initialize(guardian: AGuardian, team: Team, combatTeam: CombatTeam)
    {
        guardianStatusWidget.initialize(guardian)
        teamChoiceWidget.initialize(team, team.getPosition(guardian))
        activateGuardian(guardian, team, combatTeam)

        val circleMenuCallbacks = { nr: Int -> activateGuardian(team[nr], team, combatTeam) }

        teamChoiceWidget.setHandler(circleMenuCallbacks)
    }

    private fun activateGuardian(guardian: AGuardian, team: Team, combatTeam: CombatTeam)
    {
        if (!combatTeam.isMember(guardian) && guardian.stats.isFit)
        {
            switchButton.touchable = Touchable.enabled
            switchButton.color = Color.WHITE
        }
        else
        {
            switchButton.touchable = Touchable.disabled
            switchButton.color = Color.GRAY
        }

        guardianStatusWidget.initialize(guardian)
        val sprite = Services.Media().getMonsterSprite(guardian.speciesID, guardian.currentForm)
        guardianImg.drawable = TextureRegionDrawable(sprite)
    }
}
