/**
 * Copyright (C) 2019 Georg Eckert - All Rights Reserved
 */

package de.limbusdev.guardianmonsters.guardosphere

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align

import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.guardianmonsters.assets.paths.AssetPath
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.GuardoSphere
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.AHUD
import de.limbusdev.guardianmonsters.ui.widgets.ParticleEffectActor
import de.limbusdev.utils.extensions.replaceOnClick


import ktx.actors.plus
import ktx.actors.plusAssign
import ktx.actors.txt

/**
 * GuardoSphereHUD
 *
 * @author Georg Eckert 2017
 */
class GuardoSphereHUD
(
        skin: Skin,
        private val team: Team,
        private val guardoSphere: GuardoSphere
)
    : AHUD(skin)
{
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Properties
    private val particles      = ParticleEffectActor("guardosphere")

    // .................................................... widgets
    private val detailWidget   = GuardianDetailWidget(skin)
    private val choiceWidget   = GuardoSphereChoiceWidget(skin, guardoSphere, team)
    private val statusWidget   = GuardoSphereStatWidget(skin)

    // .................................................... buttons
    private val statViewToggle = ImageButton(skin, "button-gs-stats")
    private val backButton     = ImageButton(skin, "button-gs-back")
    private val nextButton     = ImageButton(skin, "button-gs-forth")
    private val exitButton     = ImageButton(skin, "button-gs-exit")

    // .................................................... labels
    private val pageLabel      = Label("001 .. 035", skin, "white")

    // .................................................... other
    private var currentPage    = 0


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Initializer
    init
    {
        // ................................................ layout
        // Define Actors
        particles.start()

        // Position Actors
        particles.setPosition(particlesX, particlesY, particlesAlign)
        detailWidget.setPosition(detailsX, detailsY, detailsAlign)
        choiceWidget.setPosition(sphereChoiceX, sphereChoiceY, sphereChoiceAlign)

        statViewToggle.setPosition(statToggleX, statToggleY, statToggleAlign)
        backButton.setPosition(backX, backY, backAlign)
        nextButton.setPosition(nextX, nextY, nextAlign)
        exitButton.setPosition(exitX, exitY, exitAlign)

        pageLabel.setPosition(backX + 34f, backY + 34f, Align.bottom)

        statusWidget.setPosition(statX, statY, statAlign)
        statusWidget.isVisible = false

        // Assemble Hierarchy
        stage+=particles
        stage+=detailWidget
        stage+=choiceWidget
        stage+=statViewToggle
        stage+=backButton
        stage+=nextButton
        stage+=exitButton
        stage+=statusWidget
        stage+=pageLabel


        // Define callbacks
        choiceWidget.sphereCallback = { updateDetails(guardoSphere[it]) }
        choiceWidget.teamCallback   = { updateDetails(team[it])         }

        backButton.replaceOnClick {

            currentPage = when(currentPage)
            {
                0 -> 9
                else -> --currentPage
            }
            updatePage()
        }

        nextButton.replaceOnClick {

            currentPage = when(currentPage)
            {
                9 -> 0
                else -> ++currentPage
            }
            updatePage()
        }

        statViewToggle.replaceOnClick {

            statusWidget.isVisible = statViewToggle.isChecked
        }

        exitButton.replaceOnClick { super.goToPreviousScreen() }
    }


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Methods

    private fun updatePage()
    {
        val a = (currentPage*35+1).toString().padStart(3, '0')
        val o = ((currentPage+1)*35).toString().padStart(3, '0')
        pageLabel.txt = "$a .. $o"

        choiceWidget.refresh(currentPage)
    }

    private fun updateDetails(guardian: AGuardian?)
    {
        detailWidget.showDetails(guardian)
        statusWidget.initialize(guardian)
    }


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Screen
    override fun reset() {}

    override fun show() = startBGMusic()

    override fun hide() = stopBGMusic()

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Static
    private companion object
    {
        const val PADDING           = 8f

        const val particlesX        = 0f
        const val particlesY        = 0f
        const val particlesAlign    = Align.bottomLeft

        const val detailsX          = Constant.WIDTH - PADDING
        const val detailsY          = Constant.HEIGHT - PADDING
        const val detailsAlign      = Align.topRight

        const val sphereChoiceX     = PADDING
        const val sphereChoiceY     = Constant.HEIGHT - PADDING
        const val sphereChoiceAlign = Align.topLeft

        const val statX             = Constant.WIDTH - PADDING
        const val statY             = Constant.RES_Y - PADDING
        const val statAlign         = Align.topRight

        const val backX             = 268f
        const val backY             = 4f
        const val backAlign         = Align.bottomLeft

        const val nextX             = backX + 36f
        const val nextY             = 4f
        const val nextAlign         = Align.bottomLeft

        const val exitX             = Constant.WIDTH - PADDING
        const val exitY             = 4f
        const val exitAlign         = Align.bottomRight

        const val statToggleX       = Constant.WIDTH - PADDING - 36f
        const val statToggleY       = 4f
        const val statToggleAlign   = Align.bottomRight

        fun startBGMusic()  = Services.getAudio().playLoopMusic(AssetPath.Audio.Music.GUARDOSPHERE)
        fun stopBGMusic()   = Services.getAudio().stopMusic(AssetPath.Audio.Music.GUARDOSPHERE)
    }
}
