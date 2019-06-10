package de.limbusdev.guardianmonsters.battle

import de.limbusdev.guardianmonsters.assets.paths.AssetPath
import de.limbusdev.guardianmonsters.guardians.items.Inventory
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.AScreen

/**
 * BattleScreen is the main entry point for any kind of battle. It's most important component is the
 * [BattleHUD]. The BattleScreen mainly sets the background image, starts and stops the background
 * music and is used for screen management.
 *
 * Before switching to it can be done, initialize(...) must be called to setup the [BattleHUD] with
 * a player's [Team] and an AI opponent's [Team].
 *
 * @author Georg Eckert 2016
 */
class BattleScreen(inventory: Inventory) : AScreen(BattleHUD(inventory))
{
    // .................................................................................. Properties
    companion object { const val TAG = "BattleScreen" }

    // Keep track of BattleScreen's state
    private var initialized = false

    /**
     * Getter for this screen's HUD. It's casted to [BattleHUD], which is safe, since the super
     * constructor gets called with a [BattleHUD] instance as parameter.
     */
    private val battleHUD: BattleHUD get() = super.hud as BattleHUD


    init
    {
        // set a background image for the battle arena
        setBackground(0)
    }

    // ..................................................................................... Methods
    /**
     * BattleScreen must get initialized before being shown
     * @param team          [Team] for the human player
     * @param opponentTeam  [Team] for the AI opponent
     */
    fun initialize(team: Team, opponentTeam: Team)
    {
        this.initialized = true
        battleHUD.initialize(team, opponentTeam)
    }


    // ............................................................................. libGDX's Screen
    override fun show()
    {
        super.show()

        // Throw exception, if BattleScreen has not been initialized properly
        check(initialized) { "$TAG: initialize(heroTeam, opponentTeam) must be called before drawing." }

        // Start Battle Background Music
        Services.getAudio().playLoopMusic(AssetPath.Audio.Music.BG_BATTLE_default)
    }

    override fun hide()
    {
        super.hide()
        initialized = false

        // Stop Battle Background Music
        Services.getAudio().stopMusic(AssetPath.Audio.Music.BG_BATTLE_default)
    }
}
