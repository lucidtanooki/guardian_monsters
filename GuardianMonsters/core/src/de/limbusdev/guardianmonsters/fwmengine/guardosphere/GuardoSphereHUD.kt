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
import de.limbusdev.guardianmonsters.scene2d.lSetPosition
import de.limbusdev.guardianmonsters.scene2d.lSetSize
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

        val teamWidget = GuardoSphereTeamWidget(skin, team, guardianButtonGroup)

        teamWidget.setPosition(8f,8f, Align.bottomLeft)
        teamWidget.callback = { teamPosition -> detailWidget.showDetails(team.get(teamPosition)) }

        guardoSphereChoiceWidget.sphereCallback = {

            spherePosition ->
            if (guardoSphere.get(spherePosition) != null)
            {
                detailWidget.showDetails(guardoSphere.get(spherePosition))
            }
        }

        //stage+teamWidget
    }

    private fun layout(skin: Skin) {

        // Define Actors
        val particles = ParticleEffectActor("guardosphere")
        particles.start()

        // Position Actors
        particles.setPosition(0f, 0f, Align.bottomLeft)
        detailWidget.setPosition((Constant.WIDTH - 8).toFloat(), (Constant.HEIGHT - 8).toFloat(), Align.topRight)
        guardoSphereChoiceWidget.setPosition(8f, (Constant.HEIGHT - 8).toFloat(), Align.topLeft)

        toggleGuardianStatView
                .lSetSize(40f, 40f)
                .lSetPosition(Constant.WIDTH - 8f, 8f, Align.bottomRight)
        guardianStatusWidget.setPosition((Constant.WIDTH - 8).toFloat(), (Constant.RES_Y - 8).toFloat(), Align.topRight)

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
        Services.getAudio().playLoopMusic(AssetPath.Audio.Music.GUARDOSPHERE)
    }

    override fun hide()
    {
        Services.getAudio().stopMusic(AssetPath.Audio.Music.GUARDOSPHERE)
    }
}
