package de.limbusdev.guardianmonsters.battle

import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions.run as runThis
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.battle.ui.widgets.BattleWidget
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.AHUD
import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.utils.extensions.f
import de.limbusdev.utils.extensions.set
import ktx.actors.plus
import ktx.actors.plusAssign
import ktx.actors.then


/**
 * Created by Georg Eckert 2016
 */
abstract class ABattleHUD() : AHUD(Services.getUI().battleSkin)
{
    protected var widgets = ArrayMap<String, BattleWidget>()

    // Images
    private val battleUIbg   = Image(skin.getDrawable("bg"))
    private val blackCurtain = Image(skin.getDrawable("black"))

    init
    {
        // Battle UI Black transparent Background
        battleUIbg.apply {

            width = Constant.WIDTH.f()
            height = 61f
            x = 0f
            y = 0f
        }

        // Black Curtain for fade-in and -out
        blackCurtain.apply {

            width = Constant.WIDTH.f()
            height = Constant.HEIGHT.f()
            x = 0f
            y = 0f
        }

        // Add to stage
        stage += battleUIbg
        stage += blackCurtain
    }


    override fun reset()
    {
        blackCurtain.clearActions()
    }

    override fun show()
    {
        blackCurtain += (fadeOut(1f) then visible(false))
    }

    /**
     * Registers a widget to the HUD, but does not add it to the stage
     * @param key
     * @param bw
     */
    fun registerBattleWidget(key: String, bw: BattleWidget)
    {
        widgets[key] = bw
        if(!bw.hasParent())
        {
            stage += bw
        }
    }

    fun getBattleWidget(key: String): BattleWidget = widgets.get(key)

    fun <T : BattleWidget> getBattleWidget(key: String, type: Class<T>): T
    {
        return getBattleWidget(key) as T
    }

    override fun goToPreviousScreen()
    {
        blackCurtain += (visible(true) then fadeIn(1f) then runThis{super.goToPreviousScreen()})
    }

}
