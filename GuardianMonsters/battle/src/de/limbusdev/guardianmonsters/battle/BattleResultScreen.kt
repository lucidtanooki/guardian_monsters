package de.limbusdev.guardianmonsters.battle

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20

import de.limbusdev.guardianmonsters.guardians.battle.BattleResult
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.services.Services


/**
 * BattleResultScreen
 *
 * @author Georg Eckert 2017
 */

class BattleResultScreen(team: Team, result: BattleResult) : Screen
{
    private val resultHUD: BattleResultHUD
            = BattleResultHUD(Services.UI().inventorySkin, team, result)


    override fun render(delta: Float)
    {
        // Clear screen
        Gdx.gl.glClearColor(.3f, .3f, .3f, 1f)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        resultHUD.stage.viewport.apply()
        resultHUD.update(delta)
        resultHUD.draw()
    }

    // ............................................................................................. Screen
    override fun show() {}

    override fun resize(width: Int, height: Int) = resultHUD.stage.viewport.update(width, height)

    override fun pause() {}

    override fun resume() {}

    override fun hide() {}

    override fun dispose() {}
}
