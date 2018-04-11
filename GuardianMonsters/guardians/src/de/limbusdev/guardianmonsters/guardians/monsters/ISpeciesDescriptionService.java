package de.limbusdev.guardianmonsters.guardians.monsters;

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;

/**
 * IGuardianDescriptionService
 *
 * @author Georg Eckert 2017
 */

public interface ISpeciesDescriptionService extends GuardiansServiceLocator.Service
{
    SpeciesDescription getSpeciesDescription(int speciesID);
    String getCommonNameById(int speciesID, int form);
}
