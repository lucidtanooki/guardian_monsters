package de.limbusdev.guardianmonsters.guardians.monsters;

/**
 * IGuardianDescriptionService
 *
 * @author Georg Eckert 2017
 */

public interface ISpeciesDescriptionService
{
    SpeciesDescription getSpeciesDescription(int speciesID);
    String getCommonNameById(int speciesID);
    public int getNumberOfAncestors(int speciesID);
}
