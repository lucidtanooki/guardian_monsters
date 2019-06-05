package de.limbusdev.guardianmonsters.guardians.abilities

import de.limbusdev.guardianmonsters.guardians.Element
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator

/**
 * IAbilitiesService
 *
 * @author Georg Eckert 2019
 */

interface IAbilityService : GuardiansServiceLocator.Service
{
    fun getAbility(e: Element, index: Int): Ability
    fun getAbility(aID: Ability.aID): Ability
}
