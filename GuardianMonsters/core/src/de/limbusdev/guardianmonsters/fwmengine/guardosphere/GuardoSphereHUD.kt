package de.limbusdev.guardianmonsters.fwmengine.guardosphere

import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.assets.paths.AssetPath
import de.limbusdev.guardianmonsters.guardians.monsters.GuardoSphere
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.AHUD
import de.limbusdev.guardianmonsters.ui.widgets.ParticleEffectActor
import ktx.actors.plus

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
    private var detailWidget = GuardianDetailWidget(skin)
    private var guardianButtonGroup = ButtonGroup<Button>()
    private var guardoSphereChoiceWidget: GuardoSphereChoiceWidget
    private var toggleGuardianStatView: TextButton
    private var guardianStatusWidget: GuardoSphereStatWidget

    init
    {
        guardoSphereChoiceWidget = GuardoSphereChoiceWidget(skin, guardoSphere, team, guardianButtonGroup)
        toggleGuardianStatView = TextButton("?", skin, "button-gs-default")
        guardianStatusWidget = GuardoSphereStatWidget(skin)

        layout(skin)

        guardoSphereChoiceWidget.sphereCallback = { detailWidget.showDetails(guardoSphere.get(it)) }
    }

    private fun layout(skin: Skin)
    {
        // Define Actors
        val particles = ParticleEffectActor("guardosphere")
        particles.start()

        // Position Actors
        particles.setPosition(particlesX, particlesY, particlesAlign)
        detailWidget.setPosition(detailsX, detailsY, detailsAlign)
        guardoSphereChoiceWidget.setPosition(sphereChoiceX, sphereChoiceY, sphereChoiceAlign)

        toggleGuardianStatView.setSize(statToggleWidth, statToggleHeight)
        toggleGuardianStatView.setPosition(statToggleX, statToggleY, statToggleAlign)

        guardianStatusWidget.setPosition(statX, statY, statAlign)

        // Configure Actors
        guardianButtonGroup.setMaxCheckCount(1)
        guardianButtonGroup.setMinCheckCount(1)

        // Assemble Hierarchy
        stage+particles
        stage+detailWidget
        stage+guardoSphereChoiceWidget
        stage+toggleGuardianStatView
        stage+guardianStatusWidget
    }

    override fun reset() {}

    override fun show()
    {
        startBGMusic()
    }

    override fun hide()
    {
        stopBGMusic()
    }

    companion object
    {
        const val particlesX = 0f
        const val particlesY = 0f
        const val particlesAlign = Align.bottomLeft

        const val detailsX = Constant.WIDTH - 8f
        const val detailsY = Constant.HEIGHT - 8f
        const val detailsAlign = Align.topRight

        const val sphereChoiceX = 8f
        const val sphereChoiceY = Constant.HEIGHT - 8f
        const val sphereChoiceAlign = Align.topLeft

        const val statToggleWidth = 40f
        const val statToggleHeight = 40f
        const val statToggleX = Constant.WIDTH - 8f
        const val statToggleY = 8f
        const val statToggleAlign = Align.bottomRight

        const val statX = Constant.WIDTH - 8f
        const val statY = Constant.RES_Y - 8f
        const val statAlign = Align.topRight

        fun startBGMusic()  = Services.getAudio().playLoopMusic(AssetPath.Audio.Music.GUARDOSPHERE)
        fun stopBGMusic()   = Services.getAudio().stopMusic(AssetPath.Audio.Music.GUARDOSPHERE)
    }
}
