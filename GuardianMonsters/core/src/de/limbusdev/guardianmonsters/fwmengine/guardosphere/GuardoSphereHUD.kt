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
import de.limbusdev.guardianmonsters.ui.widgets.Callback
import de.limbusdev.guardianmonsters.ui.widgets.ParticleEffectActor

/**
 * GuardoSphereHUD
 *
 * @author Georg Eckert 2017
 */

class GuardoSphereHUD(skin: Skin, private val team: Team, private val guardoSphere: GuardoSphere) :
        AHUD(skin),
        GuardoSphereTeamWidget.Callbacks
{
    private var detailWidget: GuardianDetailWidget? = null
    private var guardianButtonGroup: ButtonGroup<Button>? = null
    private var guardoSphereChoiceWidget: GuardoSphereChoiceWidget? = null
    private var toggleGuardianStatView: TextButton? = null
    private var guardianStatusWidget: GuardoSphereStatWidget? = null

    init {
        layout(skin)

        val teamWidget = GuardoSphereTeamWidget(skin, team, guardianButtonGroup)
        teamWidget.setPosition(8f, 8f, Align.bottomLeft)
        teamWidget.setCallbacks(this)

        guardoSphereChoiceWidget!!.setCallbacks(
                Callback.SingleInt{ spherePosition ->
            if (guardoSphere.get(spherePosition) != null) {
                detailWidget!!.showDetails(guardoSphere.get(spherePosition))
            }
            }
        )

        stage.addActor(teamWidget)
    }

    private fun layout(skin: Skin)
    {
        val particles = ParticleEffectActor("guardosphere")
        particles.start()
        particles.setPosition(0f, 0f, Align.bottomLeft)
        stage.addActor(particles)

        detailWidget = GuardianDetailWidget(skin)
        detailWidget!!.setPosition((Constant.WIDTH - 8).toFloat(), (Constant.HEIGHT - 8).toFloat(), Align.topRight)

        guardianButtonGroup = ButtonGroup()
        guardianButtonGroup!!.setMaxCheckCount(1)
        guardianButtonGroup!!.setMinCheckCount(1)

        guardoSphereChoiceWidget = GuardoSphereChoiceWidget(skin, guardoSphere, guardianButtonGroup!!)
        guardoSphereChoiceWidget!!.setPosition(8f, (Constant.HEIGHT - 8).toFloat(), Align.topLeft)

        stage.addActor(detailWidget)
        stage.addActor(guardoSphereChoiceWidget)

        toggleGuardianStatView = TextButton("?", skin, "button-gs-default")
        toggleGuardianStatView!!.setSize(40f, 40f)
        toggleGuardianStatView!!.setPosition((Constant.WIDTH - 8).toFloat(), 8f, Align.bottomRight)

        stage.addActor(toggleGuardianStatView)

        guardianStatusWidget = GuardoSphereStatWidget(skin)
        guardianStatusWidget!!.setPosition((Constant.WIDTH - 8).toFloat(), (Constant.RES_Y - 8).toFloat(), Align.topRight)
        stage.addActor(guardianStatusWidget)
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

    override fun onButtonPressed(teamPosition: Int)
    {
        detailWidget!!.showDetails(team.get(teamPosition))
    }
}
