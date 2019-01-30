package de.limbusdev.guardianmonsters.battle

import com.badlogic.gdx.graphics.g2d.TextureRegion

import de.limbusdev.guardianmonsters.assets.paths.AssetPath
import de.limbusdev.guardianmonsters.guardians.items.Inventory
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.AScreen

/**
 * @author Georg Eckert 2016
 */
class BattleScreen(inventory: Inventory) : AScreen(BattleHUD(inventory))
{
    // ........................................................................ Properties
    private lateinit var background: TextureRegion
    private var initialized = false

    private val battleHUD: BattleHUD
        get() = super.hud as BattleHUD


    init
    {
        setBackground(0)
    }

    // ........................................................................ Methods
    /**
     * BattleScreen must get initialized before being shown
     * @param team
     * @param opponentTeam
     */
    fun init(team: Team, opponentTeam: Team)
    {
        this.initialized = true
        battleHUD.init(team, opponentTeam)
    }

    override fun show()
    {
        super.show()
        if(!initialized)
        {
            throw ExceptionInInitializerError("BattleScreen must get initialized before drawn.")
        }
        Services.getAudio().playLoopMusic(AssetPath.Audio.Music.BG_BATTLE[0])
    }

    override fun render(delta: Float)
    {
        super.render(delta)
    }

    override fun hide()
    {
        super.hide()
        initialized = false
        Services.getAudio().stopMusic(AssetPath.Audio.Music.BG_BATTLE[0])
    }
}
