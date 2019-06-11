package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton

import de.limbusdev.guardianmonsters.assets.paths.AssetPath
import de.limbusdev.guardianmonsters.guardians.Element
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.abilities.Ability
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.widgets.Callback


/**
 *
 * @param skin battle action UI skin
 */
class AttackMenuWidget
(
        skin: Skin,
        callbacks: (Int) -> Unit
)
    : SevenButtonsWidget(skin, callbacks, order)
{
    // ............................................................................ Companion Object
    companion object
    {
        private const val TAG : String = "AttackMenuWidget"
        private val order : IntArray = intArrayOf(5, 3, 1, 0, 4, 2, 6)
    }


    // .............................................................................. Initialization
    fun initialize(guardian: AGuardian, disableAbilitiesWithInsufficientMP: Boolean)
    {
        // Set all buttons inactive & reset appearance
        for (i in buttons.keys()) { resetButton(i) }

        // for every attack, activate a button
        for (i in 0..6)
        {
            if (guardian.abilityGraph.activeAbilities.containsKey(i))
            {
                val abilityID = guardian.abilityGraph.getActiveAbility(i)
                val attack = GuardiansServiceLocator.abilities.getAbility(abilityID)
                setButtonStyle(i, skin, AssetPath.Skin.attackButtonStyle(attack.element))
                setButtonText(i, Services.getL18N().Abilities().get(attack.name))

                enableButton(i)

                // Disable Ability, when monster does not have enough MP for it
                if (disableAbilitiesWithInsufficientMP)
                {
                    if (attack.MPcost > guardian.individualStatistics.mp)
                    {
                        disableButton(i)
                    }
                }
            }
        }
    }


    // ..................................................................................... Methods
    fun toAttackInfoStyle()
    {
        val style = skin.get<TextButton.TextButtonStyle>("b-attack-info", TextButton.TextButtonStyle::class.java)

        for (i in buttons.keys()) { getButton(i).style = style }
    }

    private fun resetButton(index: Int)
    {
        disableButton(index)
        setButtonStyle(index, Element.NONE)
        setButtonText(index, "")
    }
}
