package de.limbusdev.guardianmonsters.guardosphere

import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.guardianmonsters.assets.paths.AssetPath
import de.limbusdev.guardianmonsters.guardians.monsters.GuardoSphere
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.AHUD
import de.limbusdev.guardianmonsters.ui.widgets.ParticleEffectActor
import ktx.actors.onClick
import ktx.actors.plus
import ktx.actors.txt

/**
 * GuardoSphereHUD
 *
 * @author Georg Eckert 2017
 */
class GuardoSphereHUD
(
        skin: Skin,
        team: Team,
        private val guardoSphere: GuardoSphere
)
    : AHUD(skin)
{
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Properties
    private var detailWidget             = GuardianDetailWidget(skin)
    private var guardoSphereChoiceWidget = GuardoSphereChoiceWidget(skin, guardoSphere, team)
    private var toggleGuardianStatView   = TextButton("?", skin, "button-gs-default")
    private var guardianStatusWidget     = GuardoSphereStatWidget(skin)
    private val backButton               = ImageButton(skin, "button-gs-back")
    private val nextButton               = ImageButton(skin, "button-gs-forth")
    private val pageLabel                = Label("001 .. 035", skin, "white")
    private var currentPage              = 0


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Initializer
    init
    {
        // ................................................ layout
        // Define Actors
        val particles = ParticleEffectActor("guardosphere")
        particles.start()

        // Position Actors
        particles.setPosition(particlesX, particlesY, particlesAlign)
        detailWidget.setPosition(detailsX, detailsY, detailsAlign)
        guardoSphereChoiceWidget.setPosition(sphereChoiceX, sphereChoiceY, sphereChoiceAlign)

        toggleGuardianStatView.setSize(statToggleWidth, statToggleHeight)
        toggleGuardianStatView.setPosition(statToggleX, statToggleY, statToggleAlign)

        backButton.setPosition(backX, backY, backAlign)
        nextButton.setPosition(nextX, nextY, nextAlign)
        pageLabel.setPosition(backX + 34f, backY + 34f, Align.bottom)

        guardianStatusWidget.setPosition(statX, statY, statAlign)

        // Assemble Hierarchy
        stage+particles
        stage+detailWidget
        stage+guardoSphereChoiceWidget
        stage+toggleGuardianStatView
        stage+backButton
        stage+nextButton
        stage+guardianStatusWidget
        stage+pageLabel

        guardoSphereChoiceWidget.sphereCallback = { detailWidget.showDetails(guardoSphere[it]) }

        backButton.onClick {

            currentPage = when(currentPage)
            {
                0 -> 9
                else -> --currentPage
            }
            updatePage()
        }

        nextButton.onClick {

            currentPage = when(currentPage)
            {
                9 -> 0
                else -> ++currentPage
            }
            updatePage()
        }
    }


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Methods

    private fun updatePage()
    {
        val a = (currentPage*35+1).toString().padStart(3, '0')
        val o = ((currentPage+1)*35).toString().padStart(3, '0')
        pageLabel.txt = "$a .. $o"

        guardoSphereChoiceWidget.refresh(currentPage)
    }


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Screen
    override fun reset() {}

    override fun show() = startBGMusic()

    override fun hide() = stopBGMusic()

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Static
    private companion object
    {
        const val particlesX        = 0f
        const val particlesY        = 0f
        const val particlesAlign    = Align.bottomLeft

        const val detailsX          = Constant.WIDTH - 8f
        const val detailsY          = Constant.HEIGHT - 8f
        const val detailsAlign      = Align.topRight

        const val sphereChoiceX     = 8f
        const val sphereChoiceY     = Constant.HEIGHT - 8f
        const val sphereChoiceAlign = Align.topLeft

        const val statToggleWidth   = 40f
        const val statToggleHeight  = 40f
        const val statToggleX       = Constant.WIDTH - 8f
        const val statToggleY       = 8f
        const val statToggleAlign   = Align.bottomRight

        const val statX             = Constant.WIDTH - 8f
        const val statY             = Constant.RES_Y - 8f
        const val statAlign         = Align.topRight

        const val backX             = 268f
        const val backY             = 4f
        const val backAlign         = Align.bottomLeft

        const val nextX             = backX + 36
        const val nextY             = 4f
        const val nextAlign         = Align.bottomLeft

        fun startBGMusic()  = Services.getAudio().playLoopMusic(AssetPath.Audio.Music.GUARDOSPHERE)
        fun stopBGMusic()   = Services.getAudio().stopMusic(AssetPath.Audio.Music.GUARDOSPHERE)
    }
}
