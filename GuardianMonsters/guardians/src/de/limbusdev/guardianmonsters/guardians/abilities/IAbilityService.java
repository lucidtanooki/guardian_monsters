package de.limbusdev.guardianmonsters.guardians.abilities;

import de.limbusdev.guardianmonsters.guardians.Element;

/**
 * IAbilitiesService
 *
 * @author Georg Eckert 2017
 */

public interface IAbilityService
{
    Ability getAbility(Element e, int index);
}
