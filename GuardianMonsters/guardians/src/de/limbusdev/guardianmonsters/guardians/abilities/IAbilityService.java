package de.limbusdev.guardianmonsters.guardians.abilities;

import de.limbusdev.guardianmonsters.guardians.Element;
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;

/**
 * IAbilitiesService
 *
 * @author Georg Eckert 2017
 */

public interface IAbilityService extends GuardiansServiceLocator.Service
{
    Ability getAbility(Element e, int index);
    Ability getAbility(Ability.aID aID);
}
