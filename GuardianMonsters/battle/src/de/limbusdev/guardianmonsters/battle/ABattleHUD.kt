package de.limbusdev.guardianmonsters.battle

import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions.run as runThis
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.battle.ui.widgets.BattleWidget
import de.limbusdev.guardianmonsters.scene2d.Scene2DLayout
import de.limbusdev.guardianmonsters.scene2d.makeImage
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.AHUD
import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.utils.extensions.set
import ktx.actors.plusAssign
import ktx.actors.then
import ktx.style.get


/**
 * Created by Georg Eckert 2016
 */
abstract class ABattleHUD() : AHUD(Services.UI().battleSkin)
{
    protected var widgets = ArrayMap<String, BattleWidget>()

    // ...................................................................................... Images
    // Battle UI Black transparent Background
    private val battleUIbg   = makeImage(skin["bg"], Scene2DLayout(Constant.WIDTHf, 63f))

    // Black Curtain for fade-in and -out
    private val blackCurtain = makeImage(skin["black"], Scene2DLayout(Constant.WIDTHf, Constant.HEIGHTf))

    // ............................................................................................. CONSTRUCTOR
    init
    {
        // Add to stage
        stage += battleUIbg
        stage += blackCurtain

        stage.isDebugAll = BattleDebugger.SCENE2D_DEBUG
    }


    override fun reset()
    {
        blackCurtain.clearActions()
    }

    override fun show()
    {
        blackCurtain.addAction(fadeOut(1f) then visible(false))
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
        blackCurtain.addAction(visible(true) then fadeIn(1f) then runThis{super.goToPreviousScreen()})
    }

}
