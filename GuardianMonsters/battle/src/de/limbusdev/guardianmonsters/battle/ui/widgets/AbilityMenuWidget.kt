package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton

import de.limbusdev.guardianmonsters.assets.paths.AssetPath
import de.limbusdev.guardianmonsters.guardians.Element
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.services.Services
import ktx.style.get

/**
 * AbilityMenuWidget is a menu widget with 7 buttons to choose the ability for the next attack.
 */
class AbilityMenuWidget
(
        private val skin : Skin = Services.UI().battleSkin,
        callbacks: (Int) -> Unit
)
    : SevenButtonsWidget(callbacks, order)
{
    // ............................................................................ Companion Object
    companion object
    {
        private const val TAG : String = "AbilityMenuWidget"
        private val order : IntArray = intArrayOf(5, 3, 1, 0, 4, 2, 6)
    }

    // .............................................................................. Initialization
    fun initialize(guardian: AGuardian, disableAbilitiesWithInsufficientMP: Boolean)
    {
        // Set all buttons inactive & reset appearance
        buttons.forEach { resetButton(it.key) }

        // for every active ability, activate a button
        for (i in 0..6)
        {
            val abilityID = guardian.abilityGraph.activeAbilities[i]
            if(abilityID != null)
            {
                val ability = GuardiansServiceLocator.abilities[abilityID]
                setButtonStyle(i, ability.element)
                setButtonText(i, Services.I18N().getLocalizedAbilityName(ability.name))

                enableButton(i)

                // Disable Ability, when monster does not have enough MP for it
                if (disableAbilitiesWithInsufficientMP)
                {
                    if (ability.MPcost > guardian.stats.mp)
                    {
                        disableButton(i)
                    }
                }
            }
        }
    }


    // ..................................................................................... Methods
    fun toAttackInfoStyle() { buttons.forEach { it.value.style = skin["b-attack-info"] } }

    private fun resetButton(index: Int)
    {
        disableButton(index)
        setButtonStyle(index, Element.NONE)
        setButtonText(index, "")
    }
}
